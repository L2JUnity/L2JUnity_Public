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
package org.l2junity.gameserver.config;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.l2junity.commons.config.ConfigPropertiesLoader;
import org.l2junity.commons.config.annotation.ConfigClass;
import org.l2junity.commons.config.annotation.ConfigField;
import org.l2junity.commons.util.PropertiesParser;
import org.l2junity.gameserver.model.World;

/**
 * @author lord_rex
 */
@ConfigClass(fileName = "GeoData")
public final class GeoDataConfig extends ConfigPropertiesLoader
{
	@ConfigField(name = "PathFinding", value = "0", comment =
	{
		"Pathfinding options:",
		"0 = Disabled",
		"1 = Enabled using path node files",
		"2 = Enabled using geodata cells at runtime"
	})
	public static int PATHFINDING;
	
	@ConfigField(name = "PathnodeDirectory", value = "./data/pathnode", comment =
	{
		"Pathnode directory"
	})
	public static Path PATHNODE_PATH;
	
	@ConfigField(name = "PathFindBuffers", value = "100x6;128x6;192x6;256x4;320x4;384x4;500x2", comment =
	{
		"Pathfinding array buffers configuration"
	})
	public static String PATHFIND_BUFFERS;
	
	@ConfigField(name = "LowWeight", value = "0.5", comment =
	{
		"Weight for nodes without obstacles far from walls"
	})
	public static float LOW_WEIGHT;
	
	@ConfigField(name = "MediumWeight", value = "2", comment =
	{
		"Weight for nodes near walls"
	})
	public static float MEDIUM_WEIGHT;
	
	@ConfigField(name = "HighWeight", value = "3", comment =
	{
		"Weight for nodes with obstacles"
	})
	public static float HIGH_WEIGHT;
	
	@ConfigField(name = "AdvancedDiagonalStrategy", value = "true", comment =
	{
		"Angle paths will be more 'smart', but in cost of higher CPU utilization"
	})
	public static boolean ADVANCED_DIAGONAL_STRATEGY;
	
	@ConfigField(name = "DiagonalWeight", value = "0.707", comment =
	{
		"Weight for diagonal movement. Used only with AdvancedDiagonalStrategy = True"
	})
	public static float DIAGONAL_WEIGHT;
	
	@ConfigField(name = "MaxPostfilterPasses", value = "3", comment =
	{
		"Maximum number of LOS postfilter passes, 0 will disable postfilter."
	})
	public static int MAX_POSTFILTER_PASSES;
	
	@ConfigField(name = "DebugPath", value = "false", comment =
	{
		"Path debug function.",
		"Nodes known to pathfinder will be displayed as adena, constructed path as antidots.",
		"Number of the items show node cost * 10",
		"Potions display path after first stage filter",
		"Red potions - actual waypoints. Green potions - nodes removed by LOS postfilter",
		"This function FOR DEBUG PURPOSES ONLY, never use it on the live server !"
	})
	public static boolean DEBUG_PATH;
	
	@ConfigField(name = "ForceGeoData", value = "true", comment =
	{
		"True = Loads GeoData buffer's content into physical memory.",
		"False = Does not necessarily imply that the GeoData buffer's content is not resident in physical memory."
	})
	public static boolean FORCE_GEODATA;
	
	@ConfigField(name = "CoordSynchronize", value = "-1", comment =
	{
		"This setting controls Client <--> Server Player coordinates synchronization:",
		"-1 - Will synchronize only Z from Client --> Server. Default when no geodata.",
		"1 - Synchronization Client --> Server only. Using this option (without geodata) makes it more difficult for players to bypass obstacles.",
		"2 - Intended for geodata (at least with cell-level pathfinding, otherwise can you try -1).",
		"Server sends validation packet if client goes too far from server calculated coordinates."
	})
	public static int COORD_SYNCHRONIZE;
	
	@ConfigField(name = "GeoDataPath", value = "./data/geodata", comment =
	{
		"Geodata files folder"
	})
	public static Path GEODATA_PATH;
	
	@ConfigField(name = "TryLoadUnspecifiedRegions", value = "true", comment =
	{
		"True: Try to load regions not specified below(won't disturb server startup when file does not exist)",
		"False: Don't load any regions other than the ones specified with True below"
	})
	public static boolean TRY_LOAD_UNSPECIFIED_REGIONS;
	
	@ConfigField(name = "GeoDataRegions", value = "", onlyComment = true, comment =
	{
		"List of regions to be required to load",
		"eg.:",
		"Both regions required",
		"22_22=True",
		"19_20=true",
		"Exclude region from loading",
		"25_26=false",
		"True: Region is required for the server to startup",
		"False: Region is not considered to be loaded",
	})
	public static String _GEODATA_REGIONS;
	public static Map<String, Boolean> GEODATA_REGIONS;
	
	@Override
	protected void loadImpl(PropertiesParser properties, PropertiesParser override)
	{
		final Properties props = propertiesOf(properties, override);
		GEODATA_REGIONS = new HashMap<>();
		for (int regionX = World.TILE_X_MIN; regionX <= World.TILE_X_MAX; regionX++)
		{
			for (int regionY = World.TILE_Y_MIN; regionY <= World.TILE_Y_MAX; regionY++)
			{
				String key = regionX + "_" + regionY;
				if (props.containsKey(regionX + "_" + regionY))
				{
					GEODATA_REGIONS.put(key, Boolean.parseBoolean(props.getProperty(key, "false")));
				}
			}
		}
	}
}
