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
package org.l2junity.scripts.handlers.effecthandlers.pump;

import org.l2junity.gameserver.handler.EffectHandler;
import org.l2junity.gameserver.model.StatsSet;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.effects.AbstractEffect;
import org.l2junity.gameserver.model.items.type.CrystalType;
import org.l2junity.gameserver.model.skills.Skill;
import org.l2junity.gameserver.scripting.annotations.SkillScript;

/**
 * An effect that removes equipment grade penalty. Its the base effect for the grade penalty mechanics.
 * @author Nik
 */
public final class PumpRemoveEquipPenalty extends AbstractEffect
{
	private final CrystalType _grade;
	
	public PumpRemoveEquipPenalty(StatsSet params)
	{
		_grade = params.getEnum("grade", CrystalType.class);
	}
	
	@Override
	public boolean checkPumpCondition(Creature caster, Creature target, Skill skill)
	{
		return target.isPlayer();
	}
	
	@Override
	public void pumpStart(Creature caster, Creature target, Skill skill)
	{
		final PlayerInstance player = target.getActingPlayer();
		if (player != null)
		{
			player.setExpertiseLevel(_grade);
		}
	}
	
	@Override
	public void pumpEnd(Creature caster, Creature target, Skill skill)
	{
		final PlayerInstance player = target.getActingPlayer();
		if (player != null)
		{
			player.setExpertiseLevel(null);
		}
	}
	
	@SkillScript
	public static void main()
	{
		EffectHandler.getInstance().registerHandler("p_remove_equip_penalty", PumpRemoveEquipPenalty::new);
	}
}