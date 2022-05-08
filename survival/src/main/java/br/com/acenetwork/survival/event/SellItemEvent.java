package br.com.acenetwork.survival.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;

public class SellItemEvent extends PlayerEvent
{
	private static final HandlerList HANDLER = new HandlerList();
	
	private final ItemStack item;
	
	public SellItemEvent(Player p, ItemStack item)
	{
		super(p);
		
		this.item = item;
	}
	
	public ItemStack getItem()
	{
		return item;
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
