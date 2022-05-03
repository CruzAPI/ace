package br.com.acenetwork.survival.executor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import br.com.acenetwork.commons.executor.Balance;
import br.com.acenetwork.commons.manager.CommonsConfig;
import br.com.acenetwork.commons.manager.Message;
import br.com.acenetwork.commons.player.CommonPlayer;
import br.com.acenetwork.commons.player.craft.CraftCommonPlayer;
import br.com.acenetwork.survival.manager.Config;
import br.com.acenetwork.survival.manager.Config.Type;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

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
		
		File priceFile = Config.getFile(Type.PRICE, false);
		YamlConfiguration priceConfig = YamlConfiguration.loadConfiguration(priceFile);

		File playerFile = CommonsConfig.getFile(CommonsConfig.Type.BALANCE_RAID_PLAYER, true, p.getUniqueId());
		YamlConfiguration playerConfig = YamlConfiguration.loadConfiguration(playerFile);
		
		Material type;

		if(args.length == 0)
		{
			ItemStack item = p.getInventory().getItemInMainHand();
			type = item.getType();

			if(type == Material.AIR)
			{
				TextComponent text = new TextComponent(bundle.getString("raid.cmd.sell.need-hoolding-item"));
				text.setColor(ChatColor.RED);
				sender.spigot().sendMessage(text);
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
				TextComponent text = new TextComponent(bundle.getString("raid.cmd.price.item-not-exist"));
				text.setColor(ChatColor.RED);
				sender.spigot().sendMessage(text);
				return true;
			}
		}
		else
		{
			TextComponent[] extra = new TextComponent[1];
			
			extra[0] = new TextComponent("/" + aliases
					+ " [" + bundle.getString("commons.words.item-name").toUpperCase().replace(' ', '_') + "]");
			
			TextComponent text = Message.getTextComponent(bundle.getString("commons.cmds.wrong-syntax-try"), extra);
			text.setColor(ChatColor.RED);
			sender.spigot().sendMessage(text);
			return true;
		}

		String key = type.toString();

		if(priceConfig.contains(key))
		{
			int amountSold = playerConfig.getInt(key);
			double aceShards = priceConfig.getDouble(key + ".ace-shards");
			int marketCap = priceConfig.getInt(key + ".market-cap");
			int limit = priceConfig.getInt(key + ".limit");
			
			String price = Balance.getDecimalFormat().format(aceShards / marketCap);
			
			p.sendMessage(String.format("§a%s: §e%s§a (%s/%s)", key, price, amountSold, limit));
		}
		else
		{
			TextComponent text = new TextComponent(bundle.getString("raid.cmd.sell.item-not-for-sale"));
			text.setColor(ChatColor.RED);
			sender.spigot().sendMessage(text);
		}

		return false;
	}
}