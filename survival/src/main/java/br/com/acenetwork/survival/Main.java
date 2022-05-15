package br.com.acenetwork.survival;

import java.util.Locale;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import br.com.acenetwork.commons.Commons;
import br.com.acenetwork.survival.executor.Buy;
import br.com.acenetwork.survival.executor.Delhome;
import br.com.acenetwork.survival.executor.Home;
import br.com.acenetwork.survival.executor.Price;
import br.com.acenetwork.survival.executor.Randomtp;
import br.com.acenetwork.survival.executor.Reset;
import br.com.acenetwork.survival.executor.Sell;
import br.com.acenetwork.survival.executor.Sellall;
import br.com.acenetwork.survival.executor.Sethome;
import br.com.acenetwork.survival.executor.Spawn;
import br.com.acenetwork.survival.executor.Temp;
import br.com.acenetwork.survival.executor.Track;
import br.com.acenetwork.survival.listener.AlertOreFound;
import br.com.acenetwork.survival.listener.EntityTarget;
import br.com.acenetwork.survival.listener.PlayerDeath;
import br.com.acenetwork.survival.listener.PlayerJoin;
import br.com.acenetwork.survival.listener.PlayerLogin;
import br.com.acenetwork.survival.listener.PlayerMode;
import br.com.acenetwork.survival.listener.PlayerQuit;
import br.com.acenetwork.survival.listener.PlayerRespawn;
import br.com.acenetwork.survival.listener.SpawnProtection;
import br.com.acenetwork.survival.npc.RandomTpNPC;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.SpawnReason;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.SkinTrait;

