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
import org.l2junity.gameserver.model.stats.Formulas;
import org.l2junity.gameserver.scripting.annotations.SkillScript;

/**
 * HP Drain effect implementation.
 * @author Adry_85
 */
public final class InstantHpDrain extends AbstractEffect
{
	private final double _power;
	private final double _percentage;
	
	public InstantHpDrain(StatsSet params)
	{
		_power = params.getDouble("power");
		_percentage = params.getDouble("percentage");
	}
	
	@Override
	public L2EffectType getEffectType()
	{
		return L2EffectType.HP_DRAIN;
	}
	
	@Override
	public void instant(Creature caster, WorldObject target, Skill skill, ItemInstance item)
	{
		final Creature targetCreature = target.asCreature();
		if (targetCreature == null)
		{
			return;
		}
		
		// TODO: Unhardcode Cubic Skill to avoid double damage
		if (caster.isAlikeDead() || (skill.getId() == 4050))
		{
			return;
		}
		
		boolean sps = skill.useSpiritShot() && caster.isChargedShot(ShotType.SPIRITSHOTS);
		boolean bss = skill.useSpiritShot() && caster.isChargedShot(ShotType.BLESSED_SPIRITSHOTS);
		boolean mcrit = Formulas.calcCrit(skill.getMagicCriticalRate(), caster, targetCreature, skill);
		double damage = Formulas.calcMagicDam(caster, targetCreature, skill, caster.getMAtk(), _power, targetCreature.getMDef(), sps, bss, mcrit);
		
		double drain = 0;
		int cp = (int) targetCreature.getCurrentCp();
		int hp = (int) targetCreature.getCurrentHp();
		
		if (cp > 0)
		{
			drain = (damage < cp) ? 0 : (damage - cp);
		}
		else if (damage > hp)
		{
			drain = hp;
		}
		else
		{
			drain = damage;
		}
		
		final double hpAdd = ((_percentage / 100) * drain);
		final double hpFinal = ((caster.getCurrentHp() + hpAdd) > caster.getMaxHp() ? caster.getMaxHp() : (caster.getCurrentHp() + hpAdd));
		caster.setCurrentHp(hpFinal);
		
		caster.doAttack(damage, targetCreature, skill, false, false, mcrit, false);
	}

	@SkillScript
	public static void main()
	{
		EffectHandler.getInstance().registerHandler("i_hp_drain", InstantHpDrain::new);
	}
}