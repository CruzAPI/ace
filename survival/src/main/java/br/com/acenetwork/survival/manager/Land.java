package br.com.acenetwork.survival.manager;

import static br.com.acenetwork.survival.manager.Land.Direction.SOUTH;

import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockFertilizeEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockMultiPlaceEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.StructureGrowEvent;

import br.com.acenetwork.commons.event.CustomBlockFertilizeEvent;
import br.com.acenetwork.commons.event.CustomStructureGrowEvent;
import br.com.acenetwork.survival.Main;
import br.com.acenetwork.survival.Util;
import br.com.acenetwork.survival.event.TrackEvent;
import br.com.acenetwork.survival.executor.Track.TrackType;

public class Land implements Listener
{
	private OfflinePlayer owner;
	
	public enum Direction
	{
		EAST(1, 0),
		NORTH(0, -1), 
		WEST(-1, 0), 
		SOUTH(0, 1),
		;
		
		private final int x;
		private final int z;
		
		Direction(int x, int z)
		{
			this.x = x;
			this.z = z;
		}
	}
	
	private static final int PATH_WIDTH = 5;
	
	private final int x, z;
	private final int id;
	private final Type type;
	
	public static void loadLands()
	{
		int x = -65, z = 65;
		int k = 0;
		
		int id = 0;
		
		for(int j = 0; j < Land.Type.values().length; j++)
		{
			Land.Type type = Land.Type.values()[j];
			final int size = type.getSize();
			
			z += PATH_WIDTH * SOUTH.z + size * SOUTH.z;
			
			k = (1 + k) * 2 + 1;
			
			for(int i = 0; i < Direction.values().length; i++)
			{
				Direction direction = Direction.values()[i];
				
				for(int l = 0; l < k; l++)
				{
					if(l == 0 && i == 0)
					{
						new Land(id++, x, z, type);
						continue;
					}
					
					x += PATH_WIDTH * direction.x + size * direction.x; 
					z += PATH_WIDTH * direction.z + size * direction.z;
					
					new Land(id++, x, z, type);
				}
			}
		}
	}
	
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
	
	private boolean isLand(Block b)
	{
		return b != null && isLand(b.getLocation());
	}
	
	private boolean isLand(Location l)
	{
		return isLand(l.getWorld(), l.getBlockX(), l.getBlockZ());
	}
	
	private boolean isLand(World w, int x, int z)
	{
		return w.getName().equals("world") && x >= this.x && x < this.x + type.size && z <= this.z && z > this.z - type.size;
	}
	
	private boolean isOwner(Player p)
	{
		return owner != null && p != null && owner.getUniqueId().equals(p.getUniqueId());
	}
	
