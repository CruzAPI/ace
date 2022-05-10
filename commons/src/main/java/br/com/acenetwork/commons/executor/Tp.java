package br.com.acenetwork.commons.executor;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import br.com.acenetwork.commons.manager.Message;
import br.com.acenetwork.commons.player.CommonPlayer;
import br.com.acenetwork.commons.player.craft.CraftCommonPlayer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

public class Tp implements TabExecutor
{
	@Override
	public List<String> onTabComplete(CommandSender sender, Command arg1, String arg2, String[] args)
	{
		List<String> list = new ArrayList<>();
		
		if(sender instanceof Player)
		{
			Player p = (Player) sender;
			CommonPlayer cp = CraftCommonPlayer.get(p);
			
			if(!cp.hasPermission("cmd.tp"))
			{
				return list;
			}
		}
		
		if(args.length == 1 || args.length == 2)
		{
			for(Player all : Bukkit.getOnlinePlayers())
			{
				if(all.getName().toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
				{
					list.add(all.getName());
				}
			}
		}
		
		return list;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String aliases, String[] args)
	{
		CommonPlayer cp = null;
		Player p = null;
		boolean hasPermission = true;
		ResourceBundle bundle = ResourceBundle.getBundle("message");
		
		if(sender instanceof Player)
		{
			p = (Player) sender;
			cp = CraftCommonPlayer.get(p);
			hasPermission = cp.hasPermission("cmd.teleport");
			bundle = ResourceBundle.getBundle("message", cp.getLocale());
		}
		
		if(!hasPermission)
		{
			TextComponent text = new TextComponent(bundle.getString("commons.cmds.permission"));
			text.setColor(ChatColor.RED);
			sender.spigot().sendMessage(text);
			return true;
		}
		
		DecimalFormat df = new DecimalFormat("#.###", new DecimalFormatSymbols(bundle.getLocale()));
		df.setGroupingSize(3);
		df.setGroupingUsed(true);
		
		if(p != null && args.length == 1 || args.length == 2)
		{
			Player t1 = args.length == 2 ? Bukkit.getPlayer(args[0]) : p;
			Player t2 = Bukkit.getPlayer(args[args.length - 1]);
			
			if(t1 == null || t2 == null)
			{
				TextComponent text = new TextComponent(bundle.getString("commons.cmds.player-not-found"));
				text.setColor(ChatColor.RED);
				sender.spigot().sendMessage(text);
				return true;
			}
			
			t1.teleport(t2);
			
			TextComponent[] extra = new TextComponent[2];
			
			extra[0] = new TextComponent();
			extra[0].addExtra(t1.getDisplayName());
			extra[0].setColor(ChatColor.YELLOW);
			
			extra[1] = new TextComponent();
			extra[1].addExtra(t2.getDisplayName());
			extra[1].setColor(ChatColor.YELLOW);
			
			TextComponent text = Message.getTextComponent(bundle.getString("commons.cmd.teleport.player-to-player"), extra);
        	text.setColor(ChatColor.GREEN);
        	sender.spigot().sendMessage(text);
		}
		else if(p != null && args.length == 3 || args.length == 4)
		{
			Player t = args.length == 4 ? Bukkit.getPlayer(args[0]) : p;
			
			if(t == null)
			{
				TextComponent text = new TextComponent(bundle.getString("commons.cmds.player-not-found"));
				text.setColor(ChatColor.RED);
				sender.spigot().sendMessage(text);
				return true;
			}
			
			try
			{
				double x = Double.parseDouble(args[args.length - 3]);
				double y = Double.parseDouble(args[args.length - 2]);
				double z = Double.parseDouble(args[args.length - 1]);
				
				t.teleport(new Location(t.getWorld(), x, y, z));
				
				TextComponent[] extra = new TextComponent[4];
				
				extra[0] = new TextComponent();
				extra[0].addExtra(t.getDisplayName());
				extra[0].setColor(ChatColor.YELLOW);
				
				extra[1] = new TextComponent(df.format(x));
				extra[1].setColor(ChatColor.YELLOW);
				
				extra[2] = new TextComponent(df.format(y));
				extra[2].setColor(ChatColor.YELLOW);
				
				extra[3] = new TextComponent(df.format(z));
				extra[3].setColor(ChatColor.YELLOW);
				
				TextComponent text = Message.getTextComponent(bundle.getString("commons.cmd.teleport.player-to-coords"), extra);
            	text.setColor(ChatColor.GREEN);
            	sender.spigot().sendMessage(text);				
			}
			catch(NumberFormatException ex)
			{
				TextComponent text = new TextComponent(bundle.getString("commons.cmds.invalid-number-format"));
            	text.setColor(ChatColor.RED);
            	sender.spigot().sendMessage(text);
			}
		}
		else
		{
			TextComponent[] extra = new TextComponent[1];
			
			extra[0] = new TextComponent("\n/" + aliases);
			
			if(p == null)
			{
				extra[0].addExtra(" <" + bundle.getString("commons.words.player") + ">");
				extra[0].addExtra(" <" + bundle.getString("commons.words.player") + ">");
				extra[0].addExtra("\n/" + aliases);
				extra[0].addExtra(" <" + bundle.getString("commons.words.player") + ">");
				extra[0].addExtra(" <" + bundle.getString("commons.words.player") + " <x> <y> <z>");
			}
			else
			{
				extra[0].addExtra(" [" + bundle.getString("commons.words.player") + "]");
				extra[0].addExtra(" <" + bundle.getString("commons.words.player") + ">");
				extra[0].addExtra("\n/" + aliases);
				extra[0].addExtra(" [" + bundle.getString("commons.words.player") + "]");
				extra[0].addExtra(" <" + bundle.getString("commons.words.player") + " <x> <y> <z>");
			}
			
			TextComponent text = Message.getTextComponent(bundle.getString("commons.cmds.wrong-syntax-try"), extra);
			text.setColor(ChatColor.RED);
			sender.spigot().sendMessage(text);
		}
		
		return false;
	}
}