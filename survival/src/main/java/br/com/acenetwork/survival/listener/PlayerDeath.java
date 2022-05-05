package br.com.acenetwork.survival.listener;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.ResourceBundle;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
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
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

public class PlayerDeath implements Listener
{
	@SuppressWarnings("deprecation")
	@EventHandler
	public void on(PlayerDeathEvent e)
	{
		Player p = e.getEntity();		
		Player killer = p.getKiller();
		
		ResourceBundle playerBundle = ResourceBundle.getBundle("message", CommonsUtil.getLocaleFromMinecraft(p.getLocale()));
		
		OfflinePlayer op = Arrays.stream(Bukkit.getOfflinePlayers()).filter(x -> x.getName().equals(p.getName())).findAny().orElseThrow();
		
		if(killer != null && killer != p)
		{			
			ResourceBundle killerBundle = ResourceBundle.getBundle("message", CommonsUtil.getLocaleFromMinecraft(killer.getLocale()));
			
			File playerFile = CommonsConfig.getFile(CommonsConfig.Type.BALANCE_RAID_PLAYER, true, op.getUniqueId());
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

				DecimalFormat df = new DecimalFormat("#.##");
				
				df.setGroupingSize(3);
				df.setGroupingUsed(true);
				
				df.setDecimalFormatSymbols(new DecimalFormatSymbols(playerBundle.getLocale()));
				
				TextComponent[] extra = new TextComponent[2];
				
				extra[0] = new TextComponent(df.format(balanceStole) + "/" + df.format(maxBalanceStole));
				extra[1] = new TextComponent(killer.getName());
				
				TextComponent text = Message.getTextComponent(playerBundle.getString("raid.event.player-death.other-stole"), extra);
				text.setColor(ChatColor.RED);
				p.spigot().sendMessage(text);

				df.setDecimalFormatSymbols(new DecimalFormatSymbols(killerBundle.getLocale()));
				
				extra[0] = new TextComponent(df.format(balanceStole) + "/" + df.format(maxBalanceStole));
				extra[0].setColor(ChatColor.YELLOW);
				
				extra[1] = new TextComponent(p.getName());
				extra[1].setColor(ChatColor.YELLOW);
				
				text = Message.getTextComponent(killerBundle.getString("raid.event.player-death.you-stole"), extra);
				text.setColor(ChatColor.GREEN);
				killer.spigot().sendMessage(text);
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
		
		if(p.hasMetadata("hide death message"))
		{
			p.removeMetadata("hide death message", Main.getInstance());
			e.setDeathMessage(null);
			return;
		}
		
		ResourceBundle bundle = ResourceBundle.getBundle("message", CommonsUtil.getLocaleFromMinecraft(p.getLocale()));
		
		OfflinePlayer op = Arrays.stream(Bukkit.getOfflinePlayers()).filter(x -> x.getName().equals(p.getName())).findAny().orElseThrow();
		
		File playerFile = CommonsConfig.getFile(CommonsConfig.Type.BALANCE_RAID_PLAYER, true, op.getUniqueId());
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
			
			DecimalFormat df = new DecimalFormat("#.##", new DecimalFormatSymbols(bundle.getLocale()));
			
			df.setGroupingSize(3);
			df.setGroupingUsed(true);
			
			TextComponent[] extra = new TextComponent[1];
			
			extra[0] = new TextComponent(df.format(balanceLost) + "/" + df.format(maxBalanceLost));
			
			TextComponent text = Message.getTextComponent(bundle.getString("raid.event.player-death.you-lost"), extra);
			text.setColor(ChatColor.RED);
			p.spigot().sendMessage(text);
			
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
		}
	}
}