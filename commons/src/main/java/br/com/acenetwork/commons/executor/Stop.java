package br.com.acenetwork.commons.executor;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import br.com.acenetwork.commons.Commons;
import br.com.acenetwork.commons.CommonsUtil;
import br.com.acenetwork.commons.constants.Language;
import br.com.acenetwork.commons.manager.Message;
import br.com.acenetwork.commons.player.CommonPlayer;
import br.com.acenetwork.commons.player.craft.CraftCommonPlayer;

public class Stop implements TabExecutor
{
	@Override
	public List<String> onTabComplete(CommandSender arg0, Command arg1, String arg2, String[] arg3)
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

			if(!cp.hasPermission("cmd.stop"))
			{
				cp.sendMessage("cmd.permission");
				return true;
			}

			locale = p.getLocale();
		}
		
		if(args.length == 0)
		{
		 	stop();
		}
		else
		{
		 	sender.sendMessage(Message.getMessage(locale, "cmd.wrong-syntax-try", "/" + aliases));
		}
		
		return false;
	}
	
	public static void stop()
	{
		Commons.setRestarting(true);
		Bukkit.getOnlinePlayers().stream().forEach(x -> CommonsUtil.bungeeSendPlayer(x.getName(), "lobby"));
	 	
	 	Bukkit.getScheduler().scheduleSyncDelayedTask(Commons.getPlugin(), () ->
	 	{
	 		Bukkit.shutdown();
	 	}, 60L);
	}
}