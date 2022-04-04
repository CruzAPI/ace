package br.com.acenetwork.tntrun.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import br.com.acenetwork.commons.player.CommonPlayer;
import br.com.acenetwork.commons.player.craft.CraftCommonPlayer;
import br.com.acenetwork.tntrun.Main;
import br.com.acenetwork.tntrun.TNTRunScoreboard;

public class PlayerJoin implements Listener
{
	@EventHandler
	public void on(PlayerQuitEvent e)
	{
		e.setQuitMessage(null);
	}
	
	@EventHandler
	public void on(PlayerJoinEvent e)
	{
		Player p = e.getPlayer();
		CommonPlayer cp = CraftCommonPlayer.get(p);
		
		cp.setCommonsScoreboard(new TNTRunScoreboard(cp));
		p.teleport(Main.getSpawnLocation());
		e.setJoinMessage(null);
	}
}