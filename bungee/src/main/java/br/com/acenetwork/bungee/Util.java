package br.com.acenetwork.bungee;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import br.com.acenetwork.bungee.manager.Config;
import br.com.acenetwork.bungee.manager.Config.Type;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class Util
{
	public static void sendDataPlayerCount(int count)
	{
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		
		out.writeUTF("playercount");
		out.writeInt(count);
		
		for(ServerInfo info : ProxyServer.getInstance().getServers().values())
		{
			info.sendData("commons:commons", out.toByteArray());
		}
	}
	
	public static boolean hasPermission(String uuid, String perm)
	{
		if(perm == null)
		{
			return true;
		}
		
		try
		{
			ConfigurationProvider provider = ConfigurationProvider.getProvider(YamlConfiguration.class);
			
			perm = perm.replace('.', ':');

			File userFile = Config.getFile(Type.USER, true, uuid);
			Configuration userConfig = provider.load(userFile);

			Configuration userPermissions = userConfig.getSection("permission");

			if(userPermissions != null)
			{
				for(String key : userPermissions.getKeys())
				{
					long value = userConfig.getLong("permission." + key);
					boolean valid = value == 0 || value > System.currentTimeMillis();

					if(valid && (key.endsWith("*") && perm.startsWith(key.substring(0, key.length() - 1)) || 
						perm.equals(key)))
					{
						return true;
					}
				}
			}
			
			Configuration userGroups = userConfig.getSection("group");
			
			if(userGroups != null)
			{
				for(String key : userGroups.getKeys())
				{
					long value = userConfig.getLong("group." + key);
					boolean valid = value == 0 || value > System.currentTimeMillis();

					if(valid)
					{
						File groupFile = Config.getFile(Type.GROUP, true, key);
						Configuration groupConfig = provider.load(groupFile);

						Configuration groupPermissions = groupConfig.getSection("permission");

						if(groupPermissions != null)
						{
							for(String key1 : groupPermissions.getKeys())
							{
								value = groupConfig.getLong("permisison." + key1);
								valid = value == 0 || value > System.currentTimeMillis();
								
								if(valid && (key1.endsWith("*") && perm.startsWith(key1.substring(0, key1.length() - 1)) || 
									perm.equals(key1)))
								{
									return true;
								}
							}
						}
					}
				}
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		
		return false;
	}

	public static void zipFolder(Path source) throws IOException
	{
        String zipFileName = source.getFileName().toString() + ".zip";

        try(ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFileName)))
        {
            Files.walkFileTree(source, new SimpleFileVisitor<>()
            {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attributes)
                {
                    if(attributes.isSymbolicLink())
                    {
                        return FileVisitResult.CONTINUE;
                    }

                    try(FileInputStream fis = new FileInputStream(file.toFile()))
                    {
                        Path targetFile = source.relativize(file);
                        zos.putNextEntry(new ZipEntry(targetFile.toString()));

                        byte[] buffer = new byte[1024];
                        int len;
                        
                        while((len = fis.read(buffer)) > 0)
                        {
                            zos.write(buffer, 0, len);
                        }

                        // if large file, throws out of memory
                        //byte[] bytes = Files.readAllBytes(file);
                        //zos.write(bytes, 0, bytes.length);

                        zos.closeEntry();

                        System.out.printf("Zip file : %s%n", file);

                    }
                    catch(IOException e)
                    {
                        e.printStackTrace();
                    }
                    
                    return FileVisitResult.CONTINUE;
                }
                
                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc)
                {
                    System.err.printf("Unable to zip : %s%n%s%n", file, exc);
                    return FileVisitResult.CONTINUE;
                }
            });
        }
    }
}
