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
import org.l2junity.gameserver.model.items.instance.ItemInstance;
import org.l2junity.gameserver.model.skills.Skill;
import org.l2junity.gameserver.network.client.send.SystemMessage;
import org.l2junity.gameserver.network.client.send.string.SystemMessageId;
import org.l2junity.gameserver.scripting.annotations.SkillScript;

/**
 * Hp By Level Self effect implementation.
 * @author Sdw
 */
public final class InstantHpByLevelSelf extends AbstractEffect
{
	private final double _power;
	
	public InstantHpByLevelSelf(StatsSet params)
	{
		_power = params.getDouble("power");
	}
	
	@Override
	public void instant(Creature caster, WorldObject target, Skill skill, ItemInstance item)
	{
		if (caster.isDead() || caster.isHpBlocked())
		{
			return;
		}
		
		double power = 0;
		final int levelDiff = caster.getLevel() - skill.getMagicLevel();
		if (levelDiff <= 9)
		{
			power = _power * ((10 * (10 - CommonUtil.constrain(levelDiff, 0, 9))) / 100);
		}
		
		final double healedAmount = CommonUtil.constrain(power, 0, caster.getMaxRecoverableHp() - caster.getCurrentHp());
		caster.setCurrentHp(caster.getCurrentHp() + healedAmount);
		
		// System message
		final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_HP_HAS_BEEN_RESTORED);
		sm.addInt((int) healedAmount);
		caster.sendPacket(sm);
	}

	@SkillScript
	public static void main()
	{
		EffectHandler.getInstance().registerHandler("i_hp_by_level", InstantHpByLevelSelf::new);
	}
}
