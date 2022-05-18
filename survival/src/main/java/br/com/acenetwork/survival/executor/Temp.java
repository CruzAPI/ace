package br.com.acenetwork.survival.executor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import br.com.acenetwork.commons.executor.Balance;
import br.com.acenetwork.commons.executor.BroadcastCMD;
import br.com.acenetwork.commons.manager.CommonsConfig;
import br.com.acenetwork.commons.manager.Message;
import br.com.acenetwork.commons.player.CommonPlayer;
import br.com.acenetwork.commons.player.craft.CraftCommonPlayer;
import br.com.acenetwork.survival.Main;
import br.com.acenetwork.survival.manager.Config;
import br.com.acenetwork.survival.manager.Land;
import br.com.acenetwork.survival.manager.PRICE;
import br.com.acenetwork.survival.manager.Config.Type;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Switch.Face;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static br.com.acenetwork.survival.executor.Temp.Direction.*;

public class Temp implements TabExecutor
{
	public enum Direction
	{
		EAST(1, 0),
		NORTH(0, -1), 
		WEST(-1, 0), 
		SOUTH(0, 1),
		;
		
		private final int x;
		private final int z;
		
		Direction(int x, int z)
		{
			this.x = x;
			this.z = z;
		}
	}
	
	private static final double HIGHEST = 200.0D;
	private static final double HIGH = 100.0D;
	private static final double NORMAL = 50.0D;
	private static final double LOW = 25.0D;
	private static final double LOWEST = 10.0D;
	private static final double MEME = 5.0D;
	
