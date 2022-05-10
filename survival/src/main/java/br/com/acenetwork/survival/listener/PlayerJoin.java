package br.com.acenetwork.survival.listener;

import java.io.File;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;

import br.com.acenetwork.commons.player.CommonPlayer;
import br.com.acenetwork.commons.player.craft.CraftCommonPlayer;
import br.com.acenetwork.survival.Main;
import br.com.acenetwork.survival.executor.Spawn;
import br.com.acenetwork.survival.manager.Config;
import br.com.acenetwork.survival.manager.Config.Type;
import br.com.acenetwork.survival.player.SurvivalPlayer;

public class PlayerJoin implements Listener
{
	@SuppressWarnings({ "unchecked", "deprecation" })
	@EventHandler(priority = EventPriority.HIGHEST)
	public void on(PlayerJoinEvent e)
	{
		Player p = e.getPlayer();
		CommonPlayer cp = CraftCommonPlayer.get(p);
		
//		ItemStack book = new ItemStack(Material.WRITABLE_BOOK);
//		BookMeta zbook = (BookMeta) book.getItemMeta();
//		zbook.setAuthor("§bAce Network");
//		zbook.setTitle("§bAce Network");
//		zbook.addPage("Seja bem-vindo ao Survival da Ace Network! O seu objetivo como jogador é vender seus items para conquistar Ace Shards. Esses, por sua vez, são convertidos em $BTA e enviados para a sua conta em nosso dApp.");
//		zbook.addPage("COMANDOS:\n"
//				+ "/sell -> vende o item na sua mão\n"
//				+ "/sellall -> vende o inventário completo\n"
//				+ "/bal -> checa seu balance de Ace Shards\n"
//				+ "/price -> checa o preço do item\n"
//				+ "\n"
//				+ "Não existe proteção de terreno, então tome cuidado com seus itens.");
//		book.setItemMeta(zbook);
//		book.setType(Material.WRITTEN_BOOK);
//		
//		p.getInventory().addItem(book);
		
		if(cp instanceof SurvivalPlayer)
		{
			SurvivalPlayer sp = (SurvivalPlayer) cp;
			
			if(!p.hasPlayedBefore())
			{
				p.teleport(Spawn.SPAWN_LOCATION);
				sp.setSpawnProtection(true);
				
				ItemStack woodenSword = new ItemStack(Material.WOODEN_SWORD);
				ItemMeta meta = woodenSword.getItemMeta();
				meta.addEnchant(Enchantment.VANISHING_CURSE, 1, true);
				woodenSword.setItemMeta(meta);
				
				ItemStack woodenPickaxe = new ItemStack(Material.WOODEN_PICKAXE);
				meta = woodenPickaxe.getItemMeta();
				meta.addEnchant(Enchantment.VANISHING_CURSE, 1, true);
				woodenPickaxe.setItemMeta(meta);
				
				p.getInventory().addItem(woodenSword);
				p.getInventory().addItem(woodenPickaxe);
				p.getInventory().addItem(new ItemStack(Material.SPRUCE_BOAT));
				p.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 4));
			}
			else
			{
				File playerFile = Config.getFile(Type.PLAYER_INFO, true, p.getUniqueId());
				YamlConfiguration playerConfig = YamlConfiguration.loadConfiguration(playerFile);
				
				sp.setSpawnProtection(playerConfig.getBoolean("spawn-protection"));
			}
		}
		
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
