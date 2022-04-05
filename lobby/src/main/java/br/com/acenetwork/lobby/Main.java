package br.com.acenetwork.lobby;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import br.com.acenetwork.commons.Commons;
import br.com.acenetwork.lobby.listener.GeneralEvents;
import br.com.acenetwork.lobby.listener.PlayerJoin;
import br.com.acenetwork.lobby.listener.PlayerMode;
import br.com.acenetwork.lobby.listener.PlayerModeChange;

public class Main extends JavaPlugin
{
	private static Main instance;
	
	private static Location spawnLocation;
	
	@Override
	public void onEnable()
	{
		instance = this;
		spawnLocation = new Location(Bukkit.getWorld("world"), 119.5D, 6.0D, -261.5D, 0.0F, 0.0F);
		
		Bukkit.getPluginManager().registerEvents(new PlayerMode(), this);
		
		Commons.init(this);
		
		Bukkit.getPluginManager().registerEvents(new GeneralEvents(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerJoin(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerModeChange(), this);
	}
	
	public static Main getInstance()
	{
		return instance;
	}
	
	public static Location getSpawnLocation()
	{
		return spawnLocation;
	}
}