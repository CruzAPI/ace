package br.com.acenetwork.lobby.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import br.com.acenetwork.commons.event.PlayerModeChangeEvent;
import br.com.acenetwork.commons.player.CommonPlayer;
import br.com.acenetwork.lobby.LobbyHotbar;

public class PlayerModeChange implements Listener
{
	@EventHandler
	public void on(PlayerModeChangeEvent e)
	{
		CommonPlayer cp = e.getCommonPlayer();
		cp.setCommonsHotbar(new LobbyHotbar(cp));
	}
}