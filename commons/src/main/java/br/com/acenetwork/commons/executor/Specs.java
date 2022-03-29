package br.com.acenetwork.commons.executor;

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

public class Specs implements TabExecutor
{
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
			sender.sendMessage(Message.getMessage(Language.ENGLISH.toString(), "cmd.cannot-perform-command"));
			return true;
		}

		Player p = (Player) sender;
		CommonPlayer cp = CraftCommonPlayer.get(p);
		
		if(!cp.hasPermission("cmd.specs"))
		{
			cp.sendMessage("cmd.permission");
			return true;
		}

		if(args.length == 0)
		{
			if(cp.canSpecs())
			{
				cp.setSpecs(false);
				cp.sendMessage("cmd.specs.disabled");
			}
			else
			{
				cp.setSpecs(true);
				cp.sendMessage("cmd.specs.enabled");
			}
		}
		else
		{
			cp.sendMessage("cmd.wrong-syntax-try", "/" + aliases);
		}

		return true;
	}
}