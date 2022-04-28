package br.com.acenetwork.bungee.listener;

import java.io.File;
import java.io.IOException;

import br.com.acenetwork.bungee.Util;
import br.com.acenetwork.bungee.manager.Config;
import br.com.acenetwork.bungee.manager.Config.Type;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.md_5.bungee.event.EventHandler;

public class PostLogin implements Listener
{
	@SuppressWarnings("deprecation")
	@EventHandler
	public void on(PostLoginEvent e)
	{
		Util.sendDataPlayerCount(ProxyServer.getInstance().getPlayers().size());
		
		ProxiedPlayer p = e.getPlayer();
		
		ConfigurationProvider provider = ConfigurationProvider.getProvider(YamlConfiguration.class);
		
		File playerFile = Config.getFile(Type.PLAYER, false, p.getUniqueId());

		try
		{
			playerFile.getParentFile().mkdirs();
			
			boolean newFile = playerFile.createNewFile();
			Configuration playerConfig = provider.load(playerFile);

			if(newFile)
			{
				//TODO
			}

			String ip = p.getAddress().getAddress().toString();

			playerConfig.set("ip", ip);
			playerConfig.set("name", p.getName());
			provider.save(playerConfig, playerFile);
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
		}
	}
}