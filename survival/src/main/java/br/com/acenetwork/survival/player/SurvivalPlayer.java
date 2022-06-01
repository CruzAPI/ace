package br.com.acenetwork.survival.player;

import org.bukkit.Location;

import br.com.acenetwork.commons.player.CommonPlayer;
import br.com.acenetwork.survival.manager.ChannelCommand;

public interface SurvivalPlayer extends AbilityPlayer
{
	boolean hasSpawnProtection();
	void setSpawnProtection(boolean value);
	
	boolean cancelChannel(boolean force);
	void channel(ChannelCommand channel, Location destiny, Object... args);
}