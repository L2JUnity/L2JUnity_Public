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
package org.l2junity.scripts.handlers.effecthandlers;

import org.l2junity.gameserver.handler.EffectHandler;
import org.l2junity.gameserver.model.StatsSet;
import org.l2junity.gameserver.model.WorldObject;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.effects.AbstractEffect;
import org.l2junity.gameserver.model.items.instance.ItemInstance;
import org.l2junity.gameserver.model.skills.Skill;
import org.l2junity.gameserver.model.stats.BooleanStat;
import org.l2junity.gameserver.scripting.annotations.SkillScript;

/**
 * Modify vital effect implementation.
 * @author malyelfik
 */
public final class ModifyVital extends AbstractEffect
{
	// Modify types
	enum ModifyType
	{
		DIFF,
		SET,
		PER;
	}
	
	// Effect parameters
	private final ModifyType _type;
	private final int _hp;
	private final int _mp;
	private final int _cp;
	
	public ModifyVital(StatsSet params)
	{
		_type = params.getEnum("type", ModifyType.class);
		if (!_type.equals(ModifyType.SET))
		{
			_hp = params.getInt("hp", 0);
			_mp = params.getInt("mp", 0);
			_cp = params.getInt("cp", 0);
		}
		else
		{
			_hp = params.getInt("hp", -1);
			_mp = params.getInt("mp", -1);
			_cp = params.getInt("cp", -1);
		}
	}
	
	@Override
	public void instant(Creature caster, WorldObject target, Skill skill, ItemInstance item)
	{
		final Creature targetCreature = target.asCreature();
		if (targetCreature == null)
		{
			return;
		}
		
		if (targetCreature.isDead())
		{
			return;
		}
		
		if (caster.isPlayer() && targetCreature.isPlayer() && targetCreature.getStat().has(BooleanStat.FACE_OFF) && (targetCreature.asPlayer().getAttackerObjId() != caster.getObjectId()))
		{
			return;
		}
		
		switch (_type)
		{
			case DIFF:
			{
				targetCreature.setCurrentCp(targetCreature.getCurrentCp() + _cp);
				targetCreature.setCurrentHp(targetCreature.getCurrentHp() + _hp);
				targetCreature.setCurrentMp(targetCreature.getCurrentMp() + _mp);
				break;
			}
			case SET:
			{
				if (_cp >= 0)
				{
					targetCreature.setCurrentCp(_cp);
				}
				if (_hp >= 0)
				{
					targetCreature.setCurrentHp(_hp);
				}
				if (_mp >= 0)
				{
					targetCreature.setCurrentMp(_mp);
				}
				break;
			}
			case PER:
			{
				targetCreature.setCurrentCp(targetCreature.getCurrentCp() + (targetCreature.getMaxCp() * (_cp / 100)));
				targetCreature.setCurrentHp(targetCreature.getCurrentHp() + (targetCreature.getMaxHp() * (_hp / 100)));
				targetCreature.setCurrentMp(targetCreature.getCurrentMp() + (targetCreature.getMaxMp() * (_mp / 100)));
				break;
			}
		}
	}
	
	@SkillScript
	public static void main()
	{
		EffectHandler.getInstance().registerHandler("ModifyVital", ModifyVital::new);
	}
}
