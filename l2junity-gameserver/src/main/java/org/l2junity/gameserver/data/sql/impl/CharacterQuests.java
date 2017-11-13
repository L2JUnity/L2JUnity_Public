/*
 * Copyright (C) 2004-2016 L2J Unity
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
package org.l2junity.gameserver.data.sql.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.l2junity.commons.sql.DatabaseFactory;
import org.l2junity.gameserver.config.GeneralConfig;
import org.l2junity.gameserver.data.sql.ICharacterQuests;
import org.l2junity.gameserver.instancemanager.QuestManager;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.quest.Quest;
import org.l2junity.gameserver.model.quest.QuestState;
import org.l2junity.gameserver.model.quest.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of {@link ICharacterQuests} interface.
 * @author malyelfik
 */
public class CharacterQuests implements ICharacterQuests
{
	private static final Logger LOGGER = LoggerFactory.getLogger(CharacterQuests.class);
	
	// SQL queries
	private static final String SELECT_STATES = "SELECT name, value FROM character_quests WHERE charId=? AND var=?";
	private static final String SELECT_VARIABLES = "SELECT name, var, value FROM character_quests WHERE charId=? AND var <> ?";
	private static final String INSERT_OR_UPDATE_VAR = "INSERT INTO character_quests (charId,name,var,value) VALUES (?,?,?,?) ON DUPLICATE KEY UPDATE value = ?";
	private static final String DELETE_VAR = "DELETE FROM character_quests WHERE charId=? AND name=? AND var=?";
	private static final String DELETE_QUEST = "DELETE FROM character_quests WHERE charId=? AND name=? AND var!=?";
	private static final String DELETE_QUEST_REPEAT = "DELETE FROM character_quests WHERE charId=? AND name=?";
	
	@Override
	public void loadPlayerQuests(PlayerInstance player)
	{
		try (Connection conn = DatabaseFactory.getInstance().getConnection();
			PreparedStatement stateSel = conn.prepareStatement(SELECT_STATES);
			PreparedStatement delInvalid = conn.prepareStatement(DELETE_QUEST_REPEAT);
			PreparedStatement variableSel = conn.prepareStatement(SELECT_VARIABLES))
		{
			stateSel.setInt(1, player.getObjectId());
			stateSel.setString(2, STATE_VAR);
			
			// Fetch pairs<questName, state>
			try (ResultSet rs = stateSel.executeQuery())
			{
				while (rs.next())
				{
					// Check if quest with name exists
					final String questName = rs.getString("name");
					final Quest quest = QuestManager.getInstance().getQuest(questName);
					if (quest == null)
					{
						LOGGER.debug("Unknown quest: {} for player {}!", questName, player.getName());
						if (GeneralConfig.AUTODELETE_INVALID_QUEST_DATA)
						{
							delInvalid.setInt(1, player.getObjectId());
							delInvalid.setString(2, questName);
							delInvalid.executeUpdate();
						}
						continue;
					}
					
					// Create new quest state
					new QuestState(quest, player, State.getStateId(rs.getString("value")));
				}
			}
			
			// Load other quest variables
			variableSel.setInt(1, player.getObjectId());
			variableSel.setString(2, STATE_VAR);
			try (ResultSet rs = variableSel.executeQuery())
			{
				while (rs.next())
				{
					final String questId = rs.getString("name");
					final String var = rs.getString("var");
					final String value = rs.getString("value");
					
					// Check if quest state exists
					final QuestState qs = player.getQuestState(questId);
					if (qs == null)
					{
						LOGGER.debug("Lost variable {} in quest {} for player {}!", var, questId, player.getName());
						continue;
					}
					
					// Add parameter to the quest
					qs.set(var, value, false);
				}
			}
			
		}
		catch (SQLException e)
		{
			LOGGER.warn("Error occurred while loading player quests: ", e);
		}
	}
	
	@Override
	public void updateQuestVar(QuestState qs, String key, String val)
	{
		try (Connection conn = DatabaseFactory.getInstance().getConnection();
			PreparedStatement ps = conn.prepareStatement(INSERT_OR_UPDATE_VAR))
		{
			ps.setInt(1, qs.getPlayer().getObjectId());
			ps.setString(2, qs.getQuestName());
			ps.setString(3, key);
			ps.setString(4, val);
			ps.setString(5, val);
			ps.executeUpdate();
		}
		catch (SQLException e)
		{
			LOGGER.warn("Error occurred while updating player quest variable: ", e);
		}
	}
	
	@Override
	public void deleteQuestVar(QuestState qs, String key)
	{
		try (Connection conn = DatabaseFactory.getInstance().getConnection();
			PreparedStatement ps = conn.prepareStatement(DELETE_VAR))
		{
			ps.setInt(1, qs.getPlayer().getObjectId());
			ps.setString(2, qs.getQuestName());
			ps.setString(3, key);
			ps.executeUpdate();
		}
		catch (SQLException e)
		{
			LOGGER.warn("Error occurred while deleting player quest variable: ", e);
		}
	}
	
	@Override
	public void deletePlayerQuest(QuestState qs, boolean repeatable)
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(repeatable ? DELETE_QUEST_REPEAT : DELETE_QUEST))
		{
			ps.setInt(1, qs.getPlayer().getObjectId());
			ps.setString(2, qs.getQuestName());
			if (!repeatable)
			{
				ps.setString(3, STATE_VAR);
			}
			ps.executeUpdate();
		}
		catch (Exception e)
		{
			LOGGER.warn("Error occurred while deleting player quest: ", e);
		}
		
	}
	
	// ----------------------------------------------------------------------------------------------
	// Singleton
	// NOTE: Using simple singleton instead of singleton factory since only one implementation exist.
	// ----: Would be nice if we have some kind of service container or full DI implementation.
	// ----------------------------------------------------------------------------------------------
	
	public static ICharacterQuests getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final ICharacterQuests INSTANCE = new CharacterQuests();
	}
}
