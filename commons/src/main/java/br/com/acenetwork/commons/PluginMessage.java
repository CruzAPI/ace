package br.com.acenetwork.commons;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import br.com.acenetwork.commons.event.BungeePlayerCountEvent;

public class PluginMessage implements PluginMessageListener
{
	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] msg)
	{
		if(channel.equalsIgnoreCase("commons:commons"))
		{
			ByteArrayDataInput in = ByteStreams.newDataInput(msg);
			
			String command = in.readUTF();
			
			if(command.equals("playercount"))
			{
				int count = in.readInt();
				
				Bukkit.getPluginManager().callEvent(new BungeePlayerCountEvent(count));
			}
		}
	}
}
