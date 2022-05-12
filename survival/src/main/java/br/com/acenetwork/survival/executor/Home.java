package br.com.acenetwork.survival.executor;

import java.io.File;
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
import org.bukkit.configuration.ConfigurationSection;
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
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;

public class Home implements TabExecutor, ChannelCommand
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
		
		for(String key : getHomeMap(config).keySet())
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
		
		if(args.length > 1)
		{
			TextComponent[] extra = new TextComponent[1];
			
			extra[0] = new TextComponent("/" + aliases + " [" + bundle.getString("commons.words.home") + "]");
			
			TextComponent text = Message.getTextComponent(bundle.getString("commons.cmds.wrong-syntax-try"), extra);
			text.setColor(ChatColor.RED);
			sender.spigot().sendMessage(text);
			
			return true;
		}
		
		File file = Config.getFile(Type.PLAYER_INFO, true, p.getUniqueId());
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		
		Map<String, Location> map = getHomeMap(config);
		
		if(args.length == 0)
		{
			String homes = "";
			ChatColor color = ChatColor.GREEN;
			final String comma = color + ", ";
			
			for(String key : map.keySet())
			{
				homes += "§e" + key + comma;
			}
			
			try
			{
				homes = homes.substring(0, homes.length() - comma.length());
				
				TextComponent[] extra = new TextComponent[1];
				
				extra[0] = new TextComponent(homes);
				
				TextComponent text = Message.getTextComponent(bundle.getString("raid.cmd.home.your-homes"), extra);
				text.setColor(color);
				sender.spigot().sendMessage(text);
			}
			catch(IndexOutOfBoundsException ex)
			{
				TextComponent text = new TextComponent(bundle.getString("raid.cmd.home.home-list-empty"));
				text.setColor(ChatColor.RED);
				sender.spigot().sendMessage(text);
			}
		}
		else
		{
			String home = args[0].toLowerCase();
			Location destiny = map.get(home);
			
			if(destiny == null)
			{
				TextComponent[] extra = new TextComponent[1];
				
				extra[0] = new TextComponent(home);
				
				TextComponent text = Message.getTextComponent(bundle.getString("raid.cmds.home-not-found"), extra);
				text.setColor(ChatColor.RED);
				sender.spigot().sendMessage(text);
				
				return true;
			}
			
			if(cp instanceof SurvivalPlayer)
			{
				SurvivalPlayer sp = (SurvivalPlayer) cp;
				
				if(sp.hasSpawnProtection())
				{
					TextComponent text = new TextComponent(bundle.getString("raid.cmds.cant-teleport-with-spawn-protection"));
					text.setColor(ChatColor.RED);
					sender.spigot().sendMessage(text);
					
					return true;
				}
				
				sp.channel(this, destiny, home);
			}
			else
			{
				run(cp, destiny, home);
			}
		}
		
		return false;
	}
	
	public static Map<String, Location> getHomeMap(YamlConfiguration config)
	{
		Map<String, Location> map = new HashMap<>();
		
		ConfigurationSection section = config.getConfigurationSection("home");
		
		if(section != null)
		{
			for(String key : section.getKeys(false))
			{
				map.put(key, config.getLocation("home." + key));
			}
		}
		
		return map;
	}
	
	@Override
	public void run(CommonPlayer cp, Location destiny, Object... args)
	{
		ChannelCommand.super.run(cp, destiny);
		
		ResourceBundle bundle = ResourceBundle.getBundle("message", cp.getLocale());
		Player p = cp.getPlayer();
		
		TextComponent[] extra = new TextComponent[1];
		
		extra[0] = new TextComponent(args[0].toString());
		extra[0].setColor(ChatColor.YELLOW);
		
		TextComponent text = Message.getTextComponent(bundle.getString("raid.cmd.home.teleported-to-home"), extra);
		text.setColor(ChatColor.GREEN);
		p.spigot().sendMessage(text);
	}
}