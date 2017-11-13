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
 * @author Sdw
 */
public class InstantDeleteTopAgro extends AbstractEffect
{
	private final int _chance;
	
	public InstantDeleteTopAgro(StatsSet params)
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
		
		targetAttackable.stopHating(targetAttackable.getMostHated());
		targetAttackable.setWalking();
		targetAttackable.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
	}

	@SkillScript
	public static void main()
	{
		EffectHandler.getInstance().registerHandler("i_delete_topaggro", InstantDeleteTopAgro::new);
	}
}
