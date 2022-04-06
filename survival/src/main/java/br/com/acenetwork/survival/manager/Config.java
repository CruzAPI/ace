package br.com.acenetwork.survival.manager;

import java.io.File;
import java.io.IOException;

import br.com.acenetwork.survival.Main;

import org.bukkit.configuration.file.YamlConfiguration;

public class Config
{
	public enum Type
	{
		COMBATLOG, PRICE;
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
			case PRICE:
				file = new File(Main.getInstance().getDataFolder(), "price.yml");
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