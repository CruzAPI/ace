package br.com.acenetwork.commons.listener;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

public class EntitySpawn implements Listener
{
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void on(EntitySpawnEvent e)
	{
		Entity entity = e.getEntity();

		if(entity instanceof Item)
		{
//			Bukkit.getScheduler().scheduleSyncDelayedTask(Commons.getPlugin(), () ->
//			{
//				entity.remove();
//			}, 2L* 60L * 20L);
		}
	}
}
