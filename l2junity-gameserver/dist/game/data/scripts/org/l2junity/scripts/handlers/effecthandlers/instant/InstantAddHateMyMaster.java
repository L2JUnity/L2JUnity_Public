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
package org.l2junity.scripts.handlers.effecthandlers.instant;

import org.l2junity.gameserver.handler.EffectHandler;
import org.l2junity.gameserver.model.StatsSet;
import org.l2junity.gameserver.model.WorldObject;
import org.l2junity.gameserver.model.actor.Attackable;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.effects.AbstractEffect;
import org.l2junity.gameserver.model.items.instance.ItemInstance;
import org.l2junity.gameserver.model.skills.Skill;
import org.l2junity.gameserver.scripting.annotations.SkillScript;

/**
 * An effect that adds hate to affected NPCs, but instead of effector being hated, it is effector's summoner being hated.
 * @author Nik
 */
public final class InstantAddHateMyMaster extends AbstractEffect
{
	private final int _power;
	
	public InstantAddHateMyMaster(StatsSet params)
	{
		_power = params.getInt("power");
	}
	
	@Override
	public void instant(Creature caster, WorldObject target, Skill skill, ItemInstance item)
	{
		// This effect only affects effector's master.
		if (caster.getSummoner() == null)
		{
			return;
		}
		
		final Attackable targetAttackable = target.asAttackable();
		if (targetAttackable == null)
		{
			return;
		}
		
		if (_power > 0)
		{
			targetAttackable.addDamageHate(caster.getSummoner(), 0, _power);
		}
		else if (_power < 0)
		{
			targetAttackable.reduceHate(caster.getSummoner(), -_power);
		}
	}

	@SkillScript
	public static void main()
	{
		EffectHandler.getInstance().registerHandler("i_add_hate_mymaster", InstantAddHateMyMaster::new);
	}
}
