package br.com.acenetwork.commons.player.craft;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

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
		getPlayer().setGameMode(GameMode.SPECTATOR);
		getPlayer().setAllowFlight(true);
	}
}