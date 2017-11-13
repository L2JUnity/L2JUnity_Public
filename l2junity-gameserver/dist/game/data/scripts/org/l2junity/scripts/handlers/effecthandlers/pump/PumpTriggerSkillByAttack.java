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
import org.l2junity.gameserver.handler.ITargetTypeHandler;
import org.l2junity.gameserver.handler.TargetHandler;
import org.l2junity.gameserver.model.StatsSet;
import org.l2junity.gameserver.model.WorldObject;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.effects.AbstractEffect;
import org.l2junity.gameserver.model.events.EventType;
import org.l2junity.gameserver.model.events.impl.character.OnCreatureDamageDealt;
import org.l2junity.gameserver.model.events.listeners.ConsumerEventListener;
import org.l2junity.gameserver.model.holders.SkillHolder;
import org.l2junity.gameserver.model.items.type.WeaponType;
import org.l2junity.gameserver.model.skills.Skill;
import org.l2junity.gameserver.model.skills.SkillCaster;
import org.l2junity.gameserver.scripting.annotations.SkillScript;

/**
 * Trigger Skill By Attack effect implementation.
 * @author Zealar
 */
public final class PumpTriggerSkillByAttack extends AbstractEffect
{
	private final int _minAttackerLevel;
	private final int _maxAttackerLevel;
	private final int _minDamage;
	private final int _chance;
	private final SkillHolder _skill;
	private final ITargetTypeHandler _targetTypeHandler;
	private final InstanceType _attackerType;
	private int _allowWeapons;
	private final Boolean _isCritical;
	private final boolean _allowNormalAttack;
	private final boolean _allowSkillAttack;
	private final boolean _allowReflect;
	
	/**
	 * @param params
	 */
	
	public PumpTriggerSkillByAttack(StatsSet params)
	{
		_minAttackerLevel = params.getInt("minAttackerLevel", 1);
		_maxAttackerLevel = params.getInt("maxAttackerLevel", 127);
		_minDamage = params.getInt("minDamage", 1);
		_chance = params.getInt("chance");
		_skill = new SkillHolder(params.getInt("skillId"), params.getInt("skillLevel", 1));
		final String targetType = params.getString("targetType", "SELF");
		_targetTypeHandler = TargetHandler.getInstance().getTargetTypeHandler(targetType);
		if (_targetTypeHandler == null)
		{
			throw new RuntimeException("Target Type not found for effect[" + getClass().getSimpleName() + "] TargetType[" + targetType + "].");
		}
		_attackerType = params.getEnum("attackerType", InstanceType.class, InstanceType.L2Character);
		_isCritical = params.getObject("isCritical", Boolean.class);
		_allowNormalAttack = params.getBoolean("allowNormalAttack", true);
		_allowSkillAttack = params.getBoolean("allowSkillAttack", false);
		_allowReflect = params.getBoolean("allowReflect", false);
		
		if (params.getString("allowWeapons", "ALL").equalsIgnoreCase("ALL"))
		{
			_allowWeapons = 0;
		}
		else
		{
			for (String s : params.getString("allowWeapons").split(","))
			{
				_allowWeapons |= WeaponType.valueOf(s).mask();
			}
		}
	}
	
	public void onAttackEvent(OnCreatureDamageDealt event)
	{
		if (event.isDamageOverTime() || (!_allowNormalAttack && !_allowSkillAttack))
		{
			return;
		}
		
		// Check if there is dependancy on critical.
		if ((_isCritical != null) && (_isCritical != event.isCritical()))
		{
			return;
		}
		
		// When no skill attacks are allowed.
		if (!_allowSkillAttack && (event.getSkill() != null))
		{
			return;
		}
		
		// When no normal attacks are allowed.
		if (!_allowNormalAttack && (event.getSkill() == null))
		{
			return;
		}
		
		if (!_allowReflect && event.isReflect())
		{
			return;
		}
		
		if (event.getAttacker() == event.getTarget())
		{
			return;
		}
		
		if ((event.getAttacker().getLevel() < _minAttackerLevel) || (event.getAttacker().getLevel() > _maxAttackerLevel))
		{
			return;
		}
		
		if ((event.getDamage() < _minDamage) || (Rnd.get(100) > _chance) || !event.getAttacker().getInstanceType().isType(_attackerType))
		{
			return;
		}
		
		if (_allowWeapons > 0)
		{
			if ((event.getAttacker().getActiveWeaponItem() == null) || ((event.getAttacker().getActiveWeaponItem().getItemType().mask() & _allowWeapons) == 0))
			{
				return;
			}
		}
		
		final Skill triggerSkill = _skill.getSkill();
		final WorldObject target = _targetTypeHandler.getTarget(event.getAttacker(), event.getTarget(), triggerSkill, false, false, false);
		
		if ((target != null) && target.isCreature())
		{
			SkillCaster.triggerCast(event.getAttacker(), target, triggerSkill);
		}
	}
	
	@Override
	public void pumpEnd(Creature caster, Creature target, Skill skill)
	{
		target.removeListenerIf(EventType.ON_CREATURE_DAMAGE_DEALT, listener -> listener.getOwner() == this);
	}
	
	@Override
	public void pumpStart(Creature caster, Creature target, Skill skill)
	{
		target.addListener(new ConsumerEventListener(target, EventType.ON_CREATURE_DAMAGE_DEALT, (OnCreatureDamageDealt event) -> onAttackEvent(event), this));
	}
	
	@SkillScript
	public static void main()
	{
		EffectHandler.getInstance().registerHandler("p_trigger_skill_by_attack", PumpTriggerSkillByAttack::new);
	}
}
