package br.com.acenetwork.commons.player.craft;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerGameModeChangeEvent;

import br.com.acenetwork.commons.player.CommonWatcher;

public class CraftCommonWatcher extends CraftCommonPlayer implements CommonWatcher
{
	public CraftCommonWatcher(Player p)
	{
		super(p);
	}
	
	@Override
	public void reset()
	{
		super.reset();
		setInvis(true);
		
		p.setGameMode(GameMode.SPECTATOR);
		p.setAllowFlight(true);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void on(PlayerGameModeChangeEvent e)
	{
		if(e.getPlayer() != p)
		{
			return;
		}
		
		if(e.getNewGameMode() != GameMode.SPECTATOR)
		{
			e.setCancelled(true);
		}
	}
}