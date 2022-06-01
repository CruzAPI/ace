package br.com.acenetwork.commons.executor;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerPreLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

import br.com.acenetwork.commons.Commons;
import br.com.acenetwork.commons.constants.Tag;
import br.com.acenetwork.commons.event.PlayerModeEvent;
import br.com.acenetwork.commons.inventory.Banishment;
import br.com.acenetwork.commons.manager.CommonsConfig;
import br.com.acenetwork.commons.manager.CommonsConfig.Type;
import br.com.acenetwork.commons.manager.Message;
import br.com.acenetwork.commons.player.CommonAdmin;
import br.com.acenetwork.commons.player.CommonPlayer;
import br.com.acenetwork.commons.player.craft.CraftCommonAdmin;
import br.com.acenetwork.commons.player.craft.CraftCommonPlayer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

public class Setip implements TabExecutor, Listener
{
	public Setip()
	{
		Bukkit.getPluginManager().registerEvents(this, Commons.getPlugin());
	}
	
	@Override
	public List<String> onTabComplete(CommandSender arg0, Command arg1, String arg2, String[] arg3)
	{
		return new ArrayList<>();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String aliases, String[] args)
	{
		CommonPlayer cp = null;
		boolean hasPermission = true;
		ResourceBundle bundle = ResourceBundle.getBundle("message");
		
		if(sender instanceof Player)
		{
			Player p = (Player) sender;
			cp = CraftCommonPlayer.get(p);
			hasPermission = cp.hasPermission("cmd.setip");
			bundle = ResourceBundle.getBundle("message", cp.getLocale());
		}
		
		if(!hasPermission)
		{
			TextComponent text = new TextComponent(bundle.getString("commons.cmds.permission"));
			text.setColor(ChatColor.RED);
			sender.spigot().sendMessage(text);
			return true;
		}
		
		if(args.length == 1 || args.length == 2)
		{
			OfflinePlayer op = Bukkit.getOfflinePlayerIfCached(args[0]);
			
			if(op == null)
			{
				sender.sendMessage(ChatColor.RED + bundle.getString("commons.cmds.user-not-found"));
				return true;
			}
			
			String ip;
			
			if(args.length == 2)
			{
				ip = args[1];
			}
			else
			{
				File file = CommonsConfig.getFile(Type.PLAYER, false, op.getUniqueId());
				YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
				
				ip = config.getString("ip").replace("/", "");
			}
			
			try
			{
				InetAddress address = InetAddress.getByName(ip);
				
				File file = CommonsConfig.getFile(Type.WHITELISTED_IP, true);
				YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
				
				config.set(op.getUniqueId().toString(), address.toString());
				
				try
				{
					config.save(file);
					
					TextComponent[] extra = new TextComponent[2];
					
					extra[0] = new TextComponent(op.getName());
					extra[0].setColor(ChatColor.YELLOW);
					
					extra[1] = new TextComponent(address.toString());
					extra[1].setColor(ChatColor.YELLOW);
					
					TextComponent text = Message.getTextComponent(bundle.getString("commons.cmd.setip.ip-set"), extra); 
					text.setColor(ChatColor.GREEN);
					sender.spigot().sendMessage(text);
				}
				catch(IOException e)
				{
					e.printStackTrace();
					sender.sendMessage(ChatColor.RED + bundle.getString("commons.unexpected-error"));
				}
			}
			catch(UnknownHostException e)
			{
				sender.sendMessage(ChatColor.RED + bundle.getString("commons.cmd.setip.invalid-ip-address"));
			}
		}
		else
		{
			TextComponent[] extra = new TextComponent[1];
			
			extra[0] = new TextComponent("/" + aliases);
			extra[0].addExtra(" <" + bundle.getString("commons.words.user") + ">");
			extra[0].addExtra(" [" + bundle.getString("commons.words.ip-address") + "]");
			
			TextComponent text = Message.getTextComponent(bundle.getString("commons.cmds.wrong-syntax-try"), extra); 
			text.setColor(ChatColor.RED);
			sender.spigot().sendMessage(text);
		}
		
		return false;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void asdas(PlayerLoginEvent e)
	{
		File file = CommonsConfig.getFile(Type.WHITELISTED_IP, true);
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		
		Player p = e.getPlayer();
		
		if(config.contains(p.getUniqueId().toString()) 
				&& !e.getAddress().toString().equals(config.getString(p.getUniqueId().toString())))
		{
			e.setResult(Result.KICK_OTHER);
			e.setKickMessage("troxa");
		}
	}
}