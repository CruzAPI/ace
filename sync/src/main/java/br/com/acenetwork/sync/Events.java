package br.com.acenetwork.sync;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPortalEnterEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

public class Events implements Listener
{
	@EventHandler
	public void a(PlayerPortalEvent e)
	{
		e.setCancelled(true);
	}
	
	@EventHandler
	public void a(PlayerRespawnEvent e)
	{
		e.setRespawnLocation(Main.getInstance().getSpawnLocation());
	}
	
	@EventHandler
	public void a(PlayerDeathEvent e)
	{
		e.setDeathMessage(null);
	}
	
	@EventHandler
	public void a(PlayerQuitEvent e)
	{
		e.setQuitMessage(null);
	}
	
	@EventHandler
	public void a(PlayerChatEvent e)
	{
		e.setCancelled(true);
	}
	
	@EventHandler
	public void a(PlayerInteractEvent e)
	{
		e.setCancelled(true);
	}
	
	@EventHandler
	public void a(BlockBreakEvent e)
	{
		e.setCancelled(true);
	}
	
	@EventHandler
	public void a(BlockPlaceEvent e)
	{
		e.setCancelled(true);
	}
	
	@EventHandler
	public void a(PlayerDropItemEvent e)
	{
		e.setCancelled(true);
	}
	
	@EventHandler
	public void a(PlayerPickupItemEvent e)
	{
		e.setCancelled(true);
	}
	
	@EventHandler
	public void a(InventoryClickEvent e)
	{
		e.setCancelled(true);
	}
	
	@EventHandler
	public void a(EntityDamageEvent e)
	{
		e.setCancelled(true);
	}
	
	@EventHandler
	public void a(EntitySpawnEvent e)
	{
		e.setCancelled(true);
	}
	
	@EventHandler
	public void a(FoodLevelChangeEvent e)
	{
		e.setCancelled(true);
	}
	
	@EventHandler
	public void a(WeatherChangeEvent e)
	{
		e.setCancelled(true);
	}
}