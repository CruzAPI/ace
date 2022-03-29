package br.com.acenetwork.survival.manager;

import java.io.File;
import java.io.IOException;

import br.com.acenetwork.survival.Main;

import org.bukkit.configuration.file.YamlConfiguration;

public class Config
{
	public enum Type
	{
		COMBATLOG, PRICE, PLAYER, PLAYERS_FOLDER, DATABASE;
	}

	public static File getFile(Type type, boolean createNewFile, Object... args)
	{
		File file;
		YamlConfiguration config = null;

		switch(type)
		{
			case COMBATLOG:
				file = new File(Main.getInstance().getDataFolder() + "/combatlog", args[0] + ".yml");
				break;
			case DATABASE:
				file = new File(Main.getInstance().getDataFolder(), "database.yml");
				break;
			case PLAYERS_FOLDER:
				file = new File(Main.getInstance().getDataFolder() + "/players");
				break;
			case PLAYER:
				file = new File(Main.getInstance().getDataFolder() + "/players", args[0] + ".yml");
				config = YamlConfiguration.loadConfiguration(file);
				config.set("max-balance", 1000.0D);
				break;
			case PRICE:
				file = new File(Main.getInstance().getDataFolder(), "price.yml");
				break;
			default:
				return null;
		}

		if(config == null)
		{
			config = YamlConfiguration.loadConfiguration(file);
		}
		
		if(createNewFile && !file.exists())
		{
			file.toPath().getParent().toFile().mkdirs();
			
			try
			{
				file.createNewFile();
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