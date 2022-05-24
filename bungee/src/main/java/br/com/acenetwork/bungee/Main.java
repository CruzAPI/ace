package br.com.acenetwork.bungee;

import java.io.File;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import br.com.acenetwork.bungee.executor.Alert;
import br.com.acenetwork.bungee.executor.Reset;
import br.com.acenetwork.bungee.listener.PluginMessage;
import br.com.acenetwork.bungee.listener.PostLogin;
import br.com.acenetwork.bungee.listener.ServerDisconnect;
import br.com.acenetwork.bungee.manager.Config;
import br.com.acenetwork.bungee.manager.Config.Type;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

public class Main extends Plugin
{
	public static final boolean TEST = !new File(System.getProperty("user.dir")).getParentFile().getName().equals("acenetwork");
	private static Main instance;
	
	@Override
	public void onEnable()
	{
		instance = this;
		
		ProxyServer.getInstance().registerChannel("commons:commons");
		
		if(TEST)
		{
			ProxyServer.getInstance().getConsole().sendMessage("§dServer is in test mode!");
		}
		else
		{
			ProxyServer.getInstance().getConsole().sendMessage("§aServer is in production!");
		}
		
		ProxyServer.getInstance().getPluginManager().registerCommand(this, new Alert("alert"));	
		ProxyServer.getInstance().getPluginManager().registerCommand(this, new Reset("reset"));	
//		ProxyServer.getInstance().getPluginManager().registerCommand(this, new Send("send"));	
		
		ProxyServer.getInstance().getPluginManager().registerListener(this, new PluginMessage());	
		ProxyServer.getInstance().getPluginManager().registerListener(this, new PostLogin());
		//ProxyServer.getInstance().getPluginManager().registerListener(this, new ProxyPing());
		ProxyServer.getInstance().getPluginManager().registerListener(this, new ServerDisconnect());	
		
		Config.getFile(Type.BALANCE_FOLDER, true);
		
		if(Reset.getLastResetDayOfMonth() != Reset.getTodayResetDayOfMonth())
		{
			try
			{
				Reset.reset();
			}
			catch(SQLException e)
			{
				e.printStackTrace();
			}
		}
		
		ProxyServer.getInstance().getScheduler().schedule(this, () ->
		{
			try
			{
				Reset.reset();
			}
			catch(SQLException e)
			{
				e.printStackTrace();
			}
		}, Reset.getDelay(), 24L * 60L * 60L * 1000L, TimeUnit.MILLISECONDS);
	}
	
	public static Main getInstance()
	{
		return instance;
	}
	
	public static File getCommonsDataFolder()
	{
		if(TEST)
		{
			return new File(System.getProperty("user.home") +  "/.aceconfigtest");
		}
			
		return new File(System.getProperty("user.home") +  "/.aceconfig");
	}
}