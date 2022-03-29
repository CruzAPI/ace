package br.com.acenetwork.commons.executor;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import br.com.acenetwork.commons.constants.Language;
import br.com.acenetwork.commons.manager.Message;
import br.com.acenetwork.commons.player.CommonPlayer;
import br.com.acenetwork.commons.player.craft.CraftCommonPlayer;

public class Ping implements TabExecutor
{
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String aliases, String[] args)
	{
		List<String> list = new ArrayList<>();

		Player p = null;

		if(sender instanceof Player)
		{
			p = (Player) sender;
		}

		if(args.length == 1)
		{
			for(Player all : Bukkit.getOnlinePlayers())
			{
				if(all.getName().toLowerCase().startsWith(args[0].toLowerCase()) && 
					(p == null || p.canSee(all)))
				{
					list.add(all.getName());
				}
			}
		}

		return list;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String aliases, String[] args)
	{
		if(!(sender instanceof Player))
		{
			sender.sendMessage(Message.getMessage(Language.ENGLISH.toString(), "cmd.cannot-perform-command"));
			return true;
		}

		Player p = (Player) sender;
		CommonPlayer cp = CraftCommonPlayer.get(p);

		Player t = null;
		
		if(args.length == 0)
		{
			t = p;
		}
		else if(args.length == 1)
		{
			t = Bukkit.getPlayer(args[0]);
		}
		else
		{
			cp.sendMessage("cmd.wrong-syntax-try", "/" + aliases + " [player]");
			return true;
		}

		if(t == null || !p.canSee(t))
		{
			cp.sendMessage("cmd.player-not-found");
			return true;
		}

		int ping = t.getPing();
		ChatColor color = getPingColor(ping);

		if(t == p)
		{
			cp.sendMessage("cmd.ping.self", color.toString() + ping);
		}
		else
		{
			cp.sendMessage("cmd.ping.other", t.getName(), color.toString() + ping);
		}

		return true;
	}
	
	private ChatColor getPingColor(int ping)
	{
		if(ping < 30)
		{
			return ChatColor.GREEN;
		}
		else if(ping < 100)
		{
			return ChatColor.GOLD;
		}
		else
		{
			return ChatColor.RED;
		}
	}
}