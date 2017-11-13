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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.l2junity.commons.sql.DatabaseFactory;
import org.l2junity.gameserver.model.World;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.holders.ItemHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author UnAfraid
 */
public class PlayerVariables extends AbstractVariables
{
	private static final Logger LOGGER = LoggerFactory.getLogger(PlayerVariables.class);
	
	// SQL Queries.
	private static final String SELECT_QUERY = "SELECT * FROM character_variables WHERE charId = ?";
	private static final String DELETE_QUERY = "DELETE FROM character_variables WHERE charId = ?";
	private static final String INSERT_QUERY = "INSERT INTO character_variables (charId, var, value) VALUES (?, ?, ?)";
	
	// Public variable names
	public static final String HAIR_ACCESSORY_VARIABLE_NAME = "HAIR_ACCESSORY_ENABLED";
	public static final String WORLD_CHAT_VARIABLE_NAME = "WORLD_CHAT_USED";
	public static final String VITALITY_ITEMS_USED_VARIABLE_NAME = "VITALITY_ITEMS_USED";
	public static final String ONE_DAY_REWARDS = "ONE_DAY_REWARDS";
	public static final String CEREMONY_OF_CHAOS_PROHIBITED_PENALTIES = "CEREMONY_OF_CHAOS_PENALTIES";
	public static final String ABILITY_POINTS_MAIN_CLASS_LEVEL_HANDLED = "ABILITY_POINTS_MAIN_CLASS_LEVEL_HANDLED";
	public static final String ABILITY_POINTS_DUAL_CLASS_LEVEL_HANDLED = "ABILITY_POINTS_DUAL_CLASS_LEVEL_HANDLED";
	public static final String ABILITY_POINTS_MAIN_CLASS = "ABILITY_POINTS";
	public static final String ABILITY_POINTS_DUAL_CLASS = "ABILITY_POINTS_DUAL_CLASS";
	public static final String ABILITY_POINTS_USED_MAIN_CLASS = "ABILITY_POINTS_USED";
	public static final String ABILITY_POINTS_USED_DUAL_CLASS = "ABILITY_POINTS_DUAL_CLASS_USED";
	public static final String REVELATION_SKILL_1_MAIN_CLASS = "RevelationSkill1";
	public static final String REVELATION_SKILL_2_MAIN_CLASS = "RevelationSkill2";
	public static final String REVELATION_SKILL_1_DUAL_CLASS = "DualclassRevelationSkill1";
	public static final String REVELATION_SKILL_2_DUAL_CLASS = "DualclassRevelationSkill2";
	public static final String EXTEND_DROP = "EXTEND_DROP";
	public static final String USED_PC_LOTTERY_TICKET = "USED_PC_LOTTERY_TICKET";
	public static final String CLAN_REWARD_CLAIMED = "CLAIMED_CLAN_REWARDS";
	public static final String INSTANCE_ORIGIN_LOCATION = "INSTANCE_ORIGIN";
	public static final String INSTANCE_RESTORE = "INSTANCE_RESTORE";
	public static final String FORTUNE_TELLING_VARIABLE = "FortuneTelling";
	public static final String FORTUNE_TELLING_BLACK_CAT_VARIABLE = "FortuneTellingBlackCat";
	public static final String TI_YESEGIRA_MOVIE = "TI_YESEGIRA_MOVIE";
	public static final String TI_PRESENTATION_MOVIE = "TI_presentation_movie";
	public static final String ANCIENT_ARCAN_CITY_SCENE = "ANCIENT_ARCAN_CITY_SCENE";
	public static final String DELUSION_RETURN = "DELUSION_RETURN";
	public static final String VISUAL_HAIR_ID = "visualHairId";
	public static final String VISUAL_FACE_ID = "visualFaceId";
	public static final String VISUAL_HAIR_COLOR_ID = "visualHairColorId";
	public static final String FANTASY_RETURN = "FANTASY_RETURN";
	public static final String MONSTER_RETURN = "MONSTER_RETURN";
	public static final String TAUTI_SUPPORT_BOX = "TAUTI_SUPPORT_BOX";
	public static final String GIVE_CLAN_REP = "GIVE_CLAN_REP";
	public static final String GIVE_105_LEVEL = "GIVE_105_LEVEL";
	public static final String SUPERION_SUPPORT_BOX = "SUPERION_SUPPORT_BOX";
	public static final String GIVE_DANDI_SCROLL = "GIVE_DANDI_SCROLL";
	public static final String GIVE_DANDI_SCROLL_PCCAFE = "GIVE_DANDI_SCROLL_PCCAFE";
	
