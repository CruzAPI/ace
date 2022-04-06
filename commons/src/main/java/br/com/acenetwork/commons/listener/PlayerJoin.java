package br.com.acenetwork.commons.listener;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLocaleChangeEvent;

import br.com.acenetwork.commons.Commons;
import br.com.acenetwork.commons.CommonsUtil;
import br.com.acenetwork.commons.event.PlayerModeEvent;
import br.com.acenetwork.commons.player.CommonPlayer;
import br.com.acenetwork.commons.player.craft.CraftCommonAdmin;
import br.com.acenetwork.commons.player.craft.CraftCommonPlayer;
import br.com.acenetwork.commons.player.craft.CraftCommonWatcher;

public class PlayerJoin implements Listener
{
	private static final Map<Player, Integer> TASK = new HashMap<>();
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void on(PlayerJoinEvent e)
	{
		Player p = e.getPlayer();
		String uuid = CommonsUtil.getUUIDByName(p.getName());
		
		if(CommonsUtil.hasPermission(uuid, "cmd.admin"))
		{
			new CraftCommonAdmin(p);
		}
		else if(CommonsUtil.hasPermission(uuid, "cmd.watch"))
		{
			new CraftCommonWatcher(p);
		}
		else
		{
			Bukkit.getPluginManager().callEvent(new PlayerModeEvent(p));
		}
		
		CommonPlayer cp = CraftCommonPlayer.get(p);		
		cp.setTag(cp.getBestTag());
		
		if(cp.hasPermission("cmd.specs"))
		{
			p.chat("/specs");
		}
		
		for(CommonPlayer cpall : CraftCommonPlayer.SET)
		{
			if(cpall.isInvis())
			{
				p.hidePlayer(Commons.getPlugin(), cpall.getPlayer());
			}
		}

		if(cp.isInvis())
		{
			e.setJoinMessage(null);
		}
		
		String locale = p.getLocale();
		
		int id = Bukkit.getScheduler().scheduleSyncRepeatingTask(Commons.getPlugin(), new Runnable()
		{
			int ticks = 200;
			
			@Override
			public void run()
			{
				if(!locale.equals(p.getLocale()))
				{
					Bukkit.getPluginManager().callEvent(new PlayerLocaleChangeEvent(p, p.getLocale()));
					Bukkit.getScheduler().cancelTask(TASK.remove(p));
				}
				else if(ticks <= 0)
				{
					Bukkit.getScheduler().cancelTask(TASK.remove(p));
				}
				
				ticks--;
			}
		}, 1L, 1L);
		
		TASK.put(p, id);
	}
}