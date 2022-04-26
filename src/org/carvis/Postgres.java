package org.carvis;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.Logger;

public class Postgres {
	
	private static final Logger logger = Logger.getLogger(Postgres.class.getName());
	private static final Statement statement = getStatement(getConnection());

	public Postgres() {
		super();
		checkDatabase();
		checkTable();
	}
	
	public boolean insertPackage(Package dataPackage) {
		String sql = "INSERT INTO carvis (id,name,value)VALUES(" + dataPackage.getId() + ",'"
				+ dataPackage.getName() + "'," + dataPackage.getValue() + ");";
		try {
			logger.finest("Inserting record to database table.");
			statement.executeUpdate(sql);
			logger.finest("The record is inserted.");
			return true;
		} catch (SQLException e) {
			logger.warning("Could not insert record.");
			e.printStackTrace();
		}
		return false;
	}

	private static Statement getStatement(Connection connection) {
		Statement result = null;
		try {
			logger.finest("Checking if connection is closed.");
			if(connection.isClosed()) {
				logger.severe("Got a closed connection. Exiting.");
				System.exit(0);
			}else {
				logger.finest("Connection is not closed.");
			}
		} catch (SQLException e) {
			logger.severe("A database access error occured. Exiting.");
			e.printStackTrace();
			System.exit(0);
		}
		try {
			logger.finest("Creating a statement.");
			result = connection.createStatement();
			logger.finest("Statement is created.");
		} catch (SQLException e) {
			logger.severe("Could not get statement. "
					+ "A database access error occured. Exiting.");
			e.printStackTrace();
			System.exit(0);
		}
		logger.finest("Checking statement to be not null.");
		if(result == null) {
			logger.severe("Failed getting statement. Exiting.");
			System.exit(0);
		}else {
			logger.finest("Statement is not null.");			
		}
		return result;
	}
	
	private static Connection getConnection() {
		Connection connection = null;
		
		Properties properties = readProperties();	
		final String connectionURL = properties.getProperty("url");
		try {
			logger.finest("Connecting to postgres with "+connectionURL+" and "+properties);
			connection = DriverManager.getConnection(connectionURL, properties);
			logger.finest("Connection established.");
		} catch (SQLException e) {
			logger.severe("Could not connect to postgres  with "
					+ properties.toString()
					+ "Example:\n url =jdbc:postgresql://localhost:5432/\n"
					+ "user=postgres\n"
					+ "password=\n"+
					". Exiting.");
			e.printStackTrace();
			System.exit(0);			
		}
		logger.finest("Checking driver compatibility.");
		if(connection ==null) {
			logger.severe("Failed getting connection. It is the wrong kind of driver "
					+ " to connect to the given URL ["+connectionURL+ "]. Exiting.");
			System.exit(0);			
		}else {
			logger.finest("Driver is compatible.");
		} 
		return connection;
	}
	
	private static Properties readProperties() {
		Properties properties = new Properties();
		try(InputStream in = Postgres.class.getResourceAsStream("persistence.properties")){
			properties.load(in);
		} catch (IOException e) {
			logger.severe("Failed reading properties.");
			
			e.printStackTrace();
		}
		return properties;
	}
	
	private void checkDatabase() {
		String sql = "SELECT * FROM pg_database WHERE datname = 'carvis'";
		try {
			logger.finest("Checking if database is alredy there.");
			if (!statement.executeQuery(sql).isBeforeFirst()) {
				logger.finest("Database is not there. Creating.");
				statement.executeUpdate("CREATE DATABASE carvis;");
				logger.finest("Database is created.");
			}else {
				logger.finest("Database is  there.");
			}
		} catch (SQLException e) {
			logger.warning("Could not learn about or create database.");
			e.printStackTrace();
		}
	}
	
	private void checkTable() {
		final int limit = Package.getNameLengthLimit();
		String sql = "CREATE TABLE IF NOT EXISTS carvis( id INTEGER, name"
				+ "	VARCHAR ("+ limit +"), value double precision);";
		try {
			logger.finest("Checking if table exists. If it does not exists then creating table.");
			statement.executeUpdate(sql);
			logger.finest("Table is  there.");
		} catch (SQLException e) {
			logger.warning("Could not create table if it not exists.");
			e.printStackTrace();
		}
	}

}
