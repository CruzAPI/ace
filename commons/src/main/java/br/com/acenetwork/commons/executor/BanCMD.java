package br.com.acenetwork.commons.executor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import br.com.acenetwork.commons.CommonsUtil;
import br.com.acenetwork.commons.constants.Language;
import br.com.acenetwork.commons.constants.Tag;
import br.com.acenetwork.commons.inventory.Banishment;
import br.com.acenetwork.commons.manager.CommonsConfig;
import br.com.acenetwork.commons.manager.Message;
import br.com.acenetwork.commons.manager.CommonsConfig.Type;
import br.com.acenetwork.commons.player.CommonPlayer;
import br.com.acenetwork.commons.player.craft.CraftCommonPlayer;

public class BanCMD implements TabExecutor
{
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String aliases, String[] args)
	{
		List<String> list = new ArrayList<>();

		if(sender instanceof Player)
		{
			Player p = (Player) sender;
			CommonPlayer cp = CraftCommonPlayer.get(p);

			if(!cp.hasPermission("cmd.ban"))
			{
				return list;
			}
		}

		if(args.length == 1)
		{
			for(Player all : Bukkit.getOnlinePlayers())
			{
				if(all != sender && all.getName().toLowerCase().startsWith(args[0].toLowerCase()))
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
		CommonPlayer cp = null;

		if(sender instanceof Player)
		{
			Player p = (Player) sender;
			cp = CraftCommonPlayer.get(p);

			if(!cp.hasPermission("cmd.ban"))
			{
				cp.sendMessage("cmd.permission");
				return true;
			}

			locale = p.getLocale();
		}
		
		if(args.length > 0)
		{
			String user = args[0];
			String reason = "";

			if(sender.getName().equalsIgnoreCase(user))
			{
				cp.sendMessage("cmd.ban.cannot-ban-yourself");
				return true;
			}

			for(int i = 1; i < args.length; i++)			
			{
				reason += args[i] + " ";
			}

			reason = reason.length() > 0 ? reason.substring(0, reason.length() - 1) : null;

			if(cp != null)
			{
				new Banishment(cp, user, reason);
			}
			else
			{
				ban(sender, locale, user, 0L, Tag.ADMIN, reason);
			}
		}
		else
		{
			sender.sendMessage(Message.getMessage(locale, "cmd.wrong-syntax-try", "/" + aliases + " <player> [reason...]"));
		}
		
		return false;
	}

	public static void ban(CommandSender sender, String locale, String user, long time, Tag tag, String reason)
	{
		String uuid = CommonsUtil.getUUIDByName(user);

		if(uuid == null)
		{
			sender.sendMessage(Message.getMessage(locale, "cmd.user-not-found"));
			return;
		}

		File playerFile = CommonsConfig.getFile(Type.PLAYER, false, uuid);
		YamlConfiguration playerConfig = YamlConfiguration.loadConfiguration(playerFile);

		String nickname = playerConfig.getString("name");
		String ip = playerConfig.getString("ip");

		File bannedPlayersFile = CommonsConfig.getFile(Type.BANNED_PLAYERS, true, uuid);
		YamlConfiguration bannedPlayersConfig = YamlConfiguration.loadConfiguration(bannedPlayersFile);

		bannedPlayersConfig.set("name", nickname);
		bannedPlayersConfig.set("ip", ip);
		bannedPlayersConfig.set("time", time);
		bannedPlayersConfig.set("tag", tag.name());
		bannedPlayersConfig.set("by", sender.getName());
		bannedPlayersConfig.set("reason", reason);
		
		File bannedIpsFile = CommonsConfig.getFile(Type.BANNED_IPS, true, ip);
		YamlConfiguration bannedIpsConfig = YamlConfiguration.loadConfiguration(bannedIpsFile);

		bannedIpsConfig.set("uuid", uuid);

		try
		{
			bannedPlayersConfig.save(bannedPlayersFile);
			bannedIpsConfig.save(bannedIpsFile);

			CommonsUtil.bungeeKickPlayer(nickname, getKickMessage(sender.getName(), tag, time, reason).substring(2));

			for(CommonPlayer cpall : CraftCommonPlayer.SET)
			{
				Player all = cpall.getPlayer();
				cpall.sendMessage("cmd.ban.broadcast", nickname, tag + sender.getName());
				all.playSound(all.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1.0F, 1.0F);
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
			sender.sendMessage(Message.getMessage(locale, "commons.unexpected-error"));
		}
	}

	public static String getKickMessage(String by, Tag tag, long time, String reason)
	{
		String kickMessage = "\n\n§5❤ §3§l✦ §b§lACE NETWORK §3§l✦ §5❤";
		
		if(time == 0L)
		{
			kickMessage += "\n\n" + Message.getMessage(Language.ENGLISH.toString(), "cmd.ban.permanent", tag + by);
		}
		else
		{
			kickMessage += "\n\n" + Message.getMessage(Language.ENGLISH.toString(), "cmd.ban.temporary", tag + by);
			
			time -= System.currentTimeMillis();

			long seconds = time / 1000L % 60;
			long minutes = time / (60L * 1000L) % 60;
			long hours = time / (60L * 60L * 1000L) % 24;
			long days = time / (24L * 60L * 60L * 1000L);

			kickMessage += "\n" + Message.getMessage(Language.ENGLISH.toString(), "cmd.ban.days-hours-minutes-seconds", days, hours, minutes, seconds);
		}

		if(reason != null)
		{
			kickMessage += "\n\n" + "§f\"" + reason + "\"\n";
		}

		return kickMessage;
	}
}