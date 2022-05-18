package br.com.acenetwork.survival.manager;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import br.com.acenetwork.survival.Main;

public class Land implements Listener
{
	private final int x, z;
	private final int n;
	private final Type type;
	
	public enum Type
	{
		BIG(63),
		MEDIUM(29), 
		SMALL(12), 
		;
		
		private final int size;
		
		Type(int size)
		{
			this.size = size;
		}
		
		public int getSize()
		{
			return size;
		}
	}
	
	public Land(int x, int z, Type type, int n)
	{
		this.x = x;
		this.z = z;
		this.n = n;
		this.type = type;
		Bukkit.broadcastMessage("Land: " + n + " X: " + x + " Z: " + z);
		Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
	public void on(BlockBreakEvent e)
	{
		Block b = e.getBlock();
		
		if(b.getX() >= x && b.getX() < x + type.size
				&& b.getZ() <= z && b.getZ() > z - type.size)
		{
			Bukkit.broadcastMessage("Land: " + n);
			e.setCancelled(true);
		}
	}
}