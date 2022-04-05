package br.com.acenetwork.lobby.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import br.com.acenetwork.commons.event.PlayerModeEvent;
import br.com.acenetwork.lobby.player.craft.CraftLobbyPlayer;

public class PlayerMode implements Listener
{
	@EventHandler
	public void on(PlayerModeEvent e)
	{
		Player p = e.getPlayer();
		new CraftLobbyPlayer(p);
	}
}