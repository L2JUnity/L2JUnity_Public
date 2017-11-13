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
import org.l2junity.gameserver.taskmanager.DecayTaskManager;

/**
 * Resurrection effect implementation.
 * @author Adry_85
 */
public final class InstantResurrection extends AbstractEffect
{
	private final int _power;
	
	public InstantResurrection(StatsSet params)
	{
		_power = params.getInt("power");
	}
	
	@Override
	public L2EffectType getEffectType()
	{
		return L2EffectType.RESURRECTION;
	}
	
	@Override
	public void instant(Creature caster, WorldObject target, Skill skill, ItemInstance item)
	{
		if (caster.isPlayer() && target.isPlayable())
		{
			target.getActingPlayer().reviveRequest(caster.asPlayer(), skill, target.isPet(), _power);
		}
		else if (target.isCreature())
		{
			final Creature targetCreature = target.asCreature();
			DecayTaskManager.getInstance().cancel(targetCreature);
			targetCreature.doRevive(Formulas.calculateSkillResurrectRestorePercent(_power, caster));
		}
	}

	@SkillScript
	public static void main()
	{
		EffectHandler.getInstance().registerHandler("i_resurrection", InstantResurrection::new);
	}
}