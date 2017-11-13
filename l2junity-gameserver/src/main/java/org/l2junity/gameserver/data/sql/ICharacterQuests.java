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
package org.l2junity.gameserver.data.sql;

import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.quest.QuestState;

/**
 * Interface representing access to character quests data holder.
 * @author malyelfik
 */
public interface ICharacterQuests
{
	String STATE_VAR = "<state>";
	
	/**
	 * Load player QuestStates from database.
	 * @param player player which request fetch
	 */
	void loadPlayerQuests(PlayerInstance player);
	
	/**
	 * Insert or update player's quest variable depending on {@code created} parameter.
	 * @param qs player's quest state
	 * @param key variable key
	 * @param val variable value
	 */
	void updateQuestVar(QuestState qs, String key, String val);
	
	/**
	 * Insert or update state of player's quest.
	 * @param qs player's quest state
	 * @param newState name of new state
	 */
	default void updateStateVar(QuestState qs, String newState)
	{
		updateQuestVar(qs, STATE_VAR, newState);
	}
	
	/**
	 * Delete player's quest variable from database.
	 * @param qs player's quest state
	 * @param key variable key
	 */
	void deleteQuestVar(QuestState qs, String key);
	
	/**
	 * Delete player's quest from database.
	 * @param qs player's quest state
	 * @param repeatable type of quest
	 */
	void deletePlayerQuest(QuestState qs, boolean repeatable);
}
