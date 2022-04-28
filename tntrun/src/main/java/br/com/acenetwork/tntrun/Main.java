package br.com.acenetwork.tntrun;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.FileUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import br.com.acenetwork.commons.Commons;
import br.com.acenetwork.commons.executor.Balance;
import br.com.acenetwork.commons.executor.Stop;
import br.com.acenetwork.commons.player.CommonPlayer;
import br.com.acenetwork.commons.player.craft.CraftCommonPlayer;
import br.com.acenetwork.commons.player.craft.CraftCommonWatcher;
import br.com.acenetwork.tntrun.constant.State;
import br.com.acenetwork.tntrun.event.PlayerCountChangeEvent;
import br.com.acenetwork.tntrun.event.StateChangeEvent;
import br.com.acenetwork.tntrun.event.TimerChangeEvent;
import br.com.acenetwork.tntrun.executor.Start;
import br.com.acenetwork.tntrun.executor.Timer;
import br.com.acenetwork.tntrun.listener.GeneralEvents;
import br.com.acenetwork.tntrun.listener.PlayerJoin;
import br.com.acenetwork.tntrun.listener.PlayerLogin;
import br.com.acenetwork.tntrun.listener.PlayerMode;
import br.com.acenetwork.tntrun.listener.TimerChange;
import br.com.acenetwork.tntrun.manager.Config;
import br.com.acenetwork.tntrun.manager.Config.Type;
import br.com.acenetwork.tntrun.player.Competitor;

public class Main extends JavaPlugin implements Listener
{
	private static final Map<Block, Integer> ACTIVATED_BLOCKS = new HashMap<>();
	private static final List<Competitor> ELIMINATED = new ArrayList<>();
	
	private static Main instance;
	private static boolean announceWinner;
	
	private static int timer;
	private static State state = State.STARTING;
	
	private static int taskA;
	private static int taskB;
	
