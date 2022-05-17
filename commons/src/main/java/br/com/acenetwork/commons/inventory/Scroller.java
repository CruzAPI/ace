package br.com.acenetwork.commons.inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.logging.log4j.util.Strings;
import org.bukkit.Material;
import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.text.WordUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import br.com.acenetwork.commons.CommonsUtil;
import br.com.acenetwork.commons.player.CommonPlayer;
import net.md_5.bungee.api.ChatColor;

public abstract class Scroller extends GUI
{
	private ItemStack scrollUp;
	private ItemStack scrollDown;
	protected int row;
	
	protected final List<Item> itemList = new ArrayList<>();
	
	public Scroller(CommonPlayer cp, String key)
	{
		super(cp, key, 6 * 9);
		
		ResourceBundle bundle = ResourceBundle.getBundle("message", cp.getLocale());
		
		ItemMeta meta; 
		
		scrollUp = new ItemStack(Material.LIME_DYE);
		meta = scrollUp.getItemMeta();
		meta.setDisplayName("§a" + WordUtils.capitalize(bundle.getString("commons.words.scroll-up")));
		scrollUp.setItemMeta(meta);
		
		scrollDown = new ItemStack(Material.LIME_DYE);
		meta = scrollDown.getItemMeta();
		meta.setDisplayName("§a" + WordUtils.capitalize(bundle.getString("commons.words.scroll-down")));
		scrollDown.setItemMeta(meta);
	}
	
	
	
	public void refresh()
	{
		for(int i = 0; i < 4 * 9; i++)
		{
			ItemStack itemStack;
			
			try
			{
				itemStack = itemList.get(row * 9 + i).itemStack;
			}
			catch(IndexOutOfBoundsException ex)
			{
				itemStack = null;
			}
			
			inv.setItem(9 + i, itemStack);
		}
		
		inv.setItem(4, row > 0 ? scrollUp : null);
		inv.setItem(49, itemList.size() - row * 9 > 36 ? scrollDown : null);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void asddasasddf(InventoryClickEvent e)
	{
		Player p = cp.getPlayer();
		
		if(e.getWhoClicked() != p)
		{
			return;
		}
		
		e.setCancelled(true);
		
		ItemStack current = e.getCurrentItem();
		ClickType click = e.getClick();
		
		if(CommonsUtil.compareDisplayName(current, scrollUp) && click == ClickType.LEFT)
		{
			row--;
			refresh();
		}
		else if(CommonsUtil.compareDisplayName(current, scrollDown) && click == ClickType.LEFT)
		{
			row++;
			refresh();
		}
	}
}
