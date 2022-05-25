package br.com.acenetwork.commons.executor;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import br.com.acenetwork.commons.Commons;
import br.com.acenetwork.commons.CommonsUtil;
import br.com.acenetwork.commons.event.SocketEvent;
import br.com.acenetwork.commons.manager.Message;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class Wallet implements TabExecutor, Listener
{
	public Wallet()
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
		if(!(sender instanceof Player))
		{
			return true;
		}
		
		Player p = (Player) sender;
		ResourceBundle bundle = ResourceBundle.getBundle("message", CommonsUtil.getLocaleFromMinecraft(p.getLocale()));
		
		if(args.length == 1)
		{
			String address = args[0];
			
			if(!address.matches("0x[a-fA-F0-9]{40}"))
			{
				TextComponent text = new TextComponent(bundle.getString("commons.cmd.wallet.invalid-wallet-address"));
				text.setColor(ChatColor.RED);
				sender.spigot().sendMessage(text);
				return true;
			}
			
			try
			{
				Runtime.getRuntime().exec(System.getProperty("user.home") + "/reset/sync.sh " + Commons.getSocketPort() + " " 
						+ p.getUniqueId() + " " + address + " " + p.getName());
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			TextComponent[] extra = new TextComponent[1];
			
			extra[0] = new TextComponent("/" + aliases + " <" + bundle.getString("commons.words.address") + ">");
			
			TextComponent text = Message.getTextComponent(bundle.getString("commons.cmds.wrong-syntax-try"), extra);
			text.setColor(ChatColor.RED);
			sender.spigot().sendMessage(text);
		}
		
		
		return false;
	}
	
	@EventHandler
	public void as(SocketEvent e)
	{
		String[] args = e.getArgs();
		
		String cmd = args[0];
		
		if(cmd.equals("setwallet"))
		{
			Player p = Bukkit.getPlayer(args[1]);
			
			if(p != null)
			{
				ResourceBundle bundle = ResourceBundle.getBundle("message", CommonsUtil.getLocaleFromMinecraft(p.getLocale()));
				
				if(args[3].equalsIgnoreCase("undefined"))
				{
					TextComponent text = new TextComponent(bundle.getString("commons.cmd.wallet.wallet-linked"));
					text.setColor(ChatColor.GREEN);
					p.spigot().sendMessage(text);
				}
				else
				{
					TextComponent[] extra = new TextComponent[1];
					
					extra[0] = new TextComponent("https://www.acetokennetwork.com");
					extra[0].setColor(ChatColor.GRAY);
					extra[0].setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.acetokennetwork.com/"));
					
					TextComponent text = Message.getTextComponent(bundle.getString("commons.cmd.wallet.wallet-already-linked-or-not-found"), extra);
					text.setColor(ChatColor.RED);
					p.spigot().sendMessage(text);
				}
			}
		}
	}
}