	@Override
	public void onLoad()
	{
		Bukkit.getServer().unloadWorld("world", false);
		deleteDir(new File("world"));
		
		try
		{
			FileUtils.copyDirectory(new File("map"), new File("world"));
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	private void deleteDir(File file)
	{
		if(file.isDirectory())
		{
			String[] children = file.list();
			
			for(int i = 0; i < children.length; i++)
			{
				deleteDir(new File(file, children[i]));
			}
		}
		
		file.delete();
	}
	
	@Override
	public void onEnable()
	{
		instance = this;
		
		Bukkit.getPluginManager().registerEvents(new PlayerMode(), this);
		
		Commons.init(this);
		
		Commons.registerCommand(new Timer(), "timer");
		Commons.registerCommand(new Start(), "start");
		
		Bukkit.getPluginManager().registerEvents(this, this);
		Bukkit.getPluginManager().registerEvents(new TimerChange(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerJoin(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerLogin(), this);
		Bukkit.getPluginManager().registerEvents(new GeneralEvents(), this);
		
		World w = Bukkit.getWorld("world");
		
		w.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
		w.setGameRule(GameRule.DO_MOB_SPAWNING, false);
		w.setGameRule(GameRule.MOB_GRIEFING, false);
		w.setTime(18000L);
		w.setAutoSave(false);
		w.setDifficulty(Difficulty.PEACEFUL);
		
		timer = getMaxTimer();
	}
	
	public static void startTaskB()
	{
		taskB = Bukkit.getScheduler().scheduleSyncRepeatingTask(instance, new Runnable()
		{
			@Override
			public void run()
			{
				int players = CraftCommonPlayer.getAll(Competitor.class).size();
				
				if(announceWinner)
				{
					return;
				}
								
				switch(state)
				{
				case STARTING:
					if(players < getMinPlayers())
					{
						Main.setTime(getMaxTimer());
					}
					else
					{
						Main.setTime(timer - 1);
						
						if(players > getMaxPlayers() / 1.2 && timer > 20)
						{
							Main.setTime(20);
						}
						
						if(timer <= 0)
						{
							Main.setState(State.STARTED);
							
							for(Competitor cp : CraftCommonPlayer.getAll(Competitor.class))
							{
								Player p = cp.getPlayer();
								p.teleport(getSpawnLocation());
								p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0));
							}
						}
					}
					break;
				case STARTED:
					if(taskA == 0 && timer > 5)
					{
						taskA = Bukkit.getScheduler().scheduleSyncRepeatingTask(instance, () ->
						{
							for(Competitor cp : CraftCommonPlayer.getAll(Competitor.class))
							{
								Player p = cp.getPlayer();
								Location l = p.getLocation();
								
								if(l.getY() < 0.0D)
								{
									new CraftCommonWatcher(p);
									continue;
								}
								
								final World w = l.getWorld();
								final int by = l.getBlockY() - 1;
								final int bx = l.getBlockX();
								final int bz = l.getBlockZ();
								final int nx = bx >= 0 ? 1 : -1;
								final int nz = bz >= 0 ? 1 : -1;
								
								final double lx = l.getX();
								final double lz = l.getZ();
								
								double rx = Math.abs(lx % 1);
								double rz = Math.abs(lz % 1);
								
								Integer x = null;
								Integer z = null;
								
								if(rx <= 0.3D)
								{
									x = bx - 1 * nx;
								}
								else if(rx >= 0.7D)
								{
									x = bx + 1 * nx;
								}
								
								if(rz <= 0.3D)
								{
									z = bz - 1 * nz;
								}
								else if(rz >= 0.7D)
								{
									z = bz + 1 * nz;
								}

								Block b = w.getBlockAt(bx, by, bz);
								
								if(Main.activateBlock(b))
								{
									continue;
								}
								
								if(x != null && Main.activateBlock(w.getBlockAt(x, by, bz)))
								{
									continue;
								}
								
								if(z != null && Main.activateBlock(w.getBlockAt(bx, by, z)))
								{
									continue;
								}
								
								if(x != null && z != null && Main.activateBlock(w.getBlockAt(x, by, z)))
								{
									continue;
								}
							}
						}, 0L, 1L);
					}
					
					Main.setTime(timer + 1);
					break;
				default:
					break;
				}
			}
		}, 0L, 20L);
	}
	
	public static boolean activateBlock(Block b)
	{
		if(!b.getType().hasGravity())
		{	
			return false;
		}
		
		if(ACTIVATED_BLOCKS.containsKey(b))
		{
			return true;
		}
		
		int id = Bukkit.getScheduler().scheduleSyncDelayedTask(instance, () ->
		{
			b.setType(Material.AIR);
			b.getRelative(BlockFace.DOWN).setType(Material.AIR);
			ACTIVATED_BLOCKS.remove(b);
		}, 5L);
		
		ACTIVATED_BLOCKS.put(b, id);
		return true;
	}
	
	public static void eliminate(Competitor cp)
	{
		ELIMINATED.add(cp);
		
		Set<Competitor> set = CraftCommonPlayer.getAll(Competitor.class);
		
		int players = set.size();
		
		if(players <= 0)
		{
			announceWinner();
			return;
		}
		
		Player p = cp.getPlayer();
		p.teleport(getSpawnLocation());
		
		for(CommonPlayer cpall : CraftCommonPlayer.SET)
		{
			cpall.sendMessage("tntrun.player-eliminated", p.getDisplayName());
			cpall.sendMessage("tntrun.players-remaining", set.size());
		}
		
		if(players == 1)
		{
			Player winner = set.iterator().next().getPlayer();
			winner.playSound(winner.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
			new CraftCommonWatcher(winner);
		}
	}
	
	public static void announceWinner()
	{
		if(announceWinner)
		{
			return;
		}
		
		announceWinner = true;
		
		Collections.reverse(ELIMINATED);
		
		for(CommonPlayer cpall : CraftCommonPlayer.SET)
		{
			Player all = cpall.getPlayer();
			
			all.sendMessage("§a§l§m ]                                                   [ ");
			
			for(int i = 0; i < ELIMINATED.size(); i++)
			{
				Competitor c = ELIMINATED.get(i);
				Player p = c.getPlayer();
				
				if(i == 0)
				{
					cpall.sendMessage("tntrun.firstplace", p.getDisplayName());
				}
				else if(i == 1)
				{
					cpall.sendMessage("tntrun.secondplace", p.getDisplayName());
				}
				else if(i == 2)
				{
					cpall.sendMessage("tntrun.thirdplace", p.getDisplayName());
				}
				else if(all == p)
				{
					all.sendMessage(" ");
					cpall.sendMessage("tntrun.place", i + 1, p.getDisplayName());
				}
			}
			
			all.sendMessage("§a§l§m ]                                                   [ ");
		}
		
		if(timer > 120)
		{
			File file = Config.getFile(Type.TNTRUN_CONFIG, true);
			YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
			
			for(int i = 0; i < 3 && i < ELIMINATED.size(); i++)
			{
				Competitor c = ELIMINATED.get(i);
				
//				Balance.Type balanceType = Balance.Type.MINIGAME;
//				double oldBalance = c.getBalance(balanceType);
//				double maxBalance = c.getMaxBalance(balanceType);
//				
//				if(oldBalance >= maxBalance)
//				{
//					c.sendMessage("minigame.limit-reached");
//					continue;
//				}
//				
//				final double prize;
//				
//				switch(i)
//				{
//				case 0:
//					prize = config.getDouble("first-prize");
//					break;
//				case 1:
//					prize = config.getDouble("second-prize");
//					break;
//				default:
//					prize = config.getDouble("third-prize");
//					break;
//				}
//				
//				double newBalance = Math.min(oldBalance + prize, maxBalance);
//				double gain = newBalance - oldBalance;
//				
//				try
//				{
//					c.setBalance(balanceType, newBalance);
//					c.sendMessage("minigame.you-won-shards", gain);
//				}
//				catch(IOException e)
//				{
//					e.printStackTrace();
//					c.sendMessage("commons.unexpected-error");
//				}
			}
		}
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), new Runnable()
		{
			@Override
			public void run()
			{
				Stop.stop();
			}
		}, 200L);
	}
	
	public static Main getInstance()
	{
		return instance;
	}

	public static String getTimer()
	{
		int seconds = timer % 60;
		int minutes = timer / 60;
		
		DecimalFormat df = new DecimalFormat("00");
		
		return df.format(minutes) + ":" + df.format(seconds);
	}

	public static int getMaxPlayers()
	{
		File file = Config.getFile(Type.TNTRUN_CONFIG, true);
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		
		return config.getInt("max-players");
	}

	public static State getState()
	{
		return state;
	}
	
	public static void setTime(int seconds)
	{
		if(timer != (timer = Math.max(seconds, 0)))
		{
			Bukkit.getPluginManager().callEvent(new TimerChangeEvent(timer));
		}
	}
	
	public static void setState(State state)
	{
		if(Main.state != (Main.state = state))
		{
			Bukkit.getPluginManager().callEvent(new StateChangeEvent(state));
		}
	}
	
	@EventHandler
	public void on(PlayerCountChangeEvent e)
	{
		if(state == State.STARTING)
		{
			if(taskB != 0 && e.getCount() < getMinPlayers())
			{
				Main.setTime(getMaxTimer());
				Bukkit.getScheduler().cancelTask(taskB);
				taskB = 0;
				
				for(CommonPlayer cp : CraftCommonPlayer.SET)
				{
					Player p = cp.getPlayer();
					cp.sendMessage("tntrun.timer-has-been-reset");
					cp.sendMessage("tntrun.waiting-players");
					p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0F, 1.0F);
				}
			}
			else if(taskB == 0 && e.getCount() >= getMinPlayers())
			{
				startTaskB();
			}
		}
	}
	
	public static int getTaskB()
	{
		return taskB;
	}
	
	private static int getMaxTimer()
	{
		File file = Config.getFile(Type.TNTRUN_CONFIG, true);
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		
		return config.getInt("max-timer");
	}
	
	public static Location getSpawnLocation()
	{
		File file = Config.getFile(Type.TNTRUN_CONFIG, true);
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		
		return (Location) config.get("spawn-location");
	}
	
	public static int getMinPlayers()
	{
		File file = Config.getFile(Type.TNTRUN_CONFIG, true);
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		
		return config.getInt("min-players");
	}

	public static boolean hasAnnouncedWinner()
	{
		return announceWinner;
	}
}
