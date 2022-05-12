package br.com.acenetwork.survival.manager;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import br.com.acenetwork.commons.player.CommonPlayer;

public interface ChannelCommand
{
	default void run(CommonPlayer cp, Location destiny, Object... args)
	{
		Player p = cp.getPlayer();
		p.teleport(destiny);
	}
}
