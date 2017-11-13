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
package org.l2junity.gameserver.instancemanager;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.l2junity.commons.sql.DatabaseFactory;
import org.l2junity.commons.util.Rnd;
import org.l2junity.gameserver.model.World;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.eventengine.AbstractEvent;
import org.l2junity.gameserver.model.eventengine.AbstractEventManager;
import org.l2junity.gameserver.model.eventengine.ScheduleTarget;
import org.l2junity.gameserver.model.events.Containers;
import org.l2junity.gameserver.model.events.EventType;
import org.l2junity.gameserver.model.events.impl.character.npc.OnAttackableKill;
import org.l2junity.gameserver.model.events.impl.character.player.OnPlayerLogin;
import org.l2junity.gameserver.model.events.impl.character.player.OnPlayerPvPKill;
import org.l2junity.gameserver.model.events.listeners.ConsumerEventListener;
import org.l2junity.gameserver.model.variables.AccountVariables;
import org.l2junity.gameserver.model.variables.PlayerVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Sdw
 */
public class PcCafeManager extends AbstractEventManager<AbstractEvent<?>>
{
	private static final Logger LOGGER = LoggerFactory.getLogger(PcCafeManager.class);
	public static final String ENABLE_TIMER_BONUS_KEY = "enable_timer_bonus";
	public static final String TIMER_POINTS_KEY = "timer_points";
	public static final String TIMER_DOUBLE_POINTS_PROBABILITY_KEY = "timer_double_points_probability";
	public static final String ENABLE_DAILY_BONUS_KEY = "enable_daily_bonus";
	public static final String ENABLE_DAILY_BONUS_POINTS_KEY = "daily_bonus_points";
	public static final String ENABLE_FARMING_BONUS_KEY = "enable_farming_bonus";
	public static final String ENABLE_FARMING_BONUS_POINTS_KEY = "farming_bonus_points";
	public static final String FARMING_BONUS_LEVEL_DIFF_KEY = "farming_bonus_level_diff";
	public static final String ENABLE_PVP_BONUS_KEY = "enable_pvp_bonus";
	public static final String ENABLE_PVP_BONUS_POINTS_KEY = "pvp_bonus_points";
	
	protected PcCafeManager()
	{
	}
	
	@Override
	public void onInitialized()
	{
		if (getVariables().getBoolean(ENABLE_DAILY_BONUS_KEY, false))
		{
			Containers.Players().addListener(new ConsumerEventListener(Containers.Players(), EventType.ON_PLAYER_LOGIN, (OnPlayerLogin event) -> onPlayerLogin(event), this));
		}
		if (getVariables().getBoolean(ENABLE_FARMING_BONUS_KEY, false))
		{
			Containers.Monsters().addListener(new ConsumerEventListener(Containers.Monsters(), EventType.ON_ATTACKABLE_KILL, (OnAttackableKill event) -> onAttackableKill(event), this));
		}
		if (getVariables().getBoolean(ENABLE_PVP_BONUS_KEY, false))
		{
			Containers.Players().addListener(new ConsumerEventListener(Containers.Players(), EventType.ON_PLAYER_PVP_KILL, (OnPlayerPvPKill event) -> onPlayerPvPKill(event), this));
		}
	}
	
	@ScheduleTarget
	private void rewardPoint()
	{
		if (getVariables().getBoolean(ENABLE_TIMER_BONUS_KEY, false))
		{
			long pointsToAdd = getVariables().getLong(TIMER_POINTS_KEY, 10L);
			World.getInstance().getPlayers().stream().forEach(player ->
			{
				player.increasePcCafePoints(pointsToAdd, (Rnd.get(100) < getVariables().getInt(TIMER_DOUBLE_POINTS_PROBABILITY_KEY, 10)));
				
			});
		}
	}
	
	@ScheduleTarget
	private void onReset()
	{
		// Update data for offline players.
		try (Connection con = DatabaseFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement("DELETE FROM account_gsdata WHERE var = ?");
			PreparedStatement ps2 = con.prepareStatement("DELETE FROM character_variables WHERE var = ?"))
		{
			ps.setString(1, AccountVariables.PC_CAFE_POINTS_TODAY);
			ps.executeUpdate();
			ps2.setString(1, PlayerVariables.USED_PC_LOTTERY_TICKET);
			ps2.executeUpdate();
		}
		catch (Exception e)
		{
			LOGGER.error("Could not reset pc cafe daily bonus: ", e);
		}
		
		// Update data for online players.
		World.getInstance().getPlayers().stream().forEach(player ->
		{
			player.getAccountVariables().remove(AccountVariables.PC_CAFE_POINTS_TODAY);
			player.getAccountVariables().storeMe();
			player.getVariables().remove(PlayerVariables.USED_PC_LOTTERY_TICKET);
			player.getVariables().storeMe();
		});
		
		LOGGER.info("Daily pc cafe daily bonus has been resetted.");
	}
	
	private void onPlayerLogin(OnPlayerLogin event)
	{
		final PlayerInstance player = event.getActiveChar();
		if (!player.getAccountVariables().getBoolean(AccountVariables.PC_CAFE_POINTS_TODAY, false))
		{
			player.getAccountVariables().set(AccountVariables.PC_CAFE_POINTS_TODAY, true);
			player.increasePcCafePoints(getVariables().getLong(ENABLE_DAILY_BONUS_POINTS_KEY, 1000L));
		}
	}
	
	private void onAttackableKill(OnAttackableKill event)
	{
		final PlayerInstance player = event.getAttacker();
		final int levelDiff = event.getTarget().getLevel() - player.getLevel();
		final int maxLevelDiff = getVariables().getInt(FARMING_BONUS_LEVEL_DIFF_KEY, 11);
		if ((levelDiff > -maxLevelDiff) && (levelDiff < maxLevelDiff))
		{
			player.increasePcCafePoints(getVariables().getLong(ENABLE_FARMING_BONUS_POINTS_KEY, 50L));
		}
	}
	
	private void onPlayerPvPKill(OnPlayerPvPKill event)
	{
		event.getActiveChar().increasePcCafePoints(getVariables().getLong(ENABLE_PVP_BONUS_POINTS_KEY, 100L));
	}
	
	public static final PcCafeManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final PcCafeManager INSTANCE = new PcCafeManager();
	}
}
