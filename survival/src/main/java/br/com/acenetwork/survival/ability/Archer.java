package br.com.acenetwork.survival.ability;

import java.text.DecimalFormat;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import br.com.acenetwork.commons.player.CommonPlayer;
import br.com.acenetwork.commons.player.craft.CraftCommonPlayer;
import br.com.acenetwork.survival.player.SurvivalPlayer;
import net.md_5.bungee.api.ChatColor;

public class Archer implements Listener// extends Ability
{
//	public Archer(SurvivalPlayer sp)
//	{
//		super(sp);
//	}
//	
	@EventHandler
	public void a(PlayerInteractEvent e)
	{
		Player p = e.getPlayer();
		CommonPlayer cp = CraftCommonPlayer.get(p);
//		
//		if(p != sp.getPlayer())
//		{
//			return;
//		}
		
		if(!cp.hasPermission("raid.kit.archer"))
		{
			return;
		}
		
		ItemStack item = e.getItem();
		Action action = e.getAction();
		
		if(item == null || item.getType() != Material.BOW || !action.name().contains("LEFT"))
		{
			return;
		}
		
		boolean infinity = p.getGameMode() == GameMode.CREATIVE || item.hasItemMeta() && item.getItemMeta().hasEnchant(Enchantment.ARROW_INFINITE);
		boolean flame = item.hasItemMeta() && item.getItemMeta().hasEnchant(Enchantment.ARROW_FIRE);
		
		if(p.getGameMode() != GameMode.CREATIVE && !p.getInventory().contains(Material.ARROW, infinity ? 1 : 11))
		{
			return;
		}
		
		if(!infinity)
		{
			int remove = 11;
			
			for(ItemStack content : p.getInventory())
			{
				if(content != null && content.getType() == Material.ARROW)
				{
					int removed = Math.min(remove, content.getAmount());
					remove -= removed;
					content.setAmount(content.getAmount() - removed);
				}
			}
		}
		
		
		
		DecimalFormat df = new DecimalFormat("#.##");
		
//			pitch += Math.toRadians(angulation * i * -Math.cos(pitch));
//		double newPitch = pitch + Math.toRadians(angulation * Math.abs(i) * Math.abs(Math.cos(pitch)));
//			ChatColor color;
//			
//			if(i < 0)
//			{
//				color = ChatColor.RED;
//			}
//			else if(i > 0)
//			{
//				color = ChatColor.GREEN;
//			}
//			else
//			{
//				color = ChatColor.WHITE;
//			}
//			
//			Bukkit.broadcastMessage("" + color + i + " " + df.format(Math.toDegrees(yaw)) + " " + df.format(Math.toDegrees(pitch)));
		
		final int r = 5;
		final double angulation = 6.0D;
		
		for(int i = -r; i <= r; i++)
		{
			double pitch = Math.toRadians(90.0F + p.getLocation().getPitch());
			double yaw = Math.toRadians((90.0F + p.getLocation().getYaw() + angulation * i));
			
//			double nerf = pitch + Math.toRadians(i * angulation * Math.abs(Math.cos(pitch)));
//			
			double x = Math.sin(pitch) * Math.cos(yaw);
			double y = Math.cos(pitch);
			double z = Math.sin(pitch) * Math.sin(yaw);
//					* Math.sin(yaw);
			
			
			Arrow arrow = p.launchProjectile(Arrow.class, new Vector(x, y, z));
			arrow.setCritical(true);
//			arrow.set
//			arrow.setVisualFire(flame);
			arrow.setGravity(false);
		}
	}
}