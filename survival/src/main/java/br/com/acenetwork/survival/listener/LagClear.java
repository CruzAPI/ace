package br.com.acenetwork.survival.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.world.EntitiesLoadEvent;
import org.bukkit.metadata.FixedMetadataValue;

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;

import br.com.acenetwork.survival.Main;

public class LagClear implements Listener
{
	private static final long ITEM_TICKS = 3L * 60L * 20L;
	
	@EventHandler
	public void a(EntityRemoveFromWorldEvent e)
	{
		Entity entity = e.getEntity();
		
		if(entity instanceof Item)
		{
			Item item = (Item) entity;
			
			if(item.hasMetadata("task"))
			{
				int id = item.getMetadata("task").get(0).asInt();
				item.removeMetadata("task", Main.getInstance());
				item.setMetadata("ticksLived", new FixedMetadataValue(Main.getInstance(), entity.getTicksLived()));
				Bukkit.getScheduler().cancelTask(id);
			}
		}
	}
	
	@EventHandler
	public void a(EntitiesLoadEvent e)
	{
		for(Entity entity : e.getEntities())
		{
			if(entity instanceof Item)
			{
				Item item = (Item) entity;
				long ticksLived = entity.getTicksLived();
				
				if(item.hasMetadata("ticksLived"))
				{
					ticksLived = item.getMetadata("ticksLived").get(0).asLong();
				}
				
				item.setMetadata("task", new FixedMetadataValue(Main.getInstance(), 
						Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () ->
				{
					item.remove();
				}, Math.max(0L, ITEM_TICKS - ticksLived))));
			}
		}
	}
	
	@EventHandler
	public void a(ItemSpawnEvent e)
	{
		Item item = e.getEntity();
		
		item.setMetadata("task", new FixedMetadataValue(Main.getInstance(), 
				Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () ->
		{
			item.remove();
		}, ITEM_TICKS)));
	}
}