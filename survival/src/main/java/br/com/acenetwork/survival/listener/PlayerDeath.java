package br.com.acenetwork.survival.listener;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import br.com.acenetwork.commons.CommonsUtil;
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
	@EventHandler
	public void on(PlayerDeathEvent e)
	{
		Player p = e.getEntity();		
		Player killer = p.getKiller();
		
		String playerUUID = CommonsUtil.getUUIDByName(p.getName());
		
		if(killer != null && killer != p)
		{			
			String killerUUID = CommonsUtil.getUUIDByName(killer.getName());
			
			File playerFile = CommonsConfig.getFile(CommonsConfig.Type.BALANCE_RAID_PLAYER, true, playerUUID);
			YamlConfiguration playerConfig = YamlConfiguration.loadConfiguration(playerFile);
	
			File killerFile = CommonsConfig.getFile(CommonsConfig.Type.BALANCE_RAID_PLAYER, true, killerUUID);
			YamlConfiguration killerConfig = YamlConfiguration.loadConfiguration(killerFile);
			
			double playerBalance = playerConfig.getDouble("balance");
			double playerMaxBalance = playerConfig.getDouble("max-balance");
	
			double killerBalance = killerConfig.getDouble("balance");
			double killerMaxBalance = killerConfig.getDouble("max-balance");
			
			double balanceStole = playerBalance * 0.08D;
			double maxBalanceStole = playerMaxBalance * 0.08D;

			double balanceLost = playerBalance * 0.02D;
			double maxBalanceLost = playerMaxBalance * 0.02D;

			double totalMaxBalanceLost = maxBalanceStole + maxBalanceLost;
			double totalBalanceLost = balanceStole + balanceLost;

			playerMaxBalance -= totalMaxBalanceLost;
			playerBalance -= totalBalanceLost;

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

				// GlobalScoreboard.MAP.put(p, playerMaxBalance);
				// GlobalScoreboard.MAP.put(killer, killerMaxBalance);
				// GlobalScoreboard.update();

				DecimalFormat df = new DecimalFormat("0.##");

				p.sendMessage(Message.getMessage(p.getLocale(), "event.playerdeath.ace-lost", 
					df.format(totalBalanceLost), 
					df.format(totalMaxBalanceLost)));
				
				CommonPlayer commonKiller = CraftCommonPlayer.get(killer);
				
				if(commonKiller != null)
				{					
					commonKiller.sendMessage("event.playerdeath.ace-stolen", 
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
}