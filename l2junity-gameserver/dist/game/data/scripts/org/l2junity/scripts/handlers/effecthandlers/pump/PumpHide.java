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
import org.l2junity.gameserver.model.World;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.effects.AbstractEffect;
import org.l2junity.gameserver.model.skills.Skill;
import org.l2junity.gameserver.scripting.annotations.SkillScript;

/**
 * Hide effect implementation.
 * @author ZaKaX, nBd
 */
public final class PumpHide extends AbstractEffect
{
	public PumpHide(StatsSet params)
	{
	}
	
	@Override
	public void pumpStart(Creature caster, Creature effected, Skill skill)
	{
		if (effected.isPlayer())
		{
			effected.setInvisible(true);
			
			if ((effected.getAI().getNextIntention() != null) && (effected.getAI().getNextIntention().getCtrlIntention() == CtrlIntention.AI_INTENTION_ATTACK))
			{
				effected.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
			}
			
			World.getInstance().forEachVisibleObject(effected, Creature.class, target ->
			{
				if ((target.getTarget() == effected))
				{
					target.setTarget(null);
					target.abortAttack();
					target.abortCast();
					target.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
				}
			});
		}
	}
	
	@Override
	public void pumpEnd(Creature caster, Creature target, Skill skill)
	{
		if (target.isPlayer())
		{
			PlayerInstance activeChar = target.getActingPlayer();
			if (!activeChar.inObserverMode())
			{
				activeChar.setInvisible(false);
			}
		}
	}
	
	@SkillScript
	public static void main()
	{
		EffectHandler.getInstance().registerHandler("p_hide", PumpHide::new);
	}
}