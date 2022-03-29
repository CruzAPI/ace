package br.com.acenetwork.commons.executor;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import br.com.acenetwork.commons.constants.Language;
import br.com.acenetwork.commons.manager.Message;
import br.com.acenetwork.commons.player.CommonPlayer;
import br.com.acenetwork.commons.player.craft.CraftCommonPlayer;

public class Tp implements TabExecutor
{
	@Override
	public List<String> onTabComplete(CommandSender sender, Command arg1, String arg2, String[] args)
	{
		List<String> list = new ArrayList<>();
		
		if(sender instanceof Player)
		{
			Player p = (Player) sender;
			CommonPlayer cp = CraftCommonPlayer.get(p);
			
			if(!cp.hasPermission("cmd.tp"))
			{
				return list;
			}
		}
		
		if(args.length == 1 || args.length == 2)
		{
			for(Player all : Bukkit.getOnlinePlayers())
			{
				if(all.getName().toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
				{
					list.add(all.getName());
				}
			}
		}
		
		return list;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String aliases, String[] args)
	{
		if(!(sender instanceof Player))
		{
			sender.sendMessage(Message.getMessage(Language.ENGLISH.toString(), "cmd.cannot-perform-command"));
			return true;
		}

		Player p = (Player) sender;
		CommonPlayer cp = CraftCommonPlayer.get(p);
		
		if(!cp.hasPermission("cmd.tp"))
		{
			cp.sendMessage("cmd.permission");
			return true;
		}

		if(args.length == 1)
        {
            Player t = Bukkit.getPlayer(args[0]);

            if(t != null)
            {
                p.teleport(t);
                cp.sendMessage("cmd.tp.player-to-player", p.getName(), t.getName());

                return true;
            }
            else
            {
                cp.sendMessage("cmd.player-not-found");
            }
        }
        else if(args.length == 2)
        {
            Player t1 = Bukkit.getPlayer(args[0]);
            Player t2 = Bukkit.getPlayer(args[1]);

            if(t1 != null && t2 != null)
            {
                t1.teleport(t2);
                cp.sendMessage("cmd.tp.player-to-player", t1.getName(), t2.getName());
            }
            else
            {
                cp.sendMessage("cmd.player-not-found");
            }
        }
        else if(args.length == 3)
        {
            try
            {
                double x = Double.parseDouble(args[0]);
                double y = Double.parseDouble(args[1]);
                double z = Double.parseDouble(args[2]);

            	p.teleport(new Location(p.getWorld(), x, y, z));
                cp.sendMessage("cmd.tp.player-to-coords", p.getName(), x, y, z);
            }
            catch(NumberFormatException ex)
            {
            	cp.sendMessage("cmd.wrong-syntax-try", "/" + aliases + " <player> <x> <y> <z>");
            }
        }
        else if(args.length == 4)
        {
            Player t = Bukkit.getPlayer(args[0]);
			
            try
            {
				double x = Double.parseDouble(args[1]);
                double y = Double.parseDouble(args[2]);
                double z = Double.parseDouble(args[3]);
				
                if(t != null)
                {
                    t.teleport(new Location(t.getWorld(), x, y, z));
                    cp.sendMessage("cmd.tp.player-to-coords", t.getName(), x, y, z);
                }
                else
                {
                    cp.sendMessage("cmd.player-not-found");
                }
            }
            catch(NumberFormatException ex)
            {
            	cp.sendMessage("cmd.wrong-syntax-try", "/" + aliases + " <player> <x> <y> <z>");
            }
        }
        else
        {
            cp.sendMessage("cmd.wrong-syntax-try", "\n/" + aliases + " [player] <player> \n/" + aliases + " [player] <x> <y> <z>");
        }

        return false;
	}
}