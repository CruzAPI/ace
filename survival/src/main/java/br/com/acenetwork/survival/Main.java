package br.com.acenetwork.survival;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.ResourceBundle;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import br.com.acenetwork.commons.Commons;
import br.com.acenetwork.commons.manager.Message;
import br.com.acenetwork.commons.player.CommonPlayer;
import br.com.acenetwork.commons.player.craft.CraftCommonPlayer;
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
import br.com.acenetwork.survival.manager.Config;
import br.com.acenetwork.survival.manager.Config.Type;
import net.citizensnpcs.api.CitizensAPI;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

public class Main extends JavaPlugin
{
	private static Main instance;
	private static final Random RANDOM = new Random();
	private static final List<String> BROADCASTS = new ArrayList<>();
	private boolean firstRun = true;

	
	@Override
	public void onEnable()
	{
		instance = this;
		
		Locale.setDefault(Locale.ENGLISH);		
		
		Bukkit.getPluginManager().registerEvents(new PlayerMode(), this);
		
		Commons.init(this);
		
		BROADCASTS.add("raid.broadcast.item.1");
		BROADCASTS.add("raid.broadcast.item.2");
		BROADCASTS.add("raid.broadcast.item.3");
		BROADCASTS.add("raid.broadcast.item.4");
		BROADCASTS.add("raid.broadcast.item.5");
		BROADCASTS.add("raid.broadcast.item.6");
		BROADCASTS.add("raid.broadcast.item.7");
		BROADCASTS.add("raid.broadcast.item.8");
		BROADCASTS.add("raid.broadcast.item.9");
		BROADCASTS.add("raid.broadcast.item.10");
		
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
		Commons.registerCommand(new Reset(), "reset");		
		
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
	
	private Runnable getRunnable()
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
				
				final int minPercent = botConfig.getInt("min-percent");
				final int maxPercent = botConfig.getInt("max-percent");
				final int minDelay = botConfig.getInt("min-delay");
				final int maxDelay = botConfig.getInt("max-delay");
				
				Bukkit.getScheduler().scheduleSyncDelayedTask(instance, getRunnable(), minDelay + RANDOM.nextInt(maxDelay - minDelay + 1));
				
				if(section == null)
				{
					return;
				}
				
				
				List<String> list = new ArrayList<>(section.getKeys(false));
				
				if(list.size() <= 0)
				{
					return;
				}
				
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
						
						List<CommandSender> senderList = new ArrayList<CommandSender>(Bukkit.getOnlinePlayers());
						senderList.add(Bukkit.getConsoleSender());
						
						String bundleKey = BROADCASTS.get(RANDOM.nextInt(BROADCASTS.size()));
						
						for(CommandSender sender : senderList)
						{
							ResourceBundle bundle = ResourceBundle.getBundle("message", Locale.getDefault());
							
							if(sender instanceof Player)
							{
								Player p = (Player) sender;
								CommonPlayer cp = CraftCommonPlayer.get(p);
								bundle = ResourceBundle.getBundle("message", cp.getLocale());
							}
							
							DecimalFormat df = new DecimalFormat("#.##", new DecimalFormatSymbols(bundle.getLocale()));
							df.setGroupingSize(3);
							df.setGroupingUsed(true);
							
							TextComponent[] extra = new TextComponent[1];
							
							extra[0] = new TextComponent(df.format(amount) + " " + key);
							extra[0].setColor(ChatColor.YELLOW);
							
							TextComponent text = Message.getTextComponent(bundle.getString(bundleKey), extra);
							text.setColor(ChatColor.GOLD);
							sender.spigot().sendMessage(text);
							sender.sendMessage("§e" + df.format(oldPrice) + "§7 » §e" + df.format(price) + " §a(+" + df.format(priceChangePercent) + "%)");
						}
					}
					catch(IOException ex)
					{
						ex.printStackTrace();
					}
				}
			}
		};
	}
	
	public static Main getInstance()
	{
		return instance;
	}
}