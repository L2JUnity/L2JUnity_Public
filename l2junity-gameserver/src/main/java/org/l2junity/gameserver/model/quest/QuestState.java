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
package org.l2junity.gameserver.model.quest;

import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.l2junity.gameserver.data.sql.impl.CharacterQuests;
import org.l2junity.gameserver.enums.QuestSound;
import org.l2junity.gameserver.enums.QuestType;
import org.l2junity.gameserver.instancemanager.QuestManager;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.events.AbstractScript;
import org.l2junity.gameserver.model.events.EventDispatcher;
import org.l2junity.gameserver.model.events.impl.character.player.OnPlayerQuestComplete;
import org.l2junity.gameserver.network.client.send.ExShowQuestMark;
import org.l2junity.gameserver.network.client.send.QuestList;
import org.l2junity.gameserver.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Quest state class.
 * @author Luis Arias, malyelfik
 */
public final class QuestState
{
	protected static final Logger LOGGER = LoggerFactory.getLogger(QuestState.class);
	
	// Constants
	private static final String COND_VAR = "cond";
	private static final String RESTART_VAR = "restartTime";
	private static final String MEMO_VAR = "memoState";
	private static final String MEMO_EX_VAR = "memoStateEx";
	
	/** The name of the quest of this QuestState */
	private final String _questName;
	/** The "owner" of this QuestState object */
	private final PlayerInstance _player;
	/** The current state of the quest */
	private byte _state;
	/** A map of key->value pairs containing the quest state variables and their values */
	private volatile Map<String, String> _vars;
	
	/**
	 * Constructor of the QuestState. Creates the QuestState object and sets the player's progress of the quest to this QuestState.
	 * @param quest the {@link Quest} object associated with the QuestState
	 * @param player the owner of this {@link QuestState} object
	 * @param state the initial state of the quest
	 */
	public QuestState(Quest quest, PlayerInstance player, byte state)
	{
		_questName = quest.getName();
		_player = player;
		_state = state;
		
		player.setQuestState(this);
	}
	
	/**
	 * Create QuestState object for player's quest progress and sets initial state to {@link State#CREATED}.
	 * @param quest the {@link Quest} associated with this quest state
	 * @param player owner of this object
	 */
	public QuestState(Quest quest, PlayerInstance player)
	{
		this(quest, player, State.CREATED);
	}
	
	/**
	 * Get name of underlying quest.
	 * @return the name of the quest of this QuestState
	 */
	public String getQuestName()
	{
		return _questName;
	}
	
	/**
	 * Get ID of underlying quest.
	 * @return id of quest
	 */
	public int getQuestId()
	{
		return getQuest().getId();
	}
	
	/**
	 * Get underlying quest object.
	 * @return the {@link Quest} object of this QuestState
	 */
	public Quest getQuest()
	{
		return QuestManager.getInstance().getQuest(_questName);
	}
	
	/**
	 * Get player associated with this quest state.
	 * @return the {@link PlayerInstance} object of the owner of this QuestState
	 */
	public PlayerInstance getPlayer()
	{
		return _player;
	}
	
	/**
	 * Get current quest {@link State}.
	 * @return the current {@link State} of this {@link QuestState}
	 */
	public byte getState()
	{
		return _state;
	}
	
	/**
	 * Check if current state is {@link State#CREATED}.
	 * @return {@code true} if the State of this QuestState is CREATED, {@code false} otherwise
	 */
	public boolean isCreated()
	{
		return (_state == State.CREATED);
	}
	
	/**
	 * Check if current state is {@link State#STARTED}.
	 * @return {@code true} if the State of this QuestState is STARTED, {@code false} otherwise
	 */
	public boolean isStarted()
	{
		return (_state == State.STARTED);
	}
	
	/**
	 * Check if current state is {@link State#COMPLETED}.
	 * @return {@code true} if the State of this QuestState is COMPLETED, {@code false} otherwise
	 */
	public boolean isCompleted()
	{
		return (_state == State.COMPLETED);
	}
	
	/**
	 * Change the state of this quest to the specified value.
	 * @param state the new {@link State} of the quest to set
	 * @return {@code true} if state was changed, {@code false} otherwise
	 */
	public boolean setState(byte state)
	{
		// Check if state is same
		if (_state == state)
		{
			return false;
		}
		
		// Update state in memory
		_state = state;
		
		// Store changes to database
		CharacterQuests.getInstance().updateStateVar(this, State.getStateName(state));
		
		// Send quest list update
		_player.sendPacket(new QuestList(_player));
		return true;
	}
	
