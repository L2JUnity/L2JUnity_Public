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
package org.l2junity.gameserver.model.actor;

import org.l2junity.gameserver.ai.CtrlEvent;
import org.l2junity.gameserver.enums.InstanceType;
import org.l2junity.gameserver.instancemanager.ZoneManager;
import org.l2junity.gameserver.model.ClanWar;
import org.l2junity.gameserver.model.ClanWar.ClanWarState;
import org.l2junity.gameserver.model.L2Clan;
import org.l2junity.gameserver.model.WorldObject;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.actor.stat.PlayableStat;
import org.l2junity.gameserver.model.actor.status.PlayableStatus;
import org.l2junity.gameserver.model.actor.templates.L2CharTemplate;
import org.l2junity.gameserver.model.events.EventDispatcher;
import org.l2junity.gameserver.model.events.impl.character.OnCreatureDeath;
import org.l2junity.gameserver.model.events.impl.character.OnCreatureKilled;
import org.l2junity.gameserver.model.events.returns.TerminateReturn;
import org.l2junity.gameserver.model.instancezone.Instance;
import org.l2junity.gameserver.model.interfaces.ILocational;
import org.l2junity.gameserver.model.items.instance.ItemInstance;
import org.l2junity.gameserver.model.skills.AbnormalType;
import org.l2junity.gameserver.model.skills.Skill;
import org.l2junity.gameserver.model.stats.BooleanStat;
import org.l2junity.gameserver.network.client.send.EtcStatusUpdate;

/**
 * This class represents all Playable characters in the world.<br>
 * L2Playable:
 * <ul>
 * <li>L2PcInstance</li>
 * <li>L2Summon</li>
 * </ul>
 */
public abstract class Playable extends Creature
{
	private Creature _lockedTarget = null;
	private PlayerInstance transferDmgTo = null;
	
	/**
	 * Constructor of L2Playable.<br>
	 * <B><U> Actions</U> :</B>
	 * <ul>
	 * <li>Call the L2Character constructor to create an empty _skills slot and link copy basic Calculator set to this L2Playable</li>
	 * </ul>
	 * @param objectId the object id
	 * @param template The L2CharTemplate to apply to the L2Playable
	 */
	public Playable(int objectId, L2CharTemplate template)
	{
		super(objectId, template);
		setInstanceType(InstanceType.L2Playable);
		setIsInvul(false);
	}
	
	public Playable(L2CharTemplate template)
	{
		super(template);
		setInstanceType(InstanceType.L2Playable);
		setIsInvul(false);
	}
	
	@Override
	public PlayableStat getStat()
	{
		return (PlayableStat) super.getStat();
	}
	
	@Override
	public void initCharStat()
	{
		setStat(new PlayableStat(this));
	}
	
	@Override
	public PlayableStatus getStatus()
	{
		return (PlayableStatus) super.getStatus();
	}
	
	@Override
	public void initCharStatus()
	{
		setStatus(new PlayableStatus(this));
	}
	
