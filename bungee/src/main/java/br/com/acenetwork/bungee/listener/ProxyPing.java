package br.com.acenetwork.bungee.listener;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.com.acenetwork.bungee.manager.Config;
import br.com.acenetwork.bungee.manager.Config.Type;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.md_5.bungee.event.EventHandler;

public class ProxyPing implements Listener
{
	private static final Random R = new Random();
	
	@EventHandler
	public void a(ProxyPingEvent e)
	{
		File file = Config.getFile(Type.MOTD, true);
		ConfigurationProvider provider = ConfigurationProvider.getProvider(YamlConfiguration.class);
		
		try
		{
			Configuration config = provider.load(file);
			
			List<String> list = config.getStringList("motd");
			
			TextComponent text = new TextComponent(hexColor(list.get(R.nextInt(list.size()))));
			e.getResponse().setDescriptionComponent(text);
		}
		catch(IOException e1)
		{
			e1.printStackTrace();
		}
	}
	
	public String hexColor(String text)
	{
	    Pattern pattern = Pattern.compile("#[a-fA-f0-9]{6}");
	    Matcher matcher = pattern.matcher(text);
        
	    while (matcher.find())
        {
            String color = text.substring(matcher.start(), matcher.end());
            text = text.replace(color, ChatColor.of(color) + "");
        }
	    
	    return ChatColor.translateAlternateColorCodes('&', text);
	}
}