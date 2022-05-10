package br.com.acenetwork.survival.player.craft;

import java.io.File;
import java.io.IOException;
import java.util.ResourceBundle;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.entity.Pose;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPoseChangeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import br.com.acenetwork.commons.CommonsUtil;
import br.com.acenetwork.commons.manager.Message;
import br.com.acenetwork.commons.player.CommonPlayer;
import br.com.acenetwork.commons.player.craft.CraftCommonPlayer;
import br.com.acenetwork.survival.Main;
import br.com.acenetwork.survival.executor.Spawn;
import br.com.acenetwork.survival.manager.Config;
import br.com.acenetwork.survival.manager.Config.Type;
import br.com.acenetwork.survival.player.SurvivalPlayer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;

public class CraftSurvivalPlayer extends CraftCommonPlayer implements SurvivalPlayer
{
	private boolean blindness;
	private boolean spawnProtection;
	
	
	private int spawnTaskId;
	private long spawnTaskTicks;
	private long spawnTaskTotalTicks;
	
	public CraftSurvivalPlayer(Player p)
	{
		super(p);
	}
	
	@Override
	public void reset()
	{
		setInvis(false);
		
		p.setCollidable(true);
		p.setGameMode(GameMode.SURVIVAL);
	}
	
	@Override
	public boolean cancelChannelSpawn(boolean force)
	{
		if(spawnTaskId == 0 || (!force && spawnTaskTotalTicks - spawnTaskTicks <= 35L))
		{
			return false;
		}
		
		Bukkit.getScheduler().cancelTask(spawnTaskId);
		spawnTaskId = 0;
		
		ResourceBundle bundle = ResourceBundle.getBundle("message", CommonsUtil.getLocaleFromMinecraft(p.getLocale()));
		
		p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent());
		
		TextComponent text = new TextComponent(bundle.getString("raid.cmd.spawn.teleport-cancelled"));
		text.setColor(ChatColor.RED);
		p.spigot().sendMessage(text);
		
