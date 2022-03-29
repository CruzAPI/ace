package br.com.acenetwork.commons.executor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import br.com.acenetwork.commons.CommonsUtil;
import br.com.acenetwork.commons.constants.Language;
import br.com.acenetwork.commons.manager.CommonsConfig;
import br.com.acenetwork.commons.manager.Message;
import br.com.acenetwork.commons.manager.CommonsConfig.Type;
import br.com.acenetwork.commons.player.CommonPlayer;
import br.com.acenetwork.commons.player.craft.CraftCommonPlayer;

public class Pardon implements TabExecutor
{
	@Override
	public List<String> onTabComplete(CommandSender arg0, Command arg1, String arg2, String[] arg3)
	{
		//TODO
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

			if(!cp.hasPermission("cmd.pardon"))
			{
				cp.sendMessage("cmd.permission");
				return true;
			}

			locale = p.getLocale();
		}
		
		if(args.length == 1)
		{
			String user = args[0];

			String uuid = CommonsUtil.getUUIDByName(user);

			if(uuid == null)
			{
				sender.sendMessage(Message.getMessage(locale, "cmd.user-not-found"));
				return true;
			}

			File playerFile = CommonsConfig.getFile(Type.PLAYER, false, uuid);
			YamlConfiguration playerConfig = YamlConfiguration.loadConfiguration(playerFile);
			
			String nickname = playerConfig.getString("name");
			String ip = playerConfig.getString("ip");
			
			File bannedPlayersFile = CommonsConfig.getFile(Type.BANNED_PLAYERS, false, uuid);
			File bannedIpsFile = CommonsConfig.getFile(Type.BANNED_IPS, false, ip);
			
			boolean deletePlayer = bannedPlayersFile.delete();
			boolean deleteIp = bannedIpsFile.delete();

			if(deletePlayer || deleteIp)
			{
				sender.sendMessage(Message.getMessage(locale, "cmd.pardon.user-pardoned", nickname));
			}
			else
			{
				sender.sendMessage(Message.getMessage(locale, "cmd.pardon.user-is-not-banned", nickname));
			}
		}
		else
		{
			sender.sendMessage(Message.getMessage(locale, "cmd.wrong-syntax-try", "/" + aliases + " <player>"));
		}
		
		return false;
	}
}
