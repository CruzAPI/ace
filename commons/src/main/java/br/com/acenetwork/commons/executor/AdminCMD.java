package br.com.acenetwork.commons.executor;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import br.com.acenetwork.commons.event.PlayerModeEvent;
import br.com.acenetwork.commons.manager.Message;
import br.com.acenetwork.commons.player.CommonAdmin;
import br.com.acenetwork.commons.player.CommonPlayer;
import br.com.acenetwork.commons.player.craft.CraftCommonAdmin;
import br.com.acenetwork.commons.player.craft.CraftCommonPlayer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

public class AdminCMD implements TabExecutor
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
			TextComponent text = new TextComponent(bundle.getString("commons.cmds.cant-perform-command"));
			text.setColor(ChatColor.RED);
			sender.spigot().sendMessage(text);
			return true;
		}

		Player p = (Player) sender;
		CommonPlayer cp = CraftCommonPlayer.get(p);
		
		bundle = ResourceBundle.getBundle("message", cp.getLocale());
		
		if(!cp.hasPermission("cmd.admin"))
		{
			TextComponent text = new TextComponent(bundle.getString("commons.cmds.permission"));
			text.setColor(ChatColor.RED);
			sender.spigot().sendMessage(text);
			return true;
		}
		
		if(args.length == 0)
		{
			final TextComponent text1;
			TextComponent[] extra = new TextComponent[2];
			
			if(cp instanceof CommonAdmin)
			{
				text1 = new TextComponent(bundle.getString("commons.cmd.invis.invisible-to-all"));
				
				extra[0] = new TextComponent(bundle.getString("commons.words.administrator"));
				extra[0].setColor(ChatColor.RED);
				
				Bukkit.getPluginManager().callEvent(new PlayerModeEvent(p.getPlayer()));
			}
			else
			{
				text1 = new TextComponent(bundle.getString("commons.cmd.invis.visible-to-all"));
				
				extra[0] = new TextComponent(bundle.getString("commons.words.player"));
				extra[0].setColor(ChatColor.YELLOW);
				
				cp = new CraftCommonAdmin(p);
			}
			
			TextComponent text = Message.getTextComponent(bundle.getString("commons.cmds.player-mode-change"), extra);
			text.setColor(ChatColor.GREEN);
			
			text1.setColor(ChatColor.GREEN);
			
			sender.spigot().sendMessage(text);
			sender.spigot().sendMessage(text1);
		}
		else
		{
			TextComponent[] extra = new TextComponent[1];
			
			extra[0] = new TextComponent("/" + aliases);
			
			TextComponent text = Message.getTextComponent(bundle.getString("commons.cmds.wrong-syntax-try"), extra);
			text.setColor(ChatColor.RED);
			sender.spigot().sendMessage(text);
		}

		return false;
	}
}