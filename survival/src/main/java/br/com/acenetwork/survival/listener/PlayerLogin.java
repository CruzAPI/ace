package br.com.acenetwork.survival.listener;

import java.util.HashSet;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

import net.citizensnpcs.api.CitizensAPI;

public class PlayerLogin implements Listener
{
	@EventHandler(priority = EventPriority.MONITOR)
	public void on(PlayerLoginEvent e)
	{
		Player p = e.getPlayer();
		
		if(e.getResult() == Result.ALLOWED)
		{
			for(Entry<Integer, UUID> entry : new HashSet<>(PlayerQuit.UUID_MAP.entrySet()))
			{
				if(p.getUniqueId().equals(entry.getValue()))
				{
					PlayerQuit.removeCombatLogger(CitizensAPI.getNPCRegistry().getById(entry.getKey()));
				}
			}
		}
	}
}