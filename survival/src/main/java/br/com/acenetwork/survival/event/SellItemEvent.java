package br.com.acenetwork.survival.event;

import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;

public class SellItemEvent extends ItemEvent 
{
	private static final HandlerList HANDLER = new HandlerList();
	
	public SellItemEvent(CommandSender sender, String key, int amount, double oldLiquidity, double newLiquidity, int oldMarketCap, int newMarketCap)
	{
		super(sender, key, amount, oldLiquidity, newLiquidity, oldMarketCap, newMarketCap);
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