	public Temp()
	{
		// 200 < 1%  liquidez altíssima
		// 100 < 2% liquidez alta
		// 50 < 4% liquidez média
		// 25 < 7,5% liquidez baixa
		// 10 < 17,5% liquidez baixíssima
		// 5 < 30%  liquidez meme
		for(Material type : Material.values())
		{
			switch(type)
			{
			case STONE:
				new PRICE(type, 0.02D, 3200, HIGH);
				break;
			case GRANITE:
				new PRICE(type, 0.1D, 640, NORMAL);
				break;
			case DIORITE:
				new PRICE(type, 0.1D, 640, NORMAL);
				break;
			case ANDESITE:
				new PRICE(type, 0.1D, 640, NORMAL);
				break;
			case DEEPSLATE:
				new PRICE(type, 0.02D, 1600, NORMAL);
				break;
			case COBBLED_DEEPSLATE:
				new PRICE(type, 0.01, 3200, NORMAL);
				break;
			case CALCITE:
				new PRICE(type, 0.2D, 64, NORMAL);
				break;
			case TUFF:
				new PRICE(type, 0.2D, 64, NORMAL);
				break;
			case GRASS_BLOCK:
				new PRICE(type, 0.5D, 64, LOW);
				break;
			case DIRT:
				new PRICE(type, 0.03D, 1600, NORMAL);
				break;
			case COARSE_DIRT:
				new PRICE(type, 0.5D, 64, LOW);
				break;
			case PODZOL:
				new PRICE(type, 1.0D, 64, LOW);
				break;
			case CRIMSON_NYLIUM:
				new PRICE(type, 1.0D, 64, LOW);
				break;
			case WARPED_NYLIUM:
				new PRICE(type, 1.0D, 64, LOW);
				break;
			case OAK_PLANKS:
				new PRICE(type, 0.25D, 256, LOW);
				break;
			case ACACIA_PLANKS:
				new PRICE(type, 0.25D, 256, LOW);
				break;
			case BIRCH_PLANKS:
				new PRICE(type, 0.25D, 256, LOW);
				break;
			case CRIMSON_PLANKS:
				new PRICE(type, 0.25D, 256, LOW);
				break;
			case DARK_OAK_PLANKS:
				new PRICE(type, 0.25D, 256, LOW);
				break;
			case JUNGLE_PLANKS:
				new PRICE(type, 0.25D, 256, LOW);
				break;
			case SPRUCE_PLANKS:
				new PRICE(type, 0.25D, 256, LOW);
				break;
			case WARPED_PLANKS:
				new PRICE(type, 0.25D, 256, LOW);
				break;
			case ACACIA_LOG:
				new PRICE(type, 1.0D, 64, LOW);
				break;
			case BIRCH_LOG:
				new PRICE(type, 1.0D, 64, LOW);
				break;
			case DARK_OAK_LOG:
				new PRICE(type, 1.0D, 64, LOW);
				break;
			case JUNGLE_LOG:
				new PRICE(type, 1.0D, 64, LOW);
				break;
			case OAK_LOG:
				new PRICE(type, 1.0D, 64, LOW);
				break;
			case SPRUCE_LOG:
				new PRICE(type, 1.0D, 64, LOW);
				break;
			case CRIMSON_STEM:
				new PRICE(type, 1.0D, 64, LOW);
				break;
			case WARPED_STEM:
				new PRICE(type, 1.0D, 64, LOW);
				break;
			case ACACIA_LEAVES:
				new PRICE(type, 0.1D, 640, LOWEST);
				break;
			case AZALEA_LEAVES:
				new PRICE(type, 0.1D, 640, LOWEST);
				break;
			case BIRCH_LEAVES:
				new PRICE(type, 0.1D, 640, LOWEST);
				break;
			case DARK_OAK_LEAVES:
				new PRICE(type, 0.1D, 640, LOWEST);
				break;
			case FLOWERING_AZALEA_LEAVES:
				new PRICE(type, 0.1D, 640, LOWEST);
				break;
			case JUNGLE_LEAVES:
				new PRICE(type, 0.1D, 640, LOWEST);
				break;
			case OAK_LEAVES:
				new PRICE(type, 0.1D, 640, LOWEST);
				break;
			case SPRUCE_LEAVES:
				new PRICE(type, 0.1D, 640, LOWEST);
				break;
			case NETHER_WART_BLOCK:
				new PRICE(type, 0.1D, 640, LOWEST);
				break;
			case WARPED_WART_BLOCK:
				new PRICE(type, 0.1D, 640, LOWEST);
				break;
			case ACACIA_SAPLING:
				new PRICE(type, 0.5D, 64, LOWEST);
				break;
			case BIRCH_SAPLING:
				new PRICE(type, 0.5D, 64, LOWEST);
				break;
			case DARK_OAK_SAPLING:
				new PRICE(type, 0.5D, 64, LOWEST);
				break;
			case JUNGLE_SAPLING:
				new PRICE(type, 0.5D, 64, LOWEST);
				break;
			case OAK_SAPLING:
				new PRICE(type, 0.5D, 64, LOWEST);
				break;
			case SPRUCE_SAPLING:
				new PRICE(type, 0.5D, 64, LOWEST);
				break;
			case SAND:
				new PRICE(type, 0.04D, 1600, NORMAL);
				break;
			case RED_SAND:
				new PRICE(type, 0.08D, 1600, LOW);
				break;
			case RED_SANDSTONE:
				new PRICE(type, 0.2D, 64, LOWEST);
				break;
			case GRAVEL:
				new PRICE(type, 0.1D, 640, NORMAL);
				break;
			case FLINT:
				new PRICE(type, 0.5D, 64, NORMAL);
				break;
			case DEEPSLATE_COAL_ORE:
				new PRICE(type, 1.0D, 256, LOWEST);
				break;
			case DEEPSLATE_COPPER_ORE:
				new PRICE(type, 1.5D, 128, LOWEST);
				break;
			case DEEPSLATE_IRON_ORE:
				new PRICE(type, 2.5D, 128, LOWEST);
				break;
			case DEEPSLATE_LAPIS_ORE:
				new PRICE(type, 5.0D, 64, LOWEST);
				break;
			case DEEPSLATE_REDSTONE_ORE:
				new PRICE(type, 5.0D, 64, LOWEST);
				break;
			case DEEPSLATE_GOLD_ORE:
				new PRICE(type, 10.0D, 64, LOWEST);
				break;
			case DEEPSLATE_DIAMOND_ORE:
				new PRICE(type, 30.0D, 32, LOWEST);
				break;
			case DEEPSLATE_EMERALD_ORE:
				new PRICE(type, 50.0D, 16, LOWEST);
				break;
			case COAL_ORE:
				new PRICE(type, 1.0D, 256, LOW);
				break;
			case COPPER_ORE:
				new PRICE(type, 1.5D, 128, LOW);
				break;
			case IRON_ORE:
				new PRICE(type, 2.5D, 128, LOW);
				break;
			case LAPIS_ORE:
				new PRICE(type, 5.0D, 64, LOW);
				break;
			case REDSTONE_ORE:
				new PRICE(type, 5.0D, 64, LOW);
				break;
			case GOLD_ORE:
				new PRICE(type, 10.0D, 64, LOW);
				break;
			case DIAMOND_ORE:
				new PRICE(type, 30.0D, 32, LOW);
				break;
			case EMERALD_ORE:
				new PRICE(type, 50.0D, 16, LOW);
				break;
			case NETHER_GOLD_ORE:
				new PRICE(type, 1.0D, 128, LOW);
				break;
			case NETHER_QUARTZ_ORE:
				new PRICE(type, 1.0D, 128, LOW);
				break;
			case ANCIENT_DEBRIS:
				new PRICE(type, 7.5D, 16, LOW);
				break;
			case COAL:
				new PRICE(type, 0.2D, 640, LOW);
				break;
			case COPPER_INGOT:
				new PRICE(type, 0.3D, 640, LOW);
				break;
			case IRON_INGOT:
				new PRICE(type, 0.5D, 640, NORMAL);
				break;
			case LAPIS_LAZULI:
				new PRICE(type, 0.5D, 1280, LOW);
				break;
			case REDSTONE:
				new PRICE(type, 0.5D, 1280, LOW);
				break;
			case GOLD_INGOT:
				new PRICE(type, 2.0D, 320, NORMAL);
				break;
			case DIAMOND:
				new PRICE(type, 6.0D, 256, LOW);
				break;
			case EMERALD:
				new PRICE(type, 0.5D, 256, NORMAL);
				break;
			case QUARTZ:
				new PRICE(type, 0.5D, 256, LOW);
				break;
			case QUARTZ_BLOCK:
				new PRICE(type, 2.0D, 64, LOW);
				break;
			case NETHERITE_INGOT:
				new PRICE(type, 30.0D, 64, LOW);
				break;
			case AMETHYST_SHARD:
				new PRICE(type, 0.25D, 256, LOW);
				break;
			case AMETHYST_BLOCK:
				new PRICE(type, 1.0D, 64, LOW);
				break;
			case SPONGE:
				new PRICE(type, 75.0D, 16, LOWEST);
				break;
			case DRAGON_EGG:
				new PRICE(type, 10000.0D, 1, MEME);
				break;
			case NETHER_STAR:
				new PRICE(type, 2000.0D, 1, LOWEST);
				break;
			case GLASS:
				new PRICE(type, 0.2D, 320, LOW);
				break;
			case SANDSTONE:
				new PRICE(type, 0.1D, 64, LOWEST);
				break;
			case BLACK_WOOL:
				new PRICE(type, 1.0D, 64, LOW);
				break;
			case BLUE_WOOL:
				new PRICE(type, 1.0D, 64, LOW);
				break;
			case BROWN_WOOL:
				new PRICE(type, 1.0D, 64, LOW);
				break;
			case CYAN_WOOL:
				new PRICE(type, 1.0D, 64, LOW);
				break;
			case GRAY_WOOL:
				new PRICE(type, 1.0D, 64, LOW);
				break;
			case GREEN_WOOL:
				new PRICE(type, 1.0D, 64, LOW);
				break;
			case LIGHT_BLUE_WOOL:
				new PRICE(type, 1.0D, 64, LOW);
				break;
			case LIGHT_GRAY_WOOL:
				new PRICE(type, 1.0D, 64, LOW);
				break;
			case LIME_WOOL:
				new PRICE(type, 1.0D, 64, LOW);
				break;
			case MAGENTA_WOOL:
				new PRICE(type, 1.0D, 64, LOW);
				break;
			case ORANGE_WOOL:
				new PRICE(type, 1.0D, 64, LOW);
				break;
			case PINK_WOOL:
				new PRICE(type, 1.0D, 64, LOW);
				break;
			case PURPLE_WOOL:
				new PRICE(type, 1.0D, 64, LOW);
				break;
			case RED_WOOL:
				new PRICE(type, 1.0D, 64, LOW);
				break;
			case WHITE_WOOL:
				new PRICE(type, 1.0D, 64, LOW);
				break;
			case YELLOW_WOOL:
				new PRICE(type, 1.0D, 64, LOW);
				break;
			case BRICKS:
				new PRICE(type, 1.0D, 64, LOW);
				break;
			case BRICK:
				new PRICE(type, 0.25D, 256, LOW);
				break;
			case CLAY_BALL:
				new PRICE(type, 0.1D, 256, LOW);
				break;
			case CLAY:
				new PRICE(type, 0.4D, 64, LOW);
				break;
			case BOOKSHELF:
				new PRICE(type, 3.0D, 16, LOW);
				break;
			case MOSSY_COBBLESTONE:
				new PRICE(type, 1.0D, 256, LOW);
				break;
			case OBSIDIAN:
				new PRICE(type, 2.5D, 128, LOW);
				break;
			case CRYING_OBSIDIAN:
				new PRICE(type, 10.0D, 10, LOWEST);
				break;
			case ICE:
				new PRICE(type, 0.5D, 640, NORMAL);
				break;
			case PACKED_ICE:
				new PRICE(type, 4.5D, 64, LOW);
				break;
			case SNOW_BLOCK:
				new PRICE(type, 0.4D, 1280, LOW);
				break;
			case SNOWBALL:
				new PRICE(type, 0.1D, 512, LOW);
				break;
			case NETHERRACK:
				new PRICE(type, 0.01D, 6400, HIGHEST);
				break;
			case SOUL_SAND:
				new PRICE(type, 0.1D, 640, NORMAL);
				break;
			case SOUL_SOIL:
				new PRICE(type, 0.05D, 640, LOW);
				break;
			case BASALT:
				new PRICE(type, 0.05D, 640, LOW);
				break;
			case GLOWSTONE:
				new PRICE(type, 1.0D, 64, NORMAL);
				break;
			case GLOWSTONE_DUST:
				new PRICE(type, 0.25D, 256, NORMAL);
				break;
			case STONE_BRICKS:
				new PRICE(type, 0.2D, 64, LOWEST);
				break;
			case DEEPSLATE_BRICKS:
				new PRICE(type, 0.2D, 64, LOWEST);
				break;
			case NETHER_BRICKS:
				new PRICE(type, 1.0D, 64, LOW);
				break;
			case NETHER_BRICK:
				new PRICE(type, 0.25D, 256, LOW);
				break;
			case END_STONE:
				new PRICE(type, 0.05D, 3200, HIGH);
				break;
			case END_STONE_BRICKS:
				new PRICE(type, 0.2D, 64, LOWEST);
				break;
			case MYCELIUM:
				new PRICE(type, 0.5D, 320, LOW);
				break;
			case COBBLESTONE:
				new PRICE(type, 0.01D, 6400, HIGHEST);
				break;
			case PRISMARINE_SHARD:
				new PRICE(type, 1.0D, 256, LOW);
				break;
			case PRISMARINE_CRYSTALS:
				new PRICE(type, 1.0D, 256, LOW);
				break;
			case PRISMARINE:
				new PRICE(type, 4.0D, 64, LOWEST);
				break;
			case PRISMARINE_BRICKS:
				new PRICE(type, 8.0D, 64, LOWEST);
				break;
			case DARK_PRISMARINE:
				new PRICE(type, 8.0D, 64, LOWEST);
				break;
			case SEA_LANTERN:
				new PRICE(type, 10.0D, 64, LOWEST);
				break;
			case MAGMA_BLOCK:
				new PRICE(type, 0.25D, 128, LOW);
				break;
				
			case TERRACOTTA:
				new PRICE(type, 0.5D, 64, LOW);
				break;
			case BLACK_TERRACOTTA:
				new PRICE(type, 0.5D, 64, LOW);
				break;
			case BLUE_TERRACOTTA:
				new PRICE(type, 0.5D, 64, LOW);
				break;
			case BROWN_TERRACOTTA:
				new PRICE(type, 0.5D, 64, LOW);
				break;
			case CYAN_TERRACOTTA:
				new PRICE(type, 0.5D, 64, LOW);
				break;
			case GRAY_TERRACOTTA:
				new PRICE(type, 0.5D, 64, LOW);
				break;
			case GREEN_TERRACOTTA:
				new PRICE(type, 0.5D, 64, LOW);
				break;
			case LIGHT_BLUE_TERRACOTTA:
				new PRICE(type, 0.5D, 64, LOW);
				break;
			case LIGHT_GRAY_TERRACOTTA:
				new PRICE(type, 0.5D, 64, LOW);
				break;
			case LIME_TERRACOTTA:
				new PRICE(type, 0.5D, 64, LOW);
				break;
			case MAGENTA_TERRACOTTA:
				new PRICE(type, 0.5D, 64, LOW);
				break;
			case WHITE_TERRACOTTA:
				new PRICE(type, 0.5D, 64, LOW);
				break;
			case YELLOW_TERRACOTTA:
				new PRICE(type, 0.5D, 64, LOW);
				break;
			case RED_TERRACOTTA:
				new PRICE(type, 0.5D, 64, LOW);
				break;
			case PURPLE_TERRACOTTA:
				new PRICE(type, 0.5D, 64, LOW);
				break;
			case PINK_TERRACOTTA:
				new PRICE(type, 0.5D, 64, LOW);
				break;
			case ORANGE_TERRACOTTA:
				new PRICE(type, 0.5D, 64, LOW);
				break;

			case COBWEB:
				new PRICE(type, 1.5D, 64, LOW);
				break;
			case GRASS:
				new PRICE(type, 0.2D, 64, LOW);
				break;
			case FERN:
				new PRICE(type, 0.2D, 64, LOW);
				break;
			case DEAD_BUSH:
				new PRICE(type, 0.1D, 64, LOW);
				break;
				
			case DANDELION:
				new PRICE(type, 0.5D, 64, LOW);
				break;
			case POPPY:
				new PRICE(type, 0.5D, 320, NORMAL);
				break;
			case BLUE_ORCHID:
				new PRICE(type, 0.5D, 64, LOW);
				break;
			case ALLIUM:
				new PRICE(type, 0.5D, 64, LOW);
				break;
			case AZURE_BLUET:
				new PRICE(type, 0.5D, 64, LOW);
				break;
			case ORANGE_TULIP:
				new PRICE(type, 0.5D, 64, LOW);
				break;
			case PINK_TULIP:
				new PRICE(type, 0.5D, 64, LOW);
				break;
			case RED_TULIP:
				new PRICE(type, 0.5D, 64, LOW);
				break;
			case WHITE_TULIP:
				new PRICE(type, 0.5D, 64, LOW);
				break;
			case CORNFLOWER:
				new PRICE(type, 0.5D, 64, LOW);
				break;
			case LILY_OF_THE_VALLEY:
				new PRICE(type, 0.5D, 64, LOW);
				break;
			case WITHER_ROSE:
				new PRICE(type, 0.5D, 640, NORMAL);
				break;
			case SPORE_BLOSSOM:
				new PRICE(type, 0.5D, 64, LOW);
				break;
			case BROWN_MUSHROOM:
				new PRICE(type, 0.5D, 640, LOW);
				break;
			case RED_MUSHROOM:
				new PRICE(type, 0.5D, 640, LOW);
				break;
			case CRIMSON_FUNGUS:
				new PRICE(type, 0.5D, 128, LOW);
				break;
			case WARPED_FUNGUS:
				new PRICE(type, 0.5D, 128, LOW);
				break;
			case SUGAR_CANE:
				new PRICE(type, 0.05D, 6400, NORMAL);
				break;
			case SUGAR:
				new PRICE(type, 0.1D, 64, HIGH);
				break;
			case KELP:
				new PRICE(type, 0.02D, 3200, LOW);
				break;
			case DRIED_KELP:
				new PRICE(type, 0.02D, 3200, LOW);
				break;
			case BAMBOO:
				new PRICE(type, 0.02D, 3200, LOW);
				break;
			case CACTUS:
				new PRICE(type, 0.05D, 6400, NORMAL);
				break;
			case VINE:
				new PRICE(type, 0.05D, 6400, NORMAL);
				break;
			case LILY_PAD:
				new PRICE(type, 0.15D, 64, LOW);
				break;
			case SKELETON_SKULL:
				new PRICE(type, 50.0D, 1, LOW);
				break;
			case WITHER_SKELETON_SKULL:
				new PRICE(type, 50.0D, 1, LOW);
				break;
			case PLAYER_HEAD:
				new PRICE(type, 50.0D, 1, LOW);
				break;
			case CREEPER_HEAD:
				new PRICE(type, 50.0D, 1, LOW);
				break;
			case DRAGON_HEAD:
				new PRICE(type, 50.0D, 1, LOW);
				break;
			case ZOMBIE_HEAD:
				new PRICE(type, 50.0D, 1, LOW);
				break;
			case SUNFLOWER:
				new PRICE(type, 0.2D, 320, LOW);
				break;
			case LILAC:
				new PRICE(type, 0.2D, 320, LOW);
				break;
			case ROSE_BUSH:
				new PRICE(type, 0.2D, 320, LOW);
				break;
			case PEONY:
				new PRICE(type, 0.2D, 320, LOW);
				break;
			case TALL_GRASS:
				new PRICE(type, 0.1D, 320, LOW);
				break;
			case LARGE_FERN:
				new PRICE(type, 0.1D, 320, LOW);
				break;

			case HONEY_BOTTLE:
				new PRICE(type, 0.25D, 256, NORMAL);
				break;
			case HONEY_BLOCK:
				new PRICE(type, 1.0D, 64, LOW);
				break;
				
			case HONEYCOMB:
				new PRICE(type, 0.25D, 256, LOW);
				break;
			case HONEYCOMB_BLOCK:
				new PRICE(type, 1.0D, 64, LOWEST);
				break;

			case SADDLE:
				new PRICE(type, 200.0D, 3, LOWEST);
				break;

			
			case TNT:
				new PRICE(type, 2.0D, 16, LOW);
				break;
			case REDSTONE_LAMP:
				new PRICE(type, 1.0D, 16, LOW);
				break;
			case NOTE_BLOCK:
				new PRICE(type, 2.0D, 16, LOW);
				break;

			case GHAST_TEAR:
				new PRICE(type, 10.0D, 64, NORMAL);
				break;
			case FERMENTED_SPIDER_EYE:
				new PRICE(type, 1.0D, 64, LOW);
				break;
			case MAGMA_CREAM:
				new PRICE(type, 1.0D, 64, LOW);
				break;
			case GOLDEN_CARROT:
				new PRICE(type, 1.0D, 64, LOW);
				break;
			case PHANTOM_MEMBRANE:
				new PRICE(type, 1.0D, 64, LOW);
				break;

			case BLAZE_ROD:
				new PRICE(type, 5.0D, 320, NORMAL);
				break;
			case BLAZE_POWDER:
				new PRICE(type, 2.5D, 64, LOW);
				break;

				
			case BONE:
				new PRICE(type, 0.1D, 3200, NORMAL);
				break;
			case BONE_MEAL:
				new PRICE(type, 0.03D, 192, LOW);
				break;
			case BONE_BLOCK:
				new PRICE(type, 1.0D, 64, LOW);
				break;
			case ARROW:
				new PRICE(type, 0.25D, 1600, LOW);
				break;
				
			case NAME_TAG:
				new PRICE(type, 200.0D, 3, LOWEST);
				break;

			case COMPASS:
				new PRICE(type, 4.0D, 64, LOWEST);
				break;
			case CLOCK:
				new PRICE(type, 8.0D, 64, LOWEST);
				break;

			case STRING:
				new PRICE(type, 0.2D, 3200, LOW);
				break;
			case SPIDER_EYE:
				new PRICE(type, 0.2D, 3200, LOW);
				break;
			
			case GUNPOWDER:
				new PRICE(type, 0.1D, 3200, NORMAL);
				break;
			
			case ROTTEN_FLESH:
				new PRICE(type, 0.05D, 6400, NORMAL);
				break;
				
			case EGG:
				new PRICE(type, 0.5D, 160, NORMAL);
				break;
			case FEATHER:
				new PRICE(type, 0.3D, 3200, LOW);
				break;
			case CHICKEN:
				new PRICE(type, 0.5D, 640, LOW);
				break;
			case COOKED_CHICKEN:
				new PRICE(type, 1.0D, 320, LOW);
				break;
				
			case BEEF:
				new PRICE(type, 0.5D, 640, LOW);
				break;
			case COOKED_BEEF:
				new PRICE(type, 1.0D, 320, LOW);
				break;
				
			case PORKCHOP:
				new PRICE(type, 0.5D, 640, LOW);
				break;
			case COOKED_PORKCHOP:
				new PRICE(type, 1.0D, 320, LOW);
				break;
				
			case RABBIT:
				new PRICE(type, 0.5D, 640, LOW);
				break;
			case COOKED_RABBIT:
				new PRICE(type, 1.0D, 320, LOW);
				break;
				
			case MUTTON:
				new PRICE(type, 0.5D, 320, LOW);
				break;
			case COOKED_MUTTON:
				new PRICE(type, 1.0D, 640, LOW);
				break;
			
			case SALMON:
				new PRICE(type, 0.5D, 128, LOW);
				break;
			case COD:
				new PRICE(type, 0.5D, 128, LOW);
				break;
			case TROPICAL_FISH:
				new PRICE(type, 0.5D, 128, LOW);
				break;
			case PUFFERFISH:
				new PRICE(type, 1.0D, 128, LOW);
				break;
			case COOKED_COD:
				new PRICE(type, 1.0D, 64, LOW);
				break;
			case COOKED_SALMON:
				new PRICE(type, 1.0D, 64, LOW);
				break;

			case MUSHROOM_STEW:
				new PRICE(type, 2.0D, 64, LOW);
				break;
			case BEETROOT_SOUP:
				new PRICE(type, 1.5D, 64, LOWEST);
				break;
			case RABBIT_STEW:
				new PRICE(type, 3.0D, 64, LOW);
				break;
				
			case APPLE:
				new PRICE(type, 1.0D, 640, LOW);
				break;
			case GOLDEN_APPLE:
				new PRICE(type, 18.0D, 64, LOW);
				break;
			case ENCHANTED_GOLDEN_APPLE:
				new PRICE(type, 144.0D, 10, LOWEST);
				break;
			
			case POTATO:
				new PRICE(type, 0.1D, 6400, HIGH);
				break;
			case BAKED_POTATO:
				new PRICE(type, 0.25D, 64, NORMAL);
				break;
			case POISONOUS_POTATO:
				new PRICE(type, 0.05D, 64, NORMAL);
				break;
			
			case CARROT:
				new PRICE(type, 0.1D, 6400, HIGH);
				break;

				
			case LEAD:
				new PRICE(type, 0.2D, 64, LOWEST);
				break;

			case CHARCOAL:
				new PRICE(type, 2.0D, 64, LOW);
				break;

				
			case PUMPKIN:
				new PRICE(type, 0.1D, 1600, NORMAL);
				break;
			case CARVED_PUMPKIN:
				new PRICE(type, 0.5D, 64, LOW);
				break;
			case JACK_O_LANTERN:
				new PRICE(type, 1.0D, 32, LOW);
				break;
			case PUMPKIN_SEEDS:
				new PRICE(type, 0.02D, 320, HIGHEST);
				break;
			case GLISTERING_MELON_SLICE:
				new PRICE(type, 1.0D, 64, LOW);
				break;
			case MELON_SLICE:
				new PRICE(type, 0.03D, 6400, NORMAL);
				break;
			case MELON:
				new PRICE(type, 0.3D, 256, NORMAL);
				break;
			case MELON_SEEDS:
				new PRICE(type, 0.02D, 320, HIGHEST);
				break;

			case BEETROOT:
				new PRICE(type, 0.1D, 3200, HIGH);
				break;
			case BEETROOT_SEEDS:
				new PRICE(type, 0.02D, 320, HIGHEST);
				break;
			
			case CAKE:
				new PRICE(type, 5.0D, 10, NORMAL);
				break;
				
			case BREAD:
				new PRICE(type, 0.3D, 64, LOW);
				break;
			case WHEAT:
				new PRICE(type, 0.1D, 3200, NORMAL);
				break;
			case WHEAT_SEEDS:
				new PRICE(type, 0.02D, 320, HIGHEST);
				break;
			
			case COCOA_BEANS:
				new PRICE(type, 0.1D, 3200, NORMAL);
				break;
			
			case COOKIE:
				new PRICE(type, 0.1D, 640, NORMAL);
				break;

				
			case BLACK_DYE:
				new PRICE(type, 0.25D, 64, LOW);
				break;
			case BLUE_DYE:
				new PRICE(type, 0.25D, 64, LOW);
				break;
			case BROWN_DYE:
				new PRICE(type, 0.25D, 64, LOW);
				break;
			case CYAN_DYE:
				new PRICE(type, 0.25D, 64, LOW);
				break;
			case GRAY_DYE:
				new PRICE(type, 0.25D, 64, LOW);
				break;
			case GREEN_DYE:
				new PRICE(type, 0.25D, 64, LOW);
				break;
			case LIGHT_BLUE_DYE:
				new PRICE(type, 0.25D, 64, LOW);
				break;
			case LIGHT_GRAY_DYE:
				new PRICE(type, 0.25D, 64, LOW);
				break;
			case LIME_DYE:
				new PRICE(type, 0.25D, 64, LOW);
				break;
			case MAGENTA_DYE:
				new PRICE(type, 0.25D, 64, LOW);
				break;
			case PINK_DYE:
				new PRICE(type, 0.25D, 64, LOW);
				break;
			case ORANGE_DYE:
				new PRICE(type, 0.25D, 64, LOW);
				break;
			case PURPLE_DYE:
				new PRICE(type, 0.25D, 64, LOW);
				break;
			case RED_DYE:
				new PRICE(type, 0.25D, 64, LOW);
				break;
			case WHITE_DYE:
				new PRICE(type, 0.25D, 64, LOW);
				break;
			case YELLOW_DYE:
				new PRICE(type, 0.25D, 64, LOW);
				break;
				
			case STICK:
				new PRICE(type, 0.1D, 640, HIGH);
				break;
				
			case BOWL:
				new PRICE(type, 0.75D, 64, LOW);
				break;
				
			case LEATHER:
				new PRICE(type, 1.0D, 640, NORMAL);
				break;
			case RABBIT_HIDE:
				new PRICE(type, 1.0D, 640, NORMAL);
				break;
			case RABBIT_FOOT:
				new PRICE(type, 2.0D, 64, NORMAL);
				break;
				
			case ENDER_PEARL:
				new PRICE(type, 5.0D, 320, NORMAL);
				break;
				
			case ENDER_EYE:
				new PRICE(type, 7.5D, 64, NORMAL);
				break;
			
			case BOOK:
				new PRICE(type, 1.5D, 64, NORMAL);
				break;
				
			case PAPER:
				new PRICE(type, 0.1D, 128, NORMAL);
				break;
				
			case SLIME_BALL:
				new PRICE(type, 0.5D, 640, NORMAL);
				break;
				
			case INK_SAC:
				new PRICE(type, 1.0D, 320, NORMAL);
				break;
				
			case GLOW_INK_SAC:
				new PRICE(type, 10.0D, 64, LOW);
				break;
				
			case NETHER_WART:
				new PRICE(type, 0.1D, 6400, HIGH);
				break;
				
			case SWEET_BERRIES:
				new PRICE(type, 0.1D, 640, LOW);
				break;
			case GLOW_BERRIES:
				new PRICE(type, 0.2D, 640, LOW);
				break;

			case FIRE_CHARGE:
				new PRICE(type, 1.0D, 128, LOW);
				break;
				
			case IRON_HORSE_ARMOR:
				new PRICE(type, 50.0D, 3, LOWEST);
				break;
			case GOLDEN_HORSE_ARMOR:
				new PRICE(type, 100.0D, 3, LOWEST);
				break;
			case DIAMOND_HORSE_ARMOR:
				new PRICE(type, 150.0D, 3, LOWEST);
				break;
				
			case MUSIC_DISC_11:
				new PRICE(type, 100.0D, 1, LOWEST);
				break;
			case MUSIC_DISC_13:
				new PRICE(type, 100.0D, 1, LOWEST);
				break;
			case MUSIC_DISC_BLOCKS:
				new PRICE(type, 100.0D, 1, LOWEST);
				break;
			case MUSIC_DISC_CAT:
				new PRICE(type, 100.0D, 1, LOWEST);
				break;
			case MUSIC_DISC_CHIRP:
				new PRICE(type, 100.0D, 1, LOWEST);
				break;
			case MUSIC_DISC_FAR:
				new PRICE(type, 100.0D, 1, LOWEST);
				break;
			case MUSIC_DISC_MALL:
				new PRICE(type, 100.0D, 1, LOWEST);
				break;
			case MUSIC_DISC_MELLOHI:
				new PRICE(type, 100.0D, 1, LOWEST);
				break;
			case MUSIC_DISC_PIGSTEP:
				new PRICE(type, 100.0D, 1, LOWEST);
				break;
			case MUSIC_DISC_STAL:
				new PRICE(type, 100.0D, 1, LOWEST);
				break;
			case MUSIC_DISC_STRAD:
				new PRICE(type, 100.0D, 1, LOWEST);
				break;
			case MUSIC_DISC_WAIT:
				new PRICE(type, 100.0D, 1, LOWEST);
				break;
			case MUSIC_DISC_WARD:
				new PRICE(type, 100.0D, 1, LOWEST);
				break;

			case CHORUS_FLOWER:
				new PRICE(type, 0.5D, 320, NORMAL);
				break;
			case CHORUS_FRUIT:
				new PRICE(type, 0.25D, 640, NORMAL);
				break;

			case HEART_OF_THE_SEA:
				new PRICE(type, 100.0D, 1, LOWEST);
				break;
			
			case NAUTILUS_SHELL:
				new PRICE(type, 10.0D, 32, LOW);
				break;
				
			default:
				break;
			}
		}
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String aliases, String[] args)
	{
		return new ArrayList<>();
	}
	
