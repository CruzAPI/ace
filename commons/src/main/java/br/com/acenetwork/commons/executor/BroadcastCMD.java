package br.com.acenetwork.commons.executor;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.ResourceBundle;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import br.com.acenetwork.commons.Commons;
import br.com.acenetwork.commons.manager.CommonsConfig;
import br.com.acenetwork.commons.manager.CommonsConfig.Type;
import br.com.acenetwork.commons.manager.Message;
import br.com.acenetwork.commons.player.CommonPlayer;
import br.com.acenetwork.commons.player.craft.CraftCommonPlayer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class BroadcastCMD implements TabExecutor
{
	public static final List<String> BROADCASTS = new ArrayList<>();
	
	public BroadcastCMD()
	{
		BROADCASTS.add("commons.broadcast.1");
		BROADCASTS.add("commons.broadcast.2");
		BROADCASTS.add("commons.broadcast.3");
		BROADCASTS.add("commons.broadcast.4");
		BROADCASTS.add("commons.broadcast.5");
		BROADCASTS.add("commons.broadcast.6");
		suffle();
		
		Bukkit.getScheduler().scheduleSyncRepeatingTask(Commons.getPlugin(), new Runnable()
		{
			int i = 0;
			
			@SuppressWarnings("deprecation")
			@Override
			public void run()
			{
				if(BROADCASTS.isEmpty())
				{
					return;
				}
				
				if(i >= BROADCASTS.size())
				{
					i = 0;
					suffle();
				}
				
				String key = BROADCASTS.get(i);
				
				List<CommandSender> senderList = new ArrayList<CommandSender>(Bukkit.getOnlinePlayers());
				senderList.add(Bukkit.getConsoleSender());
				
				for(CommandSender sender : senderList)
				{
					Locale locale = Locale.getDefault();
					
					if(sender instanceof Player)
					{
						Player p = (Player) sender;
						CommonPlayer cp = CraftCommonPlayer.get(p);
						
						locale = cp.getLocale();
						
						File file = CommonsConfig.getFile(Type.PLAYER, false, p.getUniqueId());
						YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
						
						List<String> mutedBroadcasts = config.contains("muted-broadcasts") ? config.getStringList("muted-broadcasts") : new ArrayList<>();
						
						if(mutedBroadcasts.contains(key))
						{
							continue;
						}
					}
					
					ResourceBundle bundle = ResourceBundle.getBundle("message", locale);
					
					TextComponent extra1 = new TextComponent("➟ ");
					extra1.setColor(ChatColor.GRAY);
					
					TextComponent extra3 = new TextComponent("✕");
					extra3.setColor(ChatColor.RED);
					extra3.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mutebroadcast " + key));
					TextComponent label = new TextComponent(bundle.getString("commons.cmd.mutebroadcast.click-to-mute"));
					label.setColor(ChatColor.RED);
					extra3.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(label).create()));
					
					TextComponent extra2 = new TextComponent(" ");
					extra2.setColor(ChatColor.DARK_GRAY);
					extra2.addExtra("[");
					extra2.addExtra(extra3);
					extra2.addExtra("]");
					
					TextComponent text = new TextComponent();
					TextComponent[] extra = new TextComponent[0];
					
					if(key.equals("commons.broadcast.1"))
					{
						text.setColor(ChatColor.LIGHT_PURPLE);
						
						extra = new TextComponent[2];
						
						extra[0] = new TextComponent("$BTA");
						extra[0].setColor(ChatColor.DARK_PURPLE);
						
						extra[1] = new TextComponent("https://www.acetokennetwork.com");
						extra[1].setColor(ChatColor.GRAY);
					}
					else if(key.equals("commons.broadcast.2"))
					{
						text.setColor(ChatColor.AQUA);
						
						extra = new TextComponent[2];
						
						extra[0] = new TextComponent("DISCORD");
						extra[0].setColor(ChatColor.DARK_AQUA);
						
						extra[1] = new TextComponent("https://discord.gg/EYV538gQt7");
						extra[1].setColor(ChatColor.GRAY);
					}
					else if(key.equals("commons.broadcast.3"))
					{
						text.setColor(ChatColor.AQUA);
						
						extra = new TextComponent[2];
						
						extra[0] = new TextComponent(bundle.getString("commons.words.bothering"));
						extra[0].setColor(ChatColor.DARK_AQUA);
						
						extra[1] = new TextComponent("/ignore");
						extra[1].setColor(ChatColor.GRAY);
					}
					else if(key.equals("commons.broadcast.4"))
					{
						text.setColor(ChatColor.GOLD);
						
						extra = new TextComponent[1];
						
						Date date = new Date();
						date.setHours(18);
						date.setMinutes(0);
						
						DateFormat df = DateFormat.getTimeInstance(DateFormat.SHORT, bundle.getLocale());
						
						extra[0] = new TextComponent(df.format(date) + " UTC -3");
						extra[0].setColor(ChatColor.YELLOW);
					}
					else if(key.equals("commons.broadcast.5"))
					{
						text.setColor(ChatColor.GRAY);
						
						extra = new TextComponent[2];
						
						extra[0] = new TextComponent("/balance");
						extra[0].setColor(ChatColor.WHITE);
						
						extra[1] = new TextComponent("/balance");
						extra[1].addExtra(" [" + bundle.getString("commons.words.player") + "]");
						extra[1].setColor(ChatColor.WHITE);
					}
					else if(key.equals("commons.broadcast.6"))
					{
						text.setColor(ChatColor.GREEN);
						
						extra = new TextComponent[1];
						
						extra[0] = new TextComponent("/wallet");
						extra[0].setColor(ChatColor.DARK_GREEN);
					}
					else if(key.equals("raid.broadcast.1"))
					{
						text.setColor(ChatColor.AQUA);
						
						extra = new TextComponent[1];
						
						extra[0] = new TextComponent("20%");
						extra[0].setColor(ChatColor.DARK_AQUA);
					}
					else if(key.equals("raid.broadcast.2"))
					{
						text.setColor(ChatColor.RED);
						
						extra = new TextComponent[1];
						
						extra[0] = new TextComponent("1%");
						extra[0].setColor(ChatColor.DARK_RED);
					}
					else if(key.equals("raid.broadcast.3"))
					{
						text.setColor(ChatColor.YELLOW);
						
						extra = new TextComponent[3];
						
						extra[0] = new TextComponent(bundle.getString("commons.words.price"));
						extra[0].setColor(ChatColor.GOLD);
						
						extra[1] = new TextComponent("/price");
						extra[1].setColor(ChatColor.GOLD);
						
						extra[2] = new TextComponent("/price");
						extra[2].addExtra(" [" + bundle.getString("commons.words.item-name").replace(' ', '_').toUpperCase() + "]");
						extra[2].setColor(ChatColor.GOLD);

					}
					else if(key.equals("raid.broadcast.4"))
					{
						text.setColor(ChatColor.AQUA);
					}
					else if(key.equals("raid.broadcast.5"))
					{
						text.setColor(ChatColor.GREEN);
						
						extra = new TextComponent[1];
						
						extra[0] = new TextComponent("/track");
						extra[0].setColor(ChatColor.DARK_GREEN);
					}
					else if(key.equals("raid.broadcast.6"))
					{
						text.setColor(ChatColor.AQUA);
						
						extra = new TextComponent[3];
						
						extra[0] = new TextComponent(bundle.getString("commons.words.ace-shards"));
						extra[0].setColor(ChatColor.DARK_AQUA);
						
						extra[1] = new TextComponent("/sell");
						extra[1].setColor(ChatColor.GRAY);
						
						extra[2] = new TextComponent("/sellall");
						extra[2].setColor(ChatColor.GRAY);
					}
					else if(key.equals("raid.broadcast.7"))
					{
						text.setColor(ChatColor.YELLOW);
						
						extra = new TextComponent[1];
						
						extra[0] = new TextComponent("/pricechart");
						extra[0].setColor(ChatColor.GOLD);
						extra[0].setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/pricechart"));
						extra[0].setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(bundle
								.getString("raid.cmd.pricechart.click-to-see")).color(ChatColor.GOLD).create()));
					}
					
					text.addExtra(extra1);
					text.addExtra(Message.getTextComponent(bundle.getString(key), extra));
					text.addExtra(extra2);
					
					sender.spigot().sendMessage(text);
				}
				
				i++;
			}
		}, 5L * 60L * 20L, 5L * 60L * 20L);
