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
package org.l2junity.gameserver.model.events.impl.restriction;

import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.base.AcquireSkillType;
import org.l2junity.gameserver.model.events.EventType;
import org.l2junity.gameserver.model.events.impl.IBaseEvent;

/**
 * @author Nik
 */
public final class CanPlayerLearnSkill implements IBaseEvent
{
	private final PlayerInstance _activeChar;
	private final int _skillId;
	private final int _skillLevel;
	private final AcquireSkillType _skillType;
	private final int _subType;
	
	public CanPlayerLearnSkill(PlayerInstance activeChar, int skillId, int skillLevel, AcquireSkillType skillType, int subType)
	{
		_activeChar = activeChar;
		_skillId = skillId;
		_skillLevel = skillLevel;
		_skillType = skillType;
		_subType = subType;
	}
	
	public PlayerInstance getActiveChar()
	{
		return _activeChar;
	}
	
	public int getSkillId()
	{
		return _skillId;
	}
	
	public int getSkillLevel()
	{
		return _skillLevel;
	}
	
	public AcquireSkillType getSkillType()
	{
		return _skillType;
	}
	
	public int getSubType()
	{
		return _subType;
	}
	
	@Override
	public EventType getType()
	{
		return EventType.CAN_PLAYER_LEARN_SKILL;
	}
}
