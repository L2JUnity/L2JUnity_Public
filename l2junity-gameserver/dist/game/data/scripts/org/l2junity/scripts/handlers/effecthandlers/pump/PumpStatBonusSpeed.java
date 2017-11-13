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

import java.util.List;

import org.l2junity.gameserver.handler.EffectHandler;
import org.l2junity.gameserver.model.StatsSet;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.effects.AbstractEffect;
import org.l2junity.gameserver.model.itemcontainer.Inventory;
import org.l2junity.gameserver.model.items.L2Item;
import org.l2junity.gameserver.model.items.instance.ItemInstance;
import org.l2junity.gameserver.model.items.type.ArmorType;
import org.l2junity.gameserver.model.skills.Skill;
import org.l2junity.gameserver.model.stats.BaseStats;
import org.l2junity.gameserver.model.stats.DoubleStat;
import org.l2junity.gameserver.scripting.annotations.SkillScript;

/**
 * @author Sdw
 */
public class PumpStatBonusSpeed extends AbstractEffect
{
	private final BaseStats _stat;
	protected final int _armorTypesMask;
	
	public PumpStatBonusSpeed(StatsSet params)
	{
		_stat = params.getEnum("stat", BaseStats.class);
		
		int armorTypesMask = 0;
		final List<String> armorTypes = params.getList("armorType", String.class);
		if (armorTypes != null)
		{
			for (String armorType : armorTypes)
			{
				try
				{
					armorTypesMask |= ArmorType.valueOf(armorType).mask();
				}
				catch (IllegalArgumentException e)
				{
					final IllegalArgumentException exception = new IllegalArgumentException("armorTypes should contain ArmorType enum value but found " + armorType);
					exception.addSuppressed(e);
					throw exception;
				}
			}
		}
		_armorTypesMask = armorTypesMask;
	}
	
	@Override
	public boolean checkPumpCondition(Creature caster, Creature target, Skill skill)
	{
		if (caster.isPlayer())
		{
			if (_armorTypesMask != 0)
			{
				final Inventory inv = caster.getInventory();
				final ItemInstance chest = inv.getPaperdollItem(Inventory.PAPERDOLL_CHEST);
				if (chest == null)
				{
					return false;
				}
				
				boolean chestOk = (_armorTypesMask & chest.getItem().getItemMask()) != 0;
				
				if (chest.getItem().getBodyPart() == L2Item.SLOT_FULL_ARMOR)
				{
					return chestOk;
				}
				
				final ItemInstance legs = inv.getPaperdollItem(Inventory.PAPERDOLL_LEGS);
				if (legs == null)
				{
					return false;
				}
				
				return chestOk && ((_armorTypesMask & legs.getItem().getItemMask()) != 0);
			}
		}
		return true;
	}
	
	@Override
	public void pump(Creature target, Skill skill)
	{
		target.getStat().mergeAdd(DoubleStat.STAT_BONUS_SPEED, _stat.ordinal() + 1);
	}
	
	@SkillScript
	public static void main()
	{
		EffectHandler.getInstance().registerHandler("p_statbonus_speed", PumpStatBonusSpeed::new);
	}
}
