package br.com.acenetwork.bungee.manager;

import java.io.File;
import java.io.IOException;

import br.com.acenetwork.bungee.Main;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class Config
{
	public enum Type
	{
		MESSAGE, GROUP, USER, NAMES, PLAYER, BANNED_IPS, BANNED_PLAYERS;
	}

	public static File getFile(Type type, boolean createNewFile, Object... args)
	{
		File file;
		ConfigurationProvider provider = ConfigurationProvider.getProvider(YamlConfiguration.class);
		Configuration config = null;
		
		switch(type)
		{
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

		file.toPath().getParent().toFile().mkdirs();
		
		if(createNewFile && !file.exists())
		{
			try
			{
				file.createNewFile();
				
				if(config == null)
				{
					config = provider.load(file);
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