	public Map<String, String> getVars()
	{
		return _vars != null ? Collections.unmodifiableMap(_vars) : Collections.emptyMap();
	}
	
	/**
	 * Store quest variable.
	 * @param key name of stored variable
	 * @param val value of stored variable
	 * @param save if {@code true} variable is stored persistently, otherwise variable is stored temporarily
	 * @return previous value for variable key if any exist otherwise {@code null}
	 */
	public String set(String key, String val, boolean save)
	{
		// Lazy initialize quest vars map
		if (_vars == null)
		{
			synchronized (this)
			{
				if (_vars == null)
				{
					_vars = new HashMap<>();
				}
			}
		}
		
		// Process val param
		if (val == null)
		{
			val = "";
		}
		
		// Store changes to memory and database
		final String prevVal = _vars.put(key, val);
		if (save)
		{
			CharacterQuests.getInstance().updateQuestVar(this, key, val);
		}
		return prevVal;
	}
	
	/**
	 * Store quest variable persistently.<br>
	 * <b>For cond set use {@link #setCond(int)} instead.</b>
	 * @param key name of stored variable
	 * @param val value of stored variable
	 * @return previous value for variable key if any exist otherwise {@code null}
	 */
	public String set(String key, String val)
	{
		return set(key, val, true);
	}
	
	/**
	 * Store quest variable persistently.<br>
	 * <b>For cond set use {@link #setCond(int)} instead.</b>
	 * @param key name of stored variable
	 * @param val value of stored variable
	 * @return previous value for variable key if any exist otherwise {@code null}
	 */
	public String set(String key, int val)
	{
		return set(key, Integer.toString(val));
	}
	
	/**
	 * Delete quest variable.
	 * @param key the name of the variable to remove
	 * @return the previous value of the variable or {@code null} if none were found
	 */
	public String unset(String key)
	{
		if (_vars == null)
		{
			return null;
		}
		
		final String old = _vars.remove(key);
		if (old != null)
		{
			CharacterQuests.getInstance().deleteQuestVar(this, key);
		}
		return old;
	}
	
	/**
	 * Get value for quest variable.
	 * @param key the name of the variable to get
	 * @return the value of the variable from the list of quest variables
	 */
	public String get(String key)
	{
		return (_vars == null) ? null : _vars.get(key);
	}
	
	/**
	 * Get integer value for quest variable.
	 * @param key the name of the variable to get
	 * @return the integer value of the variable or 0 if the variable does not exist or its value is not an integer
	 */
	public int getInt(String key)
	{
		final String value = get(key);
		if ((value == null) || value.isEmpty())
		{
			return 0;
		}
		
		int varint = 0;
		try
		{
			varint = Integer.parseInt(value);
		}
		catch (NumberFormatException nfe)
		{
			LOGGER.info("Failed to parse property {} => {} as integer at quest {} for player {}.", key, value, _questName, _player);
		}
		return varint;
	}
	
	/**
	 * Check if a given variable is set for this quest.
	 * @param key the name of variable to check
	 * @return {@code true} if the variable is set, {@code false} otherwise
	 */
	public boolean isSet(String key)
	{
		return get(key) != null;
	}
	
	/**
	 * Sets the quest state progress ({@code cond}) to the specified step.
	 * @param value the new value of the quest state progress
	 * @return this {@link QuestState} object
	 * @see #setCond(int, boolean)
	 */
	public QuestState setCond(int value)
	{
		return setCond(value, false);
	}
	
