package br.com.acenetwork.lobby.listener;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLocaleChangeEvent;
import org.bukkit.inventory.meta.FireworkMeta;

import br.com.acenetwork.commons.player.CommonPlayer;
import br.com.acenetwork.commons.player.craft.CraftCommonPlayer;
import br.com.acenetwork.lobby.LobbyScoreboard;
import br.com.acenetwork.lobby.Main;

public class PlayerJoin implements Listener
{
	@EventHandler
	public void on(PlayerJoinEvent e)
	{
		Player p = e.getPlayer();
		CommonPlayer cp = CraftCommonPlayer.get(p);
		
		p.teleport(Main.getSpawnLocation());
		
		p.sendMessage(StringUtils.repeat(" \n", 100));
		p.sendMessage("§7§m ]                                                                            [ ");
		p.sendMessage("                             §b§lAce Network");
		p.sendMessage(" ");
		p.sendMessage("                Welcome, §a" + p.getName() + "§f to the Ace Network");
		p.sendMessage(" ");
		p.sendMessage("  §2§lWEBSITE§f www.acenetwork.com.br");
		p.sendMessage("  §9§lDISCORD§f discord.com/invite/Dsqv8tdjxN");
		p.sendMessage("§7§m ]                                                                            [ ");
		
//		if(!cp.isInvis())
//		{
			Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), new Runnable()
			{
				public void run()
				{
					Firework f = (Firework) p.getWorld().spawnEntity(p.getLocation(), EntityType.FIREWORK);
					FireworkMeta meta = f.getFireworkMeta();
					
					meta.addEffect(FireworkEffect.builder().with(Type.BALL).withColor(Color.AQUA).build());
					meta.addEffect(FireworkEffect.builder().with(Type.BALL_LARGE).withColor(Color.PURPLE).build());
					meta.setPower(1);
					
					f.setFireworkMeta(meta);
				}
			}, 1L);
//		}
		
		cp.setCommonsScoreboard(new LobbyScoreboard(cp));
	}
}