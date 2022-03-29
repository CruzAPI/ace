package br.com.acenetwork.tntrun.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import br.com.acenetwork.tntrun.Main;

public class PlayerJoin implements Listener
{
	@EventHandler
	public void on(PlayerJoinEvent e)
	{
		Player p = e.getPlayer();
		p.teleport(Main.getSpawnLocation());
	}
}