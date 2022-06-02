package br.com.acenetwork.survival.listener;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityCategory;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class EntityDamageByEntity implements Listener
{
	@EventHandler
	public void a(EntityDamageByEntityEvent e)
	{
		if(e.getDamager() instanceof Player)
		{
			Player p = (Player) e.getDamager();
			
			ItemStack item = p.getInventory().getItemInMainHand();
			
			switch(item.getType())
			{
			case NETHERITE_SWORD:
				e.setDamage(8.0D);
				break;
			case DIAMOND_SWORD:
				e.setDamage(7.0D);
				break;
			case IRON_SWORD:
				e.setDamage(6.0D);
				break;
			case STONE_SWORD:
				e.setDamage(5.0D);
				break;
			case GOLDEN_SWORD:
				e.setDamage(4.0D);
				break;
			case WOODEN_SWORD:
				e.setDamage(4.0D);
				break;
				
			case NETHERITE_PICKAXE:
				e.setDamage(6.0D);
				break;
			case DIAMOND_PICKAXE:
				e.setDamage(5.0D);
				break;
			case IRON_PICKAXE:
				e.setDamage(4.0D);
				break;
			case STONE_PICKAXE:
				e.setDamage(3.0D);
				break;
			case GOLDEN_PICKAXE:
				e.setDamage(2.0D);
				break;
			case WOODEN_PICKAXE:
				e.setDamage(2.0D);
				break;
				
			case NETHERITE_SHOVEL:
				e.setDamage(5.5D);
				break;
			case DIAMOND_SHOVEL:
				e.setDamage(4.5D);
				break;
			case IRON_SHOVEL:
				e.setDamage(3.5D);
				break;
			case STONE_SHOVEL:
				e.setDamage(2.5D);
				break;
			case GOLDEN_SHOVEL:
				e.setDamage(1.5D);
				break;
			case WOODEN_SHOVEL:
				e.setDamage(1.5D);
				break;
			
			case NETHERITE_AXE:
				e.setDamage(7.0D);
				break;
			case DIAMOND_AXE:
				e.setDamage(6.0D);
				break;
			case IRON_AXE:
				e.setDamage(5.0D);
				break;
			case STONE_AXE:
				e.setDamage(4.0D);
				break;
			case GOLDEN_AXE:
				e.setDamage(3.0D);
				break;
			case WOODEN_AXE:
				e.setDamage(3.0D);
				break;
				
			case TRIDENT:
				e.setDamage(9.0D);
				break;
				
			default:
				e.setDamage(1.0D);
				break;
			}
			
			PotionEffect effect = p.getPotionEffect(PotionEffectType.INCREASE_DAMAGE);
			
			if(effect != null)
			{
				e.setDamage(e.getDamage() + (effect.getAmplifier() + 1) * 3.0D);
			}
			
			if(e.isCritical())
			{
				e.setDamage(e.getDamage() * 1.25D);
			}
			
			if(item.hasItemMeta())
			{
				if(item.getItemMeta().hasEnchant(Enchantment.DAMAGE_ALL))
				{
					e.setDamage(e.getDamage() + 1.0D + (item.getItemMeta().getEnchantLevel(Enchantment.DAMAGE_ALL) - 1) * 0.5D);
				}
				
				if(e.getEntity() instanceof LivingEntity)
				{
					LivingEntity le = (LivingEntity) e.getEntity();
					
					if(le.getCategory() == EntityCategory.ARTHROPOD)
					{
						e.setDamage(e.getDamage() + item.getItemMeta().getEnchantLevel(Enchantment.DAMAGE_ARTHROPODS) * 2.5D);
					}
					else if(le.getCategory() == EntityCategory.UNDEAD)
					{
						e.setDamage(e.getDamage() + item.getItemMeta().getEnchantLevel(Enchantment.DAMAGE_UNDEAD) * 2.5D);
					}
				}
			}
		}
	}
}