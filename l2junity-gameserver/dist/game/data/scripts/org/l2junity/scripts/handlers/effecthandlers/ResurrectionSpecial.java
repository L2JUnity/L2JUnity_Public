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
package org.l2junity.scripts.handlers.effecthandlers;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.l2junity.gameserver.handler.EffectHandler;
import org.l2junity.gameserver.model.StatsSet;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.actor.instance.L2PetInstance;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.instancezone.Instance;
import org.l2junity.gameserver.model.skills.Skill;
import org.l2junity.gameserver.model.stats.BooleanStat;
import org.l2junity.gameserver.scripting.annotations.SkillScript;

/**
 * Resurrection Special effect implementation.
 * @author Zealar
 */
public final class ResurrectionSpecial extends AbstractBooleanStatEffect
{
	private final int _power;
	private final Set<Integer> _instanceId;
	
	public ResurrectionSpecial(StatsSet params)
	{
		super(BooleanStat.RESURRECTION_SPECIAL);
		_power = params.getInt("power", 0);
		
		final String instanceIds = params.getString("instanceId", null);
		if ((instanceIds != null) && !instanceIds.isEmpty())
		{
			_instanceId = new HashSet<>();
			for (String id : instanceIds.split(";"))
			{
				_instanceId.add(Integer.parseInt(id));
			}
		}
		else
		{
			_instanceId = Collections.<Integer> emptySet();
		}
	}
	
	@Override
	public void pumpEnd(Creature effector, Creature target, Skill skill)
	{
		if (!target.isPlayer() && !target.isPet())
		{
			return;
		}
		
		final PlayerInstance caster = effector.getActingPlayer();
		final Instance instance = caster.getInstanceWorld();
		if (!_instanceId.isEmpty() && ((instance == null) || !_instanceId.contains(instance.getTemplateId())))
		{
			return;
		}
		
		if (target.isPlayer())
		{
			target.getActingPlayer().reviveRequest(caster, skill, false, _power);
		}
		else if (target.isPet())
		{
			final L2PetInstance pet = (L2PetInstance) target;
			target.getActingPlayer().reviveRequest(pet.getActingPlayer(), skill, true, _power);
		}
	}
	
	@SkillScript
	public static void main()
	{
		EffectHandler.getInstance().registerHandler("ResurrectionSpecial", ResurrectionSpecial::new);
	}
}