	@Override
	public boolean doDie(Creature killer)
	{
		final TerminateReturn returnBack = EventDispatcher.getInstance().notifyEvent(new OnCreatureDeath(killer, this), this, TerminateReturn.class);
		if ((returnBack != null) && returnBack.terminate())
		{
			return false;
		}
		
		// killing is only possible one time
		synchronized (this)
		{
			if (isDead())
			{
				return false;
			}
			// now reset currentHp to zero
			setCurrentHp(0);
			setIsDead(true);
		}
		
		EventDispatcher.getInstance().notifyEvent(new OnCreatureKilled(killer, this), killer);
		
		// Set target to null and cancel Attack or Cast
		setTarget(null);
		stopActions();
		
		// Stop HP/MP/CP Regeneration task
		getStatus().stopHpMpRegeneration();
		
		boolean deleteBuffs = true;
		
		if (getStat().has(BooleanStat.PRESERVE_ABNORMAL))
		{
			getEffectList().stopEffects(AbnormalType.PRESERVE_ABNORMAL);
			deleteBuffs = false;
		}
		if (getStat().has(BooleanStat.RESURRECTION_SPECIAL))
		{
			getEffectList().stopEffects(AbnormalType.RESURRECTION_SPECIAL);
			deleteBuffs = false;
		}
		if (isPlayer())
		{
			PlayerInstance activeChar = getActingPlayer();
			
			if (activeChar.hasCharmOfCourage())
			{
				if (activeChar.isInSiege())
				{
					getActingPlayer().reviveRequest(getActingPlayer(), null, false, 0);
				}
				activeChar.setCharmOfCourage(false);
				activeChar.sendPacket(new EtcStatusUpdate(activeChar));
			}
		}
		
		if (deleteBuffs)
		{
			stopAllEffectsExceptThoseThatLastThroughDeath();
		}
		
		// Send the Server->Client packet StatusUpdate with current HP and MP to all other L2PcInstance to inform
		broadcastStatusUpdate();
		
		ZoneManager.getInstance().getRegion(this).onDeath(this);
		
		// Notify instance
		if (isPlayer())
		{
			final Instance instance = getInstanceWorld();
			if (instance != null)
			{
				instance.onDeath(getActingPlayer());
			}
		}
		
		if (killer != null)
		{
			final PlayerInstance killerPlayer = killer.getActingPlayer();
			if ((killerPlayer != null) && isPlayable())
			{
				killerPlayer.onPlayerKill(this);
			}
		}
		
		// Notify L2Character AI
		getAI().notifyEvent(CtrlEvent.EVT_DEAD);
		return true;
	}
	
	public boolean checkIfPvP(PlayerInstance target)
	{
		final PlayerInstance player = getActingPlayer();
		
		if ((player == null) || (target == null) || (player == target))
		{
			return true;
		}
		
		if (target.isOnDarkSide())
		{
			return true;
		}
		else if (target.getReputation() < 0)
		{
			return true;
		}
		else if ((player.getPvpFlag() > 0) && (target.getPvpFlag() > 0))
		{
			return true;
		}
		else if (player.isInParty() && player.getParty().containsPlayer(target))
		{
			return false;
		}
		
		final L2Clan playerClan = player.getClan();
		
		if ((playerClan != null) && !player.isAcademyMember() && !target.isAcademyMember())
		{
			final ClanWar war = playerClan.getWarWith(target.getClanId());
			return (war != null) && (war.getState() == ClanWarState.MUTUAL);
		}
		return false;
	}
	
	/**
	 * Return True.
	 */
	@Override
	public boolean canBeAttacked()
	{
		return true;
	}
	
	/**
	 * @return {@code true} if the Silent Moving mode is active, {@code false} otherwise
	 */
	public boolean isSilentMovingAffected()
	{
		return getStat().has(BooleanStat.SILENT_MOVE);
	}
	
	/**
	 * For Newbie Protection Blessing skill, keeps you safe from an attack by a chaotic character >= 10 levels apart from you.
	 * @return
	 */
	public final boolean isProtectionBlessingAffected()
	{
		return getStat().has(BooleanStat.PROTECTION_BLESSING);
	}
	
	@Override
	public void updateEffectIcons(boolean partyOnly)
	{
		getEffectList().updateEffectIcons(partyOnly);
	}
	
	public boolean isLockedTarget()
	{
		return _lockedTarget != null;
	}
	
	public Creature getLockedTarget()
	{
		return _lockedTarget;
	}
	
	public void setLockedTarget(Creature cha)
	{
		_lockedTarget = cha;
	}
	
	public void setTransferDamageTo(PlayerInstance val)
	{
		transferDmgTo = val;
	}
	
	public PlayerInstance getTransferingDamageTo()
	{
		return transferDmgTo;
	}
	
	public abstract void doPickupItem(WorldObject object);
	
	public abstract boolean useMagic(Skill skill, ILocational target, ItemInstance item, boolean forceUse, boolean dontMove);
	
	public abstract void storeMe();
	
	public abstract void storeEffect(boolean storeEffects);
	
	public abstract void restoreEffects();
	
	@Override
	public boolean isPlayable()
	{
		return true;
	}
	
	@Override
	public Playable asPlayable()
	{
		return this;
	}
}