//		}, 50L, 50L);
	}
	
	public static void suffle()
	{
		List<String> list = new ArrayList<>(BROADCASTS);
		
		BROADCASTS.clear();
		
		Random r = new Random();
		
		while(!list.isEmpty())
		{
			BROADCASTS.add(list.remove(r.nextInt(list.size())));
		}
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String aliases, String[] args)
	{
		return new ArrayList<>();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String aliases, String[] args)
	{
		CommonPlayer cp = null;
		boolean hasPermission = true;
		ResourceBundle bundle = ResourceBundle.getBundle("message");
		
		if(sender instanceof Player)
		{
			Player p = (Player) sender;
			cp = CraftCommonPlayer.get(p);
			hasPermission = cp.hasPermission("cmd.broadcast");
			bundle = ResourceBundle.getBundle("message", cp.getLocale());
		}
		
		if(!hasPermission)
		{
			TextComponent text = new TextComponent(bundle.getString("commons.cmds.permission"));
			text.setColor(ChatColor.RED);
			sender.spigot().sendMessage(text);
			return true;
		}
		
		if(args.length > 0)
		{
		 	String msg = "";

		 	for(int i = 0; i < args.length; i++)			
		 	{
		 		msg += args[i] + " ";
		 	}

		 	msg = msg.substring(0, msg.length() - 1);
		 	
		 	Bukkit.broadcastMessage("§b§lBroadcast§7 » §f" + msg.replace('&', '§'));
		}
		else
		{
			TextComponent[] extra = new TextComponent[1];
			
			extra[0] = new TextComponent("/" + aliases + " <" + bundle.getString("commons.words.message") + "...>");
			
			TextComponent text = Message.getTextComponent(bundle.getString("commons.cmds.wrong-syntax-try"), extra);
			text.setColor(ChatColor.RED);
			sender.spigot().sendMessage(text);
		}
		
		return false;
	}
}