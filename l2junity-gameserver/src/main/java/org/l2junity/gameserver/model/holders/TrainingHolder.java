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
package org.l2junity.gameserver.model.holders;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import org.l2junity.gameserver.config.TrainingCampConfig;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;

/**
 * @author Sdw
 */
public class TrainingHolder implements Serializable
{
	private static final long serialVersionUID = 372541022666487591L;
	private final int _objectId;
	private final int _classIndex;
	private final int _level;
	private final long _startTime;
	private long _endTime = -1;
	private static final long TRAINING_DIVIDER = TimeUnit.SECONDS.toMinutes(TrainingCampConfig.MAX_DURATION);
	
	public TrainingHolder(int objectId, int classIndex, int level, long startTime)
	{
		_objectId = objectId;
		_classIndex = classIndex;
		_level = level;
		_startTime = startTime;
	}
	
	public long getEndTime()
	{
		return _endTime;
	}
	
	public void setEndTime(long _endTime)
	{
		this._endTime = _endTime;
	}
	
	public int getObjectId()
	{
		return _objectId;
	}
	
	public int getClassIndex()
	{
		return _classIndex;
	}
	
	public int getLevel()
	{
		return _level;
	}
	
	public long getStartTime()
	{
		return _startTime;
	}
	
	public boolean isTraining()
	{
		return _endTime == -1;
	}
	
	public boolean isValid(PlayerInstance player)
	{
		return TrainingCampConfig.ENABLE && (player.getObjectId() == _objectId) && (player.getClassIndex() == _classIndex);
	}
	
	public long getTime(long time, TimeUnit unit)
	{
		return unit.convert(time - _startTime, TimeUnit.MILLISECONDS);
	}
	
	public long getReminaingTime()
	{
		return TimeUnit.SECONDS.toMinutes(TrainingCampConfig.MAX_DURATION - getTime(System.currentTimeMillis(), TimeUnit.SECONDS));
	}
	
	public long getReminaingTime(long time)
	{
		return TimeUnit.SECONDS.toMinutes(TrainingCampConfig.MAX_DURATION - time);
	}
	
	public long getTrainingTime(TimeUnit unit)
	{
		return Math.min(unit.convert(TrainingCampConfig.MAX_DURATION, TimeUnit.SECONDS), unit.convert(_endTime - _startTime, TimeUnit.MILLISECONDS));
	}
	
	public static long getTrainingDivider()
	{
		return TRAINING_DIVIDER;
	}
}
