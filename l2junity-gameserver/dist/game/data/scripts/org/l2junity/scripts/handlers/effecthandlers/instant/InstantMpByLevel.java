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

import org.l2junity.commons.util.CommonUtil;
import org.l2junity.gameserver.handler.EffectHandler;
import org.l2junity.gameserver.model.StatsSet;
import org.l2junity.gameserver.model.WorldObject;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.effects.AbstractEffect;
import org.l2junity.gameserver.model.effects.L2EffectType;
import org.l2junity.gameserver.model.items.instance.ItemInstance;
import org.l2junity.gameserver.model.skills.Skill;
import org.l2junity.gameserver.model.stats.DoubleStat;
import org.l2junity.gameserver.network.client.send.SystemMessage;
import org.l2junity.gameserver.network.client.send.string.SystemMessageId;
import org.l2junity.gameserver.scripting.annotations.SkillScript;

/**
 * Mana Heal By Level effect implementation.
 * @author UnAfraid
 */
public final class InstantMpByLevel extends AbstractEffect
{
	private final double _power;
	
	public InstantMpByLevel(StatsSet params)
	{
		_power = params.getDouble("power");
	}
	
	@Override
	public L2EffectType getEffectType()
	{
		return L2EffectType.MANAHEAL_BY_LEVEL;
	}
	
	@Override
	public void instant(Creature caster, WorldObject target, Skill skill, ItemInstance item)
	{
		final Creature targetCreature = target.asCreature();
		if (targetCreature == null)
		{
			return;
		}
		
		if (targetCreature.isDead() || targetCreature.isMpBlocked())
		{
			return;
		}
		
		double power = 0;
		final int levelDiff = targetCreature.getLevel() - skill.getMagicLevel() - 5;
		if (levelDiff <= 9)
		{
			power = _power * ((10 * (10 - CommonUtil.constrain(levelDiff, 0, 9))) / 100);
			power = targetCreature.getStat().getValue(DoubleStat.MANA_CHARGE, power);
		}
		
		final double healedAmount = CommonUtil.constrain(power, 0, targetCreature.getMaxRecoverableMp() - targetCreature.getCurrentMp());
		targetCreature.setCurrentMp(targetCreature.getCurrentMp() + healedAmount);
		
		final SystemMessage sm = SystemMessage.getSystemMessage(!caster.equals(targetCreature) ? SystemMessageId.S2_MP_HAS_BEEN_RESTORED_BY_C1 : SystemMessageId.S1_MP_HAS_BEEN_RESTORED);
		if (!caster.equals(targetCreature))
		{
			sm.addCharName(caster);
		}
		sm.addInt((int) healedAmount);
		targetCreature.sendPacket(sm);
	}

	@SkillScript
	public static void main()
	{
		EffectHandler.getInstance().registerHandler("i_mp_by_level", InstantMpByLevel::new);
	}
}
