package br.com.acenetwork.tntrun.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

public class GeneralEvents implements Listener
{
	@EventHandler
	public void on(EntityDamageEvent e)
	{
		e.setCancelled(true);
	}
	
	@EventHandler
	public void on(PlayerInteractEvent e)
	{
		e.setCancelled(true);
	}
	
	@EventHandler
	public void on(PlayerDropItemEvent e)
	{
		e.setCancelled(true);
	}
	
	@EventHandler
	public void on(PlayerPickupItemEvent e)
	{
		e.setCancelled(true);
	}
	
	@EventHandler
	public void on(FoodLevelChangeEvent e)
	{
		e.setCancelled(true);
	}
	
	@EventHandler
	public void on(WeatherChangeEvent e)
	{
		e.setCancelled(true);
	}
	
	@EventHandler
	public void on(EntityTargetEvent e)
	{
		e.setCancelled(true);
	}
	
	@EventHandler
	public void on(BlockBreakEvent e)
	{
		e.setCancelled(true);
	}
	
	@EventHandler
	public void on(BlockPlaceEvent e)
	{
		e.setCancelled(true);
	}
}
