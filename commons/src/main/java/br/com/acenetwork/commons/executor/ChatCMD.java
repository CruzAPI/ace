package br.com.acenetwork.commons.executor;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import br.com.acenetwork.commons.constants.Language;
import br.com.acenetwork.commons.manager.Message;
import br.com.acenetwork.commons.player.CommonPlayer;
import br.com.acenetwork.commons.player.craft.CraftCommonPlayer;

public class ChatCMD implements TabExecutor
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

			if(!cp.hasPermission("cmd.chat"))
			{
				cp.sendMessage("cmd.permission");
				return true;
			}

			locale = p.getLocale();
		}
		
		if(args.length > 0)
		{
			
		}
		else
		{
			sender.sendMessage(Message.getMessage(locale, "cmd.wrong-syntax-try", "/" + aliases + " <true:false>"));
		}
		
		return false;
	}
}