package br.com.acenetwork.survival.listener;

import java.util.Iterator;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.EntityBlockFormEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import br.com.acenetwork.survival.Util;
import br.com.acenetwork.survival.event.TrackEvent;
import br.com.acenetwork.survival.executor.Track.TrackType;

public class SpawnProtection implements Listener
{
	@EventHandler(priority = EventPriority.HIGHEST)
	public void a(CreatureSpawnEvent e)
	{
		if(Util.isSpawn(e.getLocation()))
		{
			e.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void a(BlockPhysicsEvent e)
	{
		Block b = e.getBlock();
		
		if(Util.isSpawn(b))
		{
			e.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void a(BlockBreakEvent e)
	{
		Block b = e.getBlock();
		
		if(Util.isSpawn(b))
		{
			e.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void a(BlockPlaceEvent e)
	{
		Block b = e.getBlock();
		
		if(Util.isSpawn(b))
		{
			e.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void a(LeavesDecayEvent e)
	{
		Block b = e.getBlock();
		
		if(Util.isSpawn(b))
		{
			e.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void a(BlockBurnEvent e)
	{
		Block b = e.getBlock();
		
		if(Util.isSpawn(b))
		{
			e.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void a(BlockFadeEvent e)
	{
		Block b = e.getBlock();
		
		if(Util.isSpawn(b))
		{
			e.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void a(BlockFormEvent e)
	{
		Block b = e.getBlock();
		
		if(Util.isSpawn(b))
		{
			e.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void a(BlockFromToEvent e)
	{
		Block b = e.getBlock();
		
		if(Util.isSpawn(b))
		{
			e.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void a(EntityChangeBlockEvent e)
	{
		Block b = e.getBlock();
		
		if(Util.isSpawn(b))
		{
			e.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void a(BlockSpreadEvent e)
	{
		Block b = e.getBlock();
		
		if(Util.isSpawn(b))
		{
			e.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void a(BlockIgniteEvent e)
	{
		Block b = e.getBlock();
		
		if(Util.isSpawn(b))
		{
			e.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void a(BlockGrowEvent e)
	{
		Block b = e.getBlock();
		
		if(Util.isSpawn(b))
		{
			e.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void a(EntityBlockFormEvent e)
	{
		Block b = e.getBlock();
		
		if(Util.isSpawn(b))
		{
			e.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void a(BlockExplodeEvent e)
	{
		e.blockList().removeAll(e.blockList().stream().filter(x -> Util.isSpawn(x)).collect(Collectors.toList()));
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void a(EntityExplodeEvent e)
	{
		e.blockList().removeAll(e.blockList().stream().filter(x -> Util.isSpawn(x)).collect(Collectors.toList()));
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void a(BlockDamageEvent e)
	{
		Block b = e.getBlock();
		
		if(Util.isSpawn(b))
		{
			e.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void a(TrackEvent e)
	{
		if(e.getTrackType() != TrackType.COBBLESTONE)
		{
			return;
		}
		
		Iterator<Block> iterator = e.blockList().iterator();
		
		if(iterator.hasNext())
		{
			Block b = iterator.next();
			
			if(Util.isSpawn(b))
			{
				e.setCancelled(true);
				return;
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void a(PlayerBucketFillEvent e)
	{
		Block b = e.getBlock();
		
		if(Util.isSpawn(b))
		{
			e.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void a(PlayerInteractEvent e)
	{
		Block b = e.getClickedBlock();
		
		if(b == null)
		{
			return;
		}
		
		if(Util.isSpawn(b))
		{
			e.setCancelled(true);
		}
	}	
}
