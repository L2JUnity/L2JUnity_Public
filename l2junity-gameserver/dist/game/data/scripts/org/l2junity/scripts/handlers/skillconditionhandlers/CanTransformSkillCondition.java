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
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.skills.ISkillCondition;
import org.l2junity.gameserver.model.skills.Skill;
import org.l2junity.gameserver.model.stats.BooleanStat;
import org.l2junity.gameserver.network.client.send.string.SystemMessageId;
import org.l2junity.gameserver.scripting.annotations.SkillScript;

/**
 * @author Sdw
 */
public class CanTransformSkillCondition implements ISkillCondition
{
	// TODO: What to do with this?
	// private final int _transformId;
	
	public CanTransformSkillCondition(StatsSet params)
	{
		// _transformId = params.getInt("transformId");
	}
	
	@Override
	public boolean canUse(Creature caster, Skill skill, WorldObject target)
	{
		if (target.isPlayer())
		{
			final PlayerInstance player = target.getActingPlayer();
			if ((player == null) || player.isAlikeDead() || player.isCursedWeaponEquipped())
			{
				return false;
			}
			if (player.isSitting())
			{
				player.sendPacket(SystemMessageId.YOU_CANNOT_TRANSFORM_WHILE_SITTING);
				return false;
			}
			if (player.isTransformed())
			{
				player.sendPacket(SystemMessageId.YOU_ALREADY_POLYMORPHED_AND_CANNOT_POLYMORPH_AGAIN);
				return false;
			}
			if (player.isInWater())
			{
				player.sendPacket(SystemMessageId.YOU_CANNOT_POLYMORPH_INTO_THE_DESIRED_FORM_IN_WATER);
				return false;
			}
			if (player.isFlyingMounted() || player.isMounted())
			{
				player.sendPacket(SystemMessageId.YOU_CANNOT_TRANSFORM_WHILE_RIDING_A_PET);
				return false;
			}
			if (player.getStat().has(BooleanStat.TRANSFORM_DISABLE))
			{
				player.sendPacket(SystemMessageId.YOU_ARE_STILL_UNDER_TRANSFORMATION_PENALTY_AND_CANNOT_BE_POLYMORPHED);
				return false;
			}
		}
		
		return true;
	}

	@SkillScript
	public static void main()
	{
		SkillConditionHandler.getInstance().registerHandler("CanTransform", CanTransformSkillCondition::new);
	}
}
