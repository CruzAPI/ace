package br.com.acenetwork.commons.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

import br.com.acenetwork.commons.player.CommonPlayer;
import br.com.acenetwork.commons.player.craft.CraftCommonPlayer;

public class InventoryClose implements Listener
{
	@EventHandler
	public void on(InventoryCloseEvent e)
	{
		CommonPlayer cp = CraftCommonPlayer.get((Player) e.getPlayer());
		
		if(cp != null)
		{			
			cp.setGUI(null);
		}
	}
}