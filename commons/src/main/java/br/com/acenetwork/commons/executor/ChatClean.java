package br.com.acenetwork.commons.executor;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import br.com.acenetwork.commons.constants.Language;
import br.com.acenetwork.commons.manager.Message;
import br.com.acenetwork.commons.player.CommonPlayer;
import br.com.acenetwork.commons.player.craft.CraftCommonPlayer;

public class ChatClean implements TabExecutor
{
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String aliases, String[] args)
	{
		return null;
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

			if(!cp.hasPermission("cmd.chatclean"))
			{
				cp.sendMessage("cmd.permission");
				return true;
			}

			locale = p.getLocale();
		}
		
		if(args.length == 0)
		{
			for(CommonPlayer cpall : CraftCommonPlayer.SET)
			{
				Player all = cpall.getPlayer();
				all.sendMessage(StringUtils.repeat(" \n", 200));
			}

			if(cp == null)
			{
				sender.sendMessage(Message.getMessage(locale, "cmd.chatclean.chat-cleaned"));
			}
		}
		else
		{
			sender.sendMessage(Message.getMessage(locale, "cmd.wrong-syntax-try", "/" + aliases));
		}
		
		return false;
	}
}