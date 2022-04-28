package br.com.acenetwork.commons.executor;

import java.io.File;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
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

import br.com.acenetwork.commons.manager.CommonsConfig;
import br.com.acenetwork.commons.manager.Message;
import br.com.acenetwork.commons.player.craft.CraftCommonPlayer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

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
		Player p = null;
		Locale locale = Locale.getDefault();
		
		if(sender instanceof Player)
		{
			p = (Player) sender;
			locale = CraftCommonPlayer.get(p).getLocale();
		}
		
		final ResourceBundle bundle = ResourceBundle.getBundle("message", locale);
		
		final OfflinePlayer op;
		
		if(args.length == 0 && p != null)
		{
			op = p;
		}
		else if(args.length == 1)
		{
			op = Arrays.stream(Bukkit.getOfflinePlayers()).filter(x -> 
				x.getName().equalsIgnoreCase(args[0])).findAny().orElse(null);
		}
		else
		{
			TextComponent[] extra = new TextComponent[1];
			
			extra[0] = new TextComponent("/");
			extra[0].addExtra(aliases);
			
			String player = bundle.getString("commons.cmds.args.player");
			
			if(p == null)
			{
				extra[0].addExtra(" <" + player + ">");
			}
			else
			{
				extra[0].addExtra(" [" + player + "]");
			}
			
			TextComponent text = Message.getTextComponent(bundle.getString("commons.cmds.wrong-syntax-try"), extra);
			text.setColor(ChatColor.RED);
			sender.spigot().sendMessage(text);
			return true;
		}

		if(op == null)
		{
			TextComponent text = Message.getTextComponent(bundle.getString("commons.cmds.user-not-found"));
			text.setColor(ChatColor.RED);
			sender.spigot().sendMessage(text);
			return true;
		}
		
		
		for(Type type : Type.values())
		{
			if(type == Type.MINIGAME)
			{
				continue;
			}
			
			File file = CommonsConfig.getFile(type.getFileType(), true, op.getUniqueId());
			YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
			
			double balance = config.getDouble("balance");
			double maxBalance = config.getDouble("max-balance");
			
			DecimalFormat df = new DecimalFormat("#,###.##", new DecimalFormatSymbols(bundle.getLocale()));
			
			if(p != null && op.getUniqueId().equals(p.getUniqueId()))
			{
				TextComponent[] extra = new TextComponent[1];
				
				extra[0] = new TextComponent();
				extra[0].addExtra(df.format(balance));
				extra[0].addExtra("/");
				extra[0].addExtra(df.format(maxBalance));
				extra[0].setColor(ChatColor.YELLOW);
				
				TextComponent text = Message.getTextComponent(bundle.getString("commons.cmd.balance.self"), extra);
				
				text.setColor(ChatColor.GREEN);
				
				sender.spigot().sendMessage(text);
			}
			else
			{
				TextComponent[] extra = new TextComponent[2];
				
				extra[0] = new TextComponent(op.getName());
				extra[0].setColor(ChatColor.YELLOW);
				
				extra[1] = new TextComponent();
				extra[1].addExtra(df.format(balance));
				extra[1].addExtra("/");
				extra[1].addExtra(df.format(maxBalance));
				extra[1].setColor(ChatColor.YELLOW);
				
				TextComponent text = Message.getTextComponent(bundle.getString("commons.cmd.balance.other"), extra);
				
				text.setColor(ChatColor.GREEN);
				
				sender.spigot().sendMessage(text);
			}
		}	
		
		return false;
	}
	
	public static DecimalFormat getDecimalFormat()
	{
		return new DecimalFormat("0.##");
	}
}