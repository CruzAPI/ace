package br.com.acenetwork.commons.executor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;

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
import br.com.acenetwork.commons.player.CommonPlayer;
import br.com.acenetwork.commons.player.craft.CraftCommonPlayer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class Wallet implements TabExecutor, Listener
{
	private static final String WALLET_PATTERN = "0x[a-fA-F0-9]{40}";
	
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
		CommonPlayer cp = CraftCommonPlayer.get(p);
		ResourceBundle bundle = ResourceBundle.getBundle("message", cp.getLocale());
		
		try
		{
			if(args.length == 0)
			{
//				if(cp.getWalletAddress() != null)
//				{
//					showWallet(bundle, p, cp.getWalletAddress());
//				}
//				else
//				{
					Runtime.getRuntime().exec(String.format("node %s/reset/getwallet %s %s %s", System.getProperty("user.home"), 
							Commons.getSocketPort(), cp.requestDatabase(), p.getUniqueId()));
//				}
			}
			else if(args.length == 1)
			{
				String address = args[0];
				
				if(!address.matches(WALLET_PATTERN))
				{
					TextComponent text = new TextComponent(bundle.getString("commons.cmd.wallet.invalid-wallet-address"));
					text.setColor(ChatColor.RED);
					sender.spigot().sendMessage(text);
					return true;
				}
				
				Runtime.getRuntime().exec(String.format("node %s/reset/setwallet %s %s %s %s %s", System.getProperty("user.home"),
						Commons.getSocketPort(), cp.requestDatabase(), p.getUniqueId(), address, p.getName().toLowerCase()));
			}
			else
			{
				TextComponent[] extra = new TextComponent[1];
				
				extra[0] = new TextComponent("/" + aliases + " <" + bundle.getString("commons.words.address") + ">");
				
				TextComponent text = Message.getTextComponent(bundle.getString("commons.cmds.wrong-syntax-try"), extra);
				text.setColor(ChatColor.RED);
				sender.spigot().sendMessage(text);
			}
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
			
			TextComponent text = new TextComponent(bundle.getString("commons.unexpected-error"));
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
			int taskId = Integer.valueOf(args[1]);
			Player p = Bukkit.getPlayer(UUID.fromString(args[2]));
			String wallet = args[3].toLowerCase();
			String result = args[4];
			
			CommonPlayer cp = CraftCommonPlayer.get(p);
			
			if(cp != null)
			{
				String oldWallet = cp.getWalletAddress();
				
				if(result.equals("ALLOW"))
				{
					cp.setWalletAddress(wallet);
				}
				
				if(Bukkit.getScheduler().isQueued(taskId))
				{
					Bukkit.getScheduler().cancelTask(taskId);
					
					ResourceBundle bundle = ResourceBundle.getBundle("message", CommonsUtil.getLocaleFromMinecraft(p.getLocale()));
					
					if(result.equalsIgnoreCase("NOT_FOUND"))
					{
						TextComponent[] extra = new TextComponent[1];
						
						String url = "https://www.acetokennetwork.com/";
						
						extra[0] = new TextComponent(url);
						extra[0].setColor(ChatColor.GRAY);
						extra[0].setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url));
						
						TextComponent text = Message.getTextComponent(bundle.getString("commons.cmd.wallet.wallet-not-linked-on-the-website"), extra);
						text.setColor(ChatColor.RED);
						p.spigot().sendMessage(text);
					}
					else if(result.equalsIgnoreCase("ALREADY_LINKED"))
					{
						if(wallet.equalsIgnoreCase(oldWallet))
						{
							p.sendMessage(ChatColor.RED + bundle.getString("commons.cmd.wallet.wallet-already-linked"));
						}
						else
						{
							p.sendMessage(ChatColor.RED + bundle.getString("commons.cmd.wallet.wallet-already-linked-to-another"));
						}
					}
					else if(result.equalsIgnoreCase("ALLOW"))
					{
						p.sendMessage(ChatColor.GREEN + bundle.getString("commons.cmd.wallet.wallet-linked"));
					}
				}
			}
		}
		else if(cmd.equals("getwallet"))
		{
			int taskId = Integer.valueOf(args[1]);
			Player p = Bukkit.getPlayer(UUID.fromString(args[2]));
			String wallet = args[3].toLowerCase();
			
			CommonPlayer cp = CraftCommonPlayer.get(p);
			
			if(cp != null)
			{
				ResourceBundle bundle = ResourceBundle.getBundle("message", cp.getLocale());
				cp.setWalletAddress(wallet);
				
				if(Bukkit.getScheduler().isQueued(taskId))
				{
					Bukkit.getScheduler().cancelTask(taskId);
					showWallet(bundle, p, wallet);
				}
			}
		}
	}
	
	private static void showWallet(ResourceBundle bundle, Player p, String wallet)
	{
		if(wallet.matches(WALLET_PATTERN))
		{
			TextComponent[] extra = new TextComponent[1];
			
			extra[0] = new TextComponent(wallet);
			extra[0].setColor(ChatColor.YELLOW);
			
			TextComponent text = Message.getTextComponent(bundle.getString("commons.cmd.wallet.your-wallet"), extra);
			text.setColor(ChatColor.GREEN);
			p.spigot().sendMessage(text);
		}
		else
		{
			p.sendMessage(ChatColor.RED + bundle.getString("commons.cmd.wallet.do-not-have-any-wallet"));
			
			TextComponent[] extra = new TextComponent[1];
			
			extra[0] = new TextComponent("/wallet <" + bundle.getString("commons.words.address").toLowerCase() + ">");
			extra[0].setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/wallet "));
			
			TextComponent text = Message.getTextComponent(bundle.getString("commons.cmd.wallet.to-link-your-wallet-type"), extra);
			text.setColor(ChatColor.RED);
			p.spigot().sendMessage(text);
		}
	}
}