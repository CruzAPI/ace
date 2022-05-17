package br.com.acenetwork.survival.event;

import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;

public abstract class ItemEvent extends Event
{
	final CommandSender sender;
	final String key;
	final int amount;
	final double oldLiquidity;
	final double newLiquidity;
	final int oldMarketCap;
	final int newMarketCap;
	
	public ItemEvent(CommandSender sender, String key, int amount, double oldLiquidity, double newLiquidity, int oldMarketCap, int newMarketCap)
	{
		this.sender = sender;
		this.key = key;
		this.amount = amount;
		this.oldLiquidity = oldLiquidity;
		this.newLiquidity = newLiquidity;
		this.oldMarketCap = oldMarketCap;
		this.newMarketCap = newMarketCap;
	}
	
	public CommandSender getSender()
	{
		return sender;
	}
	
	public String getKey()
	{
		return key;
	}
	
	public int getAmount()
	{
		return amount;
	}
	
	public double getOldLiquidity()
	{
		return oldLiquidity;
	}
	
	public double getNewLiquidity()
	{
		return newLiquidity;
	}
	
	public int getOldMarketCap()
	{
		return oldMarketCap;
	}
	
	public int getNewMarketCap()
	{
		return newMarketCap;
	}
}