		return true;
	}
	
	@Override
	public void channelSpawn()
	{
		if(spawnTaskId != 0)
		{
			return;
		}
		
		ResourceBundle bundle = ResourceBundle.getBundle("message", getLocale());
		boolean safe = true;
		
		for(SurvivalPlayer spall : CraftCommonPlayer.getAll(SurvivalPlayer.class))
		{
			if(spall == this)
			{
				continue;
			}
			
			Player all = spall.getPlayer();
			
			if(p.getWorld() == all.getWorld() && p.getLocation().distance(all.getLocation()) < 100.0D)
			{
				safe = false;
				break;
			}
		}
		
		final long channelingTicks = safe ? Spawn.CHANNELING_TICKS : Spawn.LONG_CHANNELING_TICKS;
		
		TextComponent[] extra = new TextComponent[1];
		
		ChatColor textColor = ChatColor.GREEN;
		
		long seconds = channelingTicks / 20L;
		
		extra[0] = new TextComponent(seconds + " ");
		extra[0].setColor(ChatColor.YELLOW);
		extra[0].addExtra(new ComponentBuilder(bundle.getString("commons.words.seconds")).color(textColor).getCurrentComponent());
		
		TextComponent text = Message.getTextComponent(bundle.getString("raid.cmd.spawn.channeling"), extra);
		text.setColor(textColor);
		p.spigot().sendMessage(text);
		
		spawnTaskTicks = channelingTicks;
		spawnTaskTotalTicks = spawnTaskTicks;
		
		spawnTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), new Runnable()
		{
			final int tiles = 40;
			Location location = p.getLocation().clone();
			
			@Override
			public void run()
			{
				float f = (float) spawnTaskTicks / (float) spawnTaskTotalTicks;
				int redTiles = (int) (f * tiles);
				
				TextComponent extra = new TextComponent(StringUtils.repeat('▍', redTiles));
				extra.setColor(ChatColor.RED);
				
				TextComponent text = new TextComponent(StringUtils.repeat('▍', tiles - redTiles));
				text.setColor(ChatColor.GREEN);
				text.addExtra(extra);
				
				p.spigot().sendMessage(ChatMessageType.ACTION_BAR, text);
				
				if(p.getWorld() != location.getWorld() || p.getLocation().getX() != location.getX() 
						|| p.getLocation().getY() != location.getY() || p.getLocation().getZ() != location.getZ())
				{
					if(cancelChannelSpawn(false))
					{
						return;
					}
					
					location = p.getLocation().clone();
				}
				
				if(spawnTaskTicks <= 0 && spawnTaskId != 0)
				{
					Bukkit.getScheduler().cancelTask(spawnTaskId);
					spawnTaskId = 0;
					
					p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent());
					p.teleport(Spawn.SPAWN_LOCATION);
					setSpawnProtection(true);
					return;
				}
				
				spawnTaskTicks--;
			}
		}, 0L, 1L);
	}
	
	@Override
	public void setSpawnProtection(boolean spawnProtection)
	{
		if(this.spawnProtection != (this.spawnProtection = spawnProtection))
		{
			ResourceBundle bundle = ResourceBundle.getBundle("message", getLocale());
			
			if(spawnProtection)
			{
				TextComponent text = new TextComponent(bundle.getString("raid.cmd.spawn.protection-enabled"));
				text.setColor(ChatColor.GREEN);
				p.spigot().sendMessage(text);
			}
			else
			{
				TextComponent text = new TextComponent(bundle.getString("raid.cmd.spawn.protection-disabled"));
				text.setColor(ChatColor.GREEN);
				p.spigot().sendMessage(text);
			}
		}
	}
	
	@Override
	public boolean hasSpawnProtection()
	{
		return spawnProtection;
	}
	
	@EventHandler
	public void on(PlayerRespawnEvent e)
	{
		if(e.getPlayer() != p)
		{
			return;
		}
		
		Location bedSpawnLocation = p.getBedSpawnLocation();
		
		if(bedSpawnLocation != null)
		{
			e.setRespawnLocation(bedSpawnLocation);
			return;
		}
		
		e.setRespawnLocation(Spawn.SPAWN_LOCATION);
		setSpawnProtection(true);
	}
	
	@EventHandler
	public void a(FoodLevelChangeEvent e)
	{
		if(e.getEntity() != p)
		{
			return;
		}
		
		if(hasSpawnProtection() && e.getFoodLevel() < p.getFoodLevel())
		{
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void on1(EntityDamageEvent e)
	{
		if(e.getEntity() != p)
		{
			return;
		}
		
		if(hasSpawnProtection())
		{
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void on1(ProjectileLaunchEvent e)
	{
		if(e.getEntity().getShooter() != p)
		{
			return;
		}
		
		setSpawnProtection(false);
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void on1a(EntityDamageByEntityEvent e)
	{
		if(e.getDamager() != p)
		{
			return;
		}
		
		if(e.getEntity() instanceof Player)
		{
			if(!e.isCancelled())
			{
				setSpawnProtection(false);
			}
		}
	}
	
	@EventHandler
	public void on1(EntityDamageByEntityEvent e)
	{
		if(e.getDamager() != p)
		{
			return;
		}
		
		if(e.getEntity() instanceof Player)
		{
			Player p = (Player) e.getEntity();
			CommonPlayer cp = CraftCommonPlayer.get(p);
			
			if(cp instanceof SurvivalPlayer)
			{
				SurvivalPlayer sp = (SurvivalPlayer) cp;
				
				if(sp.hasSpawnProtection())
				{
					e.setCancelled(true);
					return;
				}
			}
		}
		else
		{
			if(hasSpawnProtection())
			{
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void a(PlayerItemConsumeEvent e)
	{
		if(e.getPlayer() != p)
		{
			return;
		}
		
		if(blindness && e.getItem().getType() == Material.MILK_BUCKET)
		{
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void a(EntityPoseChangeEvent e)
	{
		if(e.getEntity() != p)
		{
			return;
		}
		
		if(blindness)
		{
			blindness = false;
			p.removePotionEffect(PotionEffectType.BLINDNESS);
			return;
		}
		
		if(e.getPose() == Pose.SWIMMING && p.getLocation().getBlock().getType() == Material.COMPOSTER)
		{
			blindness = true;
			p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 99999, 5));
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void aa(PlayerInteractEvent e)
	{
		if(e.getPlayer() != p)
		{
			return;
		}
		
		cancelChannelSpawn(false);
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void aa(EntityDamageEvent e)
	{
		if(e.getEntity() != p)
		{
			return;
		}
		
		cancelChannelSpawn(false);
	}
	
	@EventHandler
	public void a(PlayerDeathEvent e)
	{
		if(e.getEntity() != p)
		{
			return;
		}
		
		cancelChannelSpawn(true);
	}
	
	@EventHandler
	public void a(PlayerQuitEvent e)
	{
		if(e.getPlayer() != p)
		{
			return;
		}
		
		cancelChannelSpawn(true);
		
		File playerFile = Config.getFile(Type.PLAYER_INFO, true, p.getUniqueId());
		YamlConfiguration playerConfig = YamlConfiguration.loadConfiguration(playerFile);
		
		playerConfig.set("spawn-protection", spawnProtection);
		
		try
		{
			playerConfig.save(playerFile);
		}
		catch(IOException e1)
		{
			e1.printStackTrace();
		}
	}
}
