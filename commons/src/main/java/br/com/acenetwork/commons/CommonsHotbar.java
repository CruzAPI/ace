package br.com.acenetwork.commons;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLocaleChangeEvent;

import br.com.acenetwork.commons.player.CommonPlayer;

public abstract class CommonsHotbar implements Listener
{
	protected final CommonPlayer cp;
	
	protected String locale;
	
	public CommonsHotbar(CommonPlayer cp)
	{
		Player p = cp.getPlayer();
		
		this.cp = cp;
		
		locale = p.getLocale();
	}
	
	@EventHandler
	public void a(PlayerLocaleChangeEvent e)
	{
		if(e.getPlayer() != cp.getPlayer())
		{
			return;
		}
		
		locale = e.getLocale();
	}
}