	/**
	 * Sets the quest state progress ({@code cond}) to the specified step.<br>
	 * Also send {@link ExShowQuestMark} and {@link QuestList} packet upon successful change.
	 * @param newCond the new value of the quest state progress
	 * @param playSound if {@code true}, plays {@link QuestSound#ITEMSOUND_QUEST_MIDDLE}
	 * @return this {@link QuestState} object
	 */
	public QuestState setCond(int newCond, boolean playSound)
	{
		// Skip cond change if quest is not started
		if (!isStarted())
		{
			return this;
		}
		
		// Check if cond is between bounds
		if ((newCond < 1) || (newCond > 31))
		{
			LOGGER.info("Player {} requested cond change to value ({}) which is out of bounds (1-31) within quest {}!", _player, newCond, _questName);
			return this;
		}
		
		// Skip cond change if prev and new cond are equal
		final int prevCond = getCond();
		if (newCond == prevCond)
		{
			return this;
		}
		
		// Compute new cond mask
		int condMask = getInt(COND_VAR);
		if (newCond > prevCond)
		{
			condMask |= 1 << (newCond - 1);
		}
		else
		{
			condMask &= (1 << newCond) - 1;
		}
		
		// Store mask change
		set(COND_VAR, condMask);
		
		// Play quest sound
		if (playSound)
		{
			AbstractScript.playSound(_player, QuestSound.ITEMSOUND_QUEST_MIDDLE);
		}
		
		// Send packets to client if not custom
		final Quest quest = getQuest();
		if (!quest.isCustomQuest() && (newCond > 0))
		{
			// If you keep that order then client send {@link RequestAddExpandQuestAlarm} packet
			_player.sendPacket(new ExShowQuestMark(getQuestId(), newCond));
			_player.sendPacket(new QuestList(_player));
		}
		return this;
	}
	
	/**
	 * Get bit set representing completed conds.
	 * @return if none cond is set {@code 1}, otherwise cond bit set <b>with MSB set to 1</b>
	 */
	public int getRawCond()
	{
		final int raw = getInt(COND_VAR);
		return (raw == 0) ? 1 : (raw | 0x80000000); // Set MSB to 1
	}
	
	/**
	 * Get {@code cond} number representing current quest progress.<br>
	 * <br>
	 * <i><b>NOTE:</b> Computed in O(log2(n)) due searching for highest one bit set.</i>
	 * @return the current quest progress ({@code cond})
	 */
	public int getCond()
	{
		// Check if quest is started
		if (!isStarted())
		{
			return 0;
		}
		
		// Fetch cond bit set and return cond
		final int condSet = getInt(COND_VAR);
		return (condSet == 0) ? 0 : (32 - Integer.numberOfLeadingZeros(condSet));
	}
	
	/**
	 * Checks if the quest state progress ({@code cond}) is at the specified step.
	 * @param condition the condition to check against
	 * @return {@code true} if the quest condition is equal to {@code condition}, {@code false} otherwise
	 */
	public boolean isCond(int condition)
	{
		return getCond() == condition;
	}
	
	/**
	 * Store memo state variable.
	 * @param value value to be set
	 * @return self {@link QuestState}
	 */
	public QuestState setMemoState(int value)
	{
		set(MEMO_VAR, value);
		return this;
	}
	
	/**
	 * Gets memo state variable.
	 * @return the current memo state value
	 */
	public int getMemoState()
	{
		return isStarted() ? getInt(MEMO_VAR) : 0;
	}
	
	/**
	 * Check if memo state is equal to {@code memoState}.
	 * @param memoState value to be set
	 * @return {@code true} if values are same otherwise {@code false}
	 */
	public boolean isMemoState(int memoState)
	{
		return getMemoState() == memoState;
	}
	
	/**
	 * Gets the memo state ex.
	 * @param slot the slot where the value was saved
	 * @return the memo state ex
	 */
	public int getMemoStateEx(int slot)
	{
		return isStarted() ? getInt(MEMO_EX_VAR + slot) : 0;
	}
	
	/**
	 * Sets the memo state ex.
	 * @param slot the slot where the value will be saved
	 * @param value the value
	 * @return this QuestState
	 */
	public QuestState setMemoStateEx(int slot, int value)
	{
		set(MEMO_EX_VAR + slot, value);
		return this;
	}
	
	/**
	 * Verifies if the given value is equal to the current memos state ex.
	 * @param slot the slot where the value was saved
	 * @param memoStateEx the value to verify
	 * @return {@code true} if the values are equal, {@code false} otherwise
	 */
	public boolean isMemoStateEx(int slot, int memoStateEx)
	{
		return getMemoStateEx(slot) == memoStateEx;
	}
	
