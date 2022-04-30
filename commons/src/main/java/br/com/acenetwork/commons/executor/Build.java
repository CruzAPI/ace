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

public class Build implements TabExecutor
{
	@Override
	public List<String> onTabComplete(CommandSender arg0, Command arg1, String arg2, String[] arg3)
	{
		return new ArrayList<>();
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
		
		if(!cp.hasPermission("cmd.build"))
		{
			TextComponent text = new TextComponent(bundle.getString("commons.dont-have-permission"));
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

		CommonAdmin admin = (CommonAdmin) cp;

		if(args.length == 0)
		{
			if(admin.canBuild())
			{
				admin.setBuild(false);
				TextComponent text = new TextComponent(bundle.getString("commons.cmd.build.disabled"));
				text.setColor(ChatColor.GREEN);
				sender.spigot().sendMessage(text);
			}
			else
			{
				admin.setBuild(true);
				TextComponent text = new TextComponent(bundle.getString("commons.cmd.build.enabled"));
				text.setColor(ChatColor.GREEN);
				sender.spigot().sendMessage(text);
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