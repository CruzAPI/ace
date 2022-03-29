package br.com.acenetwork.commons.inventory;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import br.com.acenetwork.commons.player.CommonPlayer;

public abstract class Timer extends GUI
{
	protected int days;
	protected int hours;
	protected int minutes;
	
	public Timer(CommonPlayer cp)
	{
		super(cp, Bukkit.createInventory(null, InventoryType.HOPPER, "Timer"));
		
		inv.setItem(0, days());
		inv.setItem(1, hours());
		inv.setItem(2, minutes());
		inv.setItem(4, confirm());
	}
	
	public abstract void confirmed();
	
	public enum Type
	{
		DAYS, HOURS, MINUTES;
		
		public static Type getBySlot(int slot)
		{
			if(slot == 0)
			{
				return DAYS;
			}
			
			if(slot == 1)
			{
				return HOURS;
			}
			
			if(slot == 2)
			{
				return MINUTES;
			}
			
			return null;
		}
	}
	
	public long currentTimeMillis()
	{
		if(isPermanent())
		{
			return 0L;
		}
		
		return System.currentTimeMillis() + days * 24L * 60L * 60L * 1000L + hours * 60L * 60L * 1000L + minutes * 60L * 1000L;
	}
	
	public void refresh()
	{
		inv.setItem(0, days());
		inv.setItem(1, hours());
		inv.setItem(2, minutes());
	}
	
	public ItemStack days()
	{
		ItemStack item;
		
		if(isPermanent())
		{
			item = new ItemStack(Material.PURPLE_DYE, 1);
			ItemMeta meta = item.getItemMeta();
			
			meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Days: " + ChatColor.WHITE + "\u221e");
			item.setItemMeta(meta);
		}
		else if(days == 0)
		{
			item = new ItemStack(Material.GRAY_DYE, 1);
			ItemMeta meta = item.getItemMeta();
			
			meta.setDisplayName(ChatColor.GRAY + "Days: " + ChatColor.WHITE + days);
			item.setItemMeta(meta);
		}
		else
		{
			item = new ItemStack(Material.LIME_DYE, days);
			ItemMeta meta = item.getItemMeta();
			
			meta.setDisplayName(ChatColor.GREEN + "Days: " + ChatColor.WHITE + days);
			item.setItemMeta(meta);
		}
		
		return item;
	}
	
	public ItemStack hours()
	{
		ItemStack item;
		
		if(isPermanent())
		{
			item = new ItemStack(Material.PURPLE_DYE, 1);
			ItemMeta meta = item.getItemMeta();
			
			meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Hours: " + ChatColor.WHITE + "\u221e");
			item.setItemMeta(meta);
		}
		else if(hours == 0)
		{
			item = new ItemStack(Material.GRAY_DYE, 1);
			ItemMeta meta = item.getItemMeta();
			
			meta.setDisplayName(ChatColor.GRAY + "Hours: " + ChatColor.WHITE + hours);
			item.setItemMeta(meta);
		}
		else
		{
			item = new ItemStack(Material.LIME_DYE, hours);
			ItemMeta meta = item.getItemMeta();
			
			meta.setDisplayName(ChatColor.GREEN + "Hours: " + ChatColor.WHITE + hours);
			item.setItemMeta(meta);
		}
		
		return item;
	}
	
	public ItemStack minutes()
	{
		ItemStack item;
		
		if(isPermanent())
		{
			item = new ItemStack(Material.PURPLE_DYE, 1);
			ItemMeta meta = item.getItemMeta();
			
			meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Minutes: " + ChatColor.WHITE + "\u221e");			
			item.setItemMeta(meta);
		}
		else if(minutes == 0)
		{
			item = new ItemStack(Material.GRAY_DYE, 1);
			ItemMeta meta = item.getItemMeta();
			
			meta.setDisplayName(ChatColor.GRAY + "Minutes: " + ChatColor.WHITE + minutes);			
			item.setItemMeta(meta);
		}
		else
		{
			item = new ItemStack(Material.LIME_DYE, minutes);
			ItemMeta meta = item.getItemMeta();
			
			meta.setDisplayName(ChatColor.GREEN + "Minutes: " + ChatColor.WHITE + minutes);			
			item.setItemMeta(meta);
		}
		
		return item;
	}
	
	public ItemStack confirm()
	{
		ItemStack item = new ItemStack(Material.GOLDEN_AXE, 1);
		ItemMeta meta = item.getItemMeta();
		
		meta.setDisplayName(ChatColor.YELLOW + "Confirm");
		item.setItemMeta(meta);
		
		return item;
	}
	
	public boolean isPermanent()
	{
		return days == 0 && hours == 0 && minutes == 0;
	}
	
	public void increment(Type type)
	{
		switch(type)
		{
		case DAYS:
			if(days < 64)
			{
				days++;
			}
			else
			{
				days = 0;
			}
			break;
		case HOURS:
			if(hours < 23)
			{
				hours++;
			}
			else
			{
				hours = 0;
			}
			break;
		case MINUTES:
			if(minutes < 59)
			{
				minutes++;
			}
			else
			{
				minutes = 0;
			}
			break;
		}
		
		refresh();
	}
	
	public void decrement(Type type)
	{
		switch(type)
		{
		case DAYS:
			if(days > 0)
			{
				days--;
			}
			else
			{
				days = 64;
			}
			break;
		case HOURS:
			if(hours > 0)
			{
				hours--;
			}
			else
			{
				hours = 23;
			}
			break;
		case MINUTES:
			if(minutes > 0)
			{
				minutes--;
			}
			else
			{
				minutes = 59;
			}
			break;
		}
		
		refresh();
	}
	
	public void max(Type type)
	{
		switch(type)
		{
		case DAYS:
			days = 64;
			break;
		case HOURS:
			hours = 23;
			break;
		case MINUTES:
			minutes = 59;
			break;
		}
		
		refresh();
	}
	
	public void min(Type type)
	{
		switch(type)
		{
		case DAYS:
			days = 0;
			break;
		case HOURS:
			hours = 0;
			break;
		case MINUTES:
			minutes = 0;
			break;
		}
		
		refresh();
	}
	
	public void max()
	{
		days = 64;
		hours = 23;
		minutes = 59;
		
		refresh();
	}
	
	public void min()
	{
		days = 0;
		hours = 0;
		minutes = 0;
		
		refresh();
	}
	
	public int get(Type type)
	{
		switch(type)
		{
		case DAYS:
			return days;
		case HOURS:
			return hours;
		default:
			return minutes;
		}
	}



	@EventHandler//(priority = EventPriority.HIGHEST)
	public void on(InventoryClickEvent e)
	{
		Player p = (Player) e.getWhoClicked();
		
		if(p != cp.getPlayer())
		{
			return;
		}

		e.setCancelled(true);
		
		if(!e.getInventory().equals(inv))
		{
			return;
		}

		ClickType click = e.getClick();
		int slot = e.getSlot();
		
		Type type = Type.getBySlot(slot);
		
		if(type != null)
		{
			if(click == ClickType.LEFT)
			{
				increment(type);
			}
			else if(click == ClickType.RIGHT)
			{
				decrement(type);
			}
			else if(click == ClickType.SHIFT_LEFT)
			{
				max(type);
			}
			else if(click == ClickType.SHIFT_RIGHT)
			{
				min(type);
			}
			else if(click == ClickType.MIDDLE)
			{
				if(isPermanent())
				{
					max();
				}
				else
				{
					min();
				}
			}
		}
		else if(slot == 4 && click == ClickType.LEFT)
		{
			p.closeInventory();
			confirmed();
		}
	}
}