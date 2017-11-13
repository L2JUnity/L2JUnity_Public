/*
 * Copyright (C) 2004-2017 L2J Unity
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
package org.l2junity.gameserver.network.client.send.custom;

import java.util.Set;

import org.l2junity.gameserver.data.sql.impl.ClanTable;
import org.l2junity.gameserver.data.xml.impl.NpcData;
import org.l2junity.gameserver.enums.NpcInfoType;
import org.l2junity.gameserver.enums.Team;
import org.l2junity.gameserver.model.L2Clan;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.actor.poly.ObjectPoly;
import org.l2junity.gameserver.model.itemcontainer.Inventory;
import org.l2junity.gameserver.model.skills.AbnormalVisualEffect;
import org.l2junity.gameserver.model.stats.DoubleStat;
import org.l2junity.gameserver.model.zone.ZoneId;
import org.l2junity.gameserver.network.client.OutgoingPackets;
import org.l2junity.gameserver.network.client.send.AbstractMaskPacket;
import org.l2junity.network.PacketWriter;

/**
 * @author Sdw
 */
public class PlayerPolymorphedNpcInfo extends AbstractMaskPacket<NpcInfoType>
{
	private final PlayerInstance _player;
	private final ObjectPoly _poly;
	private final byte[] _masks = new byte[]
	{
		(byte) 0x00,
		(byte) 0x0C,
		(byte) 0x0C,
		(byte) 0x00,
		(byte) 0x00
	};
	
	private int _initSize = 0;
	private int _blockSize = 0;
	
	private int _clanCrest = 0;
	private int _clanLargeCrest = 0;
	private int _allyCrest = 0;
	private int _allyId = 0;
	private int _clanId = 0;
	private int _statusMask = 0;
	private final Set<AbnormalVisualEffect> _abnormalVisualEffects;
	private String _title;
	
	private final double _speedMultiplier;
	
	public PlayerPolymorphedNpcInfo(PlayerInstance player)
	{
		_player = player;
		_poly = player.getPoly();
		_abnormalVisualEffects = player.getEffectList().getCurrentAbnormalVisualEffects();
		
		if (_player.isInvisible())
		{
			_title = "Invisible";
		}
		else
		{
			_title = player.getTitle();
		}
		
		addComponentType(NpcInfoType.ATTACKABLE, NpcInfoType.UNKNOWN1, NpcInfoType.TITLE, NpcInfoType.ID, NpcInfoType.POSITION, NpcInfoType.ALIVE, NpcInfoType.RUNNING, NpcInfoType.HEADING, NpcInfoType.EQUIPPED, NpcInfoType.NAME);
		
		if ((player.getStat().getPAtkSpd() > 0) || (player.getStat().getMAtkSpd() > 0))
		{
			addComponentType(NpcInfoType.ATK_CAST_SPEED);
		}
		
		if (player.getRunSpeed() > 0)
		{
			addComponentType(NpcInfoType.SPEED_MULTIPLIER);
		}
		
		if (player.getTeam() != Team.NONE)
		{
			addComponentType(NpcInfoType.TEAM);
		}
		
		if (player.isInsideZone(ZoneId.WATER) || player.isFlying())
		{
			addComponentType(NpcInfoType.SWIM_OR_FLY);
		}
		
		if (player.isFlying())
		{
			addComponentType(NpcInfoType.FLYING);
		}
		
		if (player.getMaxHp() > 0)
		{
			addComponentType(NpcInfoType.MAX_HP);
		}
		
		if (player.getMaxMp() > 0)
		{
			addComponentType(NpcInfoType.MAX_MP);
		}
		
		if (player.getCurrentHp() <= player.getMaxHp())
		{
			addComponentType(NpcInfoType.CURRENT_HP);
		}
		
		if (player.getCurrentMp() <= player.getMaxMp())
		{
			addComponentType(NpcInfoType.CURRENT_MP);
		}
		
		if (_player.getReputation() != 0)
		{
			addComponentType(NpcInfoType.REPUTATION);
		}
		
		if (!_abnormalVisualEffects.isEmpty() || player.isInvisible())
		{
			addComponentType(NpcInfoType.ABNORMALS);
		}
		
		if (player.getEnchantEffect() > 0)
		{
			addComponentType(NpcInfoType.ENCHANT);
		}
		
		if (player.getTransformationDisplayId() > 0)
		{
			addComponentType(NpcInfoType.TRANSFORMATION);
		}
		
		if (player.isShowSummonAnimation())
		{
			addComponentType(NpcInfoType.SUMMONED);
		}
		
		if (player.getClanId() > 0)
		{
			L2Clan clan = ClanTable.getInstance().getClan(player.getClanId());
			if (clan != null)
			{
				_clanId = clan.getId();
				_clanCrest = clan.getCrestId();
				_clanLargeCrest = clan.getCrestLargeId();
				_allyCrest = clan.getAllyCrestId();
				_allyId = clan.getAllyId();
				
				addComponentType(NpcInfoType.CLAN);
			}
		}
		
		addComponentType(NpcInfoType.UNKNOWN8);
		
		if (player.getPvpFlag() > 0)
		{
			addComponentType(NpcInfoType.PVP_FLAG);
		}
		
		// TODO: Confirm me
		if (player.isInCombat())
		{
			_statusMask |= 0x01;
		}
		if (player.isDead())
		{
			_statusMask |= 0x02;
		}
		if (player.isTargetable())
		{
			_statusMask |= 0x04;
		}
		
		_statusMask |= 0x08;
		
		if (_statusMask != 0)
		{
			addComponentType(NpcInfoType.VISUAL_STATE);
		}
		
		_speedMultiplier = ((player.isRunning() ? player.getRunSpeed() : player.getWalkSpeed()) / player.getMovementSpeedMultiplier()) / NpcData.getInstance().getTemplate(_poly.getPolyId()).getBaseValue(player.isRunning() ? DoubleStat.RUN_SPEED : DoubleStat.WALK_SPEED, 0);
	}
	
