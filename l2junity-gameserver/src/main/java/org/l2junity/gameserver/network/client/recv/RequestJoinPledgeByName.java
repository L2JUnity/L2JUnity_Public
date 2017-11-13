/*
 * Copyright (C) 2004-2016 L2J Unity
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
package org.l2junity.gameserver.network.client.recv;

import org.l2junity.gameserver.model.L2Clan;
import org.l2junity.gameserver.model.World;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.actor.request.ClanInvitationRequest;
import org.l2junity.gameserver.model.events.EventDispatcher;
import org.l2junity.gameserver.model.events.impl.restriction.CanPlayerInviteToClan;
import org.l2junity.gameserver.model.events.returns.BooleanReturn;
import org.l2junity.gameserver.network.client.L2GameClient;
import org.l2junity.gameserver.network.client.send.AskJoinPledge;
import org.l2junity.gameserver.network.client.send.string.SystemMessageId;
import org.l2junity.network.PacketReader;

/**
 * @author Sdw
 */
public class RequestJoinPledgeByName implements IClientIncomingPacket
{
	private String _charName;
	private int _pledgeType;
	
	@Override
	public boolean read(L2GameClient client, PacketReader packet)
	{
		_charName = packet.readS();
		_pledgeType = packet.readD();
		return true;
	}
	
	@Override
	public void run(L2GameClient client)
	{
		final PlayerInstance activeChar = client.getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		final L2Clan clan = activeChar.getClan();
		if (clan == null)
		{
			return;
		}
		
		final PlayerInstance target = World.getInstance().getPlayer(_charName);
		if (target == null)
		{
			return;
		}
		
		final BooleanReturn term = EventDispatcher.getInstance().notifyEvent(new CanPlayerInviteToClan(activeChar, target), activeChar, BooleanReturn.class);
		if ((term != null) && !term.getValue())
		{
			activeChar.sendPacket(SystemMessageId.YOU_HAVE_INVITED_THE_WRONG_TARGET);
			return;
		}
		
		if (!clan.checkClanJoinCondition(activeChar, target, _pledgeType))
		{
			return;
		}
		
		final ClanInvitationRequest request = new ClanInvitationRequest(activeChar, target, _pledgeType);
		request.scheduleTimeout(30 * 1000);
		activeChar.addRequest(request);
		target.addRequest(request);
		
		final String pledgeName = activeChar.getClan().getName();
		final String subPledgeName = (activeChar.getClan().getSubPledge(_pledgeType) != null ? activeChar.getClan().getSubPledge(_pledgeType).getName() : null);
		target.sendPacket(new AskJoinPledge(activeChar.getObjectId(), subPledgeName, _pledgeType, pledgeName));
	}
}