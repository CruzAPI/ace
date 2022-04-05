package br.com.acenetwork.tntrun.listener;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import br.com.acenetwork.commons.player.CommonPlayer;
import br.com.acenetwork.commons.player.craft.CraftCommonPlayer;
import br.com.acenetwork.tntrun.Main;
import br.com.acenetwork.tntrun.constant.State;
import br.com.acenetwork.tntrun.event.CompetitorJoinEvent;
import br.com.acenetwork.tntrun.event.CompetitorLeftEvent;
import br.com.acenetwork.tntrun.event.StateChangeEvent;
import br.com.acenetwork.tntrun.event.TimerChangeEvent;
import br.com.acenetwork.tntrun.player.Competitor;

public class TimerChange implements Listener
{
	@EventHandler
	public void on(CompetitorLeftEvent e)
	{
		Competitor c = e.getCompetitor();
		Player p = c.getPlayer();

		String arg = "(" + CraftCommonPlayer.getAll(Competitor.class).size() + "/" + Main.getMaxPlayers() + ")";
		
		CraftCommonPlayer.SET.stream().forEach(x -> x.sendMessage("tntrun.player-left-the-game", 
			p.getDisplayName(), arg));
	}
	
	@EventHandler
	public void on(CompetitorJoinEvent e)
	{
		Competitor c = e.getCompetitor();
		Player p = c.getPlayer();
		
		String arg = "(" + CraftCommonPlayer.getAll(Competitor.class).size() + "/" + Main.getMaxPlayers() + ")";
		
		CraftCommonPlayer.SET.stream().forEach(x -> x.sendMessage("tntrun.player-joined-the-game", 
			p.getDisplayName(), arg));
	}
	
	@EventHandler
	public void on(StateChangeEvent e)
	{
		if(e.getState() == State.STARTED)
		{
			CraftCommonPlayer.SET.stream().forEach(x -> x.sendMessage("tntrun.game-started"));
		}
	}
	
	@EventHandler
	public void on(TimerChangeEvent e)
	{
		if(Main.getState() == State.STARTING && CraftCommonPlayer.getAll(Competitor.class).size() >= Main.getMinPlayers())
		{
			int seconds = e.getSeconds();
			
			if(seconds > 0 && seconds % 60 == 0)
			{
				for(CommonPlayer cp : CraftCommonPlayer.SET)
				{
					Player p = cp.getPlayer();
					cp.sendMessage("tntrun.game-starting-in-minutes", seconds / 60);
					p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
				}
			}
			else if(seconds == 30 || seconds == 15 || seconds == 10 || (seconds <= 5 && seconds > 0))
			{
				for(CommonPlayer cp : CraftCommonPlayer.SET)
				{
					Player p = cp.getPlayer();
					cp.sendMessage("tntrun.game-starting-in-seconds", seconds);
					p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
				}
			}
		}
	}
}