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

import org.l2junity.gameserver.ai.CtrlIntention;
import org.l2junity.gameserver.handler.EffectHandler;
import org.l2junity.gameserver.model.StatsSet;
import org.l2junity.gameserver.model.WorldObject;
import org.l2junity.gameserver.model.actor.Attackable;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.effects.AbstractEffect;
import org.l2junity.gameserver.model.effects.L2EffectType;
import org.l2junity.gameserver.model.items.instance.ItemInstance;
import org.l2junity.gameserver.model.skills.Skill;
import org.l2junity.gameserver.model.stats.Formulas;
import org.l2junity.gameserver.scripting.annotations.SkillScript;

/**
 * Delete Hate Of Me effect implementation.
 * @author Adry_85
 */
public final class InstantDeleteHateOfMe extends AbstractEffect
{
	private final int _chance;
	
	public InstantDeleteHateOfMe(StatsSet params)
	{
		_chance = params.getInt("chance");
	}
	
	@Override
	public boolean calcSuccess(Creature caster, WorldObject target, Skill skill)
	{
		return target.isAttackable() && Formulas.calcProbability(_chance, caster, target.asAttackable(), skill);
	}
	
	@Override
	public L2EffectType getEffectType()
	{
		return L2EffectType.HATE;
	}
	
	@Override
	public void instant(Creature caster, WorldObject target, Skill skill, ItemInstance item)
	{
		final Attackable targetAttackable = target.asAttackable();
		if (targetAttackable == null)
		{
			return;
		}
		
		targetAttackable.stopHating(caster);
		targetAttackable.setWalking();
		targetAttackable.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
	}

	@SkillScript
	public static void main()
	{
		EffectHandler.getInstance().registerHandler("i_delete_hate_of_me", InstantDeleteHateOfMe::new);
	}
}
