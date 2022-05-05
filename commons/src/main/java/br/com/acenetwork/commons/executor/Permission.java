package br.com.acenetwork.commons.executor;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import br.com.acenetwork.commons.CommonsUtil;
import br.com.acenetwork.commons.manager.CommonsConfig;
import br.com.acenetwork.commons.manager.CommonsConfig.Type;
import br.com.acenetwork.commons.manager.Message;
import br.com.acenetwork.commons.player.CommonPlayer;
import br.com.acenetwork.commons.player.craft.CraftCommonPlayer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

public class Permission implements TabExecutor
{
	private enum Key
	{
		PERMISSION("permission", null), GROUP("group", Type.GROUP), USER("user", Type.USER);
		
		private String name;
		private Type type;

		Key(String name, Type type)
		{
			this.name = name;
			this.type = type;
		}

		@Override
		public String toString()
		{
			return name;
		}
	}

	@Override
	public List<String> onTabComplete(CommandSender arg0, Command arg1, String arg2, String[] arg3)
	{
		return null;
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
			hasPermission = cp.hasPermission("cmd.permission");
			bundle = ResourceBundle.getBundle("message", cp.getLocale());
		}
		
		if(!hasPermission)
		{
			TextComponent text = new TextComponent(bundle.getString("commons.dont-have-permission"));
			text.setColor(ChatColor.RED);
			sender.spigot().sendMessage(text);
			return true;
		}
		
