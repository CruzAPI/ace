package br.com.acenetwork.bungee.executor;

import java.util.Locale;
import java.util.ResourceBundle;

import org.bukkit.entity.Player;

import br.com.acenetwork.bungee.Util;
import br.com.acenetwork.bungee.manager.Message;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class Alert extends Command
{
	public Alert(String name)
	{
		super(name);
	}
	
	@Override
	public void execute(CommandSender sender, String[] args)
	{
		ResourceBundle bundle = ResourceBundle.getBundle("message");
		
		if(sender instanceof Player)
		{
			ProxiedPlayer p = (ProxiedPlayer) sender;
			bundle = ResourceBundle.getBundle("message", p.getLocale());
			
			if(!Util.hasPermission(p.getUniqueId().toString(), "cmd.alert"))
			{
				TextComponent text = new TextComponent(bundle.getString("commons.cmds.permission"));
				text.setColor(ChatColor.RED);
				sender.sendMessage(text);
				return;
			}
		}
		
		
		if(args.length > 0)
		{
			alert(args);
		}
		else
		{
			TextComponent[] extra = new TextComponent[1];
			
			extra[0] = new TextComponent("/" + getName() + " <" + bundle.getString("commons.words.message") + "...>");
			
			TextComponent text = Message.getTextComponent(bundle.getString("commons.cmds.wrong-syntax-try"), extra);
			text.setColor(ChatColor.RED);
			sender.sendMessage(text);
		}
	}
	
	public static void alert(String... args)
	{
		String msg = "";

	 	for(int i = 0; i < args.length; i++)			
	 	{
	 		msg += args[i] + " ";
	 	}

	 	msg = msg.substring(0, msg.length() - 1);
	 	
		ProxyServer.getInstance().broadcast("§4§lALERT §f" + msg.replace('&', '§'));
	}
}