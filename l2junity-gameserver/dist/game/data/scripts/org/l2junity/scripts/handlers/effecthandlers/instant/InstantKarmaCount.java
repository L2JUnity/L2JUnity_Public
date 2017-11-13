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

import org.l2junity.gameserver.handler.EffectHandler;
import org.l2junity.gameserver.model.StatsSet;
import org.l2junity.gameserver.model.WorldObject;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.effects.AbstractEffect;
import org.l2junity.gameserver.model.items.instance.ItemInstance;
import org.l2junity.gameserver.model.skills.Skill;
import org.l2junity.gameserver.scripting.annotations.SkillScript;

/**
 * Item Effect: Decreases/resets karma count.
 * @author Nik
 */
public class InstantKarmaCount extends AbstractEffect
{
	private final int _amount;
	private final int _mode;
	
	public InstantKarmaCount(StatsSet params)
	{
		_amount = params.getInt("amount");
		switch (params.getString("mode", "DIFF"))
		{
			case "DIFF":
			{
				_mode = 0;
				break;
			}
			case "RESET":
			{
				_mode = 1;
				break;
			}
			default:
			{
				throw new IllegalArgumentException("Mode should be DIFF or RESET skill id:" + params.getInt("id"));
			}
		}
	}
	
	@Override
	public void instant(Creature caster, WorldObject target, Skill skill, ItemInstance item)
	{
		final PlayerInstance targetPlayer = target.asPlayer();
		if (targetPlayer == null)
		{
			return;
		}
		
		// Check if player has no karma.
		if (targetPlayer.getReputation() >= 0)
		{
			return;
		}
		
		switch (_mode)
		{
			case 0: // diff
			{
				int newReputation = Math.min(targetPlayer.getReputation() + _amount, 0);
				targetPlayer.setReputation(newReputation);
				break;
			}
			case 1: // reset
			{
				targetPlayer.setReputation(_amount);
				break;
			}
		}
	}

	@SkillScript
	public static void main()
	{
		EffectHandler.getInstance().registerHandler("i_karma_count", InstantKarmaCount::new);
	}
}
