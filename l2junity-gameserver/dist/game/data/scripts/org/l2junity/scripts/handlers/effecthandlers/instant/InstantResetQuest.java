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
package org.l2junity.scripts.handlers.effecthandlers.instant;

import java.util.List;

import org.l2junity.gameserver.handler.EffectHandler;
import org.l2junity.gameserver.instancemanager.QuestManager;
import org.l2junity.gameserver.model.StatsSet;
import org.l2junity.gameserver.model.WorldObject;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.effects.AbstractEffect;
import org.l2junity.gameserver.model.items.instance.ItemInstance;
import org.l2junity.gameserver.model.quest.Quest;
import org.l2junity.gameserver.model.skills.Skill;
import org.l2junity.gameserver.scripting.annotations.SkillScript;

/**
 * @author Sdw
 */
public class InstantResetQuest extends AbstractEffect
{
	final private List<Integer> _questIds;
	
	public InstantResetQuest(StatsSet params)
	{
		_questIds = params.getList("questIds", Integer.class);
	}
	
	@Override
	public void instant(Creature caster, WorldObject target, Skill skill, ItemInstance item)
	{
		final PlayerInstance targetPlayer = target.asPlayer();
		if (targetPlayer == null)
		{
			return;
		}
		
		for (int questId : _questIds)
		{
			final Quest quest = QuestManager.getInstance().getQuest(questId);
			if (quest != null)
			{
				targetPlayer.delQuestState(quest.getClass().getSimpleName());
			}
		}
	}

	@SkillScript
	public static void main()
	{
		EffectHandler.getInstance().registerHandler("i_reset_quest", InstantResetQuest::new);
	}
}
