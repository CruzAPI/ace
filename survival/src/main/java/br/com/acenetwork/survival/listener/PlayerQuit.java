package br.com.acenetwork.survival.listener;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;

import br.com.acenetwork.commons.player.CommonPlayer;
import br.com.acenetwork.commons.player.craft.CraftCommonPlayer;
import br.com.acenetwork.survival.Main;
import br.com.acenetwork.survival.manager.Config;
import br.com.acenetwork.survival.manager.Config.Type;
import br.com.acenetwork.survival.player.SurvivalPlayer;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.SpawnReason;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.api.trait.trait.Inventory;

public class PlayerQuit implements Listener
{
	public static final Map<Integer, Integer> TASK_MAP = new HashMap<>();
	public static final Map<Integer, String> UUID_MAP = new HashMap<>();
	
	@EventHandler
	public void on(PlayerQuitEvent e)
	{
		Player p = e.getPlayer();
		CommonPlayer cp = CraftCommonPlayer.get(p);
		
		if(p.isDead())
		{
			return;
		}

		if(cp instanceof SurvivalPlayer)
		{
			NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, p.getName());
			
			if(npc.spawn(p.getLocation(), SpawnReason.PLUGIN))
			{
				Player player = (Player) npc.getEntity();
				
				Equipment equipment = npc.getTrait(Equipment.class);
				
				equipment.set(Equipment.EquipmentSlot.HELMET, p.getInventory().getItem(EquipmentSlot.HEAD));
				equipment.set(Equipment.EquipmentSlot.CHESTPLATE, p.getInventory().getItem(EquipmentSlot.CHEST));
				equipment.set(Equipment.EquipmentSlot.LEGGINGS, p.getInventory().getItem(EquipmentSlot.LEGS));
				equipment.set(Equipment.EquipmentSlot.BOOTS, p.getInventory().getItem(EquipmentSlot.FEET));
				equipment.set(Equipment.EquipmentSlot.HAND, p.getInventory().getItem(EquipmentSlot.HAND));
				equipment.set(Equipment.EquipmentSlot.OFF_HAND, p.getInventory().getItem(EquipmentSlot.OFF_HAND));
				
				Inventory inventory = npc.getTrait(Inventory.class);
				
				inventory.setContents(p.getInventory().getContents());
					
				player.setExp(p.getExp());
				player.setLevel(p.getLevel());
				player.setFireTicks(p.getFireTicks());
				player.setFallDistance(p.getFallDistance());
				player.addPotionEffects(p.getActivePotionEffects());
				player.setFoodLevel(p.getFoodLevel());
				player.setMaximumNoDamageTicks(p.getMaximumNoDamageTicks());
				player.setNoDamageTicks(p.getNoDamageTicks());
				player.setMaxHealth(p.getMaxHealth());
				player.setHealth(p.getHealth());
				player.setMaximumAir(p.getMaximumAir());
				player.setRemainingAir(p.getRemainingAir());
				player.setFreezeTicks(p.getFreezeTicks());
				player.setArrowsInBody(p.getArrowsInBody());
				player.setSaturation(p.getSaturation());
				player.setSaturatedRegenRate(p.getSaturatedRegenRate());
				player.setUnsaturatedRegenRate(p.getUnsaturatedRegenRate());
				player.setStarvationRate(p.getStarvationRate());
				player.setVelocity(p.getVelocity());
				
				npc.setProtected(false);
				
				int id = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), new Runnable()
				{
					int seconds = 60;
					
					@Override
					public void run()
					{						
						if(seconds == 0)
						{
							removeCombatLogger(npc);
						}
						
						seconds--;
					}
				}, 0L, 20L);
				
				
				UUID_MAP.put(npc.getId(), cp.getUUID());
				TASK_MAP.put(npc.getId(), id);
			}
		}
	}
	
	public static void removeCombatLogger(NPC npc)
	{
		String npcUUID = UUID_MAP.remove(npc.getId());
		Integer id = TASK_MAP.remove(npc.getId());
		
		if(id != 0)
		{
			Bukkit.getScheduler().cancelTask(id);
		}
				
		File file = Config.getFile(Type.COMBATLOG, true, npcUUID);
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		
		Inventory inventory = npc.getTrait(Inventory.class);
		
		if(npc.isSpawned())
		{
			Player p = (Player) npc.getEntity();
			
			config.set("inventory", inventory.getContents());
			config.set("location", p.getLocation());
			config.set("exp", p.getExp());
			config.set("level", p.getLevel());
			config.set("fire-ticks", p.getFireTicks());
			config.set("fall-distance", p.getFallDistance());
			config.set("potion-effects", p.getActivePotionEffects());
			config.set("food-level", p.getFoodLevel());
			config.set("max-no-damage-ticks", p.getMaximumNoDamageTicks());
			config.set("no-damage-ticks", p.getNoDamageTicks());
			config.set("max-health", p.getMaxHealth());
			config.set("health", p.getHealth());
			config.set("max-air", p.getMaximumAir());
			config.set("remaining-air", p.getRemainingAir());
			config.set("freeze-ticks", p.getFreezeTicks());
			config.set("arrows-in-body", p.getArrowsInBody());
			config.set("saturation", p.getSaturation());
			config.set("saturated-regen-rate", p.getSaturatedRegenRate());
			config.set("unsaturated-regen-rate", p.getUnsaturatedRegenRate());
			config.set("velocity", p.getVelocity());
		}
		else
		{
			config.set("death", true);
		}
		
		try
		{
			config.save(file);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		
		npc.despawn();
		npc.destroy();
	}
}
