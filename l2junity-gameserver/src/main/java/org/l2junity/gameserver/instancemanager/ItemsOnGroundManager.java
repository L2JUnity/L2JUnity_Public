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
package org.l2junity.gameserver.instancemanager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.l2junity.commons.loader.annotations.Dependency;
import org.l2junity.commons.loader.annotations.InstanceGetter;
import org.l2junity.commons.loader.annotations.Load;
import org.l2junity.commons.sql.DatabaseFactory;
import org.l2junity.commons.util.concurrent.ThreadPool;
import org.l2junity.gameserver.ItemsAutoDestroy;
import org.l2junity.gameserver.config.GeneralConfig;
import org.l2junity.gameserver.datatables.ItemTable;
import org.l2junity.gameserver.loader.LoadGroup;
import org.l2junity.gameserver.model.items.instance.ItemInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class manage all items on ground.
 * @author Enforcer
 */
public final class ItemsOnGroundManager
{
	private static final Logger LOGGER = LoggerFactory.getLogger(ItemsOnGroundManager.class);
	
	private final Set<ItemInstance> _items = ConcurrentHashMap.newKeySet();
	
	protected ItemsOnGroundManager()
	{
	}
	
	@Load(group = LoadGroup.class, dependencies =
	{
		@Dependency(clazz = ItemTable.class),
		@Dependency(clazz = CursedWeaponsManager.class)
	})
	private void load()
	{
		if (!GeneralConfig.SAVE_DROPPED_ITEM)
		{
			return;
		}
		
		if (GeneralConfig.SAVE_DROPPED_ITEM_INTERVAL > 0)
		{
			final long interval = TimeUnit.MINUTES.toMillis(GeneralConfig.SAVE_DROPPED_ITEM_INTERVAL);
			ThreadPool.scheduleAtFixedRate(this::storeInDB, interval, interval, TimeUnit.MILLISECONDS);
		}
		
		// If SaveDroppedItem is false, may want to delete all items previously stored to avoid add old items on reactivate
		if (!GeneralConfig.SAVE_DROPPED_ITEM && GeneralConfig.CLEAR_DROPPED_ITEM_TABLE)
		{
			emptyTable();
		}
		
		if (!GeneralConfig.SAVE_DROPPED_ITEM)
		{
			return;
		}
		
		// if DestroyPlayerDroppedItem was previously false, items currently protected will be added to ItemsAutoDestroy
		if (GeneralConfig.DESTROY_DROPPED_PLAYER_ITEM)
		{
			String str = null;
			if (!GeneralConfig.DESTROY_EQUIPABLE_PLAYER_ITEM)
			{
				// Recycle misc. items only
				str = "UPDATE itemsonground SET drop_time = ? WHERE drop_time = -1 AND equipable = 0";
			}
			else if (GeneralConfig.DESTROY_EQUIPABLE_PLAYER_ITEM)
			{
				// Recycle all items including equip-able
				str = "UPDATE itemsonground SET drop_time = ? WHERE drop_time = -1";
			}
			
			try (Connection con = DatabaseFactory.getInstance().getConnection();
				PreparedStatement ps = con.prepareStatement(str))
			{
				ps.setLong(1, System.currentTimeMillis());
				ps.execute();
			}
			catch (Exception e)
			{
				LOGGER.error("Error while updating table ItemsOnGround: ", e);
			}
		}
		
		// Add items to world
		try (Connection con = DatabaseFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT object_id, item_id, count, enchant_level, x, y, z, drop_time, equipable FROM itemsonground"))
		{
			int count = 0;
			try (ResultSet rs = ps.executeQuery())
			{
				ItemInstance item;
				while (rs.next())
				{
					item = new ItemInstance(rs.getInt(1), rs.getInt(2));
					// this check and..
					if (item.isStackable() && (rs.getInt(3) > 1))
					{
						item.setCount(rs.getInt(3));
					}
					// this, are really necessary?
					if (rs.getInt(4) > 0)
					{
						item.setEnchantLevel(rs.getInt(4));
					}
					final long dropTime = rs.getLong(8);
					item.setDropTime(dropTime);
					item.setProtected(dropTime == -1);
					item.setSpawned(true);
					item.spawnMe(rs.getInt(5), rs.getInt(6), rs.getInt(7));
					_items.add(item);
					count++;
					// add to ItemsAutoDestroy only items not protected
					if (!GeneralConfig.LIST_PROTECTED_ITEMS.contains(item.getId()))
					{
						if (dropTime > -1)
						{
							if (((GeneralConfig.AUTODESTROY_ITEM_AFTER > 0) && !item.getItem().hasExImmediateEffect()) || ((GeneralConfig.HERB_AUTO_DESTROY_TIME > 0) && item.getItem().hasExImmediateEffect()))
							{
								ItemsAutoDestroy.getInstance().addItem(item);
							}
						}
					}
				}
			}
			LOGGER.info("Loaded {} items.", count);
		}
		catch (Exception e)
		{
			LOGGER.error("Error while loading ItemsOnGround: ", e);
		}
		
		if (GeneralConfig.EMPTY_DROPPED_ITEM_TABLE_AFTER_LOAD)
		{
			emptyTable();
		}
	}
	
	public void save(ItemInstance item)
	{
		if (GeneralConfig.SAVE_DROPPED_ITEM)
		{
			_items.add(item);
		}
	}
	
	public void removeObject(ItemInstance item)
	{
		if (GeneralConfig.SAVE_DROPPED_ITEM)
		{
			_items.remove(item);
		}
	}
	
	public void saveInDb()
	{
		storeInDB();
	}
	
	public void cleanUp()
	{
		_items.clear();
	}
	
	public void emptyTable()
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection();
			Statement s = con.createStatement())
		{
			s.executeUpdate("DELETE FROM itemsonground");
		}
		catch (Exception e)
		{
			LOGGER.error("Error while cleaning table ItemsOnGround: ", e);
		}
	}
	
	private synchronized void storeInDB()
	{
		if (!GeneralConfig.SAVE_DROPPED_ITEM)
		{
			return;
		}
		
		emptyTable();
		
		if (_items.isEmpty())
		{
			return;
		}
		
		try (Connection con = DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement("INSERT INTO itemsonground(object_id, item_id, count, enchant_level, x, y, z, drop_time, equipable) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)"))
		{
			for (ItemInstance item : _items)
			{
				if (item == null)
				{
					continue;
				}
				
				if (CursedWeaponsManager.getInstance().isCursed(item.getId()))
				{
					continue; // Cursed Items not saved to ground, prevent double save
				}
				
				try
				{
					statement.setInt(1, item.getObjectId());
					statement.setInt(2, item.getId());
					statement.setLong(3, item.getCount());
					statement.setInt(4, item.getEnchantLevel());
					statement.setInt(5, (int) item.getX());
					statement.setInt(6, (int) item.getY());
					statement.setInt(7, (int) item.getZ());
					statement.setLong(8, (item.isProtected() ? -1 : item.getDropTime())); // item is protected or AutoDestroyed
					statement.setLong(9, (item.isEquipable() ? 1 : 0)); // set equip-able
					statement.execute();
				}
				catch (Exception e)
				{
					LOGGER.error("Error while inserting into table ItemsOnGround: ", e);
				}
			}
		}
		catch (SQLException e)
		{
			LOGGER.error("SQL error while storing items on ground: ", e);
		}
	}
	
	/**
	 * Gets the single instance of {@code ItemsOnGroundManager}.
	 * @return single instance of {@code ItemsOnGroundManager}
	 */
	@InstanceGetter
	public static ItemsOnGroundManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final ItemsOnGroundManager INSTANCE = new ItemsOnGroundManager();
	}
}
