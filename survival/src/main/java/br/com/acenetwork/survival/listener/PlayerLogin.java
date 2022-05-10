package br.com.acenetwork.survival.listener;

import java.util.Iterator;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;

public class PlayerLogin implements Listener
{
	@EventHandler(priority = EventPriority.MONITOR)
	public void on(PlayerLoginEvent e)
	{
		Player p = e.getPlayer();
		
		if(e.getResult() == Result.ALLOWED)
		{
			Iterator<NPC> iterator = CitizensAPI.getNPCRegistry().iterator();
			while(iterator.hasNext())
			{
				NPC npc = iterator.next();
				
				if(p.getUniqueId().equals(npc.data().get("uuid")))
				{
					iterator.remove();
					PlayerQuit.removeCombatLogger(npc);
				}
			}
		}
	}
}