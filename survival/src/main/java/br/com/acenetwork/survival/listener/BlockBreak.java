package br.com.acenetwork.survival.listener;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import br.com.acenetwork.commons.manager.Message;
import br.com.acenetwork.commons.player.CommonPlayer;
import br.com.acenetwork.commons.player.craft.CraftCommonPlayer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class BlockBreak implements Listener
{
	List<Block> list = new ArrayList<>();

	@EventHandler
	public void on(BlockPlaceEvent e)
	{
		Block b = e.getBlock();
		
		switch(b.getType())
		{
			case DEEPSLATE_DIAMOND_ORE:
			case DEEPSLATE_EMERALD_ORE:
			case EMERALD_ORE:
			case DIAMOND_ORE:
			case ANCIENT_DEBRIS:
				list.add(b);
				break;
			default:
				return;
		}
	}

	@EventHandler
	public void on(BlockBreakEvent e)
	{
		Block b = e.getBlock();
		Player p = e.getPlayer();
		
		Material type;
		
		switch(b.getType())
		{
			case EMERALD_ORE:
			case DIAMOND_ORE:
			case ANCIENT_DEBRIS:
				type = b.getType();
				break;
			case DEEPSLATE_DIAMOND_ORE:
				type = Material.DIAMOND_ORE;
				break;
			case DEEPSLATE_EMERALD_ORE:
				type = Material.EMERALD_ORE;
				break;
			default:
				return;
		}
		
		b.getRelative(BlockFace.UP);		

		if(b.getType().toString().contains(type.toString()) && !list.contains(b))
		{
			int n = find(b, type);

			for(CommonPlayer cpall : CraftCommonPlayer.SET)
			{
				if(cpall.hasPermission("alert.orefound"))
				{
					Player all = cpall.getPlayer();
					
					TextComponent component = new TextComponent(
						Message.getMessage(all.getLocale(), "alert.orefound", p.getName(), n + "x " + type.toString()));

					TextComponent extra = new TextComponent(" §e" + Message.getMessage(all.getLocale(), "commons.teleport"));
					extra.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp " + p.getName()));
					component.addExtra(extra);

					all.spigot().sendMessage(component);
				}
			}
		}

		list.remove(b);
	}

	private int find(Block block, Material type)
	{
		int r = 1;
		int n = 0;

		for(int x = -r; x <= r; x++)
		{
			for(int y = -r; y <= r; y++)
			{
				for(int z = -r; z <= r; z++)
				{
					Block b = block.getWorld().getBlockAt(x + block.getX(), y + block.getY(), z + block.getZ());

					if(b.getType().toString().contains(type.toString()) && !list.contains(b))
					{
						list.add(b);
						n++;
						n += find(b, type);
					}
				}
			}
		}

		return n;
	}
}