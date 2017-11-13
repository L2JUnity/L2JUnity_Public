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
package org.l2junity.gameserver.model.options;

import java.util.ArrayList;
import java.util.List;

import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.debugger.DebugType;
import org.l2junity.gameserver.model.effects.AbstractEffect;
import org.l2junity.gameserver.model.holders.SkillHolder;
import org.l2junity.gameserver.model.skills.BuffInfo;
import org.l2junity.gameserver.model.skills.Skill;
import org.l2junity.gameserver.network.client.send.SkillCoolTime;

/**
 * @author UnAfraid
 */
public class Options
{
	private final int _id;
	private List<AbstractEffect> _effects = null;
	private List<SkillHolder> _activeSkill = null;
	private List<SkillHolder> _passiveSkill = null;
	private List<OptionsSkillHolder> _activationSkills = null;
	
	/**
	 * @param id
	 */
	public Options(int id)
	{
		_id = id;
	}
	
	public final int getId()
	{
		return _id;
	}
	
	public void addEffect(AbstractEffect effect)
	{
		if (_effects == null)
		{
			_effects = new ArrayList<>();
		}
		_effects.add(effect);
	}
	
	public List<AbstractEffect> getEffects()
	{
		return _effects;
	}
	
	public boolean hasEffects()
	{
		return _effects != null;
	}
	
	public boolean hasActiveSkills()
	{
		return _activeSkill != null;
	}
	
	public List<SkillHolder> getActiveSkills()
	{
		return _activeSkill;
	}
	
	public void addActiveSkill(SkillHolder holder)
	{
		if (_activeSkill == null)
		{
			_activeSkill = new ArrayList<>();
		}
		_activeSkill.add(holder);
	}
	
	public boolean hasPassiveSkills()
	{
		return _passiveSkill != null;
	}
	
	public List<SkillHolder> getPassiveSkills()
	{
		return _passiveSkill;
	}
	
	public void addPassiveSkill(SkillHolder holder)
	{
		if (_passiveSkill == null)
		{
			_passiveSkill = new ArrayList<>();
		}
		_passiveSkill.add(holder);
	}
	
	public boolean hasActivationSkills()
	{
		return _activationSkills != null;
	}
	
	public boolean hasActivationSkills(OptionsSkillType type)
	{
		if (_activationSkills != null)
		{
			for (OptionsSkillHolder holder : _activationSkills)
			{
				if (holder.getSkillType() == type)
				{
					return true;
				}
			}
		}
		return false;
	}
	
	public List<OptionsSkillHolder> getActivationsSkills()
	{
		return _activationSkills;
	}
	
	public List<OptionsSkillHolder> getActivationsSkills(OptionsSkillType type)
	{
		List<OptionsSkillHolder> temp = new ArrayList<>();
		if (_activationSkills != null)
		{
			for (OptionsSkillHolder holder : _activationSkills)
			{
				if (holder.getSkillType() == type)
				{
					temp.add(holder);
				}
			}
		}
		return temp;
	}
	
	public void addActivationSkill(OptionsSkillHolder holder)
	{
		if (_activationSkills == null)
		{
			_activationSkills = new ArrayList<>();
		}
		_activationSkills.add(holder);
	}
	
	public void apply(PlayerInstance player)
	{
		player.sendDebugMessage("Activating option id: " + _id, DebugType.OPTIONS);
		if (hasEffects())
		{
			final BuffInfo info = new BuffInfo(player, player, null, true, null, this);
			for (AbstractEffect effect : _effects)
			{
				if (effect.calcSuccess(info.getEffector(), info.getEffected(), info.getSkill()))
				{
					effect.instant(info.getEffector(), info.getEffected(), info.getSkill(), info.getItem());
					player.sendDebugMessage("Appling instant effect: " + effect.getClass().getSimpleName(), DebugType.OPTIONS);
					if (effect.checkPumpCondition(info.getEffector(), info.getEffected(), info.getSkill()))
					{
						info.addEffect(effect);
						player.sendDebugMessage("Appling continious effect: " + effect.getClass().getSimpleName(), DebugType.OPTIONS);
					}
				}
			}
			if (!info.getEffects().isEmpty())
			{
				player.getEffectList().add(info);
			}
		}
		if (hasActiveSkills())
		{
			for (SkillHolder holder : getActiveSkills())
			{
				addSkill(player, holder.getSkill());
				player.sendDebugMessage("Adding active skill: " + getActiveSkills(), DebugType.OPTIONS);
			}
		}
		if (hasPassiveSkills())
		{
			for (SkillHolder holder : getPassiveSkills())
			{
				addSkill(player, holder.getSkill());
				player.sendDebugMessage("Adding passive skill: " + getPassiveSkills(), DebugType.OPTIONS);
			}
		}
		if (hasActivationSkills())
		{
			for (OptionsSkillHolder holder : _activationSkills)
			{
				player.addTriggerSkill(holder);
				player.sendDebugMessage("Adding trigger skill: " + holder, DebugType.OPTIONS);
			}
		}
		
		player.sendSkillList();
	}
	
	public void remove(PlayerInstance player)
	{
		player.sendDebugMessage("Deactivating option id: " + _id, DebugType.OPTIONS);
		if (hasEffects())
		{
			for (BuffInfo info : player.getEffectList().getOptions())
			{
				if (info.getOption() == this)
				{
					player.sendDebugMessage("Removing effects: " + info.getEffects(), DebugType.OPTIONS);
					player.getEffectList().remove(info, false, true, true);
				}
			}
		}
		if (hasActiveSkills())
		{
			for (SkillHolder holder : getActiveSkills())
			{
				player.removeSkill(holder.getSkill(), false, false);
				player.sendDebugMessage("Removing active skill: " + getActiveSkills(), DebugType.OPTIONS);
			}
		}
		if (hasPassiveSkills())
		{
			for (SkillHolder holder : getPassiveSkills())
			{
				player.removeSkill(holder.getSkill(), false, true);
				player.sendDebugMessage("Removing passive skill: " + getPassiveSkills(), DebugType.OPTIONS);
			}
		}
		if (hasActivationSkills())
		{
			for (OptionsSkillHolder holder : _activationSkills)
			{
				player.removeTriggerSkill(holder);
				player.sendDebugMessage("Removing trigger skill: " + holder, DebugType.OPTIONS);
			}
		}
		
		player.getStat().recalculateStats(true);
		player.sendSkillList();
	}
	
	private void addSkill(PlayerInstance player, Skill skill)
	{
		boolean updateTimeStamp = false;
		
		player.addSkill(skill, false);
		
		if (skill.isActive())
		{
			final long remainingTime = player.getSkillRemainingReuseTime(skill.getReuseHashCode());
			if (remainingTime > 0)
			{
				player.addTimeStamp(skill, remainingTime);
			}
			updateTimeStamp = true;
		}
		if (updateTimeStamp)
		{
			player.sendPacket(new SkillCoolTime(player));
		}
	}
}
