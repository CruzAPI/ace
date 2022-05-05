package br.com.acenetwork.commons.executor;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import br.com.acenetwork.commons.manager.Message;
import br.com.acenetwork.commons.player.craft.CraftCommonPlayer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

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
		Player p = null;
		ResourceBundle bundle = ResourceBundle.getBundle("message");
		
		if(sender instanceof Player)
		{
			p = (Player) sender;
			bundle = ResourceBundle.getBundle("message", CraftCommonPlayer.get(p).getLocale());
		}

		Player t = null;
		
		if(p != null && args.length == 0)
		{
			t = p;
		}
		else if(args.length == 1)
		{
			t = Bukkit.getPlayer(args[0]);
		}
		else
		{
			TextComponent[] extra = new TextComponent[1];
			
			extra[0] = new TextComponent("/" + aliases);
			
			String argsPlayer = bundle.getString("commons.words.player");
			
			if(p == null)
			{
				extra[0].addExtra(" <" + argsPlayer + ">");
			}
			else
			{
				extra[0].addExtra(" [" + argsPlayer + "]");
			}
			
			TextComponent text = Message.getTextComponent(bundle.getString("commons.cmds.wrong-syntax-try"), extra);
			text.setColor(ChatColor.RED);
			sender.spigot().sendMessage(text);
			return true;
		}

		if(t == null || (p != null && !p.canSee(t)))
		{
			TextComponent text = new TextComponent(bundle.getString("commons.cmds.player-not-found"));
			text.setColor(ChatColor.RED);
			sender.spigot().sendMessage(text);
			return true;
		}

		int ping = t.getPing();
		ChatColor color = getPingColor(ping);

		if(t == p)
		{
			TextComponent[] extra = new TextComponent[1];
			
			extra[0] = new TextComponent(ping + "ms");
			extra[0].setColor(color);
			
			TextComponent text = Message.getTextComponent(bundle.getString("commons.cmd.ping.self"), extra);
			text.setColor(ChatColor.GREEN);
			sender.spigot().sendMessage(text);
		}
		else
		{
			TextComponent[] extra = new TextComponent[2];
			
			extra[0] = new TextComponent(t.getName());
			extra[0].setColor(ChatColor.YELLOW);
			
			extra[1] = new TextComponent(ping + "ms");
			extra[1].setColor(color);
			
			TextComponent text = Message.getTextComponent(bundle.getString("commons.cmd.ping.other"), extra);
			text.setColor(ChatColor.GREEN);
			sender.spigot().sendMessage(text);
		}

		return true;
	}
	
	private ChatColor getPingColor(int ping)
	{
		if(ping < 30)
		{
			return ChatColor.DARK_GREEN;
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