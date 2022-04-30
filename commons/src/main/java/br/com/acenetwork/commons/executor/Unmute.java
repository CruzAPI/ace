package br.com.acenetwork.commons.executor;

import java.io.File;
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

public class Unmute implements TabExecutor
{
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String aliases, String[] args)
	{
		//TODO
		return new ArrayList<>();
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
			hasPermission = cp.hasPermission("cmd.unmute");
			bundle = ResourceBundle.getBundle("message", cp.getLocale());
		}
		
		if(!hasPermission)
		{
			TextComponent text = new TextComponent(bundle.getString("commons.dont-have-permission"));
			text.setColor(ChatColor.RED);
			sender.spigot().sendMessage(text);
			return true;
		}
		
		if(args.length == 1)
		{
			OfflinePlayer op = Arrays.stream(Bukkit.getOfflinePlayers()).filter(x -> 
					x.getName().equalsIgnoreCase(args[0])).findAny().orElse(null);
			
			if(op == null)
			{
				TextComponent text = new TextComponent(bundle.getString("commons.cmds.user-not-found"));
				text.setColor(ChatColor.RED);
				sender.spigot().sendMessage(text);
				return true;
			}

			File playerFile = CommonsConfig.getFile(Type.PLAYER, false, op.getUniqueId());
			YamlConfiguration playerConfig = YamlConfiguration.loadConfiguration(playerFile);
			
			String nickname = playerConfig.getString("name");
			
			File mutedPlayersFile = CommonsConfig.getFile(Type.MUTED_PLAYERS, false, op.getUniqueId());
			
			if(mutedPlayersFile.delete())
			{
				TextComponent[] extra = new TextComponent[1];
				
				extra[0] = new TextComponent(nickname);
				extra[0].setColor(ChatColor.YELLOW);
				
				TextComponent text = Message.getTextComponent(bundle.getString("commons.cmd.unmute.user-unmuted"), extra);
				text.setColor(ChatColor.GREEN);
				sender.spigot().sendMessage(text);
			}
			else
			{
				TextComponent[] extra = new TextComponent[1];
				
				extra[0] = new TextComponent(nickname);
				
				TextComponent text = Message.getTextComponent(bundle.getString("commons.cmd.unmute.user-isnt-muted"), extra);
				text.setColor(ChatColor.RED);
				sender.spigot().sendMessage(text);
			}
		}
		else
		{
			TextComponent[] extra = new TextComponent[1];
			
			extra[0] = new TextComponent("/" + aliases + " <" + bundle.getString("commons.words.player") + ">");
			
			TextComponent text = Message.getTextComponent(bundle.getString("commons.cmds.wrong-syntax-try"), extra);
			text.setColor(ChatColor.RED);
			sender.spigot().sendMessage(text);
		}
		
		return false;
	}
}