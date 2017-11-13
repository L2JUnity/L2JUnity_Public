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

import org.l2junity.gameserver.ai.CtrlEvent;
import org.l2junity.gameserver.handler.EffectHandler;
import org.l2junity.gameserver.model.StatsSet;
import org.l2junity.gameserver.model.WorldObject;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.actor.instance.L2MonsterInstance;
import org.l2junity.gameserver.model.effects.AbstractEffect;
import org.l2junity.gameserver.model.items.instance.ItemInstance;
import org.l2junity.gameserver.model.skills.Skill;
import org.l2junity.gameserver.model.stats.Formulas;
import org.l2junity.gameserver.network.client.send.string.SystemMessageId;
import org.l2junity.gameserver.scripting.annotations.SkillScript;

/**
 * Spoil effect implementation.
 * @author _drunk_, Ahmed, Zoey76
 */
public final class InstantSpoil extends AbstractEffect
{
	public InstantSpoil(StatsSet params)
	{
	}
	
	@Override
	public boolean calcSuccess(Creature caster, WorldObject target, Skill skill)
	{
		return target.isMonster() && Formulas.calcMagicSuccess(caster, target.asMonster(), skill);
	}
	
	@Override
	public void instant(Creature caster, WorldObject target, Skill skill, ItemInstance item)
	{
		final L2MonsterInstance targetMonster = target.asMonster();
		if (targetMonster == null)
		{
			return;
		}

		if (targetMonster.isDead())
		{
			caster.sendPacket(SystemMessageId.INVALID_TARGET);
			return;
		}

		if (targetMonster.isSpoiled())
		{
			caster.sendPacket(SystemMessageId.IT_HAS_ALREADY_BEEN_SPOILED);
			return;
		}
		
		targetMonster.setSpoilerObjectId(caster.getObjectId());
		caster.sendPacket(SystemMessageId.THE_SPOIL_CONDITION_HAS_BEEN_ACTIVATED);
		targetMonster.getAI().notifyEvent(CtrlEvent.EVT_ATTACKED, caster);
	}

	@SkillScript
	public static void main()
	{
		EffectHandler.getInstance().registerHandler("i_spoil", InstantSpoil::new);
	}
}
