package br.com.acenetwork.commons.executor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.bukkit.Bukkit;
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

public class Tell implements TabExecutor
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

		if(args.length > 1)
		{			
			String msg = "";
	
			for(int i = 1; i < args.length; i++)			
			{
				msg += args[i] + " ";
			}

			tell(cp, Bukkit.getPlayer(args[0]), msg);
		}
		else
		{
			TextComponent[] extra = new TextComponent[1];
			
			extra[0] = new TextComponent("/" + aliases);
			extra[0].addExtra(" <" + bundle.getString("commons.words.player") + ">");
			extra[0].addExtra(" <" + bundle.getString("commons.words.message") + "...>");
			
			TextComponent text = Message.getTextComponent(bundle.getString("commons.cmds.wrong-syntax-try"), extra);
			text.setColor(ChatColor.RED);
			sender.spigot().sendMessage(text);
		}

		return true;
	}

	public static void tell(CommonPlayer cp, Player t, String msg)
	{
		Player p = cp.getPlayer();
		ResourceBundle bundle = ResourceBundle.getBundle("message", cp.getLocale());
		
		if(t == null || !p.canSee(t))
		{
			TextComponent text = new TextComponent(bundle.getString("commons.cmds.player-not-found"));
			text.setColor(ChatColor.RED);
			p.spigot().sendMessage(text);
			return;
		}

		if(t == p)
		{
			TextComponent text = new TextComponent(bundle.getString("commons.cmd.tell.cant-tell-yourself"));
			text.setColor(ChatColor.RED);
			p.spigot().sendMessage(text);
			return;
		}

		File targetFile = CommonsConfig.getFile(Type.PLAYER, false, t.getUniqueId());
		YamlConfiguration targetConfig = YamlConfiguration.loadConfiguration(targetFile);
		
		if(targetConfig.getStringList("ignored-players").contains(p.getUniqueId().toString()))
		{
			TextComponent text = new TextComponent(bundle.getString("commons.cmd.tell.cant-send-message"));
			text.setColor(ChatColor.RED);
			p.spigot().sendMessage(text);
			return;
		}

		File playerFile = CommonsConfig.getFile(Type.PLAYER, false, p.getUniqueId());
		YamlConfiguration playerConfig = YamlConfiguration.loadConfiguration(playerFile);

		targetConfig.set("last-reply", p.getUniqueId().toString());
		playerConfig.set("last-reply", p.getUniqueId().toString());

		try
		{
			playerConfig.save(playerFile);
			targetConfig.save(targetFile);
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
		}

		msg = msg.length() > 0 ? msg.substring(0, msg.length() - 1) : null;

		p.sendMessage(String.format("§7[%s§7 » %s§7]§l §f%s", p.getDisplayName(), t.getDisplayName(), msg));
		t.sendMessage(String.format("§7[%s§7 » %s§7]§l §f%s", p.getDisplayName(), t.getDisplayName(), msg));
	}
}