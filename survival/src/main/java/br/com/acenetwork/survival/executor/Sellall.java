package br.com.acenetwork.survival.executor;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import br.com.acenetwork.commons.constants.Language;
import br.com.acenetwork.commons.manager.Message;
import br.com.acenetwork.commons.player.CommonPlayer;
import br.com.acenetwork.commons.player.craft.CraftCommonPlayer;
import br.com.acenetwork.survival.manager.AmountPrice;
import br.com.acenetwork.survival.manager.Config;
import br.com.acenetwork.survival.manager.Config.Type;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Sellall implements TabExecutor
{
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
			File priceFile = Config.getFile(Type.PRICE, false);
			YamlConfiguration priceConfig = YamlConfiguration.loadConfiguration(priceFile);
			
			File playerFile = Config.getFile(Type.PLAYER, true, cp.getUUID());
			YamlConfiguration playerConfig = YamlConfiguration.loadConfiguration(playerFile);
			
			double balance = playerConfig.getDouble("balance");
			double maxBalance = playerConfig.getDouble("max-balance");

			if(balance >= maxBalance)
			{
				cp.sendMessage("cmd.sell.limit-reached");
				return true;
			}
			
			Map<Material, AmountPrice> map = new HashMap<>();
			Map<Integer, Integer> slotsToSell = new HashMap<>();
			double total = 0.0D;

			for(int slot = 0; slot < p.getInventory().getSize(); slot++)
			{
				if(balance + total >= maxBalance)
				{
					break;
				}

				final ItemStack item = p.getInventory().getItem(slot);
				
				if(item == null)
				{
					continue;
				}

				final Material type = item.getType();
				final String key = type.toString();
				final int amount = item.getAmount();
	
				int index = playerConfig.getInt(key);
	
				try
				{
					float price = priceConfig.getFloatList(key).get(index);
					
					double limit = maxBalance - balance - total;
					int amountToSell = (int) (limit / price) + (limit % price != 0 ? 1 : 0);
					amountToSell = amountToSell >= amount ? amount : amountToSell; 
					double itemTotal = price * amountToSell;

					slotsToSell.put(slot, amount - amountToSell);
					
					if(!map.containsKey(type))
					{
						map.put(type, new AmountPrice());
					}
					
					AmountPrice ap = map.get(type);
	
					ap.amount += amountToSell;
					ap.price += itemTotal;
	
					total += itemTotal;
					
					playerConfig.set(key, index + 1);
				}
				catch(IndexOutOfBoundsException e)
				{
					continue;
				}
			}
	
			try
			{	
				playerConfig.set("balance", Math.min(maxBalance, balance + total));
				playerConfig.save(playerFile);
	
				for(Entry<Integer, Integer> entry : slotsToSell.entrySet())
				{
					p.getInventory().getItem(entry.getKey()).setAmount(entry.getValue());
				}
	
				DecimalFormat df = new DecimalFormat("0.##");
				
				for(Entry<Material, AmountPrice> entry : map.entrySet())
				{
					cp.sendMessage("cmd.sell.item-sold", 
						entry.getValue().amount, entry.getKey().toString(), df.format(entry.getValue().price));
				}
				
				if(map.isEmpty())
				{
					cp.sendMessage("cmd.sellall.no-items-to-sell");
				}
				else
				{
					cp.sendMessage("cmd.sellall.totalizing", df.format(total));
				}
			}
			catch(IOException e)
			{
				e.printStackTrace();
				cp.sendMessage("commons.unexpected-error");
			}
		}
		else
		{
			cp.sendMessage("cmd.wrong-syntax-try", "/" + aliases);
		}
		

		return true;
	}
}