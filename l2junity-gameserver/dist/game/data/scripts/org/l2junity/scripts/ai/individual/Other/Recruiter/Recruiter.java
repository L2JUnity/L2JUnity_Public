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
package org.l2junity.scripts.ai.individual.Other.Recruiter;

import java.util.concurrent.TimeUnit;

import org.l2junity.gameserver.config.TrainingCampConfig;
import org.l2junity.gameserver.data.xml.impl.ExperienceData;
import org.l2junity.gameserver.model.Location;
import org.l2junity.gameserver.model.actor.Npc;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.ceremonyofchaos.CeremonyOfChaosEvent;
import org.l2junity.gameserver.model.events.EventType;
import org.l2junity.gameserver.model.events.ListenerRegisterType;
import org.l2junity.gameserver.model.events.annotations.RegisterEvent;
import org.l2junity.gameserver.model.events.annotations.RegisterType;
import org.l2junity.gameserver.model.events.impl.character.player.OnPlayerLogin;
import org.l2junity.gameserver.model.events.impl.character.player.OnPlayerLogout;
import org.l2junity.gameserver.model.events.impl.restriction.CanPlayerAttack;
import org.l2junity.gameserver.model.events.impl.restriction.CanPlayerInviteToAlly;
import org.l2junity.gameserver.model.events.impl.restriction.CanPlayerInviteToClan;
import org.l2junity.gameserver.model.events.impl.restriction.CanPlayerInviteToParty;
import org.l2junity.gameserver.model.events.impl.restriction.CanPlayerJoinSiege;
import org.l2junity.gameserver.model.events.impl.restriction.CanPlayerTrade;
import org.l2junity.gameserver.model.events.impl.restriction.CanPlayerUseAction;
import org.l2junity.gameserver.model.events.impl.restriction.CanPlayerUseItem;
import org.l2junity.gameserver.model.events.impl.restriction.CanPlayerUseSkill;
import org.l2junity.gameserver.model.events.impl.restriction.IsPlayerInvul;
import org.l2junity.gameserver.model.events.impl.restriction.IsWorldObjectVisibleFor;
import org.l2junity.gameserver.model.events.returns.BooleanReturn;
import org.l2junity.gameserver.model.holders.TrainingHolder;
import org.l2junity.gameserver.model.olympiad.OlympiadManager;
import org.l2junity.gameserver.model.variables.AccountVariables;
import org.l2junity.gameserver.model.zone.ZoneId;
import org.l2junity.gameserver.network.client.send.SystemMessage;
import org.l2junity.gameserver.network.client.send.string.SystemMessageId;
import org.l2junity.gameserver.network.client.send.training.ExTrainingZone_Admission;
import org.l2junity.gameserver.network.client.send.training.ExTrainingZone_Leaving;
import org.l2junity.gameserver.scripting.annotations.GameScript;

import org.l2junity.scripts.ai.AbstractNpcAI;

/**
 * Recruiter AI.
 * @author Gladicek
 */
public final class Recruiter extends AbstractNpcAI
{
	// NPC
	private static final int RECRUITER = 4378;
	// Misc
	private static final Location TRAINING_LOCATION = new Location(-56516, 135938, -2672);
	
