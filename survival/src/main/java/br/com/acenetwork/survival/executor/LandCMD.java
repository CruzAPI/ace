package br.com.acenetwork.survival.executor;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import br.com.acenetwork.commons.Commons;
import br.com.acenetwork.commons.CommonsUtil;
import br.com.acenetwork.commons.event.SocketEvent;
import br.com.acenetwork.commons.manager.CommonsConfig;
import br.com.acenetwork.commons.manager.Message;
import br.com.acenetwork.commons.manager.CommonsConfig.Type;
import br.com.acenetwork.commons.player.CommonPlayer;
import br.com.acenetwork.commons.player.craft.CraftCommonPlayer;
import br.com.acenetwork.survival.Main;
import br.com.acenetwork.survival.manager.Land;
import br.com.acenetwork.survival.player.SurvivalPlayer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class LandCMD implements TabExecutor, Listener
{
	public LandCMD()
	{
		Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String aliases, String[] args)
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
			sender.sendMessage(ChatColor.RED + bundle.getString("commons.cmds.cant-perform-command"));
			return true;
		}
		
		Player p = (Player) sender;
		CommonPlayer cp = CraftCommonPlayer.get(p);
		
		bundle = ResourceBundle.getBundle("message", cp.getLocale());
		
		if(args.length == 0)
		{
			try
			{
				Runtime.getRuntime().exec(String.format("node %s/reset/uuidland %s %s %s", System.getProperty("user.home"), 
						Commons.getSocketPort(), cp.requestDatabase(), p.getUniqueId()));
			}
			catch(IOException e)
			{
				e.printStackTrace();
				sender.sendMessage(ChatColor.RED + bundle.getString("commons.unexpected-error"));
			}
		}
		else if(args.length == 1)
		{
			try
			{
				int id = Integer.valueOf(args[0]) - 1;
				
				Land land = Land.getById(id);
				
				if(land == null)
				{
					p.sendMessage(ChatColor.RED + bundle.getString("raid.cmd.land.land-not-found"));
					return true;
				}
				
				if(!land.isOwner(p) && !cp.hasPermission("land.bypass"))
				{
					p.sendMessage(ChatColor.RED + bundle.getString("commons.cmds.permission"));
					return true;
				}
				
				if(cp instanceof SurvivalPlayer && !((SurvivalPlayer) cp).hasSpawnProtection())
				{
					TextComponent[] array = new TextComponent[1];
					
					array[0] = new TextComponent("/spawn");
					
					TextComponent text = Message.getTextComponent(bundle.getString("raid.cmd.randomtp.need-spawn-protection"), array);
					text.setColor(ChatColor.RED);
					p.spigot().sendMessage(text);
					return true;
				}
				
				final World w = land.getWorld();
				final int x = land.getX();
				final int z = land.getZ();
				final int y = 1 + w.getHighestBlockYAt(x, z);
				
				p.teleport(new Location(w, x + 0.5D, y, z + 0.5D, -135.0F, 0.0F));
			}
			catch(NumberFormatException e)
			{
				p.sendMessage(ChatColor.RED + bundle.getString("commons.cmds.invalid-number-format"));
			}
		}
		
		return true;
	}
	
	@EventHandler
	public void oi(SocketEvent e)
	{
		String[] args = e.getArgs();
		String cmd = args[0];
		
		if(cmd.equals("uuidland"))
		{
			int taskId = Integer.valueOf(args[1]);
			Player p = Bukkit.getPlayer(UUID.fromString(args[2]));
			
			if(p != null)
			{
				ResourceBundle bundle = ResourceBundle.getBundle("message", CommonsUtil.getLocaleFromMinecraft(p.getLocale()));
				
				TextComponent[] array = new TextComponent[1];
				
				array[0] = new TextComponent();
				
				for(int i = 3; i < args.length; i++)
				{
					int id = Integer.valueOf(args[i]);
					
					Land land = Land.getById(id);
					
					if(land != null)
					{
						land.setOwner(p);
						
						TextComponent extra = new TextComponent(land.getBeautyId());
						extra.setColor(ChatColor.YELLOW);
						extra.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/land " + land.getBeautyId()));
						extra.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(bundle.getString("raid.cmd.land.click-to-teleport")).create()));
						array[0].addExtra(extra);
						
						if(i + 1 < args.length)
						{
							array[0].addExtra(", ");
						}
					}
				}
				
				if(Bukkit.getScheduler().isQueued(taskId))
				{
					Bukkit.getScheduler().cancelTask(taskId);
					
					if(array[0].toPlainText().isEmpty())
					{
						File linksFile = CommonsConfig.getFile(Type.LINKS, true);
						YamlConfiguration linksConfig = YamlConfiguration.loadConfiguration(linksFile);
						
						TextComponent[] extra = new TextComponent[1];
						
						String url = linksConfig.getString("lands");
						
						extra[0] = new TextComponent(url);
						extra[0].setColor(ChatColor.GRAY);;
						extra[0].setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url));
						
						TextComponent text = Message.getTextComponent(bundle.getString("raid.cmd.land.you-do-not-have-lands"), extra);
						text.setColor(ChatColor.RED);
						p.spigot().sendMessage(text);
						return;
					}
					
					TextComponent text = Message.getTextComponent(bundle.getString("raid.cmd.land.your-lands"), array);
					text.setColor(ChatColor.GREEN);
					p.spigot().sendMessage(text);
				}
			}
		}
	}
}