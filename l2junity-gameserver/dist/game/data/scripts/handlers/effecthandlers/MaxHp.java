/*
 * Copyright (C) 2004-2015 L2J Unity
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
package handlers.effecthandlers;

import org.l2junity.gameserver.model.StatsSet;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.items.instance.ItemInstance;
import org.l2junity.gameserver.model.skills.Skill;
import org.l2junity.gameserver.model.stats.Stats;

/**
 * @author NosBit
 */
public class MaxHp extends AbstractStatEffect
{
	private final boolean _heal;
	
	public MaxHp(StatsSet params)
	{
		super(params, Stats.MAX_HP);
		
		_heal = params.getBoolean("heal", false);
	}
	
	@Override
	public void continuousInstant(Creature effector, Creature effected, Skill skill, ItemInstance item)
	{
		if (_heal && !effected.isHpBlocked())
		{
			switch (_mode)
			{
				case DIFF:
				{
					effected.setCurrentHp(effected.getCurrentHp() + _amount);
					break;
				}
				case PER:
				{
					effected.setCurrentHp(effected.getCurrentHp() + (effected.getMaxHp() * (_amount / 100)));
					break;
				}
			}
		}
	}
}
