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
package org.l2junity.gameserver.network.client.send;

import java.util.LinkedList;
import java.util.List;

import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.quest.QuestState;
import org.l2junity.gameserver.network.client.OutgoingPackets;
import org.l2junity.network.PacketWriter;

public class QuestList implements IClientOutgoingPacket
{
	private final List<QuestState> _activeQuests;
	private final byte[] _oneTimeQuestMask;
	
	public QuestList(PlayerInstance player)
	{
		_activeQuests = new LinkedList<>();
		_oneTimeQuestMask = new byte[128];
		
		for (QuestState qs : player.getAllQuestStates())
		{
			final int questId = qs.getQuestId();
			if (questId > 0)
			{
				if (qs.isStarted())
				{
					_activeQuests.add(qs);
				}
				else if (qs.isCompleted() && !(((questId > 255) && (questId < 10256)) || (questId > 11023)))
				{
					_oneTimeQuestMask[(questId % 10000) / 8] |= 1 << (questId % 8);
				}
			}
		}
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.QUEST_LIST.writeId(packet);
		packet.writeH(_activeQuests.size());
		for (QuestState qs : _activeQuests)
		{
			packet.writeD(qs.getQuestId()); // Quest ID
			packet.writeD(qs.getRawCond()); // Quest state
		}
		packet.writeB(_oneTimeQuestMask);
		return true;
	}
}
