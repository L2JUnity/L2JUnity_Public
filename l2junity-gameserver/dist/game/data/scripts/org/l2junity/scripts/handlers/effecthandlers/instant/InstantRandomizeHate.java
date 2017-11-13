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

import java.util.List;

import org.l2junity.commons.util.Rnd;
import org.l2junity.gameserver.handler.EffectHandler;
import org.l2junity.gameserver.model.StatsSet;
import org.l2junity.gameserver.model.World;
import org.l2junity.gameserver.model.WorldObject;
import org.l2junity.gameserver.model.actor.Attackable;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.effects.AbstractEffect;
import org.l2junity.gameserver.model.items.instance.ItemInstance;
import org.l2junity.gameserver.model.skills.Skill;
import org.l2junity.gameserver.model.stats.Formulas;
import org.l2junity.gameserver.scripting.annotations.SkillScript;

/**
 * Randomize Hate effect implementation.
 */
public final class InstantRandomizeHate extends AbstractEffect
{
	private final int _chance;
	
	public InstantRandomizeHate(StatsSet params)
	{
		_chance = params.getInt("chance");
	}
	
	@Override
	public boolean calcSuccess(Creature caster, WorldObject target, Skill skill)
	{
		return target.isAttackable() && Formulas.calcProbability(_chance, caster, target.asAttackable(), skill);
	}
	
	@Override
	public void instant(Creature caster, WorldObject target, Skill skill, ItemInstance item)
	{
		final Attackable targetAttackable = target.asAttackable();
		if (targetAttackable == null)
		{
			return;
		}
		
		if (caster.equals(targetAttackable))
		{
			return;
		}
		
		final List<Creature> targetList = World.getInstance().getVisibleObjects(targetAttackable, Creature.class, c -> !c.equals(caster) && (!c.isAttackable() || !c.asAttackable().isInMyClan(targetAttackable)));
		if (targetList.isEmpty())
		{
			return;
		}
		
		// Choosing randomly a new target
		final Creature randomTarget = targetList.get(Rnd.get(targetList.size()));
		final int hate = targetAttackable.getHating(caster);
		targetAttackable.stopHating(caster);
		targetAttackable.addDamageHate(randomTarget, 0, hate);
	}

	@SkillScript
	public static void main()
	{
		EffectHandler.getInstance().registerHandler("i_randomize_hate", InstantRandomizeHate::new);
	}
}