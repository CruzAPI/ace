package br.com.acenetwork.survival.executor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import br.com.acenetwork.commons.constants.Language;
import br.com.acenetwork.commons.manager.Message;
import br.com.acenetwork.commons.player.CommonPlayer;
import br.com.acenetwork.commons.player.craft.CraftCommonPlayer;
import br.com.acenetwork.survival.Util;

public class Track implements TabExecutor
{
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
		if(!(sender instanceof Player))
		{
			sender.sendMessage(Message.getMessage(Language.ENGLISH.toString(), "cmd.cannot-perform-command"));
			return true;
		}
				
		Player p = (Player) sender;
		CommonPlayer cp = CraftCommonPlayer.get(p);
		
		List<Player> targetList = new ArrayList<>(Util.getOnlinePlayersAndNPCs().stream().
			filter(x -> p.canSee(x)).collect(Collectors.toList()));
		
		if(args.length == 1 && !args[0].equalsIgnoreCase("all"))
		{
			Player t = targetList.stream().filter(x -> x.getName().equalsIgnoreCase(args[0])).findAny().orElse(null);
			
			if(p == t)
			{
				cp.sendMessage("cmd.track.cannot-track-yourself");
				return true;
			}

			if(t == null || !p.canSee(t))
			{
				cp.sendMessage("cmd.player-not-found");
				return true;
			}
			
			targetList.clear();
			targetList.add(t);
		}
		else if(args.length > 1)
		{
			cp.sendMessage("cmd.wrong-syntax-try", "/" + aliases + " [player]");
			return true;
		}

		Block middle = p.getLocation().getBlock().getRelative(BlockFace.DOWN);
		Material arms;
		Material end;
		int trackDistance = 25;
		boolean selfDestroy = false;
		int[] range = new int[] {0, 0, 0, 0};

		switch(middle.getType())
		{
			case DIAMOND_BLOCK:
				arms = Material.OBSIDIAN;
				end = Material.GOLD_BLOCK;
				break;
			case EMERALD_BLOCK:	
				arms = Material.IRON_BLOCK;
				end = Material.GOLD_BLOCK;
				trackDistance = 50;
				break;
			case OBSIDIAN:
				arms = Material.COBBLESTONE;
				end = Material.STONE;
				selfDestroy = true;
				break;
			default: 
				cp.sendMessage("cmd.track.not-valid");
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
			cp.sendMessage("cmd.track.not-valid");
			p.sendMessage("§7https://www.brawl.com/wiki/tracking-raid/");
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
		p.sendMessage(String.format("§f%s§7:§f %s§7,§f %s§7,§f %s", Message.getMessage(p.getLocale(), "cmd.track.location"),
			middle.getX(), middle.getY(), middle.getZ()));
		p.sendMessage(String.format("§f%s§7: %s§f %s§7, %s§f %s§7, %s§f %s§7, %s§f %s", 
			Message.getMessage(p.getLocale(), "cmd.track.range"),
			Message.getMessage(p.getLocale(), "cmd.track.north"),
			range[0], 
			Message.getMessage(p.getLocale(), "cmd.track.south"),
			range[1], 
			Message.getMessage(p.getLocale(), "cmd.track.east"),
			range[2], 
			Message.getMessage(p.getLocale(), "cmd.track.west"),
			range[3]));
		p.sendMessage("§f ---§e " + Message.getMessage(p.getLocale(), "cmd.track.results") + " §f---");
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