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

public class Invsee implements TabExecutor
{
	@Override
	public List<String> onTabComplete(CommandSender sender, Command arg1, String arg2, String[] args)
	{
		List<String> list = new ArrayList<>();
		
		if(!(sender instanceof Player))
		{
			return list;
		}
		
		Player p = (Player) sender;
		CommonPlayer cp = CraftCommonPlayer.get(p);
		
		if(!cp.hasPermission("cmd.invsee"))
		{
			return list;
		}
		
		if(args.length == 1)
		{
			for(Player all : Bukkit.getOnlinePlayers())
			{
				if(all.getName().toLowerCase().startsWith(args[0].toLowerCase()))
				{
					list.add(all.getName());
				}
			}
		}
		
		return null;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String aliases, String[] args)
	{
		if(!(sender instanceof Player))
		{
			sender.sendMessage(Message.getMessage(Language.ENGLISH.toString(), "cmd.cannot-perform-command"));
			return true;
		}

		Player p = (Player) sender;
		CommonPlayer cp = CraftCommonPlayer.get(p);
		
		if(!cp.hasPermission("cmd.invsee"))
		{
			cp.sendMessage("cmd.permission");
			return true;
		}

		if(args.length == 1)
		{
			Player t = Bukkit.getPlayer(args[0]);
			
			if(t == null)
			{
				cp.sendMessage("cmd.player-not-found");
				return true;
			}
			
			p.openInventory(t.getInventory());
		}
		else
		{
			cp.sendMessage("cmd.wrong-syntax-try", "/" + aliases + " <player>");
		}

		return true;
	}
}