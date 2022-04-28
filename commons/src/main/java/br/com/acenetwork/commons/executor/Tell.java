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

import br.com.acenetwork.commons.constants.Language;
import br.com.acenetwork.commons.manager.CommonsConfig;
import br.com.acenetwork.commons.manager.Message;
import br.com.acenetwork.commons.manager.CommonsConfig.Type;
import br.com.acenetwork.commons.player.CommonPlayer;
import br.com.acenetwork.commons.player.craft.CraftCommonPlayer;

public class Tell implements TabExecutor
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

		if(args.length > 1)
		{			
			String msg = "";
	
			for(int i = 1; i < args.length; i++)			
			{
				msg += args[i] + " ";
			}

			tell(cp, Bukkit.getPlayer(args[0]), msg);
		}
		else
		{
			cp.sendMessage("cmd.wrong-syntax-try", "/" + aliases + " <player> <msg...>");
		}

		return true;
	}

	public static void tell(CommonPlayer cp, Player t, String msg)
	{
		Player p = cp.getPlayer();

		if(t == null || !p.canSee(t))
		{
			cp.sendMessage("cmd.player-not-found");
			return;
		}

		if(t == p)
		{
			cp.sendMessage("cmd.tell.cannot-tell-yourself");
			return;
		}

		File targetFile = CommonsConfig.getFile(Type.PLAYER, false, t.getUniqueId());
		YamlConfiguration targetConfig = YamlConfiguration.loadConfiguration(targetFile);
		
		if(targetConfig.getStringList("ignored-players").contains(p.getUniqueId().toString()))
		{
			cp.sendMessage("cmd.tell.cannot-send-message");
			return;
		}

		File playerFile = CommonsConfig.getFile(Type.PLAYER, false, p.getUniqueId());
		YamlConfiguration playerConfig = YamlConfiguration.loadConfiguration(playerFile);

		targetConfig.set("last-reply", p.getUniqueId().toString());
		playerConfig.set("last-reply", p.getUniqueId().toString());

		try
		{
			playerConfig.save(playerFile);
			targetConfig.save(targetFile);
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
		}

		msg = msg.length() > 0 ? msg.substring(0, msg.length() - 1) : null;

		p.sendMessage(String.format("§7[%s§7 » %s§7]§l §f%s", p.getDisplayName(), t.getDisplayName(), msg));
		t.sendMessage(String.format("§7[%s§7 » %s§7]§l §f%s", p.getDisplayName(), t.getDisplayName(), msg));
	}
}