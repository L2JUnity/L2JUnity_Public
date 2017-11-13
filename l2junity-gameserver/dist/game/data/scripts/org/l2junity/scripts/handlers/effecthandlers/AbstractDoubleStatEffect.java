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
package org.l2junity.scripts.handlers.effecthandlers;

import java.util.List;

import org.l2junity.gameserver.enums.StatModifierType;
import org.l2junity.gameserver.model.StatsSet;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.effects.AbstractEffect;
import org.l2junity.gameserver.model.itemcontainer.Inventory;
import org.l2junity.gameserver.model.items.L2Item;
import org.l2junity.gameserver.model.items.instance.ItemInstance;
import org.l2junity.gameserver.model.items.type.ArmorType;
import org.l2junity.gameserver.model.items.type.WeaponType;
import org.l2junity.gameserver.model.skills.Skill;
import org.l2junity.gameserver.model.stats.DoubleStat;

/**
 * @author Sdw
 */
public abstract class AbstractDoubleStatEffect extends AbstractEffect
{
	protected final DoubleStat _addStat;
	protected final DoubleStat _mulStat;
	protected final double _amount;
	protected final StatModifierType _mode;
	protected final int _weaponTypesMask;
	protected final List<ArmorType> _armorTypes;
	
	public AbstractDoubleStatEffect(StatsSet params, DoubleStat stat)
	{
		this(params, stat, stat);
	}
	
	public AbstractDoubleStatEffect(StatsSet params, DoubleStat mulStat, DoubleStat addStat)
	{
		_addStat = addStat;
		_mulStat = mulStat;
		_amount = params.getDouble("amount", 0);
		_mode = params.getEnum("mode", StatModifierType.class, StatModifierType.DIFF);
		
		int weaponTypesMask = 0;
		final List<String> weaponTypes = params.getList("weaponType", String.class);
		if (weaponTypes != null)
		{
			for (String weaponType : weaponTypes)
			{
				try
				{
					weaponTypesMask |= WeaponType.valueOf(weaponType).mask();
				}
				catch (IllegalArgumentException e)
				{
					final IllegalArgumentException exception = new IllegalArgumentException("weaponType should contain WeaponType enum value but found " + weaponType);
					exception.addSuppressed(e);
					throw exception;
				}
			}
		}
		
		_armorTypes = params.getEnumList("armorType", ArmorType.class);
		
		_weaponTypesMask = weaponTypesMask;
	}
	
	@Override
	public void pump(Creature target, Skill skill)
	{
		switch (_mode)
		{
			case DIFF:
			{
				target.getStat().mergeAdd(_addStat, _amount);
				break;
			}
			case PER:
			{
				target.getStat().mergeMul(_mulStat, (_amount / 100) + 1);
				break;
			}
		}
	}
	
	@Override
	public boolean checkPumpCondition(Creature caster, Creature target, Skill skill)
	{
		if (caster.isPlayer())
		{
			final Inventory inv = caster.getInventory();
			
			if (_weaponTypesMask != 0)
			{
				return (_weaponTypesMask & inv.getWearedMask()) != 0;
			}
			
			if (_armorTypes != null)
			{
				for (ArmorType type : _armorTypes)
				{
					switch (type)
					{
						case LIGHT:
						case HEAVY:
						case MAGIC:
						{
							final ItemInstance chest = inv.getPaperdollItem(Inventory.PAPERDOLL_CHEST);
							if (chest == null)
							{
								continue;
							}
							
							boolean chestOk = (type.mask() & chest.getItem().getItemMask()) != 0;
							
							if (chestOk && (chest.getItem().getBodyPart() == L2Item.SLOT_FULL_ARMOR))
							{
								return true;
							}
							
							final ItemInstance legs = inv.getPaperdollItem(Inventory.PAPERDOLL_LEGS);
							if (legs == null)
							{
								continue;
							}
							
							if (chestOk && ((type.mask() & legs.getItem().getItemMask()) != 0))
							{
								return true;
							}
						}
						case SIGIL:
						{
							final ItemInstance item = target.getSecondaryWeaponInstance();
							return (item != null) && (item.getItemType() == ArmorType.SIGIL);
						}
						case SHIELD:
						{
							final ItemInstance item = target.getSecondaryWeaponInstance();
							return (item != null) && (item.getItemType() == ArmorType.SHIELD);
						}
					}
				}
				return false;
			}
		}
		return true;
	}
}
