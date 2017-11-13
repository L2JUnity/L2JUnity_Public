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

import org.l2junity.gameserver.enums.StatModifierType;
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
 * CP change effect. It is mostly used for potions and static damage.
 * @author Nik
 */
public final class InstantCp extends AbstractEffect
{
	private final int _amount;
	private final StatModifierType _mode;
	
	public InstantCp(StatsSet params)
	{
		_amount = params.getInt("amount");
		_mode = params.getEnum("mode", StatModifierType.class, StatModifierType.DIFF);
	}
	
	@Override
	public void instant(Creature caster, WorldObject target, Skill skill, ItemInstance item)
	{
		final Creature targetCreature = target.asCreature();
		if (targetCreature == null)
		{
			return;
		}
		
		if (targetCreature.isDead() || targetCreature.isDoor() || targetCreature.isHpBlocked())
		{
			return;
		}
		
		double amount = 0;
		switch (_mode)
		{
			case DIFF:
			{
				amount = Math.min(_amount, targetCreature.getMaxRecoverableCp() - targetCreature.getCurrentCp());
				break;
			}
			case PER:
			{
				amount = Math.min((targetCreature.getCurrentCp() * _amount) / 100.0, targetCreature.getMaxRecoverableCp() - targetCreature.getCurrentCp());
				break;
			}
		}
		
		if (amount != 0)
		{
			final double newCp = amount + targetCreature.getCurrentCp();
			targetCreature.setCurrentCp(newCp, false);
			targetCreature.broadcastStatusUpdate(caster);
		}
		
		if (amount >= 0)
		{
			if ((caster != null) && (caster != targetCreature))
			{
				final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S2_CP_HAS_BEEN_RESTORED_BY_C1);
				sm.addCharName(caster);
				sm.addInt((int) amount);
				targetCreature.sendPacket(sm);
			}
			else
			{
				final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_CP_HAS_BEEN_RESTORED);
				sm.addInt((int) amount);
				targetCreature.sendPacket(sm);
			}
		}
	}

	@SkillScript
	public static void main()
	{
		EffectHandler.getInstance().registerHandler("i_cp", InstantCp::new);
	}
}