	public Land(int id, int x, int z, Type type)
	{
		this.id = id;
		this.x = x;
		this.z = z;
		this.type = type;
		
		Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
		
		
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void a(BlockGrowEvent e)
	{
		Block b = e.getBlock();
		BlockState block = e.getNewState();
		
		if(isLand(b) && isLand(block.getLocation()) && owner != null)
		{
			e.setCancelled(false);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void a(BlockSpreadEvent e)
	{
		Block b = e.getSource();
		BlockState block = e.getNewState();
		
		
		if(isLand(b) && isLand(block.getLocation()) && owner != null)
		{
			e.setCancelled(false);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
	public void on(PlayerInteractEvent e)
	{
		Action a = e.getAction();
		if(a == Action.RIGHT_CLICK_BLOCK)
		{
			Player p = e.getPlayer();
			
			Block b = e.getClickedBlock();
			BlockFace bf = e.getBlockFace();
			
			Block iteract = b.getType().isInteractable() && !p.isSneaking() ? b : b.getRelative(bf);
			
			if(isLand(iteract))
			{
				if(isOwner(p))
				{
					e.setCancelled(false);
				}
			}
		}
		
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void a(HangingBreakByEntityEvent e)
	{
		Entity entity = e.getEntity();
		
		if(isLand(entity.getLocation()))
		{
			Entity remover = e.getRemover();
			Projectile projectile = null;
			Player p = null;
			
			if(remover instanceof Projectile)
			{
				projectile = (Projectile) remover;
				
				if(projectile.getShooter() instanceof Player)
				{
					p = (Player) projectile.getShooter();
				}
				
				Bukkit.broadcastMessage(projectile.getShooter() + "");
			}
			else if(remover instanceof Player)
			{
				p = (Player) remover;
			}
			
			if(p != null)
			{
				if(isOwner(p))
				{
					e.setCancelled(false);
				}
			}
			else if(projectile != null)
			{
				if(projectile.getShooter() instanceof Block)
				{
					if(isLand(((Block) projectile.getShooter())))
					{
						
					}
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void a(PlayerBucketFillEvent e)
	{
		Block b = e.getBlock();
		
		if(isLand(b))
		{
			Player p = e.getPlayer();
			e.setCancelled(!isOwner(p));
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void a(PlayerBucketEmptyEvent e)
	{
		Block b = e.getBlock();
		
		if(isLand(b))
		{
			Player p = e.getPlayer();
			e.setCancelled(!isOwner(p));
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
	public void on(BlockBreakEvent e)
	{
		Block b = e.getBlock();
		Player p = e.getPlayer();
		
		if(isLand(b))
		{
			e.setCancelled(!isOwner(p));
		}
	}
	
	/**
	 * @param e
	 */
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
	public void on(BlockPlaceEvent e)
	{
		Block b = e.getBlock();
		Player p = e.getPlayer();
		
		if(isLand(b))
		{
			e.setCancelled(!isOwner(p));
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void a(BlockFromToEvent e)
	{
		Block b = e.getToBlock();
		
		if(isLand(b) && owner != null)
		{
			e.setCancelled(false);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void a(BlockPhysicsEvent e)
	{
		Block b = e.getBlock();
		
		if(isLand(b) && owner != null)
		{
			e.setCancelled(false);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void a(BlockPistonExtendEvent e)
	{
		Block b = e.getBlock();
		
		if(isLand(b) && owner != null)
		{
			e.setCancelled(false);
			
			for(Block blocks : e.getBlocks())
			{
				blocks = blocks.getRelative(e.getDirection());
				
				if(!isLand(blocks))
				{
					e.setCancelled(true);
					return;
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void a(BlockPistonRetractEvent e)
	{
		Block b = e.getBlock();
		
		if(isLand(b))
		{
			e.setCancelled(false);
			
			for(Block blocks : e.getBlocks())
			{
				if(!isLand(blocks))
				{
					e.setCancelled(true);
					return;
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void a(BlockMultiPlaceEvent e)
	{
		Block b = e.getBlock();
		
		if(isLand(b))
		{
			Player p = e.getPlayer();
			
			if(isOwner(p))
			{
				e.setCancelled(false);
				
				for(BlockState bs : e.getReplacedBlockStates())
				{
					if(!isLand(bs.getLocation()))
					{
						e.setCancelled(true);
						return;
					}
				}
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
		
		Block middle = e.getMiddleBlock();
		
		if(isLand(middle))
		{
			e.setCancelled(false);
			
			for(Block b : e.blockList())
			{
				if(!isLand(b))
				{
					e.setCancelled(true);
					return;
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void a(CustomBlockFertilizeEvent ce)
	{
		BlockFertilizeEvent e = ce.getBlockFertilizeEvent();
		
		Block b = e.getBlock();
		
		if(isLand(b))
		{
			e.getBlocks().clear();
			
			for(BlockState blocks : ce.getOriginalBlocks())
			{
				if(isLand(blocks.getLocation()))
				{
					e.getBlocks().add(blocks);
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void a(CustomStructureGrowEvent ce)
	{
		StructureGrowEvent e = ce.getStructureGrowEvent();
		
		Location l = e.getLocation();
		
		if(isLand(l))
		{
			e.getBlocks().clear();
			
			for(BlockState blocks : ce.getOriginalBlocks())
			{
				if(isLand(blocks.getLocation()))
				{
					e.getBlocks().add(blocks);
				}
			}
		}
	}
}