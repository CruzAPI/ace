package br.com.acenetwork.tntrun.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

import br.com.acenetwork.commons.CommonsUtil;
import br.com.acenetwork.commons.player.craft.CraftCommonPlayer;
import br.com.acenetwork.tntrun.Main;
import br.com.acenetwork.tntrun.constant.State;
import br.com.acenetwork.tntrun.player.Competitor;

public class PlayerLogin implements Listener
{
	@EventHandler
	public void on(PlayerLoginEvent e)
	{
		Player p = e.getPlayer();
		String uuid = CommonsUtil.getUUIDByName(p.getName());
		
		if(CommonsUtil.hasPermission(uuid, "cmd.admin") || CommonsUtil.hasPermission(uuid, "cmd.watch"))
		{
			return;
		}
		
		if(Main.getState() == State.STARTING && CraftCommonPlayer.getAll(Competitor.class).size() >= Main.getMaxPlayers())
		{
			e.setResult(Result.KICK_FULL);
		}
	}
}