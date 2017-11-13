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
import java.sql.SQLException;
import java.sql.Statement;

import org.l2junity.commons.sql.DatabaseFactory;
import org.l2junity.commons.sql.migrations.IDatabaseMigration;

/**
 * @author Supreme
 */
public class UIKeysSettingsMigration implements IDatabaseMigration
{
	@Override
	public String getName()
	{
		return "2017-04-19_UIKeysSettingsMigration";
	}
	
	@Override
	public boolean onUp() throws SQLException
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection();
			Statement st = con.createStatement())
		{
			// Drop Table
			st.execute("DROP TABLE IF EXISTS character_ui_actions");
			st.execute("DROP TABLE IF EXISTS character_ui_categories");
		}
		
		return true;
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
