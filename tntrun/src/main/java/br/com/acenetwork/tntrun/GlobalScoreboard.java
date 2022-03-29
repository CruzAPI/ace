package br.com.acenetwork.tntrun;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import br.com.acenetwork.commons.player.craft.CraftCommonPlayer;
import br.com.acenetwork.tntrun.constant.State;
import br.com.acenetwork.tntrun.player.Competitor;

public class GlobalScoreboard
{
	public static void updateAll()
	{
		for(Player p : Bukkit.getOnlinePlayers())
		{
			Scoreboard scoreboard = p.getScoreboard();
			
			boolean set = false;
			Objective objective;
			
			if(scoreboard.getObjective("tntrun") == null)
			{
				set = true;
				objective = scoreboard.registerNewObjective("tntrun", "dummy", "tntrun");
			}
			else
			{
				objective = scoreboard.getObjective("tntrun");
			}
			
			Team t = scoreboard.getTeam("t0");
			t = t == null ? scoreboard.registerNewTeam("t0") : t;
			
			OfflinePlayer p0 = Bukkit.getOfflinePlayer("§f ");
			
			if(!t.hasPlayer(p0))
			{
				t.addPlayer(p0);
			}
			
			if(Main.getState() == State.STARTING)
			{
				t.setPrefix("§aStarting in:");
			}
			else
			{
				t.setPrefix("§cDuration:");
			}
			
			t.setSuffix("" + Main.getTimer());
			
			t = scoreboard.getTeam("t1");
			t = t == null ? scoreboard.registerNewTeam("t1") : t;
			
			OfflinePlayer p1 = Bukkit.getOfflinePlayer("§a§f ");
			
			if(!t.hasPlayer(p1))
			{
				t.addPlayer(p1);
			}
			
			if(Main.getState() == State.STARTING)
			{
				t.setPrefix("§aPlayer count:");
				t.setSuffix(CraftCommonPlayer.getAll(Competitor.class).size() + "/" + Main.getMaxPlayers());
			}
			else
			{
				t.setPrefix("§cPlayers alive:");
				t.setSuffix(CraftCommonPlayer.getAll(Competitor.class).size() + "");
			}
			
			t = scoreboard.getTeam("t2");
			t = t == null ? scoreboard.registerNewTeam("t2") : t;
			
			OfflinePlayer p2 = Bukkit.getOfflinePlayer("§b§f");
			
			if(!t.hasPlayer(p2))
			{
				t.addPlayer(p2);
			}
			
			t.setPrefix("acenetwork");
			t.setSuffix(".com.br");
			
			if(set)
			{
				objective.setDisplaySlot(DisplaySlot.SIDEBAR);
				objective.setDisplayName("§f§lTNT RUN");
				objective.getScore("§0 ").setScore(6);
				objective.getScore(p0).setScore(5);
				objective.getScore("§1 ").setScore(4);
				objective.getScore(p1).setScore(3);
				objective.getScore("§2 ").setScore(2);
				objective.getScore(p2).setScore(1);
			}
		}
	}
}