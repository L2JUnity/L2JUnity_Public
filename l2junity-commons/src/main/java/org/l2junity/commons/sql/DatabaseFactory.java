/*
 * Copyright (C) 2004-2015 L2J Unity
 * 
 * This file is part of L2J Unity.
 * 
 * L2J Unity is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J Unity is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2junity.commons.sql;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import org.l2junity.commons.util.concurrent.ThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * This class manages the database connections.
 */
public final class DatabaseFactory
{
	private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseFactory.class);
	
	private static DatabaseFactory _instance;
	private ComboPooledDataSource _source;
	
	private IDatabaseConfig _databaseConfig = null;
	
	protected DatabaseFactory()
	{
		// visibility
	}
	
	public void setDatabaseConfig(IDatabaseConfig config)
	{
		_databaseConfig = config;
	}
	
	public void init() throws SQLException
	{
		if (_databaseConfig == null)
		{
			throw new NullPointerException("Database Config is not specified!");
		}
		
		try
		{
			// Sorry, no noob support. If you really want that crap, use ConfigLoader#loadImpl(PropertiesParser, PropertiesParser)
			// if (_databaseConfig.getMaxConnections() < 2) { _databaseConfig.getMaxConnections() = 2; LOGGER.warn("A minimum of {} db connections are required.", _databaseConfig.getMaxConnections()); }
			
			_source = new ComboPooledDataSource();
			_source.setAutoCommitOnClose(true);
			
			_source.setInitialPoolSize(10);
			_source.setMinPoolSize(10);
			_source.setMaxPoolSize(Math.max(10, _databaseConfig.getMaxConnections()));
			
			_source.setAcquireRetryAttempts(0); // try to obtain connections indefinitely (0 = never quit)
			_source.setAcquireRetryDelay(500); // 500 milliseconds wait before try to acquire connection again
			_source.setCheckoutTimeout(0); // 0 = wait indefinitely for new connection
			// if pool is exhausted
			_source.setAcquireIncrement(5); // if pool is exhausted, get 5 more connections at a time
			// cause there is a "long" delay on acquire connection
			// so taking more than one connection at once will make connection pooling
			// more effective.
			
			_source.setIdleConnectionTestPeriod(3600); // test idle connection every 60 sec
			_source.setMaxIdleTime(_databaseConfig.getMaxIdleTime()); // 0 = idle connections never expire
			// *THANKS* to connection testing configured above
			// but I prefer to disconnect all connections not used
			// for more than 1 hour
			
			// enables statement caching, there is a "semi-bug" in c3p0 0.9.0 but in 0.9.0.2 and later it's fixed
			_source.setMaxStatementsPerConnection(100);
			
			_source.setBreakAfterAcquireFailure(false); // never fail if any way possible
			// setting this to true will make
			// c3p0 "crash" and refuse to work
			// till restart thus making acquire
			// errors "FATAL" ... we don't want that
			// it should be possible to recover
			_source.setJdbcUrl(_databaseConfig.getJdbcURL());
			_source.setUser(_databaseConfig.getUsername());
			_source.setPassword(_databaseConfig.getPassword());
			
			/* Test the connection */
			_source.getConnection().close();
		}
		catch (SQLException x)
		{
			// re-throw the exception
			throw x;
		}
		catch (Exception e)
		{
			throw new SQLException("Could not init DB connection:" + e.getMessage());
		}
		
		LOGGER.info("Initialized DB '{}' as user '{}'.", _databaseConfig.getJdbcURL(), _databaseConfig.getUsername());
	}
	
	private void closeSource()
	{
		if (_source != null)
		{
			_source.close();
		}
		
		// Is this really necessary?
		_source = null;
	}
	
	/**
	 * Shutdown.
	 */
	public static void shutdown()
	{
		if (_instance == null)
		{
			return;
		}
		
		LOGGER.info("Shutting down.");
		
		try
		{
			DatabaseFactory.getInstance().closeSource();
		}
		catch (Exception e)
		{
			LOGGER.warn("", e);
		}
	}
	
	/**
	 * Gets the single instance of L2DatabaseFactory.
	 * @return single instance of L2DatabaseFactory
	 */
	public static DatabaseFactory getInstance()
	{
		synchronized (DatabaseFactory.class)
		{
			if (_instance == null)
			{
				_instance = new DatabaseFactory();
			}
		}
		return _instance;
	}
	
	/**
	 * Gets the connection.
	 * @return the connection
	 */
	public Connection getConnection()
	{
		Connection con = null;
		while (con == null)
		{
			try
			{
				con = _source.getConnection();
				
				ThreadPool.schedule(new ConnectionCloser(con, new RuntimeException()), _databaseConfig.getConnectionCloseTime(), TimeUnit.MILLISECONDS);
			}
			catch (SQLException e)
			{
				LOGGER.warn("getConnection() failed, trying again", e);
			}
		}
		return con;
	}
	
	/**
	 * The Class ConnectionCloser.
	 */
	private static final class ConnectionCloser implements Runnable
	{
		private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionCloser.class);
		
		/** The connection. */
		private final Connection c;
		
		/** The exception. */
		private final RuntimeException exp;
		
		/**
		 * Instantiates a new connection closer.
		 * @param con the con
		 * @param e the e
		 */
		public ConnectionCloser(Connection con, RuntimeException e)
		{
			c = con;
			exp = e;
		}
		
		@Override
		public void run()
		{
			try
			{
				if (!c.isClosed())
				{
					LOGGER.warn("Unclosed connection! Trace: {}", exp.getStackTrace()[1], exp);
				}
			}
			catch (SQLException e)
			{
				LOGGER.warn("", e);
			}
		}
	}
	
	/**
	 * Gets the busy connection count.
	 * @return the busy connection count
	 * @throws SQLException the SQL exception
	 */
	public int getBusyConnectionCount() throws SQLException
	{
		return _source.getNumBusyConnectionsDefaultUser();
	}
	
	/**
	 * Gets the idle connection count.
	 * @return the idle connection count
	 * @throws SQLException the SQL exception
	 */
	public int getIdleConnectionCount() throws SQLException
	{
		return _source.getNumIdleConnectionsDefaultUser();
	}
}
