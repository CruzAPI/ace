package br.com.acenetwork.commons.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.StructureGrowEvent;

import br.com.acenetwork.commons.event.CustomStructureGrowEvent;

public class CustomStructureGrow implements Listener
{
	@EventHandler(priority = EventPriority.LOWEST)
	public void on(StructureGrowEvent e)
	{
		Bukkit.getPluginManager().callEvent(new CustomStructureGrowEvent(e));
	}
}