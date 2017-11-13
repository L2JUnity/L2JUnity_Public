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
import org.l2junity.gameserver.data.xml.impl.SkillData;
import org.l2junity.gameserver.handler.EffectHandler;
import org.l2junity.gameserver.handler.ITargetTypeHandler;
import org.l2junity.gameserver.handler.TargetHandler;
import org.l2junity.gameserver.model.StatsSet;
import org.l2junity.gameserver.model.WorldObject;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.effects.AbstractEffect;
import org.l2junity.gameserver.model.events.EventType;
import org.l2junity.gameserver.model.events.impl.character.OnCreatureSkillFinishCast;
import org.l2junity.gameserver.model.events.listeners.ConsumerEventListener;
import org.l2junity.gameserver.model.holders.SkillHolder;
import org.l2junity.gameserver.model.skills.BuffInfo;
import org.l2junity.gameserver.model.skills.Skill;
import org.l2junity.gameserver.model.skills.SkillCaster;
import org.l2junity.gameserver.scripting.annotations.SkillScript;

/**
 * Trigger Skill By Skill effect implementation.
 * @author Zealar
 */
public final class PumpTriggerSkillBySkill extends AbstractEffect
{
	private final int _castSkillId;
	private final int _chance;
	private final SkillHolder _skill;
	private final int _skillLevelScaleTo;
	private final ITargetTypeHandler _targetTypeHandler;
	
	public PumpTriggerSkillBySkill(StatsSet params)
	{
		_castSkillId = params.getInt("castSkillId");
		_chance = params.getInt("chance");
		_skill = new SkillHolder(params.getInt("skillId"), params.getInt("skillLevel"));
		_skillLevelScaleTo = params.getInt("skillLevelScaleTo", 0);
		final String targetType = params.getString("targetType", "TARGET");
		_targetTypeHandler = TargetHandler.getInstance().getTargetTypeHandler(targetType);
		if (_targetTypeHandler == null)
		{
			throw new RuntimeException("Target Type not found for effect[" + getClass().getSimpleName() + "] TargetType[" + targetType + "].");
		}
	}
	
	@Override
	public void pumpStart(Creature caster, Creature target, Skill skill)
	{
		target.addListener(new ConsumerEventListener(target, EventType.ON_CREATURE_SKILL_FINISH_CAST, (OnCreatureSkillFinishCast event) -> onSkillUseEvent(event), this));
	}
	
	@Override
	public void pumpEnd(Creature caster, Creature target, Skill skill)
	{
		target.removeListenerIf(EventType.ON_CREATURE_SKILL_FINISH_CAST, listener -> listener.getOwner() == this);
	}
	
	private void onSkillUseEvent(OnCreatureSkillFinishCast event)
	{
		if ((_chance == 0) || ((_skill.getSkillId() == 0) || (_skill.getSkillLevel() == 0) || (_castSkillId == 0)))
		{
			return;
		}
		
		if (_castSkillId != event.getSkill().getId())
		{
			return;
		}
		
		if (!event.getTarget().isCreature())
		{
			return;
		}
		
		if ((_chance < 100) && (Rnd.get(100) > _chance))
		{
			return;
		}
		
		final Skill triggerSkill;
		if (_skillLevelScaleTo <= 0)
		{
			triggerSkill = _skill.getSkill();
		}
		else
		{
			final BuffInfo buffInfo = ((Creature) event.getTarget()).getEffectList().getBuffInfoBySkillId(_skill.getSkillId());
			if (buffInfo != null)
			{
				triggerSkill = SkillData.getInstance().getSkill(_skill.getSkillId(), Math.min(_skillLevelScaleTo, buffInfo.getSkill().getLevel() + 1));
			}
			else
			{
				triggerSkill = _skill.getSkill();
			}
		}
		
		final WorldObject target = _targetTypeHandler.getTarget(event.getCaster(), event.getTarget(), triggerSkill, false, false, false);
		if ((target != null) && target.isCreature())
		{
			SkillCaster.triggerCast(event.getCaster(), target, triggerSkill);
		}
	}
	
	@SkillScript
	public static void main()
	{
		EffectHandler.getInstance().registerHandler("p_trigger_skill_by_skill", PumpTriggerSkillBySkill::new);
	}
}
