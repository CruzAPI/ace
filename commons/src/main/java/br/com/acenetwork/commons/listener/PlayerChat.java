package br.com.acenetwork.commons.listener;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;

import br.com.acenetwork.commons.constants.Tag;
import br.com.acenetwork.commons.executor.MuteCMD;
import br.com.acenetwork.commons.manager.CommonsConfig;
import br.com.acenetwork.commons.manager.CommonsConfig.Type;
import br.com.acenetwork.commons.player.CommonPlayer;
import br.com.acenetwork.commons.player.craft.CraftCommonPlayer;

public class PlayerChat implements Listener
{
	@EventHandler
	public void on(PlayerChatEvent e)
	{
		e.setCancelled(true);

		Player p = e.getPlayer();
		CommonPlayer cp = CraftCommonPlayer.get(p);
		
		File playerFile = CommonsConfig.getFile(Type.PLAYER, false, cp.getUUID());
		YamlConfiguration playerConfig = YamlConfiguration.loadConfiguration(playerFile);
		
		String clan = "";
		
		if(playerConfig.contains("clan"))
		{
			clan = "§8[§b" + playerConfig.getString("clan") + "§8] ";
		}
		
		String msg = clan + p.getDisplayName() + ":§l §f" + e.getMessage();
		
		File mutedPlayersFile = CommonsConfig.getFile(Type.MUTED_PLAYERS, false, cp.getUniqueID());
		YamlConfiguration mutedPlayersConfig = YamlConfiguration.loadConfiguration(mutedPlayersFile);

		if(mutedPlayersFile.exists())
		{
			long time = mutedPlayersConfig.getLong("time");
			boolean valid = time == 0L || time > System.currentTimeMillis();

			if(valid)
			{
				String by = mutedPlayersConfig.getString("by");
				Tag tag = Tag.valueOf(mutedPlayersConfig.getString("tag"));
				String reason = mutedPlayersConfig.getString("reason");	
				
				String warnMessage = MuteCMD.getWarnMessage(p.getLocale(), by, tag, time, reason);
				p.sendMessage(warnMessage);
				return;
			}
		}

		String playerUUID = cp.getUUID();

		for(CommonPlayer cpall : CraftCommonPlayer.SET)
		{
			Player all = cpall.getPlayer();

			File allPlayerFile = CommonsConfig.getFile(Type.PLAYER, false, cpall.getUUID());
			YamlConfiguration allPlayerConfig = YamlConfiguration.loadConfiguration(allPlayerFile);
			
			if(allPlayerConfig.getStringList("ignored-players").contains(playerUUID))
			{
				continue;
			}

			all.sendMessage(msg);
		}

		Bukkit.getConsoleSender().sendMessage(msg);
	}	
}