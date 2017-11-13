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
package org.l2junity.gameserver.data.xml.impl;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.l2junity.commons.loader.annotations.Dependency;
import org.l2junity.commons.loader.annotations.InstanceGetter;
import org.l2junity.commons.loader.annotations.Load;
import org.l2junity.gameserver.data.xml.IGameXmlReader;
import org.l2junity.gameserver.datatables.ItemTable;
import org.l2junity.gameserver.loader.LoadGroup;
import org.l2junity.gameserver.model.items.L2Item;
import org.l2junity.gameserver.model.items.instance.ItemInstance;
import org.l2junity.gameserver.model.items.type.CrystalType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

/**
 * This class holds the Enchant HP Bonus Data.
 * @author MrPoke, Zoey76
 */
public class EnchantItemHPBonusData implements IGameXmlReader
{
	private static final Logger LOGGER = LoggerFactory.getLogger(EnchantItemHPBonusData.class);
	
	private static float FULL_ARMOR_MODIFIER = 1.5f;
	
	private final Map<CrystalType, List<Integer>> _armorHPBonuses = new EnumMap<>(CrystalType.class);
	
	/**
	 * Instantiates a new enchant hp bonus data.
	 */
	protected EnchantItemHPBonusData()
	{
	}
	
	@Load(group = LoadGroup.class, dependencies = @Dependency(clazz = ItemTable.class))
	protected void load() throws Exception
	{
		_armorHPBonuses.clear();
		parseDatapackFile("data/stats/enchantHPBonus.xml");
		LOGGER.info("Loaded {} Enchant HP Bonuses.", _armorHPBonuses.size());
	}
	
	@Override
	public void parseDocument(Document doc, Path path)
	{
		forEach(doc, "list", listNode ->
		{
			forEach(listNode, "onePieceFactor", onePieceFactorNode ->
			{
				FULL_ARMOR_MODIFIER = Integer.parseInt(onePieceFactorNode.getTextContent()) / 100f;
			});
			forEach(listNode, "enchantHP", enchantHPNode ->
			{
				final List<Integer> bonuses = new ArrayList<>(12);
				
				forEach(enchantHPNode, "bonus", bonusNode ->
				{
					bonuses.add(Integer.parseInt(bonusNode.getTextContent()));
				});
				_armorHPBonuses.put(parseEnum(enchantHPNode.getAttributes(), CrystalType.class, "grade"), bonuses);
			});
		});
	}
	
	public int getLoadedElementsCount()
	{
		return _armorHPBonuses.size();
	}
	
	/**
	 * Gets the HP bonus.
	 * @param item the item
	 * @return the HP bonus
	 */
	public final int getHPBonus(ItemInstance item)
	{
		if ((item.getOlyEnchantLevel() <= 0) || (item.getItem().getType2() != L2Item.TYPE2_SHIELD_ARMOR))
		{
			return 0;
		}
		
		final List<Integer> values = _armorHPBonuses.get(item.getItem().getCrystalTypePlus());
		if ((values == null) || values.isEmpty())
		{
			return 0;
		}
		
		final int bonus = values.get(Math.min(item.getOlyEnchantLevel(), values.size()) - 1);
		if (item.getItem().getBodyPart() == L2Item.SLOT_FULL_ARMOR)
		{
			return (int) (bonus * FULL_ARMOR_MODIFIER);
		}
		return bonus;
	}
	
	/**
	 * Gets the single instance of EnchantHPBonusData.
	 * @return single instance of EnchantHPBonusData
	 */
	@InstanceGetter
	public static final EnchantItemHPBonusData getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final EnchantItemHPBonusData _instance = new EnchantItemHPBonusData();
	}
}
