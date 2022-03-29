package br.com.acenetwork.survival.manager;

public class GlobalScoreboard
{
	// public static final Map<Player, Double> MAP = new HashMap<>();
	
	// public static void update()
	// {
	// 	for(Player all : Bukkit.getOnlinePlayers())
	// 	{
	// 		if(((CraftPlayer) all).getHandle().b.isDisconnected())
	// 		{
	// 			continue;
	// 		}

	// 		Scoreboard scoreboard = all.getScoreboard() == null ? 
	// 			Bukkit.getScoreboardManager().getNewScoreboard() : all.getScoreboard();
				
	// 		Objective objective = scoreboard.getObjective("bounty") == null ? 
	// 		scoreboard.registerNewObjective("bounty", "dummy", "bounty") : scoreboard.getObjective("bounty");
			
	// 		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
	// 		objective.setDisplayName("§b§lTOP DAILY SHARDS");

	// 		Map<Player, Double> sortedMap = Util.sortByValue(MAP);

	// 		int i = 0;
			
	// 		Map<Player, Double> top10 = new LinkedHashMap<>();
			
	// 		for(Entry<Player, Double> entry : sortedMap.entrySet())
	// 		{
	// 			if(i >= 10)
	// 			{
	// 				break;
	// 			}

	// 			Player key = entry.getKey();
	// 			double value = entry.getValue();
				
	// 			top10.put(key, value);
				
	// 			i++;
	// 		}

	// 		for(String entry : scoreboard.getEntries())
	// 		{
	// 			if(!top10.keySet().stream().filter(x -> x.getName().equals(entry)).findAny().isPresent())
	// 			{
	// 				scoreboard.resetScores(entry);
	// 			}
	// 		}

	// 		for(Entry<Player, Double> entry : top10.entrySet())
	// 		{
	// 			Player key = entry.getKey();
	// 			double value = entry.getValue();

	// 			objective.getScore(key.getName()).setScore((int) value);
	// 		}
	// 	}
	// }
}