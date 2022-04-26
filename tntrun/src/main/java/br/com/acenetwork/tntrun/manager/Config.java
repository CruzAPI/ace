package br.com.acenetwork.tntrun.manager;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import br.com.acenetwork.tntrun.Main;

public class Config
{
	public enum Type
	{
		TNTRUN_CONFIG;
	}
	
	public static File getFile(Type type, boolean createNewFile, Object... args)
	{
		File file;
		YamlConfiguration config = null;
		
		switch(type)
		{
		case TNTRUN_CONFIG:
			file = new File(Main.getInstance().getDataFolder(), "config.yml");
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
				
				config.set("min-players", 4);
				config.set("max-players", 16);
				config.set("spawn-location", new Location(Bukkit.getWorld("world"), 0.5D, 71.5D, 0.5D, 0.0F, 0.0F));
				config.set("max-timer", 120L);
				config.set("first-prize", 25);
				config.set("seconds-prize", 10);
				config.set("third-prize", 5);
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