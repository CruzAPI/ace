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

public class Reply implements TabExecutor
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
		
		if(args.length > 0)
		{
			String msg = "";
	
			for(int i = 0; i < args.length; i++)			
			{
				msg += args[i] + " ";
			}

			File playerFile = CommonsConfig.getFile(Type.PLAYER, false, cp.getUniqueID());
			YamlConfiguration playerConfig = YamlConfiguration.loadConfiguration(playerFile);

			String targetUUID = playerConfig.getString("last-reply");

			if(targetUUID == null)
			{
				cp.sendMessage("cmd.reply.no-player-to-reply");
			}
			else
			{
				Tell.tell(cp, CommonsUtil.getNameByUUID(targetUUID), msg);
			}
		}
		else
		{
			cp.sendMessage("cmd.wrong-syntax-try", "/" + aliases + " <msg...>");
		}

		return true;
	}
}