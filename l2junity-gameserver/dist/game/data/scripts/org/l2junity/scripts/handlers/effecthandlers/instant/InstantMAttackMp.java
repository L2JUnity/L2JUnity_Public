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
import org.l2junity.gameserver.model.stats.BooleanStat;
import org.l2junity.gameserver.model.stats.Formulas;
import org.l2junity.gameserver.network.client.send.SystemMessage;
import org.l2junity.gameserver.network.client.send.string.SystemMessageId;
import org.l2junity.gameserver.scripting.annotations.SkillScript;

/**
 * Magical Attack MP effect.
 * @author Adry_85
 */
public final class InstantMAttackMp extends AbstractEffect
{
	private final double _power;
	private final boolean _critical;
	private final double _criticalLimit;
	
	public InstantMAttackMp(StatsSet params)
	{
		_power = params.getDouble("power");
		_critical = params.getBoolean("critical");
		_criticalLimit = params.getDouble("criticalLimit");
	}
	
	@Override
	public boolean calcSuccess(Creature caster, WorldObject target, Skill skill)
	{
		final Creature targetCreature = target.asCreature();
		if (targetCreature == null)
		{
			return false;
		}

		if (targetCreature.isMpBlocked())
		{
			return false;
		}

		if (caster.isPlayer() && targetCreature.isPlayer() && targetCreature.getStat().has(BooleanStat.FACE_OFF) && (targetCreature.asPlayer().getAttackerObjId() != caster.getObjectId()))
		{
			return false;
		}
		
		if (!Formulas.calcMagicAffected(caster, targetCreature, skill))
		{
			if (caster.isPlayer())
			{
				caster.sendPacket(SystemMessageId.YOUR_ATTACK_HAS_FAILED);
			}
			if (targetCreature.isPlayer())
			{
				SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_RESISTED_C2_S_DRAIN);
				sm.addCharName(targetCreature);
				sm.addCharName(caster);
				targetCreature.sendPacket(sm);
			}
			return false;
		}
		return true;
	}
	
	@Override
	public L2EffectType getEffectType()
	{
		return L2EffectType.MAGICAL_ATTACK;
	}
	
	@Override
	public void instant(Creature caster, WorldObject target, Skill skill, ItemInstance item)
	{
		final Creature targetCreature = target.asCreature();
		if (targetCreature == null)
		{
			return;
		}

		if (!targetCreature.isCreature() || caster.isAlikeDead())
		{
			return;
		}

		boolean sps = skill.useSpiritShot() && caster.isChargedShot(ShotType.SPIRITSHOTS);
		boolean bss = skill.useSpiritShot() && caster.isChargedShot(ShotType.BLESSED_SPIRITSHOTS);
		final byte shld = Formulas.calcShldUse(caster, targetCreature);
		final boolean mcrit = _critical ? Formulas.calcCrit(skill.getMagicCriticalRate(), caster, targetCreature, skill) : false;
		double damage = Formulas.calcManaDam(caster, targetCreature, skill, _power, shld, sps, bss, mcrit, _criticalLimit);
		double mp = Math.min(targetCreature.getCurrentMp(), damage);
		
		if (damage > 0)
		{
			targetCreature.stopEffectsOnDamage();
			targetCreature.setCurrentMp(targetCreature.getCurrentMp() - mp);
		}
		
		if (targetCreature.isPlayer())
		{
			final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S2_S_MP_HAS_BEEN_DRAINED_BY_C1);
			sm.addCharName(caster);
			sm.addInt((int) mp);
			targetCreature.sendPacket(sm);
		}
		
		if (caster.isPlayer())
		{
			SystemMessage sm2 = SystemMessage.getSystemMessage(SystemMessageId.YOUR_OPPONENT_S_MP_WAS_REDUCED_BY_S1);
			sm2.addInt((int) mp);
			caster.sendPacket(sm2);
		}
	}

	@SkillScript
	public static void main()
	{
		EffectHandler.getInstance().registerHandler("i_m_attack_mp", InstantMAttackMp::new);
	}
}
