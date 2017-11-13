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
package org.l2junity.scripts.handlers.effecthandlers.pump;

import org.l2junity.gameserver.handler.EffectHandler;
import org.l2junity.gameserver.model.StatsSet;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.effects.AbstractEffect;
import org.l2junity.gameserver.model.items.instance.ItemInstance;
import org.l2junity.gameserver.model.skills.Skill;
import org.l2junity.gameserver.model.stats.DoubleStat;
import org.l2junity.gameserver.scripting.annotations.SkillScript;

/**
 * @author Sdw
 */
public class PumpAttackAttributeAdd extends AbstractEffect
{
	private final double _amount;
	
	public PumpAttackAttributeAdd(StatsSet params)
	{
		_amount = params.getDouble("amount");
	}
	
	@Override
	public void pump(Creature target, Skill skill)
	{
		final ItemInstance weapon = target.getActiveWeaponInstance();
		if (weapon != null)
		{
			switch (weapon.getAttackAttributeType())
			{
				case FIRE:
				{
					target.getStat().mergeAdd(DoubleStat.FIRE_POWER, _amount);
					break;
				}
				case WATER:
				{
					target.getStat().mergeAdd(DoubleStat.WATER_POWER, _amount);
					break;
				}
				case WIND:
				{
					target.getStat().mergeAdd(DoubleStat.WIND_POWER, _amount);
					break;
				}
				case EARTH:
				{
					target.getStat().mergeAdd(DoubleStat.EARTH_POWER, _amount);
					break;
				}
				case HOLY:
				{
					target.getStat().mergeAdd(DoubleStat.HOLY_POWER, _amount);
					break;
				}
				case DARK:
				{
					target.getStat().mergeAdd(DoubleStat.DARK_POWER, _amount);
					break;
				}
			}
		}
	}
	
	@SkillScript
	public static void main()
	{
		EffectHandler.getInstance().registerHandler("p_attack_attribute_add", PumpAttackAttributeAdd::new);
	}
}
