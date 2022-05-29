package br.com.acenetwork.commons.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFertilizeEvent;
import org.bukkit.event.block.SpongeAbsorbEvent;
import org.bukkit.event.world.StructureGrowEvent;

import br.com.acenetwork.commons.event.CustomBlockFertilizeEvent;
import br.com.acenetwork.commons.event.CustomSpongeAbsorbEvent;
import br.com.acenetwork.commons.event.CustomStructureGrowEvent;

public class CustomStructureGrow implements Listener
{
	@EventHandler(priority = EventPriority.LOWEST)
	public void on(StructureGrowEvent e)
	{
		Bukkit.getPluginManager().callEvent(new CustomStructureGrowEvent(e));
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void on(BlockFertilizeEvent e)
	{
		Bukkit.getPluginManager().callEvent(new CustomBlockFertilizeEvent(e));
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void on(SpongeAbsorbEvent e)
	{
		Bukkit.getPluginManager().callEvent(new CustomSpongeAbsorbEvent(e));
	}
}