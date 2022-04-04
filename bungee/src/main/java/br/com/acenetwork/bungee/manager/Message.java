package br.com.acenetwork.bungee.manager;

import java.io.File;

import br.com.acenetwork.bungee.manager.Config.Type;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

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
			
			ConfigurationProvider provider = ConfigurationProvider.getProvider(YamlConfiguration.class);
			File file = Config.getFile(Type.MESSAGE, false, locale);
			Configuration config = provider.load(file);
			
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