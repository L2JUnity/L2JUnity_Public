/*
 * Copyright (C) 2004-2016 L2J Unity
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
package org.l2junity.scripts.handlers.effecthandlers;

import org.l2junity.gameserver.model.StatsSet;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.skills.Skill;
import org.l2junity.gameserver.model.stats.DoubleStat;

/**
 * @author UnAfraid
 */
public abstract class AbstractDoubleStatConditionalHpEffect extends AbstractDoubleStatEffect
{
	private final int _hpPercent;
	
	protected AbstractDoubleStatConditionalHpEffect(StatsSet params, DoubleStat stat, int hpPercent)
	{
		super(params, stat);
		_hpPercent = hpPercent;
	}
	
	@Override
	public boolean checkPumpCondition(Creature caster, Creature target, Skill skill)
	{
		return ((_hpPercent <= 0) || (target.getCurrentHpPercent() <= _hpPercent));
	}
}