package br.com.acenetwork.commons.executor;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import br.com.acenetwork.commons.constants.Language;
import br.com.acenetwork.commons.manager.Message;
import br.com.acenetwork.commons.player.CommonPlayer;
import br.com.acenetwork.commons.player.craft.CraftCommonPlayer;

public class Broadcast implements TabExecutor
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
		CommonPlayer cp = null;

		if(sender instanceof Player)
		{
			Player p = (Player) sender;
			cp = CraftCommonPlayer.get(p);

			if(!cp.hasPermission("cmd.broadcast"))
			{
				cp.sendMessage("cmd.permission");
				return true;
			}

			locale = p.getLocale();
		}
		
		if(args.length > 0)
		{
		 	String msg = "";

		 	for(int i = 0; i < args.length; i++)			
		 	{
		 		msg += args[i] + " ";
		 	}

		 	msg = msg.substring(0, msg.length() - 1);
		 	
		 	Bukkit.broadcastMessage("§b§lBroadcast§7 » §f" + msg.replace('&', '§'));
		}
		else
		{
		 	sender.sendMessage(Message.getMessage(locale, "cmd.wrong-syntax-try", "/" + aliases + " <msg...>"));
		}
		
		return false;
	}
}