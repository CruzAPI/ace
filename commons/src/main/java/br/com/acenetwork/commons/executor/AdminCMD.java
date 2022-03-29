package br.com.acenetwork.commons.executor;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import br.com.acenetwork.commons.constants.Language;
import br.com.acenetwork.commons.event.PlayerModeEvent;
import br.com.acenetwork.commons.manager.Message;
import br.com.acenetwork.commons.player.CommonAdmin;
import br.com.acenetwork.commons.player.CommonPlayer;
import br.com.acenetwork.commons.player.craft.CraftCommonAdmin;
import br.com.acenetwork.commons.player.craft.CraftCommonPlayer;

public class AdminCMD implements TabExecutor
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
		
		if(!cp.hasPermission("cmd.admin"))
		{
			cp.sendMessage("cmd.permission");
			return true;
		}
		
		if(args.length == 0)
		{
			if(cp instanceof CommonAdmin)
			{
				Bukkit.getPluginManager().callEvent(new PlayerModeEvent(p.getPlayer()));
				cp.sendMessage("cmd.admin.player-mode");
				cp.sendMessage("cmd.invis.visible-to-all");
			}
			else
			{
				cp = new CraftCommonAdmin(p);
				cp.sendMessage("cmd.admin.admin-mode");
				cp.sendMessage("cmd.invis.invisible-to-all");
			}
		}
		else
		{
			cp.sendMessage("cmd.wrong-syntax-try", "/" + aliases);
		}

		return false;
	}
}