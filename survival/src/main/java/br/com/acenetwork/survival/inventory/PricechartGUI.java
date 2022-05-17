package br.com.acenetwork.survival.inventory;

import java.io.File;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.logging.log4j.util.Strings;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import br.com.acenetwork.commons.CommonsUtil;
import br.com.acenetwork.commons.inventory.Scroller;
import br.com.acenetwork.commons.player.CommonPlayer;
import br.com.acenetwork.survival.event.BuyItemEvent;
import br.com.acenetwork.survival.event.SellItemEvent;
import br.com.acenetwork.survival.manager.Config;
import br.com.acenetwork.survival.manager.Config.Type;
import net.md_5.bungee.api.ChatColor;

public class PricechartGUI extends Scroller
{
	private ItemStack sortItem;
	private int sortIndex = 1;
	private boolean inverted;
	
	private enum Sort
	{
		ALPHABETICAL, TYPE, PRICE, LIQUIDITY;
	}
	
	public PricechartGUI(CommonPlayer cp)
	{
		super(cp, "raid.inv.pricechart.title");
		
		sortItem = new ItemStack(Material.HOPPER);
		
		refresh();
	}
	
	private void resetList()
	{
		ResourceBundle bundle = ResourceBundle.getBundle("message", cp.getLocale());
		
		itemList.clear();
		
		File file = Config.getFile(Type.PRICE, true);
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		
		ConfigurationSection section = config.getConfigurationSection("");
		
		if(section != null)
		{
			DecimalFormat df = new DecimalFormat("#.####", new DecimalFormatSymbols(bundle.getLocale()));
			df.setGroupingUsed(true);
			df.setGroupingSize(3);
			
			for(String key : section.getKeys(false))
			{
				double liquidity = config.getDouble(key + ".liquidity");
				int marketCap = config.getInt(key + ".market-cap");
				
				double price = liquidity / marketCap;
				
				ItemStack itemStack = new ItemStack(Material.valueOf(key));
				ItemMeta meta = itemStack.getItemMeta();
				meta.setDisplayName("§f" + key);
				meta.setLore(List.of
				(
					Strings.EMPTY,
					ChatColor.GREEN + StringUtils.capitalize(bundle.getString("commons.words.price"))
							.concat(": ")
							+ ChatColor.YELLOW
							+ df.format(price),
					Strings.EMPTY,
					ChatColor.GREEN + StringUtils.capitalize(bundle.getString("raid.inv.pricechart.shards-in-liquidity"))
							.concat(": ")
							+ ChatColor.YELLOW
							+ df.format(liquidity),
					ChatColor.GREEN + StringUtils.capitalize(bundle.getString("raid.inv.pricechart.items-in-liquidity"))
							.concat(": ")
							+ ChatColor.YELLOW
							+ df.format(marketCap)
				));
				
				itemStack.setItemMeta(meta);
				itemList.add(new ItemLiquidity(itemStack, key, liquidity, marketCap));
			}
		}
	}
	
	private class ItemLiquidity extends Item
	{
		private String key;
		private double liquidity;
		private int marketCap;
		
		public ItemLiquidity(ItemStack itemStack, String key, double liquidity, int marketCap)
		{
			super(itemStack);
			
			this.key = key;
			this.liquidity = liquidity;
			this.marketCap = marketCap;
		}
		
		private double price()
		{
			return liquidity / marketCap;
		}
	}
	
	@Override
	public void refresh()
	{
		Sort sort = Sort.values()[sortIndex];
		
		resetList();
		
		Collections.sort(itemList, new Comparator<Item>()
		{
			@Override
			public int compare(Item o1, Item o2)
			{
				final ItemLiquidity i1;
				final ItemLiquidity i2;
				
				if(inverted)
				{
					i1 = (ItemLiquidity) o2;
					i2 = (ItemLiquidity) o1;
				}
				else
				{
					i1 = (ItemLiquidity) o1;
					i2 = (ItemLiquidity) o2;
				}
				
				switch(sort)
				{
				case ALPHABETICAL:
					return i1.key.compareTo(i2.key);
				case PRICE:
					return Double.valueOf(i1.price()).compareTo(i2.price());
				case LIQUIDITY:
					return Double.valueOf(i1.liquidity).compareTo(i2.liquidity);
				default:
					return inverted ? -1 : 0;
				}
			}
		});
		
		
		super.refresh();
		
		ResourceBundle bundle = ResourceBundle.getBundle("message", cp.getLocale());
		
		ItemMeta meta;
		
		meta = sortItem.getItemMeta();
		meta.setDisplayName("§f" + WordUtils.capitalize(bundle.getString("commons.verbs.sort")));
		meta.setLore(List.of
		(
			Strings.EMPTY,
			getSortSubtitle(Sort.ALPHABETICAL, StringUtils.capitalize(bundle.getString("commons.words.alphabetical"))),
			getSortSubtitle(Sort.TYPE, StringUtils.capitalize(bundle.getString("commons.words.type"))),
			getSortSubtitle(Sort.PRICE, StringUtils.capitalize(bundle.getString("commons.words.price"))),
			getSortSubtitle(Sort.LIQUIDITY, StringUtils.capitalize(bundle.getString("commons.words.liquidity"))),
			Strings.EMPTY,
			ChatColor.WHITE + StringUtils.capitalize(bundle.getString("commons.invs.change-sort"))
					.concat(" [")	
					.concat(StringUtils.capitalize(bundle.getString("commons.invs.left-right-click")))
					.concat("]"),
			ChatColor.WHITE + StringUtils.capitalize(bundle.getString("commons.invs.invert-sort"))
					.concat(" [")	
					.concat(StringUtils.capitalize(bundle.getString("commons.invs.middle-click")))
					.concat("]")
		));
		sortItem.setItemMeta(meta);
		
		inv.setItem(5, sortItem);
	}
	
	private String getSortSubtitle(Sort sort, String string)
	{
		ChatColor color = ChatColor.DARK_GRAY;
		String suffix = "";
		
		if(Sort.values()[sortIndex] == sort)
		{
			color = ChatColor.GREEN;
			suffix = " " + (inverted ? "\u25bc" : "\u25b2");
		}
		
		return color + string + suffix;
	}
	
	@EventHandler
	public void a(BuyItemEvent e)
	{
		refresh();
	}
	
	@EventHandler
	public void a(SellItemEvent e)
	{
		refresh();
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void asddasdf(InventoryClickEvent e)
	{
		Player p = cp.getPlayer();
		
		if(e.getWhoClicked() != p)
		{
			return;
		}
		
		e.setCancelled(true);
		
		ItemStack current = e.getCurrentItem();
		ClickType click = e.getClick();
		
		if(CommonsUtil.compareDisplayName(current, sortItem))
		{
			if(click == ClickType.LEFT)
			{
				sortIndex--;
				sortIndex = sortIndex < 0 ? Sort.values().length - 1 : sortIndex;
				refresh();
			}
			else if(click == ClickType.RIGHT)
			{
				sortIndex++;
				sortIndex = sortIndex >= Sort.values().length ? 0 : sortIndex;
				refresh();
			}
			else if(click == ClickType.MIDDLE)
			{
				inverted = !inverted;
				refresh();
			}
		}
	}
}
