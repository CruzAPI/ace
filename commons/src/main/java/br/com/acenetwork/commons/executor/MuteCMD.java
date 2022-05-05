package br.com.acenetwork.commons.executor;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

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
			TextComponent text = new TextComponent(bundle.getString("commons.cmds.permission"));
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
				TextComponent text = new TextComponent(bundle.getString("commons.cmd.mute.cant-mute-yourself"));
				text.setColor(ChatColor.RED);
				sender.spigot().sendMessage(text);
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
				mute(sender, user, 0L, Tag.ADMIN, reason);
			}
		}
		else
		{
			TextComponent[] extra = new TextComponent[1];
			
			extra[0] = new TextComponent("/" + aliases);
			extra[0].addExtra(" <" + bundle.getString("commons.cmds.args.player") + ">");
			extra[0].addExtra(" [" + bundle.getString("commons.cmds.args.reason") + "...]");
			
			TextComponent text = Message.getTextComponent(bundle.getString("commons.cmds.wrong-syntax-try"), extra); 
			text.setColor(ChatColor.RED);
			sender.spigot().sendMessage(text);
		}
		
		return false;
	}


	public static void mute(CommandSender sender, String user, long time, Tag tag, String reason)
	{
		ResourceBundle bundle = ResourceBundle.getBundle("message");
		
		if(sender instanceof Player)
		{
			Player p = (Player) sender;
			CommonPlayer cp = CraftCommonPlayer.get(p);
			bundle = ResourceBundle.getBundle("message", cp.getLocale());
		}
		
		OfflinePlayer op = Arrays.stream(Bukkit.getOfflinePlayers()).filter(x -> 
				x.getName().equalsIgnoreCase(user)).findAny().orElse(null);
		
		if(op == null)
		{
			TextComponent text = new TextComponent(bundle.getString("commons.cmds.user-not-found"));
			text.setColor(ChatColor.RED);
			sender.spigot().sendMessage(text);
			return;
		}

		File mutedPlayersFile = CommonsConfig.getFile(Type.MUTED_PLAYERS, true, op.getUniqueId());
		YamlConfiguration mutedPlayersConfig = YamlConfiguration.loadConfiguration(mutedPlayersFile);

		mutedPlayersConfig.set("name", op.getName());
		mutedPlayersConfig.set("time", time);
		mutedPlayersConfig.set("tag", tag.name());
		mutedPlayersConfig.set("by", sender.getName());
		mutedPlayersConfig.set("reason", reason);

		try
		{
			mutedPlayersConfig.save(mutedPlayersFile);
			
			TextComponent[] extra = new TextComponent[1];
			
			extra[0] = new TextComponent(op.getName());
			extra[0].setColor(ChatColor.YELLOW);
			
			
			TextComponent text = Message.getTextComponent(bundle.getString("commons.cmd.mute.user-muted"), extra);
			text.setColor(ChatColor.GREEN);
			sender.spigot().sendMessage(text);
		}
		catch(IOException e)
		{
			e.printStackTrace();
			TextComponent text = new TextComponent(bundle.getString("commons.unexpected-error"));
			text.setColor(ChatColor.RED);
			sender.spigot().sendMessage(text);
		}
	}

	public static String getWarnMessage(Locale locale, String by, Tag tag, long time, String reason)
	{
		ResourceBundle bundle = ResourceBundle.getBundle("message", locale);
		
		String warnMessage = "\n\n§5❤ §3§l✦ §b§lACE NETWORK §3§l✦ §5❤\n\n";
		
		TextComponent[] extra = new TextComponent[1];
		
		extra[0] = new TextComponent();
		extra[0].addExtra(tag + by);
		extra[0].setColor(ChatColor.RED);
		
		if(time == 0L)
		{
			TextComponent text = Message.getTextComponent(bundle.getString("commons.cmd.mute.permanent"), extra);
			text.setColor(ChatColor.GRAY);
			warnMessage += text.toLegacyText();
		}
		else
		{
			TextComponent text = Message.getTextComponent(bundle.getString("commons.cmd.mute.temporary"), extra);
			text.setColor(ChatColor.GRAY);
			warnMessage += text.toLegacyText();
			
			time -= System.currentTimeMillis();

			long seconds = time / 1000L % 60;
			long minutes = time / (60L * 1000L) % 60;
			long hours = time / (60L * 60L * 1000L) % 24;
			long days = time / (24L * 60L * 60L * 1000L);
			
			DecimalFormat df = new DecimalFormat("00");
			
			warnMessage += "\n§c§o" + df.format(days) + ":" + df.format(hours) + ":" + df.format(minutes) + ":" + df.format(seconds);
		}

		if(reason != null)
		{
			warnMessage += "\n" + "§f\"" + reason + "\"\n";
		}

		return warnMessage;
	}
}