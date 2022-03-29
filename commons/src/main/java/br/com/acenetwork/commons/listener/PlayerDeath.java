package br.com.acenetwork.commons.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import br.com.acenetwork.commons.manager.Message;
import br.com.acenetwork.commons.player.CommonPlayer;
import br.com.acenetwork.commons.player.craft.CraftCommonPlayer;

public class PlayerDeath implements Listener
{
	@EventHandler(priority = EventPriority.MONITOR)
	public void on(PlayerDeathEvent e)
	{
		Player p = e.getEntity();
		
		if(!e.getKeepInventory() && e.getDrops().size() > 0)
		{
			p.sendMessage(Message.getMessage(p.getLocale(), "event.playerdeath.dropped-items-will-be-cleaned", 2));
		}
	}
}