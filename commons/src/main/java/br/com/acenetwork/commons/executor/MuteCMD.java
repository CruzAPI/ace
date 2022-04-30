package br.com.acenetwork.commons.executor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import br.com.acenetwork.commons.constants.Language;
import br.com.acenetwork.commons.constants.Tag;
import br.com.acenetwork.commons.inventory.Mute;
import br.com.acenetwork.commons.manager.CommonsConfig;
import br.com.acenetwork.commons.manager.CommonsConfig.Type;
import br.com.acenetwork.commons.manager.Message;
import br.com.acenetwork.commons.player.CommonPlayer;
import br.com.acenetwork.commons.player.craft.CraftCommonPlayer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

public class MuteCMD implements TabExecutor
{
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String aliases, String[] args)
	{
		List<String> list = new ArrayList<>();

		if(sender instanceof Player)
		{
			Player p = (Player) sender;
			CommonPlayer cp = CraftCommonPlayer.get(p);

			if(!cp.hasPermission("cmd.mute"))
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
		CommonPlayer cp = null;
		boolean hasPermission = true;
		ResourceBundle bundle = ResourceBundle.getBundle("message");
		
		if(sender instanceof Player)
		{
			Player p = (Player) sender;
			cp = CraftCommonPlayer.get(p);
			hasPermission = cp.hasPermission("cmd.mute");
			bundle = ResourceBundle.getBundle("message", cp.getLocale());
		}
		
		if(!hasPermission)
		{
			TextComponent text = new TextComponent(bundle.getString("commons.dont-have-permission"));
			text.setColor(ChatColor.RED);
			sender.spigot().sendMessage(text);
			return true;
		}
		
		if(args.length > 0)
		{
			String user = args[0];
			String reason = "";

			if(sender.getName().equalsIgnoreCase(user))
			{
				cp.sendMessage("cmd.mute.cannot-mute-yourself");
				return true;
			}

			for(int i = 1; i < args.length; i++)			
			{
				reason += args[i] + " ";
			}

			reason = reason.length() > 0 ? reason.substring(0, reason.length() - 1) : null;

			if(cp != null)
			{
				new Mute(cp, user, reason);
			}
			else
			{
				mute(sender, locale, user, 0L, Tag.ADMIN, reason);
			}
		}
		else
		{
			sender.sendMessage(Message.getMessage(locale, "cmd.wrong-syntax-try", "/" + aliases + " <player> [reason...]"));
		}
		
		return false;
	}


	public static void mute(CommandSender sender, String locale, String user, long time, Tag tag, String reason)
	{
		OfflinePlayer op = Arrays.stream(Bukkit.getOfflinePlayers()).filter(x -> 
			x.getName().equalsIgnoreCase(user)).findAny().orElse(null);
		
		if(op == null)
		{
			sender.sendMessage(Message.getMessage(locale, "cmd.user-not-found"));
			return;
		}

		File playerFile = CommonsConfig.getFile(Type.PLAYER, false, op.getUniqueId());
		YamlConfiguration playerConfig = YamlConfiguration.loadConfiguration(playerFile);

		String nickname = playerConfig.getString("name");

		File mutedPlayersFile = CommonsConfig.getFile(Type.MUTED_PLAYERS, true, op.getUniqueId());
		YamlConfiguration mutedPlayersConfig = YamlConfiguration.loadConfiguration(mutedPlayersFile);

		mutedPlayersConfig.set("name", nickname);
		mutedPlayersConfig.set("time", time);
		mutedPlayersConfig.set("tag", tag.name());
		mutedPlayersConfig.set("by", sender.getName());
		mutedPlayersConfig.set("reason", reason);

		try
		{
			mutedPlayersConfig.save(mutedPlayersFile);

			sender.sendMessage(Message.getMessage(locale, "cmd.mute.user-muted", nickname));
		}
		catch(IOException e)
		{
			e.printStackTrace();
			sender.sendMessage(Message.getMessage(locale, "commons.unexpected-error"));
		}
	}

	public static String getWarnMessage(String locale, String by, Tag tag, long time, String reason)
	{
		String warnMessage = "\n\n§5❤ §3§l✦ §b§lACE NETWORK §3§l✦ §5❤";
		
		if(time == 0L)
		{
			warnMessage += "\n\n" + Message.getMessage(locale, "cmd.mute.permanent", tag + by);
		}
		else
		{
			warnMessage += "\n\n" + Message.getMessage(locale, "cmd.mute.temporary", tag + by);
			
			time -= System.currentTimeMillis();

			long seconds = time / 1000L % 60;
			long minutes = time / (60L * 1000L) % 60;
			long hours = time / (60L * 60L * 1000L) % 24;
			long days = time / (24L * 60L * 60L * 1000L);

			warnMessage += "\n" + Message.getMessage(locale, "cmd.mute.days-hours-minutes-seconds", 
				days, hours, minutes, seconds);
		}

		if(reason != null)
		{
			warnMessage += "\n" + "§f\"" + reason + "\"\n";
		}

		return warnMessage;
	}
}