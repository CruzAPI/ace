package br.com.acenetwork.commons;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketImpl;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import br.com.acenetwork.commons.event.PlayerModeEvent;
import br.com.acenetwork.commons.event.SocketEvent;
import br.com.acenetwork.commons.executor.AdminCMD;
import br.com.acenetwork.commons.executor.BTA;
import br.com.acenetwork.commons.executor.Balance;
import br.com.acenetwork.commons.executor.BanCMD;
import br.com.acenetwork.commons.executor.BroadcastCMD;
import br.com.acenetwork.commons.executor.Build;
import br.com.acenetwork.commons.executor.ChatCMD;
import br.com.acenetwork.commons.executor.ChatClean;
import br.com.acenetwork.commons.executor.Ignore;
import br.com.acenetwork.commons.executor.Invis;
import br.com.acenetwork.commons.executor.Invsee;
import br.com.acenetwork.commons.executor.MuteCMD;
import br.com.acenetwork.commons.executor.Mutebroadcast;
import br.com.acenetwork.commons.executor.Pardon;
import br.com.acenetwork.commons.executor.Permission;
import br.com.acenetwork.commons.executor.Ping;
import br.com.acenetwork.commons.executor.Reply;
import br.com.acenetwork.commons.executor.Specs;
import br.com.acenetwork.commons.executor.Stop;
import br.com.acenetwork.commons.executor.TagCMD;
import br.com.acenetwork.commons.executor.Tell;
import br.com.acenetwork.commons.executor.Test;
import br.com.acenetwork.commons.executor.Tp;
import br.com.acenetwork.commons.executor.Unmute;
import br.com.acenetwork.commons.executor.Wallet;
import br.com.acenetwork.commons.executor.WatchCMD;
import br.com.acenetwork.commons.listener.EntitySpawn;
import br.com.acenetwork.commons.listener.InventoryClose;
import br.com.acenetwork.commons.listener.PlayerChat;
import br.com.acenetwork.commons.listener.PlayerDeath;
import br.com.acenetwork.commons.listener.PlayerJoin;
import br.com.acenetwork.commons.listener.PlayerLogin;
import br.com.acenetwork.commons.listener.PlayerQuit;
import br.com.acenetwork.commons.listener.SocketListener;
import br.com.acenetwork.commons.manager.Broadcast;
import br.com.acenetwork.commons.player.CommonPlayer;
import br.com.acenetwork.commons.player.craft.CraftCommonPlayer;

public class Commons
{
	private static JavaPlugin plugin;
	private static boolean restarting;
	public static final boolean TEST = !new File(System.getProperty("user.dir")).getParentFile().getName().equals("acenetwork");
	
	public static void init(JavaPlugin plugin)
	{
		Commons.plugin = plugin;
		
		Locale.setDefault(new Locale("en", "US"));
		
		if(TEST)
		{
			Bukkit.getConsoleSender().sendMessage("§dServer is in test mode!");
		}
		else
		{
			Bukkit.getConsoleSender().sendMessage("§aServer is in production!");
		}
		
		Bukkit.getMessenger().registerIncomingPluginChannel(plugin, "commons:commons", new PluginMessage());
		Bukkit.getMessenger().registerOutgoingPluginChannel(plugin, "commons:commons");
		
		Bukkit.getPluginManager().registerEvents(new EntitySpawn(), plugin);
		Bukkit.getPluginManager().registerEvents(new InventoryClose(), plugin);
		Bukkit.getPluginManager().registerEvents(new PlayerChat(), plugin);
		Bukkit.getPluginManager().registerEvents(new PlayerDeath(), plugin);
		Bukkit.getPluginManager().registerEvents(new PlayerJoin(), plugin);
		Bukkit.getPluginManager().registerEvents(new PlayerLogin(), plugin);
		Bukkit.getPluginManager().registerEvents(new PlayerQuit(), plugin);
		Bukkit.getPluginManager().registerEvents(new SocketListener(), plugin);

		registerCommand(new AdminCMD(), "admin");
		registerCommand(new Balance(), "balance", "bal", "points", "coins");
		registerCommand(new BanCMD(), "ban");
		registerCommand(new BroadcastCMD(), "broadcast", "bc", "shout");
		registerCommand(new Build(), "build");
		registerCommand(new BTA(), "bta");
		registerCommand(new ChatClean(), "chatclean", "cc");
		registerCommand(new ChatCMD(), "chat");
		registerCommand(new Ignore(), "ignore");
		registerCommand(new Invis(), "invis", "v", "vanish");
		registerCommand(new Invis(), "vis");
		registerCommand(new Invsee(), "invsee");
		registerCommand(new Mutebroadcast(), "mutebroadcast");
		registerCommand(new MuteCMD(), "mute");
		registerCommand(new Pardon(), "pardon");
		registerCommand(new Permission(), "permission", "pex", "perm");
		registerCommand(new Ping(), "ping");
		registerCommand(new Reply(), "reply", "r");
		registerCommand(new Specs(), "specs");
		registerCommand(new Stop(), "stop");
		registerCommand(new TagCMD(), "tag");
		registerCommand(new Tell(), "tell", "msg", "t", "w", "whisper");
		registerCommand(new Test(), "test");
		registerCommand(new Tp(), "teleport", "tp");
		registerCommand(new Unmute(), "unmute");
		registerCommand(new WatchCMD(), "watch");
		registerCommand(new Wallet(), "wallet");
		
		for(Player all : Bukkit.getOnlinePlayers())
		{
			try
			{
				Bukkit.getPluginManager().callEvent(new PlayerModeEvent(all));
				
				CommonPlayer cp = CraftCommonPlayer.get(all);
				
				if(cp.hasPermission("cmd.admin"))
				{
					all.chat("/admin");
				}
				else if(cp.hasPermission("cmd.watch"))
				{
					all.chat("/watch");
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
		new Thread(() ->
		{
			while(true)
			{
				try(ServerSocket ss = new ServerSocket(getSocketPort()); Socket s = ss.accept();
						DataInputStream in = new DataInputStream(s.getInputStream()))
				{
					List<String> list = new ArrayList<>();
					
					while(true)
					{
						try
						{
							list.add(in.readUTF());
						}
						catch(EOFException ex)
						{
							break;
						}
					}
					
					Bukkit.getPluginManager().callEvent(new SocketEvent(list.toArray(String[]::new)));
				}
				catch(IOException e)
				{
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	public static int getSocketPort()
	{
		return Bukkit.getPort() + 5000;
	}

	public static void registerCommand(TabExecutor executor, String name, String... aliases)
	{
		try
		{
			final Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
			bukkitCommandMap.setAccessible(true);
			
			CommandMap commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());
			
			Class<?> clazz = PluginCommand.class;
			
			Constructor<?> constructor = clazz.getDeclaredConstructor(String.class, Plugin.class);
			constructor.setAccessible(true);
			
			PluginCommand command = (PluginCommand) constructor.newInstance(name, plugin);
			
			command.setAliases(Arrays.asList(aliases));
			command.register(commandMap);
			command.setExecutor(executor);
			command.setTabCompleter(executor);
			commandMap.register(command.getName(), command);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	public static JavaPlugin getPlugin()
	{
		return plugin;
	}
	
	public static File getDataFolder()
	{
		if(TEST)
		{
			return new File(System.getProperty("user.home") +  "/.aceconfigtest");
		}
		
		return new File(System.getProperty("user.home") +  "/.aceconfig");
	}

	public static void setRestarting(boolean value)
	{
		restarting = value;
	}
	
	public static boolean isRestarting()
	{
		return restarting;
	}
}