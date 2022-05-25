package br.com.acenetwork.commons.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SocketEvent extends Event
{
	private static final HandlerList HANDLER = new HandlerList();
	
	private final String[] args;
	
	public SocketEvent(String[] args)
	{
		super(true);
		
		this.args = args;
	}
	
	public String[] getArgs()
	{
		return args;
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