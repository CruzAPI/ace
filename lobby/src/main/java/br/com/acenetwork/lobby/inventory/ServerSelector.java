package br.com.acenetwork.lobby.inventory;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import br.com.acenetwork.commons.CommonsUtil;
import br.com.acenetwork.commons.inventory.GUI;
import br.com.acenetwork.commons.manager.Message;
import br.com.acenetwork.commons.player.CommonPlayer;

public class ServerSelector extends GUI
{
	private ItemStack survival;
	private ItemStack tntrun;
	
	public ServerSelector(CommonPlayer cp)
	{
		super(cp, Bukkit.createInventory(cp.getPlayer(), 9 * 3, 
			Message.getMessage(cp.getPlayer().getLocale(), "lobby.inventory.server-selector.title")));
		
		Player p = cp.getPlayer();
		String locale = p.getLocale();
		
		for(int i = 0; i < inv.getSize(); i++)
		{
			inv.setItem(i, new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
		}
		
		ItemMeta meta;
		
		survival = new ItemStack(Material.EMERALD_BLOCK);
		meta = survival.getItemMeta();
		meta.addEnchant(Enchantment.DURABILITY, 3, true);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		meta.setDisplayName(Message.getMessage(locale, "lobby.inventory.server-selector.survival.display-name"));
		meta.setLore(Arrays.asList
		(
			Message.getMessage(locale, "lobby.inventory.server-selector.survival.lore1"),
			Message.getMessage(locale, "lobby.inventory.server-selector.survival.lore2")
		));
		survival.setItemMeta(meta);
		
		tntrun = new ItemStack(Material.TNT);
		meta = tntrun.getItemMeta();
		meta.addEnchant(Enchantment.DURABILITY, 3, true);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		meta.addEnchant(Enchantment.DURABILITY, 3, true);
		meta.setDisplayName(Message.getMessage(locale, "lobby.inventory.server-selector.tntrun.display-name"));
		meta.setLore(Arrays.asList
		(
			Message.getMessage(locale, "lobby.inventory.server-selector.tntrun.lore1"),
			Message.getMessage(locale, "lobby.inventory.server-selector.tntrun.lore2")
		));
		tntrun.setItemMeta(meta);
		
		inv.setItem(12, survival);
		inv.setItem(14, tntrun);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void on(InventoryClickEvent e)
	{
		Player p = (Player) e.getWhoClicked();
		
		if(cp.getPlayer() != p)
		{
			return;
		}
		
		e.setCancelled(true);
		
		ItemStack item = e.getCurrentItem();
		ClickType click = e.getClick();
		
		if(CommonsUtil.compareDisplayName(item, survival) && click == ClickType.LEFT)
		{
			CommonsUtil.bungeeSendPlayer(p.getName(), "emerald");
		}
		else if(CommonsUtil.compareDisplayName(item, tntrun) && click == ClickType.LEFT)
		{
			CommonsUtil.bungeeSendPlayer(p.getName(), "a1.tntrun");
		}
	}
}