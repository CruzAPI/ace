package br.com.acenetwork.commons.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import br.com.acenetwork.commons.Commons;
import br.com.acenetwork.commons.CommonsUtil;
import br.com.acenetwork.commons.event.PlayerModeEvent;
import br.com.acenetwork.commons.player.CommonPlayer;
import br.com.acenetwork.commons.player.craft.CraftCommonAdmin;
import br.com.acenetwork.commons.player.craft.CraftCommonPlayer;
import br.com.acenetwork.commons.player.craft.CraftCommonWatcher;

public class PlayerJoin implements Listener
{
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
	}
}