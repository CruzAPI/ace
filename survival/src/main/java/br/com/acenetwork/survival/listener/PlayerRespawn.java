package br.com.acenetwork.survival.listener;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class PlayerRespawn implements Listener
{
	@EventHandler
	public void on(PlayerRespawnEvent e)
	{
		Player p = e.getPlayer();
		
		Location bedSpawnLocation = p.getBedSpawnLocation();
		
		if(bedSpawnLocation != null)
		{
			e.setRespawnLocation(bedSpawnLocation);
			return;
		}

		Random r = new Random();

		World w = Bukkit.getWorld("world");
		int x = r.nextInt(1000) - 500;
		int z = r.nextInt(1000) - 500;
		int y = w.getHighestBlockYAt(x, z);
		
		e.setRespawnLocation(new Location(w, x + 0.5D, y + 3.0D, z + 0.5d));
	}	
}