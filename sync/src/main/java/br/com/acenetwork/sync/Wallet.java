package br.com.acenetwork.sync;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.text.WordUtils;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

public class Wallet implements TabExecutor
{
	@Override
	public List<String> onTabComplete(CommandSender arg0, Command arg1, String arg2, String[] arg3)
	{
		return new ArrayList<>();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String aliases, String[] args)
	{
		if(!(sender instanceof Player))
		{
			return true;
		}
		
		Player p = (Player) sender;
		ResourceBundle bundle = ResourceBundle.getBundle("message", Main.getLocaleFromMinecraft(p.getLocale()));
		
		if(args.length == 1)
		{
			String address = args[0];
			
			if(!address.matches("0x[a-fA-F0-9]{40}"))
			{
				TextComponent text = new TextComponent(bundle.getString("invalid-wallet-address"));
				text.setColor(ChatColor.RED);
				sender.spigot().sendMessage(text);
				return true;
			}
			
			String kickMessage = "§5❤ §3§l✦ §b§lACE NETWORK §3§l✦ §5❤";

			
			TextComponent text1 = new TextComponent(bundle.getString("wallet-linked-sucessfully"));
			text1.setColor(ChatColor.GREEN);
			
			TextComponent extra = new TextComponent(args[0]);
			extra.setColor(ChatColor.WHITE);
			
			TextComponent text2 = new TextComponent(WordUtils.capitalize(bundle.getString("address") + ": "));
			text2.addExtra(extra);
			text2.setColor(ChatColor.GRAY);
			
			kickMessage += "\n\n" + text1.toLegacyText() + "\n\n" + text2.toLegacyText();
			
			p.kickPlayer(kickMessage);
		}
		else
		{
			TextComponent[] extra = new TextComponent[1];
			
			extra[0] = new TextComponent("/" + aliases + " <" + bundle.getString("address") + ">");
			
			TextComponent text = Message.getTextComponent(bundle.getString("wrong-syntax-try"), extra);
			text.setColor(ChatColor.RED);
			sender.spigot().sendMessage(text);
		}
		
		
		return false;
	}
}