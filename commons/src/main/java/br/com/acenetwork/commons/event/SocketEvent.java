package br.com.acenetwork.commons.event;

import java.net.Socket;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SocketEvent extends Event
{
	private static final HandlerList HANDLER = new HandlerList();
	
	private final Socket s;
	
	public SocketEvent(Socket s)
	{
		super(true);
		
		this.s = s;
	}
	
	public Socket getSocket()
	{
		return s;
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