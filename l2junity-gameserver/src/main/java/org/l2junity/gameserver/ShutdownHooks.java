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
package org.l2junity.gameserver;

import org.l2junity.commons.lang.management.ShutdownManager;
import org.l2junity.gameserver.config.GeneralConfig;
import org.l2junity.gameserver.config.L2JModsConfig;
import org.l2junity.gameserver.data.sql.impl.ClanTable;
import org.l2junity.gameserver.data.sql.impl.OfflineTradersTable;
import org.l2junity.gameserver.datatables.BotReportTable;
import org.l2junity.gameserver.instancemanager.CastleManorManager;
import org.l2junity.gameserver.instancemanager.CeremonyOfChaosManager;
import org.l2junity.gameserver.instancemanager.CursedWeaponsManager;
import org.l2junity.gameserver.instancemanager.DBSpawnManager;
import org.l2junity.gameserver.instancemanager.GlobalVariablesManager;
import org.l2junity.gameserver.instancemanager.GrandBossManager;
import org.l2junity.gameserver.instancemanager.ItemAuctionManager;
import org.l2junity.gameserver.instancemanager.ItemsOnGroundManager;
import org.l2junity.gameserver.instancemanager.QuestManager;
import org.l2junity.gameserver.model.World;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.entity.Hero;
import org.l2junity.gameserver.model.olympiad.Olympiad;
import org.l2junity.gameserver.network.EventLoopGroupManager;
import org.l2junity.gameserver.network.client.ClientNetworkManager;
import org.l2junity.gameserver.network.client.Disconnection;
import org.l2junity.gameserver.network.client.send.SystemMessage;
import org.l2junity.gameserver.network.client.send.string.SystemMessageId;
import org.l2junity.gameserver.network.gameserverpackets.ServerStatus;
import org.l2junity.gameserver.network.telnet.TelnetServer;
import org.l2junity.gameserver.plugins.ServerPluginProvider;
import org.l2junity.gameserver.util.Broadcast;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author lord_rex
 */
public final class ShutdownHooks
{
	private static final Logger LOGGER = LoggerFactory.getLogger(ShutdownHooks.class);
	
	private ShutdownHooks()
	{
		// utility class
	}
	
