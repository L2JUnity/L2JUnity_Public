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
package org.l2junity.gameserver.data.sql.migrations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.l2junity.commons.sql.DatabaseFactory;
import org.l2junity.commons.sql.migrations.IDatabaseMigration;
import org.l2junity.gameserver.model.variables.PlayerVariables;

/**
 * @author UnAfraid
 */
public class LocationDropMigration implements IDatabaseMigration
{
	@Override
	public String getName()
	{
		return "2017-02-16_LocationDropMigration";
	}
	
	@Override
	public boolean onUp() throws SQLException
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection())
		{
			try (PreparedStatement st = con.prepareStatement("DELETE FROM character_variables WHERE var = ?"))
			{
				st.setString(1, PlayerVariables.INSTANCE_ORIGIN_LOCATION);
				st.execute();
				return true;
			}
		}
	}
	
	@Override
	public boolean onDown()
	{
		return false;
	}
	
	@Override
	public boolean isReversable()
	{
		return false;
	}
}
