package br.com.acenetwork.survival.executor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import br.com.acenetwork.commons.manager.Message;
import br.com.acenetwork.commons.player.CommonPlayer;
import br.com.acenetwork.commons.player.craft.CraftCommonPlayer;
import br.com.acenetwork.survival.Main;
import br.com.acenetwork.survival.Util;
import br.com.acenetwork.survival.manager.ChannelCommand;
import br.com.acenetwork.survival.manager.Config;
import br.com.acenetwork.survival.manager.Config.Type;
import br.com.acenetwork.survival.player.SurvivalPlayer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

public class Delhome implements TabExecutor
{
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String aliases, String[] args)
	{
		List<String> list = new ArrayList<>();
		
		if(!(sender instanceof Player))
		{
			return list;
		}
		
		if(args.length != 1)
		{
			return list;
		}
		
		Player p = (Player) sender;
		
		File file = Config.getFile(Type.PLAYER_INFO, true, p.getUniqueId());
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		
		for(String key : Home.getHomeMap(config).keySet())
		{
			if(key.toLowerCase().startsWith(args[0].toLowerCase()))
			{
				list.add(key.toLowerCase());
			}
		}
		
		Collections.sort(list, new Comparator<String>()
		{
			@Override
			public int compare(String o1, String o2)
			{
				return o1.compareTo(o2);
			}
		});
		
		return list;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String aliases, String[] args)
	{
		final ResourceBundle bundle;
		
		if(!(sender instanceof Player))
		{
			bundle = ResourceBundle.getBundle("message");
			
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
			File file = Config.getFile(Type.PLAYER_INFO, true, p.getUniqueId());
			YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
			
			String home = args[0].toLowerCase();
			
			if(!config.contains("home." + home))
			{
				TextComponent[] extra = new TextComponent[1];
				
				extra[0] = new TextComponent(home);
				
				TextComponent text = Message.getTextComponent(bundle.getString("raid.cmds.home-not-found"), extra);
				text.setColor(ChatColor.RED);
				sender.spigot().sendMessage(text);
				
				return true;
			}
			
			config.set("home." + home, null);
			
			try
			{
				config.save(file);
				
				TextComponent[] extra = new TextComponent[1];
				
				extra[0] = new TextComponent(args[0].toString());
				extra[0].setColor(ChatColor.YELLOW);
				
				TextComponent text = Message.getTextComponent(bundle.getString("raid.cmd.delhome.home-deleted"), extra);
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
			TextComponent[] extra = new TextComponent[1];
			
			extra[0] = new TextComponent("/" + aliases + " <" + bundle.getString("commons.words.home") + ">");
			
			TextComponent text = Message.getTextComponent(bundle.getString("commons.cmds.wrong-syntax-try"), extra);
			text.setColor(ChatColor.RED);
			sender.spigot().sendMessage(text);
		}
		
		return false;
	}
}