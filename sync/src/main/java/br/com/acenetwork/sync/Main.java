package br.com.acenetwork.sync;

import java.util.Locale;
import java.util.ResourceBundle;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

public class Main extends JavaPlugin implements Listener
{
	private static Main instance;
	
	@Override
	public void onEnable()
	{
		instance = this;
		
		Wallet wallet = new Wallet();
		
		getCommand("wallet").setTabCompleter(wallet);
		getCommand("wallet").setExecutor(wallet);
		
		Bukkit.getWorlds().forEach(x -> x.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false));
		
//		for(World w : Bukkit.getWorlds())
//		{
//			
//		}
		
		Bukkit.getPluginManager().registerEvents(this, this);
		Bukkit.getPluginManager().registerEvents(new Events(), this);
		
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () ->
		{
			for(Player p : Bukkit.getOnlinePlayers())
			{
				ResourceBundle bundle = ResourceBundle.getBundle("message", getLocaleFromMinecraft(p.getLocale()));
				
				TextComponent extra1 = new TextComponent("➟ ");
				extra1.setColor(ChatColor.GRAY);
				
				TextComponent text = new TextComponent();
				text.setColor(ChatColor.GREEN);
				
				TextComponent[] extra = new TextComponent[1];
				
				extra[0] = new TextComponent("/wallet <" + bundle.getString("address") + ">");
				extra[0].setColor(ChatColor.DARK_GREEN);
				
				text.addExtra(extra1);
				text.addExtra(Message.getTextComponent(bundle.getString("link-your-wallet"), extra));
				p.spigot().sendMessage(text);
			}
		}, 200L, 200L);
	}
	
	public static Locale getLocaleFromMinecraft(String locale)
	{
		String[] split = locale.split("_");
		
		if(split.length == 1)
		{
			return new Locale(split[0]);
		}
		else
		{
			return new Locale(split[0], split[1]);
		}
	}
	
	@EventHandler
	public void a(PlayerJoinEvent e)
	{
		e.setJoinMessage(null);
		
		Player p = e.getPlayer();
		
		for(Player all : Bukkit.getOnlinePlayers())
		{
			all.hidePlayer(this, p);
			p.hidePlayer(this, all);
		}
		
		for(PotionEffect effect : p.getActivePotionEffects())
		{
			p.removePotionEffect(effect.getType());
		}
		
		p.setCollidable(true);
		p.setVelocity(new Vector());
		p.setMaximumNoDamageTicks(20);
		p.setFireTicks(0);
		p.setFreezeTicks(0);
		p.setArrowsInBody(0);
		
		p.getInventory().clear();
		p.getInventory().setArmorContents(null);
		
		p.setMaxHealth(20.0D);
		p.setHealth(p.getMaxHealth());
		p.setFoodLevel(20);
		
		p.setFlySpeed(0.1F);
		p.setWalkSpeed(0.2F);
		p.setExp(0.0F);
		p.setLevel(0);
		
		p.setGameMode(GameMode.ADVENTURE);
		p.teleport(getSpawnLocation());
	}

	public Location getSpawnLocation()
	{
		return new Location(Bukkit.getWorld("world_the_end"), 500.5D, 250.0D, 500.5D, 0.0F, 0.0F);
	}
	
	public static Main getInstance()
	{
		return instance;
	}
}