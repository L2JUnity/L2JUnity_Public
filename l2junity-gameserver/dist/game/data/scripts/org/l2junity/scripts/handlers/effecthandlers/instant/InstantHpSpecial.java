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
 * HP change effect. It is mostly used for potions and static damage.<br>
 * This effect is different from {@link InstantHp} effect because it affects CP as well.
 * @author Nik
 */
public final class InstantHpSpecial extends AbstractEffect
{
	private final int _amount;
	private final StatModifierType _mode;
	
	public InstantHpSpecial(StatsSet params)
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
		
		double hpAmount = 0;
		double cpAmount = 0;
		switch (_mode)
		{
			case DIFF:
			{
				hpAmount = Math.min(_amount, targetCreature.getMaxRecoverableHp() - targetCreature.getCurrentHp());
				if (_amount > hpAmount)
				{
					cpAmount = Math.min(_amount - hpAmount, targetCreature.getMaxRecoverableCp() - targetCreature.getCurrentCp());
				}
				break;
			}
			case PER:
			{
				hpAmount = Math.min(_amount, (targetCreature.getCurrentHp() * 100) / targetCreature.getMaxRecoverableHp());
				if (_amount > hpAmount)
				{
					cpAmount = Math.min(_amount, (targetCreature.getCurrentCp() * 100) / targetCreature.getMaxRecoverableCp());
					cpAmount = Math.min((targetCreature.getCurrentCp() * cpAmount) / 100.0, targetCreature.getMaxRecoverableCp() - targetCreature.getCurrentCp());
				}
				hpAmount = Math.min((targetCreature.getCurrentHp() * hpAmount) / 100.0, targetCreature.getMaxRecoverableHp() - targetCreature.getCurrentHp());
				break;
			}
		}
		
		if ((hpAmount >= 0) || (cpAmount >= 0))
		{
			SystemMessage hpSM = null;
			if (hpAmount != 0)
			{
				final double newHp = hpAmount + targetCreature.getCurrentHp();
				targetCreature.setCurrentHp(newHp, false);
				
				if (caster.getObjectId() != targetCreature.getObjectId())
				{
					hpSM = SystemMessage.getSystemMessage(SystemMessageId.S2_HP_HAS_BEEN_RESTORED_BY_C1);
					hpSM.addCharName(caster);
					hpSM.addInt((int) hpAmount);
				}
				else
				{
					hpSM = SystemMessage.getSystemMessage(SystemMessageId.S1_HP_HAS_BEEN_RESTORED);
					hpSM.addInt((int) hpAmount);
				}
			}
			
			SystemMessage cpSM = null;
			if (cpAmount != 0)
			{
				final double newCp = cpAmount + targetCreature.getCurrentCp();
				targetCreature.setCurrentCp(newCp, false);
				
				if (caster.getObjectId() != targetCreature.getObjectId())
				{
					cpSM = SystemMessage.getSystemMessage(SystemMessageId.S2_CP_HAS_BEEN_RESTORED_BY_C1);
					cpSM.addCharName(caster);
					cpSM.addInt((int) cpAmount);
				}
				else
				{
					cpSM = SystemMessage.getSystemMessage(SystemMessageId.S1_CP_HAS_BEEN_RESTORED);
					cpSM.addInt((int) cpAmount);
				}
			}
			
			if (hpSM != null)
			{
				targetCreature.sendPacket(hpSM);
			}
			if (cpSM != null)
			{
				targetCreature.sendPacket(cpSM);
			}
			
			targetCreature.broadcastStatusUpdate(caster);
		}
		else
		{
			final double damage = -hpAmount - cpAmount;
			targetCreature.reduceCurrentHp(damage, caster, skill, false, false, false, false);
			caster.sendDamageMessage(targetCreature, skill, (int) damage, false, false);
		}
	}

	@SkillScript
	public static void main()
	{
		EffectHandler.getInstance().registerHandler("i_hp_special", InstantHpSpecial::new);
	}
}
