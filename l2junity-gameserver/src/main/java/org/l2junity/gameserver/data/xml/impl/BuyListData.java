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

import java.nio.file.DirectoryStream.Filter;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.l2junity.commons.loader.annotations.Dependency;
import org.l2junity.commons.loader.annotations.InstanceGetter;
import org.l2junity.commons.loader.annotations.Load;
import org.l2junity.commons.loader.annotations.Reload;
import org.l2junity.commons.sql.DatabaseFactory;
import org.l2junity.gameserver.config.GeneralConfig;
import org.l2junity.gameserver.data.xml.IGameXmlReader;
import org.l2junity.gameserver.datatables.ItemTable;
import org.l2junity.gameserver.loader.LoadGroup;
import org.l2junity.gameserver.model.buylist.Product;
import org.l2junity.gameserver.model.buylist.ProductList;
import org.l2junity.gameserver.model.items.L2Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;

/**
 * Loads buy lists for NPCs.
 * @author NosBit
 */
public final class BuyListData implements IGameXmlReader
{
	private static final Logger LOGGER = LoggerFactory.getLogger(BuyListData.class);
	
	private final Map<Integer, ProductList> _buyLists = new HashMap<>();
	
	protected BuyListData()
	{
	}
	
	@Reload("buylist")
	@Load(group = LoadGroup.class, dependencies = @Dependency(clazz = ItemTable.class))
	private void load() throws Exception
	{
		_buyLists.clear();
		parseDatapackDirectory("data/buylists", false);
		if (GeneralConfig.CUSTOM_BUYLIST_LOAD)
		{
			parseDatapackDirectory("data/buylists/custom", false);
		}
		
		LOGGER.info("Loaded {} BuyLists.", _buyLists.size());
		
		try (Connection con = DatabaseFactory.getInstance().getConnection();
			Statement statement = con.createStatement();
			ResultSet rs = statement.executeQuery("SELECT * FROM `buylists`"))
		{
			while (rs.next())
			{
				int buyListId = rs.getInt("buylist_id");
				int itemId = rs.getInt("item_id");
				long count = rs.getLong("count");
				long nextRestockTime = rs.getLong("next_restock_time");
				final ProductList buyList = getBuyList(buyListId);
				if (buyList == null)
				{
					LOGGER.warn("BuyList found in database but not loaded from xml! BuyListId: " + buyListId);
					continue;
				}
				final Product product = buyList.getProductByItemId(itemId);
				if (product == null)
				{
					LOGGER.warn("ItemId found in database but not loaded from xml! BuyListId: " + buyListId + " ItemId: " + itemId);
					continue;
				}
				if (count < product.getMaxCount())
				{
					product.setCount(count);
					product.restartRestockTask(nextRestockTime);
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.warn("Failed to load buyList data from database.", e);
		}
	}
	
	@Override
	public void parseDocument(Document doc, Path path)
	{
		try
		{
			final int buyListId = Integer.parseInt(path.getFileName().toString().replaceAll(".xml", ""));
			forEach(doc, "list", (list) ->
			{
				final int defaultBaseTax = parseInteger(list.getAttributes(), "baseTax", 0);
				final ProductList buyList = new ProductList(buyListId);
				forEach(list, (node) ->
				{
					switch (node.getNodeName())
					{
						case "item":
						{
							final NamedNodeMap attrs = node.getAttributes();
							
							final int itemId = parseInteger(attrs, "id");
							final L2Item item = ItemTable.getInstance().getTemplate(itemId);
							if (item != null)
							{
								final long price = parseLong(attrs, "price", -1L);
								final long restockDelay = parseLong(attrs, "restock_delay", -1L);
								final long count = parseLong(attrs, "count", -1L);
								final int baseTax = parseInteger(attrs, "baseTax", defaultBaseTax);
								
								buyList.addProduct(new Product(buyListId, item, price, restockDelay, count, baseTax));
							}
							else
							{
								LOGGER.warn("Item not found. BuyList:" + buyListId + " ItemID:" + itemId + " File:" + path);
							}
							break;
						}
						case "npcs":
						{
							forEach(node, "npc", (npcNode) -> buyList.addAllowedNpc(Integer.parseInt(npcNode.getTextContent())));
							break;
						}
					}
				});
				_buyLists.put(buyListId, buyList);
			});
		}
		catch (Exception e)
		{
			LOGGER.warn("Failed to load buyList data from xml File:" + path, e);
		}
	}
	
	public int getLoadedElementsCount()
	{
		return _buyLists.size();
	}
	
	@Override
	public Filter<Path> getCurrentFileFilter()
	{
		return NUMERIC_XML_FILTER;
	}
	
	public ProductList getBuyList(int listId)
	{
		return _buyLists.get(listId);
	}
	
	@InstanceGetter
	public static BuyListData getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final BuyListData _instance = new BuyListData();
	}
}
