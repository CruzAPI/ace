package br.com.acenetwork.commons.executor;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import br.com.acenetwork.commons.manager.Message;
import br.com.acenetwork.commons.player.CommonAdmin;
import br.com.acenetwork.commons.player.CommonPlayer;
import br.com.acenetwork.commons.player.craft.CraftCommonPlayer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

public class Invis implements TabExecutor
{
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) 
	{
		return new ArrayList<>();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String aliases, String[] args)
	{
		ResourceBundle bundle = ResourceBundle.getBundle("message");
		
		if(!(sender instanceof Player))
		{
			TextComponent text = new TextComponent(bundle.getString("commons.cmds.cant-perform-command"));
			text.setColor(ChatColor.RED);
			sender.spigot().sendMessage(text);
			return true;
		}

		Player p = (Player) sender;
		CommonPlayer cp = CraftCommonPlayer.get(p);
		
		bundle = ResourceBundle.getBundle("message", cp.getLocale());
		
		if(!cp.hasPermission("cmd.invis"))
		{
			TextComponent text = new TextComponent(bundle.getString("commons.cmds.permission"));
			text.setColor(ChatColor.RED);
			sender.spigot().sendMessage(text);
			return true;
		}

		if(!(cp instanceof CommonAdmin))
		{
			TextComponent text = new TextComponent(bundle.getString("commons.cmds.need-admin"));
			text.setColor(ChatColor.RED);
			sender.spigot().sendMessage(text);
			return true;
		}
		
		if(args.length == 0)
		{
			if(command.getName().equalsIgnoreCase("vis"))
			{
				if(cp.setInvis(false))
				{
					TextComponent text = new TextComponent(bundle.getString("commons.cmd.invis.visible-to-all"));
					text.setColor(ChatColor.GREEN);
					sender.spigot().sendMessage(text);
				}
				else
				{
					TextComponent text = new TextComponent(bundle.getString("commons.cmd.invis.already-visible"));
					text.setColor(ChatColor.RED);
					sender.spigot().sendMessage(text);
				}
			}
			else
			{
				if(cp.setInvis(true))
				{
					TextComponent text = new TextComponent(bundle.getString("commons.cmd.invis.invisible-to-all"));
					text.setColor(ChatColor.GREEN);
					sender.spigot().sendMessage(text);
				}
				else
				{
					TextComponent text = new TextComponent(bundle.getString("commons.cmd.invis.already-invisible"));
					text.setColor(ChatColor.RED);
					sender.spigot().sendMessage(text);
				}
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

		return true;
	}
}