	@Override
	protected byte[] getMasks()
	{
		return _masks;
	}
	
	@Override
	protected void onNewMaskAdded(NpcInfoType component)
	{
		calcBlockSize(_player, component);
	}
	
	private void calcBlockSize(PlayerInstance player, NpcInfoType type)
	{
		switch (type)
		{
			case ATTACKABLE:
			case UNKNOWN1:
			{
				_initSize += type.getBlockLength();
				break;
			}
			case TITLE:
			{
				_initSize += type.getBlockLength() + (_title.length() * 2);
				break;
			}
			case NAME:
			{
				_blockSize += type.getBlockLength() + (player.getName().length() * 2);
				break;
			}
			default:
			{
				_blockSize += type.getBlockLength();
				break;
			}
		}
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.NPC_INFO.writeId(packet);
		
		packet.writeD(_player.getObjectId());
		packet.writeC(_player.isShowSummonAnimation() ? 0x02 : 0x00); // // 0=teleported 1=default 2=summoned
		packet.writeH(37); // mask_bits_37
		packet.writeB(_masks);
		
		// Block 1
		packet.writeC(_initSize);
		
		if (containsMask(NpcInfoType.ATTACKABLE))
		{
			packet.writeC(_player.isAttackable() ? 0x01 : 0x00);
		}
		if (containsMask(NpcInfoType.UNKNOWN1))
		{
			packet.writeD(0x00); // unknown
		}
		if (containsMask(NpcInfoType.TITLE))
		{
			packet.writeS(_title);
		}
		
		// Block 2
		packet.writeH(_blockSize);
		if (containsMask(NpcInfoType.ID))
		{
			packet.writeD(_poly.getPolyId() + 1000000);
		}
		if (containsMask(NpcInfoType.POSITION))
		{
			packet.writeD((int) _player.getX());
			packet.writeD((int) _player.getY());
			packet.writeD((int) _player.getZ());
		}
		if (containsMask(NpcInfoType.HEADING))
		{
			packet.writeD(_player.getHeading());
		}
		if (containsMask(NpcInfoType.UNKNOWN2))
		{
			packet.writeD(0x00); // Unknown
		}
		if (containsMask(NpcInfoType.ATK_CAST_SPEED))
		{
			packet.writeD(_player.getPAtkSpd());
			packet.writeD(_player.getMAtkSpd());
		}
		if (containsMask(NpcInfoType.SPEED_MULTIPLIER))
		{
			packet.writeE((float) _speedMultiplier);
			packet.writeE((float) _player.getStat().getAttackSpeedMultiplier());
		}
		if (containsMask(NpcInfoType.EQUIPPED))
		{
			packet.writeD(_player.getInventory().getPaperdollItemDisplayId(Inventory.PAPERDOLL_RHAND));
			packet.writeD(0x00); // Armor id?
			packet.writeD(_player.getInventory().getPaperdollItemDisplayId(Inventory.PAPERDOLL_LHAND));
		}
		if (containsMask(NpcInfoType.ALIVE))
		{
			packet.writeC(_player.isDead() ? 0x00 : 0x01);
		}
		if (containsMask(NpcInfoType.RUNNING))
		{
			packet.writeC(_player.isRunning() ? 0x01 : 0x00);
		}
		if (containsMask(NpcInfoType.SWIM_OR_FLY))
		{
			packet.writeC(_player.isInsideZone(ZoneId.WATER) ? 0x01 : _player.isFlying() ? 0x02 : 0x00);
		}
		if (containsMask(NpcInfoType.TEAM))
		{
			packet.writeC(_player.getTeam().getId());
		}
		if (containsMask(NpcInfoType.ENCHANT))
		{
			packet.writeD(_player.getEnchantEffect());
		}
		if (containsMask(NpcInfoType.FLYING))
		{
			packet.writeD(_player.isFlying() ? 0x01 : 00);
		}
		if (containsMask(NpcInfoType.CLONE))
		{
			packet.writeD(0x00); // Player ObjectId with Decoy
		}
		if (containsMask(NpcInfoType.UNKNOWN8))
		{
			// No visual effect
			packet.writeD(0x00); // Unknown
		}
		if (containsMask(NpcInfoType.DISPLAY_EFFECT))
		{
			packet.writeD(0x00);
		}
		if (containsMask(NpcInfoType.TRANSFORMATION))
		{
			packet.writeD(_player.getTransformationDisplayId()); // Transformation ID
		}
		if (containsMask(NpcInfoType.CURRENT_HP))
		{
			packet.writeD((int) _player.getCurrentHp());
		}
		if (containsMask(NpcInfoType.CURRENT_MP))
		{
			packet.writeD((int) _player.getCurrentMp());
		}
		if (containsMask(NpcInfoType.MAX_HP))
		{
			packet.writeD(_player.getMaxHp());
		}
		if (containsMask(NpcInfoType.MAX_MP))
		{
			packet.writeD(_player.getMaxMp());
		}
		if (containsMask(NpcInfoType.SUMMONED))
		{
			packet.writeC(0x00); // 2 - do some animation on spawn
		}
		if (containsMask(NpcInfoType.UNKNOWN12))
		{
			packet.writeD(0x00);
			packet.writeD(0x00);
		}
		if (containsMask(NpcInfoType.NAME))
		{
			packet.writeS(_player.getName());
		}
		if (containsMask(NpcInfoType.NAME_NPCSTRINGID))
		{
			packet.writeD(-1); // NPCStringId for name
		}
		if (containsMask(NpcInfoType.TITLE_NPCSTRINGID))
		{
			packet.writeD(-1); // NPCStringId for title
		}
		if (containsMask(NpcInfoType.PVP_FLAG))
		{
			packet.writeC(_player.getPvpFlag()); // PVP flag
		}
		if (containsMask(NpcInfoType.REPUTATION))
		{
			packet.writeD(_player.getReputation()); // Reputation
		}
		if (containsMask(NpcInfoType.CLAN))
		{
			packet.writeD(_clanId);
			packet.writeD(_clanCrest);
			packet.writeD(_clanLargeCrest);
			packet.writeD(_allyId);
			packet.writeD(_allyCrest);
		}
		
		if (containsMask(NpcInfoType.VISUAL_STATE))
		{
			packet.writeC(_statusMask);
		}
		
		if (containsMask(NpcInfoType.ABNORMALS))
		{
			packet.writeH(_abnormalVisualEffects.size() + (_player.isInvisible() ? 1 : 0));
			for (AbnormalVisualEffect abnormalVisualEffect : _abnormalVisualEffects)
			{
				packet.writeH(abnormalVisualEffect.getClientId());
			}
			if (_player.isInvisible())
			{
				packet.writeH(AbnormalVisualEffect.STEALTH.getClientId());
			}
		}
		return true;
	}
}