package br.com.acenetwork.tntrun.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import br.com.acenetwork.tntrun.constant.State;

public class StateChangeEvent extends Event
{
	private static final HandlerList HANDLER = new HandlerList();
	
	private final State state;
	
	public StateChangeEvent(State state)
	{
		this.state = state;
	}
	
	public State getState()
	{
		return state;
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