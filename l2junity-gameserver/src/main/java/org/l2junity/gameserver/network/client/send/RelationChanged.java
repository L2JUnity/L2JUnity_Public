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

import org.l2junity.gameserver.model.actor.Playable;
import org.l2junity.gameserver.network.client.OutgoingPackets;
import org.l2junity.network.PacketWriter;

/**
 * @author Luca Baldi
 */
public final class RelationChanged implements IClientOutgoingPacket
{
	// Masks
	public static final byte SEND_ONE = (byte) 0x00;
	public static final byte SEND_DEFAULT = (byte) 0x01;
	public static final byte SEND_MULTI = (byte) 0x04;
	
	protected static class Relation
	{
		int _objId, _relation, _autoAttackable, _reputation, _pvpFlag;
	}
	
	private Relation _singled;
	private final List<Relation> _multi;
	private byte _mask = (byte) 0x00;
	
	public RelationChanged(Playable activeChar, int relation, boolean autoattackable)
	{
		_mask |= SEND_ONE;
		
		_singled = new Relation();
		_singled._objId = activeChar.getObjectId();
		_singled._relation = relation;
		_singled._autoAttackable = autoattackable ? 1 : 0;
		_singled._reputation = activeChar.getReputation();
		_singled._pvpFlag = activeChar.getPvpFlag();
		_multi = null;
	}
	
	public RelationChanged()
	{
		_mask |= SEND_MULTI;
		_multi = new LinkedList<>();
	}
	
	public void addRelation(Playable activeChar, int relation, boolean autoattackable)
	{
		Relation r = new Relation();
		r._objId = activeChar.getObjectId();
		r._relation = relation;
		r._autoAttackable = autoattackable ? 1 : 0;
		r._reputation = activeChar.getReputation();
		r._pvpFlag = activeChar.getPvpFlag();
		_multi.add(r);
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.RELATION_CHANGED.writeId(packet);
		
		packet.writeC(_mask);
		if (_multi == null)
		{
			writeRelation(packet, _singled);
		}
		else
		{
			packet.writeH(_multi.size());
			for (Relation r : _multi)
			{
				writeRelation(packet, r);
			}
		}
		return true;
	}
	
	private void writeRelation(PacketWriter packet, Relation relation)
	{
		packet.writeD(relation._objId);
		
		if ((_mask & SEND_DEFAULT) == 0)
		{
			packet.writeD(relation._relation);
			packet.writeC(relation._autoAttackable);
			packet.writeD(relation._reputation);
			packet.writeC(relation._pvpFlag);
		}
	}
}
