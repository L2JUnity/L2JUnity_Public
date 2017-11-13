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

import org.l2junity.gameserver.handler.EffectHandler;
import org.l2junity.gameserver.model.StatsSet;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.effects.AbstractEffect;
import org.l2junity.gameserver.model.skills.Skill;
import org.l2junity.gameserver.model.stats.BooleanStat;
import org.l2junity.gameserver.scripting.annotations.SkillScript;

/**
 * Effect that blocks damage and heals to HP/MP. <BR>
 * Regeneration or DOT shouldn't be blocked, Vampiric Rage and Balance Life as well.
 * @author Nik
 */
public final class PumpBlockGetdamage extends AbstractEffect
{
	private final boolean _blockHp;
	private final boolean _blockMp;
	
	public PumpBlockGetdamage(StatsSet params)
	{
		final String type = params.getString("type");
		_blockHp = type.equalsIgnoreCase("BLOCK_HP");
		_blockMp = type.equalsIgnoreCase("BLOCK_MP");
	}
	
	@Override
	public void pump(Creature target, Skill skill)
	{
		if (_blockHp)
		{
			target.getStat().set(BooleanStat.HP_BLOCKED);
		}
		else if (_blockMp)
		{
			target.getStat().set(BooleanStat.MP_BLOCKED);
		}
	}
	
	@SkillScript
	public static void main()
	{
		EffectHandler.getInstance().registerHandler("p_block_getdamage", PumpBlockGetdamage::new);
	}
}
