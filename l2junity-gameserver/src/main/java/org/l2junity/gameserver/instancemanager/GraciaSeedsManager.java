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
package org.l2junity.gameserver.instancemanager;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import org.l2junity.commons.util.concurrent.ThreadPool;
import org.l2junity.gameserver.config.GraciaSeedsConfig;
import org.l2junity.gameserver.instancemanager.tasks.UpdateSoDStateTask;
import org.l2junity.gameserver.model.quest.Quest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class GraciaSeedsManager
{
	private static final Logger LOGGER = LoggerFactory.getLogger(GraciaSeedsManager.class);
	
	public static String ENERGY_SEEDS = "EnergySeeds";
	
	private static final byte SOITYPE = 2;
	private static final byte SOATYPE = 3;
	
	// Seed of Destruction
	private static final byte SODTYPE = 1;
	private int _SoDTiatKilled = 0;
	private int _SoDState = 1;
	private final Calendar _SoDLastStateChangeDate;
	
	protected GraciaSeedsManager()
	{
		_SoDLastStateChangeDate = Calendar.getInstance();
		loadData();
		handleSodStages();
	}
	
	public void saveData(byte seedType)
	{
		switch (seedType)
		{
			case SODTYPE:
				// Seed of Destruction
				GlobalVariablesManager.getInstance().set(GlobalVariablesManager.SOD_STATE_VAR, _SoDState);
				GlobalVariablesManager.getInstance().set(GlobalVariablesManager.SOD_TIAT_KILLED_VAR, _SoDTiatKilled);
				GlobalVariablesManager.getInstance().set(GlobalVariablesManager.SOD_LSC_DATE_VAR, _SoDLastStateChangeDate.getTimeInMillis());
				break;
			case SOITYPE:
				// Seed of Infinity
				break;
			case SOATYPE:
				// Seed of Annihilation
				break;
			default:
				LOGGER.warn("Unknown SeedType in SaveData: " + seedType);
				break;
		}
	}
	
	public void loadData()
	{
		// Seed of Destruction variables
		if (GlobalVariablesManager.getInstance().hasVariable(GlobalVariablesManager.SOD_STATE_VAR))
		{
			_SoDState = GlobalVariablesManager.getInstance().getInt(GlobalVariablesManager.SOD_STATE_VAR);
			_SoDTiatKilled = GlobalVariablesManager.getInstance().getInt(GlobalVariablesManager.SOD_TIAT_KILLED_VAR);
			_SoDLastStateChangeDate.setTimeInMillis(GlobalVariablesManager.getInstance().getLong(GlobalVariablesManager.SOD_LSC_DATE_VAR));
		}
		else
		{
			// save Initial values
			saveData(SODTYPE);
		}
	}
	
	private void handleSodStages()
	{
		switch (_SoDState)
		{
			case 1:
				// do nothing, players should kill Tiat a few times
				break;
			case 2:
				// Conquest Complete state, if too much time is passed than change to defense state
				long timePast = System.currentTimeMillis() - _SoDLastStateChangeDate.getTimeInMillis();
				if (timePast >= GraciaSeedsConfig.SOD_STAGE_2_LENGTH)
				{
					// change to Attack state because Defend statet is not implemented
					setSoDState(1, true);
				}
				else
				{
					ThreadPool.schedule(new UpdateSoDStateTask(), GraciaSeedsConfig.SOD_STAGE_2_LENGTH - timePast, TimeUnit.MILLISECONDS);
				}
				break;
			case 3:
				// not implemented
				setSoDState(1, true);
				break;
			default:
				LOGGER.warn("Unknown Seed of Destruction state(" + _SoDState + ")! ");
		}
	}
	
	public void updateSodState()
	{
		final Quest quest = QuestManager.getInstance().getQuest(ENERGY_SEEDS);
		if (quest == null)
		{
			LOGGER.warn("missing EnergySeeds Quest!");
		}
		else
		{
			quest.notifyEvent("StopSoDAi", null, null);
		}
	}
	
	public void increaseSoDTiatKilled()
	{
		if (_SoDState == 1)
		{
			_SoDTiatKilled++;
			if (_SoDTiatKilled >= GraciaSeedsConfig.SOD_TIAT_KILL_COUNT)
			{
				setSoDState(2, false);
			}
			saveData(SODTYPE);
			Quest esQuest = QuestManager.getInstance().getQuest(ENERGY_SEEDS);
			if (esQuest == null)
			{
				LOGGER.warn("missing EnergySeeds Quest!");
			}
			else
			{
				esQuest.notifyEvent("StartSoDAi", null, null);
			}
		}
	}
	
	public int getSoDTiatKilled()
	{
		return _SoDTiatKilled;
	}
	
	public void setSoDState(int value, boolean doSave)
	{
		LOGGER.info("New Seed of Destruction state -> " + value + ".");
		_SoDLastStateChangeDate.setTimeInMillis(System.currentTimeMillis());
		_SoDState = value;
		// reset number of Tiat kills
		if (_SoDState == 1)
		{
			_SoDTiatKilled = 0;
		}
		
		handleSodStages();
		
		if (doSave)
		{
			saveData(SODTYPE);
		}
	}
	
	public long getSoDTimeForNextStateChange()
	{
		switch (_SoDState)
		{
			case 1:
				return -1;
			case 2:
				return ((_SoDLastStateChangeDate.getTimeInMillis() + GraciaSeedsConfig.SOD_STAGE_2_LENGTH) - System.currentTimeMillis());
			case 3:
				// not implemented yet
				return -1;
			default:
				// this should not happen!
				return -1;
		}
	}
	
	public Calendar getSoDLastStateChangeDate()
	{
		return _SoDLastStateChangeDate;
	}
	
	public int getSoDState()
	{
		return _SoDState;
	}
	
	/**
	 * Gets the single instance of {@code GraciaSeedsManager}.
	 * @return single instance of {@code GraciaSeedsManager}
	 */
	public static GraciaSeedsManager getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final GraciaSeedsManager _instance = new GraciaSeedsManager();
	}
}