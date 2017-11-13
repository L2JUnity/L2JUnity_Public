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
package org.l2junity.gameserver.model.events.impl.character.npc;

import org.l2junity.gameserver.model.actor.Npc;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.events.EventType;
import org.l2junity.gameserver.model.events.impl.IBaseEvent;
import org.l2junity.gameserver.model.teleporter.TeleportHolder;
import org.l2junity.gameserver.model.teleporter.TeleportLocation;

/**
 * Player teleport request listner - called from {@link TeleportHolder#doTeleport(PlayerInstance, Npc, int)}
 * @author malyelfik
 */
public final class OnNpcTeleportRequest implements IBaseEvent
{
	private final PlayerInstance _player;
	private final Npc _npc;
	private final TeleportLocation _loc;
	
	public OnNpcTeleportRequest(PlayerInstance player, Npc npc, TeleportLocation loc)
	{
		_player = player;
		_npc = npc;
		_loc = loc;
	}
	
	public PlayerInstance getPlayer()
	{
		return _player;
	}
	
	public Npc getNpc()
	{
		return _npc;
	}
	
	public TeleportLocation getLocation()
	{
		return _loc;
	}
	
	@Override
	public EventType getType()
	{
		return EventType.ON_NPC_TELEPORT_REQUEST;
	}
}
