package br.com.acenetwork.survival.npc;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import net.citizensnpcs.api.npc.NPC;

public class RandomTpNPC extends CustomNPC
{
	public RandomTpNPC(NPC npc)
	{
		super(npc);
	}
	
	@EventHandler
	public void a(PlayerInteractEntityEvent e)
	{
		if(e.getRightClicked() != npc.getEntity())
		{
			return;
		}
		
		e.setCancelled(true);
		e.getPlayer().chat("/randomtp");
	}
	
	@EventHandler
	public void a(EntityDamageByEntityEvent e)
	{
		if(e.getEntity() != npc.getEntity())
		{
			return;
		}
		
		if(e.getDamager() instanceof Player)
		{
			((Player) e.getDamager()).chat("/randomtp");
		}
	}
}