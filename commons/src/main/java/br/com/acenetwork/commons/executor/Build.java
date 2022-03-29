package br.com.acenetwork.commons.executor;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import br.com.acenetwork.commons.constants.Language;
import br.com.acenetwork.commons.manager.Message;
import br.com.acenetwork.commons.player.CommonAdmin;
import br.com.acenetwork.commons.player.CommonPlayer;
import br.com.acenetwork.commons.player.craft.CraftCommonPlayer;

public class Build implements TabExecutor
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
		
		if(!cp.hasPermission("cmd.build"))
		{
			cp.sendMessage("cmd.permission");
			return true;
		}

		if(!(cp instanceof CommonAdmin))
		{
			cp.sendMessage("cmd.need-admin");
			return true;
		}

		CommonAdmin admin = (CommonAdmin) cp;

		if(args.length == 0)
		{
			if(admin.canBuild())
			{
				admin.setBuild(false);
				admin.sendMessage("cmd.build.disabled");
			}
			else
			{
				admin.setBuild(true);
				admin.sendMessage("cmd.build.enabled");
			}
		}
		else
		{
			cp.sendMessage("cmd.wrong-syntax-try", "/" + aliases);
		}

		return true;
	}		
}