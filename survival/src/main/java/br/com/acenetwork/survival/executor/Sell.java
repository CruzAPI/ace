package br.com.acenetwork.survival.executor;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import br.com.acenetwork.commons.executor.Balance;
import br.com.acenetwork.commons.manager.CommonsConfig;
import br.com.acenetwork.commons.manager.Message;
import br.com.acenetwork.commons.player.CommonPlayer;
import br.com.acenetwork.commons.player.craft.CraftCommonPlayer;
import br.com.acenetwork.survival.manager.AmountPrice;
import br.com.acenetwork.survival.manager.Config;
import br.com.acenetwork.survival.manager.Config.Type;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

public class Sell implements TabExecutor
{
	public enum SellType
	{
		HAND, ALL;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String aliases, String[] args)
	{
		return new ArrayList<>();
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
		
		if(args.length == 0)
		{
			sell(cp, SellType.HAND);
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
	
	public static void sell(CommonPlayer cp, SellType sellType)
	{
		ResourceBundle bundle = ResourceBundle.getBundle("message", cp.getLocale());
		
		Player p = cp.getPlayer();
		
		File priceFile = Config.getFile(Type.PRICE, false);
		YamlConfiguration priceConfig = YamlConfiguration.loadConfiguration(priceFile);

		File playerFile = CommonsConfig.getFile(CommonsConfig.Type.BALANCE_RAID_PLAYER, true, p.getUniqueId());
		YamlConfiguration playerConfig = YamlConfiguration.loadConfiguration(playerFile);
		
		double balance = playerConfig.getDouble("balance");
		double maxBalance = playerConfig.getDouble("max-balance");
		
		Map<Material, AmountPrice> map = new HashMap<>();
		
		List<ItemStack> itemsToSell = new ArrayList<>();
		
		if(sellType == SellType.HAND)
		{
			itemsToSell.add(p.getInventory().getItemInMainHand());
		}
		else
		{
			for(ItemStack content : p.getInventory())
			{
				itemsToSell.add(content);
			}
		}
		
		double total = 0.0D;
		
		for(int i = 0; i < itemsToSell.size(); i++)
		{
			ItemStack item = itemsToSell.get(i);
			
			if(item == null || item.getType() == Material.AIR)
			{
				if(sellType == SellType.HAND)
				{
					TextComponent text = new TextComponent(bundle.getString("raid.cmd.sell.need-hoolding-item"));
					text.setColor(ChatColor.RED);
					p.spigot().sendMessage(text);
					return;
				}
				
				continue;
			}
			
			if(balance >= maxBalance)
			{
				if(i == 0)
				{
					TextComponent text = new TextComponent(bundle.getString("raid.cmd.sell.limit-reached"));
					text.setColor(ChatColor.RED);
					p.spigot().sendMessage(text);
					return;
				}
				
				break;
			}
			
			final Material type = item.getType();
			
			if(!priceConfig.contains(type.toString()))
			{
				if(sellType == SellType.HAND)
				{
					TextComponent text = new TextComponent(bundle.getString("raid.cmd.sell.item-not-for-sale"));
					text.setColor(ChatColor.RED);
					p.spigot().sendMessage(text);
					return;
				}
				
				continue;
			}
			
			int amountSold = playerConfig.getInt(type.toString());
			int limit = priceConfig.getInt(type + ".limit");
			
			if(amountSold >= limit)
			{
				if(sellType == SellType.HAND)
				{
					TextComponent text = new TextComponent(bundle.getString("raid.cmd.sell.cant-sell-anymore"));
					text.setColor(ChatColor.RED);
					p.spigot().sendMessage(text);
					return;
				}
				
				continue;
			}
			
			int marketCap = priceConfig.getInt(type + ".market-cap");
			double aceShards = priceConfig.getDouble(type + ".ace-shards");
			
			final double a = aceShards;
			final double b = marketCap;
			final double c = maxBalance - balance;
			
			final int x = c >= a ? Integer.MAX_VALUE : (int) Math.ceil(((b * c) / (a - c)));
			final int amountToSell = Math.max(0, Math.min(Math.min(item.getAmount(), limit - amountSold), x));
			
			item.setAmount(item.getAmount() - amountToSell);
			playerConfig.set(type.toString(), amountSold + amountToSell);
			
			double oldPrice = aceShards / marketCap;
			double newPrice = (aceShards - oldPrice * amountToSell) / (marketCap + amountToSell);
			
			final double finalPrice = (oldPrice + newPrice) / 2.0D;
			
			double shards = finalPrice * amountToSell;
			
			aceShards -= shards;
			marketCap += amountToSell;
			
			priceConfig.set(type + ".market-cap", marketCap);
			priceConfig.set(type + ".ace-shards", aceShards);
			playerConfig.set("balance", Math.min(balance += shards, maxBalance));
			
			total += shards;
			
			AmountPrice ap = map.containsKey(type) ? map.get(type) : new AmountPrice();
			
			ap.amount += amountToSell;
			ap.price += shards;
			
			map.put(type, ap);
		}
		
		try
		{
			playerConfig.save(playerFile);
			priceConfig.save(priceFile);
			
			if(map.isEmpty())
			{
				TextComponent text = new TextComponent(bundle.getString("raid.cmd.sellall.no-items-to-sell"));
				text.setColor(ChatColor.RED);
				p.spigot().sendMessage(text);
			}
			else
			{
				DecimalFormat df = new DecimalFormat("#.##", new DecimalFormatSymbols(bundle.getLocale()));
				
				df.setGroupingSize(3);
				df.setGroupingUsed(true);
				
				for(Entry<Material, AmountPrice> entry : map.entrySet())
				{
					Material type = entry.getKey();
					AmountPrice ap = entry.getValue();
					
					TextComponent[] extra = new TextComponent[2];
					
					extra[0] = new TextComponent(ap.amount + " " + type);
					extra[0].setColor(ChatColor.YELLOW);
					
					extra[1] = new TextComponent(df.format(ap.price));
					extra[1].setColor(ChatColor.YELLOW);
					
					TextComponent text = Message.getTextComponent(bundle.getString("raid.cmd.sell.item-sold"), extra);
					text.setColor(ChatColor.GREEN);
					p.spigot().sendMessage(text);
				}
				
				if(sellType == SellType.ALL)
				{
					TextComponent[] extra = new TextComponent[1];
					
					extra[0] = new TextComponent(df.format(total));
					extra[0].setColor(ChatColor.YELLOW);
					
					TextComponent text = Message.getTextComponent(bundle.getString("raid.cmd.sellall.totalizing"), extra);
					text.setColor(ChatColor.GREEN);
					p.spigot().sendMessage(text);
				}
			}
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
			TextComponent text = new TextComponent(bundle.getString("commons.unexpected-error"));
			text.setColor(ChatColor.RED);
			p.spigot().sendMessage(text);
		}
	}
}