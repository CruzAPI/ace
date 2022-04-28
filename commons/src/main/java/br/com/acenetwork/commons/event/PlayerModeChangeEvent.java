package br.com.acenetwork.commons.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import br.com.acenetwork.commons.player.CommonPlayer;

public class PlayerModeChangeEvent extends Event
{
	private static final HandlerList HANDLER = new HandlerList();
	
	private final CommonPlayer cp;
	
	public PlayerModeChangeEvent(CommonPlayer cp)
	{
		super();
		this.cp = cp;
	}
	
	public CommonPlayer getCommonPlayer()
	{
		return cp;
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