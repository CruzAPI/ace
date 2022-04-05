package br.com.acenetwork.lobby;

import org.bukkit.event.EventHandler;

import br.com.acenetwork.commons.CommonsScoreboard;
import br.com.acenetwork.commons.event.BungeePlayerCountEvent;
import br.com.acenetwork.commons.manager.Message;
import br.com.acenetwork.commons.player.CommonPlayer;

public class LobbyScoreboard extends CommonsScoreboard
{
	private int playerCount;
	
	public LobbyScoreboard(CommonPlayer cp)
	{
		super(cp);
		
		objective.setDisplayName("§b§lAce Network");
		setScore(6);
		setScore(5, getPrefix(5), getSuffix(5));
		setScore(4);
		setScore(3, getPrefix(3), getSuffix(3));
		setScore(2);
		setScore(1, "acenetwork", ".com.br");
	}
	
	private String getPrefix(int score)
	{
		if(score == 5)
		{
			return Message.getMessage(locale, "lobby.scoreboard.score5.prefix");
		}
		
		if(score == 3)
		{
			return Message.getMessage(locale, "lobby.scoreboard.score3.prefix");
		}
		
		return null;
	}
	
	private String getSuffix(int score)
	{
		if(score == 5)
		{
			return Message.getMessage(locale, "lobby.scoreboard.score5.suffix", playerCount);
		}
		
		if(score == 3)
		{
			return Message.getMessage(locale, "lobby.scoreboard.score3.suffix");
		}
		
		return null;
	}
	
	@EventHandler
	public void on(BungeePlayerCountEvent e)
	{
		playerCount = e.getCount();
	}
}