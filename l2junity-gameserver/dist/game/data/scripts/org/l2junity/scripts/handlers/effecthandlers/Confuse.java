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
package org.l2junity.scripts.handlers.effecthandlers;

import java.util.List;

import org.l2junity.commons.util.Rnd;
import org.l2junity.gameserver.ai.CtrlEvent;
import org.l2junity.gameserver.ai.CtrlIntention;
import org.l2junity.gameserver.handler.EffectHandler;
import org.l2junity.gameserver.model.StatsSet;
import org.l2junity.gameserver.model.World;
import org.l2junity.gameserver.model.WorldObject;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.items.instance.ItemInstance;
import org.l2junity.gameserver.model.skills.Skill;
import org.l2junity.gameserver.model.stats.BooleanStat;
import org.l2junity.gameserver.model.stats.Formulas;
import org.l2junity.gameserver.scripting.annotations.SkillScript;

/**
 * Confuse effect implementation.
 * @author littlecrow
 */
public final class Confuse extends AbstractBooleanStatEffect
{
	private final int _chance;
	
	public Confuse(StatsSet params)
	{
		super(BooleanStat.CONFUSED);
		_chance = params.getInt("chance", 100);
	}
	
	@Override
	public boolean calcSuccess(Creature caster, WorldObject target, Skill skill)
	{
		return target.isCreature() && Formulas.calcProbability(_chance, caster, target.asCreature(), skill);
	}
	
	@Override
	public void instant(Creature caster, WorldObject target, Skill skill, ItemInstance item)
	{
		final Creature targetCreature = target.asCreature();
		if (targetCreature == null)
		{
			return;
		}
		
		targetCreature.getAI().notifyEvent(CtrlEvent.EVT_CONFUSED);
		
		// Getting the possible targets
		final List<Creature> targetList = World.getInstance().getVisibleObjects(targetCreature, Creature.class);
		// if there is no target, exit function
		if (!targetList.isEmpty())
		{
			// Choosing randomly a new target
			final Creature randomTarget = targetList.get(Rnd.nextInt(targetList.size()));
			// Attacking the target
			targetCreature.setTarget(randomTarget);
			targetCreature.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, randomTarget);
		}
	}
	
	@SkillScript
	public static void main()
	{
		EffectHandler.getInstance().registerHandler("Confuse", Confuse::new);
	}
}
