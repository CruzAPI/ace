package br.com.acenetwork.survival.listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Beacon;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.type.Dispenser;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFertilizeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockMultiPlaceEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.EntityBlockFormEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.block.SpongeAbsorbEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityPlaceEvent;
import org.bukkit.event.entity.SheepRegrowWoolEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.event.world.StructureGrowEvent;

import br.com.acenetwork.commons.CommonsUtil;
import br.com.acenetwork.commons.event.CustomBlockFertilizeEvent;
import br.com.acenetwork.commons.event.CustomSpongeAbsorbEvent;
import br.com.acenetwork.commons.event.CustomStructureGrowEvent;
import br.com.acenetwork.commons.player.craft.CraftCommonPlayer;
import br.com.acenetwork.survival.Util;
import br.com.acenetwork.survival.event.TrackEvent;
import br.com.acenetwork.survival.executor.Track.TrackType;

public class SpawnProtection implements Listener
{
	@EventHandler(priority = EventPriority.HIGHEST)
	public void aasda(CustomSpongeAbsorbEvent ce)
	{
		SpongeAbsorbEvent e = ce.getSpongeAbsorbEvent();
		e.getBlocks().removeAll(e.getBlocks().stream().filter(x -> Util.isSpawn(x.getLocation())).collect(Collectors.toList()));
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void aasdas(EntityPlaceEvent e)
	{
		
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void aasdas(CreatureSpawnEvent e)
	{
		SpawnReason reason = e.getSpawnReason();
		
		if(Util.isSpawn(e.getLocation()) && 
				(reason == SpawnReason.BUILD_IRONGOLEM || reason == SpawnReason.BUILD_SNOWMAN || reason == SpawnReason.BUILD_WITHER))
		{
			e.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void a(BlockSpreadEvent e)
	{
		Block b = e.getSource();
		BlockState block = e.getNewState();
		
		if(Util.isSpawn(b) || Util.isSpawn(block.getLocation()))
		{
			e.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void a(BlockGrowEvent e)
	{
		Block b = e.getBlock();
		BlockState block = e.getNewState();
		
		if(Util.isSpawn(b) || Util.isSpawn(block.getLocation()))
		{
			e.setCancelled(true);
		}
	}
	
//	@EventHandler(priority = EventPriority.HIGHEST)
//	public void a(SheepRegrowWoolEvent e)
//	{
//		e.
//	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void a(BlockDispenseEvent e)
	{
		Block b = e.getBlock();
		Dispenser dispenser = (Dispenser) b.getState().getBlockData();
		
		if(Util.isSpawn(b) || (Util.isSpawn(b.getRelative(dispenser.getFacing())) && Util.isDispensable(e.getItem().getType())))
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
//	
//	
//	@EventHandler(priority = EventPriority.HIGHEST)
//	public void a(LeavesDecayEvent e)
//	{
//		Block b = e.getBlock();
//		
//		if(Util.isSpawn(b))
//		{
//			e.setCancelled(true);
//		}
//	}
//	
//	@EventHandler(priority = EventPriority.HIGHEST)
//	public void a(BlockBurnEvent e)
//	{
//		Block b = e.getBlock();
//		
//		if(Util.isSpawn(b))
//		{
//			e.setCancelled(true);
//		}
//	}
//	
//	@EventHandler(priority = EventPriority.HIGHEST)
//	public void a(BlockFadeEvent e)
//	{
//		Block b = e.getBlock();
//		
//		if(Util.isSpawn(b))
//		{
//			e.setCancelled(true);
//		}
//	}
//	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void a(BlockFormEvent e)
	{
		Block b = e.getBlock();
		
		if(Util.isSpawn(b))
		{
			e.setCancelled(true);
		}
	}
//	
//	@EventHandler(priority = EventPriority.HIGHEST)
//	public void a(BlockSpreadEvent e)
//	{
//		Block b = e.getBlock();
//		
//		if(Util.isSpawn(b))
//		{
//			e.setCancelled(true);
//		}
//	}
//	
//	@EventHandler(priority = EventPriority.HIGHEST)
//	public void a(BlockIgniteEvent e)
//	{
//		Block b = e.getBlock();
//		
//		if(Util.isSpawn(b))
//		{
//			e.setCancelled(true);
//		}
//	}
//	
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void a(CustomBlockFertilizeEvent ce)
	{
		BlockFertilizeEvent e = ce.getBlockFertilizeEvent();
		e.getBlocks().removeAll(e.getBlocks().stream().filter(x -> Util.isSpawn(x.getLocation())).collect(Collectors.toList()));
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void a(CustomStructureGrowEvent ce)
	{
		StructureGrowEvent e = ce.getStructureGrowEvent();
		e.getBlocks().removeAll(e.getBlocks().stream().filter(x -> Util.isSpawn(x.getLocation())).collect(Collectors.toList()));
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
//	
//	@EventHandler(priority = EventPriority.HIGHEST)
//	public void a(BlockDamageEvent e)
//	{
//		Block b = e.getBlock();
//		
//		if(Util.isSpawn(b))
//		{
//			e.setCancelled(true);
//		}
//	}
//	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void a(PortalCreateEvent e)
	{
		for(BlockState blocks : e.getBlocks())
		{
			if(Util.isSpawn(blocks.getBlock()))
			{
				if(e.getEntity() instanceof Player)
				{
					Player p = (Player) e.getEntity();
					ResourceBundle bundle = ResourceBundle.getBundle("message", CommonsUtil.getLocaleFromMinecraft(p.getLocale()));
					p.sendMessage("§c" + bundle.getString("raid.event.portal-create.cant-create-portal-spawn"));
				}
				
				e.setCancelled(true);
				return;
			}
		}
	}
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void a(TrackEvent e)
	{
		if(e.getTrackType() != TrackType.COBBLESTONE)
		{
			return;
		}
		
		for(Block b : e.blockList())
		{
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
	public void a(PlayerBucketEmptyEvent e)
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
	public void a(BlockPistonExtendEvent e)
	{
		for(Block blocks : e.getBlocks())
		{
			blocks = blocks.getRelative(e.getDirection());
			
			if(Util.isSpawn(blocks))
			{
				e.setCancelled(true);
				return;
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void a(BlockPistonRetractEvent e)
	{
		for(Block blocks : e.getBlocks())
		{
			if(Util.isSpawn(blocks))
			{
				e.setCancelled(true);
				return;
			}
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
	public void a(BlockFromToEvent e)
	{
		Block b = e.getToBlock();
		
		if(Util.isSpawn(b))
		{
			e.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void a(BlockMultiPlaceEvent e)
	{
		for(BlockState bs : e.getReplacedBlockStates())
		{
			if(Util.isSpawn(bs.getLocation()))
			{
				e.setCancelled(true);
				return;
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void a(PlayerInteractEvent e)
	{
		Block b = e.getClickedBlock();
		Action a = e.getAction();
		
		if(b == null)
		{
			return;
		}
		
		if(a == Action.RIGHT_CLICK_BLOCK)
		{
			if(Util.isSpawn(b))
			{
				e.setUseInteractedBlock(Result.DENY);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void a(HangingBreakByEntityEvent e)
	{
		Entity entity = e.getEntity();
		
		if(Util.isSpawn(entity.getLocation()))
		{
			e.setCancelled(true);
		}
	}
}