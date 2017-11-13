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
 * @author Sdw
 */
public class AbilityPointsMigration implements IDatabaseMigration
{
	@Override
	public String getName()
	{
		return "2017-03-31_AbilityPointsMigration";
	}
	
	@Override
	public boolean onUp() throws SQLException
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection())
		{
			try (PreparedStatement ps = con.prepareStatement("DELETE FROM character_variables WHERE var = ?"))
			{
				ps.setString(1, PlayerVariables.ABILITY_POINTS_DUAL_CLASS);
				ps.addBatch();
				ps.setString(1, PlayerVariables.ABILITY_POINTS_MAIN_CLASS);
				ps.addBatch();
				ps.setString(1, PlayerVariables.ABILITY_POINTS_USED_DUAL_CLASS);
				ps.addBatch();
				ps.setString(1, PlayerVariables.ABILITY_POINTS_USED_MAIN_CLASS);
				ps.addBatch();
				ps.executeBatch();
			}
			
			try (PreparedStatement ps = con.prepareStatement("DELETE FROM character_skills WHERE skill_id >= ? AND skill_id <= ?"))
			{
				ps.setInt(1, 19121);
				ps.setInt(2, 19177);
				ps.execute();
			}
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