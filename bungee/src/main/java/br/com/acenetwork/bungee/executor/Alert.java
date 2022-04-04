package br.com.acenetwork.bungee.executor;

import java.util.Locale;

import br.com.acenetwork.bungee.Util;
import br.com.acenetwork.bungee.manager.Message;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
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
		String locale = "en_us";
		
		if(sender instanceof ProxiedPlayer)
		{
			ProxiedPlayer p = (ProxiedPlayer) sender;
			locale = p.getLocale().toString().toLowerCase();
			
			if(!Util.hasPermission(p.getUniqueId().toString(), "cmd.alert"))
			{
				p.sendMessage(Message.getMessage(locale, "cmd.permission"));
				return;
			}
		}
		
		if(args.length > 0)
		{
			String msg = "";

		 	for(int i = 0; i < args.length; i++)			
		 	{
		 		msg += args[i] + " ";
		 	}

		 	msg = msg.substring(0, msg.length() - 1);
		 	
			ProxyServer.getInstance().broadcast("§4§lALERT §f" + msg.replace('&', '§'));
		}
		else
		{
			sender.sendMessage(Message.getMessage(locale, "cmd.wrong-syntax-try", "/" + getName()));
		}
	}
}