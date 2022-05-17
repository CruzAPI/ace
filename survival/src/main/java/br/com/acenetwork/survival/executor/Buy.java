package br.com.acenetwork.survival.executor;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.ResourceBundle;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import br.com.acenetwork.commons.CommonsUtil;
import br.com.acenetwork.commons.manager.Message;
import br.com.acenetwork.commons.player.CommonPlayer;
import br.com.acenetwork.commons.player.craft.CraftCommonPlayer;
import br.com.acenetwork.survival.Main;
import br.com.acenetwork.survival.event.BuyItemEvent;
import br.com.acenetwork.survival.manager.Config;
import br.com.acenetwork.survival.manager.Config.Type;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

public class Buy implements TabExecutor
{
	private final List<String> broadcastList = new ArrayList<>();
	private final Random random = new Random();
	
	private int taskId;
	
	private final Main instance = Main.getInstance();
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String aliases, String[] args)
	{
		List<String> list = new ArrayList<>();
		
		if(sender instanceof Player)
		{
			Player p = (Player) sender;
			CommonPlayer cp = CraftCommonPlayer.get(p);
			
			if(!cp.hasPermission("cmd.buy"))
			{
				return list;
			}
		}
		
		File priceFile = Config.getFile(Type.PRICE, false);
		YamlConfiguration priceConfig = YamlConfiguration.loadConfiguration(priceFile);
		
		if(args.length == 1)
		{
			for(String type : priceConfig.getKeys(false))
			{
				if(type.toUpperCase().startsWith(args[0].toUpperCase()))
				{
					list.add(type);
				}
			}
		}

		return list;
	}
	
	public Buy()
	{
		broadcastList.add("raid.broadcast.item.1");
		broadcastList.add("raid.broadcast.item.2");
		broadcastList.add("raid.broadcast.item.3");
		broadcastList.add("raid.broadcast.item.4");
		broadcastList.add("raid.broadcast.item.5");
		broadcastList.add("raid.broadcast.item.6");
		broadcastList.add("raid.broadcast.item.7");
		broadcastList.add("raid.broadcast.item.8");
		broadcastList.add("raid.broadcast.item.9");
		broadcastList.add("raid.broadcast.item.10");
		
		File botFile = Config.getFile(Type.BOT, true);
		YamlConfiguration botConfig = YamlConfiguration.loadConfiguration(botFile);
		
		final int minDelay = botConfig.getInt("min-delay");
		final int maxDelay = botConfig.getInt("max-delay");
		
		taskId = Bukkit.getScheduler().scheduleSyncDelayedTask(instance, getRunnable(Bukkit.getConsoleSender()), minDelay + random.nextInt(maxDelay - minDelay + 1));
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String aliases, String[] args)
	{
		final ResourceBundle bundle;
		
		if(sender instanceof Player)
		{
			Player p = (Player) sender;
			CommonPlayer cp = CraftCommonPlayer.get(p);
			bundle = ResourceBundle.getBundle("message", cp.getLocale());
			
			if(!cp.hasPermission("cmd.buy"))
			{
				TextComponent text = new TextComponent(bundle.getString("commons.cmds.permission"));
				text.setColor(ChatColor.RED);
				sender.spigot().sendMessage(text);
				return true;
			}
		}
		else
		{
			bundle = ResourceBundle.getBundle("message");
		}
		
		
		if(args.length == 0)
		{
			getRunnable(sender).run();
		}
		else if(args.length == 1)
		{
			getRunnable(sender, args[0]).run();
		}
		else if(args.length == 2)
		{
			try
			{
				Runnable runnable;
				
				if(args[1].endsWith("%"))
				{
					runnable = getRunnableByPercent(sender, args[0], Integer.valueOf(args[1].substring(0, args[1].length() - 1)));
				}
				else
				{
					runnable = getRunnable(sender, args[0], Integer.valueOf(args[1]));
				}
				
				runnable.run();
			}
			catch(NumberFormatException ex)
			{
				TextComponent text = new TextComponent(bundle.getString("commons.cmds.invalid-number-format"));
            	text.setColor(ChatColor.RED);
            	sender.spigot().sendMessage(text);
			}
		}
		else
		{
			TextComponent[] extra = new TextComponent[1];
			
			extra[0] = new TextComponent("/" + aliases + " [" 
					+ bundle.getString("commons.words.item-name").replace(' ', '_').toUpperCase() + "] [" + bundle.getString("commons.words.amount") + "]");
			
			TextComponent text = Message.getTextComponent(bundle.getString("commons.cmds.wrong-syntax-try"), extra);
			text.setColor(ChatColor.RED);
			sender.spigot().sendMessage(text);
		}

		return false;
	}
	
	public Runnable getRunnable(CommandSender sender)
	{
		File file = Config.getFile(Type.PRICE, false);
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		
		ConfigurationSection section = config.getConfigurationSection("");
		
		if(section == null)
		{
			return getRunnable(sender, "");
		}
		
		List<String> list = new ArrayList<>(section.getKeys(false));
		
		if(list.isEmpty())
		{
			return getRunnable(sender, "");
		}
		
		String key = list.get(random.nextInt(list.size()));
		
		return getRunnable(sender, key);
	}
	
	public Runnable getRunnable(CommandSender sender, String key)
	{
		File botFile = Config.getFile(Type.BOT, true);
		YamlConfiguration botConfig = YamlConfiguration.loadConfiguration(botFile);
		
		int minPercent = botConfig.getInt("min-percent");
		int maxPercent = botConfig.getInt("max-percent");
		
		double percent = (minPercent + random.nextInt(maxPercent - minPercent) + random.nextDouble()) / 100.0D;
		
		return getRunnableByPercent(sender, key, percent);
	}
	
	public Runnable getRunnableByPercent(CommandSender sender, String key, double percent)
	{
		File file = Config.getFile(Type.PRICE, false);
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		
		int marketCap = config.getInt(key + ".market-cap");
		int amount = (int) (percent * marketCap);
		
		return getRunnable(sender, key, amount);
	}
	
	
	public Runnable getRunnable(CommandSender sender, String key, int amountArg)
	{
		return new Runnable()
		{
			@Override
			public void run()
			{
				if(taskId != 0)
				{
					Bukkit.getScheduler().cancelTask(taskId);
					taskId = 0;
				}
				
				ResourceBundle bundle;
				
				if(sender instanceof Player)
				{
					Player p = (Player) sender;
					bundle = ResourceBundle.getBundle("message", CommonsUtil.getLocaleFromMinecraft(p.getLocale()));
				}
				else
				{
					bundle = ResourceBundle.getBundle("message");
				}
				
				File botFile = Config.getFile(Type.BOT, true);
				YamlConfiguration botConfig = YamlConfiguration.loadConfiguration(botFile);
				
				final int minDelay = botConfig.getInt("min-delay");
				final int maxDelay = botConfig.getInt("max-delay");
				
				taskId = Bukkit.getScheduler().scheduleSyncDelayedTask(instance, getRunnable(Bukkit.getConsoleSender()), minDelay + random.nextInt(maxDelay - minDelay + 1));

				File file = Config.getFile(Type.PRICE, false);
				YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
				
				if(!config.contains(key))
				{
					TextComponent[] extra = new TextComponent[1];
					
					extra[0] = new TextComponent("\"" + key + "\"");
					
					TextComponent text = Message.getTextComponent(bundle.getString("commons.cmds.item-x-not-found"), extra);
					text.setColor(ChatColor.RED);
					sender.spigot().sendMessage(text);
					return;
				}
				
				final int oldMarketCap = config.getInt(key + ".market-cap");
				int marketCap = oldMarketCap;
				final double oldLiquidity = config.getDouble(key + ".liquidity");
				double liquidity = oldLiquidity;
				double max = config.getDouble(key + ".max");
				
				int amount = amountArg == 0 ? 1 : amountArg;
				
				double oldPrice = liquidity / marketCap;
				double newPrice = (liquidity + oldPrice * amount) / (marketCap - amount);
				final double price = (newPrice + oldPrice) / 2.0D;
				
				if(oldPrice >= max)
				{
					TextComponent[] extra = new TextComponent[1];
					
					extra[0] = new TextComponent("\"" + key + "\"");
					
					TextComponent text = Message.getTextComponent(bundle.getString("raid.cmd.buy.max-limit-reached"), extra);
					text.setColor(ChatColor.RED);
					sender.spigot().sendMessage(text);
					return;
				}
				
				liquidity += price * amount;
				marketCap -= amount;
				
				config.set(key + ".market-cap", marketCap);
				config.set(key + ".liquidity", liquidity);
				
				if(marketCap <= 0)
				{
					TextComponent text = new TextComponent(bundle.getString("raid.cmd.buy.market-cap-lower"));
					text.setColor(ChatColor.RED);
					sender.spigot().sendMessage(text);
					return;
				}
				
				final double finalPrice = liquidity / marketCap;
						
				double priceChangePercent = (finalPrice / oldPrice - 1.0D) * 100.0D;
				
				try
				{
					config.save(file);
					
					Bukkit.getPluginManager().callEvent(new BuyItemEvent(sender, key, amount, oldLiquidity, liquidity, oldMarketCap, marketCap));
					
					List<CommandSender> senderList = new ArrayList<CommandSender>(Bukkit.getOnlinePlayers());
					senderList.add(Bukkit.getConsoleSender());
					
					String bundleKey = broadcastList.get(random.nextInt(broadcastList.size()));
					
					for(CommandSender sender : senderList)
					{
						bundle = ResourceBundle.getBundle("message", Locale.getDefault());
						
						if(sender instanceof Player)
						{
							Player p = (Player) sender;
							CommonPlayer cp = CraftCommonPlayer.get(p);
							bundle = ResourceBundle.getBundle("message", cp.getLocale());
						}
						
						DecimalFormat df = new DecimalFormat("#.##", new DecimalFormatSymbols(bundle.getLocale()));
						df.setGroupingSize(3);
						df.setGroupingUsed(true);
						
						TextComponent[] extra = new TextComponent[1];
						
						extra[0] = new TextComponent(df.format(amount) + " " + key);
						extra[0].setColor(ChatColor.YELLOW);
						
						TextComponent text = Message.getTextComponent(bundle.getString(bundleKey), extra);
						text.setColor(ChatColor.GOLD);
						sender.spigot().sendMessage(text);
						sender.sendMessage("§e" + df.format(oldPrice) + "§7 » §e" + df.format(finalPrice) + " §a(+" + df.format(priceChangePercent) + "%)");
					}
				}
				catch(IOException ex)
				{
					ex.printStackTrace();
					TextComponent text = new TextComponent(bundle.getString("commons.unexpected-error"));
					text.setColor(ChatColor.RED);
					sender.spigot().sendMessage(text);
				}
			}
		};
	}
}