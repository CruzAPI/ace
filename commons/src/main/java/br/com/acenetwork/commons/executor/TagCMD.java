package br.com.acenetwork.commons.executor;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import br.com.acenetwork.commons.constants.Tag;
import br.com.acenetwork.commons.manager.Message;
import br.com.acenetwork.commons.player.CommonPlayer;
import br.com.acenetwork.commons.player.craft.CraftCommonPlayer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

public class TagCMD implements TabExecutor
{
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String aliases, String[] args)
	{
		List<String> list = new ArrayList<>();
		
		if(!(sender instanceof Player))
		{
			return list;
		}

		Player p = (Player) sender;
		CommonPlayer cp = CraftCommonPlayer.get(p);

		if(args.length == 1)
		{
			for(Tag tag : Tag.values())
			{
				if(tag.name().startsWith(args[0].toUpperCase()) && cp.hasPermission(tag.getPermission()))
				{
					list.add(tag.name());
				}
			}
		}

		return list;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String aliases, String[] args)
	{
		ResourceBundle bundle = ResourceBundle.getBundle("message");
		
		if(!(sender instanceof Player))
		{
			TextComponent text = new TextComponent(bundle.getString("commons.cmd.cant-perform-command"));
			text.setColor(ChatColor.RED);
			sender.spigot().sendMessage(text);
			return true;
		}

		Player p = (Player) sender;
		CommonPlayer cp = CraftCommonPlayer.get(p);
		
		bundle = ResourceBundle.getBundle("message", cp.getLocale());

		if(args.length == 0)
		{
			String tags = "";

			for(Tag tag : Tag.values())
			{
				if(cp.hasPermission(tag.getPermission()))
				{
					tags += tag.getName() + "§a, ";
				}
			}

			if(tags.length() > 2)
			{
				tags = tags.substring(0, tags.length() - 2) + "§a.";
			}
			
			TextComponent[] extra = new TextComponent[1];
			
			extra[0] = new TextComponent();
			extra[0].addExtra(tags);
			
			TextComponent text = Message.getTextComponent(bundle.getString("commons.cmd.tag.your-tags"), extra);
			text.setColor(ChatColor.GREEN);
			sender.spigot().sendMessage(text);
		}
		else if(args.length == 1)
		{
			try
			{
				Tag tag = Tag.valueOf(args[0].toUpperCase());

				if(cp.hasPermission(tag.getPermission()))
				{
					cp.setTag(tag);
					
					TextComponent[] extra = new TextComponent[1];
					
					extra[0] = new TextComponent();
					extra[0].addExtra(tag.getName());
					
					TextComponent text = Message.getTextComponent(bundle.getString("commons.cmd.tag.tag-selected"), extra);
					text.setColor(ChatColor.GREEN);
					sender.spigot().sendMessage(text);
				}
				else
				{
					TextComponent text = new TextComponent(bundle.getString("commons.cmds.permission"));
					text.setColor(ChatColor.RED);
					sender.spigot().sendMessage(text);
				}
			}
			catch(IllegalArgumentException e)
			{
				TextComponent text = new TextComponent(bundle.getString("commons.cmd.tag.tag-not-found"));
				text.setColor(ChatColor.RED);
				sender.spigot().sendMessage(text);
			}
		}
		else
		{
			TextComponent[] extra = new TextComponent[1];
			
			extra[0] = new TextComponent("/" + aliases);
			extra[0].addExtra(" [" + bundle.getString("commons.words.tag") + "]");
			
			TextComponent text = Message.getTextComponent(bundle.getString("commons.cmds.wrong-syntax-try"), extra);
			text.setColor(ChatColor.RED);
			sender.spigot().sendMessage(text);
		}

		return true;
	}
}