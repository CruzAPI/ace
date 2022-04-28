package br.com.acenetwork.survival.executor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import br.com.acenetwork.commons.constants.Language;
import br.com.acenetwork.commons.executor.Balance;
import br.com.acenetwork.commons.manager.CommonsConfig;
import br.com.acenetwork.commons.manager.Message;
import br.com.acenetwork.commons.player.CommonPlayer;
import br.com.acenetwork.commons.player.craft.CraftCommonPlayer;
import br.com.acenetwork.survival.manager.AmountPrice;
import br.com.acenetwork.survival.manager.Config;
import br.com.acenetwork.survival.manager.Config.Type;

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
		if(!(sender instanceof Player))
		{
			sender.sendMessage(Message.getMessage(Language.ENGLISH.toString(), "cmd.cannot-perform-command"));
			return true;
		}
		
		Player p = (Player) sender;		
		CommonPlayer cp = CraftCommonPlayer.get(p);
		
		if(args.length == 0)
		{
			sell(cp, SellType.HAND);
		}
		else
		{
			cp.sendMessage("cmd.wrong-syntax-try", "/" + aliases);
		}

		return false;
	}
	
	public static void sell(CommonPlayer cp, SellType sellType)
	{
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
					cp.sendMessage("cmd.sell.need-holding-item");
					return;
				}
				
				continue;
			}
			
			if(balance >= maxBalance)
			{
				if(i == 0)
				{
					cp.sendMessage("cmd.sell.limit-reached");
					return;
				}
				
				break;
			}
			
			final Material type = item.getType();
			
			if(!priceConfig.contains(type.toString()))
			{
				if(sellType == SellType.HAND)
				{
					cp.sendMessage("cmd.sell.item-not-for-sale");
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
					cp.sendMessage("cmd.sell.cannot-sell-anymore");
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
				cp.sendMessage("cmd.sellall.no-items-to-sell");
			}
			else
			{
				for(Entry<Material, AmountPrice> entry : map.entrySet())
				{
					Material type = entry.getKey();
					AmountPrice ap = entry.getValue();
					cp.sendMessage("cmd.sell.item-sold", ap.amount, type, Balance.getDecimalFormat().format(ap.price));
				}
				
				if(sellType == SellType.ALL)
				{
					cp.sendMessage("cmd.sellall.totalizing", Balance.getDecimalFormat().format(total));
				}
			}
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
			cp.sendMessage("commons.unexpected-error");
		}
	}
}