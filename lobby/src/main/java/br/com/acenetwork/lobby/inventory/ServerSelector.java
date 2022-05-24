package br.com.acenetwork.lobby.inventory;

import java.util.Arrays;
import java.util.ResourceBundle;

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
import br.com.acenetwork.commons.player.CommonPlayer;
import net.md_5.bungee.api.ChatColor;

public class ServerSelector extends GUI
{
	private ItemStack survival;
	private ItemStack tntrun;
	
	@SuppressWarnings("deprecation")
	public ServerSelector(CommonPlayer cp)
	{
		super(cp, "commons.words.server-selector", 9 * 3);
		
		Player p = cp.getPlayer();
		
		ResourceBundle bundle = ResourceBundle.getBundle("message", cp.getLocale());
		
		for(int i = 0; i < inv.getSize(); i++)
		{
			inv.setItem(i, new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
		}
		
		ItemMeta meta;
		
		survival = new ItemStack(Material.EMERALD_BLOCK);
		meta = survival.getItemMeta();
		meta.addEnchant(Enchantment.DURABILITY, 3, true);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		meta.setDisplayName(ChatColor.GREEN + bundle.getString("commons.words.survival"));
		meta.setLore(Arrays.asList
		(
				ChatColor.GRAY + bundle.getString("lobby.inv.server-selector.join-now"),
				ChatColor.GREEN + bundle.getString("lobby.inv.server-selector.survival-world")
		));
		survival.setItemMeta(meta);
		
		tntrun = new ItemStack(Material.TNT);
		meta = tntrun.getItemMeta();
		meta.addEnchant(Enchantment.DURABILITY, 3, true);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		meta.addEnchant(Enchantment.DURABILITY, 3, true);
		meta.setDisplayName(ChatColor.RED + bundle.getString("commons.words.tnt-run"));
		meta.setLore(Arrays.asList
		(
				ChatColor.GRAY + bundle.getString("lobby.inv.server-selector.join-now"),
				ChatColor.RED + bundle.getString("lobby.inv.server-selector.minigame-tnt-run")
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
			CommonsUtil.bungeeSendPlayer(p.getName(), "raid");
			p.closeInventory();
		}
		else if(CommonsUtil.compareDisplayName(item, tntrun) && click == ClickType.LEFT)
		{
			CommonsUtil.bungeeSendPlayer(p.getName(), "a1.tntrun");
			p.closeInventory();
		}
	}
}