public class Main extends JavaPlugin
{
	private static Main instance;

	
	@Override
	public void onEnable()
	{
		instance = this;
		
		Locale.setDefault(Locale.ENGLISH);		
		
		Bukkit.getPluginManager().registerEvents(new PlayerMode(), this);
		
		Commons.init(this);
		
		Bukkit.getPluginManager().registerEvents(new AlertOreFound(), this);
		Bukkit.getPluginManager().registerEvents(new EntityTarget(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerDeath(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerJoin(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerLogin(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerQuit(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerRespawn(), this);
		Bukkit.getPluginManager().registerEvents(new SpawnProtection(), this);
		
		Commons.registerCommand(new Temp(), "temp");
		
		Commons.registerCommand(new Buy(), "buy");
		Commons.registerCommand(new Delhome(), "delhome");
		Commons.registerCommand(new Home(), "home");
		Commons.registerCommand(new Price(), "price");
		Commons.registerCommand(new Randomtp(), "randomtp");		
		Commons.registerCommand(new Reset(), "reset");		
		Commons.registerCommand(new Sell(), "sell");
		Commons.registerCommand(new Sellall(), "sellall");
		Commons.registerCommand(new Sethome(), "sethome");
		Commons.registerCommand(new Spawn(), "spawn");
		Commons.registerCommand(new Track(), "track");
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(this, () ->
		{
			CitizensAPI.getNPCRegistry().deregisterAll();
			
			NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "Random Teleport");
			npc.spawn(new Location(Bukkit.getWorld("world"), -1.5D, 66.0D, 2.5D, -135.0F, 0.0F), SpawnReason.PLUGIN);
//			npc.setAlwaysUseNameHologram(true);
			npc.getOrAddTrait(SkinTrait.class).setSkinPersistent("4341a52e", 
					"n0RG1PvSiRc9V9od7D2wrAeuVBYcNU4EIbsAsmTPI/b2mhRNUiR6iJKcMX8u6JbjJE+2jO8RuEzjw7gHuhxk6pyW9oXvkNE4RnhDbZqLC0Y233PfKls6ChpHWQQzHrFNqyw2Yz8HZhRL0Y+BHG3y+3qRWSUJK7UdId9OQl1wpi5cDYWUfOcam6nvw7H1j4N+SZun/aMVEozUi6p8l3iRw0bTv3s6SQli06yF1I322YuWfrqxAwcEHCAlwVdLZNVcdnh5P8V2ymNf8zPKRfBtoXRXt3nJUPvyCUhb/8gxQjOZ/dBtKzo97/FfLTr23jh3K8epLMaizi/42ccHfMxPhFmWOc0EamcB8wJ2qqb5Kru00VVB0AAxYB5tpnflXuGJ5TV0uadf9wK7Isla+rLe4n6861qCqCDu3XlnDQxZtUEkVZjbJCvX7OmL7si0Td3VHBt9yZg7sE3Gy/GADtY1k/fcuXn1C9okyJJHknSiqPb1ENQurO/tXSrRW8rBW9iZLerDeIwoiEKtiIpYKzcpsRfF/xD/Ec4GCQDY3bBpzXzf66euEHh+0Ux9KEdQj8pMYZ0ghqfMfdr2zkSiZNdFsgeZc3oKdOet2Mxrrj4hbBmtdxHFjR9lHc64M4tJX4NG8Os6BwYPV6c6AAXp2L12vKg6LN/6zK2s02Nz5iwmogM=",
					"ewogICJ0aW1lc3RhbXAiIDogMTY1MjEyNzQyMjU0NiwKICAicHJvZmlsZUlkIiA6ICJmZTYxY2RiMjUyMTA0ODYzYTljY2E2ODAwZDRiMzgzZSIsCiAgInByb2ZpbGVOYW1lIiA6ICJNeVNoYWRvd3MiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjM2MDMwZDU4ZDU5Mzk4M2EwNmQzMWQ0YjUzZGI0YzQ2YmFiYTY0OTNjYmU2MzU2YWUyYWI5ODRlNWFjMTdkIgogICAgfQogIH0KfQ==");
//			npc.data().set(NPC, UUID.fromString("3e740e01-e4bc-43d1-b89f-1b55b90bd38e"));
//			npc.data().set(NPC.PLAYER_SKIN_TEXTURE_PROPERTIES_METADATA, "ewogICJ0aW1lc3RhbXAiIDogMTY1MjEyNzQyMjU0NiwKICAicHJvZmlsZUlkIiA6ICJmZTYxY2RiMjUyMTA0ODYzYTljY2E2ODAwZDRiMzgzZSIsCiAgInByb2ZpbGVOYW1lIiA6ICJNeVNoYWRvd3MiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjM2MDMwZDU4ZDU5Mzk4M2EwNmQzMWQ0YjUzZGI0YzQ2YmFiYTY0OTNjYmU2MzU2YWUyYWI5ODRlNWFjMTdkIgogICAgfQogIH0KfQ==");
//			npc.data().set(NPC.PLAYER_SKIN_TEXTURE_PROPERTIES_SIGN_METADATA, "n0RG1PvSiRc9V9od7D2wrAeuVBYcNU4EIbsAsmTPI/b2mhRNUiR6iJKcMX8u6JbjJE+2jO8RuEzjw7gHuhxk6pyW9oXvkNE4RnhDbZqLC0Y233PfKls6ChpHWQQzHrFNqyw2Yz8HZhRL0Y+BHG3y+3qRWSUJK7UdId9OQl1wpi5cDYWUfOcam6nvw7H1j4N+SZun/aMVEozUi6p8l3iRw0bTv3s6SQli06yF1I322YuWfrqxAwcEHCAlwVdLZNVcdnh5P8V2ymNf8zPKRfBtoXRXt3nJUPvyCUhb/8gxQjOZ/dBtKzo97/FfLTr23jh3K8epLMaizi/42ccHfMxPhFmWOc0EamcB8wJ2qqb5Kru00VVB0AAxYB5tpnflXuGJ5TV0uadf9wK7Isla+rLe4n6861qCqCDu3XlnDQxZtUEkVZjbJCvX7OmL7si0Td3VHBt9yZg7sE3Gy/GADtY1k/fcuXn1C9okyJJHknSiqPb1ENQurO/tXSrRW8rBW9iZLerDeIwoiEKtiIpYKzcpsRfF/xD/Ec4GCQDY3bBpzXzf66euEHh+0Ux9KEdQj8pMYZ0ghqfMfdr2zkSiZNdFsgeZc3oKdOet2Mxrrj4hbBmtdxHFjR9lHc64M4tJX4NG8Os6BwYPV6c6AAXp2L12vKg6LN/6zK2s02Nz5iwmogM=");
//			npc.setName("Random Teleport");
			new RandomTpNPC(npc);
		}, 1L);
		
		for(Player all : Bukkit.getOnlinePlayers())
		{
			all.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
		}
	}
	
	@Override
	public void onDisable()
	{
		CitizensAPI.getNPCRegistry().forEach(x -> PlayerQuit.removeCombatLogger(x));
	}
	
	
	
	public static Main getInstance()
	{
		return instance;
	}
}