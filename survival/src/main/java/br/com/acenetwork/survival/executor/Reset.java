package br.com.acenetwork.survival.executor;

import java.io.File;
import java.nio.file.Files;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import br.com.acenetwork.commons.CommonsUtil;
import br.com.acenetwork.commons.constants.Language;
import br.com.acenetwork.commons.manager.Message;
import br.com.acenetwork.commons.player.CommonPlayer;
import br.com.acenetwork.commons.player.craft.CraftCommonPlayer;
import br.com.acenetwork.survival.manager.Config;
import br.com.acenetwork.survival.manager.Config.Type;
import br.com.acenetwork.survival.manager.Database;

public class Reset implements TabExecutor
{	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String aliases, String[] args)
	{
		return new ArrayList<>();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String aliases, String[] args)
	{
		String locale = Language.ENGLISH.toString();
		
		if(sender instanceof Player)
		{
			Player p = (Player) sender;
			CommonPlayer cp = CraftCommonPlayer.get(p);
			locale = p.getLocale();
			
			if(!cp.hasPermission("cmd.reset"))
			{
				cp.sendMessage("cmd.permission");
				return true;
			}
		}

		if(args.length == 0)
		{
			sender.sendMessage(Message.getMessage(locale, "cmd.reset.confirm"));
		}
		else if(args.length == 1 && args[0].equalsIgnoreCase("confirm"))
		{
			File playersFolder = Config.getFile(Type.PLAYERS_FOLDER, false);

			try(Database database = new Database())
			{
				PreparedStatement ps1 = database.connection.prepareStatement("truncate table `Coins`;");
				
				ps1.execute();

				String sql = "insert into `Coins` values ";
				
				File[] files = playersFolder.listFiles();
				
				files = files == null ? new File[0] : files;

				for(File file : files)
				{
					YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
					
					String uuid = file.getName().substring(0, 36);
					String name = CommonsUtil.getNameByUUID(uuid);
					double balance = config.getDouble("balance");

					sql += "(default, '" + uuid + "', '" + name + "', " + balance + "), ";
				}
				
				sql = sql.substring(0, sql.length() - 2) + ";";

				if(files.length > 0)
				{
					PreparedStatement ps2 = database.connection.prepareStatement(sql);

					ps2.execute();
					
					deleteDir(playersFolder);
					
					sender.sendMessage(Message.getMessage(locale, "cmd.reset.points-reset"));
				}

				// GlobalScoreboard.MAP.clear();
				
				// for(Player all : Bukkit.getOnlinePlayers())
				// {
				// 	File file = Config.getFile(Type.PLAYER, true, all.getUniqueId());
				// 	YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

				// 	GlobalScoreboard.MAP.put(all, config.getDouble("max-balance"));
				// }
				
				// GlobalScoreboard.update();
			}
			catch(SQLException e)
			{
				e.printStackTrace();
				sender.sendMessage(Message.getMessage(locale, "commons.unexpected-error"));
				return true;
			}
		}
		else
		{
			sender.sendMessage("cmd.wrong-syntax-try", "/" + aliases);
		}
		
		return false;
	}

	private void deleteDir(File file)
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