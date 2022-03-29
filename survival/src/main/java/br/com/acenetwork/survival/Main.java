package br.com.acenetwork.survival;

import java.util.Locale;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import br.com.acenetwork.commons.Commons;
import br.com.acenetwork.survival.executor.Balance;
import br.com.acenetwork.survival.executor.Price;
import br.com.acenetwork.survival.executor.Reset;
import br.com.acenetwork.survival.executor.Sell;
import br.com.acenetwork.survival.executor.Sellall;
import br.com.acenetwork.survival.executor.Track;
import br.com.acenetwork.survival.listener.BlockBreak;
import br.com.acenetwork.survival.listener.EntityTarget;
import br.com.acenetwork.survival.listener.PlayerDeath;
import br.com.acenetwork.survival.listener.PlayerJoin;
import br.com.acenetwork.survival.listener.PlayerLogin;
import br.com.acenetwork.survival.listener.PlayerMode;
import br.com.acenetwork.survival.listener.PlayerQuit;
import br.com.acenetwork.survival.listener.PlayerRespawn;
import net.citizensnpcs.api.CitizensAPI;

public class Main extends JavaPlugin
{
	private static Main instance;

	@Override
	public void onEnable()
	{
		instance = this;
		
		Locale.setDefault(Locale.ENGLISH);		
		
		Bukkit.getPluginManager().registerEvents(new PlayerMode(), this);
		
		Commons.init(this);
		
		Bukkit.getPluginManager().registerEvents(new BlockBreak(), this);
		Bukkit.getPluginManager().registerEvents(new EntityTarget(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerDeath(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerJoin(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerLogin(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerQuit(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerRespawn(), this);
		
		Commons.registerCommand(new Balance(), "balance", "bal", "points", "coins");
		Commons.registerCommand(new Price(), "price");
		Commons.registerCommand(new Sell(), "sell");
		Commons.registerCommand(new Sellall(), "sellall");
		Commons.registerCommand(new Reset(), "reset");
		Commons.registerCommand(new Track(), "track");		
		
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(this, () ->
		{
			CitizensAPI.getNPCRegistry().deregisterAll();
		}, 1L);
		
		for(Player all : Bukkit.getOnlinePlayers())
		{
			all.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
		}	
	}
	
	@Override
	public void onDisable()
	{
		for(int id : PlayerQuit.UUID_MAP.keySet())
		{
			PlayerQuit.removeCombatLogger(CitizensAPI.getNPCRegistry().getById(id));
		}
	}
	
	public static Main getInstance()
	{
		return instance;
	}
}