package br.com.acenetwork.tntrun.executor;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import br.com.acenetwork.commons.constants.Language;
import br.com.acenetwork.commons.manager.Message;
import br.com.acenetwork.commons.player.CommonPlayer;
import br.com.acenetwork.commons.player.craft.CraftCommonPlayer;
import br.com.acenetwork.tntrun.Main;

public class Timer implements TabExecutor
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

			if(!cp.hasPermission("tntrun.cmd.timer"))
			{
				cp.sendMessage("cmd.permission");
				return true;
			}

			locale = p.getLocale();
		}
		
		if(args.length == 1)
		{
			try
			{
				int time = Integer.valueOf(args[0]);
				
				if(time < 0)
				{
					time = 0;
				}
				else if(time > 3600)
				{
					time = 3600;
				}
				
				Main.setTime(time);
				sender.sendMessage(Message.getMessage(locale, "tntrun.cmd.timer.timer-set-to", time));
			}
			catch(NumberFormatException e)
			{
				sender.sendMessage(Message.getMessage(locale, "tntrun.cmd.timer.invalid-number"));
			}
		}
		else
		{
			sender.sendMessage(Message.getMessage(locale, "cmd.wrong-syntax-try", "/" + aliases + " <seconds>"));
		}
		
		return false;
	}
}