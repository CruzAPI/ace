package br.com.acenetwork.survival.player.craft;

import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import br.com.acenetwork.commons.player.CommonPlayer;
import br.com.acenetwork.commons.player.craft.CraftCommonPlayer;
import br.com.acenetwork.survival.player.SurvivalPlayer;

public class CraftSurvivalPlayer extends CraftCommonPlayer implements SurvivalPlayer
{
	public CraftSurvivalPlayer(Player p)
	{
		super(p);
	}
	
	@Override
	public void reset()
	{
		setInvis(false);
		p.setGameMode(GameMode.SURVIVAL);
	}
	
	@EventHandler
	public void on1(EntityDamageByEntityEvent e)
	{
		if(e.getEntity() != p)
		{
			return;
		}
		
		Entity damager = e.getDamager();
		
		CommonPlayer ct;
		
		if(damager instanceof Player)
		{
			ct = CraftCommonPlayer.get((Player) damager);
		}
		else if(damager.hasMetadata("CommonPlayer"))
		{
			ct = (CommonPlayer) damager.getMetadata("CommonPlayer").get(0).value();
		}
		else
		{
			return;
		}
		
		if(getClan() != null && getClan().equals(ct.getClan()))
		{
			e.setCancelled(true);
		}
	}
}