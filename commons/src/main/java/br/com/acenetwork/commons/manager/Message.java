package br.com.acenetwork.commons.manager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.YamlConfiguration;

import br.com.acenetwork.commons.manager.CommonsConfig.Type;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

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
	
	public static TextComponent getTextComponent(String pattern, TextComponent... extra)
	{
		List<Integer> indexes = new ArrayList<>();
		
		for(int i = 0; i < pattern.length(); i++)
		{
			try
			{
				if(pattern.charAt(i) == '{')
				{
					indexes.add(Integer.valueOf(pattern.charAt(i + 1) + "")); 
				}
			}
			catch(Exception e)
			{
				continue;
			}
		}
		
		TextComponent text = new TextComponent();
		
		String[] split = pattern.split("\\{[0-9][^}]*\\}");
		
		for(int i = 0; ; i++)
		{
			try
			{
				text.addExtra(split[i]);
				text.addExtra(extra[indexes.get(i)]);
			}
			catch(Exception e)
			{
				break;
			}
		}
		
		return text;
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