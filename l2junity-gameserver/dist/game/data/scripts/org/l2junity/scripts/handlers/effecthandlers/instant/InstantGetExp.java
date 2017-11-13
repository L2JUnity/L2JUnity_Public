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

import org.l2junity.gameserver.handler.EffectHandler;
import org.l2junity.gameserver.model.StatsSet;
import org.l2junity.gameserver.model.WorldObject;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.effects.AbstractEffect;
import org.l2junity.gameserver.model.items.instance.ItemInstance;
import org.l2junity.gameserver.model.skills.Skill;
import org.l2junity.gameserver.scripting.annotations.SkillScript;

/**
 * Get EXP effect implementation.
 * @author Kazumi
 */
public final class InstantGetExp extends AbstractEffect
{
	private final long _exp;
	private final int _minLevel;
	private final int _underMinLevelExpPercent;
	
	public InstantGetExp(StatsSet params)
	{
		_exp = params.getLong("exp");
		_minLevel = params.getInt("minLevel");
		_underMinLevelExpPercent = params.getInt("underMinLevelExpPercent");
	}
	
	@Override
	public void instant(Creature caster, WorldObject target, Skill skill, ItemInstance item)
	{
		final PlayerInstance casterPlayer = caster.asPlayer();
		if (casterPlayer == null)
		{
			return;
		}
		
		if (casterPlayer.isAlikeDead())
		{
			return;
		}
		
		if (casterPlayer.getLevel() >= _minLevel)
		{
			casterPlayer.addExpAndSp(_exp, 0);
		}
		else
		{
			final long exp = (_exp * _underMinLevelExpPercent) / 100L;
			casterPlayer.addExpAndSp(exp, 0);
		}
	}

	@SkillScript
	public static void main()
	{
		EffectHandler.getInstance().registerHandler("i_get_exp", InstantGetExp::new);
	}
}