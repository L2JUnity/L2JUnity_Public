/*
 * Copyright (C) 2004-2017 L2J Unity
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
package org.l2junity.commons.sql.migrations;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;

import org.l2junity.commons.sql.DatabaseFactory;
import org.l2junity.commons.util.ClassPathUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author UnAfraid
 */
public final class DatabaseMigrationManager
{
	private final Logger LOGGER = LoggerFactory.getLogger(DatabaseMigrationManager.class);
	private final Map<String, IDatabaseMigration> _migrations = new HashMap<>();
	
	protected DatabaseMigrationManager()
	{
		init();
	}
	
	private void init()
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection();
			Statement ps = con.createStatement();
			ResultSet rs = ps.executeQuery("SHOW TABLES LIKE \"migrations\""))
		{
			if (!rs.next())
			{
				final StringJoiner sj = new StringJoiner(System.lineSeparator());
				sj.add("CREATE TABLE IF NOT EXISTS `migrations` (");
				sj.add("`id` int(10) unsigned NOT NULL AUTO_INCREMENT,");
				sj.add("`name` varchar(255) NOT NULL,");
				sj.add("`migrated` tinyint(1) unsigned NOT NULL,");
				sj.add("PRIMARY KEY (`id`,`name`)");
				sj.add(") ENGINE=InnoDB DEFAULT CHARSET=utf8");
				ps.execute(sj.toString());
				LOGGER.info("Migrations table created!");
			}
		}
		catch (Exception e)
		{
			LOGGER.warn("Failed to create migrations table", e);
		}
	}
	
	public void processPackage(Package packageName)
	{
		try
		{
			ClassPathUtil.getAllClassesExtending(packageName.getName(), IDatabaseMigration.class).forEach(migrationClass ->
			{
				try
				{
					final IDatabaseMigration migration = migrationClass.newInstance();
					_migrations.put(migration.getName(), migration);
				}
				catch (Exception e)
				{
					LOGGER.warn("Failed to initialize migration: {}", migrationClass.getName(), e);
				}
			});
		}
		catch (IOException e)
		{
			LOGGER.warn("Failed to scan for configs", e);
		}
		
		final Set<String> migrated = new HashSet<>();
		try (Connection con = DatabaseFactory.getInstance().getConnection();
			Statement ps = con.createStatement();
			ResultSet rs = ps.executeQuery("SELECT * FROM migrations"))
		{
			while (rs.next())
			{
				if (rs.getInt("migrated") > 0)
				{
					migrated.add(rs.getString("name"));
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.warn("Failed to retreive database migrations: ", e);
		}
		
		//@formatter:off
		final IDatabaseMigration[] notMigrated = _migrations.values().stream()
			.filter(migration -> !migrated.contains(migration.getName()))
			.sorted(Comparator.comparing(IDatabaseMigration::getName))
			.toArray(IDatabaseMigration[]::new);
		//@formatter:on
		
		for (IDatabaseMigration migration : notMigrated)
		{
			if (migrate(migration))
			{
				LOGGER.info("Successfully migrated: {}", migration.getName());
			}
			else
			{
				LOGGER.warn("Failed to migrate migrated: {}", migration.getName());
			}
		}
		LOGGER.info("Processed: {} of {} migrations", notMigrated.length, _migrations.size());
	}
	
	public Collection<IDatabaseMigration> getMigrated()
	{
		return Collections.unmodifiableCollection(_migrations.values());
	}
	
	public boolean migrate(IDatabaseMigration migration)
	{
		try
		{
			_migrations.putIfAbsent(migration.getName(), migration);
			
			if (!migration.onUp())
			{
				return false;
			}
			
			try (Connection con = DatabaseFactory.getInstance().getConnection();
				PreparedStatement ps = con.prepareStatement("INSERT INTO migrations (name, migrated) VALUES (?, ?)"))
			{
				ps.setString(1, migration.getName());
				ps.setInt(2, 1);
				ps.execute();
			}
			catch (Exception e)
			{
				LOGGER.warn("Failed to retreive database migrations: ", e);
			}
			return true;
		}
		catch (SQLException e)
		{
			LOGGER.warn("Failed to migrate: {}", migration.getClass().getName(), e);
		}
		return false;
	}
	
	public boolean rollback(IDatabaseMigration migration)
	{
		if (!migration.isReversable())
		{
			return false;
		}
		
		try (Connection con = DatabaseFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT * FROM migrations WHERE name = ?"))
		{
			ps.setString(1, migration.getName());
			try (ResultSet rs = ps.executeQuery())
			{
				if (rs.next())
				{
					if (rs.getInt("migrated") > 0)
					{
						if (migration.onDown())
						{
							try (PreparedStatement delPs = con.prepareStatement("DELETE FROM migrations WHERE id = ?"))
							{
								delPs.setInt(1, rs.getInt("id"));
								delPs.execute();
							}
							return true;
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.warn("Failed to retreive database migrations: ", e);
		}
		return false;
	}
	
	/**
	 * Gets the single instance of {@code DatabaseMigrationManager}.
	 * @return single instance of {@code DatabaseMigrationManager}
	 */
	public static DatabaseMigrationManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final DatabaseMigrationManager INSTANCE = new DatabaseMigrationManager();
	}
}
