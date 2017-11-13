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
package org.l2junity.gameserver;

import org.l2junity.commons.util.concurrent.IThreadPoolInitializer;
import org.l2junity.gameserver.config.ThreadPoolConfig;

/**
 * @author nos
 */
public final class GameThreadPools implements IThreadPoolInitializer
{
	@Override
	public int getScheduledThreadPoolSize()
	{
		return ThreadPoolConfig.SCHEDULED_THREAD_POOL_SIZE != -1 ? ThreadPoolConfig.SCHEDULED_THREAD_POOL_SIZE : Runtime.getRuntime().availableProcessors() * 4;
	}
	
	@Override
	public int getThreadPoolSize()
	{
		return ThreadPoolConfig.THREAD_POOL_SIZE != -1 ? ThreadPoolConfig.THREAD_POOL_SIZE : Runtime.getRuntime().availableProcessors() * 2;
	}
}
