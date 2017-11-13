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

import org.l2junity.gameserver.enums.ShotType;
import org.l2junity.gameserver.handler.EffectHandler;
import org.l2junity.gameserver.model.StatsSet;
import org.l2junity.gameserver.model.WorldObject;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.effects.AbstractEffect;
import org.l2junity.gameserver.model.effects.L2EffectType;
import org.l2junity.gameserver.model.items.instance.ItemInstance;
import org.l2junity.gameserver.model.skills.Skill;
import org.l2junity.gameserver.model.stats.BaseStats;
import org.l2junity.gameserver.model.stats.BooleanStat;
import org.l2junity.gameserver.model.stats.DoubleStat;
import org.l2junity.gameserver.model.stats.Formulas;
import org.l2junity.gameserver.network.client.send.ExMagicAttackInfo;
import org.l2junity.gameserver.network.client.send.SystemMessage;
import org.l2junity.gameserver.network.client.send.string.SystemMessageId;
import org.l2junity.gameserver.scripting.annotations.SkillScript;

/**
 * HpCpHeal effect implementation.
 * @author Sdw
 */
public final class InstantHealSpecial extends AbstractEffect
{
	private final double _power;
	
	public InstantHealSpecial(StatsSet params)
	{
		_power = params.getDouble("power");
	}
	
	@Override
	public L2EffectType getEffectType()
	{
		return L2EffectType.HEAL;
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
		
		if (!caster.equals(targetCreature) && targetCreature.getStat().has(BooleanStat.FACE_OFF))
		{
			return;
		}
		
		// TODO: Shots bonus needs more propper formula.
		final double shotsBonus = skill.isMagic() ? caster.getStat().getValue(DoubleStat.SPIRITSHOTS_BONUS) * (caster.isChargedShot(ShotType.BLESSED_SPIRITSHOTS) ? 36 : (caster.isChargedShot(ShotType.SPIRITSHOTS) ? 18 : 1)) : 1;
		final double mAtkBonus = Math.sqrt(caster.getMAtk() * shotsBonus);
		final double baseAmount = (_power + mAtkBonus + caster.getStat().getAdd(DoubleStat.HEAL_EFFECT)) * targetCreature.getStat().getMul(DoubleStat.HEAL_EFFECT);
		final double levelMod = (((caster.getLevel() + 89) + (5.5 * Math.max(caster.getLevel() - 99, 0))) / 100);
		final double weaponBonus = Math.min(levelMod * DoubleStat.weaponBaseValue(caster, DoubleStat.MAGIC_ATTACK) * BaseStats.MEN.calcBonus(caster), baseAmount);
		double amount = baseAmount + weaponBonus;
		
		// Heal critic, since CT2.3 Gracia Final
		if (skill.isMagic() && Formulas.calcCrit(skill.getMagicCriticalRate(), caster, targetCreature, skill))
		{
			amount *= 3;
			caster.sendPacket(SystemMessageId.M_CRITICAL);
			caster.sendPacket(new ExMagicAttackInfo(caster.getObjectId(), targetCreature.getObjectId(), ExMagicAttackInfo.CRITICAL_HEAL));
			if (targetCreature.isPlayer() && (targetCreature != caster))
			{
				targetCreature.sendPacket(new ExMagicAttackInfo(caster.getObjectId(), targetCreature.getObjectId(), ExMagicAttackInfo.CRITICAL_HEAL));
			}
		}
		
		// Prevents overheal
		final double hpAmount = Math.min(amount, targetCreature.getMaxRecoverableHp() - targetCreature.getCurrentHp());
		if (hpAmount != 0)
		{
			final double newHp = hpAmount + targetCreature.getCurrentHp();
			targetCreature.setCurrentHp(newHp, false);
			targetCreature.broadcastStatusUpdate(caster);
		}
		
		if (targetCreature.isPlayer())
		{
			if (caster.isPlayer() && (caster != targetCreature))
			{
				SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S2_HP_HAS_BEEN_RESTORED_BY_C1);
				sm.addString(caster.getName());
				sm.addInt((int) hpAmount);
				targetCreature.sendPacket(sm);
			}
			else
			{
				SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_HP_HAS_BEEN_RESTORED);
				sm.addInt((int) hpAmount);
				targetCreature.sendPacket(sm);
			}
			
			final double cpAmount = Math.max(Math.min(amount - hpAmount, targetCreature.getMaxRecoverableCp() - targetCreature.getCurrentCp()), 0);
			if (cpAmount != 0)
			{
				final double newCp = cpAmount + targetCreature.getCurrentCp();
				targetCreature.setCurrentCp(newCp, false);
			}
			
			if ((cpAmount > 0) || (hpAmount == 0))
			{
				if (caster.isPlayer() && (caster != targetCreature))
				{
					SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S2_CP_HAS_BEEN_RESTORED_BY_C1);
					sm.addString(caster.getName());
					sm.addInt((int) cpAmount);
					targetCreature.sendPacket(sm);
				}
				else
				{
					SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_CP_HAS_BEEN_RESTORED);
					sm.addInt((int) cpAmount);
					targetCreature.sendPacket(sm);
				}
			}
		}
		targetCreature.broadcastStatusUpdate(caster);
	}

	@SkillScript
	public static void main()
	{
		EffectHandler.getInstance().registerHandler("i_heal_special", InstantHealSpecial::new);
	}
}
