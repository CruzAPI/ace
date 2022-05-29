package br.com.acenetwork.commons.inventory;

import java.text.MessageFormat;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import br.com.acenetwork.commons.manager.FakeAnvil;
import br.com.acenetwork.commons.player.CommonPlayer;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.inventory.ContainerAnvil;

public class AnvilCommand extends GUI
{
	private final String command;
	
	public AnvilCommand(CommonPlayer cp, String placeholder, String command)
	{
		super(cp, getInventory(cp.getPlayer()));
		
		this.command = command;

		ItemStack item = new ItemStack(Material.PAPER);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(placeholder);
		item.setItemMeta(meta);
		
		inv.setItem(0, item);
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e)
	{
		Player p = cp.getPlayer();
	
		if(e.getPlayer() != p)
		{
			return;
		}

		inv.clear();
	}

	@EventHandler
	public void a(PrepareAnvilEvent e)
	{
		Bukkit.broadcastMessage("§a" + e.getInventory().getRenameText());
		Bukkit.broadcastMessage("§a" + e.getResult() + "");
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e)
	{
		Player p = cp.getPlayer();
	
		if(e.getWhoClicked() != p)
		{
			return;
		}
		
		e.setCancelled(true);

		
		
//		current.get
		
//		ContainerAnvil aaa;
//		aaa.
		
		ClickType click = e.getClick();
		int rawSlot = e.getRawSlot();
		int slot = e.getSlot();
		
		ItemStack current = inv.getItem(rawSlot);
		
		Bukkit.broadcastMessage(current + " current");
		
		if(current == null)
		{
			return;
		}
		
		
		
		String displayName = current.hasItemMeta() ? current.getItemMeta().getDisplayName() : null;
		
		Bukkit.broadcastMessage(rawSlot + " rawSlot");
		Bukkit.broadcastMessage(slot + " slot");
		Bukkit.broadcastMessage(displayName + " displayName");
		Bukkit.broadcastMessage(current.hasItemMeta() + " hasItemMeta");
		
		if(displayName != null && slot == 2 && click == ClickType.LEFT)
		{
			Bukkit.broadcastMessage(" truuuu ");
			p.closeInventory();
			p.chat(MessageFormat.format(command, displayName));
		}
	}

	private static Inventory getInventory(Player p)
	{
		return new FakeAnvil(p, ((CraftPlayer) p).getHandle().nextContainerCounter(), "asdasd").getBukkitView().getTopInventory();
	}
}
