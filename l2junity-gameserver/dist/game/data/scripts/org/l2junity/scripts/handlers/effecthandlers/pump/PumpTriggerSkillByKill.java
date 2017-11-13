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

import org.l2junity.commons.util.Rnd;
import org.l2junity.gameserver.enums.InstanceType;
import org.l2junity.gameserver.handler.EffectHandler;
import org.l2junity.gameserver.model.StatsSet;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.effects.AbstractEffect;
import org.l2junity.gameserver.model.events.EventType;
import org.l2junity.gameserver.model.events.impl.character.OnCreatureKilled;
import org.l2junity.gameserver.model.events.listeners.ConsumerEventListener;
import org.l2junity.gameserver.model.holders.SkillHolder;
import org.l2junity.gameserver.model.skills.Skill;
import org.l2junity.gameserver.model.skills.SkillCaster;
import org.l2junity.gameserver.scripting.annotations.SkillScript;

/**
 * Trigger Skill By Kill effect implementation.
 * @author Sdw
 */
public final class PumpTriggerSkillByKill extends AbstractEffect
{
	private final int _chance;
	private final InstanceType _attackerType;
	private final SkillHolder _skill;
	
	public PumpTriggerSkillByKill(StatsSet params)
	{
		_chance = params.getInt("chance");
		_attackerType = params.getEnum("attackerType", InstanceType.class, InstanceType.L2Character);
		_skill = new SkillHolder(params.getInt("skillId"), params.getInt("skillLevel"));
	}
	
	public void onCreatureKilled(OnCreatureKilled event, Creature target)
	{
		if ((_chance == 0) || ((_skill.getSkillId() == 0) || (_skill.getSkillLevel() == 0)))
		{
			return;
		}
		
		if (event.getAttacker() == event.getTarget())
		{
			return;
		}
		
		if ((Rnd.get(100) > _chance) || !event.getTarget().getInstanceType().isType(_attackerType))
		{
			return;
		}
		
		if (event.getAttacker() == target)
		{
			SkillCaster.triggerCast(target, target, _skill.getSkill());
		}
	}
	
	@Override
	public void pumpEnd(Creature caster, Creature target, Skill skill)
	{
		target.removeListenerIf(EventType.ON_CREATURE_KILLED, listener -> listener.getOwner() == this);
	}
	
	@Override
	public void pumpStart(Creature caster, Creature target, Skill skill)
	{
		target.addListener(new ConsumerEventListener(target, EventType.ON_CREATURE_KILLED, (OnCreatureKilled event) -> onCreatureKilled(event, target), this));
	}
	
	@SkillScript
	public static void main()
	{
		EffectHandler.getInstance().registerHandler("p_trigger_skill_by_kill", PumpTriggerSkillByKill::new);
	}
}
