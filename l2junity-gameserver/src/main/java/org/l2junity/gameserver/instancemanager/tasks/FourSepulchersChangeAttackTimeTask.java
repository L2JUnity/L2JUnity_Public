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
package org.l2junity.gameserver.instancemanager.tasks;

import java.util.Calendar;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.l2junity.commons.util.concurrent.ThreadPool;
import org.l2junity.gameserver.config.GeneralConfig;
import org.l2junity.gameserver.instancemanager.FourSepulchersManager;

/**
 * Four Sepulchers change attack time task.
 * @author xban1x
 */
public final class FourSepulchersChangeAttackTimeTask implements Runnable
{
	@Override
	public void run()
	{
		final FourSepulchersManager manager = FourSepulchersManager.getInstance();
		manager.setIsEntryTime(false);
		manager.setIsWarmUpTime(false);
		manager.setIsAttackTime(true);
		manager.setIsCoolDownTime(false);
		
		manager.locationShadowSpawns();
		
		manager.spawnMysteriousBox(31921);
		manager.spawnMysteriousBox(31922);
		manager.spawnMysteriousBox(31923);
		manager.spawnMysteriousBox(31924);
		
		if (!manager.isFirstTimeRun())
		{
			manager.setWarmUpTimeEnd(Calendar.getInstance().getTimeInMillis());
		}
		
		long interval = 0;
		// say task
		if (manager.isFirstTimeRun())
		{
			for (double min = Calendar.getInstance().get(Calendar.MINUTE); min < manager.getCycleMin(); min++)
			{
				// looking for next shout time....
				if ((min % 5) == 0)// check if min can be divided by 5
				{
					final Calendar inter = Calendar.getInstance();
					inter.set(Calendar.MINUTE, (int) min);
					ThreadPool.schedule(new FourSepulchersManagerSayTask(), inter.getTimeInMillis() - Calendar.getInstance().getTimeInMillis(), TimeUnit.MILLISECONDS);
					break;
				}
			}
		}
		else
		{
			ThreadPool.schedule(new FourSepulchersManagerSayTask(), 5 * 60400, TimeUnit.MILLISECONDS);
		}
		// searching time when attack time will be ended:
		// counting difference between time when attack time ends and
		// current time
		// and then launching change time task
		if (manager.isFirstTimeRun())
		{
			interval = manager.getAttackTimeEnd() - Calendar.getInstance().getTimeInMillis();
		}
		else
		{
			interval = GeneralConfig.FS_TIME_ATTACK * 60000L;
		}
		
		manager.setChangeCoolDownTimeTask(ThreadPool.schedule(new FourSepulchersChangeCoolDownTimeTask(), interval, TimeUnit.MILLISECONDS));
		final ScheduledFuture<?> changeAttackTimeTask = manager.getChangeAttackTimeTask();
		
		if (changeAttackTimeTask != null)
		{
			changeAttackTimeTask.cancel(true);
			manager.setChangeAttackTimeTask(null);
		}
	}
}
