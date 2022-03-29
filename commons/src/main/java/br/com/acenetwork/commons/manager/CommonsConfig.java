package br.com.acenetwork.commons.manager;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.YamlConfiguration;

import br.com.acenetwork.commons.Commons;

public class CommonsConfig
{
	public enum Type
	{
		CLANS_JSON, MESSAGE, GROUP, USER, NAMES, PLAYER, BANNED_PLAYERS, BANNED_IPS, MUTED_PLAYERS;
	}

	public static File getFile(Type type, boolean createNewFile, Object... args)
	{
		File file;
		YamlConfiguration config = null;

		switch(type)
		{
			case CLANS_JSON:
				file = new File(Commons.getDataFolder(), "ace_clans.json");
				break;
			case MESSAGE:
				file = new File(Commons.getDataFolder() + "/messages", args[0] + ".yml");
				break;
			case MUTED_PLAYERS:
				file = new File(Commons.getDataFolder() + "/muted-players", args[0] + ".yml");
				break;
			case BANNED_IPS:
				file = new File(Commons.getDataFolder() + "/banned-ips", args[0] + ".yml");
				break;
			case BANNED_PLAYERS:
				file = new File(Commons.getDataFolder() + "/banned-players", args[0] + ".yml");
				break;
			case GROUP:
				file = new File(Commons.getDataFolder() + "/permissions/groups", args[0] + ".yml");
				break;
			case USER:
				file = new File(Commons.getDataFolder() + "/permissions/users", args[0] + ".yml");
				break;
			case NAMES:
				file = new File(Commons.getDataFolder() + "/names", args[0] + ".yml");
				break;
			case PLAYER:
				file = new File(Commons.getDataFolder() + "/players", args[0] + ".yml");
				break;
			default:
				return null;
		}
		
		file.toPath().getParent().toFile().mkdirs();
		
		if(createNewFile && !file.exists())
		{
			try
			{
				file.createNewFile();
				
				if(config == null)
				{
					config = YamlConfiguration.loadConfiguration(file);
				}
				
				config.save(file);
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}

		return file;
	}	
}