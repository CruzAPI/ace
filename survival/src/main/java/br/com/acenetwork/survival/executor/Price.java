package br.com.acenetwork.survival.executor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import br.com.acenetwork.commons.constants.Language;
import br.com.acenetwork.commons.manager.CommonsConfig;
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

public class Price implements TabExecutor
{
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String aliases, String[] args)
	{
		List<String> list = new ArrayList<>();
		
		if(!(sender instanceof Player))
		{
			return list;
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
		
		File priceFile = Config.getFile(Type.PRICE, false);
		YamlConfiguration priceConfig = YamlConfiguration.loadConfiguration(priceFile);

		File playerFile = CommonsConfig.getFile(CommonsConfig.Type.BALANCE_RAID_PLAYER, true, cp.getUUID());
		YamlConfiguration playerConfig = YamlConfiguration.loadConfiguration(playerFile);
		
		Material type;

		if(args.length == 0)
		{
			ItemStack item = p.getInventory().getItemInMainHand();
			type = item.getType();

			if(type == Material.AIR)
			{
				cp.sendMessage("cmd.sell.need-holding-item");
				return true;
			}
		}
		else if(args.length == 1)
		{
			try
			{
				type = Material.valueOf(args[0].toUpperCase());
			}
			catch(IllegalArgumentException e)
			{
				cp.sendMessage("cmd.price.item-not-exists");
				return true;
			}
		}
		else
		{
			cp.sendMessage("cmd.wrong-syntax-try", "/" + aliases + " [item]");
			return true;
		}

		String key = type.toString();

		if(priceConfig.contains(key))
		{
			int index = playerConfig.getInt(key);
			List<Float> priceList = priceConfig.getFloatList(key);
			String priceArray = "§a[";

			for(int i = 0; i < priceList.size(); i++)
			{
				 priceArray += (index == i ? "§e" : "§a") + priceList.get(i) + "§a, ";
			}
			
			priceArray = priceArray.substring(0, priceArray.length() - 2) + "]";
			

			p.sendMessage(String.format("§e%s§a: %s", key, priceArray));
		}
		else
		{
			cp.sendMessage("cmd.sell.item-not-for-sale");
		}

		return false;
	}
}