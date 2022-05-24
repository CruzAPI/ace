package br.com.acenetwork.lobby;

import java.util.ResourceBundle;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerLocaleChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import br.com.acenetwork.commons.CommonsHotbar;
import br.com.acenetwork.commons.CommonsUtil;
import br.com.acenetwork.commons.player.CommonPlayer;
import br.com.acenetwork.lobby.inventory.ServerSelector;
import net.md_5.bungee.api.ChatColor;

public class LobbyHotbar extends CommonsHotbar
{
	private ItemStack serverSelector;
	
	public LobbyHotbar(CommonPlayer cp)
	{
		super(cp);
		
		Player p = cp.getPlayer();
		
		ResourceBundle bundle = ResourceBundle.getBundle("message", cp.getLocale());
		
		ItemMeta meta;
		
		serverSelector = new ItemStack(Material.NETHER_STAR);
		meta = serverSelector.getItemMeta();
		meta.setDisplayName(ChatColor.WHITE + bundle.getString("commons.words.server-selector"));
		serverSelector.setItemMeta(meta);
		
		p.getInventory().setItem(4, serverSelector);
	}
	
	@EventHandler
	public void on(PlayerInteractEvent e)
	{
		Player p = e.getPlayer();
		
		if(cp.getPlayer() != p)
		{
			return;
		}
		
		ItemStack item = e.getItem();
		Action action = e.getAction();
		boolean rightClick = action.toString().contains("RIGHT");
		
		if(CommonsUtil.compareDisplayName(item, serverSelector) && rightClick)
		{
			new ServerSelector(cp);
		}
	}
	
	@EventHandler
	public void on(PlayerLocaleChangeEvent e)
	{
		Player p = e.getPlayer();
		
		if(cp.getPlayer() != p)
		{
			return;
		}
		
		ResourceBundle bundle = ResourceBundle.getBundle("message", cp.getLocale());
		
		ItemMeta meta;
		meta = serverSelector.getItemMeta();
		meta.setDisplayName(ChatColor.WHITE + bundle.getString("commons.words.server-selector"));
		serverSelector.setItemMeta(meta);
		
		p.getInventory().setItem(4, serverSelector);
	}
}