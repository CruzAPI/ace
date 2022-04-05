package br.com.acenetwork.bungee.listener;

import br.com.acenetwork.bungee.Util;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.ServerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ServerDisconnect implements Listener
{
	@EventHandler
	public void on(ServerDisconnectEvent e)
	{
		Util.sendDataPlayerCount(ProxyServer.getInstance().getPlayers().size());
	}
}