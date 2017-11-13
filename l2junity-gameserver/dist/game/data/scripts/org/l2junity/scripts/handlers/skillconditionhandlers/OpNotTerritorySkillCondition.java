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
import org.l2junity.gameserver.model.Territory;
import org.l2junity.gameserver.model.WorldObject;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.skills.ISkillCondition;
import org.l2junity.gameserver.model.skills.Skill;
import org.l2junity.gameserver.scripting.annotations.SkillScript;

/**
 * @author UnAfraid
 */
public class OpNotTerritorySkillCondition implements ISkillCondition
{
	private enum AffectType
	{
		SELF,
		ALL
	}

	private final Territory _territory = new Territory();
	private final AffectType _type;

	public OpNotTerritorySkillCondition(StatsSet params)
	{
		_type = params.getEnum("affectType", AffectType.class, AffectType.SELF);
		for (StatsSet loc : params.getList("territory", StatsSet.class))
		{
			_territory.addPoint(loc.getInt(".x"), loc.getInt(".y"), loc.getInt(".minZ"), loc.getInt(".maxZ"));
		}
	}
	
	@Override
	public boolean canUse(Creature caster, Skill skill, WorldObject target)
	{
		switch (_type)
		{
			case SELF:
			{
				return !_territory.isInside(caster.getX(), caster.getY(), caster.getZ());
			}
			case ALL:
			{
				return !_territory.isInside(caster.getX(), caster.getY(), caster.getZ()) && !_territory.isInside(target.getX(), target.getY(), target.getZ());
			}
		}
		return false;
	}

	@SkillScript
	public static void main()
	{
		SkillConditionHandler.getInstance().registerHandler("OpNotTerritory", OpNotTerritorySkillCondition::new);
	}
}
