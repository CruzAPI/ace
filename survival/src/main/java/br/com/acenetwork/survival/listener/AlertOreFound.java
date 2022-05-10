package br.com.acenetwork.survival.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

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
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class AlertOreFound implements Listener
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
					
					ResourceBundle bundle = ResourceBundle.getBundle("message", cpall.getLocale());
					
					TextComponent[] extra = new TextComponent[3];
					
					extra[0] = new TextComponent();
					extra[0].addExtra(p.getDisplayName());

					extra[1] = new TextComponent("[" + bundle.getString("commons.verbs.teleport").toUpperCase() + "]");
					extra[1].setColor(ChatColor.YELLOW);
					
					extra[2] = new TextComponent(n + " " + type.toString());
					extra[2].setColor(ChatColor.YELLOW);
					extra[2].setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp " + p.getName()));
					
					TextComponent text = Message.getTextComponent(bundle.getString("raid.event.block-break.alert-ore-found"), extra);
					text.setColor(ChatColor.GREEN);
					all.spigot().sendMessage(text);
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