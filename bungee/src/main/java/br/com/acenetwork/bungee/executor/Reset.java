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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.UUID;

import br.com.acenetwork.bungee.Main;
import br.com.acenetwork.bungee.Util;
import br.com.acenetwork.bungee.manager.Config;
import br.com.acenetwork.bungee.manager.Config.Type;
import br.com.acenetwork.bungee.manager.Database;
import br.com.acenetwork.bungee.manager.Message;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
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
		boolean hasPermission = true;
		ResourceBundle bundle = ResourceBundle.getBundle("message");
		
		if(sender instanceof ProxiedPlayer)
		{
			ProxiedPlayer p = (ProxiedPlayer) sender;
			hasPermission = Util.hasPermission(p.getUniqueId().toString(), "cmd.reset");
			bundle = ResourceBundle.getBundle("message", p.getLocale());
		}
		
		if(!hasPermission)
		{
			TextComponent text = new TextComponent(bundle.getString("commons.cmds.permission"));
			text.setColor(ChatColor.RED);
			sender.sendMessage(text);
			return;
		}
		
		if(args.length == 0)
		{
			TextComponent[] extra = new TextComponent[1];
			
			extra[0] = new TextComponent("/reset confirm");
			
			TextComponent text = Message.getTextComponent(bundle.getString("commons.cmd.reset.confirm"), extra);
			text.setColor(ChatColor.RED);
			sender.sendMessage(text);
		}
		else if(args.length == 1 && args[0].equalsIgnoreCase("confirm"))
		{
			try
			{
				reset();
			}
			catch(Exception e)
			{
				e.printStackTrace();
				TextComponent text = new TextComponent(bundle.getString("commons.unexpected-error"));
				text.setColor(ChatColor.RED);
				sender.sendMessage(text);
			}
		}
		else
		{
			TextComponent[] extra = new TextComponent[1];
			
			extra[0] = new TextComponent("/" + getName());
			
			TextComponent text = Message.getTextComponent(bundle.getString("commons.cmds.wrong-syntax-try"), extra);
			text.setColor(ChatColor.RED);
			sender.sendMessage(text);
		}
		
		return;
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
			String table = Main.TEST ? "Test" : "Coins";
			
			PreparedStatement ps1 = database.connection.prepareStatement("truncate table `" + table + "`;");
			
			ps1.execute();
			
			Map<UUID, Double> map = new HashMap<>();
			
			File[] balanceFolders = new File[]
			{
				Config.getFile(Type.BALANCE_RAID_FOLDER, true),
				Config.getFile(Type.BALANCE_MINIGAME_FOLDER, true)
			};
			
			for(File balanceFolder : balanceFolders)
			{
				File[] files = balanceFolder.listFiles();
				
				files = files == null ? new File[0] : files;
				
				for(File file : files)
				{
					Configuration config = provider.load(file);
					
					UUID uuid = UUID.fromString(file.getName().substring(0, 36));
					double balance = config.getDouble("balance");
					
					double oldBalance = map.containsKey(uuid) ? map.get(uuid) : 0.0D;
					map.put(uuid, oldBalance + balance);
				}
			}
			
			String sql = "insert into `" + table + "` values ";
			
			for(Entry<UUID, Double> entry : map.entrySet())
			{
				UUID uuid = entry.getKey();
				double balance = entry.getValue();
								
				sql += "(default, '" + uuid + "', 'aaa', " + balance + "), ";
			}
			
			sql = sql.substring(0, sql.length() - 2) + ";";

			if(!map.isEmpty())
			{
				database.connection.prepareStatement(sql).execute();
			}
			
			Instant now = Instant.now();
			ZonedDateTime znow = now.atZone(ZoneId.systemDefault());
			
			File balanceFolder = Config.getFile(Type.BALANCE_FOLDER, false);
			
//			for(int i = 1; ; i++)
//			{
//				String fileName = znow.getYear() + "-" + znow.getMonthValue() + "-" + znow.getDayOfMonth() + "-" + i;
//				
//				File file = Config.getFile(Type.BALANCE_HISTORY_ZIP, false, fileName);
//				
//				if(file.exists())
//				{
//					continue;
//				}
//				
//				file.getParentFile().mkdirs();
//				Files.copy(balanceFolder.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
//				break;
//			}
//			
			
			deleteDir(balanceFolder);
			
			Configuration resetConfig = provider.load(resetFile);
			resetConfig.set("last-reset", getTodayResetDayOfMonth());
			provider.save(resetConfig, resetFile);
			
			List<CommandSender> senderList = new ArrayList<CommandSender>(ProxyServer.getInstance().getPlayers());
			senderList.add(ProxyServer.getInstance().getConsole());
			
			for(CommandSender sender : senderList)
			{
				ResourceBundle bundle = ResourceBundle.getBundle("message");
				
				if(sender instanceof ProxiedPlayer)
				{
					bundle = ResourceBundle.getBundle("message", ((ProxiedPlayer) sender).getLocale());
				}
				
				TextComponent text = new TextComponent(bundle.getString("commons.cmd.reset.points-reset"));
				text.setColor(ChatColor.RED);
				sender.sendMessage(text);
			}
			
			if(Main.TEST)
			{
//				Runtime.getRuntime().exec(System.getProperty("user.home") + "/reset/testpay.bash");
			}
			else
			{
				Runtime.getRuntime().exec("screen -dmS payment /home/ace/reset/payment.sh");
			}
			
			return true;
		}
		catch(IOException e)
		{
			e.printStackTrace();
			return false;
		}
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
