package br.com.acenetwork.bungee.listener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PluginMessage implements Listener
{
	@EventHandler
	public void on(PluginMessageEvent e)
	{
		String tag = e.getTag();
		ByteArrayDataInput in = ByteStreams.newDataInput(e.getData());
		
		if(tag.equalsIgnoreCase("commons:commons"))
		{
			String command = in.readUTF();
			
			if(command.equals("kick"))
			{
				String name = in.readUTF();
				String kickMessage = in.readUTF();
				
				ProxiedPlayer p = ProxyServer.getInstance().getPlayer(name);
				
				if(p != null)
				{
					p.disconnect(kickMessage);
				}
			}
			else if(command.equals("sendplayer"))
			{
				ProxiedPlayer p = ProxyServer.getInstance().getPlayer(in.readUTF());
				ServerInfo server = ProxyServer.getInstance().getServerInfo(in.readUTF());
				
				if(p != null && server != null)
				{
					p.connect(server);
				}
			}
		}
	}	
}