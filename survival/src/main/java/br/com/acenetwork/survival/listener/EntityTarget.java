package br.com.acenetwork.survival.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;

import br.com.acenetwork.commons.player.CommonPlayer;
import br.com.acenetwork.commons.player.craft.CraftCommonPlayer;
import br.com.acenetwork.survival.player.SurvivalPlayer;

public class EntityTarget implements Listener
{
	@EventHandler
	public void on(EntityTargetEvent e)
	{
		if(e.getTarget() instanceof Player)
		{
			Player p = (Player) e.getTarget();
			CommonPlayer cp = CraftCommonPlayer.get(p);
			
			if(cp instanceof SurvivalPlayer)
			{
				SurvivalPlayer sp = (SurvivalPlayer) cp;
				
				if(sp.hasSpawnProtection())
				{
					e.setCancelled(true);
					return;
				}
			}
		}
		
		e.setCancelled(false);
	}
}
