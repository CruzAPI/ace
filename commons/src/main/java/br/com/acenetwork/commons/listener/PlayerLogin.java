package br.com.acenetwork.commons.listener;

import java.io.File;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

import br.com.acenetwork.commons.Commons;
import br.com.acenetwork.commons.CommonsUtil;
import br.com.acenetwork.commons.constants.Tag;
import br.com.acenetwork.commons.executor.BanCMD;
import br.com.acenetwork.commons.manager.CommonsConfig;
import br.com.acenetwork.commons.manager.CommonsConfig.Type;

public class PlayerLogin implements Listener
{
	@EventHandler(priority = EventPriority.HIGHEST)
	public void on(PlayerLoginEvent e)
	{
		Player p = e.getPlayer();
		
		String uuid0 = CommonsUtil.getUUIDByName(p.getName());

		File playerFile = CommonsConfig.getFile(Type.PLAYER, false, uuid0);
		YamlConfiguration playerConfig = YamlConfiguration.loadConfiguration(playerFile);
		
		String ip = playerConfig.getString("ip");
		
		File bannedIpsFile = CommonsConfig.getFile(Type.BANNED_IPS, false, ip);
		YamlConfiguration bannedIpsConfig = YamlConfiguration.loadConfiguration(bannedIpsFile);

		String uuid1 = bannedIpsConfig.getString("uuid");

		String[] uuids = new String[2];

		uuids[0] = uuid0;
		uuids[1] = uuid0.equals(uuid1) ? null : uuid1;
		
		for(String uuid : uuids)
		{
			if(uuid == null)
			{
				continue;
			}

			File bannedPlayersFile = CommonsConfig.getFile(Type.BANNED_PLAYERS, false, uuid);
			YamlConfiguration bannedPlayersConfig = YamlConfiguration.loadConfiguration(bannedPlayersFile);
	
			if(bannedPlayersFile.exists())
			{
				long time = bannedPlayersConfig.getLong("time");
				boolean valid = time == 0L || time > System.currentTimeMillis();

				if(valid)
				{
					String by = bannedPlayersConfig.getString("by");
					Tag tag = Tag.valueOf(bannedPlayersConfig.getString("tag"));
					String reason = bannedPlayersConfig.getString("reason");	
					
					String kickMessage = BanCMD.getKickMessage(by, tag, time, reason);

					e.setResult(Result.KICK_BANNED);
					e.setKickMessage(kickMessage);
					return;
				}
			}
		}
		
		if(Commons.isRestarting())
		{
			e.setResult(Result.KICK_OTHER);
			e.setKickMessage("§cServer restarting...");
			return;
		}
	}
}
