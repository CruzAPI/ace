package br.com.acenetwork.commons.executor;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import br.com.acenetwork.commons.Commons;
import br.com.acenetwork.commons.event.SocketEvent;
import br.com.acenetwork.commons.manager.CommonsConfig;
import br.com.acenetwork.commons.manager.Message;
import br.com.acenetwork.commons.manager.CommonsConfig.Type;
import br.com.acenetwork.commons.player.CommonPlayer;
import br.com.acenetwork.commons.player.craft.CraftCommonPlayer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;

public class BTA implements TabExecutor, Listener
{
	public BTA()
	{
		Bukkit.getPluginManager().registerEvents(this, Commons.getPlugin());
	}
	
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

		if(args.length == 0)
		{		
			try
			{
				Runtime.getRuntime().exec(String.format("node %s/reset/getbtabalance %s %s %s", System.getProperty("user.home"),
						Commons.getSocketPort(), cp.requestDatabase(), p.getUniqueId()));
			}
			catch(IOException e)
			{
				e.printStackTrace();
				p.sendMessage(ChatColor.RED + bundle.getString("commons.unexpected-error"));
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
	
	@EventHandler
	public void onSocketEvent(SocketEvent e)
	{
		String[] args = e.getArgs();
		
		String cmd = args[0];
		
		if(cmd.equalsIgnoreCase("getbtabalance"))
		{
			int taskId = Integer.valueOf(args[1]);
			Player p = Bukkit.getPlayer(UUID.fromString(args[2]));
			CommonPlayer cp = CraftCommonPlayer.get(p);
			double price = Double.valueOf(args[3]);
			
			if(cp != null && Bukkit.getScheduler().isQueued(taskId))
			{
				ResourceBundle bundle = ResourceBundle.getBundle("message", cp.getLocale());
				Bukkit.getScheduler().cancelTask(taskId);
				
				TextComponent[] extra = new TextComponent[1];
				
				DecimalFormat df = new DecimalFormat("#.######", new DecimalFormatSymbols(bundle.getLocale()));
				df.setGroupingSize(3);
				df.setGroupingUsed(true);
				
				p.sendMessage(ChatColor.LIGHT_PURPLE + "Beta ACE " + ChatColor.DARK_PURPLE + "(BTA)");
				p.sendMessage(ChatColor.WHITE + "$" + df.format(price));
				p.sendMessage(" ");
				
				df.applyPattern("0.00");
				
				try
				{
					double balance = Double.valueOf(args[4]);
					
					extra[0] = new TextComponent(df.format(balance) + " $BTA ");
					extra[0].setColor(ChatColor.DARK_PURPLE);
					extra[0].addExtra(new ComponentBuilder("(≈" + df.format(balance * price) + " USD)").color(ChatColor.DARK_GRAY).getCurrentComponent());
					
					TextComponent text = Message.getTextComponent(bundle.getString("commons.cmd.bta.show-balance"), extra);
					text.setColor(ChatColor.LIGHT_PURPLE);
					p.spigot().sendMessage(text);
				}
				catch(NumberFormatException ex)
				{
					p.sendMessage(ChatColor.RED + bundle.getString("commons.cmd.wallet.do-not-have-any-wallet"));
					
					extra = new TextComponent[1];
					
					extra[0] = new TextComponent("/wallet <" + bundle.getString("commons.words.address").toLowerCase() + ">");
					extra[0].setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/wallet "));
					
					TextComponent text = Message.getTextComponent(bundle.getString("commons.cmd.wallet.to-link-your-wallet-type"), extra);
					text.setColor(ChatColor.RED);
					p.spigot().sendMessage(text);
				}
			}
		}
	}
}