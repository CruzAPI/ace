package br.com.acenetwork.commons.executor;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import br.com.acenetwork.commons.CommonsUtil;
import br.com.acenetwork.commons.constants.Language;
import br.com.acenetwork.commons.manager.CommonsConfig;
import br.com.acenetwork.commons.manager.Message;
import br.com.acenetwork.commons.player.CommonPlayer;
import br.com.acenetwork.commons.player.craft.CraftCommonPlayer;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class Balance implements TabExecutor
{
	public enum Type
	{
		RAID(CommonsConfig.Type.BALANCE_RAID_PLAYER), MINIGAME(CommonsConfig.Type.BALANCE_MINIGAME_PLAYER);
		
		private final CommonsConfig.Type fileType;
		
		Type(CommonsConfig.Type fileType)
		{
			this.fileType = fileType;
		}
		
		public CommonsConfig.Type getFileType()
		{
			return fileType;
		}
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String aliases, String[] args)
	{
		List<String> list = new ArrayList<>();
		
		Player p = null;
		
		if(sender instanceof Player)
		{
			p = (Player) sender;
		}
		
		if(args.length == 1)
		{
			for(Player all : Bukkit.getOnlinePlayers())
			{
				if(all.getName().toLowerCase().startsWith(args[0].toLowerCase()) && 
					(p == null || p.canSee(all)))
				{
					list.add(all.getName());
				}
			}
		}

		return list;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String aliases, String[] args)
	{
		String locale = Language.ENGLISH.toString();
		String uuid = null;
		Player p = null;
		
		if(sender instanceof Player)
		{
			p = (Player) sender;
			CommonPlayer cp = CraftCommonPlayer.get(p);
			locale = p.getLocale();
			uuid = cp.getUUID();
		}
		
		DecimalFormat df = getDecimalFormat();
		
		String targetUUID;

		if(args.length == 0)
		{
			if(p == null)
			{
				sender.sendMessage(Message.getMessage(locale, "cmd.wrong-syntax-try", "/" + aliases + " <player>"));
				return true;
			}
			
			targetUUID = uuid;
		}
		else if(args.length == 1)
		{
			targetUUID = CommonsUtil.getUUIDByName(args[0]);
		}
		else
		{
			if(p == null)
			{
				sender.sendMessage(Message.getMessage(locale, "cmd.wrong-syntax-try", "/" + aliases + " <player>"));
				return true;
			}
			
			sender.sendMessage(Message.getMessage(locale, "cmd.wrong-syntax-try", "/" + aliases));
			return true;
		}

		if(targetUUID == null)
		{
			sender.sendMessage(Message.getMessage(locale, "cmd.user-not-found"));
			return true;
		}
		
		
		for(Type type : Type.values())
		{
			File file = CommonsConfig.getFile(type.getFileType(), true, targetUUID);
			YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
			
			double balance = config.getDouble("balance");
			double maxBalance = config.getDouble("max-balance");
			
			String formatBalance = df.format(balance);
			String formatMaxBalance = df.format(maxBalance);
			
			String balanceType = file.getParentFile().getName();
			
			if(targetUUID.equals(uuid))
			{
				sender.sendMessage(Message.getMessage(locale, "cmd.balance." + balanceType + ".self", formatBalance + "/" + formatMaxBalance));
			}
			else
			{
				File commonsPlayerFile = CommonsConfig.getFile(CommonsConfig.Type.PLAYER, false, targetUUID);
				YamlConfiguration commonsPlayerConfig = YamlConfiguration.loadConfiguration(commonsPlayerFile);
				
				String username = commonsPlayerConfig.getString("name");
				
				sender.sendMessage(Message.getMessage(locale, "cmd.balance." + balanceType + ".other", username, formatBalance + "/" + formatMaxBalance));
			}
		}	
		
		return false;
	}
	
	public static DecimalFormat getDecimalFormat()
	{
		return new DecimalFormat("0.##");
	}
}