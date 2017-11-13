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
package org.l2junity.scripts.handlers.effecthandlers.consume;

import org.l2junity.gameserver.handler.EffectHandler;
import org.l2junity.gameserver.model.StatsSet;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.effects.AbstractEffect;
import org.l2junity.gameserver.model.skills.Skill;
import org.l2junity.gameserver.network.client.send.string.SystemMessageId;
import org.l2junity.gameserver.scripting.annotations.SkillScript;

/**
 * Mp Consume Per Level effect implementation.
 */
public final class ConsumeMp extends AbstractEffect
{
	private final double _power;
	
	public ConsumeMp(StatsSet params)
	{
		_power = params.getDouble("power", 0);
		setTicks(params.getInt("ticks"));
	}
	
	@Override
	public boolean consume(Creature target, Skill skill)
	{
		if (target.isDead())
		{
			return false;
		}
		
		final double consume = _power * getTicksMultiplier();
		double mp = target.getCurrentMp();
		final double maxMp = target.getMaxRecoverableMp();
		if ((consume > 0) && (mp > maxMp))
		{
			return false;
		}
		
		if ((consume < 0) && ((mp + consume) <= 0))
		{
			target.sendPacket(SystemMessageId.YOUR_SKILL_WAS_DEACTIVATED_DUE_TO_LACK_OF_MP);
			return false;
		}
		
		target.setCurrentMp(Math.min(target.getCurrentMp() + consume, maxMp));
		return true;
	}

	@SkillScript
	public static void main()
	{
		EffectHandler.getInstance().registerHandler("c_mp", ConsumeMp::new);
	}
}
