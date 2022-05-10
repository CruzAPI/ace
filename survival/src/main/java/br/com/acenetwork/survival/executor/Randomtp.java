package br.com.acenetwork.survival.executor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import br.com.acenetwork.commons.manager.Message;
import br.com.acenetwork.commons.player.CommonPlayer;
import br.com.acenetwork.commons.player.craft.CraftCommonPlayer;
import br.com.acenetwork.survival.manager.Config;
import br.com.acenetwork.survival.manager.Config.Type;
import br.com.acenetwork.survival.player.SurvivalPlayer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

public class Randomtp implements TabExecutor
{
	private static final Random RANDOM = new Random();
	private static final long COOLDOWN_MS = 5L * 60L * 1000L;
	
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
			run(cp);
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
	
	private void run(CommonPlayer cp)
	{
		final long currentTimeMillis = System.currentTimeMillis();
		ResourceBundle bundle = ResourceBundle.getBundle("message",  cp.getLocale());
		Player p = cp.getPlayer();
		
		if(cp instanceof SurvivalPlayer)
		{
			SurvivalPlayer sp = (SurvivalPlayer) cp;
			
			File file = Config.getFile(Type.PLAYER_INFO, true, p.getUniqueId());
			YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
			
			long cooldown = config.getLong("randomtp-cooldown");
			
			if(currentTimeMillis < cooldown)
			{
				cooldown -= currentTimeMillis;
				
				long hours = cooldown / (60L * 60L * 1000L) % 24;
				long minutes = cooldown / (60L * 1000L) % 60;
				long seconds = cooldown / 1000L % 60;
				
				TextComponent[] extra = new TextComponent[1];
				
				extra[0] = new TextComponent();
				
				if(hours != 0)
				{
					extra[0].addExtra(hours + " " + bundle.getString("commons.words.hour" + (hours == 1 ? "" : "s")));
				}
				else if(minutes != 0)
				{
					extra[0].addExtra(minutes + " " + bundle.getString("commons.words.minute" + (minutes == 1 ? "" : "s")));
				}
				else
				{
					extra[0].addExtra(seconds + " " + bundle.getString("commons.words.second" + (seconds == 1 ? "" : "s")));
				}
				
				TextComponent text = Message.getTextComponent(bundle.getString("commons.cooldown"), extra);
				text.setColor(ChatColor.RED);
				p.spigot().sendMessage(text);
				
				return;
			}
			
			if(!sp.hasSpawnProtection())
			{
				TextComponent[] extra = new TextComponent[1];
				
				extra[0] = new TextComponent("/spawn");
				
				TextComponent text = Message.getTextComponent(bundle.getString("raid.cmd.randomtp.need-spawn-protection"), extra);
				text.setColor(ChatColor.RED);
				p.spigot().sendMessage(text);
				return;
			}
			
			config.set("randomtp-cooldown", System.currentTimeMillis() + COOLDOWN_MS);
			
			try
			{
				config.save(file);
			}
			catch(IOException e)
			{
				e.printStackTrace();
				
				TextComponent text = new TextComponent(bundle.getString("commons.unexpected-error"));
				text.setColor(ChatColor.RED);
				p.spigot().sendMessage(text);
				return;
			}
		}
		
		World w = Bukkit.getWorld("world");
		
		loop1:for(int i = 0; i < 1000; i++)
		{
			int x = (RANDOM.nextInt(800) + 200) * (RANDOM.nextBoolean() ? 1 : -1);
			int z = (RANDOM.nextInt(800) + 200) * (RANDOM.nextBoolean() ? 1 : -1);
			int y = w.getHighestBlockYAt(x, z);
			
			if(i == 999)
			{
				p.teleport(new Location(w, x, y, z));
				break;
			}
			
			if(w.getBiome(x, y, z).toString().contains("OCEAN"))
			{
				continue;
			}
			
			for(SurvivalPlayer spall : CraftCommonPlayer.getAll(SurvivalPlayer.class))
			{
				if(spall == cp)
				{
					continue;
				}
				
				Player all = spall.getPlayer();
				
				if(w == all.getWorld() && new Location(w, x, y, z).distance(all.getLocation()) < 50.0D)
				{
					continue loop1;
				}
			}
			
			p.teleport(new Location(w, x, y, z));
			break;
		}
	}
}