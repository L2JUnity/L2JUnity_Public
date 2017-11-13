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
package org.l2junity.gameserver.data.sql.migrations;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.l2junity.commons.sql.DatabaseFactory;
import org.l2junity.commons.sql.migrations.IDatabaseMigration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author UnAfraid
 */
public class SecondaryAuthMigration implements IDatabaseMigration
{
	private static final Logger LOGGER = LoggerFactory.getLogger(SecondaryAuthMigration.class);
	
	@Override
	public String getName()
	{
		return "2017-02-25_SecondaryAuthMigration";
	}
	
	@Override
	public boolean onUp() throws SQLException
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection())
		{
			final Map<String, Map<String, String>> vars = new HashMap<>();
			
			// Fetch all data
			try (PreparedStatement st = con.prepareStatement("SELECT * FROM account_gsdata WHERE var IN (?, ?)"))
			{
				st.setString(1, "secauth_pwd");
				st.setString(2, "secauth_wte");
				try (ResultSet rs = st.executeQuery())
				{
					while (rs.next())
					{
						final String accountName = rs.getString("account_name");
						final String key = rs.getString("var");
						final String value = rs.getString("value");
						vars.computeIfAbsent(accountName, k -> new HashMap<>()).put(key, value);
					}
				}
			}
			
			// Nothing to do if no variables
			if (vars.isEmpty())
			{
				return true;
			}
			
			// Update data
			try (PreparedStatement ps = con.prepareStatement("UPDATE account_gsdata SET value = ? WHERE account_name = ? AND var = ?"))
			{
				for (Entry<String, Map<String, String>> entry : vars.entrySet())
				{
					final String accountName = entry.getKey();
					for (Entry<String, String> valuesEntry : entry.getValue().entrySet())
					{
						processData(accountName, valuesEntry.getKey(), valuesEntry.getValue(), ps);
					}
				}
				ps.executeBatch();
			}
		}
		return true;
	}
	
	private void processData(String accountName, String key, String value, PreparedStatement ps) throws SQLException
	{
		switch (key)
		{
			case "secauth_pwd":
			{
				serializeObject(accountName, key, value, ps);
				break;
			}
			case "secauth_wte":
			{
				serializeObject(accountName, key, Integer.parseInt(value), ps);
				break;
			}
		}
		
		ps.setString(2, accountName);
		ps.setString(3, key);
		ps.addBatch();
	}
	
	private void serializeObject(String accountName, String key, Object value, PreparedStatement ps) throws SQLException
	{
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos))
		{
			oos.writeObject(value);
			final byte[] valueAsByteArray = baos.toByteArray();
			try (ByteArrayInputStream stream = new ByteArrayInputStream(valueAsByteArray))
			{
				ps.setBinaryStream(1, stream, valueAsByteArray.length);
			}
		}
		catch (IOException e)
		{
			LOGGER.warn("Failed to migrate AccountVariables entry {} for player: {}", key, accountName, e);
		}
	}
	
	@Override
	public boolean onDown()
	{
		return false;
	}
	
	@Override
	public boolean isReversable()
	{
		return false;
	}
}
