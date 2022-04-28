package br.com.acenetwork.commons.player.craft;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

import br.com.acenetwork.commons.Commons;
import br.com.acenetwork.commons.CommonsHotbar;
import br.com.acenetwork.commons.CommonsScoreboard;
import br.com.acenetwork.commons.CommonsUtil;
import br.com.acenetwork.commons.constants.Tag;
import br.com.acenetwork.commons.event.PlayerModeChangeEvent;
import br.com.acenetwork.commons.executor.Balance;
import br.com.acenetwork.commons.inventory.GUI;
import br.com.acenetwork.commons.manager.CommonsConfig;
import br.com.acenetwork.commons.manager.CommonsConfig.Type;
import br.com.acenetwork.commons.manager.Message;
import br.com.acenetwork.commons.player.CommonPlayer;

public abstract class CraftCommonPlayer implements CommonPlayer
{
	public static final Set<CommonPlayer> SET = new HashSet<>();
	
	protected final Player p;
	
	private GUI gui;
	private CommonsScoreboard commonsScoreboard;
	private CommonsHotbar commonsHotbar;
	private Tag tag;
	private long combat;
	private long playerCombat;
	private Player lastPlayerDamage;
	private boolean specs;
	private boolean invis;
	private boolean ignoreInvisAndSpecs;
	
	public CraftCommonPlayer(Player p)
	{
		this.p = p;
		
		CommonPlayer previous = get(p);
		
		SET.add(this);
		
		if(previous != null)	
		{
			specs = previous.canSpecs();
			commonsScoreboard = previous.getCommonsScoreboard();
			previous.delete();
		}
		
		reset();
		
		Bukkit.getPluginManager().callEvent(new PlayerModeChangeEvent(this));
		Bukkit.getPluginManager().registerEvents(this, Commons.getPlugin());
	}
	
	public static <T extends CommonPlayer> Set<T> getAll(Class<T> type)
	{
		return SET.stream().filter(x -> type.isInstance(x)).map(x -> type.cast(x)).collect(Collectors.toSet());
	}
	
	public static CommonPlayer get(Player p)
	{
		return SET.stream().filter(x -> x.getPlayer() == p).findAny().orElse(null);
	}
	
	@Override
	public boolean delete()
	{
		reset();
		HandlerList.unregisterAll(this);
		return SET.remove(this);
	}
	
	@Override
	public void setSpecs(boolean value)
	{
		if(ignoreInvisAndSpecs)
		{
			return;
		}
		
		if(value)
		{
			for(CommonPlayer cp : SET)
			{
				p.showPlayer(Commons.getPlugin(), cp.getPlayer());
			}
		}
		else
		{
			for(CommonPlayer cp : SET)
			{
				if(cp.isInvis())
				{
					p.hidePlayer(Commons.getPlugin(), cp.getPlayer());
				}
				else
				{
					p.showPlayer(Commons.getPlugin(), cp.getPlayer());
				}
			}
		}
		
		specs = value;
	}
	
	@Override
	public boolean canSpecs()
	{
		return specs;
	}

	@Override
	public boolean setInvis(boolean value)
	{
		if(value)
		{
			for(CommonPlayer cp : SET)
			{
				if(cp.getIgnoreInvisAndSpecs())
				{
					continue;
				}
				
				if(!cp.canSpecs())
				{
					cp.getPlayer().hidePlayer(Commons.getPlugin(), p);
				}
			}
		}
		else
		{
			for(CommonPlayer cp : SET)
			{
				if(cp.getIgnoreInvisAndSpecs())
				{
					continue;
				}
				
				cp.getPlayer().showPlayer(Commons.getPlugin(), p);
			}
		}
		
		return invis != (invis = value);
	}
	
	@Override
	public boolean isInvis()
	{
		return invis;
	}

	@Override
	public void setIgnoreInvisAndSpecs(boolean value)
	{
		ignoreInvisAndSpecs = value;
	}

	@Override
	public boolean getIgnoreInvisAndSpecs()
	{
		return ignoreInvisAndSpecs;
	}

	@Override
	public boolean isCombat()
	{
		return System.currentTimeMillis() - combat < 9000L;
	}

	@Override
	public boolean isCombat(long ms)
	{
		return System.currentTimeMillis() - combat < ms;
	}

