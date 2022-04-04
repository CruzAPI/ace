package br.com.acenetwork.tntrun.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TimerChangeEvent extends Event
{
	private static final HandlerList HANDLER = new HandlerList();
	
	private final int seconds;
	
	public TimerChangeEvent(int seconds)
	{
		this.seconds = seconds;
	}
	
	public int getSeconds()
	{
		return seconds;
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