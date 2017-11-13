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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.l2junity.commons.loader.annotations.Dependency;
import org.l2junity.commons.loader.annotations.InstanceGetter;
import org.l2junity.commons.loader.annotations.Load;
import org.l2junity.commons.loader.annotations.Reload;
import org.l2junity.gameserver.config.GeneralConfig;
import org.l2junity.gameserver.data.xml.IGameXmlReader;
import org.l2junity.gameserver.datatables.ItemTable;
import org.l2junity.gameserver.enums.SpecialItemType;
import org.l2junity.gameserver.loader.LoadGroup;
import org.l2junity.gameserver.model.StatsSet;
import org.l2junity.gameserver.model.actor.Npc;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.holders.ItemChanceHolder;
import org.l2junity.gameserver.model.holders.ItemHolder;
import org.l2junity.gameserver.model.holders.MultisellEntryHolder;
import org.l2junity.gameserver.model.holders.MultisellListHolder;
import org.l2junity.gameserver.model.holders.PreparedMultisellListHolder;
import org.l2junity.gameserver.model.items.L2Item;
import org.l2junity.gameserver.network.client.send.MultiSellList;
import org.l2junity.gameserver.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public final class MultisellData implements IGameXmlReader
{
	private static final Logger LOGGER = LoggerFactory.getLogger(MultisellData.class);
	
	public static final int PAGE_SIZE = 40;
	
	private final Map<Integer, MultisellListHolder> _multisells = new HashMap<>();
	
	protected MultisellData()
	{
	}
	
	@Reload("multisell")
	@Load(group = LoadGroup.class, dependencies = @Dependency(clazz = ItemTable.class))
	private void load() throws Exception
	{
		_multisells.clear();
		parseDatapackDirectory("data/multisell", false);
		if (GeneralConfig.CUSTOM_MULTISELL_LOAD)
		{
			parseDatapackDirectory("data/multisell/custom", false);
		}
		
		LOGGER.info("Loaded {} multisell lists.", _multisells.size());
	}
	
	@Override
	public void parseDocument(Document doc, Path path)
	{
		try
		{
			forEach(doc, "list", listNode ->
			{
				final StatsSet set = new StatsSet(parseAttributes(listNode));
				final String filename = path.getFileName().toString();
				final int listId = Integer.parseInt(filename.substring(0, filename.length() - 4));
				final List<MultisellEntryHolder> entries = new ArrayList<>(listNode.getChildNodes().getLength());
				
				forEach(listNode, itemNode ->
				{
					if ("item".equalsIgnoreCase(itemNode.getNodeName()))
					{
						final List<ItemHolder> ingredients = new ArrayList<>(1);
						final List<ItemChanceHolder> products = new ArrayList<>(1);
						final MultisellEntryHolder entry = new MultisellEntryHolder(ingredients, products);
						
						for (Node d = itemNode.getFirstChild(); d != null; d = d.getNextSibling())
						{
							if ("ingredient".equalsIgnoreCase(d.getNodeName()))
							{
								final int id = parseInteger(d.getAttributes(), "id");
								final long count = parseLong(d.getAttributes(), "count");
								final ItemHolder ingredient = new ItemHolder(id, count);
								
								if (itemExists(ingredient))
								{
									ingredients.add(ingredient);
								}
								else
								{
									LOGGER.warn("Invalid ingredient id or count for itemId: {}, count: {} in list: {}", ingredient.getId(), ingredient.getCount(), listId);
									continue;
								}
							}
							else if ("production".equalsIgnoreCase(d.getNodeName()))
							{
								final int id = parseInteger(d.getAttributes(), "id");
								final long count = parseLong(d.getAttributes(), "count");
								final double chance = parseDouble(d.getAttributes(), "chance", Double.NaN);
								final ItemChanceHolder product = new ItemChanceHolder(id, chance, count);
								
								if (itemExists(product))
								{
									// Check chance only of items that have set chance. Items without chance (NaN) are used for displaying purposes.
									if ((!Double.isNaN(chance) && (chance < 0)) || (chance > 100))
									{
										LOGGER.warn("Invalid chance for itemId: {}, count: {}, chance: {} in list: {}", product.getId(), product.getCount(), chance, listId);
										continue;
									}
									
									products.add(product);
								}
								else
								{
									LOGGER.warn("Invalid product id or count for itemId: {}, count: {} in list: {}", product.getId(), product.getCount(), listId);
									continue;
								}
							}
						}
						
						final double totalChance = products.stream().filter(i -> !Double.isNaN(i.getChance())).mapToDouble(ItemChanceHolder::getChance).sum();
						if (totalChance > 100)
						{
							LOGGER.warn("Products' total chance of {}% exceeds 100% for list: {} at entry {}.", totalChance, listId, entries.size() + 1);
						}
						
						entries.add(entry);
					}
					else if ("npcs".equalsIgnoreCase(itemNode.getNodeName()))
					{
						// Initialize NPCs with the size of child nodes.
						final Set<Integer> allowNpc = new HashSet<>(itemNode.getChildNodes().getLength());
						forEach(itemNode, n -> "npc".equalsIgnoreCase(n.getNodeName()) && Util.isDigit(n.getTextContent()), n -> allowNpc.add(Integer.parseInt(n.getTextContent())));
						
						// Add npcs to stats set.
						set.set("allowNpc", allowNpc);
					}
				});
				
				set.set("listId", listId);
				set.set("entries", entries);
				
				_multisells.put(listId, new MultisellListHolder(set));
			});
		}
		catch (Exception e)
		{
			LOGGER.error("Error in file: {}", path, e);
		}
	}
	
	public int getMultisellCount()
	{
		return _multisells.size();
	}
	
	@Override
	public Filter<Path> getCurrentFileFilter()
	{
		return NUMERIC_XML_FILTER;
	}
	
	/**
	 * This will generate the multisell list for the items.<br>
	 * There exist various parameters in multisells that affect the way they will appear:
	 * <ol>
	 * <li>Inventory only:
	 * <ul>
	 * <li>If true, only show items of the multisell for which the "primary" ingredients are already in the player's inventory. By "primary" ingredients we mean weapon and armor.</li>
	 * <li>If false, show the entire list.</li>
	 * </ul>
	 * </li>
	 * <li>Maintain enchantment: presumably, only lists with "inventory only" set to true should sometimes have this as true. This makes no sense otherwise...
	 * <ul>
	 * <li>If true, then the product will match the enchantment level of the ingredient.<br>
	 * If the player has multiple items that match the ingredient list but the enchantment levels differ, then the entries need to be duplicated to show the products and ingredients for each enchantment level.<br>
	 * For example: If the player has a crystal staff +1 and a crystal staff +3 and goes to exchange it at the mammon, the list should have all exchange possibilities for the +1 staff, followed by all possibilities for the +3 staff.</li>
	 * <li>If false, then any level ingredient will be considered equal and product will always be at +0</li>
	 * </ul>
	 * </li>
	 * <li>Apply taxes: Uses the "taxIngredient" entry in order to add a certain amount of adena to the ingredients.
	 * <li>
	 * <li>Additional product and ingredient multipliers.</li>
	 * </ol>
	 * @param listId
	 * @param player
	 * @param npc
	 * @param inventoryOnly
	 * @param ingredientMultiplier
	 * @param productMultiplier
	 */
	public final void separateAndSend(int listId, PlayerInstance player, Npc npc, boolean inventoryOnly, double ingredientMultiplier, double productMultiplier)
	{
		final MultisellListHolder template = _multisells.get(listId);
		if (template == null)
		{
			LOGGER.warn("Can't find list id: {} requested by player: {}, npcId: {}", listId, player.getName(), (npc != null ? npc.getId() : 0));
			return;
		}
		
		if (((npc != null) && !template.isNpcAllowed(npc.getId())) || ((npc == null) && template.isNpcOnly()))
		{
			if (player.isGM())
			{
				player.sendMessage("Multisell " + listId + " is restricted. Under current conditions cannot be used. Only GMs are allowed to use it.");
			}
			else
			{
				LOGGER.warn("Player {} attempted to open multisell {} from npc {} which is not allowed!", player, listId, npc);
				return;
			}
		}
		
		// Check if ingredient/product multipliers are set, if not, set them to the template value.
		ingredientMultiplier = (Double.isNaN(ingredientMultiplier) ? template.getIngredientMultiplier() : ingredientMultiplier);
		productMultiplier = (Double.isNaN(productMultiplier) ? template.getProductMultiplier() : productMultiplier);
		
		final PreparedMultisellListHolder list = new PreparedMultisellListHolder(template, inventoryOnly, player.getInventory(), npc, ingredientMultiplier, productMultiplier);
		int index = 0;
		do
		{
			// send list at least once even if size = 0
			player.sendPacket(new MultiSellList(list, index));
			index += PAGE_SIZE;
		}
		while (index < list.getEntries().size());
		
		player.setMultiSell(list);
	}
	
	public final void separateAndSend(int listId, PlayerInstance player, Npc npc, boolean inventoryOnly)
	{
		separateAndSend(listId, player, npc, inventoryOnly, Double.NaN, Double.NaN);
	}
	
	private final boolean itemExists(ItemHolder holder)
	{
		final SpecialItemType specialItem = SpecialItemType.getByClientId(holder.getId());
		if (specialItem != null)
		{
			return true;
		}
		
		final L2Item template = ItemTable.getInstance().getTemplate(holder.getId());
		return (template != null) && (template.isStackable() ? (holder.getCount() >= 1) : (holder.getCount() == 1));
	}
	
	@InstanceGetter
	public static MultisellData getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final MultisellData _instance = new MultisellData();
	}
}
