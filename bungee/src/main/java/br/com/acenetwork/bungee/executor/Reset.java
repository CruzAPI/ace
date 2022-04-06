package br.com.acenetwork.bungee.executor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import br.com.acenetwork.bungee.Main;
import br.com.acenetwork.bungee.Util;
import br.com.acenetwork.bungee.manager.Config;
import br.com.acenetwork.bungee.manager.Message;
import br.com.acenetwork.bungee.manager.Config.Type;
import br.com.acenetwork.bungee.manager.Database;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.scheduler.GroupedThreadFactory.BungeeGroup;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class Reset extends Command
{
	public static final int RESET_HOUR = 21;
	
	public Reset(String name)
	{
		super(name);
	}

	@Override
	public void execute(CommandSender sender, String[] args)
	{
		String locale = "en_us";
		
		if(sender instanceof ProxiedPlayer)
		{
			ProxiedPlayer p = (ProxiedPlayer) sender;
			locale = p.getLocale().toString().toLowerCase();
			
			if(!Util.hasPermission(p.getUniqueId().toString(), "cmd.reset"))
			{
				p.sendMessage(Message.getMessage(locale, "cmd.permission"));
				return;
			}
		}
		
		if(args.length == 0)
		{
			sender.sendMessage(Message.getMessage(locale, "cmd.reset.confirm"));
		}
		else if(args.length == 1 && args[0].equalsIgnoreCase("confirm"))
		{
			try
			{
				reset();
			}
			catch(SQLException e)
			{
				e.printStackTrace();
				sender.sendMessage(Message.getMessage(locale, "commons.unexpected-error"));
			}
		}
		else
		{
			sender.sendMessage(Message.getMessage(locale, "cmd.wrong-syntax-try", "/" + getName()));
		}
	}
	
	public static int getTodayResetDayOfMonth()
	{
		Instant now = Instant.now();
		ZonedDateTime znow = now.atZone(ZoneId.systemDefault());
		
		Instant yesterday = now.minus(1, ChronoUnit.DAYS);
		ZonedDateTime zyesterday = yesterday.atZone(ZoneId.systemDefault());
		
		if(znow.getHour() < RESET_HOUR)
		{
			return zyesterday.getDayOfMonth();
		}
		else
		{
			return znow.getDayOfMonth();
		}
	}
	
	public static long getDelay()
	{
		Instant now = Instant.now();
		ZonedDateTime znow = now.atZone(ZoneId.systemDefault());
		
		int nowHour = znow.getHour();
		
		long delay;
		
		if(nowHour < Reset.RESET_HOUR)
		{
			delay = (Reset.RESET_HOUR - nowHour) * 60L * 60L * 1000L;
		}
		else
		{
			delay = (24 + Reset.RESET_HOUR - nowHour) * 60L * 60L * 1000L;
		}
		
		delay -= znow.getMinute() * 60L * 1000L;
		delay -= znow.getSecond() * 1000L;
		
		return delay;
	}
	
	public static boolean reset() throws SQLException
	{
		ConfigurationProvider provider = ConfigurationProvider.getProvider(YamlConfiguration.class);
		
		File resetFile = Config.getFile(Type.RESET, true);
		
		try(Database database = new Database())
		{
			String table = "Coins";
			
			PreparedStatement ps1 = database.connection.prepareStatement("truncate table `" + table + "`;");
			
			ps1.execute();

			Map<String, Double> map = new HashMap<>();
			
			File[] balanceFolders = new File[]
			{
				Config.getFile(Type.BALANCE_RAID_FOLDER, true),
				Config.getFile(Type.BALANCE_TNTRUN_FOLDER, true)
			};
			
			for(File balanceFolder : balanceFolders)
			{
				File[] files = balanceFolder.listFiles();
				
				files = files == null ? new File[0] : files;
				
				for(File file : files)
				{
					Configuration config = provider.load(file);
					
					String uuid = file.getName().substring(0, 36);
					double balance = config.getDouble("balance");
					
					if(map.containsKey(uuid))
					{
						map.put(uuid, map.get(uuid) + balance);
					}
					else
					{
						map.put(uuid, balance);
					}
				}
			}
			
			String sql = "insert into `" + table + "` values ";
			
			for(Entry<String, Double> entry : map.entrySet())
			{
				String uuid = entry.getKey();
				double balance = entry.getValue();
				
				String name = Util.getNameByUUID(uuid);
				sql += "(default, '" + uuid + "', '" + name + "', " + balance + "), ";
			}
			
			sql = sql.substring(0, sql.length() - 2) + ";";

			if(!map.isEmpty())
			{
				database.connection.prepareStatement(sql).execute();
			}
			
			deleteDir(Config.getFile(Type.BALANCE_FOLDER, false));
			
			Configuration resetConfig = provider.load(resetFile);
			resetConfig.set("last-reset", getTodayResetDayOfMonth());
			provider.save(resetConfig, resetFile);
			
			ProxyServer.getInstance().getPlayers().stream().forEach(x -> 
			x.sendMessage(Message.getMessage(x.getLocale().toString().toLowerCase(), "cmd.reset.points-reset")));
			
			ProxyServer.getInstance().getConsole().sendMessage(Message.getMessage("en_us", "cmd.reset.points-reset"));
			
			return true;
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		
		return false;
	}
	
	public static int getLastResetDayOfMonth()
	{
		ConfigurationProvider provider = ConfigurationProvider.getProvider(YamlConfiguration.class);
		
		File file = Config.getFile(Type.RESET, true);
		
		try
		{
			Configuration config = provider.load(file);
			return config.getInt("last-reset");
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		
		return 0;
	}
	
	private static void deleteDir(File file)
	{
		File[] contents = file.listFiles();
		
		if(contents != null)
		{
			for(File f : contents)
			{
				if(!Files.isSymbolicLink(f.toPath()))
				{
					deleteDir(f);
				}
			}
		}
		
		file.delete();
	}
}
