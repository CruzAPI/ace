package br.com.acenetwork.survival;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import br.com.acenetwork.commons.Commons;
import br.com.acenetwork.commons.executor.Balance;
import br.com.acenetwork.commons.manager.CommonsConfig;
import br.com.acenetwork.commons.player.craft.CraftCommonPlayer;
import br.com.acenetwork.survival.executor.Price;
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
import br.com.acenetwork.survival.manager.Config;
import br.com.acenetwork.survival.manager.Config.Type;
import net.citizensnpcs.api.CitizensAPI;
import net.md_5.bungee.api.ChatColor;

public class Main extends JavaPlugin
{
	private static Main instance;
	private static final Random RANDOM = new Random();
	private boolean firstRun = true;
	
	@Override
	public void onEnable()
	{
		instance = this;
		
		Locale.setDefault(Locale.ENGLISH);		
		
		Bukkit.getPluginManager().registerEvents(new PlayerMode(), this);
		
		Commons.registerBroadcast("raid.broadcast1");
		Commons.registerBroadcast("raid.broadcast2");
		Commons.registerBroadcast("raid.broadcast3");
		Commons.registerBroadcast("raid.broadcast4");
		Commons.registerBroadcast("raid.broadcast5");
		Commons.registerBroadcast("raid.broadcast6");
		Commons.registerBroadcast("raid.broadcast7");
		Commons.init(this);
		
		Bukkit.getPluginManager().registerEvents(new BlockBreak(), this);
		Bukkit.getPluginManager().registerEvents(new EntityTarget(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerDeath(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerJoin(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerLogin(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerQuit(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerRespawn(), this);
		
		Commons.registerCommand(new Price(), "price");
		Commons.registerCommand(new Sell(), "sell");
		Commons.registerCommand(new Sellall(), "sellall");
		Commons.registerCommand(new Track(), "track");		
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(this, () ->
		{
			CitizensAPI.getNPCRegistry().deregisterAll();
		}, 1L);
		
		for(Player all : Bukkit.getOnlinePlayers())
		{
			all.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
		}
		
		instance.getRunnable().run();
	}
	
	@Override
	public void onDisable()
	{
		for(int id : new HashSet<>(PlayerQuit.UUID_MAP.keySet()))
		{
			PlayerQuit.removeCombatLogger(CitizensAPI.getNPCRegistry().getById(id));
		}
	}
	
	public Runnable getRunnable()
	{
		return new Runnable()
		{
			@Override
			public void run()
			{
				File file = Config.getFile(Type.PRICE, false);
				YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
				
				File botFile = Config.getFile(Type.BOT, true);
				YamlConfiguration botConfig = YamlConfiguration.loadConfiguration(botFile);
				
				ConfigurationSection section = config.getConfigurationSection("");
				
				if(section == null)
				{
					return;
				}
				
				final int minPercent = botConfig.getInt("min-percent");
				final int maxPercent = botConfig.getInt("max-percent");
				final int minDelay = botConfig.getInt("min-delay");
				final int maxDelay = botConfig.getInt("max-delay");
				
				List<String> list = new ArrayList<>(section.getKeys(false));
				String key = list.get(RANDOM.nextInt(list.size()));
				
				int marketCap = config.getInt(key + ".market-cap");
				double aceShards = config.getDouble(key + ".ace-shards");
				double max = config.getDouble(key + ".max");
				
				double percent = (minPercent + RANDOM.nextInt(maxPercent - minPercent + 1)) / 100.0D;
				int amount = (int) (percent * marketCap);
				
				double oldPrice = aceShards / marketCap;
				double newPrice = (aceShards + oldPrice * amount) / (marketCap - amount);
				final double price = (newPrice + oldPrice) / 2.0D;
				
				if(oldPrice >= max)
				{
					Bukkit.broadcastMessage(key + " max valued !!! ");
					return;
				}
				
				double priceChangePercent = (price / oldPrice - 1.0D) * 100.0D;
				
				marketCap -= amount;
				aceShards += price;
				
				config.set(key + ".market-cap", marketCap);
				config.set(key + ".ace-shards", aceShards);
				
				if(firstRun == (firstRun = false))
				{
					try
					{
						config.save(file);
						Bukkit.broadcastMessage("oi " + ChatColor.of("#012345").toString().replace('§', '&') + " oi");
						Bukkit.broadcastMessage("§aUm Sheikh Árabe comprou §e" + key + " x" + amount + " §b" + oldPrice + " -> " 
								+ price + " §a" + Balance.getDecimalFormat().format(priceChangePercent) + "%");
						Bukkit.broadcastMessage("§aPreço subiu em §e" + Balance.getDecimalFormat().format(priceChangePercent) + "%");
						Bukkit.broadcastMessage("§aLua!");
						
						CraftCommonPlayer.SET.forEach(x -> x.sendMessage("raid/broadcast/bot", key, null));
					}
					catch(IOException ex)
					{
						ex.printStackTrace();
					}
				}
				
				Bukkit.getScheduler().scheduleSyncDelayedTask(instance, getRunnable(), minDelay + RANDOM.nextInt(maxDelay - minDelay + 1));
			}
		};
	}
	
	public static Main getInstance()
	{
		return instance;
	}
}