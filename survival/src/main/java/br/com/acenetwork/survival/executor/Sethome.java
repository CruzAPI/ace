package br.com.acenetwork.survival.executor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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

public class Sethome implements TabExecutor
{
	private static final int MAX_HOME = 3;
	private static final int BLOCKS_AWAY_FROM_SPAWN = 512;
	
	@Override
	public List<String> onTabComplete(CommandSender arg0, Command arg1, String arg2, String[] arg3)
	{
		return new ArrayList<>();
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
			
			Map<String, Location> map = Home.getHomeMap(config);
			
			String home = args[0].toLowerCase();
			
			if(p.getWorld().getName().equals("world") && Math.abs(p.getLocation().getBlockX()) < BLOCKS_AWAY_FROM_SPAWN
					 && Math.abs(p.getLocation().getBlockZ()) < BLOCKS_AWAY_FROM_SPAWN)
			{
				TextComponent[] extra = new TextComponent[1];
				
				extra[0] = new TextComponent(BLOCKS_AWAY_FROM_SPAWN + "");
				
				TextComponent text = Message.getTextComponent(bundle.getString("raid.cmd.sethome.need-away-from-spawn"), extra);
				text.setColor(ChatColor.RED);
				sender.spigot().sendMessage(text);
				
				return true;
			}
			
			if(map.containsKey(home))
			{
				TextComponent[] extra = new TextComponent[1];
				
				extra[0] = new TextComponent(home);
				
				TextComponent text = Message.getTextComponent(bundle.getString("raid.cmd.sethome.home-already-set"), extra);
				text.setColor(ChatColor.RED);
				sender.spigot().sendMessage(text);
				
				return true;
			}
			
			if(!home.matches("[a-z0-9]{0,16}"))
			{
				TextComponent text = new TextComponent(bundle.getString("commons.cmds.invalid-input"));
				text.setColor(ChatColor.RED);
				sender.spigot().sendMessage(text);
				
				return true;
			}
			
			if(map.size() >= MAX_HOME)
			{
				TextComponent text = new TextComponent(bundle.getString("raid.cmd.sethome.limit-reached"));
				text.setColor(ChatColor.RED);
				sender.spigot().sendMessage(text);
				
				return true;
			}
			
			config.set("home." + home, p.getLocation());
			
			try
			{
				config.save(file);
				
				TextComponent[] extra = new TextComponent[1];
				
				extra[0] = new TextComponent(args[0].toString());
				extra[0].setColor(ChatColor.YELLOW);
				
				TextComponent text = Message.getTextComponent(bundle.getString("raid.cmd.sethome.home-set"), extra);
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