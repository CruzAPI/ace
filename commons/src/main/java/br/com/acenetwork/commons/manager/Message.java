package br.com.acenetwork.commons.manager;

import java.io.File;

import org.bukkit.configuration.file.YamlConfiguration;

import br.com.acenetwork.commons.manager.CommonsConfig.Type;

public class Message
{
	private final String locale;
	private final String key;
	private final Object[] args;
	
	public Message(String locale, String key, Object... args)
	{
		this.locale = locale;
		this.key = key;
		this.args = args;
	}
	
	public static String getMessage(String locale, String key, Object... args)
	{
		try
		{
			switch(locale)
			{
				case "pt_br":
					break;
				default:
					locale = "en_us";
					break;
			}
			
			File file = CommonsConfig.getFile(Type.MESSAGE, true, locale);
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
	
	public String getString()
	{
		return getMessage(locale, key, args);
	}
}