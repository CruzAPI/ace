package br.com.acenetwork.survival.player.craft;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Pose;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPoseChangeEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import br.com.acenetwork.commons.player.CommonPlayer;
import br.com.acenetwork.commons.player.craft.CraftCommonPlayer;
import br.com.acenetwork.survival.player.SurvivalPlayer;

public class CraftSurvivalPlayer extends CraftCommonPlayer implements SurvivalPlayer
{
	private boolean blindness;
	
	public CraftSurvivalPlayer(Player p)
	{
		super(p);
	}
	
	@Override
	public void reset()
	{
		setInvis(false);
		
		p.setCollidable(true);
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
	
	
	@EventHandler
	public void a(PlayerItemConsumeEvent e)
	{
		if(e.getPlayer() != p)
		{
			return;
		}
		
		if(blindness && e.getItem().getType() == Material.MILK_BUCKET)
		{
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void a(EntityPoseChangeEvent e)
	{
		if(e.getEntity() != p)
		{
			return;
		}
		
		if(blindness)
		{
			blindness = false;
			p.removePotionEffect(PotionEffectType.BLINDNESS);
			return;
		}
		
		if(e.getPose() == Pose.SWIMMING && p.getLocation().getBlock().getType() == Material.COMPOSTER)
		{
			blindness = true;
			p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 99999, 5));
		}
	}
}
