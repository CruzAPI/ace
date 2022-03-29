package br.com.acenetwork.tntrun.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import br.com.acenetwork.commons.event.PlayerModeEvent;
import br.com.acenetwork.commons.player.craft.CraftCommonWatcher;
import br.com.acenetwork.tntrun.Main;
import br.com.acenetwork.tntrun.constant.State;
import br.com.acenetwork.tntrun.player.craft.CraftCompetitor;

public class PlayerMode implements Listener
{
	@EventHandler
	public void on(PlayerModeEvent e)
	{
		if(Main.getState() == State.STARTED)
		{
			new CraftCommonWatcher(e.getPlayer());
		}
		else
		{
			new CraftCompetitor(e.getPlayer());
		}
	}
}
