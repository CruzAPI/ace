package br.com.acenetwork.commons;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLocaleChangeEvent;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import br.com.acenetwork.commons.player.CommonPlayer;

public abstract class CommonsScoreboard implements Listener
{
	protected final CommonPlayer cp;
	protected final Scoreboard scoreboard;
	protected final Objective objective;
	
	protected String locale;
	
	public CommonsScoreboard(CommonPlayer cp)
	{
		Player p = cp.getPlayer();
		locale = p.getLocale();

		this.cp = cp;
		this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		this.objective = this.scoreboard.registerNewObjective(p.getName(), "dummy", p.getName());
		
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		
		Bukkit.getPluginManager().registerEvents(this, Commons.getPlugin());
	}
	
	protected void setScore(int score)
	{
		setScore(score, "", "");
	}
	
	protected void setScore(int score, String prefix, String suffix)
	{
		String entry = "§";
		
		switch(score)
		{
		case 11:
			entry += "a";
			break;
		case 12:
			entry += "b";
			break;
		case 13:
			entry += "c";
			break;
		case 14:
			entry += "d";
			break;
		case 15:
			entry += "e";
			break;
		case 16:
			entry += "f";
			break;
		default:
			entry += (score - 1);
			break;
		}
		
		setScore(score, prefix, entry, suffix);
	}
	
	protected void setScore(int score, String prefix, String entry, String suffix)
	{
		Team t = scoreboard.registerNewTeam("t" + score);
		
		t.setPrefix(prefix);
		t.addEntry(entry);
		t.setSuffix(suffix);
		
		objective.getScore(entry).setScore(score);
	}
	
	protected void updateScore(int score, String prefix, String suffix)
	{
		Team t = scoreboard.getTeam("t" + score);
		
		if(prefix != null)
		{
			t.setPrefix(prefix);
		}
		
		if(suffix != null)
		{
			t.setSuffix(suffix);
		}
	}
	
	public Scoreboard getScoreboard()
	{
		return scoreboard;
	}
	
	@EventHandler
	public void a(PlayerLocaleChangeEvent e)
	{
		if(e.getPlayer() != cp.getPlayer())
		{
			return;
		}
		
		locale = e.getLocale();
	}
}