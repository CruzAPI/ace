package br.com.acenetwork.commons.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BungeePlayerCountEvent extends Event
{
	private static final HandlerList HANDLER = new HandlerList();
	
	private final int count;
	
	public BungeePlayerCountEvent(int count)
	{
		super();
		
		this.count = count;
	}
	
	public int getCount()
	{
		return count;
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
}