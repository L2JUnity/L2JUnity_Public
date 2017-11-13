/*
 * Copyright (C) 2004-2015 L2J DataPack
 * 
 * This file is part of L2J DataPack.
 * 
 * L2J DataPack is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J DataPack is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2junity.scripts.handlers.targethandlers.affectscope;

import java.util.function.Consumer;

import org.l2junity.commons.lang.mutable.MutableInt;
import org.l2junity.gameserver.geodata.GeoData;
import org.l2junity.gameserver.handler.IAffectScopeHandler;
import org.l2junity.gameserver.handler.TargetHandler;
import org.l2junity.gameserver.model.World;
import org.l2junity.gameserver.model.WorldObject;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.skills.Skill;
import org.l2junity.gameserver.scripting.annotations.SkillScript;

/**
 * Ring Range affect scope implementation. Gathers objects in ring/donut shaped area with start and end range.
 * @author Nik
 */
public class RingRange implements IAffectScopeHandler
{
	@Override
	public void forEachAffected(Creature activeChar, WorldObject target, Skill skill, Consumer<? super WorldObject> action)
	{
		final int affectRange = skill.getAffectRange();
		final int affectLimit = skill.getAffectLimit();
		final int startRange = skill.getFanRange()[2];
		
		// Target checks.
		final MutableInt affected = new MutableInt(0);
		
		// Check and add targets.
		World.getInstance().forEachVisibleObjectInRadius(target, Creature.class, affectRange, c ->
		{
			if ((affectLimit > 0) && (affected.intValue() >= affectLimit))
			{
				return;
			}
			
			if (c.isDead())
			{
				return;
			}
			
			// Targets before the start range are unaffected.
			if (c.isInRadius2d(target, startRange))
			{
				return;
			}
			
			if (!skill.getAffectObjectHandler().checkAffectedObject(activeChar, c))
			{
				return;
			}
			
			if (!GeoData.getInstance().canSeeTarget(target, c))
			{
				return;
			}
			
			affected.increment();
			action.accept(c);
		});
	}
	
	@SkillScript
	public static void main()
	{
		TargetHandler.getInstance().registerAffectScopeHandler("RING_RANGE", new RingRange());
	}
}