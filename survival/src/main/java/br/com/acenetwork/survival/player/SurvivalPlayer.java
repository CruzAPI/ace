package br.com.acenetwork.survival.player;

import br.com.acenetwork.commons.player.CommonPlayer;

public interface SurvivalPlayer extends CommonPlayer
{
	boolean hasSpawnProtection();
	void setSpawnProtection(boolean value);
	void channelSpawn();
	boolean cancelChannelSpawn(boolean force);
}