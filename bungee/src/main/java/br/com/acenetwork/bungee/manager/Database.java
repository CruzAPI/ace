package br.com.acenetwork.bungee.manager;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import br.com.acenetwork.bungee.manager.Config.Type;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class Database implements AutoCloseable
{
	public final Connection connection;

	public Database() throws SQLException
	{
		ConfigurationProvider provider = ConfigurationProvider.getProvider(YamlConfiguration.class);
		
		File file = Config.getFile(Type.DATABASE, false);
		
		Configuration config;
		
		try
		{
			config = provider.load(file);
		}
		catch(IOException e)
		{
			connection = null;
			e.printStackTrace();
			return;
		}
		
		String host = config.getString("mysql.host");
		String database = config.getString("mysql.database");
		String user = config.getString("mysql.user");
		String password = config.getString("mysql.password");
		int port = config.getInt("mysql.port");
		
		connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + 
			"?useAffectedRows=true", user, password);
	}

	@Override
	public void close() throws SQLException
	{
		connection.close();
	}
}