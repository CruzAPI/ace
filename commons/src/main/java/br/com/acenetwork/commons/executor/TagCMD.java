package br.com.acenetwork.commons.executor;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import br.com.acenetwork.commons.constants.Language;
import br.com.acenetwork.commons.constants.Tag;
import br.com.acenetwork.commons.manager.Message;
import br.com.acenetwork.commons.player.CommonPlayer;
import br.com.acenetwork.commons.player.craft.CraftCommonPlayer;

public class TagCMD implements TabExecutor
{
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String aliases, String[] args)
	{
		List<String> list = new ArrayList<>();
		
		if(!(sender instanceof Player))
		{
			return list;
		}

		Player p = (Player) sender;
		CommonPlayer cp = CraftCommonPlayer.get(p);

		if(args.length == 1)
		{
			for(Tag tag : Tag.values())
			{
				if(tag.name().startsWith(args[0].toUpperCase()) && cp.hasPermission(tag.getPermission()))
				{
					list.add(tag.name());
				}
			}
		}

		return list;
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

		if(!p.isOp())
		{
			cp.sendMessage("cmd.permission");
			return true;
		}

		if(args.length == 0)
		{
			String tags = "";

			for(Tag tag : Tag.values())
			{
				if(cp.hasPermission(tag.getPermission()))
				{
					tags += tag.getName() + "§e, ";
				}
			}

			if(tags.length() > 2)
			{
				tags = tags.substring(0, tags.length() - 2) + "§e.";
			}

			cp.sendMessage("cmd.tag.your-tags", tags);
		}
		else if(args.length == 1)
		{
			try
			{
				Tag tag = Tag.valueOf(args[0].toUpperCase());

				if(cp.hasPermission(tag.getPermission()))
				{
					cp.setTag(tag);
					cp.sendMessage("cmd.tag.tag-selected", tag.getName());
				}
				else
				{
					cp.sendMessage("cmd.permission");
				}
			}
			catch(IllegalArgumentException e)
			{
				cp.sendMessage("cmd.tag.tag-not-found");
			}
		}
		else
		{
			cp.sendMessage("cmd.wrong-syntax-try", "/" + aliases + " [tag]");
		}

		return true;
	}
}