package br.com.acenetwork.commons.executor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import br.com.acenetwork.commons.manager.CommonsConfig;
import br.com.acenetwork.commons.manager.CommonsConfig.Type;
import br.com.acenetwork.commons.player.CommonPlayer;
import br.com.acenetwork.commons.player.craft.CraftCommonPlayer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

public class Mutebroadcast implements TabExecutor
{
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String aliases, String[] args)
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
		
		if(args.length == 1)
		{
			String key = args[0];
			
			if(!BroadcastCMD.BROADCASTS.contains(key))
			{
				p.sendMessage("Unknown command. Type \"/help\" for help.");
				return true;
			}
			
			File file = CommonsConfig.getFile(Type.PLAYER, true, p.getUniqueId());
			YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
			
			List<String> mutedBroadcasts = config.contains("muted-broadcasts") ? config.getStringList("muted-broadcasts") : new ArrayList<>();
			
			if(mutedBroadcasts.contains(key))
			{
				return true;
			}
			
			mutedBroadcasts.add(key);
			
			config.set("muted-broadcasts", mutedBroadcasts);
			
			try
			{
				config.save(file);
				
				TextComponent text = new TextComponent(bundle.getString("commons.cmd.mutebroadcast.broadcast-muted"));
				text.setColor(ChatColor.GREEN);
				p.spigot().sendMessage(text);
			}
			catch(IOException e)
			{
				e.printStackTrace();
				
				TextComponent text = new TextComponent(bundle.getString("commons.unexpected-error"));
				text.setColor(ChatColor.RED);
				p.spigot().sendMessage(text);
			}
		}
		else
		{
			p.sendMessage("Unknown command. Type \"/help\" for help.");
		}
		
		return false;
	}
}