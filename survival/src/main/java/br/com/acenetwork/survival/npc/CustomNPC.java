package br.com.acenetwork.survival.npc;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import br.com.acenetwork.survival.Main;
import net.citizensnpcs.api.npc.NPC;

public abstract class CustomNPC implements Listener
{
	public static final Set<CustomNPC> SET = new HashSet<>();
	
	protected final NPC npc;
	
	public CustomNPC(NPC npc)
	{
		this.npc = npc;
		
		CustomNPC previous = get(npc);
		
		SET.add(this);
		
		if(previous != null)	
		{
			previous.delete();
		}
		
		Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
	}
	
	public static CustomNPC get(NPC npc)
	{
		return SET.stream().filter(x -> x.npc == npc).findAny().orElse(null);
	}
	
	public boolean delete()
	{
		HandlerList.unregisterAll(this);
		return SET.remove(this);
	}
}