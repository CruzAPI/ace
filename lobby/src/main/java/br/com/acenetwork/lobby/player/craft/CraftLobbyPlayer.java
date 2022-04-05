package br.com.acenetwork.lobby.player.craft;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import br.com.acenetwork.commons.player.craft.CraftCommonPlayer;
import br.com.acenetwork.lobby.player.LobbyPlayer;

public class CraftLobbyPlayer extends CraftCommonPlayer implements LobbyPlayer
{
	public CraftLobbyPlayer(Player p)
	{
		super(p);
	}
	
	@Override
	public void reset()
	{
		super.reset();
		p.setGameMode(GameMode.ADVENTURE);
	}
}