package br.com.acenetwork.survival.listener;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import br.com.acenetwork.commons.manager.CommonsConfig;
import br.com.acenetwork.commons.manager.Message;
import br.com.acenetwork.commons.player.CommonPlayer;
import br.com.acenetwork.commons.player.craft.CraftCommonPlayer;
import br.com.acenetwork.survival.Main;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Inventory;

public class PlayerDeath implements Listener
{
	@SuppressWarnings("deprecation")
	@EventHandler
	public void on(PlayerDeathEvent e)
	{
		Player p = e.getEntity();		
		Player killer = p.getKiller();
		
		if(killer != null && killer != p)
		{			
			File playerFile = CommonsConfig.getFile(CommonsConfig.Type.BALANCE_RAID_PLAYER, true, p.getUniqueId());
			YamlConfiguration playerConfig = YamlConfiguration.loadConfiguration(playerFile);
	
			File killerFile = CommonsConfig.getFile(CommonsConfig.Type.BALANCE_RAID_PLAYER, true, killer.getUniqueId());
			YamlConfiguration killerConfig = YamlConfiguration.loadConfiguration(killerFile);
			
			double playerBalance = playerConfig.getDouble("balance");
			double playerMaxBalance = playerConfig.getDouble("max-balance");
	
			double killerBalance = killerConfig.getDouble("balance");
			double killerMaxBalance = killerConfig.getDouble("max-balance");
			
			double balanceStole = playerBalance * 0.2D;
			double maxBalanceStole = playerMaxBalance * 0.2D;

			playerBalance -= balanceStole;
			playerMaxBalance -= maxBalanceStole;

			killerMaxBalance += maxBalanceStole;
			killerBalance += balanceStole;

			playerConfig.set("max-balance", playerMaxBalance);
			playerConfig.set("balance", Math.min(playerMaxBalance, playerBalance));

			killerConfig.set("max-balance", killerMaxBalance);
			killerConfig.set("balance", Math.min(killerMaxBalance, killerBalance));

			try
			{
				playerConfig.save(playerFile);
				killerConfig.save(killerFile);

				DecimalFormat df = new DecimalFormat("0.##");

				p.sendMessage(Message.getMessage(p.getLocale(), "event.playerdeath.other-stole", 
					df.format(balanceStole),
					df.format(maxBalanceStole),
					killer.getName()));
				
				CommonPlayer commonKiller = CraftCommonPlayer.get(killer);
				
				if(commonKiller != null)
				{					
					commonKiller.sendMessage("event.playerdeath.you-stole", 
						df.format(balanceStole), df.format(maxBalanceStole), p.getName());
				}
			}
			catch(IOException ex)
			{
				ex.printStackTrace();
			}
		}
		
		if(p.hasMetadata("NPC") && !e.getKeepInventory())
		{		
			NPC npc = CitizensAPI.getNPCRegistry().getNPC(p);
			
			Inventory inventory = npc.getTrait(Inventory.class);
			
			for(ItemStack item : inventory.getContents())
			{
				if(item == null || item.getType() == Material.AIR)
				{
					continue;
				}
				
				e.getDrops().add(item);
			}
			
			PlayerQuit.removeCombatLogger(npc);
		}
		
		CommonPlayer cp = CraftCommonPlayer.get(p);
		
		if(p.hasMetadata("hide death message"))
		{
			p.removeMetadata("hide death message", Main.getInstance());
			e.setDeathMessage(null);
		}
		
		if(cp != null)
		{			
			cp.setCombat(false);
			cp.setPlayerCombat(false);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void b(PlayerDeathEvent e)
	{
		Player p = e.getEntity();
		
		File playerFile = CommonsConfig.getFile(CommonsConfig.Type.BALANCE_RAID_PLAYER, true, p.getUniqueId());
		YamlConfiguration playerConfig = YamlConfiguration.loadConfiguration(playerFile);
		
		double playerBalance = playerConfig.getDouble("balance");
		double playerMaxBalance = playerConfig.getDouble("max-balance");

		double balanceLost = playerBalance * 0.01D;
		double maxBalanceLost = playerMaxBalance * 0.01D;

		playerBalance -= balanceLost;
		playerMaxBalance -= maxBalanceLost;
		
		playerConfig.set("max-balance", playerMaxBalance);
		playerConfig.set("balance", Math.min(playerMaxBalance, playerBalance));

		try
		{
			playerConfig.save(playerFile);
			
			DecimalFormat df = new DecimalFormat("0.##");

			p.sendMessage(Message.getMessage(p.getLocale(), "event.playerdeath.you-lost", 
				df.format(balanceLost),
				df.format(maxBalanceLost)));
			
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
		}
	}
}