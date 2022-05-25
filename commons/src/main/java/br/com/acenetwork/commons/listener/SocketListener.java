package br.com.acenetwork.commons.listener;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import br.com.acenetwork.commons.event.SocketEvent;

public class SocketListener implements Listener
{
//	@EventHandler
//	public synchronized void a(SocketEvent e)
//	{
//		Socket s = e.getSocket();
//		
//		try(DataInputStream in = new DataInputStream(s.getInputStream()))
//		{
//			String cmd = in.readUTF();
//			
//			if(cmd.equals("broadcast"))
//			{
//				Bukkit.broadcastMessage(in.readUTF());
//			}
//		}
//		catch(IOException ex)
//		{
//			ex.printStackTrace();
//		}
//	}
}