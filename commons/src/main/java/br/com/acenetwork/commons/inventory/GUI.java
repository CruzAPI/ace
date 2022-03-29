package br.com.acenetwork.commons.inventory;

import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;

import br.com.acenetwork.commons.player.CommonPlayer;

public abstract class GUI implements Listener
{
	protected final CommonPlayer cp;
	protected final Inventory inv;
		
	public GUI(CommonPlayer cp, Inventory inv)
	{
		this.cp = cp;
		this.inv = inv;
		
		cp.getPlayer().openInventory(inv);
		cp.setGUI(this);
	}
}