package br.com.acenetwork.tntrun;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerLocaleChangeEvent;

import br.com.acenetwork.commons.CommonsScoreboard;
import br.com.acenetwork.commons.manager.Message;
import br.com.acenetwork.commons.player.CommonPlayer;
import br.com.acenetwork.commons.player.craft.CraftCommonPlayer;
import br.com.acenetwork.tntrun.constant.State;
import br.com.acenetwork.tntrun.event.PlayerCountChangeEvent;
import br.com.acenetwork.tntrun.event.StateChangeEvent;
import br.com.acenetwork.tntrun.event.TimerChangeEvent;
import br.com.acenetwork.tntrun.player.Competitor;

public class TNTRunScoreboard extends CommonsScoreboard
{
	public TNTRunScoreboard(CommonPlayer cp)
	{
		super(cp);
		
		objective.setDisplayName("§f§lTNT RUN");
		setScore(6);
		setScore(5, getPrefix(5), getSuffix(5));
		setScore(4);
		setScore(3, getPrefix(3), getSuffix(3));
		setScore(2);
		setScore(1, "acenetwork.", "com.br");
	}
	
	private String getPrefix(int score)
	{
		if(score == 5)
		{
			if(Main.getState() == State.STARTED)
			{
				return Message.getMessage(locale, "tntrun.scoreboard.score5.prefix2");
			}
			
			return Message.getMessage(locale, "tntrun.scoreboard.score5.prefix1");
		}
		
		if(score == 3)
		{
			if(Main.getState() == State.STARTED)
			{
				return Message.getMessage(locale, "tntrun.scoreboard.score3.prefix2");
			}
			
			return Message.getMessage(locale, "tntrun.scoreboard.score3.prefix1");
		}
		
		return null;
	}
	
	private String getSuffix(int score)
	{
		if(score == 5)
		{
			return " " + Main.getTimer();
		}
		
		if(score == 3)
		{
			int playerCount = CraftCommonPlayer.getAll(Competitor.class).size();
			
			if(Main.getState() == State.STARTED)
			{
				return Message.getMessage(locale, "tntrun.scoreboard.score3.suffix2", playerCount);
			}
			
			return Message.getMessage(locale, "tntrun.scoreboard.score3.suffix1", playerCount + "/" + Main.getMaxPlayers());
		}
		
		return null;
	}
	
	@EventHandler
	public void on(StateChangeEvent e)
	{
		updateScore(5, getPrefix(5), null);
		updateScore(3, getPrefix(3), getSuffix(3));
	}
	
	@EventHandler
	public void on(TimerChangeEvent e)
	{
		updateScore(5, null, getSuffix(5));
	}
	
	@EventHandler
	public void on(PlayerCountChangeEvent e)
	{
		updateScore(3, null, getSuffix(3));
	}
	
	@EventHandler
	public void on(PlayerLocaleChangeEvent e)
	{
		if(e.getPlayer() != cp.getPlayer())
		{
			return;
		}
		
		updateScore(5, getPrefix(5), null);
		updateScore(3, getPrefix(3), getSuffix(3));
	}
}
