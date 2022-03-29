package br.com.acenetwork.commons.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import br.com.acenetwork.commons.player.CommonPlayer;
import br.com.acenetwork.commons.player.craft.CraftCommonPlayer;

public class PlayerQuit implements Listener
{
	@EventHandler(priority = EventPriority.HIGHEST)
	public void on(PlayerQuitEvent e)
	{
		Player p = e.getPlayer();
		CommonPlayer cp = CraftCommonPlayer.get(p);

		cp.delete();

		if(cp.isInvis())
		{
			e.setQuitMessage(null);
		}
	}
}