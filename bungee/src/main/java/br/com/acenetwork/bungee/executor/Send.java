package br.com.acenetwork.bungee.executor;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import br.com.acenetwork.bungee.Util;
import br.com.acenetwork.bungee.manager.Message;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class Send// extends Command
{
//	public Send(String name)
//	{
//		super(name);
//	}
//	
//	@Override
//	public void execute(CommandSender sender, String[] args)
//	{
//		if(sender instanceof ProxiedPlayer)
//		{
//			ProxiedPlayer p = (ProxiedPlayer) sender;
//			
//			if(!Util.hasPermission(p.getUniqueId().toString(), "cmd.alert"))
//			{
//				p.sendMessage(Message.getMessage(´, "cmd.permission"));
//				return;
//			}
//		}
//		
//		if(args.length == 2)
//		{
//			List<ProxiedPlayer> list = new ArrayList<>();
//			
//			if(args[0].equalsIgnoreCase("all"))
//			{
//				list = new ArrayList<>(ProxyServer.getInstance().getPlayers());
//			}
//			else
//			{
//				ServerInfo serverOrigin = ProxyServer.getInstance().getServerInfo(args[0]);
//				
//				if(serverOrigin != null)
//				{
//					list = new ArrayList<>(serverOrigin.getPlayers());
//				}
//				else	
//				{					
//					ProxiedPlayer t = ProxyServer.getInstance().getPlayer(args[0]);
//					
//					if(t == null)
//					{
//						sender.sendMessage(Message.getMessage(locale, "cmd.player-not-found"));
//						return;
//					}
//					
//					list.add(t);
//				}
//			}
//			
//			ServerInfo server = ProxyServer.getInstance().getServerInfo(args[1]);
//			
//			if(server == null)
//			{
//				sender.sendMessage(Message.getMessage(locale, "cmd.server-not-found"));
//				return;
//			}
//			
//			for(ProxiedPlayer all : list)
//			{
//				all.connect(server);
//			}
//			
//			sender.sendMessage(Message.getMessage(locale, "cmd.send.players-sent", list.size(), server.getName()));
//		}
//		else
//		{
//			sender.sendMessage(Message.getMessage(locale, "cmd.wrong-syntax-try", "/" + getName() + " <all:player:server> <server>"));
//		}
//	}
}