package br.com.acenetwork.commons.manager;

import java.util.ArrayList;
import java.util.List;

import net.md_5.bungee.api.chat.TextComponent;

public class Message
{
	public static TextComponent getTextComponent(String pattern, TextComponent[] extra)
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
		
		String[] split = pattern.split("\\{[0-9][^}]*\\}");
		
		TextComponent base = new TextComponent();
		
		for(int i = 0; ; i++)
		{
			try
			{
				base.addExtra(split[i]);
				base.addExtra(extra[indexes.get(i)]);
			}
			catch(Exception e)
			{
				break;
			}
		}
		
		return base;
	}
	
	//TODO: REMOVE
	public static String getMessage(String locale, String string, Object... args)
	{
		return null;
	}
}