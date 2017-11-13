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
package org.l2junity.scripts.ai.individual.Other.GatekeeperOfAbyss;

import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.events.EventType;
import org.l2junity.gameserver.model.events.ListenerRegisterType;
import org.l2junity.gameserver.model.events.annotations.Id;
import org.l2junity.gameserver.model.events.annotations.RegisterEvent;
import org.l2junity.gameserver.model.events.annotations.RegisterType;
import org.l2junity.gameserver.model.events.impl.character.npc.OnNpcTeleportRequest;
import org.l2junity.gameserver.model.events.returns.TerminateReturn;
import org.l2junity.gameserver.network.client.send.string.SystemMessageId;
import org.l2junity.gameserver.scripting.annotations.GameScript;

import org.l2junity.scripts.ai.AbstractNpcAI;

/**
 * Gatekeeper of Abyss AI.
 * @author malyelfik
 */
public final class GatekeeperOfAbyss extends AbstractNpcAI
{
	@RegisterEvent(EventType.ON_NPC_TELEPORT_REQUEST)
	@RegisterType(ListenerRegisterType.NPC)
	@Id(32539)
	public TerminateReturn onPlayerTeleportRequest(OnNpcTeleportRequest evt)
	{
		final PlayerInstance player = evt.getPlayer();
		if (player.isFlyingMounted())
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_ENTER_A_SEED_WHILE_IN_A_FLYING_TRANSFORMATION_STATE);
			return new TerminateReturn(true, true, true);
		}
		return null;
	}
	
	@GameScript
	public static void main()
	{
		new GatekeeperOfAbyss();
	}
}
