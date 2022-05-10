package br.com.acenetwork.survival.event;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import br.com.acenetwork.survival.executor.Track;
import br.com.acenetwork.survival.executor.Track.TrackType;

public class TrackEvent extends PlayerEvent implements Cancellable
{
	private static final HandlerList HANDLER = new HandlerList();
	
	private final TrackType type;
	private final Block middle;
	private boolean cancel;
	private final List<Block> blockList;
	
	public TrackEvent(Player p, TrackType type, Block middle, List<Block> blockList)
	{
		super(p);
		this.type = type;
		this.middle = middle;
		this.blockList = blockList;
	}
	
	public List<Block> blockList()
	{
		return blockList;
	}
	
	public Block getMiddleBlock()
	{
		return middle;
	}
	
	public TrackType getTrackType()
	{
		return type;
	}
	
	public static HandlerList getHandlerList()
	{
		return HANDLER;
	}
	
	@Override
	public HandlerList getHandlers()
	{
		return HANDLER;
	}

	@Override
	public boolean isCancelled()
	{
		return cancel;
	}

	@Override
	public void setCancelled(boolean arg0)
	{
		cancel = arg0;
	}
}