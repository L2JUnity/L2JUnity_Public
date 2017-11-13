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
package org.l2junity.gameserver.model.holders;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.l2junity.gameserver.datatables.ItemTable;
import org.l2junity.gameserver.model.items.L2Item;

/**
 * @author Nik
 */
public class MultisellEntryHolder
{
	private final boolean _stackable;
	private final List<ItemHolder> _ingredients;
	private final List<ItemChanceHolder> _products;
	
	public MultisellEntryHolder(List<ItemHolder> ingredients, List<ItemChanceHolder> products)
	{
		_ingredients = Collections.unmodifiableList(ingredients);
		_products = Collections.unmodifiableList(products);
		_stackable = products.stream().map(i -> ItemTable.getInstance().getTemplate(i.getId())).filter(Objects::nonNull).allMatch(L2Item::isStackable);
	}
	
	public final List<ItemHolder> getIngredients()
	{
		return _ingredients;
	}
	
	public final List<ItemChanceHolder> getProducts()
	{
		return _products;
	}
	
	public final boolean isStackable()
	{
		return _stackable;
	}
}