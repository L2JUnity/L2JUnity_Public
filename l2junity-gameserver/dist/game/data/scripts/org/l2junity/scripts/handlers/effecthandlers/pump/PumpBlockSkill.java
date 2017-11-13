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

import org.l2junity.commons.util.ArrayUtil;
import org.l2junity.gameserver.handler.EffectHandler;
import org.l2junity.gameserver.model.StatsSet;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.effects.AbstractEffect;
import org.l2junity.gameserver.model.events.EventType;
import org.l2junity.gameserver.model.events.impl.character.OnCreatureSkillUse;
import org.l2junity.gameserver.model.events.listeners.FunctionEventListener;
import org.l2junity.gameserver.model.events.returns.TerminateReturn;
import org.l2junity.gameserver.model.skills.Skill;
import org.l2junity.gameserver.scripting.annotations.SkillScript;

/**
 * Block Skills by isMagic type.
 * @author Nik
 */
public final class PumpBlockSkill extends AbstractEffect
{
	private final int[] _magicTypes;
	
	public PumpBlockSkill(StatsSet params)
	{
		_magicTypes = params.getIntArray("magicTypes", ";");
		
		if ((_magicTypes == null) || (_magicTypes.length == 0))
		{
			throw new IllegalArgumentException("Must have parameters!");
		}
	}
	
	public TerminateReturn onSkillUseEvent(OnCreatureSkillUse event)
	{
		if (ArrayUtil.contains(_magicTypes, event.getSkill().getMagicType()))
		{
			return new TerminateReturn(true, true, true);
		}
		
		return null;
	}
	
	@Override
	public void pumpStart(Creature caster, Creature target, Skill skill)
	{
		target.addListener(new FunctionEventListener(target, EventType.ON_CREATURE_SKILL_USE, (OnCreatureSkillUse event) -> onSkillUseEvent(event), this));
	}
	
	@Override
	public void pumpEnd(Creature caster, Creature target, Skill skill)
	{
		target.removeListenerIf(EventType.ON_CREATURE_SKILL_USE, listener -> listener.getOwner() == this);
	}
	
	@SkillScript
	public static void main()
	{
		EffectHandler.getInstance().registerHandler("p_block_skill", PumpBlockSkill::new);
	}
}
