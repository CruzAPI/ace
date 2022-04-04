package br.com.acenetwork.tntrun.event;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerCountChangeEvent extends Event
{
	private static final HandlerList HANDLER = new HandlerList();
	
	private final int count;
	
	public PlayerCountChangeEvent(int count)
	{
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