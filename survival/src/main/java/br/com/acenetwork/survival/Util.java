package br.com.acenetwork.survival;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import br.com.acenetwork.survival.npc.CustomNPC;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;

public class Util
{
	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map)
	{
		List<Entry<K, V>> list = new ArrayList<>(map.entrySet());
		list.sort(Entry.comparingByValue());
		Collections.reverse(list);
		
		Map<K, V> result = new LinkedHashMap<>();

		for(Entry<K, V> entry : list)
		{
			result.put(entry.getKey(), entry.getValue());
    	}

		return result;
	}
	
	public static Collection<Player> getOnlinePlayersAndNPCs()
	{
		List<Player> list = new ArrayList<>(Bukkit.getOnlinePlayers());
		Iterator<NPC> iterator = CitizensAPI.getNPCRegistry().iterator();
		
		while(iterator.hasNext())
		{
			NPC npc = iterator.next();
			
			if(CustomNPC.get(npc) != null || !npc.isSpawned())
			{
				continue;
			}
			
			Player npcPlayer = (Player) npc.getEntity();
			list.add(npcPlayer);
		}
		
		return list;
	}
	
	public static boolean isSpawn(Block b)
	{
		return isSpawn(b.getLocation());
	}
	
	public static boolean isSpawn(Location l)
	{
		return l.getWorld().getName().equals("world") && l.getBlockX() <= 60 && l.getBlockX() >= -60 && l.getBlockZ() <= 60 && l.getBlockZ() >= -60;
	}
}