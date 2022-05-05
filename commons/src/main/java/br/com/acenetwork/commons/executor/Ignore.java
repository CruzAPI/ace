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

import br.com.acenetwork.commons.manager.CommonsConfig;
import br.com.acenetwork.commons.manager.Message;
import br.com.acenetwork.commons.manager.CommonsConfig.Type;
import br.com.acenetwork.commons.player.CommonPlayer;
import br.com.acenetwork.commons.player.craft.CraftCommonPlayer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

public class Ignore implements TabExecutor
{
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) 
	{
		List<String> list = new ArrayList<>();

		if(!(sender instanceof Player))
		{
			return list;
		}

		Player p = (Player) sender;

		if(args.length == 1)
		{
			for(Player all : Bukkit.getOnlinePlayers())
			{
				if(all != p && all.getName().toLowerCase().startsWith(args[0].toLowerCase()) && p.canSee(all))
				{
					list.add(all.getName());
				}
			}
		}

		return list;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String aliases, String[] args)
	{
		ResourceBundle bundle = ResourceBundle.getBundle("message");
		
		if(!(sender instanceof Player))
		{
			TextComponent text = new TextComponent(bundle.getString("commons.cmds.cant-perform-command"));
			text.setColor(ChatColor.RED);
			sender.spigot().sendMessage(text);
			return true;
		}

		Player p = (Player) sender;
		CommonPlayer cp = CraftCommonPlayer.get(p);
		
		bundle = ResourceBundle.getBundle("message", cp.getLocale());
		
		if(args.length == 1)
		{
			OfflinePlayer op = Arrays.stream(Bukkit.getOfflinePlayers()).filter(x -> 
					x.getName().equalsIgnoreCase(args[0])).findAny().orElse(null);
			
			if(p.getName().equalsIgnoreCase(args[0]))
			{
				TextComponent text = new TextComponent(bundle.getString("commons.cmd.ignore.cannot-ignore-yourself"));
				text.setColor(ChatColor.RED);
				sender.spigot().sendMessage(text);
				return true;
			}

			if(op == null)
			{
				TextComponent text = new TextComponent(bundle.getString("commons.cmds.user-not-found"));
				text.setColor(ChatColor.RED);
				sender.spigot().sendMessage(text);
				return true;
			}

			File playerFile = CommonsConfig.getFile(Type.PLAYER, false, p.getUniqueId());
			YamlConfiguration playerConfig = YamlConfiguration.loadConfiguration(playerFile);

			List<String> ignoredPlayers = playerConfig.getStringList("ignored-players");
			
			String messageKey;

			if(ignoredPlayers.contains(op.getUniqueId().toString()))
			{
				ignoredPlayers.remove(op.getUniqueId().toString());
				messageKey = "commons.cmd.ignore.removed-from-list";
			}
			else
			{
				ignoredPlayers.add(op.getUniqueId().toString());
				messageKey = "commons.cmd.ignore.added-to-list";
			}

			playerConfig.set("ignored-players", ignoredPlayers);
			
			try
			{
				playerConfig.save(playerFile);
				
				TextComponent[] extra = new TextComponent[1];
				
				extra[0] = new TextComponent(op.getName());
				extra[0].setColor(ChatColor.YELLOW);
				
				TextComponent text = Message.getTextComponent(bundle.getString(messageKey), extra);
				text.setColor(ChatColor.GREEN);
				sender.spigot().sendMessage(text);
			}
			catch(IOException ex)
			{
				ex.printStackTrace();
				TextComponent text = new TextComponent(bundle.getString("commons.unexpected-error"));
				text.setColor(ChatColor.RED);
				sender.spigot().sendMessage(text);
			}
		}
		else
		{
			TextComponent[] extra = new TextComponent[1];
			
			extra[0] = new TextComponent("/" + aliases);
			extra[0].addExtra(" <" + bundle.getString("commons.words.player") + ">");
			
			TextComponent text = Message.getTextComponent(bundle.getString("commons.cmds.wrong-syntax-try"), extra);
			text.setColor(ChatColor.RED);
			sender.spigot().sendMessage(text);
		}

		return true;
	}
}