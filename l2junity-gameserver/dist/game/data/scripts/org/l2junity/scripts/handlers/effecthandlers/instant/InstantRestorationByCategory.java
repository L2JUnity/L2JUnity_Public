/*
 * Copyright (C) 2004-2017 L2J Unity
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
package org.l2junity.scripts.handlers.effecthandlers.instant;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.l2junity.gameserver.enums.CategoryType;
import org.l2junity.gameserver.handler.EffectHandler;
import org.l2junity.gameserver.model.StatsSet;
import org.l2junity.gameserver.model.WorldObject;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.effects.AbstractEffect;
import org.l2junity.gameserver.model.holders.ItemHolder;
import org.l2junity.gameserver.model.items.instance.ItemInstance;
import org.l2junity.gameserver.model.skills.Skill;
import org.l2junity.gameserver.scripting.annotations.SkillScript;

/**
 * @author Sdw
 */
public class InstantRestorationByCategory extends AbstractEffect
{
	final private Map<CategoryType, List<ItemHolder>> _products = new EnumMap<>(CategoryType.class);
	
	public InstantRestorationByCategory(StatsSet params)
	{
		for (StatsSet group : params.getList("categories", StatsSet.class))
		{
			final List<ItemHolder> items = new ArrayList<>();
			for (StatsSet item : group.getList(".", StatsSet.class))
			{
				items.add(new ItemHolder(item.getInt(".id"), item.getInt(".count")));
			}
			_products.put(group.getEnum(".category", CategoryType.class), items);
		}
	}
	
	@Override
	public void instant(Creature caster, WorldObject target, Skill skill, ItemInstance item)
	{
		final PlayerInstance casterPlayer = caster.asPlayer();
		if (casterPlayer == null)
		{
			return;
		}
		
		//@formatter:off
		_products.entrySet().stream()
				.filter(entry -> casterPlayer.isInCategory(entry.getKey()))
				.map(Entry::getValue)
				.flatMap(List::stream)
				.forEach(i -> casterPlayer.addItem("Extract", i.getId(), i.getCount(), casterPlayer, true));
		//@formatter:on
	}

	@SkillScript
	public static void main()
	{
		// Not confirmed
		EffectHandler.getInstance().registerHandler("i_restoration_by_category", InstantRestorationByCategory::new);
	}
}