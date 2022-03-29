package br.com.acenetwork.survival.executor;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import br.com.acenetwork.commons.constants.Language;
import br.com.acenetwork.commons.manager.Message;
import br.com.acenetwork.commons.player.CommonPlayer;
import br.com.acenetwork.commons.player.craft.CraftCommonPlayer;
import br.com.acenetwork.survival.manager.Config;
import br.com.acenetwork.survival.manager.Config.Type;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Sell implements TabExecutor
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
			final ItemStack item = p.getInventory().getItemInMainHand();
			final Material type = item.getType();
			String key = type.toString();

			final int amount = item.getAmount();
			
			if(type == Material.AIR)
			{
				cp.sendMessage("cmd.sell.need-holding-item");
				return true;
			}

			File priceFile = Config.getFile(Type.PRICE, false);
			YamlConfiguration priceConfig = YamlConfiguration.loadConfiguration(priceFile);

			File playerFile = Config.getFile(Type.PLAYER, true, cp.getUUID());
			YamlConfiguration playerConfig = YamlConfiguration.loadConfiguration(playerFile);
			
			double balance = playerConfig.getDouble("balance");
			double maxBalance = playerConfig.getDouble("max-balance");

			if(balance >= maxBalance && !p.isOp())
			{
				cp.sendMessage("cmd.sell.limit-reached");
				return true;
			}

			int index = playerConfig.getInt(key);

			try
			{
				float price = priceConfig.getFloatList(key).get(index);
				
				double limit = maxBalance - balance;
				int amountToSell;
				
				double total;

				if(p.isOp())
				{
					amountToSell = amount;
					total = price * amountToSell;
				}
				else
				{
					amountToSell = (int) (limit / price) + (limit % price != 0 ? 1 : 0);
					amountToSell = amountToSell >= amount ? amount : amountToSell;
					total = price * amountToSell;;
				}

				playerConfig.set("balance", Math.min(maxBalance, balance + total));
				playerConfig.set(key, index + 1);
				playerConfig.save(playerFile);
				
				DecimalFormat df = new DecimalFormat("0.##");

				p.getInventory().getItemInMainHand().setAmount(amount - amountToSell);
				cp.sendMessage("cmd.sell.item-sold", amountToSell, key, df.format(total));
			}
			catch(IOException e)
			{
				e.printStackTrace();
				cp.sendMessage("commons.unexpected-error");
			}
			catch(IndexOutOfBoundsException e)
			{
				if(index == 0)
				{
					cp.sendMessage("cmd.sell.item-not-for-sale");
				}
				else
				{
					cp.sendMessage("cmd.sell.cannot-sell-anymore");
				}
			}
		}
		else
		{
			cp.sendMessage("cmd.wrong-syntax-try", "/" + aliases);
		}

		return false;
	}
}