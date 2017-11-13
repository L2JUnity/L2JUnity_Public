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
package org.l2junity.scripts.handlers.effecthandlers.pump;

import org.l2junity.gameserver.ai.CtrlIntention;
import org.l2junity.gameserver.handler.EffectHandler;
import org.l2junity.gameserver.model.StatsSet;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.actor.Summon;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.skills.Skill;
import org.l2junity.gameserver.model.stats.BooleanStat;
import org.l2junity.gameserver.scripting.annotations.SkillScript;
import org.l2junity.scripts.handlers.effecthandlers.AbstractBooleanStatEffect;

/**
 * Betray effect implementation.
 * @author decad
 */
public final class PumpBetray extends AbstractBooleanStatEffect
{
	public PumpBetray(StatsSet params)
	{
		super(BooleanStat.BETRAYED);
	}
	
	@Override
	public void pumpStart(Creature caster, Creature target, Skill skill)
	{
		final Summon summon = target.asSummon();
		if (summon != null)
		{
			final PlayerInstance owner = summon.getOwner();
			if (owner != null)
			{
				target.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, target.asSummon().getOwner());
			}
		}
	}
	
	@Override
	public void pumpEnd(Creature caster, Creature target, Skill skill)
	{
		target.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
	}
	
	@SkillScript
	public static void main()
	{
		EffectHandler.getInstance().registerHandler("p_betray", PumpBetray::new);
	}
}
