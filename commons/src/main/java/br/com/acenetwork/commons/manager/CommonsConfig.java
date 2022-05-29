package br.com.acenetwork.commons.manager;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.YamlConfiguration;

import br.com.acenetwork.commons.Commons;

public class CommonsConfig
{
	public enum Type
	{
		BALANCE_FOLDER, BALANCE_RAID_PLAYER, BALANCE_RAID_FOLDER, 
		CLANS_JSON, MESSAGE, GROUP, USER, 
		PLAYER, 
		BANNED_PLAYERS, BANNED_IPS, MUTED_PLAYERS, DATABASE;
	}

	public static File getFile(Type type, boolean createNewFile, Object... args)
	{
		File file;
		YamlConfiguration config = null;

		switch(type)
		{
		case DATABASE:
			file = new File(Commons.getDataFolder(), "database.yml");
			break;
		case BALANCE_FOLDER:
			file = new File(Commons.getDataFolder() + "/balance");
			break;
		case BALANCE_RAID_FOLDER:
			file = new File(Commons.getDataFolder() + "/balance/raid");
			break;
		case BALANCE_RAID_PLAYER:
			file = new File(Commons.getDataFolder() + "/balance/raid", args[0] + ".yml");
			config = YamlConfiguration.loadConfiguration(file);
			config.set("max-balance", 3000.0D);
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
		case PLAYER:
			file = new File(Commons.getDataFolder() + "/players", args[0] + ".yml");
			break;
		default:
			return null;
		}
		
		if(createNewFile && !file.exists())
		{
			file.toPath().getParent().toFile().mkdirs();
			
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