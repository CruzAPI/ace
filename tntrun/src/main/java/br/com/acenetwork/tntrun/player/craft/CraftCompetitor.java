package br.com.acenetwork.tntrun.player.craft;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import br.com.acenetwork.commons.player.craft.CraftCommonPlayer;
import br.com.acenetwork.tntrun.Main;
import br.com.acenetwork.tntrun.constant.State;
import br.com.acenetwork.tntrun.player.Competitor;

public class CraftCompetitor extends CraftCommonPlayer implements Competitor
{
	public CraftCompetitor(Player p)
	{
		super(p);
	}
	
	@Override
	public void reset()
	{
		super.reset();
		setInvis(false);
		p.setGameMode(GameMode.ADVENTURE);
	}
}