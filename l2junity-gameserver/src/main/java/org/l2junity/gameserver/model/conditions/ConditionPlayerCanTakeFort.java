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
package org.l2junity.gameserver.model.conditions;

import org.l2junity.gameserver.instancemanager.FortManager;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.entity.Fort;
import org.l2junity.gameserver.model.items.L2Item;
import org.l2junity.gameserver.model.skills.Skill;
import org.l2junity.gameserver.network.client.send.SystemMessage;
import org.l2junity.gameserver.network.client.send.string.SystemMessageId;
import org.l2junity.gameserver.util.Util;

/**
 * Player Can Take Fort condition implementation.
 * @author Adry_85
 */
public class ConditionPlayerCanTakeFort extends Condition
{
	private final boolean _val;
	
	public ConditionPlayerCanTakeFort(boolean val)
	{
		_val = val;
	}
	
	@Override
	public boolean testImpl(Creature effector, Creature effected, Skill skill, L2Item item)
	{
		if ((effector == null) || !effector.isPlayer())
		{
			return !_val;
		}
		
		final PlayerInstance player = effector.getActingPlayer();
		boolean canTakeFort = true;
		if (player.isAlikeDead() || player.isCursedWeaponEquipped() || !player.isClanLeader())
		{
			canTakeFort = false;
		}
		
		final Fort fort = FortManager.getInstance().getFort(player);
		final SystemMessage sm;
		if ((fort == null) || (fort.getResidenceId() <= 0) || !fort.getSiege().isInProgress() || (fort.getSiege().getAttackerClan(player.getClan()) == null))
		{
			sm = SystemMessage.getSystemMessage(SystemMessageId.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS);
			sm.addSkillName(skill);
			player.sendPacket(sm);
			canTakeFort = false;
		}
		else if (fort.getFlagPole() != effected)
		{
			player.sendPacket(SystemMessageId.INVALID_TARGET);
			canTakeFort = false;
		}
		else if (!Util.checkIfInRange(200, player, effected, true))
		{
			player.sendPacket(SystemMessageId.THE_DISTANCE_IS_TOO_FAR_AND_SO_THE_CASTING_HAS_BEEN_STOPPED);
			canTakeFort = false;
		}
		return (_val == canTakeFort);
	}
}
