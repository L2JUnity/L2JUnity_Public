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
import org.l2junity.gameserver.model.Party;
import org.l2junity.gameserver.model.StatsSet;
import org.l2junity.gameserver.model.WorldObject;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.effects.AbstractEffect;
import org.l2junity.gameserver.model.instancezone.Instance;
import org.l2junity.gameserver.model.items.instance.ItemInstance;
import org.l2junity.gameserver.model.olympiad.OlympiadManager;
import org.l2junity.gameserver.model.skills.Skill;
import org.l2junity.gameserver.model.zone.ZoneId;
import org.l2junity.gameserver.network.client.send.SystemMessage;
import org.l2junity.gameserver.network.client.send.string.SystemMessageId;
import org.l2junity.gameserver.scripting.annotations.SkillScript;

/**
 * GM Effect: Call Target's Party around target effect implementation.
 * @author Nik
 */
public final class InstantCallTargetParty extends AbstractEffect
{
	public InstantCallTargetParty(StatsSet params)
	{
	}
	
	@Override
	public void instant(Creature caster, WorldObject target, Skill skill, ItemInstance item)
	{
		final PlayerInstance targetPlayer = target.asPlayer();
		if (targetPlayer == null)
		{
			return;
		}
		
		final Party party = targetPlayer.getParty();
		if (party != null)
		{
			party.getMembers().stream().filter(p -> (p != targetPlayer) && checkSummonTargetStatus(p, caster)).forEach(p -> p.teleToLocation(targetPlayer.getLocation(), true));
		}
	}
	
	public static boolean checkSummonTargetStatus(PlayerInstance target, Creature activeChar)
	{
		if (target == activeChar)
		{
			return false;
		}
		
		if (target.isAlikeDead())
		{
			SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_IS_DEAD_AT_THE_MOMENT_AND_CANNOT_BE_SUMMONED_OR_TELEPORTED);
			sm.addPcName(target);
			activeChar.sendPacket(sm);
			return false;
		}
		
		if (target.isInStoreMode())
		{
			SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_IS_CURRENTLY_TRADING_OR_OPERATING_A_PRIVATE_STORE_AND_CANNOT_BE_SUMMONED_OR_TELEPORTED);
			sm.addPcName(target);
			activeChar.sendPacket(sm);
			return false;
		}
		
		if (target.isImmobilized() || target.isInCombat())
		{
			SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_IS_ENGAGED_IN_COMBAT_AND_CANNOT_BE_SUMMONED_OR_TELEPORTED);
			sm.addPcName(target);
			activeChar.sendPacket(sm);
			return false;
		}
		
		if (target.isInOlympiadMode())
		{
			activeChar.sendPacket(SystemMessageId.A_USER_PARTICIPATING_IN_THE_OLYMPIAD_CANNOT_USE_SUMMONING_OR_TELEPORTING);
			return false;
		}
		
		if (target.isFlyingMounted() || target.isCombatFlagEquipped())
		{
			activeChar.sendPacket(SystemMessageId.YOU_CANNOT_USE_SUMMONING_OR_TELEPORTING_IN_THIS_AREA);
			return false;
		}
		
		if (target.inObserverMode() || OlympiadManager.getInstance().isRegisteredInComp(target))
		{
			SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_IS_IN_AN_AREA_WHICH_BLOCKS_SUMMONING_OR_TELEPORTING2);
			sm.addCharName(target);
			activeChar.sendPacket(sm);
			return false;
		}
		
		if (target.isInsideZone(ZoneId.NO_SUMMON_FRIEND) || target.isInsideZone(ZoneId.JAIL))
		{
			SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_IS_IN_AN_AREA_WHICH_BLOCKS_SUMMONING_OR_TELEPORTING);
			sm.addString(target.getName());
			activeChar.sendPacket(sm);
			return false;
		}
		
		final Instance instance = activeChar.getInstanceWorld();
		if ((instance != null) && !instance.isPlayerSummonAllowed())
		{
			activeChar.sendPacket(SystemMessageId.YOU_MAY_NOT_SUMMON_FROM_YOUR_CURRENT_LOCATION);
			return false;
		}
		return true;
	}

	@SkillScript
	public static void main()
	{
		EffectHandler.getInstance().registerHandler("i_call_target_party", InstantCallTargetParty::new);
	}
}
