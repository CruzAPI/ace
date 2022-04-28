package br.com.acenetwork.survival.listener;

import java.io.File;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;

import br.com.acenetwork.survival.Main;
import br.com.acenetwork.survival.manager.Config;
import br.com.acenetwork.survival.manager.Config.Type;

public class PlayerJoin implements Listener
{
	@SuppressWarnings({ "unchecked", "deprecation" })
	@EventHandler(priority = EventPriority.HIGHEST)
	public void on(PlayerJoinEvent e)
	{
		Player p = e.getPlayer();
		
		p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
		
		File combatlogFile = Config.getFile(Type.COMBATLOG, false, p.getUniqueId());
		
		if(!combatlogFile.exists())
		{
			return;
		}
		
		YamlConfiguration config = YamlConfiguration.loadConfiguration(combatlogFile);
		
		if(config.getBoolean("death"))
		{
			p.getInventory().clear();
			p.setLevel(0);
			p.setExp(0.0F);
			p.setMetadata("hide death message", new FixedMetadataValue(Main.getInstance(), true));
			p.setHealth(0.0D);
		}
		else
		{			
			List<ItemStack> listItems = (List<ItemStack>) config.getList("inventory");
			ItemStack[] contents = new ItemStack[listItems.size()];
			contents = listItems.toArray(contents);
			
			p.getInventory().setContents(contents);
			p.teleport((Location) config.get("location"), TeleportCause.PLUGIN);
			p.setFireTicks(config.getInt("fire-ticks"));
			p.setFallDistance(NumberConversions.toFloat(config.get("fall-distance")));
			p.addPotionEffects((List<PotionEffect>) config.getList("potion-effects"));
			p.setFoodLevel(config.getInt("food-level"));
			p.setMaximumNoDamageTicks(config.getInt("max-no-damage-ticks"));
			p.setNoDamageTicks(config.getInt("no-damage-ticks"));
			p.setMaxHealth(config.getInt("max-health"));
			p.setHealth(config.getInt("health"));
			p.setMaximumAir(config.getInt("max-air"));
			p.setRemainingAir(config.getInt("remaining-air"));
			p.setFreezeTicks(config.getInt("freeze-ticks"));
			p.setArrowsInBody(config.getInt("arrows-in-body"));
			p.setSaturation(NumberConversions.toFloat(config.get("saturation")));
			p.setSaturatedRegenRate(config.getInt("saturated-regen-rate"));
			p.setUnsaturatedRegenRate(config.getInt("unsaturated-regen-rate"));
			p.setVelocity((Vector) config.get("velocity"));
		}
		
		combatlogFile.delete();
	}
}
