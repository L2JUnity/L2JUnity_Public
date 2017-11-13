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
package org.l2junity.gameserver.ai;

import static org.l2junity.gameserver.ai.CtrlIntention.AI_INTENTION_ATTACK;
import static org.l2junity.gameserver.ai.CtrlIntention.AI_INTENTION_FOLLOW;

import org.l2junity.gameserver.model.WorldObject;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.actor.Summon;
import org.l2junity.gameserver.model.items.instance.ItemInstance;
import org.l2junity.gameserver.model.skills.Skill;
import org.l2junity.gameserver.model.skills.SkillCaster;

public class SummonAI extends PlayableAI
{
	private volatile boolean _thinking; // to prevent recursive thinking
	private volatile boolean _startFollow = ((Summon) _actor).getFollowStatus();
	private Creature _lastAttack = null;
	private volatile boolean _isDefending;
	
	public SummonAI(Summon summon)
	{
		super(summon);
	}
	
	@Override
	protected void onIntentionIdle()
	{
		stopFollow();
		_startFollow = false;
		onIntentionActive();
	}
	
	@Override
	protected void onIntentionActive()
	{
		Summon summon = (Summon) _actor;
		if (_startFollow)
		{
			setIntention(AI_INTENTION_FOLLOW, summon.getOwner());
		}
		else
		{
			super.onIntentionActive();
		}
	}
	
	private void thinkAttack()
	{
		final WorldObject target = getTarget();
		final Creature attackTarget = (target != null) && target.isCreature() ? (Creature) target : null;
		
		if (checkTargetLostOrDead(attackTarget))
		{
			setTarget(null);
			return;
		}
		if (maybeMoveToPawn(attackTarget, _actor.getPhysicalAttackRange()))
		{
			return;
		}
		clientStopMoving(null);
		_actor.doAutoAttack(attackTarget);
	}
	
	private void thinkCast()
	{
		Summon summon = (Summon) _actor;
		if (summon.isCastingNow(SkillCaster::isAnyNormalType))
		{
			return;
		}
		
		final WorldObject target = _skill.getTarget(_actor, _forceUse, _dontMove, false);
		if (checkTargetLost(target))
		{
			setTarget(null);
			return;
		}
		boolean val = _startFollow;
		if (maybeMoveToPawn(target, _actor.getMagicalAttackRange(_skill)))
		{
			return;
		}
		summon.setFollowStatus(false);
		setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
		_startFollow = val;
		_actor.doCast(_skill, _item, _forceUse, _dontMove);
	}
	
	private void thinkPickUp()
	{
		final WorldObject target = getTarget();
		if (checkTargetLost(target))
		{
			return;
		}
		if (maybeMoveToPawn(target, 36))
		{
			return;
		}
		setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
		getActor().doPickupItem(target);
	}
	
	private void thinkInteract()
	{
		final WorldObject target = getTarget();
		if (checkTargetLost(target))
		{
			return;
		}
		if (maybeMoveToPawn(target, 36))
		{
			return;
		}
		setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
	}
	
	@Override
	protected void onEvtThink()
	{
		if (_thinking || _actor.isCastingNow() || _actor.isAllSkillsDisabled())
		{
			return;
		}
		_thinking = true;
		try
		{
			switch (getIntention())
			{
				case AI_INTENTION_ATTACK:
					thinkAttack();
					break;
				case AI_INTENTION_CAST:
					thinkCast();
					break;
				case AI_INTENTION_PICK_UP:
					thinkPickUp();
					break;
				case AI_INTENTION_INTERACT:
					thinkInteract();
					break;
			}
		}
		finally
		{
			_thinking = false;
		}
	}
	
	@Override
	protected void onEvtFinishCasting()
	{
		if (_lastAttack == null)
		{
			((Summon) _actor).setFollowStatus(_startFollow);
		}
		else
		{
			setIntention(CtrlIntention.AI_INTENTION_ATTACK, _lastAttack);
			_lastAttack = null;
		}
	}
	
	@Override
	protected void onEvtAttacked(Creature attacker)
	{
		super.onEvtAttacked(attacker);
		
		startFollowEvadeTarget(attacker); // TODO: This should only work for autoattack, not skills, not even melee skills!
		
		if (isDefending())
		{
			defendAttack(attacker);
		}
	}
	
	@Override
	protected void onEvtEvaded(Creature attacker)
	{
		super.onEvtEvaded(attacker);
		
		if (isDefending())
		{
			defendAttack(attacker);
		}
	}
	
	public void defendAttack(Creature attacker)
	{
		// Cannot defend while attacking or casting.
		if (_actor.isAttackingNow() || _actor.isCastingNow())
		{
			return;
		}
		
		final Summon summon = getActor();
		if ((summon.getOwner() != null) && (summon.getOwner() != attacker) && !summon.isMoving() && summon.canAttack(attacker, false) && summon.getOwner().isInRadius3d(_actor, 300))
		{
			summon.doAutoAttack(attacker);
		}
	}
	
	public void notifyFollowStatusChange()
	{
		_startFollow = !_startFollow;
		switch (getIntention())
		{
			case AI_INTENTION_ACTIVE:
			case AI_INTENTION_FOLLOW:
			case AI_INTENTION_MOVE_TO:
			case AI_INTENTION_PICK_UP:
				((Summon) _actor).setFollowStatus(_startFollow);
		}
	}
	
	public void setStartFollowController(boolean val)
	{
		_startFollow = val;
	}
	
	@Override
	protected void onIntentionCast(Skill skill, WorldObject target, ItemInstance item, boolean forceUse, boolean dontMove)
	{
		if (getIntention() == AI_INTENTION_ATTACK)
		{
			_lastAttack = (getTarget() != null) && getTarget().isCreature() ? (Creature) getTarget() : null;
		}
		else
		{
			_lastAttack = null;
		}
		super.onIntentionCast(skill, target, item, forceUse, dontMove);
	}
	
	@Override
	public void stopAITask()
	{
		super.stopAITask();
	}
	
	@Override
	public Summon getActor()
	{
		return (Summon) super.getActor();
	}
	
	/**
	 * @return if the summon is defending itself or master.
	 */
	public boolean isDefending()
	{
		return _isDefending;
	}
	
	/**
	 * @param isDefending set the summon to defend itself and master, or be passive and avoid while being attacked.
	 */
	public void setDefending(boolean isDefending)
	{
		_isDefending = isDefending;
	}
}
