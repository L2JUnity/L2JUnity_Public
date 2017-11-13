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
package org.l2junity.gameserver.model.variables;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map.Entry;

import org.l2junity.commons.sql.DatabaseFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author UnAfraid
 */
public class AccountVariables extends AbstractVariables
{
	static final Logger LOGGER = LoggerFactory.getLogger(AccountVariables.class);
	
	// SQL Queries.
	private static final String SELECT_QUERY = "SELECT * FROM account_gsdata WHERE account_name = ?";
	private static final String DELETE_QUERY = "DELETE FROM account_gsdata WHERE account_name = ?";
	private static final String INSERT_QUERY = "INSERT INTO account_gsdata (account_name, var, value) VALUES (?, ?, ?)";
	
	public static final String PC_CAFE_POINTS = "PC_CAFE_POINTS";
	public static final String PC_CAFE_POINTS_TODAY = "PC_CAFE_POINTS_TODAY";
	public static final String PREMIUM_ACCOUNT = "PREMIUM_ACCOUNT";
	public static final String TRAINING_CAMP = "TRAINING_CAMP";
	public static final String TRAINING_CAMP_DURATION = "TRAINING_CAMP_DURATION";
	public static final String UI_SETTINGS = "UI_SETTINGS";
	
	private final String _accountName;
	
	public AccountVariables(String accountName)
	{
		_accountName = accountName;
		restoreMe();
	}
	
	@Override
	public boolean restoreMe()
	{
		// Restore previous variables.
		try (Connection con = DatabaseFactory.getInstance().getConnection();
			PreparedStatement st = con.prepareStatement(SELECT_QUERY))
		{
			st.setString(1, _accountName);
			try (ResultSet rset = st.executeQuery())
			{
				while (rset.next())
				{
					Object deSerializedObject;
					try (final ValidObjectInputStream objectIn = new ValidObjectInputStream(new ByteArrayInputStream(rset.getBytes("value"))))
					{
						deSerializedObject = objectIn.readObject();
					}
					catch (IOException | ClassNotFoundException e)
					{
						LOGGER.warn("Couldn't restore variable {} for: {}", rset.getString("var"), _accountName, e);
						deSerializedObject = null;
					}
					set(rset.getString("var"), deSerializedObject);
				}
			}
		}
		catch (SQLException e)
		{
			LOGGER.warn("Couldn't restore variables for: {}", _accountName, e);
			return false;
		}
		finally
		{
			compareAndSetChanges(true, false);
		}
		return true;
	}
	
	@Override
	public boolean storeMe()
	{
		// No changes, nothing to store.
		if (!hasChanges())
		{
			return false;
		}
		
		try (Connection con = DatabaseFactory.getInstance().getConnection())
		{
			// Clear previous entries.
			try (PreparedStatement st = con.prepareStatement(DELETE_QUERY))
			{
				st.setString(1, _accountName);
				st.execute();
			}
			
			// Insert all variables.
			try (PreparedStatement st = con.prepareStatement(INSERT_QUERY))
			{
				st.setString(1, _accountName);
				for (Entry<String, Object> entry : getSet().entrySet())
				{
					try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
						ObjectOutputStream oos = new ObjectOutputStream(baos);)
					{
						
						oos.writeObject(entry.getValue());
						byte[] asBytes = baos.toByteArray();
						try (ByteArrayInputStream bais = new ByteArrayInputStream(asBytes))
						{
							st.setString(2, entry.getKey());
							st.setBinaryStream(3, bais, asBytes.length);
							st.addBatch();
						}
					}
					catch (IOException e)
					{
						LOGGER.warn("Couldn't store variable {} for account {}", entry.getKey(), _accountName);
					}
				}
				st.executeBatch();
			}
		}
		catch (SQLException e)
		{
			LOGGER.warn("Couldn't update variables for: {}", _accountName, e);
			return false;
		}
		finally
		{
			compareAndSetChanges(true, false);
		}
		return true;
	}
	
	@Override
	public boolean deleteMe()
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection())
		{
			// Clear previous entries.
			try (PreparedStatement st = con.prepareStatement(DELETE_QUERY))
			{
				st.setString(1, _accountName);
				st.execute();
			}
			
			// Clear all entries
			getSet().clear();
		}
		catch (Exception e)
		{
			LOGGER.warn("Couldn't delete variables for: {}", _accountName, e);
			return false;
		}
		return true;
	}
	
	public boolean hasUiSettings()
	{
		final byte[] data = getObject(UI_SETTINGS, byte[].class);
		return (data != null) && (data.length > 0);
	}
}