	@Override
	public boolean isPlayerCombat()
	{
		return System.currentTimeMillis() - playerCombat < 9000L;
	}

	@Override
	public boolean isPlayerCombat(long ms)
	{
		return System.currentTimeMillis() - playerCombat < ms;
	}

	@Override
	public void setPlayerCombat(boolean value)
	{
		playerCombat = value ? System.currentTimeMillis() : 0L;
	}

	@Override
	public void setCombat(boolean value)
	{
		combat = value ? System.currentTimeMillis() : 0L;
	}
	
	@Override
	public void sendMessage(String key, Object... args)
	{
		p.sendMessage(Message.getMessage(p.getLocale(), key, args));
	}

	@Override
	public Player getPlayer()
	{
		return p;
	}
	
	@EventHandler
	public void on(PlayerQuitEvent e)
	{
		if(e.getPlayer() != p)
		{
			return;
		}
		
		setCommonsHotbar(null);
		setCommonsScoreboard(null);
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void commonEntityDamage(EntityDamageEvent e)
	{
		if(e.getEntity() != p)
		{
			return;
		}
		
		setCombat(true);
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void on(EntityDamageByEntityEvent e)
	{
		if(e.getEntity() != p)
		{
			return;
		}
				
		Entity entity = e.getEntity();
		Entity damager = e.getDamager();
		
		Player p = null;
		Player t = null;

		if(entity instanceof Player)
		{
			p = (Player) entity;

			if(damager instanceof Player)
			{
				t = (Player) damager;
			}
			else if(damager instanceof Projectile)
			{
				Projectile projectile = (Projectile) damager;

				if(projectile.getShooter() instanceof Player)
				{
					t = (Player) projectile.getShooter();
				}
			}
		}
		
		if(p != null && t != null && p != t)
		{
			if(t.getNoDamageTicks() > t.getMaximumNoDamageTicks())
			{
				t.setNoDamageTicks(0);
			}
			
			lastPlayerDamage = t;
			setPlayerCombat(true);
		}
	}
	
	@Override
	public Tag getTag()
	{
		return tag == null ? Tag.DEFAULT : tag;
	}

	@Override
	public boolean hasPermission(String perm)
	{
		return CommonsUtil.hasPermission(p.getUniqueId(), perm);
//		perm = perm.replace('.', ':');
//
//		File userFile = CommonsConfig.getFile(Type.USER, true, getUniqueID());
//		YamlConfiguration userConfig = YamlConfiguration.loadConfiguration(userFile);
//
//		ConfigurationSection userPermissions = userConfig.getConfigurationSection("permission");
//
//		if(userPermissions != null)
//		{
//			for(String key : userPermissions.getKeys(false))
//			{
//				long value = userConfig.getLong("permission." + key);
//				boolean valid = value == 0 || value > System.currentTimeMillis();
//
//				if(valid && (key.endsWith("*") && perm.startsWith(key.substring(0, key.length() - 1)) || 
//					perm.equals(key)))
//				{
//					return true;
//				}
//			}
//		}
//		
//		ConfigurationSection userGroups = userConfig.getConfigurationSection("group");
//		
//		if(userGroups != null)
//		{
//			for(String key : userGroups.getKeys(false))
//			{
//				long value = userConfig.getLong("group." + key);
//				boolean valid = value == 0 || value > System.currentTimeMillis();
//
//				if(valid)
//				{
//					File groupFile = CommonsConfig.getFile(Type.GROUP, true, key);
//					YamlConfiguration groupConfig = YamlConfiguration.loadConfiguration(groupFile);
//
//					ConfigurationSection groupPermissions = groupConfig.getConfigurationSection("permission");
//
//					if(groupPermissions != null)
//					{
//						for(String key1 : groupPermissions.getKeys(false))
//						{
//							value = groupConfig.getLong("permisison." + key1);
//							valid = value == 0 || value > System.currentTimeMillis();
//							
//							if(valid && (key1.endsWith("*") && perm.startsWith(key1.substring(0, key1.length() - 1)) || 
//								perm.equals(key1)))
//							{
//								return true;
//							}
//						}
//					}
//				}
//			}
//		}
//
//		return false;
	}
	
	@Override
	public Tag getBestTag()
	{
		for(Tag tag : Tag.values())
		{
			if(hasPermission(tag.getPermission()))
			{
				return tag;
			}
		}
		
		return Tag.DEFAULT;
	}
	
	@Override
	public Player getLastPlayerDamage()
	{
		return lastPlayerDamage;
	}
	
	@Override
	public boolean setTag(Tag tag)
	{
		p.setDisplayName(tag + p.getName());
		p.setPlayerListName(p.getDisplayName().substring(0, Math.min(16, p.getDisplayName().length())));
		return this.tag != (this.tag = tag == null ? Tag.DEFAULT : tag);
	}

	@Override
	public void setGUI(GUI gui)
	{
		if(this.gui != null)
		{
			HandlerList.unregisterAll(this.gui);
		}
		
		if((this.gui = gui) != null)
		{
			Bukkit.getPluginManager().registerEvents(this.gui, Commons.getPlugin());
		}
	}

	@Override
	public CommonsScoreboard getCommonsScoreboard()
	{
		return commonsScoreboard;
	}
	
	@Override
	public void setCommonsScoreboard(CommonsScoreboard commonsScoreboard)
	{
		if(this.commonsScoreboard != null)
		{
			HandlerList.unregisterAll(this.commonsScoreboard);
		}
		
		if((this.commonsScoreboard = commonsScoreboard) != null)
		{
			Bukkit.getPluginManager().registerEvents(this.commonsScoreboard, Commons.getPlugin());
			p.setScoreboard(commonsScoreboard.getScoreboard());
		}
		else
		{
			p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
		}
	}
	
	@Override
	public CommonsHotbar getCommonsHotbar()
	{
		return commonsHotbar;
	}
	
	@Override
	public void setCommonsHotbar(CommonsHotbar commonsHotbar)
	{
		if(this.commonsHotbar != null)
		{
			HandlerList.unregisterAll(this.commonsHotbar);
		}
		
		if((this.commonsHotbar = commonsHotbar) != null)
		{
			Bukkit.getPluginManager().registerEvents(this.commonsHotbar, Commons.getPlugin());
		}
	}
	
	@Override
	public GUI getGUI()
	{
		return gui;
	}
	
	@Override
	public double getBalance(Balance.Type balanceType)
	{
		File file = CommonsConfig.getFile(balanceType.getFileType(), true, p.getUniqueId());
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		
		return config.getDouble("balance");
	}
	
	@Override
	public double getMaxBalance(Balance.Type balanceType)
	{
		File file = CommonsConfig.getFile(balanceType.getFileType(), true, p.getUniqueId());
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		
		return config.getDouble("max-balance");
	}
	
	@Override
	public void setBalance(Balance.Type balanceType, double balance) throws IOException
	{
		File file = CommonsConfig.getFile(balanceType.getFileType(), true, p.getUniqueId());
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		
		config.set("balance", balance);
		config.save(file);
	}
	
	@Override
	public void setMaxBalance(Balance.Type balanceType, double balance) throws IOException
	{
		File file = CommonsConfig.getFile(balanceType.getFileType(), true, p.getUniqueId());
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		
		config.set("max-balance", balance);
		config.save(file);
	}
	
	@Override
	public String getClan()
	{
		File playerFile = CommonsConfig.getFile(Type.PLAYER, false, p.getUniqueId());
		YamlConfiguration playerConfig = YamlConfiguration.loadConfiguration(playerFile);
		
		return playerConfig.getString("clan");
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void reset()
	{
		for(PotionEffect effect : p.getActivePotionEffects())
		{
			p.removePotionEffect(effect.getType());
		}
		
		p.setCollidable(true);
		p.setVelocity(new Vector());
		p.setMaximumNoDamageTicks(20);
		p.setFireTicks(0);
		p.setFreezeTicks(0);
		p.setArrowsInBody(0);
		
		p.getInventory().clear();
		p.getInventory().setArmorContents(null);
		
		p.setMaxHealth(20.0D);
		p.setHealth(p.getMaxHealth());
		p.setFoodLevel(20);
		
		p.setFlySpeed(0.1F);
		p.setWalkSpeed(0.2F);
		p.setExp(0.0F);
		p.setLevel(0);

		setCombat(false);
		setPlayerCombat(false);
	}
	
	@Override
	public Locale getLocale()
	{
		String[] split = p.getLocale().split("_");
		
		return new Locale(split[0], split[1].toUpperCase());
	}
}
