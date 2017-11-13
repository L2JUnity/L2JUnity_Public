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
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.l2junity.commons.sql.DatabaseFactory;
import org.l2junity.commons.sql.migrations.IDatabaseMigration;
import org.l2junity.gameserver.model.Location;
import org.l2junity.gameserver.model.holders.ItemHolder;
import org.l2junity.gameserver.model.variables.PlayerVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author UnAfraid
 */
public class PlayerVariablesMigration implements IDatabaseMigration
{
	private static final Logger LOGGER = LoggerFactory.getLogger(PlayerVariablesMigration.class);
	
	@Override
	public String getName()
	{
		return "2017-02-08_PlayerVariablesMigration";
	}
	
	@Override
	public boolean onUp() throws SQLException
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection())
		{
			final Map<Integer, Map<String, String>> vars = new HashMap<>();
			
			// Fetch all data
			try (Statement st = con.createStatement();
				ResultSet rs = st.executeQuery("SELECT * FROM character_variables"))
			{
				while (rs.next())
				{
					final int objectId = rs.getInt("charId");
					final String key = rs.getString("var");
					final String value = rs.getString("val");
					vars.computeIfAbsent(objectId, k -> new HashMap<>()).put(key, value);
				}
			}
			
			// Change structure
			try (Statement st = con.createStatement())
			{
				st.execute("ALTER TABLE `character_variables` CHANGE COLUMN `val` `value`  blob NOT NULL AFTER `var`, ADD PRIMARY KEY (`charId`, `var`)");
			}
			
			// Update data
			try (PreparedStatement ps = con.prepareStatement("UPDATE character_variables SET value = ? WHERE charId = ? AND var = ?"))
			{
				for (Entry<Integer, Map<String, String>> entry : vars.entrySet())
				{
					final int objectId = entry.getKey();
					for (Entry<String, String> valuesEntry : entry.getValue().entrySet())
					{
						processData(objectId, valuesEntry.getKey(), valuesEntry.getValue(), ps);
					}
				}
				ps.executeBatch();
			}
		}
		
		return true;
	}
	
	private void processData(int objectId, String key, String value, PreparedStatement ps) throws SQLException
	{
		switch (key)
		{
			// Integer
			case PlayerVariables.VITALITY_ITEMS_USED_VARIABLE_NAME:
			case PlayerVariables.WORLD_CHAT_VARIABLE_NAME:
			case PlayerVariables.ABILITY_POINTS_MAIN_CLASS:
			case PlayerVariables.ABILITY_POINTS_DUAL_CLASS:
			case PlayerVariables.ABILITY_POINTS_USED_MAIN_CLASS:
			case PlayerVariables.ABILITY_POINTS_USED_DUAL_CLASS:
			case PlayerVariables.REVELATION_SKILL_1_MAIN_CLASS:
			case PlayerVariables.REVELATION_SKILL_2_MAIN_CLASS:
			case PlayerVariables.REVELATION_SKILL_1_DUAL_CLASS:
			case PlayerVariables.REVELATION_SKILL_2_DUAL_CLASS:
			case PlayerVariables.USED_PC_LOTTERY_TICKET:
			case PlayerVariables.CEREMONY_OF_CHAOS_PROHIBITED_PENALTIES:
			case PlayerVariables.CLAN_REWARD_CLAIMED:
			case PlayerVariables.INSTANCE_RESTORE:
			case PlayerVariables.FORTUNE_TELLING_VARIABLE:
			case PlayerVariables.DELUSION_RETURN:
			case PlayerVariables.VISUAL_HAIR_ID:
			case PlayerVariables.VISUAL_HAIR_COLOR_ID:
			case PlayerVariables.VISUAL_FACE_ID:
			case PlayerVariables.FANTASY_RETURN:
			case PlayerVariables.MONSTER_RETURN:
			{
				serializeObject(objectId, key, Integer.parseInt(value), ps);
				break;
			}
			
			// Boolean
			case PlayerVariables.HAIR_ACCESSORY_VARIABLE_NAME:
			case PlayerVariables.FORTUNE_TELLING_BLACK_CAT_VARIABLE:
			case PlayerVariables.TI_YESEGIRA_MOVIE:
			case PlayerVariables.TI_PRESENTATION_MOVIE:
			case PlayerVariables.ANCIENT_ARCAN_CITY_SCENE:
			{
				serializeObject(objectId, key, Boolean.parseBoolean(value), ps);
				break;
			}
			
			// String -> List
			case PlayerVariables.EXTEND_DROP:
			{
				final String[] drops = value.split(";");
				final List<ItemHolder> items = new ArrayList<>(drops.length);
				for (String drop : drops)
				{
					final String[] item = drop.split(",");
					items.add(new ItemHolder(Integer.parseInt(item[0]), Long.parseLong(item[1])));
				}
				serializeObject(objectId, key, drops, ps);
				break;
			}
			case PlayerVariables.ONE_DAY_REWARDS:
			{
				serializeObject(objectId, key, new ArrayList<>(Arrays.asList(value.split(","))), ps);
				break;
			}
			case PlayerVariables.INSTANCE_ORIGIN_LOCATION:
			{
				final String[] loc = value.split(";");
				if (loc.length == 3)
				{
					serializeObject(objectId, key, new Location(Integer.parseInt(loc[0]), Integer.parseInt(loc[1]), Integer.parseInt(loc[2])), ps);
				}
				break;
			}
			default:
			{
				if (key.startsWith("SubSkill-") || key.startsWith("DualSkill-"))
				{
					serializeObject(objectId, key, value, ps);
					break;
				}
				else if (key.startsWith("Mentor-Penalty-"))
				{
					serializeObject(objectId, key, Long.parseLong(value), ps);
					break;
				}
				else if (key.startsWith("SUBCLASS_INDEX_"))
				{
					serializeObject(objectId, key, Integer.parseInt(value), ps);
					break;
				}
				else if (key.startsWith("HOLY_POMANDER_"))
				{
					serializeObject(objectId, key, Boolean.parseBoolean(value), ps);
					break;
				}
				
				// Try integer
				try
				{
					int val = Integer.parseInt(value);
					serializeObject(objectId, key, val, ps);
				}
				catch (Exception e)
				{
					// Try long
					try
					{
						long val = Long.parseLong(value);
						serializeObject(objectId, key, val, ps);
					}
					catch (Exception e2)
					{
						// Try double
						try
						{
							double val = Double.parseDouble(value);
							serializeObject(objectId, key, val, ps);
						}
						catch (Exception e3)
						{
							// Go as String
							serializeObject(objectId, key, value, ps);
						}
					}
				}
				break;
			}
		}
		
		ps.setInt(2, objectId);
		ps.setString(3, key);
		ps.addBatch();
	}
	
	/**
	 * @param objectId
	 * @param key
	 * @param value
	 * @param ps
	 * @throws SQLException
	 */
	private void serializeObject(int objectId, String key, Object value, PreparedStatement ps) throws SQLException
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
			LOGGER.warn("Failed to migrate PlayerVariables entry {} for player: {}", key, objectId, e);
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
