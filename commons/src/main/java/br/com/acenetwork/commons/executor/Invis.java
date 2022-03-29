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

public class Invis implements TabExecutor
{
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) 
	{
		return new ArrayList<>();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String aliases, String[] args)
	{
		if(!(sender instanceof Player))
		{
			sender.sendMessage(Message.getMessage(Language.ENGLISH.toString(), "cmd.cannot-perform-command"));
			return true;
		}

		Player p = (Player) sender;
		CommonPlayer cp = CraftCommonPlayer.get(p);
		
		if(!cp.hasPermission("cmd.invis"))
		{
			cp.sendMessage("cmd.permission");
			return true;
		}

		if(!(cp instanceof CommonAdmin))
		{
			cp.sendMessage("cmd.need-admin");
			return true;
		}
		
		if(args.length == 0)
		{
			if(command.getName().equalsIgnoreCase("vis"))
			{
				if(cp.setInvis(false))
				{
					cp.sendMessage("cmd.invis.visible-to-all");
				}
				else
				{
					cp.sendMessage("cmd.invis.already-visible");
				}
			}
			else
			{
				if(cp.setInvis(true))
				{
					cp.sendMessage("cmd.invis.invisible-to-all");
				}
				else
				{
					cp.sendMessage("cmd.invis.already-invisible");
				}
			}
		}
		else
		{
			cp.sendMessage("cmd.wrong-syntax-try", "/" + aliases);
		}

		return true;
	}
}