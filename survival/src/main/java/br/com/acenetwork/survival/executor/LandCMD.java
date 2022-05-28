package br.com.acenetwork.survival.executor;

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
import br.com.acenetwork.commons.event.SocketEvent;
import br.com.acenetwork.commons.player.CommonPlayer;
import br.com.acenetwork.commons.player.craft.CraftCommonPlayer;
import br.com.acenetwork.survival.Main;
import net.md_5.bungee.api.ChatColor;

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
			
		}
		else if(args.length == 1 && args[0].equalsIgnoreCase("sync"))
		{
			try
			{
				Runtime.getRuntime().exec(String.format("node %s/reset/synclands %s %s %s", System.getProperty("user.home"), 
						Commons.getSocketPort(), cp.requestDatabase(), p.getUniqueId()));
			}
			catch(IOException e)
			{
				e.printStackTrace();
				sender.sendMessage(ChatColor.RED + bundle.getString("commons.unexpected-error"));
			}
		}
		
		return true;
	}
	
	@EventHandler
	public void oi(SocketEvent e)
	{
		String[] args = e.getArgs();
		String cmd = args[0];
		
		if(cmd.equals("synclands"))
		{
			int taskId = Integer.valueOf(args[1]);
			Player p = Bukkit.getPlayer(UUID.fromString(args[2]));
			String wallet = args[3].toLowerCase();
			String result = args[4];
		}
	}
}