	private final int _objectId;
	
	public PlayerVariables(int objectId)
	{
		_objectId = objectId;
		restoreMe();
	}
	
	@Override
	public boolean restoreMe()
	{
		// Restore previous variables.
		try (Connection con = DatabaseFactory.getInstance().getConnection();
			PreparedStatement st = con.prepareStatement(SELECT_QUERY))
		{
			st.setInt(1, _objectId);
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
						LOGGER.warn("Couldn't restore variable {} for: {}", rset.getString("var"), getPlayer(), e);
						deSerializedObject = null;
					}
					set(rset.getString("var"), deSerializedObject);
				}
			}
		}
		catch (SQLException e)
		{
			LOGGER.warn("Couldn't restore variables for: {}", getPlayer(), e);
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
				st.setInt(1, _objectId);
				st.execute();
			}
			
			// Insert all variables.
			try (PreparedStatement st = con.prepareStatement(INSERT_QUERY))
			{
				st.setInt(1, _objectId);
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
						LOGGER.warn("Couldn't store variable {} for player {}", entry.getKey(), getPlayer());
					}
				}
				st.executeBatch();
			}
		}
		catch (SQLException e)
		{
			LOGGER.warn("Couldn't update variables for player: {}", getPlayer(), e);
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
				st.setInt(1, _objectId);
				st.execute();
			}
			
			// Clear all entries
			getSet().clear();
		}
		catch (Exception e)
		{
			LOGGER.warn("Couldn't delete variables for: {}", getPlayer(), e);
			return false;
		}
		return true;
	}
	
	public PlayerInstance getPlayer()
	{
		return World.getInstance().getPlayer(_objectId);
	}
	
	public void addOneDayReward(int rewardId)
	{
		List<Integer> rewards = getList(ONE_DAY_REWARDS, Integer.class);
		if (rewards == null)
		{
			rewards = new ArrayList<>();
			set(ONE_DAY_REWARDS, rewards);
		}
		rewards.add(rewardId);
	}
	
	public void removeOneDayReward(int rewardId)
	{
		final List<Integer> rewards = getList(ONE_DAY_REWARDS, Integer.class);
		if ((rewards != null) && rewards.contains(rewardId))
		{
			rewards.remove(rewards.indexOf(rewardId));
		}
	}
	
	public boolean hasOneDayReward(int rewardId)
	{
		final List<Integer> rewards = getList(ONE_DAY_REWARDS, Integer.class);
		return ((rewards != null) && rewards.contains(rewardId));
	}
	
	public List<Integer> getOneDayRewards()
	{
		final List<Integer> rewards = getList(ONE_DAY_REWARDS, Integer.class);
		return rewards != null ? rewards : Collections.emptyList();
	}
	
	public void updateExtendDrop(int dropId, int itemId, long itemCount)
	{
		Map<Integer, List<ItemHolder>> drops = getMapOfList(EXTEND_DROP, Integer.class, ItemHolder.class);
		if (drops == null)
		{
			drops = new HashMap<>();
			set(EXTEND_DROP, drops);
		}
		
		final List<ItemHolder> itemz = drops.get(dropId);
		if (itemz != null)
		{
			itemz.removeIf(item -> item.getId() == itemId);
		}
		drops.computeIfAbsent(dropId, k -> new ArrayList<>()).add(new ItemHolder(itemId, itemCount));
	}
	
	public long getExtendDropCount(int dropId, int itemId)
	{
		final Map<Integer, List<ItemHolder>> drops = getMapOfList(EXTEND_DROP, Integer.class, ItemHolder.class);
		if (drops != null)
		{
			final List<ItemHolder> itemz = drops.get(dropId);
			if (itemz != null)
			{
				return itemz.stream().filter(drop -> drop.getId() == itemId).mapToLong(ItemHolder::getCount).findFirst().orElse(0);
			}
		}
		return 0;
	}
}
