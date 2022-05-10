package br.com.acenetwork.survival.executor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import br.com.acenetwork.commons.manager.Message;
import br.com.acenetwork.commons.player.CommonPlayer;
import br.com.acenetwork.commons.player.craft.CraftCommonPlayer;
import br.com.acenetwork.survival.Main;
import br.com.acenetwork.survival.Util;
import br.com.acenetwork.survival.player.SurvivalPlayer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

public class Spawn implements TabExecutor
{
	public static final Location SPAWN_LOCATION = new Location(Bukkit.getWorld("world"), 0.5D, 66.0D, 0.5D, 0.0F, 0.0F);
	public static final Map<Player, Integer> TASK_MAP = new HashMap<>();
	
	public static final long CHANNELING_TICKS = 10L * 20L;
	public static final long LONG_CHANNELING_TICKS = 60L * 20L;
	
	public Spawn()
	{
		Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), () ->
		{
			for(SurvivalPlayer spall : CraftCommonPlayer.getAll(SurvivalPlayer.class))
			{
				Player all = spall.getPlayer();
				
				if(!Util.isSpawn(all.getLocation()))
				{
					spall.setSpawnProtection(false);
				}
			}
		}, 20L, 20L);
	}
	
	@Override
	public List<String> onTabComplete(CommandSender arg0, Command arg1, String arg2, String[] arg3)
	{
		return new ArrayList<>();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String aliases, String[] args)
	{
		final ResourceBundle bundle;
		
		if(!(sender instanceof Player))
		{
			bundle = ResourceBundle.getBundle("message");
			
			TextComponent text = new TextComponent(bundle.getString("commons.cmds.cant-perform-command"));
			text.setColor(ChatColor.RED);
			sender.spigot().sendMessage(text);
			return true;
		}
		
		Player p = (Player) sender;
		CommonPlayer cp = CraftCommonPlayer.get(p);
		
		bundle = ResourceBundle.getBundle("message", cp.getLocale());
		
		if(args.length == 0)
		{
			if(cp instanceof SurvivalPlayer)
			{
				SurvivalPlayer sp = (SurvivalPlayer) cp;
				
				if(sp.hasSpawnProtection())
				{
					p.teleport(SPAWN_LOCATION);
					return true;
				}
				
				sp.channelSpawn();
			}
			else
			{
				p.teleport(SPAWN_LOCATION);
			}
		}
		else
		{
			TextComponent[] extra = new TextComponent[1];
			
			extra[0] = new TextComponent("/" + aliases);
			
			TextComponent text = Message.getTextComponent(bundle.getString("commons.cmds.wrong-syntax-try"), extra);
			text.setColor(ChatColor.RED);
			sender.spigot().sendMessage(text);
		}
		
		return false;
	}
}