	/**
	 * Set condition to {@code 1}, state to {@link State#STARTED} and play the {@link QuestSound#ITEMSOUND_QUEST_ACCEPT}.<br>
	 * Works only if state is {@link State#CREATED} and the quest is not a custom.
	 * @return the newly created {@link QuestState} object
	 */
	public QuestState startQuest()
	{
		if (isCreated() && !getQuest().isCustomQuest())
		{
			setState(State.STARTED);
			setCond(1);
			AbstractScript.playSound(_player, QuestSound.ITEMSOUND_QUEST_ACCEPT);
		}
		return this;
	}
	
	/**
	 * Finishes the quest and removes all quest items associated with this quest from the player's inventory.<br>
	 * If {@code type} is {@code QuestType.ONE_TIME}, also removes all other quest data associated with this quest.
	 * @param type the {@link QuestType} of the quest
	 * @param playExitSound if {@code true}, plays "ItemSound.quest_finish"
	 * @return this {@link QuestState} object
	 * @see #exitQuest(QuestType type, boolean playExitQuest)
	 * @see #exitQuest(boolean repeatable, boolean playExitQuest)
	 */
	public QuestState exitQuest(QuestType type, boolean playExitSound)
	{
		// Check if started
		if (!isStarted())
		{
			return this;
		}
		
		// Clean registered quest items
		final Quest quest = getQuest();
		quest.removeRegisteredQuestItems(_player);
		
		// Delete quest state
		final boolean repeatable = (type == QuestType.REPEATABLE);
		CharacterQuests.getInstance().deletePlayerQuest(this, repeatable);
		if (repeatable)
		{
			_player.delQuestState(_questName);
			_player.sendPacket(new QuestList(_player));
		}
		else
		{
			setState(State.COMPLETED);
		}
		_vars = null;
		
		// If quest is daily then set reenter time
		if (type == QuestType.DAILY)
		{
			final Calendar reDo = Calendar.getInstance();
			if (reDo.get(Calendar.HOUR_OF_DAY) >= quest.getResetHour())
			{
				reDo.add(Calendar.DATE, 1);
			}
			reDo.set(Calendar.HOUR_OF_DAY, quest.getResetHour());
			reDo.set(Calendar.MINUTE, quest.getResetMinutes());
			set(RESTART_VAR, String.valueOf(reDo.getTimeInMillis()));
		}
		
		// Play quest exit sound
		if (playExitSound)
		{
			AbstractScript.playSound(_player, QuestSound.ITEMSOUND_QUEST_FINISH);
		}
		
		// Notify listeners
		EventDispatcher.getInstance().notifyEventAsync(new OnPlayerQuestComplete(_player, quest.getId(), type), _player);
		return this;
	}
	
	/**
	 * Finishes the quest and removes all quest items associated with this quest from the player's inventory.<br>
	 * If {@code type} is {@code QuestType.ONE_TIME}, also removes all other quest data associated with this quest.
	 * @param type the {@link QuestType} of the quest
	 * @return this {@link QuestState} object
	 * @see #exitQuest(QuestType type)
	 * @see #exitQuest(QuestType type, boolean playExitSound)
	 * @see #exitQuest(boolean repeatable, boolean playExitQuest)
	 */
	public QuestState exitQuest(QuestType type)
	{
		return exitQuest(type, false);
	}
	
	/**
	 * Finishes the quest and removes all quest items associated with this quest from the player's inventory.<br>
	 * If {@code repeatable} is set to {@code false}, also removes all other quest data associated with this quest.
	 * @param repeatable if {@code true}, deletes all data and variables of this quest, otherwise keeps them
	 * @param playExitQuest if {@code true}, plays "ItemSound.quest_finish"
	 * @return this {@link QuestState} object
	 * @see #exitQuest(QuestType type)
	 * @see #exitQuest(QuestType type, boolean playExitSound)
	 * @see #exitQuest(boolean repeatable, boolean playExitQuest)
	 */
	public QuestState exitQuest(boolean repeatable, boolean playExitQuest)
	{
		final QuestType type = (repeatable) ? QuestType.REPEATABLE : QuestType.ONE_TIME;
		return exitQuest(type, playExitQuest);
	}
	
	/**
	 * Check if a daily quest is available to be started over.
	 * @return {@code true} if the quest is available, {@code false} otherwise.
	 */
	public boolean isNowAvailable()
	{
		final String val = get(RESTART_VAR);
		return (val != null) && (!Util.isDigit(val) || (Long.parseLong(val) <= System.currentTimeMillis()));
	}
}