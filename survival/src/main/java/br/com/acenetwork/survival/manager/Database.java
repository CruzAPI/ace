package br.com.acenetwork.survival.manager;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import br.com.acenetwork.survival.manager.Config.Type;

import org.bukkit.configuration.file.YamlConfiguration;

public class Database implements AutoCloseable
{
	public final Connection connection;

	public Database() throws SQLException
	{
		File file = Config.getFile(Type.DATABASE, false);
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		
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