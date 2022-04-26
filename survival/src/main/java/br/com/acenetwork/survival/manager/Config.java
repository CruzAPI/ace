package br.com.acenetwork.survival.manager;

import java.io.File;
import java.io.IOException;

import br.com.acenetwork.survival.Main;

import org.bukkit.configuration.file.YamlConfiguration;

public class Config
{
	public enum Type
	{
		BOT, COMBATLOG, PRICE;
	}

	public static File getFile(Type type, boolean createNewFile, Object... args)
	{
		File file;
		YamlConfiguration config = null;

		switch(type)
		{
			case BOT:
				file = new File(Main.getInstance().getDataFolder(), "bot.yml");
				break;
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
				
				switch(type)
				{
				case BOT:
					config.set("min-delay", 3L * 60L * 20L);
					config.set("max-delay", 10L * 60L * 20L);
					config.set("min-percent", 10);
					config.set("max-percent", 50);
					break;
				default:
					break;
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