	private Recruiter()
	{
		addStartNpc(RECRUITER);
		addFirstTalkId(RECRUITER);
		addTalkId(RECRUITER);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		String htmltext = null;
		
		if (!TrainingCampConfig.ENABLE || !checkConditions(player))
		{
			return htmltext;
		}
		
		switch (event)
		{
			case "4378.htm":
			case "4378-02.htm":
			{
				htmltext = event;
				break;
			}
			case "info":
			{
				if (player.isPremium() || !TrainingCampConfig.PREMIUM_ONLY)
				{
					htmltext = "4378-02.htm";
				}
				else
				{
					htmltext = "4378-07.htm";
				}
				break;
			}
			case "enter":
			{
				final long trainingCampDuration = player.getAccountVariables().getLong(AccountVariables.TRAINING_CAMP_DURATION, 0L);
				if (trainingCampDuration >= TrainingCampConfig.MAX_DURATION)
				{
					player.sendPacket(SystemMessageId.YOU_HAVE_COMPLETED_THE_DAY_S_TRAINING);
				}
				else if (player.isPremium() || !TrainingCampConfig.PREMIUM_ONLY)
				{
					final TrainingHolder holder = player.getAccountVariables().getObject(AccountVariables.TRAINING_CAMP, TrainingHolder.class);
					if (holder == null)
					{
						player.disableAutoShotsAll();
						player.setLastLocation();
						player.teleToLocation(TRAINING_LOCATION);
						// @Sdw: Here we are supposed to send ExUserInfoEquipSlot with a fake equip of a SLS, feels ugly to me, not doing it.
						player.getAccountVariables().set(AccountVariables.TRAINING_CAMP, new TrainingHolder(player.getObjectId(), player.getClassIndex(), player.getLevel(), System.currentTimeMillis()));
						final long timeRemaining = TrainingCampConfig.MAX_DURATION - Math.min(trainingCampDuration, 0);
						if (trainingCampDuration > 0)
						{
							player.sendPacket(new ExTrainingZone_Admission(player.getLevel(), 0L, timeRemaining));
						}
						else
						{
							player.sendPacket(new ExTrainingZone_Admission(player));
						}
						
						startQuestTimer("finish", timeRemaining, npc, player);
					}
					else
					{
						htmltext = "4378-06.htm";
					}
					break;
				}
				else
				{
					htmltext = "4378-01.htm";
				}
				break;
			}
			case "4378-04.htm":
			{
				final TrainingHolder holder = player.getAccountVariables().getObject(AccountVariables.TRAINING_CAMP, TrainingHolder.class);
				if ((holder != null) && (holder.getObjectId() == player.getObjectId()))
				{
					if (holder.getClassIndex() == player.getClassIndex())
					{
						final long trainingTime = holder.getTrainingTime(TimeUnit.MINUTES);
						final long expGained = (long) ((trainingTime * (ExperienceData.getInstance().getExpForLevel(holder.getLevel()) * ExperienceData.getInstance().getTrainingRate(holder.getLevel()))) / TrainingHolder.getTrainingDivider());
						final long spGained = expGained / 250L;
						
						String html = getHtm(player.getHtmlPrefix(), "4378-04.htm");
						html = html.replace("%training_level%", String.valueOf(holder.getLevel()));
						html = html.replace("%training_time%", String.valueOf(trainingTime));
						html = html.replace("%training_exp%", String.valueOf(expGained));
						html = html.replace("%training_sp%", String.valueOf(spGained));
						htmltext = html;
					}
					else
					{
						player.sendPacket(SystemMessageId.YOU_CAN_ONLY_BE_REWARDED_AS_THE_CLASS_IN_WHICH_YOU_ENTERED_THE_TRAINING_CAMP);
					}
				}
				else
				{
					htmltext = "4378-05.htm";
				}
				break;
			}
			case "calculate":
			{
				final TrainingHolder holder = player.getAccountVariables().getObject(AccountVariables.TRAINING_CAMP, TrainingHolder.class);
				if ((holder != null) && (holder.getObjectId() == player.getObjectId()))
				{
					if (holder.getClassIndex() == player.getClassIndex())
					{
						final long trainingTime = holder.getTrainingTime(TimeUnit.MINUTES);
						if (trainingTime > 1)
						{
							player.sendPacket(SystemMessageId.CALCULATING_XP_AND_SP_OBTAINED_FROM_TRAINING);
							
							final long expGained = (long) (trainingTime * (ExperienceData.getInstance().getExpForLevel(player.getLevel()) * ExperienceData.getInstance().getTrainingRate(player.getLevel()))) / TrainingHolder.getTrainingDivider();
							final long spGained = expGained / 250;
							player.addExpAndSp(expGained, spGained);
							
							final SystemMessage sysMsg = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_COMPLETED_TRAINING_IN_THE_ROYAL_TRAINING_CAMP_AND_OBTAINED_S1_XP_AND_S2_SP);
							sysMsg.addLong(expGained);
							sysMsg.addLong(spGained);
							player.sendPacket(sysMsg);
						}
						else
						{
							player.sendPacket(SystemMessageId.YOU_CANNOT_RECEIVE_REWARDS_FOR_TRAINING_IF_YOU_HAVE_TRAINED_FOR_LESS_THAN_1_MINUTE);
							player.getAccountVariables().remove(AccountVariables.TRAINING_CAMP);
							player.getAccountVariables().set(AccountVariables.TRAINING_CAMP_DURATION, holder.getTrainingTime(TimeUnit.SECONDS));
						}
					}
					else
					{
						player.sendPacket(SystemMessageId.YOU_CAN_ONLY_BE_REWARDED_AS_THE_CLASS_IN_WHICH_YOU_ENTERED_THE_TRAINING_CAMP);
					}
				}
				break;
			}
			case "finish":
			{
				final TrainingHolder holder = player.getAccountVariables().getObject(AccountVariables.TRAINING_CAMP, TrainingHolder.class);
				if ((holder != null) && (holder.getObjectId() == player.getObjectId()))
				{
					player.teleToLocation(player.getLastLocation());
					player.unsetLastLocation();
					player.sendPacket(ExTrainingZone_Leaving.STATIC_PACKET);
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(Npc npc, PlayerInstance player)
	{
		return "4378.htm";
	}
	
	private boolean checkConditions(PlayerInstance player)
	{
		if (player.getLevel() <= TrainingCampConfig.MIN_LEVEL)
		{
			final SystemMessage sysMsg = SystemMessage.getSystemMessage(SystemMessageId.LV_S1_OR_ABOVE).addInt(TrainingCampConfig.MIN_LEVEL);
			player.sendPacket(sysMsg);
			return false;
		}
		else if (player.getLevel() >= TrainingCampConfig.MAX_LEVEL)
		{
			final SystemMessage sysMsg = SystemMessage.getSystemMessage(SystemMessageId.LV_S1_OR_BELOW).addInt(TrainingCampConfig.MAX_LEVEL);
			player.sendPacket(sysMsg);
			return false;
		}
		else if (player.isFlyingMounted() || player.isTransformed())
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_ENTER_THE_TRAINING_CAMP_WITH_A_MOUNT_OR_IN_A_TRANSFORMED_STATE);
			return false;
		}
		final TrainingHolder holder = player.getAccountVariables().getObject(AccountVariables.TRAINING_CAMP, TrainingHolder.class);
		if ((holder != null) && (holder.getObjectId() != player.getObjectId()))
		{
			player.sendPacket(SystemMessageId.ONLY_ONE_CHARACTER_PER_ACCOUNT_MAY_ENTER_AT_ANY_TIME);
			return false;
		}
		else if (player.isInParty())
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_ENTER_THE_TRAINING_CAMP_WHILE_IN_A_PARTY_OR_USING_THE_AUTOMATIC_REPLACEMENT_SYSTEM);
			return false;
		}
		else if (player.isCursedWeaponEquipped() || (player.getReputation() < 0))
		{
			return false;
		}
		else if (player.isInDuel())
		{
			return false;
		}
		else if (player.isInOlympiadMode() || OlympiadManager.getInstance().isRegistered(player))
		{
			return false;
		}
		else if (player.isOnEvent(CeremonyOfChaosEvent.class) || (player.getBlockCheckerArena() > -1)) // TODO underground coliseum and kratei checks.
		{
			return false;
		}
		else if (player.isInInstance())
		{
			return false;
		}
		else if (player.isInSiege())
		{
			return false;
		}
		else if (player.isInsideZone(ZoneId.SIEGE))
		{
			return false;
		}
		else if (player.isFishing())
		{
			return false;
		}
		else if (player.hasServitors())
		{
			return false;
		}
		return true;
	}
	
	@RegisterEvent(EventType.ON_PLAYER_LOGIN)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	private void onPlayerLogin(OnPlayerLogin event)
	{
		final PlayerInstance player = event.getActiveChar();
		final TrainingHolder holder = player.getAccountVariables().getObject(AccountVariables.TRAINING_CAMP, TrainingHolder.class);
		if (holder == null)
		{
			return;
		}
		
		if (holder.isValid(player) && holder.isTraining())
		{
			final long time = holder.getTime(System.currentTimeMillis(), TimeUnit.SECONDS);
			
			if (time < TrainingCampConfig.MAX_DURATION)
			{
				player.setLastLocation();
				player.teleToLocation(TRAINING_LOCATION);
				player.sendPacket(new ExTrainingZone_Admission(holder.getLevel(), TimeUnit.SECONDS.toMinutes(time), holder.getReminaingTime(time)));
				startQuestTimer("finish", holder.getReminaingTime(time) * 1000, null, player);
			}
			else
			{
				final long trainingCampDuration = TimeUnit.SECONDS.toMillis(TrainingCampConfig.MAX_DURATION) - player.getAccountVariables().getLong(AccountVariables.TRAINING_CAMP_DURATION, 0L);
				holder.setEndTime(holder.getStartTime() + trainingCampDuration);
			}
		}
	}
	
	@RegisterEvent(EventType.ON_PLAYER_LOGOUT)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	private void onPlayerLogout(OnPlayerLogout event)
	{
		final PlayerInstance player = event.getActiveChar();
		final TrainingHolder holder = player.getAccountVariables().getObject(AccountVariables.TRAINING_CAMP, TrainingHolder.class);
		if (holder == null)
		{
			return;
		}
		
		if (holder.isValid(player) && holder.isTraining())
		{
			cancelQuestTimer("finish", null, player);
		}
	}
	
	@RegisterEvent(EventType.CAN_PLAYER_USE_ACTION)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	private BooleanReturn canPlayerUseAction(CanPlayerUseAction event)
	{
		return event.getActiveChar().isInTraingCamp() ? BooleanReturn.FALSE : null;
	}
	
	@RegisterEvent(EventType.CAN_PLAYER_ATTACK)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	private BooleanReturn canPlayerAttack(CanPlayerAttack event)
	{
		return event.getActiveChar().isInTraingCamp() ? BooleanReturn.FALSE : null;
	}
	
	@RegisterEvent(EventType.CAN_PLAYER_INVITE_TO_PARTY)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	private BooleanReturn canPlayerInviteToParty(CanPlayerInviteToParty event)
	{
		return (event.getRequestor().isInTraingCamp() || event.getTarget().isInTraingCamp()) ? BooleanReturn.FALSE : null;
	}
	
	@RegisterEvent(EventType.CAN_PLAYER_INVITE_TO_ALLY)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	private BooleanReturn canPlayerInviteToAlly(CanPlayerInviteToAlly event)
	{
		return (event.getRequestor().isInTraingCamp() || event.getTarget().isInTraingCamp()) ? BooleanReturn.FALSE : null;
	}
	
	@RegisterEvent(EventType.CAN_PLAYER_INVITE_TO_CLAN)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	private BooleanReturn canPlayerInviteToClan(CanPlayerInviteToClan event)
	{
		return (event.getRequestor().isInTraingCamp() || event.getTarget().isInTraingCamp()) ? BooleanReturn.FALSE : null;
	}
	
	@RegisterEvent(EventType.CAN_PLAYER_TRADE)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	private BooleanReturn canPlayerTrade(CanPlayerTrade event)
	{
		return (event.getRequestor().isInTraingCamp() || event.getTarget().isInTraingCamp()) ? BooleanReturn.FALSE : null;
	}
	
	@RegisterEvent(EventType.CAN_PLAYER_USE_ITEM)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	private BooleanReturn canPlayerUseItem(CanPlayerUseItem event)
	{
		return event.getActiveChar().isInTraingCamp() ? BooleanReturn.FALSE : null;
	}
	
	@RegisterEvent(EventType.CAN_PLAYER_USE_SKILL)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	private BooleanReturn canPlayerUseSkill(CanPlayerUseSkill event)
	{
		return event.getActiveChar().isInTraingCamp() ? BooleanReturn.FALSE : null;
	}
	
	@RegisterEvent(EventType.IS_WORLD_OBJECT_VISIBLE_FOR)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	private BooleanReturn isWorldObjectVisibleFor(IsWorldObjectVisibleFor event)
	{
		return (event.getWorldObject().isPlayer() && event.getWorldObject().getActingPlayer().isInTraingCamp()) ? BooleanReturn.FALSE : null;
	}
	
	@RegisterEvent(EventType.IS_PLAYER_INVUL)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	private BooleanReturn isPlayerInvul(IsPlayerInvul event)
	{
		return (event.getActiveChar().isInTraingCamp()) ? BooleanReturn.TRUE : null;
	}
	
	@RegisterEvent(EventType.CAN_PLAYER_JOIN_SIEGE)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	private BooleanReturn canPlayerJoinSiege(CanPlayerJoinSiege event)
	{
		return (event.getActiveChar().isInTraingCamp()) ? BooleanReturn.FALSE : null;
	}
	
	@GameScript
	public static void main()
	{
		new Recruiter();
	}
}