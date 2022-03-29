package br.com.acenetwork.survival.listener;

import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.projectiles.ProjectileSource;

import br.com.acenetwork.commons.player.CommonPlayer;
import br.com.acenetwork.commons.player.craft.CraftCommonPlayer;
import br.com.acenetwork.survival.Main;

public class ProjectileLaunch implements Listener
{
	@EventHandler
	public void on(ProjectileLaunchEvent e)
	{
		Projectile entity = e.getEntity();
		ProjectileSource shooter = entity.getShooter();
		
		if(shooter instanceof Player)
		{
			Player p = (Player) shooter;
			CommonPlayer cp = CraftCommonPlayer.get(p);
			
			entity.setMetadata("CommonPlayer", new FixedMetadataValue(Main.getInstance(), cp));
		}
	}
}
