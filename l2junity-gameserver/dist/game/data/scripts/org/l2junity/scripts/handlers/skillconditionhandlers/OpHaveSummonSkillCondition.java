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
package org.l2junity.scripts.handlers.skillconditionhandlers;

import org.l2junity.gameserver.handler.SkillConditionHandler;
import org.l2junity.gameserver.model.StatsSet;
import org.l2junity.gameserver.model.WorldObject;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.skills.ISkillCondition;
import org.l2junity.gameserver.model.skills.Skill;
import org.l2junity.gameserver.network.client.send.string.SystemMessageId;
import org.l2junity.gameserver.scripting.annotations.SkillScript;

/**
 * @author UnAfraid
 */
public class OpHaveSummonSkillCondition implements ISkillCondition
{
	public OpHaveSummonSkillCondition(StatsSet params)
	{
	}
	
	@Override
	public boolean canUse(Creature caster, Skill skill, WorldObject target)
	{
		if (!caster.hasServitors())
		{
			if (caster.isPlayer())
			{
				caster.sendPacket(SystemMessageId.YOU_CANNOT_USE_THE_SKILL_BECAUSE_THE_SERVITOR_HAS_NOT_BEEN_SUMMONED);
			}
			
			return false;
		}
		
		return true;
	}

	@SkillScript
	public static void main()
	{
		SkillConditionHandler.getInstance().registerHandler("OpHaveSummon", OpHaveSummonSkillCondition::new);
	}
}
