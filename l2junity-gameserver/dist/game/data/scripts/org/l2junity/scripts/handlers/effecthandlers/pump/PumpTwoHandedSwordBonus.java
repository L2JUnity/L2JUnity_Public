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

import org.l2junity.gameserver.enums.StatModifierType;
import org.l2junity.gameserver.handler.EffectHandler;
import org.l2junity.gameserver.model.StatsSet;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.effects.AbstractEffect;
import org.l2junity.gameserver.model.itemcontainer.Inventory;
import org.l2junity.gameserver.model.items.L2Item;
import org.l2junity.gameserver.model.items.type.WeaponType;
import org.l2junity.gameserver.model.skills.Skill;
import org.l2junity.gameserver.model.stats.DoubleStat;
import org.l2junity.gameserver.scripting.annotations.SkillScript;

/**
 * @author Sdw
 */
public class PumpTwoHandedSwordBonus extends AbstractEffect
{
	protected final double _pAtkAmount;
	protected final StatModifierType _pAtkmode;
	
	protected final double _accuracyAmount;
	protected final StatModifierType _accuracyMode;
	
	public PumpTwoHandedSwordBonus(StatsSet params)
	{
		_pAtkAmount = params.getDouble("pAtkAmount");
		_pAtkmode = params.getEnum("pAtkmode", StatModifierType.class, StatModifierType.DIFF);
		
		_accuracyAmount = params.getDouble("accuracyAmount");
		_accuracyMode = params.getEnum("accuracyMode", StatModifierType.class, StatModifierType.DIFF);
	}
	
	@Override
	public boolean checkPumpCondition(Creature caster, Creature target, Skill skill)
	{
		if (caster.isPlayer())
		{
			final Inventory inv = caster.getInventory();
			
			return ((WeaponType.SWORD.mask() & inv.getWearedMask()) != 0) && ((caster.getActiveWeaponItem().getBodyPart() & L2Item.SLOT_LR_HAND) != 0);
		}
		return true;
	}
	
	@Override
	public void pump(Creature target, Skill skill)
	{
		switch (_pAtkmode)
		{
			case DIFF:
			{
				target.getStat().mergeAdd(DoubleStat.PHYSICAL_ATTACK, _pAtkAmount);
				break;
			}
			case PER:
			{
				target.getStat().mergeMul(DoubleStat.PHYSICAL_ATTACK, (_pAtkAmount / 100) + 1);
				break;
			}
		}
		
		switch (_accuracyMode)
		{
			case DIFF:
			{
				target.getStat().mergeAdd(DoubleStat.ACCURACY_COMBAT, _accuracyAmount);
				break;
			}
			case PER:
			{
				target.getStat().mergeMul(DoubleStat.ACCURACY_COMBAT, (_accuracyAmount / 100) + 1);
				break;
			}
		}
	}
	
	@SkillScript
	public static void main()
	{
		EffectHandler.getInstance().registerHandler("p_2h_sword_bonus", PumpTwoHandedSwordBonus::new);
	}
}