	/**
	 * Initializes the main shutdown related events.
	 */
	public static void init()
	{
		ShutdownManager.addShutdownHook(() ->
		{
			final TimeCounter tc = new TimeCounter();
			final TimeCounter tc1 = new TimeCounter();
			
			try
			{
				if ((L2JModsConfig.OFFLINE_TRADE_ENABLE || L2JModsConfig.OFFLINE_CRAFT_ENABLE) && L2JModsConfig.RESTORE_OFFLINERS)
				{
					OfflineTradersTable.getInstance().storeOffliners();
					LOGGER.info("Offline Traders Table: Offline shops stored(" + tc.getEstimatedTimeAndRestartCounter() + "ms).");
				}
			}
			catch (Throwable t)
			{
				// ignore
			}
			
			try
			{
				disconnectAllCharacters();
				LOGGER.info("All players disconnected and saved(" + tc.getEstimatedTimeAndRestartCounter() + "ms).");
			}
			catch (Throwable t)
			{
				// ignore
			}
			
			try
			{
				LoginServerThread.getInstance().interrupt();
				LOGGER.info("Login Server Thread: Thread interruped(" + tc.getEstimatedTimeAndRestartCounter() + "ms).");
			}
			catch (Throwable t)
			{
				// ignore
			}
			
			try
			{
				TelnetServer.getInstance().shutdown();
				LOGGER.info("Telnet Server Thread: Thread interruped(" + tc.getEstimatedTimeAndRestartCounter() + "ms).");
			}
			catch (Throwable t)
			{
				// ignore
			}
			
			// last byebye, save all data and quit this server
			// Save all raidboss and GrandBoss status ^_^
			DBSpawnManager.getInstance().cleanUp();
			LOGGER.info("RaidBossSpawnManager: All raidboss info saved(" + tc.getEstimatedTimeAndRestartCounter() + "ms).");
			GrandBossManager.getInstance().cleanUp();
			LOGGER.info("GrandBossManager: All Grand Boss info saved(" + tc.getEstimatedTimeAndRestartCounter() + "ms).");
			ItemAuctionManager.getInstance().shutdown();
			LOGGER.info("Item Auction Manager: All tasks stopped(" + tc.getEstimatedTimeAndRestartCounter() + "ms).");
			Olympiad.getInstance().saveOlympiadStatus();
			LOGGER.info("Olympiad System: Data saved(" + tc.getEstimatedTimeAndRestartCounter() + "ms).");
			CeremonyOfChaosManager.getInstance().stopScheduler();
			LOGGER.info("CeremonyOfChaosManager: Scheduler stopped(" + tc.getEstimatedTimeAndRestartCounter() + "ms).");
			
			Hero.getInstance().shutdown();
			LOGGER.info("Hero System: Data saved(" + tc.getEstimatedTimeAndRestartCounter() + "ms).");
			ClanTable.getInstance().shutdown();
			LOGGER.info("Clan System: Data saved(" + tc.getEstimatedTimeAndRestartCounter() + "ms).");
			
			// Save Cursed Weapons data before closing.
			CursedWeaponsManager.getInstance().saveData();
			LOGGER.info("Cursed Weapons Manager: Data saved(" + tc.getEstimatedTimeAndRestartCounter() + "ms).");
			
			// Save all manor data
			if (!GeneralConfig.ALT_MANOR_SAVE_ALL_ACTIONS)
			{
				CastleManorManager.getInstance().storeMe();
				LOGGER.info("Castle Manor Manager: Data saved(" + tc.getEstimatedTimeAndRestartCounter() + "ms).");
			}
			
			// Save all global (non-player specific) Quest data that needs to persist after reboot
			QuestManager.getInstance().save();
			LOGGER.info("Quest Manager: Data saved(" + tc.getEstimatedTimeAndRestartCounter() + "ms).");
			
			// Save all global variables data
			GlobalVariablesManager.getInstance().storeMe();
			LOGGER.info("Global Variables Manager: Variables saved(" + tc.getEstimatedTimeAndRestartCounter() + "ms).");
			
			// Save items on ground before closing
			if (GeneralConfig.SAVE_DROPPED_ITEM)
			{
				ItemsOnGroundManager.getInstance().saveInDb();
				LOGGER.info("Items On Ground Manager: Data saved(" + tc.getEstimatedTimeAndRestartCounter() + "ms).");
				ItemsOnGroundManager.getInstance().cleanUp();
				LOGGER.info("Items On Ground Manager: Cleaned up(" + tc.getEstimatedTimeAndRestartCounter() + "ms).");
			}
			
			// Save bot reports to database
			if (GeneralConfig.BOTREPORT_ENABLE)
			{
				BotReportTable.getInstance().saveReportedCharData();
				LOGGER.info("Bot Report Table: Sucessfully saved reports to database in {}!", tc.getEstimatedTimeAndRestartCounter());
			}
			
			ServerPluginProvider.getInstance().onShutdown();
			LOGGER.info("Plugins: Shutdown in {}ms.", tc.getEstimatedTimeAndRestartCounter());
			
			// saveData sends messages to exit players, so shutdown selector after it
			try
			{
				ClientNetworkManager.getInstance().stop();
				EventLoopGroupManager.getInstance().shutdown();
				LOGGER.info("Game Server: Selector thread has been shut down(" + tc.getEstimatedTimeAndRestartCounter() + "ms).");
			}
			catch (Throwable t)
			{
				// ignore
			}
			
			LOGGER.info("The server has been successfully shut down in " + (tc1.getEstimatedTime() / 1000) + " second(s).");
		});
		
		ShutdownManager.addShutdownCounterListener((shutdownCounter, i) ->
		{
			final int players = World.getInstance().getPlayers().size();
			
			if (players > 0)
			{
				final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.THE_SERVER_WILL_BE_COMING_DOWN_IN_S1_SECOND_S_PLEASE_FIND_A_SAFE_PLACE_TO_LOG_OUT);
				sm.addInt(i);
				Broadcast.toAllOnlinePlayers(sm);
			}
			else
			{
				shutdownCounter.setFastMode(true);
			}
			
			if (i == 1)
			{
				Broadcast.toAllOnlinePlayers("Server is " + shutdownCounter.getMode().getShortDescription() + "ing NOW!");
			}
		});
		
		ShutdownManager.addShutdownAbortListener((mode, initiator) ->
		{
			LoginServerThread.getInstance().setServerStatus(ServerStatus.STATUS_AUTO);
			Broadcast.toAllOnlinePlayers(initiator + " has aborted server " + mode.getShortDescription() + ".");
		});
		
		LOGGER.info("Standard shutdown hook(s) are initialized.");
	}
	
	/**
	 * This disconnects all clients from the server.
	 */
	private static void disconnectAllCharacters()
	{
		for (PlayerInstance player : World.getInstance().getPlayers())
		{
			Disconnection.of(player).defaultSequence(true);
		}
	}
	
	/**
	 * A simple class used to track down the estimated time of method executions.<br>
	 * Once this class is created, it saves the start time, and when you want to get the estimated time, use the getEstimatedTime() method.
	 */
	private static final class TimeCounter
	{
		private long _startTime;
		
		protected TimeCounter()
		{
			restartCounter();
		}
		
		protected void restartCounter()
		{
			_startTime = System.currentTimeMillis();
		}
		
		protected long getEstimatedTimeAndRestartCounter()
		{
			final long toReturn = System.currentTimeMillis() - _startTime;
			restartCounter();
			return toReturn;
		}
		
		protected long getEstimatedTime()
		{
			return System.currentTimeMillis() - _startTime;
		}
	}
}
