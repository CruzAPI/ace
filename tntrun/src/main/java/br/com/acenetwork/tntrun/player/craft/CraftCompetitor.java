package br.com.acenetwork.tntrun.player.craft;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import br.com.acenetwork.commons.player.craft.CraftCommonPlayer;
import br.com.acenetwork.tntrun.Main;
import br.com.acenetwork.tntrun.constant.State;
import br.com.acenetwork.tntrun.event.CompetitorJoinEvent;
import br.com.acenetwork.tntrun.event.CompetitorLeftEvent;
import br.com.acenetwork.tntrun.event.PlayerCountChangeEvent;
import br.com.acenetwork.tntrun.player.Competitor;

public class CraftCompetitor extends CraftCommonPlayer implements Competitor
{
	public CraftCompetitor(Player p)
	{
		super(p);
		
		Bukkit.getPluginManager().callEvent(new PlayerCountChangeEvent(CraftCommonPlayer.getAll(Competitor.class).size()));
		
		if(Main.getState() == State.STARTING)
		{
			Bukkit.getPluginManager().callEvent(new CompetitorJoinEvent(this));
		}
	}
	
	@Override
	public boolean delete()
	{
		boolean delete = super.delete();
		
		if(Main.getState() == State.STARTING)
		{
			Bukkit.getPluginManager().callEvent(new CompetitorLeftEvent(this));
		}
		else
		{
			Main.eliminate(this);
		}
		
		Bukkit.getPluginManager().callEvent(new PlayerCountChangeEvent(CraftCommonPlayer.getAll(Competitor.class).size()));
		return delete;
	}
	
	@Override
	public void reset()
	{
		super.reset();
		setInvis(false);
		p.setGameMode(GameMode.ADVENTURE);
	}
}