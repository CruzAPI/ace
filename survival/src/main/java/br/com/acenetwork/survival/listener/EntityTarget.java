package br.com.acenetwork.survival.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;

public class EntityTarget implements Listener
{
	@EventHandler
	public void on(EntityTargetEvent e)
	{
		e.setCancelled(false);
	}
}
