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
package org.l2junity.scripts.handlers.effecthandlers.instant;

import org.l2junity.gameserver.enums.AttributeType;
import org.l2junity.gameserver.handler.EffectHandler;
import org.l2junity.gameserver.model.StatsSet;
import org.l2junity.gameserver.model.WorldObject;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.actor.request.EnchantItemAttributeRequest;
import org.l2junity.gameserver.model.effects.AbstractEffect;
import org.l2junity.gameserver.model.items.instance.ItemInstance;
import org.l2junity.gameserver.model.skills.Skill;
import org.l2junity.gameserver.network.client.send.ExChooseInventoryAttributeItem;
import org.l2junity.gameserver.scripting.annotations.SkillScript;

/**
 * @author Sdw
 */
public class InstantEnchantAttribute extends AbstractEffect
{
	final AttributeType _weaponAttribute;
	final AttributeType _armorAttribute;
	final int _maxLevel;
	final int _minValue;
	final int _maxValue;
	
	public InstantEnchantAttribute(StatsSet params)
	{
		_weaponAttribute = params.getEnum("weaponAttribute", AttributeType.class);
		_armorAttribute = params.getEnum("armorAttribute", AttributeType.class);
		_maxLevel = params.getInt("maxLevel");
		_minValue = params.getInt("minValue", 0);
		_maxValue = params.getInt("maxValue", 0);
	}
	
	@Override
	public void instant(Creature caster, WorldObject target, Skill skill, ItemInstance item)
	{
		final PlayerInstance casterPlayer = caster.asPlayer();
		if (casterPlayer == null)
		{
			return;
		}
		
		casterPlayer.addRequest(new EnchantItemAttributeRequest(casterPlayer, item.getObjectId(), _weaponAttribute, _armorAttribute, _maxLevel, _minValue, _maxValue));
		casterPlayer.sendPacket(new ExChooseInventoryAttributeItem(casterPlayer, item));
	}

	@SkillScript
	public static void main()
	{
		EffectHandler.getInstance().registerHandler("i_enchant_attribute", InstantEnchantAttribute::new);
	}
}
