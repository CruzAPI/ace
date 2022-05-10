package br.com.acenetwork.survival.executor;

import java.io.File;
import java.nio.file.Files;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import br.com.acenetwork.commons.manager.CommonsConfig;
import br.com.acenetwork.commons.manager.CommonsConfig.Type;
import br.com.acenetwork.commons.manager.Database;
import br.com.acenetwork.commons.manager.Message;
import br.com.acenetwork.commons.player.CommonPlayer;
import br.com.acenetwork.commons.player.craft.CraftCommonPlayer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

public class Reset implements TabExecutor
{
	@Override
	public List<String> onTabComplete(CommandSender arg0, Command arg1, String arg2, String[] arg3)
	{
		return new ArrayList<>();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String aliases, String[] args)
	{
		boolean hasPermission = true;
		ResourceBundle bundle = ResourceBundle.getBundle("message");
		
		if(sender instanceof Player)
		{
			Player p = (Player) sender;
			CommonPlayer cp = CraftCommonPlayer.get(p);
			hasPermission = cp.hasPermission("cmd.reset");
			bundle = ResourceBundle.getBundle("message", cp.getLocale());
		}
		
		if(!hasPermission)
		{
			TextComponent text = new TextComponent(bundle.getString("commons.cmds.permission"));
			text.setColor(ChatColor.RED);
			sender.spigot().sendMessage(text);
			return true;
		}
		
		if(args.length == 0)
		{
			TextComponent[] extra = new TextComponent[1];
			
			extra[0] = new TextComponent("/reset confirm");
			
			TextComponent text = Message.getTextComponent(bundle.getString("raid.cmd.reset.confirm"), extra);
			text.setColor(ChatColor.RED);
			sender.spigot().sendMessage(text);
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
				sender.spigot().sendMessage(text);
			}
		}
		else
		{
			TextComponent[] extra = new TextComponent[1];
			
			extra[0] = new TextComponent("/" + aliases);
			
			TextComponent text = Message.getTextComponent(bundle.getString("commons.cmds.wrong-syntax-try"), extra);
			text.setColor(ChatColor.RED);
			sender.spigot().sendMessage(text);
		}
		
		return false;
	}
	
	public static boolean reset() throws SQLException
	{
//		File resetFile = Config.getFile(Type.RESET, true);
		
		try(Database database = new Database())
		{
			String table = "Coins";
//					Commons.TEST ? "Test" : "Coins";
			
			PreparedStatement ps1 = database.connection.prepareStatement("truncate table `" + table + "`;");
			
			ps1.execute();
			
			Map<UUID, Double> map = new HashMap<>();
			
			File[] balanceFolders = new File[]
			{
				CommonsConfig.getFile(Type.BALANCE_RAID_FOLDER, true),
			};
			
			for(File balanceFolder : balanceFolders)
			{
				File[] files = balanceFolder.listFiles();
				
				files = files == null ? new File[0] : files;
				
				for(File file : files)
				{
					YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
					
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
				
				File playerFile = CommonsConfig.getFile(Type.PLAYER, false, uuid);
				YamlConfiguration playerConfig = YamlConfiguration.loadConfiguration(playerFile);
				String name = playerConfig.getString("name");
				
				sql += "(default, '" + uuid + "', '" + name + "', " + balance + "), ";
			}
			
			sql = sql.substring(0, sql.length() - 2) + ";";

			if(!map.isEmpty())
			{
				database.connection.prepareStatement(sql).execute();
			}
			
			Instant now = Instant.now();
			ZonedDateTime znow = now.atZone(ZoneId.systemDefault());
			
			File balanceFolder = CommonsConfig.getFile(Type.BALANCE_FOLDER, false);
			
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
			
			List<CommandSender> senderList = new ArrayList<CommandSender>(Bukkit.getOnlinePlayers());
			senderList.add(Bukkit.getConsoleSender());
			
			for(CommandSender sender : senderList)
			{
				ResourceBundle bundle = ResourceBundle.getBundle("message", Locale.getDefault());
				
				if(sender instanceof Player)
				{
					Player p = (Player) sender;
					CommonPlayer cp = CraftCommonPlayer.get(p);
					
					bundle = ResourceBundle.getBundle("message", cp.getLocale());
				}
				
				TextComponent text = new TextComponent(bundle.getString("raid.cmd.reset.points-reset"));
				text.setColor(ChatColor.RED);
				sender.spigot().sendMessage(text);
			}
		
			return true;
		}
		catch(SQLException e)
		{
			e.printStackTrace();
			return false;
		}
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