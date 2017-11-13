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
package org.l2junity.gameserver.tests;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.l2junity.commons.util.AppInit;
import org.l2junity.commons.util.AppInit.ApplicationMode;
import org.l2junity.gameserver.GameThreadPools;
import org.l2junity.gameserver.cache.HtmCache;
import org.l2junity.gameserver.config.GameConfigMarker;
import org.l2junity.gameserver.config.GeneralConfig;
import org.l2junity.gameserver.config.L2JModsConfig;

/**
 * @author lord_rex
 */
public final class HtmCacheTest
{
	@Before
	public void setUp()
	{
		AppInit.defaultInit(ApplicationMode.GAME, GameConfigMarker.class.getPackage().getName(), GameThreadPools.class);
		GeneralConfig.DEADLOCK_DETECTOR = false;
		L2JModsConfig.L2JMOD_MULTILANG_ENABLE = true;
		HtmCache.getInstance();
	}
	
	@Test
	public void testNull()
	{
		final String content = HtmCache.getInstance().getHtm(null, "data/html/servnews.htm");
		Assert.assertNotNull(content);
		Assert.assertNotEquals(content, "");
	}
	
	@Test
	public void testEN()
	{
		final String content = HtmCache.getInstance().getHtm("en", "data/html/servnews.htm");
		Assert.assertNotNull(content);
		Assert.assertNotEquals(content, "");
	}
	
	@Test
	public void testRU()
	{
		final String content = HtmCache.getInstance().getHtm("ru", "data/html/servnews.htm");
		Assert.assertNotNull(content);
		Assert.assertNotEquals(content, "");
	}
}
