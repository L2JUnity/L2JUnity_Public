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
package org.l2junity.scripts.handlers.effecthandlers.instant;

import org.l2junity.gameserver.config.GeneralConfig;
import org.l2junity.gameserver.handler.EffectHandler;
import org.l2junity.gameserver.model.StatsSet;
import org.l2junity.gameserver.model.WorldObject;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.actor.request.ChangeAttributeRequest;
import org.l2junity.gameserver.model.effects.AbstractEffect;
import org.l2junity.gameserver.model.items.enchant.attribute.AttributeHolder;
import org.l2junity.gameserver.model.items.instance.ItemInstance;
import org.l2junity.gameserver.model.skills.Skill;
import org.l2junity.gameserver.network.client.send.InventoryUpdate;
import org.l2junity.gameserver.network.client.send.SystemMessage;
import org.l2junity.gameserver.network.client.send.changeattribute.ExChangeAttributeFail;
import org.l2junity.gameserver.network.client.send.changeattribute.ExChangeAttributeOk;
import org.l2junity.gameserver.network.client.send.string.SystemMessageId;
import org.l2junity.gameserver.scripting.annotations.SkillScript;

/**
 * @author Sdw
 */
public class InstantChangeAttribute extends AbstractEffect
{
	public InstantChangeAttribute(StatsSet params)
	{
		
	}
	
	@Override
	public void instant(Creature caster, WorldObject target, Skill skill, ItemInstance item)
	{
		final PlayerInstance casterPlayer = caster.asPlayer();
		if (casterPlayer == null)
		{
			return;
		}
		
		final ChangeAttributeRequest request = casterPlayer.getRequest(ChangeAttributeRequest.class);
		if ((request == null) || !request.isProcessing())
		{
			return;
		}
		final ItemInstance itemToChange = request.getChangeAttributeItem();
		final ItemInstance crystal = request.getChangeAttributeCrystal();
		if ((item == null) || (crystal == null))
		{
			casterPlayer.removeRequest(request.getClass());
			return;
		}
		
		final InventoryUpdate iu = new InventoryUpdate();
		synchronized (itemToChange)
		{
			// last validation check
			if ((itemToChange.getOwnerId() != casterPlayer.getObjectId()) || !itemToChange.isElementable() || !itemToChange.isWeapon())
			{
				casterPlayer.removeRequest(request.getClass());
				casterPlayer.sendPacket(ExChangeAttributeFail.STATIC_PACKET);
				return;
			}
			
			final AttributeHolder holder = itemToChange.getAttackAttribute();
			itemToChange.clearAttribute(holder.getType());
			itemToChange.setAttribute(new AttributeHolder(request.getAttributeType(), holder.getValue()), true);
			
			if (!GeneralConfig.FORCE_INVENTORY_UPDATE)
			{
				iu.addModifiedItem(itemToChange);
				casterPlayer.sendInventoryUpdate(iu);
			}
			else
			{
				casterPlayer.sendItemList(true);
			}
			
			request.setProcessing(false);
			casterPlayer.sendPacket(ExChangeAttributeOk.STATIC_PACKET);
			casterPlayer.removeRequest(request.getClass());
			final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_S_S2_ATTRIBUTE_HAS_SUCCESSFULLY_CHANGED_TO_S3_ATTRIBUTE);
			sm.addItemName(itemToChange);
			sm.addAttribute(holder.getType().getClientId());
			sm.addAttribute(request.getAttributeType().getClientId());
			casterPlayer.sendPacket(sm);
		}
	}
	
	@SkillScript
	public static void main()
	{
		EffectHandler.getInstance().registerHandler("i_change_attribute", InstantChangeAttribute::new);
	}
}
