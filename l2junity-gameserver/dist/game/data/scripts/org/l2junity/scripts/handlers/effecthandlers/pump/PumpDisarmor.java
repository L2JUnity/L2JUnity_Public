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
package org.l2junity.scripts.handlers.effecthandlers.pump;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.l2junity.gameserver.datatables.ItemTable;
import org.l2junity.gameserver.handler.EffectHandler;
import org.l2junity.gameserver.model.StatsSet;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.effects.AbstractEffect;
import org.l2junity.gameserver.model.items.L2Item;
import org.l2junity.gameserver.model.items.instance.ItemInstance;
import org.l2junity.gameserver.model.skills.Skill;
import org.l2junity.gameserver.network.client.send.InventoryUpdate;
import org.l2junity.gameserver.network.client.send.SystemMessage;
import org.l2junity.gameserver.network.client.send.string.SystemMessageId;
import org.l2junity.gameserver.scripting.annotations.SkillScript;

/**
 * Disarm by inventory slot effect implementation. At end of effect, it re-equips that item.
 * @author Nik
 */
public final class PumpDisarmor extends AbstractEffect
{
	private final Map<Integer, Integer> _unequippedItems; // PlayerObjId, ItemObjId
	private final int _slot;
	
	public PumpDisarmor(StatsSet params)
	{
		_unequippedItems = new ConcurrentHashMap<>();
		_slot = ItemTable._slots.get(params.getString("slot"));
	}
	
	@Override
	public boolean checkPumpCondition(Creature caster, Creature target, Skill skill)
	{
		return (_slot != L2Item.SLOT_NONE) && target.isPlayer();
	}
	
	@Override
	public void pumpStart(Creature caster, Creature target, Skill skill)
	{
		if (!target.isPlayer())
		{
			return;
		}
		
		PlayerInstance player = target.getActingPlayer();
		ItemInstance[] unequiped = player.getInventory().unEquipItemInBodySlotAndRecord(_slot);
		if (unequiped.length > 0)
		{
			InventoryUpdate iu = new InventoryUpdate();
			for (ItemInstance itm : unequiped)
			{
				iu.addModifiedItem(itm);
			}
			player.sendInventoryUpdate(iu);
			player.broadcastUserInfo();
			
			SystemMessage sm = null;
			if (unequiped[0].getEnchantLevel() > 0)
			{
				sm = SystemMessage.getSystemMessage(SystemMessageId.THE_EQUIPMENT_S1_S2_HAS_BEEN_REMOVED);
				sm.addInt(unequiped[0].getEnchantLevel());
				sm.addItemName(unequiped[0]);
			}
			else
			{
				sm = SystemMessage.getSystemMessage(SystemMessageId.S1_HAS_BEEN_UNEQUIPPED);
				sm.addItemName(unequiped[0]);
			}
			player.sendPacket(sm);
			target.getInventory().blockItemSlot(_slot);
			_unequippedItems.put(target.getObjectId(), unequiped[0].getObjectId());
		}
	}
	
	@Override
	public void pumpEnd(Creature caster, Creature target, Skill skill)
	{
		if (!target.isPlayer())
		{
			return;
		}
		
		Integer disarmedObjId = _unequippedItems.remove(target.getObjectId());
		if ((disarmedObjId != null) && (disarmedObjId > 0))
		{
			PlayerInstance player = target.getActingPlayer();
			target.getInventory().unblockItemSlot(_slot);
			
			ItemInstance disarmed = player.getInventory().getItemByObjectId(disarmedObjId);
			if (disarmed != null)
			{
				player.getInventory().equipItem(disarmed);
				InventoryUpdate iu = new InventoryUpdate();
				iu.addModifiedItem(disarmed);
				player.sendInventoryUpdate(iu);
				
				SystemMessage sm = null;
				if (disarmed.isEquipped())
				{
					if (disarmed.getEnchantLevel() > 0)
					{
						sm = SystemMessage.getSystemMessage(SystemMessageId.EQUIPPED_S1_S2);
						sm.addInt(disarmed.getEnchantLevel());
						sm.addItemName(disarmed);
					}
					else
					{
						sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_EQUIPPED_YOUR_S1);
						sm.addItemName(disarmed);
					}
					player.sendPacket(sm);
				}
			}
		}
	}
	
	@SkillScript
	public static void main()
	{
		EffectHandler.getInstance().registerHandler("p_disarmor", PumpDisarmor::new);
	}
}
