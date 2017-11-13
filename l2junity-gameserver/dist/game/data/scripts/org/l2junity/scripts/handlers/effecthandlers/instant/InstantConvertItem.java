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

import java.util.Collection;

import org.l2junity.gameserver.data.xml.impl.ConvertData;
import org.l2junity.gameserver.handler.EffectHandler;
import org.l2junity.gameserver.model.StatsSet;
import org.l2junity.gameserver.model.VariationInstance;
import org.l2junity.gameserver.model.WorldObject;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.effects.AbstractEffect;
import org.l2junity.gameserver.model.ensoul.EnsoulOption;
import org.l2junity.gameserver.model.items.Weapon;
import org.l2junity.gameserver.model.items.enchant.attribute.AttributeHolder;
import org.l2junity.gameserver.model.items.instance.ItemInstance;
import org.l2junity.gameserver.model.skills.Skill;
import org.l2junity.gameserver.network.client.send.InventoryUpdate;
import org.l2junity.gameserver.network.client.send.SystemMessage;
import org.l2junity.gameserver.network.client.send.string.SystemMessageId;
import org.l2junity.gameserver.scripting.annotations.SkillScript;

/**
 * Convert Item effect implementation.
 * @author Zoey76
 */
public final class InstantConvertItem extends AbstractEffect
{
	public InstantConvertItem(StatsSet params)
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
		
		if (targetPlayer.isAlikeDead())
		{
			return;
		}

		if (targetPlayer.hasItemRequest())
		{
			return;
		}
		
		final Weapon weaponItem = targetPlayer.getActiveWeaponItem();
		if (weaponItem == null)
		{
			return;
		}
		
		final ItemInstance wpn = targetPlayer.getActiveWeaponInstance();
		
		if (wpn == null)
		{
			return;
		}
		
		final int newItemId = ConvertData.getInstance().getConversionId(wpn.getId());
		if (newItemId == 0)
		{
			return;
		}
		
		final int enchantLevel = wpn.getEnchantLevel();
		final AttributeHolder elementals = wpn.getAttributes() == null ? null : wpn.getAttackAttribute();
		final VariationInstance augmentation = wpn.getAugmentation();
		final Collection<EnsoulOption> specialAbilities = wpn.getSpecialAbilities();
		final Collection<EnsoulOption> specialAbilitiesAdditional = wpn.getAdditionalSpecialAbilities();
		final ItemInstance[] unequiped = targetPlayer.getInventory().unEquipItemInBodySlotAndRecord(wpn.getItem().getBodyPart());
		final InventoryUpdate iu = new InventoryUpdate();
		for (ItemInstance unequippedItem : unequiped)
		{
			iu.addModifiedItem(unequippedItem);
		}
		targetPlayer.sendInventoryUpdate(iu);
		
		if (unequiped.length <= 0)
		{
			return;
		}
		byte count = 0;
		for (ItemInstance unequippedItem : unequiped)
		{
			if (!(unequippedItem.getItem() instanceof Weapon))
			{
				count++;
				continue;
			}
			
			final SystemMessage sm;
			if (unequippedItem.getEnchantLevel() > 0)
			{
				sm = SystemMessage.getSystemMessage(SystemMessageId.THE_EQUIPMENT_S1_S2_HAS_BEEN_REMOVED);
				sm.addInt(unequippedItem.getEnchantLevel());
				sm.addItemName(unequippedItem);
			}
			else
			{
				sm = SystemMessage.getSystemMessage(SystemMessageId.S1_HAS_BEEN_UNEQUIPPED);
				sm.addItemName(unequippedItem);
			}
			targetPlayer.sendPacket(sm);
		}
		
		if (count == unequiped.length)
		{
			return;
		}
		
		final ItemInstance destroyItem = targetPlayer.getInventory().destroyItem("ChangeWeapon", wpn, targetPlayer, null);
		if (destroyItem == null)
		{
			return;
		}
		
		final ItemInstance newItem = targetPlayer.getInventory().addItem("ChangeWeapon", newItemId, 1, targetPlayer, destroyItem);
		if (newItem == null)
		{
			return;
		}
		
		// Add elementals.
		if (elementals != null)
		{
			newItem.setAttribute(elementals, true);
		}
		
		// Add augmentation.
		if (augmentation != null)
		{
			newItem.setAugmentation(augmentation, true);
		}
		
		// Add special abilities.
		int i = 1;
		for (EnsoulOption ensoul : specialAbilities)
		{
			newItem.addSpecialAbility(ensoul, i++, 1, true);
		}
		i = 1;
		for (EnsoulOption ensoul : specialAbilitiesAdditional)
		{
			newItem.addSpecialAbility(ensoul, i++, 2, true);
		}
		newItem.setEnchantLevel(enchantLevel);
		targetPlayer.getInventory().equipItem(newItem);
		
		final SystemMessage msg;
		if (newItem.getEnchantLevel() > 0)
		{
			msg = SystemMessage.getSystemMessage(SystemMessageId.EQUIPPED_S1_S2);
			msg.addInt(newItem.getEnchantLevel());
			msg.addItemName(newItem);
		}
		else
		{
			msg = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_EQUIPPED_YOUR_S1);
			msg.addItemName(newItem);
		}
		targetPlayer.sendPacket(msg);
		
		final InventoryUpdate u = new InventoryUpdate();
		u.addRemovedItem(destroyItem);
		u.addItem(newItem);
		targetPlayer.sendInventoryUpdate(u);

		targetPlayer.broadcastUserInfo();
	}

	@SkillScript
	public static void main()
	{
		EffectHandler.getInstance().registerHandler("i_convert_item", InstantConvertItem::new);
	}
}
