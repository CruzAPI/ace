package br.com.acenetwork.commons.event;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.block.BlockState;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.world.StructureGrowEvent;

public class CustomStructureGrowEvent extends Event
{
	private static final HandlerList HANDLER = new HandlerList();
	private final List<BlockState> blocks;
	private final StructureGrowEvent e;
	
	public CustomStructureGrowEvent(StructureGrowEvent e)
	{
		this.e = e;
		blocks = new ArrayList<>(e.getBlocks());
	}
	
	public List<BlockState> getOriginalBlocks()
	{
		return new ArrayList<>(blocks);
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

	public StructureGrowEvent getStructureGrowEvent()
	{
		return e;
	}
}