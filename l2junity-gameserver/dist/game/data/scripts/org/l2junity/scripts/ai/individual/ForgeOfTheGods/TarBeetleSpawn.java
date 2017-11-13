/*
 * Copyright (C) 2004-2015 L2J DataPack
 * 
 * This file is part of L2J DataPack.
 * 
 * L2J DataPack is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J DataPack is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2junity.scripts.ai.individual.ForgeOfTheGods;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.l2junity.commons.util.Rnd;
import org.l2junity.commons.util.concurrent.ThreadPool;
import org.l2junity.gameserver.data.xml.IGameXmlReader;
import org.l2junity.gameserver.geodata.GeoData;
import org.l2junity.gameserver.model.L2Spawn;
import org.l2junity.gameserver.model.Location;
import org.l2junity.gameserver.model.Territory;
import org.l2junity.gameserver.model.actor.Npc;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Tar Beetle zone spawn
 * @author malyelfik
 */
public class TarBeetleSpawn implements IGameXmlReader
{
	private final List<SpawnZone> zones = new ArrayList<>();
	private ScheduledFuture<?> spawnTask;
	private ScheduledFuture<?> shotTask;
	
	protected TarBeetleSpawn()
	{
		load();
	}
	
	private void load()
	{
		try
		{
			parseDatapackFile("data/spawnZones/tar_beetle.xml");
		}
		catch (Exception e)
		{
			LOGGER.warn("Failed to load tar beetle spawns: ", e);
		}
		if (!zones.isEmpty())
		{
			spawnTask = ThreadPool.scheduleAtFixedRate(() -> zones.forEach(SpawnZone::refreshSpawn), 1000, 60000, TimeUnit.MILLISECONDS);
			shotTask = ThreadPool.scheduleAtFixedRate(() -> zones.forEach(SpawnZone::refreshShots), 300000, 300000, TimeUnit.MILLISECONDS);
		}
	}
	
	@Override
	public void parseDocument(Document doc, Path path)
	{
		int i = 0;
		for (Node d = doc.getFirstChild(); d != null; d = d.getNextSibling())
		{
			if (d.getNodeName().equals("list"))
			{
				for (Node r = d.getFirstChild(); r != null; r = r.getNextSibling())
				{
					if (r.getNodeName().equals("spawnZone"))
					{
						NamedNodeMap attrs = r.getAttributes();
						final int npcCount = parseInteger(attrs, "maxNpcCount");
						final SpawnZone sp = new SpawnZone(npcCount, i);
						for (Node b = r.getFirstChild(); b != null; b = b.getNextSibling())
						{
							if (b.getNodeName().equals("zone"))
							{
								attrs = b.getAttributes();
								final int minZ = parseInteger(attrs, "minZ");
								final int maxZ = parseInteger(attrs, "maxZ");
								final Zone zone = new Zone();
								for (Node c = b.getFirstChild(); c != null; c = c.getNextSibling())
								{
									attrs = c.getAttributes();
									if (c.getNodeName().equals("point"))
									{
										final int x = parseInteger(attrs, "x");
										final int y = parseInteger(attrs, "y");
										zone.addPoint(x, y, minZ, maxZ);
									}
									else if (c.getNodeName().equals("bannedZone"))
									{
										final Zone bannedZone = new Zone();
										final int bMinZ = parseInteger(attrs, "minZ");
										final int bMaxZ = parseInteger(attrs, "maxZ");
										for (Node n = c.getFirstChild(); n != null; n = n.getNextSibling())
										{
											if (n.getNodeName().equals("point"))
											{
												attrs = n.getAttributes();
												int x = parseInteger(attrs, "x");
												int y = parseInteger(attrs, "y");
												bannedZone.addPoint(x, y, bMinZ, bMaxZ);
											}
										}
										zone.addBannedZone(bannedZone);
									}
								}
								sp.addZone(zone);
							}
						}
						zones.add(i++, sp);
					}
				}
			}
		}
	}
	
	public final void unload()
	{
		if (spawnTask != null)
		{
			spawnTask.cancel(false);
		}
		if (shotTask != null)
		{
			shotTask.cancel(false);
		}
		zones.forEach(SpawnZone::unload);
		zones.clear();
	}
	
	public final void removeBeetle(Npc npc)
	{
		zones.get(npc.getVariables().getInt("zoneIndex", 0)).removeSpawn(npc);
		npc.deleteMe();
	}
	
	private final class Zone extends Territory
	{
		private static final long serialVersionUID = -8367481434795455749L;
		private List<Zone> _bannedZones;
		
		public Zone()
		{
		}
		
		@Override
		public Location getRandomPoint()
		{
			Location location = super.getRandomPoint();
			while ((location != null) && isInsideBannedZone(location))
			{
				location = super.getRandomPoint();
			}
			return location;
		}
		
		public final void addBannedZone(Zone bZone)
		{
			if (_bannedZones == null)
			{
				_bannedZones = new ArrayList<>();
			}
			_bannedZones.add(bZone);
		}
		
		private final boolean isInsideBannedZone(Location location)
		{
			if (_bannedZones != null)
			{
				for (Zone zone : _bannedZones)
				{
					if (zone.contains(location.getX(), location.getY()))
					{
						return true;
					}
				}
			}
			return false;
		}
	}
	
	private final class SpawnZone
	{
		private final List<Zone> _zones = new ArrayList<>();
		private final Set<Npc> _spawn = ConcurrentHashMap.newKeySet();
		private final int _maxNpcCount;
		private final int _index;
		
		public SpawnZone(int maxNpcCount, int index)
		{
			_maxNpcCount = maxNpcCount;
			_index = index;
		}
		
		public final void addZone(Zone zone)
		{
			_zones.add(zone);
		}
		
		public final void removeSpawn(Npc obj)
		{
			_spawn.remove(obj);
		}
		
		public final void unload()
		{
			_spawn.forEach(Npc::deleteMe);
			_spawn.clear();
			_zones.clear();
		}
		
		public final void refreshSpawn()
		{
			try
			{
				while (_spawn.size() < _maxNpcCount)
				{
					final Location location = _zones.get(Rnd.get(_zones.size())).getRandomPoint();
					if (location != null)
					{
						final L2Spawn spawn = new L2Spawn(18804);
						spawn.setHeading(Rnd.get(65535));
						spawn.setXYZ(location.getX(), location.getY(), GeoData.getInstance().getSpawnHeight(location));
						
						final Npc npc = spawn.doSpawn();
						npc.setRandomWalking(true);
						npc.setIsImmobilized(true);
						npc.setIsInvul(true);
						npc.disableCoreAI(true);
						npc.setScriptValue(5);
						npc.getVariables().set("zoneIndex", _index);
						_spawn.add(npc);
					}
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
		public final void refreshShots()
		{
			if (_spawn.size() > 0)
			{
				for (Npc npc : _spawn)
				{
					final int val = npc.getScriptValue();
					if (val == 5)
					{
						npc.deleteMe();
						_spawn.remove(npc);
					}
					else
					{
						npc.setScriptValue(val + 1);
					}
				}
			}
		}
	}
	
	private static final class SingletonHolder
	{
		protected static final TarBeetleSpawn INSTANCE = new TarBeetleSpawn();
	}
	
	public static TarBeetleSpawn getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
}