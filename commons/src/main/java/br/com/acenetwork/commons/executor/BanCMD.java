package br.com.acenetwork.commons.executor;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import br.com.acenetwork.commons.constants.Tag;
import br.com.acenetwork.commons.inventory.Banishment;
import br.com.acenetwork.commons.manager.CommonsConfig;
import br.com.acenetwork.commons.manager.CommonsConfig.Type;
import br.com.acenetwork.commons.manager.Message;
import br.com.acenetwork.commons.player.CommonPlayer;
import br.com.acenetwork.commons.player.craft.CraftCommonPlayer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

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
		CommonPlayer cp = null;
		boolean hasPermission = true;
		ResourceBundle bundle = ResourceBundle.getBundle("message");
		
		if(sender instanceof Player)
		{
			Player p = (Player) sender;
			cp = CraftCommonPlayer.get(p);
			hasPermission = cp.hasPermission("cmd.ban");
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
				TextComponent text = new TextComponent(bundle.getString("commons.cmd.ban.cant-ban-yourself"));
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
				new Banishment(cp, user, reason);
			}
			else
			{
				ban(sender, bundle.getLocale(), user, 0L, Tag.ADMIN, reason);
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

	public static void ban(CommandSender sender, Locale locale, String user, long time, Tag tag, String reason)
	{
		ResourceBundle bundle = ResourceBundle.getBundle("message", locale);
		
		OfflinePlayer op = Arrays.stream(Bukkit.getOfflinePlayers()).filter(x -> 
				x.getName().equalsIgnoreCase(user)).findAny().orElse(null);
		
		if(op == null)
		{
			TextComponent text = new TextComponent(bundle.getString("commons.cmds.user-not-found"));
			text.setColor(ChatColor.RED);
			sender.spigot().sendMessage(text);
			return;
		}

		File playerFile = CommonsConfig.getFile(Type.PLAYER, false, op.getUniqueId());
		YamlConfiguration playerConfig = YamlConfiguration.loadConfiguration(playerFile);

		String ip = playerConfig.getString("ip");

		File bannedPlayersFile = CommonsConfig.getFile(Type.BANNED_PLAYERS, true, op.getUniqueId());
		YamlConfiguration bannedPlayersConfig = YamlConfiguration.loadConfiguration(bannedPlayersFile);

		bannedPlayersConfig.set("name", op.getName());
		bannedPlayersConfig.set("ip", ip);
		bannedPlayersConfig.set("time", time);
		bannedPlayersConfig.set("tag", tag.name());
		bannedPlayersConfig.set("by", sender.getName());
		bannedPlayersConfig.set("reason", reason);
		
		File bannedIpsFile = CommonsConfig.getFile(Type.BANNED_IPS, true, ip);
		YamlConfiguration bannedIpsConfig = YamlConfiguration.loadConfiguration(bannedIpsFile);

		bannedIpsConfig.set("uuid", op.getUniqueId().toString());

		try
		{
			bannedPlayersConfig.save(bannedPlayersFile);
			bannedIpsConfig.save(bannedIpsFile);
			
			String displayName;
			
			if(op.isOnline())
			{
				Player p = op.getPlayer();
				displayName = op.getPlayer().getDisplayName();
				p.kickPlayer(getKickMessage(sender.getName(), tag, time, reason));
			}
			else
			{
				displayName = op.getName();
			}
			
//			TODO
//			CommonsUtil.bungeeKickPlayer(nickname, getKickMessage(sender.getName(), tag, time, reason).substring(2));
			
			TextComponent[] extra = new TextComponent[2];
			
			extra[0] = new TextComponent();
			extra[0].addExtra(displayName);
			extra[0].setColor(ChatColor.GRAY);
			
			extra[1] = new TextComponent();
			extra[1].addExtra(sender instanceof Player ? ((Player) sender).getDisplayName() : sender.getName());
			extra[1].setColor(ChatColor.RED);
			
			for(CommonPlayer cpall : CraftCommonPlayer.SET)
			{
				ResourceBundle allBundle = ResourceBundle.getBundle("message" , cpall.getLocale());
				Player all = cpall.getPlayer();
				
				TextComponent text = Message.getTextComponent(allBundle.getString("commons.cmd.ban.broadcast"), extra);
				text.setColor(ChatColor.GREEN);
				all.spigot().sendMessage(text);
				
				all.playSound(all.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1.0F, 1.0F);
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
			TextComponent text = new TextComponent(bundle.getString("commons.unexpected-error"));
			text.setColor(ChatColor.RED);
			sender.spigot().sendMessage(text);
		}
	}

	public static String getKickMessage(String by, Tag tag, long time, String reason)
	{
		String kickMessage = "\n\n§5❤ §3§l✦ §b§lACE NETWORK §3§l✦ §5❤";
		
		if(time == 0L)
		{
			kickMessage += MessageFormat.format("\n\n§7Your account has been permanently banned by {0}§7.", tag + by);
		}
		else
		{
			kickMessage += MessageFormat.format("\n\n§7Your account has been temporarily banned by {0}§7.", tag + by);
			
			time -= System.currentTimeMillis();

			long seconds = time / 1000L % 60;
			long minutes = time / (60L * 1000L) % 60;
			long hours = time / (60L * 60L * 1000L) % 24;
			long days = time / (24L * 60L * 60L * 1000L);

			kickMessage += MessageFormat.format("\n§c§o{0}d {1}h {2}m {3}s", days, hours, minutes, seconds);
		}

		if(reason != null)
		{
			kickMessage += "\n\n§f\"" + reason + "\"\n";
		}

		return kickMessage;
	}
}