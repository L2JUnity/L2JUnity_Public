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
package org.l2junity.gameserver.model;

import java.util.List;
import java.util.function.Function;

import org.l2junity.gameserver.enums.OneDayRewardStatus;
import org.l2junity.gameserver.handler.AbstractOneDayRewardHandler;
import org.l2junity.gameserver.handler.OneDayRewardHandler;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.base.ClassId;
import org.l2junity.gameserver.model.holders.ItemHolder;

/**
 * @author Sdw
 */
public class OneDayRewardDataHolder
{
	private final int _id;
	private final int _rewardId;
	private final List<ItemHolder> _rewardsItems;
	private final List<ClassId> _classRestriction;
	private final int _requiredCompletions;
	private final StatsSet _params;
	private final boolean _isOneTime;
	private final boolean _isMainClassOnly;
	private final boolean _isDualClassOnly;
	private final boolean _isDisplayedWhenNotAvailable;
	private final AbstractOneDayRewardHandler _handler;
	
	public OneDayRewardDataHolder(StatsSet set)
	{
		final Function<OneDayRewardDataHolder, AbstractOneDayRewardHandler> handler = OneDayRewardHandler.getInstance().getHandler(set.getString("handler"));
		
		_id = set.getInt("id");
		_rewardId = set.getInt("reward_id");
		_requiredCompletions = set.getInt("requiredCompletion", 0);
		_rewardsItems = set.getList("items", ItemHolder.class);
		_classRestriction = set.getList("classRestriction", ClassId.class);
		_params = set.getObject("params", StatsSet.class);
		_isOneTime = set.getBoolean("isOneTime", true);
		_isMainClassOnly = set.getBoolean("isMainClassOnly", true);
		_isDualClassOnly = set.getBoolean("isDualClassOnly", false);
		_isDisplayedWhenNotAvailable = set.getBoolean("isDisplayedWhenNotAvailable", true);
		_handler = handler != null ? handler.apply(this) : null;
	}
	
	public int getId()
	{
		return _id;
	}
	
	public int getRewardId()
	{
		return _rewardId;
	}
	
	public List<ClassId> getClassRestriction()
	{
		return _classRestriction;
	}
	
	public List<ItemHolder> getRewards()
	{
		return _rewardsItems;
	}
	
	public int getRequiredCompletions()
	{
		return _requiredCompletions;
	}
	
	public StatsSet getParams()
	{
		return _params;
	}
	
	public boolean isOneTime()
	{
		return _isOneTime;
	}
	
	public boolean isMainClassOnly()
	{
		return _isMainClassOnly;
	}
	
	public boolean isDualClassOnly()
	{
		return _isDualClassOnly;
	}
	
	public boolean isDisplayedWhenNotAvailable()
	{
		return _isDisplayedWhenNotAvailable;
	}
	
	public boolean isDisplayable(PlayerInstance player)
	{
		// Check if its main class only
		if (isMainClassOnly() && (player.isSubClassActive() || player.isDualClassActive()))
		{
			return false;
		}
		
		// Check if its dual class only.
		if (isDualClassOnly() && !player.isDualClassActive())
		{
			return false;
		}
		
		// Check for specific class restrictions
		if (!_classRestriction.isEmpty() && !_classRestriction.contains(player.getClassId()))
		{
			return false;
		}
		
		final int status = getStatus(player);
		if (!isDisplayedWhenNotAvailable() && (status == OneDayRewardStatus.NOT_AVAILABLE.getClientId()))
		{
			return false;
		}
		
		// Show only if its repeatable, recently completed or incompleted that has met the checks above.
		return (!isOneTime() || getRecentlyCompleted(player) || (status != OneDayRewardStatus.COMPLETED.getClientId()));
	}
	
	public void requestReward(PlayerInstance player)
	{
		if ((_handler != null) && isDisplayable(player))
		{
			_handler.requestReward(player);
		}
	}
	
	public int getStatus(PlayerInstance player)
	{
		return _handler != null ? _handler.getStatus(player) : OneDayRewardStatus.NOT_AVAILABLE.getClientId();
	}
	
	public int getProgress(PlayerInstance player)
	{
		return _handler != null ? _handler.getProgress(player) : OneDayRewardStatus.NOT_AVAILABLE.getClientId();
	}
	
	public boolean getRecentlyCompleted(PlayerInstance player)
	{
		return (_handler != null) && _handler.getRecentlyCompleted(player);
	}
	
	public void reset()
	{
		if (_handler != null)
		{
			_handler.reset();
		}
	}
}
