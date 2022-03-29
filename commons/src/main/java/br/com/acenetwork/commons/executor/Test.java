package br.com.acenetwork.commons.executor;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;


public class Test implements TabExecutor
{
	@Override
	public List<String> onTabComplete(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args)
	{
//		Player p = (Player) sender;
//		
//		Chunk chunk = new Location(p.getWorld(), Integer.valueOf(args[0]), 70, Integer.valueOf(args[1])).getChunk();
//		chunk.load(true);
//		chunk.setForceLoaded(true);
//		Bukkit.broadcastMessage(chunk.isLoaded() + "?" + chunk.isForceLoaded() + chunk.isEntitiesLoaded() + chunk.getX() + " " + chunk.getZ());
		
//		if(sender instanceof Player)
//		{
//			return true;
//		}
//		
//		Bukkit.getConsoleSender().sendMessage("" + p.getVelocity().toString());
//		
////		CommonsUtil.test();
//		
//		
//		GameProfile profile = new GameProfile(UUID.randomUUID(), "Testaa");
//		WorldServer worldServer = ((CraftWorld) p.getWorld()).getHandle();
//		EntityPlayer entity = new EntityPlayer(((CraftServer) Bukkit.getServer()).getHandle().getServer(), 
//				worldServer, 
//				profile);
//		for(Player all : Bukkit.getOnlinePlayers())
//		{
//			PlayerConnection connection = ((CraftPlayer) all).getHandle().b;
//			connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.c, entity));
//		}
////		
//		Location l = p.getLocation();
//		entity.setLocation(l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch());
//		entity.setNoGravity(true);
//		entity.setInvulnerable(true);
		
//		entity.movementTick();
//		entity.move();
		
//		entity.connection = new PlayerConnection(MinecraftServer.getServer(), 
//			new NetworkManager(EnumProtocolDirection.CLIENTBOUND), 
//			entity);
//		worldServer.addEntity(entity, SpawnReason.CUSTOM);
//		
//		
//		
//		entity.getBukkitEntity().getInventory().setArmorContents(p.getInventory().getArmorContents());
//		entity.getBukkitEntity().getInventory().setContents(p.getInventory().getContents());
//		
//		
//		
//		Bukkit.getPluginManager().callEvent(new PlayerModeEvent(entity.getBukkitEntity().getPlayer()));
		
		
//            connection.sendPacket(new PacketPlayOutEntityMetadata(entity.getId(), entity.getDataWatcher(), true));
//            connection.sendPacket(new PacketPlayOutNamedEntitySpawn(entity));
//		Bukkit.broadcastMessage("test");
//		
//		CommonPlayer test = CraftCommonPlayer.get(entity.getBukkitEntity().getPlayer());
		
//		test.getPlayer().setGravity(true);
//		
//		if(test == null)
//		{
//			Bukkit.broadcastMessage("test = null");
//		}
//		else
//		{			
//			Bukkit.broadcastMessage("test.hasGravity() " + test.getPlayer().hasGravity());
//		}
//		entity.getBukkitEntity().setGravity(true);
		
		
//		entity.getBukkitEntity().teleport(p);
		
//		sender.sendMessage("wwtf");
//		CommonsUtil.test();
		// long time = System.currentTimeMillis();

		// File testFile = CommonsConfig.getFile(Type.TEST, true, args[0]);
		// YamlConfiguration testConfig = YamlConfiguration.loadConfiguration(testFile);
		
		// Bukkit.broadcastMessage(testConfig.getString("uuid"));
		
		// time = System.currentTimeMillis() - time;
		// Bukkit.broadcastMessage(time + "ms");




		// new Thread(() ->
		// {
		// 	for(int i = 0; i < 290000; i++)
		// 	{
		// 		Bukkit.broadcastMessage(i + "");
	
		// 		try
		// 		{
		// 			File testFile = CommonsConfig.getFile(Type.TEST, true, UUID.randomUUID());
		// 			YamlConfiguration testConfig = YamlConfiguration.loadConfiguration(testFile);
		// 			testFile.createNewFile();
					
		// 			testConfig.set("uuid", UUID.randomUUID().toString());
		// 			testConfig.save(testFile);
		// 		}
		// 		catch(IOException ex)
		// 		{
		// 			ex.printStackTrace();
		// 			break;
		// 		}
		// 	}
		// }).start();



		// long time = System.currentTimeMillis();

		// File testFile = CommonsConfig.getFile(Type.TEST, true, "testaa");
		// YamlConfiguration testConfig = YamlConfiguration.loadConfiguration(testFile);
		
		// Bukkit.broadcastMessage(testConfig.getString(args[0]));
		
		// time = System.currentTimeMillis() - time;
		// Bukkit.broadcastMessage(time + "ms");



		// File testFile = CommonsConfig.getFile(Type.TEST, true, "testaa");
		// YamlConfiguration testConfig = YamlConfiguration.loadConfiguration(testFile);

		// new Thread(() ->
		// {
		// 	for(int i = 0; i < 980000; i++)
		// 	{
		// 		Bukkit.broadcastMessage(i + "");
		// 		testConfig.set(UUID.randomUUID().toString(), UUID.randomUUID().toString());
		// 	}

		// 	try
		// 	{
		// 		testConfig.save(testFile);
		// 	}
		// 	catch(IOException e) 
		// 	{
		// 		// TODO Auto-generated catch block
		// 		e.printStackTrace();
		// 	}
		// }).start();

		return false;
	}

	
}
