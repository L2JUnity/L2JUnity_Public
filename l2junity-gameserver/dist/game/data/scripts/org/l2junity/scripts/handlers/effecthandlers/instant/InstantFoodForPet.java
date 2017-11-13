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
package org.l2junity.scripts.handlers.effecthandlers.instant;

import org.l2junity.gameserver.config.RatesConfig;
import org.l2junity.gameserver.enums.MountType;
import org.l2junity.gameserver.handler.EffectHandler;
import org.l2junity.gameserver.model.StatsSet;
import org.l2junity.gameserver.model.WorldObject;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.actor.instance.L2PetInstance;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.effects.AbstractEffect;
import org.l2junity.gameserver.model.items.instance.ItemInstance;
import org.l2junity.gameserver.model.skills.Skill;
import org.l2junity.gameserver.scripting.annotations.SkillScript;

/**
 * @author Sdw
 */
public class InstantFoodForPet extends AbstractEffect
{
	private final int _normal;
	private final int _ride;
	private final int _wyvern;
	
	public InstantFoodForPet(StatsSet params)
	{
		_normal = params.getInt("normal");
		_ride = params.getInt("ride");
		_wyvern = params.getInt("wyvern");
	}
	
	@Override
	public void instant(Creature caster, WorldObject target, Skill skill, ItemInstance item)
	{
		if (target.isPet())
		{
			final L2PetInstance targetPet = target.asPet();
			targetPet.setCurrentFed(targetPet.getCurrentFed() + (_normal * RatesConfig.PET_FOOD_RATE));
		}
		else if (target.isPlayer())
		{
			final PlayerInstance targetPlayer = target.asPlayer();
			if (targetPlayer.getMountType() == MountType.WYVERN)
			{
				targetPlayer.setCurrentFeed(targetPlayer.getCurrentFeed() + _wyvern);
			}
			else
			{
				targetPlayer.setCurrentFeed(targetPlayer.getCurrentFeed() + _ride);
			}
		}
	}

	@SkillScript
	public static void main()
	{
		EffectHandler.getInstance().registerHandler("i_food_for_pet", InstantFoodForPet::new);
	}
}
