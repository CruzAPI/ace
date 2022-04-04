package br.com.acenetwork.commons;

import java.io.File;
import java.io.FileReader;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import br.com.acenetwork.commons.manager.CommonsConfig;
import br.com.acenetwork.commons.manager.CommonsConfig.Type;

public class CommonsUtil
{
	public static boolean hasPermission(String uuid, String perm)
	{
		perm = perm.replace('.', ':');

		File userFile = CommonsConfig.getFile(Type.USER, true, uuid);
		YamlConfiguration userConfig = YamlConfiguration.loadConfiguration(userFile);

		ConfigurationSection userPermissions = userConfig.getConfigurationSection("permission");

		if(userPermissions != null)
		{
			for(String key : userPermissions.getKeys(false))
			{
				long value = userConfig.getLong("permission." + key);
				boolean valid = value == 0 || value > System.currentTimeMillis();

				if(valid && (key.endsWith("*") && perm.startsWith(key.substring(0, key.length() - 1)) || 
					perm.equals(key)))
				{
					return true;
				}
			}
		}
		
		ConfigurationSection userGroups = userConfig.getConfigurationSection("group");
		
		if(userGroups != null)
		{
			for(String key : userGroups.getKeys(false))
			{
				long value = userConfig.getLong("group." + key);
				boolean valid = value == 0 || value > System.currentTimeMillis();

				if(valid)
				{
					File groupFile = CommonsConfig.getFile(Type.GROUP, true, key);
					YamlConfiguration groupConfig = YamlConfiguration.loadConfiguration(groupFile);

					ConfigurationSection groupPermissions = groupConfig.getConfigurationSection("permission");

					if(groupPermissions != null)
					{
						for(String key1 : groupPermissions.getKeys(false))
						{
							value = groupConfig.getLong("permisison." + key1);
							valid = value == 0 || value > System.currentTimeMillis();
							
							if(valid && (key1.endsWith("*") && perm.startsWith(key1.substring(0, key1.length() - 1)) || 
								perm.equals(key1)))
							{
								return true;
							}
						}
					}
				}
			}
		}

		return false;
	}
	
	public static void test()
	{
		JsonParser parser = new JsonParser();
		
		try
		{			
			JsonArray array = (JsonArray) parser.parse(new FileReader(CommonsConfig.getFile(Type.CLANS_JSON, false)));
			
			for(Object o : array)
			{
				JsonObject json = (JsonObject) o;
				
				String username = json.get("Username").getAsString();
				String clan = json.get("Clan").getAsString();
				
				String uuid = CommonsUtil.getUUIDByName(username);
				
				File file = CommonsConfig.getFile(Type.PLAYER, false, uuid);
				YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
				
				if(file.exists())
				{
					config.set("clan", clan);
					config.save(file);
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
//		Gson gson = new Gson();
//		
//		File file = CommonsConfig.getFile(Type.CLANS, false);
//		
//		try
//		{
//			FileReader reader = new FileReader(file);
//			JsonReader jr = gson. newJsonReader(reader);
//			jr.
//			while(jr.hasNext())
//			{
//				jr.
//				Bukkit.broadcastMessage("" + jr.getPath());
//			}
//		}
//		catch(IOException e)
//		{
//			e.printStackTrace();
//		}
	}
	
	public static boolean permissionSyntaxIsValid(String perm)
	{
		if(!perm.matches("[a-z.*]{0,30}") || perm.startsWith(".") || perm.endsWith("."))
		{
			return false;
		}

		if(perm.contains("*") && !perm.equals("*") && !perm.endsWith(".*"))
		{
			return false;
		}

		for(int i = 0; i + 1 < perm.length(); i++)
		{
			if((perm.charAt(i) == '.' && perm.charAt(i + 1) == '.') || perm.charAt(i) == '*')
			{
				return false;
			}
		}

		return true;
	}

	public static void bungeeKickPlayer(String playerName, String kickMessage)
	{
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		
		out.writeUTF("kick");
		out.writeUTF(playerName);
		out.writeUTF(kickMessage);
		
		Bukkit.getServer().sendPluginMessage(Commons.getPlugin(), "commons:commons", out.toByteArray());
	}

	public static String getUUIDByName(String name)
	{
		File namesFile = CommonsConfig.getFile(Type.NAMES, false, name.toLowerCase());
		YamlConfiguration namesConfig = YamlConfiguration.loadConfiguration(namesFile);

		return namesConfig.getString("uuid");
	}

	public static String getNameByUUID(String uuid)
	{
		File playerFile = CommonsConfig.getFile(Type.PLAYER, false, uuid);
		YamlConfiguration playerConfig = YamlConfiguration.loadConfiguration(playerFile);

		return playerConfig.getString("name");
	}

	public static boolean groupSyntaxIsValid(String group)
	{
		return group.matches("[a-z]{0,16}");
	}
}