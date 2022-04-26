package br.com.acenetwork.commons.executor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import br.com.acenetwork.commons.Commons;
import br.com.acenetwork.commons.constants.Language;
import br.com.acenetwork.commons.manager.Broadcast;
import br.com.acenetwork.commons.manager.Message;
import br.com.acenetwork.commons.player.CommonPlayer;
import br.com.acenetwork.commons.player.craft.CraftCommonPlayer;

public class BroadcastCMD implements TabExecutor
{
	public static final List<Broadcast> BROADCASTS = new ArrayList<>();
	
	public BroadcastCMD()
	{
		Bukkit.getScheduler().scheduleSyncRepeatingTask(Commons.getPlugin(), new Runnable()
		{
			int i = 0;
			
			@Override
			public void run()
			{
				if(BROADCASTS.isEmpty())
				{
					return;
				}
				
				if(i >= BROADCASTS.size())
				{
					i = 0;
					suffle();
				}
				
				String msg = "§7➟ %s §8[§c✕§8]";
				
				Broadcast bc = BROADCASTS.get(i);
				CraftCommonPlayer.SET.forEach(x -> x.getPlayer().sendMessage( 
					String.format(msg, Message.getMessage(x.getPlayer().getLocale(), bc.key, bc.args))));
				
				Bukkit.getConsoleSender().sendMessage(String.format(msg, 
					Message.getMessage(Language.ENGLISH.toString(), bc.key, bc.args)));
				
				i++;
			}
		}, 5L * 60L * 20L, 5L * 60L * 20L);
	}
	
	public static void suffle()
	{
		List<Broadcast> list = new ArrayList<>(BROADCASTS);
		
		BROADCASTS.clear();
		
		Random r = new Random();
		
		while(!list.isEmpty())
		{
			BROADCASTS.add(list.remove(r.nextInt(list.size())));
		}
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String aliases, String[] args)
	{
		return new ArrayList<>();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String aliases, String[] args)
	{
		String locale = Language.ENGLISH.toString();
		CommonPlayer cp = null;

		if(sender instanceof Player)
		{
			Player p = (Player) sender;
			cp = CraftCommonPlayer.get(p);

			if(!cp.hasPermission("cmd.broadcast"))
			{
				cp.sendMessage("cmd.permission");
				return true;
			}

			locale = p.getLocale();
		}
		
		if(args.length > 0)
		{
		 	String msg = "";

		 	for(int i = 0; i < args.length; i++)			
		 	{
		 		msg += args[i] + " ";
		 	}

		 	msg = msg.substring(0, msg.length() - 1);
		 	
		 	Bukkit.broadcastMessage("§b§lBroadcast§7 » §f" + msg.replace('&', '§'));
		}
		else
		{
		 	sender.sendMessage(Message.getMessage(locale, "cmd.wrong-syntax-try", "/" + aliases + " <msg...>"));
		}
		
		return false;
	}
}