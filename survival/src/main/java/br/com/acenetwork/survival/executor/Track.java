package br.com.acenetwork.survival.executor;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import br.com.acenetwork.commons.manager.Message;
import br.com.acenetwork.commons.player.CommonPlayer;
import br.com.acenetwork.commons.player.craft.CraftCommonPlayer;
import br.com.acenetwork.survival.Util;
import br.com.acenetwork.survival.event.TrackEvent;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;

public class Track implements TabExecutor
{
	public enum TrackType
	{
		COBBLESTONE, OBSIDIAN, IRON;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String aliases, String[] args)
	{
		List<String> list = new ArrayList<>();
		
		if(!(sender instanceof Player))
		{
			return list;
		}
		
		Player p = (Player) sender;
		
		if(args.length == 1)
		{
			if("all".startsWith(args[0].toLowerCase()))
			{
				list.add("all");
			}
			
			for(Player all : Util.getOnlinePlayersAndNPCs())
			{
				if(all.getName().toLowerCase().startsWith(args[0].toLowerCase()) && p.canSee(all) && all != p)
				{
					list.add(all.getName());
				}
			}
		}
		
		return list;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String aliases, String[] args)
	{
		ResourceBundle bundle = ResourceBundle.getBundle("message");
		
		if(!(sender instanceof Player))
		{
			TextComponent text = new TextComponent(bundle.getString("commons.cmds.cant-perform-command"));
			text.setColor(ChatColor.RED);
			sender.spigot().sendMessage(text);
			return true;
		}
		
		Player p = (Player) sender;
		CommonPlayer cp = CraftCommonPlayer.get(p);
		
		bundle = ResourceBundle.getBundle("message", cp.getLocale());
		
		List<Player> targetList = new ArrayList<>(Util.getOnlinePlayersAndNPCs().stream().
			filter(x -> p.canSee(x)).collect(Collectors.toList()));
		
		if(args.length == 1 && !args[0].equalsIgnoreCase("all"))
		{
			Player t = targetList.stream().filter(x -> x.getName().equalsIgnoreCase(args[0])).findAny().orElse(null);
			
			if(p == t)
			{
				TextComponent text = new TextComponent(bundle.getString("raid.cmd.track.cant-track-yourself"));
				text.setColor(ChatColor.RED);
				sender.spigot().sendMessage(text);
				return true;
			}

			if(t == null || !p.canSee(t))
			{
				TextComponent text = new TextComponent(bundle.getString("commons.cmds.player-not-found"));
				text.setColor(ChatColor.RED);
				sender.spigot().sendMessage(text);
				return true;
			}
			
			targetList.clear();
			targetList.add(t);
		}
		else if(args.length > 1)
		{
			TextComponent[] extra = new TextComponent[1];
			
			extra[0] = new TextComponent("/" + aliases);
			extra[0].addExtra(" [" + bundle.getString("commons.words.player") + "]");
			
			TextComponent text = Message.getTextComponent(bundle.getString("commons.cmd.wrong-syntax-try"), extra);
			text.setColor(ChatColor.RED);
			sender.spigot().sendMessage(text);
			return true;
		}

		Block middle = p.getLocation().getBlock().getRelative(BlockFace.DOWN);
		Material arms;
		Material end;
		int trackDistance = 25;
		boolean selfDestroy = false;
		int[] range = new int[] {0, 0, 0, 0};
		
		final TrackType type;
		
		switch(middle.getType())
		{
			case DIAMOND_BLOCK:
				arms = Material.OBSIDIAN;
				end = Material.GOLD_BLOCK;
				type = TrackType.OBSIDIAN;
				break;
			case EMERALD_BLOCK:	
				arms = Material.IRON_BLOCK;
				end = Material.GOLD_BLOCK;
				trackDistance = 50;
				type = TrackType.IRON;
				break;
			case OBSIDIAN:
				arms = Material.COBBLESTONE;
				end = Material.STONE;
				selfDestroy = true;
				type = TrackType.COBBLESTONE;
				break;
			default: 
				TextComponent text = new TextComponent(bundle.getString("raid.cmd.track.not-valid"));
				text.setColor(ChatColor.RED);
				sender.spigot().sendMessage(text);
				p.sendMessage("§7https://www.brawl.com/wiki/tracking-raid/");
				return true;
		}

		List<Block> blockList = new ArrayList<Block>();
		
		blockList.add(middle);

		for(int i = 0; i < range.length; i++)
		{
			List<Block> armList = new ArrayList<>();
			
			for(int j = 1; ; j++)
			{
				Block b = middle.getRelative(getBlockFaceByInt(i), j);

				range[i] += trackDistance;
				
				if(b.getType() == end)
				{
					blockList.add(b);
					break;
				}
				else if(b.getType() != arms)
				{
					range[i] = 0;
					blockList.removeAll(armList);
					break;
				}
				
				armList.add(b);
				blockList.add(b);
			}
		}
		
		if(blockList.size() <= 1)
		{
			TextComponent text = new TextComponent(bundle.getString("raid.cmd.track.not-valid"));
			text.setColor(ChatColor.RED);
			sender.spigot().sendMessage(text);
			p.sendMessage("§7https://www.brawl.com/wiki/tracking-raid/");
			return true;
		}
		
		TrackEvent e = new TrackEvent(p, type, middle, blockList);
		Bukkit.getPluginManager().callEvent(e);
		
		if(e.isCancelled())
		{
			return true;
		}
		
		if(selfDestroy)
		{
			for(Block b : blockList)
			{
				b.setType(Material.AIR);
			}
		}

		String result = "";
		
		final int x1 = middle.getX();
		final int z1 = middle.getZ();
		
		for(Player t : targetList)
		{
			if(t == p)
			{
				continue;
			}
			
			String targetDirection = "";

			int x2 = t.getLocation().getBlockX();
			int z2 = t.getLocation().getBlockZ();

			if(x1 == x2)
			{
				x2++;
			}
			
			if(z1 == z2)
			{
				z2++;
			}

			if(z2 >= z1 && z2 <= z1 + range[1])
			{
				targetDirection += "S";
			}
			else if(z2 <= z1 && z2 >= z1 - range[0])
			{
				targetDirection += "N";
			}

			if(x2 >= x1 && x2 <= x1 + range[2])
			{
				targetDirection += "E";
			}
			else if(x2 <= x1 && x2 >= x1 - range[3])
			{
				targetDirection += "W";
			}

			if(!targetDirection.isEmpty())
			{
				result += "§f" + t.getName() + ": " + (targetDirection.length() == 1 ? "§e" : "§a") 
					+ targetDirection + "§f, ";
			}
		}

		if(result.length() > 2)
		{
			result = result.substring(0, result.length() - 2);
		}

		p.sendMessage(" ");
		p.sendMessage("§a[TRACK]");
		
		TextComponent text = new TextComponent(bundle.getString("commons.words.location"));
		
		text.setColor(ChatColor.WHITE);
		text.addExtra(new ComponentBuilder(":").color(ChatColor.GRAY).getCurrentComponent());
		text.addExtra(" " + middle.getX());
		text.addExtra(new ComponentBuilder(",").color(ChatColor.GRAY).getCurrentComponent());
		text.addExtra(" " + middle.getY());
		text.addExtra(new ComponentBuilder(",").color(ChatColor.GRAY).getCurrentComponent());
		text.addExtra(" " + middle.getZ());
		
		p.spigot().sendMessage(text);
		
		text = new TextComponent(bundle.getString("commons.words.range"));
		
		text.setColor(ChatColor.WHITE);
		text.addExtra(new ComponentBuilder(": " + bundle.getString("commons.words.north")).color(ChatColor.GRAY).getCurrentComponent());
		text.addExtra(" " + range[0]);
		text.addExtra(new ComponentBuilder(", " + bundle.getString("commons.words.south")).color(ChatColor.GRAY).getCurrentComponent());
		text.addExtra(" " + range[1]);
		text.addExtra(new ComponentBuilder(", " + bundle.getString("commons.words.east")).color(ChatColor.GRAY).getCurrentComponent());
		text.addExtra(" " + range[2]);
		text.addExtra(new ComponentBuilder(", " + bundle.getString("commons.words.west")).color(ChatColor.GRAY).getCurrentComponent());
		text.addExtra(" " + range[3]);
		
		p.spigot().sendMessage(text);
		
		text = new TextComponent();
		
		text.setColor(ChatColor.WHITE);
		text.addExtra(" --- ");
		text.addExtra(new ComponentBuilder(bundle.getString("commons.words.results")).color(ChatColor.GRAY).getCurrentComponent());
		text.addExtra(" --- ");
		
		p.spigot().sendMessage(text);
		
		p.sendMessage(result);
		
		return false;
	}

	private BlockFace getBlockFaceByInt(int i)
	{
		switch(i)
		{
			case 0:
				return BlockFace.NORTH;
			case 1:
				return BlockFace.SOUTH;
			case 2:
				return BlockFace.EAST;
			case 3:
				return BlockFace.WEST;
			default:
				return null;
		}
	}
}