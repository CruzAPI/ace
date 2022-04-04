package br.com.acenetwork.tntrun.event;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import br.com.acenetwork.tntrun.player.Competitor;

public class CompetitorLeftEvent extends Event
{
	private static final HandlerList HANDLER = new HandlerList();
	
	private final Competitor competitor;
	
	public CompetitorLeftEvent(Competitor competitor)
	{
		this.competitor = competitor;
	}
	
	public Competitor getCompetitor()
	{
		return competitor;
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