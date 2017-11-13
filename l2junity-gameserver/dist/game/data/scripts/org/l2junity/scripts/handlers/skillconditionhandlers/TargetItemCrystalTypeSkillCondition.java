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
package org.l2junity.scripts.handlers.skillconditionhandlers;

import java.util.List;

import org.l2junity.gameserver.enums.CrystallizationType;
import org.l2junity.gameserver.enums.ItemGrade;
import org.l2junity.gameserver.handler.SkillConditionHandler;
import org.l2junity.gameserver.model.StatsSet;
import org.l2junity.gameserver.model.WorldObject;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.items.instance.ItemInstance;
import org.l2junity.gameserver.model.skills.ISkillCondition;
import org.l2junity.gameserver.model.skills.Skill;
import org.l2junity.gameserver.scripting.annotations.SkillScript;

/**
 * @author Sdw
 */
public class TargetItemCrystalTypeSkillCondition implements ISkillCondition
{
	private final List<CrystallizationType> _itemType;
	private final ItemGrade _crystalType;
	private final int _scrollGroup;
	
	public TargetItemCrystalTypeSkillCondition(StatsSet params)
	{
		_itemType = params.getEnumList("itemType", CrystallizationType.class);
		_crystalType = params.getEnum("crystalType", ItemGrade.class);
		_scrollGroup = params.getInt("scrollGroup", 1);
	}
	
	@Override
	public boolean canUse(Creature caster, Skill skill, WorldObject target)
	{
		if ((target != null) && target.isItem())
		{
			final ItemInstance item = (ItemInstance) target;
			return item.getItem().getItemGrade().equals(_crystalType) && _itemType.contains(CrystallizationType.getByItem(item.getItem())) && (item.getItem().getEnchantGroup() == _scrollGroup);
		}
		
		return false;
	}

	@SkillScript
	public static void main()
	{
		SkillConditionHandler.getInstance().registerHandler("TargetItemCrystalType", TargetItemCrystalTypeSkillCondition::new);
	}
}
