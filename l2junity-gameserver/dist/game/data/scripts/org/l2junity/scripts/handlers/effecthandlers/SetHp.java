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
package org.l2junity.scripts.handlers.effecthandlers;

import org.l2junity.gameserver.enums.StatModifierType;
import org.l2junity.gameserver.handler.EffectHandler;
import org.l2junity.gameserver.model.StatsSet;
import org.l2junity.gameserver.model.WorldObject;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.effects.AbstractEffect;
import org.l2junity.gameserver.model.items.instance.ItemInstance;
import org.l2junity.gameserver.model.skills.Skill;
import org.l2junity.gameserver.scripting.annotations.SkillScript;

/**
 * An effect that sets the current hp to the given amount.
 * @author Nik
 */
public final class SetHp extends AbstractEffect
{
	private final double _amount;
	private final StatModifierType _mode;
	
	public SetHp(StatsSet params)
	{
		_amount = params.getDouble("amount", 0);
		_mode = params.getEnum("mode", StatModifierType.class, StatModifierType.DIFF);
	}
	
	@Override
	public void instant(Creature caster, WorldObject target, Skill skill, ItemInstance item)
	{
		final Creature targetCreature = target.asPlayer();
		if (targetCreature == null)
		{
			return;
		}
		
		if (targetCreature.isDoor() || targetCreature.isDead())
		{
			return;
		}
		
		boolean full = (_mode == StatModifierType.PER) && (_amount == 100.0);
		double amount = full ? targetCreature.getMaxHp() : (_mode == StatModifierType.PER) ? ((targetCreature.getMaxHp() * _amount) / 100.0) : _amount;
		targetCreature.setCurrentHp(amount);
	}
	
	@SkillScript
	public static void main()
	{
		EffectHandler.getInstance().registerHandler("SetHp", SetHp::new);
	}
}