	private static final int PATH_WIDTH = 5;
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String aliases, String[] args)
	{
		int x = -65, z = 65;
		int k = 0;
		
		int n = 0;
		
		for(int j = 0; j < Land.Type.values().length; j++)
		{
			Land.Type type = Land.Type.values()[j];
			final int size = type.getSize();
			
			z += PATH_WIDTH * SOUTH.z + size * SOUTH.z;
			
			k = (1 + k) * 2 + 1;
			
//			x += PATH_WIDTH * direction.x + size * direction.x; 
//			z += PATH_WIDTH * direction.z + size * direction.z;
//			
			for(int i = 0; i < Direction.values().length; i++)
			{
				Direction direction = Direction.values()[i];
				
				for(int l = 0; l < k; l++)
				{
					if(l == 0 && i == 0)
					{
						new Land(x, z, type, n++);
						continue;
					}
					
					x += PATH_WIDTH * direction.x + size * direction.x; 
					z += PATH_WIDTH * direction.z + size * direction.z;
					
					new Land(x, z, type, n++);
				}
			}
		}
		
		if(true)
		{
			sender.sendMessage("Unknown command. Type \"/help\" for help.");
			return true;
		}
		
		File file = Config.getFile(Type.PRICE, true);
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		
		for(PRICE price : PRICE.LIST)
		{
			Bukkit.broadcastMessage(price.toString());
			config.set(price.type + ".liquidity", price.liquidity);
			config.set(price.type + ".market-cap", price.marketCap);
			config.set(price.type + ".limit", price.sellLimit);
			config.set(price.type + ".max", price.maxPrice);
		}
		
		try
		{
			config.save(file);
			Bukkit.broadcastMessage("top");
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		
		return true;
	}
}