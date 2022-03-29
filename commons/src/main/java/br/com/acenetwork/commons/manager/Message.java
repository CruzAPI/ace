package br.com.acenetwork.commons.manager;

import java.io.File;

import org.bukkit.configuration.file.YamlConfiguration;

import br.com.acenetwork.commons.manager.CommonsConfig.Type;

public class Message
{
	public static String getMessage(String locale, String key, Object... args)
	{
		try
		{
			switch(locale)
			{
//				case "pt_br":
//					break;
				default:
					locale = "en_us";
					break;
			}
			
			File file = CommonsConfig.getFile(Type.MESSAGE, false, locale);
			YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
			
			String value = config.getString(key.replace('.', ':'));
			
			if(value == null)
			{
				return key;
			}
			
			for(int i = 0; i < args.length; i++)
			{
				value = value.replace("{" + i + "}", args[i] + "");
			}
			
			return value;
		}
		catch(Exception e)
		{
			return key;
		}
	}
}