		try
		{
			if(args.length == 4 && (args[0].equalsIgnoreCase("group") || args[0].equalsIgnoreCase("g")) 
				&& args[2].equalsIgnoreCase("add"))
			{
				final String group = args[1].toLowerCase();
				final String permission = args[3].toLowerCase();
				final String configPermission = permission.replace('.', ':');

				if(!CommonsUtil.groupSyntaxIsValid(group))
				{
					TextComponent text = new TextComponent(bundle.getString("commons.cmd.permission.invalid-group-syntax"));
					text.setColor(ChatColor.RED);
					sender.spigot().sendMessage(text);
					return true;	
				}

				if(!CommonsUtil.permissionSyntaxIsValid(permission))
				{
					TextComponent text = new TextComponent(bundle.getString("commons.cmd.permission.invalid-permission-syntax"));
					text.setColor(ChatColor.RED);
					sender.spigot().sendMessage(text);
					return true;
				}

				File groupFile = CommonsConfig.getFile(Type.GROUP, true, group);
				YamlConfiguration groupConfig = YamlConfiguration.loadConfiguration(groupFile);

				groupConfig.set("permission." + configPermission, 0L);
				groupConfig.save(groupFile);
				
				TextComponent[] extra = new TextComponent[2];
				
				extra[0] = new TextComponent(permission);
				extra[0].setColor(ChatColor.YELLOW);
				
				extra[1] = new TextComponent(group);
				extra[1].setColor(ChatColor.YELLOW);

				TextComponent text = Message.getTextComponent(bundle.getString("commons.cmd.permission.permission-added-to-group"), extra);
				text.setColor(ChatColor.GREEN);
				sender.spigot().sendMessage(text);
			}
			else if(args.length == 4 && (args[0].equalsIgnoreCase("group") || args[0].equalsIgnoreCase("g")) 
				&& args[2].equalsIgnoreCase("remove"))
			{
				final String group = args[1].toLowerCase();
				final String permission = args[3].toLowerCase();
				final String configPermission = permission.replace('.', ':');
				
				File groupFile = CommonsConfig.getFile(Type.GROUP, false, group);
				YamlConfiguration groupConfig = YamlConfiguration.loadConfiguration(groupFile);
				
				if(!groupFile.exists())
				{
					TextComponent text = new TextComponent(bundle.getString("commons.cmd.permission.group-not-found"));
					text.setColor(ChatColor.RED);
					sender.spigot().sendMessage(text);
					return true;
				}

				int n = 0;

				if(configPermission.endsWith("*"))
				{
					ConfigurationSection section = groupConfig.getConfigurationSection("permission");
	
					if(section != null)
					{
						for(String key : section.getKeys(false))
						{
							if(key.startsWith(configPermission.substring(0, configPermission.length() - 1)))
							{
								groupConfig.set("permission." + key, null);
								n++;
							}
						}
					}
				}
				else if(groupConfig.contains("permission." + configPermission))
				{
					groupConfig.set("permission." + configPermission, null);
					n++;
				}

				groupConfig.save(groupFile);
				
				if(n > 0)
				{
					TextComponent[] extra = new TextComponent[2];
					
					extra[0] = new TextComponent(permission);
					extra[0].setColor(ChatColor.YELLOW);
					
					extra[1] = new TextComponent(group);
					extra[1].setColor(ChatColor.YELLOW);

					TextComponent text = Message.getTextComponent(bundle.getString("commons.cmd.permission.permission-removed-from-group"), extra);
					text.setColor(ChatColor.GREEN);
					sender.spigot().sendMessage(text);
				}
				else
				{
					TextComponent text = new TextComponent(bundle.getString("commons.cmd.permission.permission-not-found"));
					text.setColor(ChatColor.RED);
					sender.spigot().sendMessage(text);
				}
			}
			else if(args.length == 5 && (args[0].equalsIgnoreCase("group") || args[0].equalsIgnoreCase("g")) &&
				(args[2].equalsIgnoreCase("user") || args[2].equalsIgnoreCase("u")) &&
				 args[3].equalsIgnoreCase("add"))
			{
				final String group = args[1].toLowerCase();
				final String user = args[4].toLowerCase();
				
				OfflinePlayer op = Arrays.stream(Bukkit.getOfflinePlayers()).filter(x -> 
						x.getName().equalsIgnoreCase(user)).findAny().orElse(null);				
				
				if(op == null)
				{
					TextComponent text = new TextComponent(bundle.getString("commons.cmds.user-not-found"));
					text.setColor(ChatColor.RED);
					sender.spigot().sendMessage(text);
					return true;	
				}

				if(!CommonsUtil.groupSyntaxIsValid(group))
				{
					TextComponent text = new TextComponent(bundle.getString("commons.cmd.permission.invalid-group-syntax"));
					text.setColor(ChatColor.RED);
					sender.spigot().sendMessage(text);
					return true;	
				}

				File groupFile = CommonsConfig.getFile(Type.GROUP, true, group);
				YamlConfiguration groupConfig = YamlConfiguration.loadConfiguration(groupFile);

				groupConfig.set("user." + op.getUniqueId(), 0L);
				groupConfig.save(groupFile);

				File userFile = CommonsConfig.getFile(Type.USER, true, op.getUniqueId());
				YamlConfiguration userConfig = YamlConfiguration.loadConfiguration(userFile);

				userConfig.set("group." + group, 0L);
				userConfig.save(userFile);

				TextComponent[] extra = new TextComponent[2];
				
				extra[0] = new TextComponent(op.getName());
				extra[0].setColor(ChatColor.YELLOW);
				
				extra[1] = new TextComponent(group);
				extra[1].setColor(ChatColor.YELLOW);

				TextComponent text = Message.getTextComponent(bundle.getString("commons.cmd.permission.user-added-to-group"), extra);
				text.setColor(ChatColor.GREEN);
				sender.spigot().sendMessage(text);
			}
			else if(args.length == 5 && (args[0].equalsIgnoreCase("group") || args[0].equalsIgnoreCase("g")) &&
				(args[2].equalsIgnoreCase("user") || args[2].equalsIgnoreCase("u")) &&
				 args[3].equalsIgnoreCase("remove"))
			{
				final String group = args[1].toLowerCase();
				final String user = args[4].toLowerCase();

				OfflinePlayer op = Arrays.stream(Bukkit.getOfflinePlayers()).filter(x -> 
						x.getName().equalsIgnoreCase(user)).findAny().orElse(null);

				if(op == null)
				{
					TextComponent text = new TextComponent(bundle.getString("commons.cmds.user-not-found"));
					text.setColor(ChatColor.RED);
					sender.spigot().sendMessage(text);
					return true;	
				}

				File groupFile = CommonsConfig.getFile(Type.GROUP, false, group);
				YamlConfiguration groupConfig = YamlConfiguration.loadConfiguration(groupFile);
				
				if(!groupFile.exists())
				{
					TextComponent text = new TextComponent(bundle.getString("commons.cmd.permission.group-not-found"));
					text.setColor(ChatColor.RED);
					sender.spigot().sendMessage(text);
					return true;
				}

				File userFile = CommonsConfig.getFile(Type.USER, true, op.getUniqueId());
				YamlConfiguration userConfig = YamlConfiguration.loadConfiguration(userFile);
				
				if(groupConfig.contains("user." + op.getUniqueId()) || userConfig.contains("group." + group))
				{
					groupConfig.set("user." + op.getUniqueId(), null);
					groupConfig.save(groupFile);
					userConfig.set("group." + group, null);
					userConfig.save(userFile);
					
					TextComponent[] extra = new TextComponent[2];
					
					extra[0] = new TextComponent(op.getName());
					extra[0].setColor(ChatColor.YELLOW);
					
					extra[1] = new TextComponent(group);
					extra[1].setColor(ChatColor.YELLOW);

					TextComponent text = Message.getTextComponent(bundle.getString("commons.cmd.permission.user-removed-from-user"), extra);
					text.setColor(ChatColor.GREEN);
					sender.spigot().sendMessage(text);
				}
				else
				{
					TextComponent text = new TextComponent(bundle.getString("commons.cmd.permission.user-not-found-in-group"));
					text.setColor(ChatColor.RED);
					sender.spigot().sendMessage(text);
				}
			}
			else if(args.length == 4 && (args[0].equalsIgnoreCase("user") || args[0].equalsIgnoreCase("u")) &&
				args[2].equalsIgnoreCase("add"))
			{
				final String user = args[1].toLowerCase();
				final String permission = args[3].toLowerCase();
				final String configPermission = permission.replace('.', ':');

				OfflinePlayer op = Arrays.stream(Bukkit.getOfflinePlayers()).filter(x -> 
						x.getName().equalsIgnoreCase(user)).findAny().orElse(null);
				
				if(op == null)
				{
					TextComponent text = new TextComponent(bundle.getString("commons.cmds.user-not-found"));
					text.setColor(ChatColor.RED);
					sender.spigot().sendMessage(text);
					return true;	
				}

				if(!CommonsUtil.permissionSyntaxIsValid(permission))
				{
					TextComponent text = new TextComponent(bundle.getString("commons.cmd.permission.invalid-permission-syntax"));
					text.setColor(ChatColor.RED);
					sender.spigot().sendMessage(text);
					return true;
				}

				File userFile = CommonsConfig.getFile(Type.USER, true, op.getUniqueId());
				YamlConfiguration userConfig = YamlConfiguration.loadConfiguration(userFile);
				
				userConfig.set("permission." + configPermission, 0L);
				userConfig.save(userFile);

				TextComponent[] extra = new TextComponent[2];
				
				extra[0] = new TextComponent(permission);
				extra[0].setColor(ChatColor.YELLOW);
				
				extra[1] = new TextComponent(op.getName());
				extra[1].setColor(ChatColor.YELLOW);

				TextComponent text = Message.getTextComponent(bundle.getString("commons.cmd.permission.permission-added-to-user"), extra);
				text.setColor(ChatColor.GREEN);
				sender.spigot().sendMessage(text);
			}
			else if(args.length == 4 && (args[0].equalsIgnoreCase("user") || args[0].equalsIgnoreCase("u")) &&
				args[2].equalsIgnoreCase("remove"))
			{
				final String user = args[1].toLowerCase();
				final String permission = args[3].toLowerCase();
				final String configPermission = permission.replace('.', ':');

				OfflinePlayer op = Arrays.stream(Bukkit.getOfflinePlayers()).filter(x -> 
						x.getName().equalsIgnoreCase(user)).findAny().orElse(null);
				
				if(op == null)
				{
					TextComponent text = new TextComponent(bundle.getString("commons.cmds.user-not-found"));
					text.setColor(ChatColor.RED);
					sender.spigot().sendMessage(text);
					return true;	
				}

				File userFile = CommonsConfig.getFile(Type.USER, true, op.getUniqueId());
				YamlConfiguration userConfig = YamlConfiguration.loadConfiguration(userFile);
				
				int n = 0;

				if(configPermission.endsWith("*"))
				{
					ConfigurationSection section = userConfig.getConfigurationSection("permission");
	
					if(section != null)
					{
						for(String key : section.getKeys(false))
						{
							if(key.startsWith(configPermission.substring(0, configPermission.length() - 1)))
							{
								userConfig.set("permission." + key, null);
								n++;
							}
						}
					}
				}
				else if(userConfig.contains("permission." + configPermission))
				{
					userConfig.set("permission." + configPermission, null);
					n++;
				}

				userConfig.save(userFile);

				if(n > 0)
				{
					TextComponent[] extra = new TextComponent[2];
					
					extra[0] = new TextComponent(permission);
					extra[0].setColor(ChatColor.YELLOW);
					
					extra[1] = new TextComponent(op.getName());
					extra[1].setColor(ChatColor.YELLOW);

					TextComponent text = Message.getTextComponent(bundle.getString("commons.cmd.permission.permission-removed-from-user"), extra);
					text.setColor(ChatColor.GREEN);
					sender.spigot().sendMessage(text);
				}
				else
				{
					TextComponent text = new TextComponent(bundle.getString("commons.cmd.permission.permission-not-found"));
					text.setColor(ChatColor.RED);
					sender.spigot().sendMessage(text);
				}
			}
			else if((args.length == 3 || args.length == 4) &&
				(args[0].equalsIgnoreCase("group") || args[0].equalsIgnoreCase("g")) && 
				 args[2].equalsIgnoreCase("list"))
			{
//				String page = args.length == 4 ? args[3] : "1";
//				TODO
//				printList(cp, Key.GROUP, args[1].toLowerCase(), Key.PERMISSION, page);
			}
			else if((args.length == 4 || args.length == 5) && 
				(args[0].equalsIgnoreCase("group") || args[0].equalsIgnoreCase("g")) &&
				(args[2].equalsIgnoreCase("user") || args[2].equalsIgnoreCase("u")) &&
				 args[3].equalsIgnoreCase("list"))
			{
//				String page = args.length == 5 ? args[4] : "1";
//				printList(cp, Key.GROUP, args[1].toLowerCase(), Key.USER, page);
			}
			else if((args.length == 3 || args.length == 4) &&
				(args[0].equalsIgnoreCase("user") || args[0].equalsIgnoreCase("u")) && 
				 args[2].equalsIgnoreCase("list"))
			{
				String page = args.length == 4 ? args[3] : "1";
				String user = args[1].toLowerCase();

				OfflinePlayer op = Arrays.stream(Bukkit.getOfflinePlayers()).filter(x -> 
						x.getName().equalsIgnoreCase(user)).findAny().orElse(null);
				
				if(op == null)
				{
					TextComponent text = new TextComponent(bundle.getString("commons.cmds.user-not-found"));
					text.setColor(ChatColor.RED);
					sender.spigot().sendMessage(text);
					return true;	
				}

//				printList(cp, Key.USER, op, Key.PERMISSION, page);
			}
			else if((args.length == 4 || args.length == 5) && 
				(args[0].equalsIgnoreCase("user") || args[0].equalsIgnoreCase("u")) &&
				(args[2].equalsIgnoreCase("group") || args[2].equalsIgnoreCase("g")) &&
				 args[3].equalsIgnoreCase("list"))
			{
				String page = args.length == 5 ? args[4] : "1";
				String user = args[1].toLowerCase();

				OfflinePlayer op = Arrays.stream(Bukkit.getOfflinePlayers()).filter(x -> 
						x.getName().equalsIgnoreCase(user)).findAny().orElse(null);
				
				if(op == null)
				{
					TextComponent text = new TextComponent(bundle.getString("commons.cmds.user-not-found"));
					text.setColor(ChatColor.RED);
					sender.spigot().sendMessage(text);
					return true;
				}

//				printList(cp, Key.USER, op, Key.GROUP, page);
			}
			else
			{
				TextComponent[] extra = new TextComponent[1];
				
				extra[0] = new TextComponent("/" + aliases + " help");
				
				TextComponent text = Message.getTextComponent(bundle.getString("commons.cmds.wrong-syntax-try"), extra);
				text.setColor(ChatColor.RED);
				sender.spigot().sendMessage(text);
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
			TextComponent text = new TextComponent(bundle.getString("commons.unexpected-error"));
			text.setColor(ChatColor.RED);
			sender.spigot().sendMessage(text);
			return true;
		}

		return false;
	}

//	private void printList(CommonPlayer cp, Key key, OfflinePlayer op, Key listOf, String pageArgs) throws IOException
//	{
//		int page;
//
//		try
//		{
//			page = Integer.valueOf(pageArgs);
//		}
//		catch(NumberFormatException e)
//		{
//			cp.sendMessage("cmd.p.page-must-be-numeric");
//			return;
//		}
//
//		File file = CommonsConfig.getFile(key.type, false, op.getUniqueId());
//		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
//		
//		if(!file.exists())
//		{
//			cp.sendMessage("cmd.p." + key + "-not-found");
//			return;
//		}
//
//		ConfigurationSection section = config.getConfigurationSection(listOf.name);
//
//		int n = 0;
//		int maxPages = 0;
//
//		List<String> list = new ArrayList<>();
//
//		if(section != null)
//		{
//			Set<String> set = section.getKeys(false);
//			maxPages = set.size() / 10 + (set.size() % 10 == 0 ? 0 : 1);
//			
//			for(String line : set)
//			{
//				if(n < page * 10 - 10)
//				{
//					n++;
//					continue;
//				}
//
//				if(n >= page * 10)
//				{
//					break;
//				}
//
//				if(listOf == Key.PERMISSION)
//				{
//					list.add(line.replace(':', '.'));
//				}
//				else if(listOf == Key.USER)
//				{
//					File playerFile = CommonsConfig.getFile(Type.PLAYER, false, line);
//					YamlConfiguration playerConfig = YamlConfiguration.loadConfiguration(playerFile);
//
//					list.add(playerConfig.contains("name") ? playerConfig.getString("name") : line);
//				}
//				else
//				{
//					list.add(line);
//				}
//
//				n++;
//			}
//		}
//
//		if(list.isEmpty())
//		{
//			if(page == 1)
//			{
//				cp.sendMessage("cmd.p." + listOf.name + "-list-empty");
//			}
//			else
//			{
//				cp.sendMessage("cmd.p.page-not-found");
//			}
//		}
//		else
//		{
//			cp.sendMessage("cmd.p." + key + "-" + listOf + "-list", op.getName(), "[" + page + "/" + maxPages + "]");
//
//			for(String line : list)
//			{
//				cp.getPlayer().sendMessage("§c- " + line);
//			}
//		}
//	}
}