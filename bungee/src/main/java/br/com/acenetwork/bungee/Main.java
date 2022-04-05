package br.com.acenetwork.bungee;

import java.io.File;

import br.com.acenetwork.bungee.executor.Alert;
import br.com.acenetwork.bungee.executor.Send;
import br.com.acenetwork.bungee.listener.PluginMessage;
import br.com.acenetwork.bungee.listener.PostLogin;
import br.com.acenetwork.bungee.listener.ServerDisconnect;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

public class Main extends Plugin
{
	@Override
	public void onEnable()
	{
		ProxyServer.getInstance().registerChannel("commons:commons");
		
		ProxyServer.getInstance().getPluginManager().registerCommand(this, new Alert("alert"));	
		ProxyServer.getInstance().getPluginManager().registerCommand(this, new Send("send"));	
		
		ProxyServer.getInstance().getPluginManager().registerListener(this, new PluginMessage());	
		ProxyServer.getInstance().getPluginManager().registerListener(this, new PostLogin());	
		ProxyServer.getInstance().getPluginManager().registerListener(this, new ServerDisconnect());	
	}
	
	public static File getCommonsDataFolder()
	{
		return new File(System.getProperty("user.home") +  "/.aceconfig");
	}
}