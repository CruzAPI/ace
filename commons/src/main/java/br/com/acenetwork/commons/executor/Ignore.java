package br.com.acenetwork.commons.executor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import br.com.acenetwork.commons.CommonsUtil;
import br.com.acenetwork.commons.constants.Language;
import br.com.acenetwork.commons.manager.CommonsConfig;
import br.com.acenetwork.commons.manager.Message;
import br.com.acenetwork.commons.manager.CommonsConfig.Type;
import br.com.acenetwork.commons.player.CommonPlayer;
import br.com.acenetwork.commons.player.craft.CraftCommonPlayer;

public class Ignore implements TabExecutor
{
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) 
	{
		List<String> list = new ArrayList<>();

		if(!(sender instanceof Player))
		{
			return list;
		}

		Player p = (Player) sender;

		if(args.length == 1)
		{
			for(Player all : Bukkit.getOnlinePlayers())
			{
				if(all != p && all.getName().toLowerCase().startsWith(args[0].toLowerCase()) && p.canSee(all))
				{
					list.add(all.getName());
				}
			}
		}

		return list;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String aliases, String[] args)
	{
		if(!(sender instanceof Player))
		{
			sender.sendMessage(Message.getMessage(Language.ENGLISH.toString(), "cmd.cannot-perform-command"));
			return true;
		}

		Player p = (Player) sender;
		CommonPlayer cp = CraftCommonPlayer.get(p);
		
		if(args.length == 1)
		{
			String uuid = CommonsUtil.getUUIDByName(args[0]);

			if(p.getName().equalsIgnoreCase(args[0]))
			{
				cp.sendMessage("cmd.ignore.cannot-ignore-yourself");
				return true;
			}

			if(uuid == null)
			{
				cp.sendMessage("cmd.user-not-found", args[0]);
				return true;
			}

			File playerFile = CommonsConfig.getFile(Type.PLAYER, false, cp.getUniqueID());
			YamlConfiguration playerConfig = YamlConfiguration.loadConfiguration(playerFile);

			List<String> ignoredPlayers = playerConfig.getStringList("ignored-players");
			
			String messageKey;

			if(ignoredPlayers.contains(uuid))
			{
				ignoredPlayers.remove(uuid);
				messageKey = "cmd.ignore.removed-from-list";
			}
			else
			{
				ignoredPlayers.add(uuid);
				messageKey = "cmd.ignore.added-to-list";
			}

			playerConfig.set("ignored-players", ignoredPlayers);
			
			try
			{
				playerConfig.save(playerFile);
				cp.sendMessage(messageKey, CommonsUtil.getNameByUUID(uuid));
			}
			catch(IOException ex)
			{
				ex.printStackTrace();
				cp.sendMessage("commons.unexpected-error");
			}
		}
		else
		{
			cp.sendMessage("cmd.wrong-syntax-try", "/" + aliases + " <player>");
		}

		return true;
	}
}