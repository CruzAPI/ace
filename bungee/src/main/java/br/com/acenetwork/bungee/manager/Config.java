package br.com.acenetwork.bungee.manager;

import java.io.File;
import java.io.IOException;

import br.com.acenetwork.bungee.Main;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class Config
{
	public enum Type
	{
		BALANCE_FOLDER, BALANCE_RAID_PLAYER, BALANCE_RAID_FOLDER, BALANCE_TNTRUN_PLAYER, BALANCE_TNTRUN_FOLDER, 
		DATABASE, RESET, MESSAGE, GROUP, USER, NAMES, PLAYER, BANNED_IPS, BANNED_PLAYERS;
	}

	public static File getFile(Type type, boolean createNewFile, Object... args)
	{
		File file;
		ConfigurationProvider provider = ConfigurationProvider.getProvider(YamlConfiguration.class);
		Configuration config = null;
		
		switch(type)
		{
		case BALANCE_FOLDER:
			file = new File(Main.getCommonsDataFolder() + "/balance");
			break;
		case BALANCE_RAID_FOLDER:
			file = new File(Main.getCommonsDataFolder() + "/balance/raid");
			break;
		case BALANCE_TNTRUN_FOLDER:
			file = new File(Main.getCommonsDataFolder() + "/balance/tntrun");
			break;
		case BALANCE_RAID_PLAYER:
			file = new File(Main.getCommonsDataFolder() + "/balance/raid", args[0] + ".yml");
			break;
		case BALANCE_TNTRUN_PLAYER:
			file = new File(Main.getCommonsDataFolder() + "/balance/tntrun", args[0] + ".yml");
			break;
		case DATABASE:
			file = new File(Main.getCommonsDataFolder(), "database.yml");
			break;
		case RESET:
			file = new File(Main.getCommonsDataFolder(), "reset.yml");
			break;
		case MESSAGE:
			file = new File(Main.getCommonsDataFolder() + "/messages", args[0] + ".yml");
			break;
		case BANNED_IPS:
			file = new File(Main.getCommonsDataFolder() + "/banned-ips", args[0] + ".yml");
			break;
		case BANNED_PLAYERS:
			file = new File(Main.getCommonsDataFolder() + "/banned-players", args[0] + ".yml");
			break;
		case PLAYER:
			file = new File(Main.getCommonsDataFolder() + "/players", args[0] + ".yml");
			break;
		case GROUP:
			file = new File(Main.getCommonsDataFolder() + "/permissions/groups", args[0] + ".yml");
			break;
		case USER:
			file = new File(Main.getCommonsDataFolder() + "/permissions/users", args[0] + ".yml");
			break;
		case NAMES:
			file = new File(Main.getCommonsDataFolder() + "/names", args[0] + ".yml");
			break;
		default:
			return null;
		}
		
		if(createNewFile && !file.exists())
		{
			if(type.toString().contains("FOLDER"))
			{
				file.mkdirs();
				return file;
			}
			
			file.getParentFile().mkdirs();
			
			try
			{
				file.createNewFile();
				
				if(config == null)
				{
					config = provider.load(file);
					
					switch(type)
					{
					case BALANCE_RAID_PLAYER:
						config.set("max-balance", 1000.0D);
						break;
					case BALANCE_TNTRUN_PLAYER:
						config.set("max-balance", 200.0D);
						break;
					default:
						break;
					}
				}
				
				provider.save(config, file);
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}

		return file;
	}	
}