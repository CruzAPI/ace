package br.com.acenetwork.tntrun.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import br.com.acenetwork.commons.event.PlayerModeChangeEvent;

public class GeneralEvents implements Listener
{
	@EventHandler
	public void on(PlayerModeChangeEvent e)
	{
		Player p = e.getCommonPlayer().getPlayer();
		p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 999999, 0));
	}
	
	@EventHandler
	public void on(BlockPhysicsEvent e)
	{
		e.setCancelled(true);
	}
	
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
	public void on(EntityDropItemEvent e)
	{
		e.setCancelled(true);
	}
	
	@EventHandler
	public void on(EntityPickupItemEvent e)
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
