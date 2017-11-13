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
package org.l2junity.scripts.quests.Q10331_StartOfFate;

import org.l2junity.gameserver.config.GeneralConfig;
import org.l2junity.gameserver.enums.CategoryType;
import org.l2junity.gameserver.enums.Race;
import org.l2junity.gameserver.model.actor.Npc;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.base.ClassId;
import org.l2junity.gameserver.model.events.EventType;
import org.l2junity.gameserver.model.events.ListenerRegisterType;
import org.l2junity.gameserver.model.events.annotations.RegisterEvent;
import org.l2junity.gameserver.model.events.annotations.RegisterType;
import org.l2junity.gameserver.model.events.impl.character.player.OnPlayerLevelChanged;
import org.l2junity.gameserver.model.events.impl.character.player.OnPlayerLogin;
import org.l2junity.gameserver.model.events.impl.character.player.OnPlayerPressTutorialMark;
import org.l2junity.gameserver.model.quest.Quest;
import org.l2junity.gameserver.model.quest.QuestState;
import org.l2junity.gameserver.model.quest.State;
import org.l2junity.gameserver.network.client.send.TutorialShowHtml;
import org.l2junity.gameserver.network.client.send.TutorialShowQuestionMark;
import org.l2junity.gameserver.util.Util;

/**
 * Start of Fate (10331)
 * @author Gladicek
 */
public final class Q10331_StartOfFate extends Quest
{
	// NPCs
	private static final int SEBION = 32978;
	private static final int FRANCO = 32153;
	private static final int RIVIAN = 32147;
	private static final int DEVON = 32160;
	private static final int TOOK = 32150;
	private static final int MOKA = 32157;
	private static final int VALFAR = 32146;
	// Items
	private static final int SARIL_NECKLACE = 17580;
	private static final int SOULSHOT_D = 1463;
	private static final int SPIRITSHOT_D = 3948;
	private static final int SCROLL_OF_ESCAPE = 736;
	private static final int PAULINA_EQUIPMENT_SET_D = 46849;
	// Misc
	private static final int MIN_LEVEL = 18;
	
	public Q10331_StartOfFate()
	{
		super(10331);
		addStartNpc(SEBION);
		addTalkId(SEBION, FRANCO, RIVIAN, DEVON, TOOK, MOKA, VALFAR);
		addCondInCategory(CategoryType.FIRST_CLASS_GROUP, "");
		addCondNotRace(Race.ERTHEIA, "32978-10.htm");
		addCondMinLevel(MIN_LEVEL, "32978-11.htm");
		registerQuestItems(SARIL_NECKLACE);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return null;
		}
		
		String htmltext = null;
		switch (event)
		{
			case "32978-02.htm":
			{
				htmltext = event;
				break;
			}
			case "32978-03.html":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			/**
			 * 1st class transfer htmls menu with classes
			 */
			case "32146-02.html": // Kamael Female
			case "32146-03.html": // Kamael Male
			case "32153-02.html": // Human Fighter
			case "32153-03.html": // Human Mage
			case "32157-02.html": // Dwarven Fighter
			case "32147-02.html": // Elven Fighter
			case "32147-03.html": // Elven Mage
			case "32160-02.html": // Dark Elven Fighter
			case "32160-03.html": // Dark Elven Mage
			case "32150-02.html": // Orc Fighter
			case "32150-03.html": // Orc Mage
				/**
				 * 1st class transfer htmls for each class
				 */
			case "32146-04.html": // Trooper
			case "32146-05.html": // Warder
			case "32153-04.html": // Warrior
			case "32153-05.html": // Knight
			case "32153-06.html": // Rogue
			case "32153-07.html": // Wizard
			case "32153-08.html": // Cleric
			case "32157-03.html": // Scavenger
			case "32157-04.html": // Artisan
			case "32147-04.html": // Elven Knight
			case "32147-05.html": // Elven Scout
			case "32147-06.html": // Elven Wizard
			case "32147-07.html": // Elven Oracle
			case "32160-04.html": // Palus Knight
			case "32160-05.html": // Assasin
			case "32160-06.html": // Dark Wizard
			case "32160-07.html": // Shilien Oracle
			case "32150-04.html": // Orc Raider
			case "32150-05.html": // Orc Monk
			case "32150-06.html": // Orc Shaman
			{
				if ((qs.getCond() >= 3) && (qs.getCond() <= 8))
				{
					htmltext = event;
				}
				break;
			}
			default:
			{
				if (event.startsWith("classChange;"))
				{
					final ClassId newClassId = ClassId.getClassId(Integer.parseInt(event.replace("classChange;", "")));
					final ClassId currentClassId = player.getClassId();
					
					if (!newClassId.childOf(currentClassId) || ((qs.getCond() < 3) && (qs.getCond() > 8)))
					{
						Util.handleIllegalPlayerAction(player, "Player " + player.getName() + " tried to cheat the 1st class transfer!", GeneralConfig.DEFAULT_PUNISH);
						return null;
					}
					else if ((player.getLevel() >= MIN_LEVEL))
					{
						switch (newClassId)
						{
							case WARRIOR:
								htmltext = "32153-09.html";
								break;
							case KNIGHT:
								htmltext = "32153-10.html";
								break;
							case ROGUE:
								htmltext = "32153-11.html";
								break;
							case WIZARD:
								htmltext = "32153-12.html";
								break;
							case CLERIC:
								htmltext = "32153-13.html";
								break;
							case ELVEN_KNIGHT:
								htmltext = "32147-08.html";
								break;
							case ELVEN_SCOUT:
								htmltext = "32147-09.html";
								break;
							case ELVEN_WIZARD:
								htmltext = "32147-10.html";
								break;
							case ORACLE:
								htmltext = "32147-11.html";
								break;
							case PALUS_KNIGHT:
								htmltext = "32160-08.html";
								break;
							case ASSASSIN:
								htmltext = "32160-09.html";
								break;
							case DARK_WIZARD:
								htmltext = "32160-10.html";
								break;
							case SHILLIEN_ORACLE:
								htmltext = "32160-11.html";
								break;
							case ORC_RAIDER:
								htmltext = "32150-07.html";
								break;
							case ORC_MONK:
								htmltext = "32150-08.html";
								break;
							case ORC_SHAMAN:
								htmltext = "32150-09.html";
								break;
							case SCAVENGER:
								htmltext = "32157-05.html";
								break;
							case ARTISAN:
								htmltext = "32157-06.html";
								break;
							case TROOPER:
								htmltext = "32146-06.html";
								break;
							case WARDER:
								htmltext = "32146-07.html";
								break;
						}
						player.setBaseClass(newClassId);
						player.setClassId(newClassId.getId());
						player.broadcastUserInfo();
						player.sendSkillList();
						giveItems(player, SOULSHOT_D, 1500);
						giveItems(player, SPIRITSHOT_D, 1500);
						giveItems(player, SCROLL_OF_ESCAPE, 10);
						giveItems(player, PAULINA_EQUIPMENT_SET_D, 1);
						addExp(player, 296_000);
						addSp(player, 15);
						player.sendPacket(new TutorialShowHtml(npc.getObjectId(), "..\\L2Text\\QT_009_enchant_01.htm", TutorialShowHtml.LARGE_WINDOW));
						qs.exitQuest(false, true);
					}
					else
					{
						htmltext = getNoQuestLevelRewardMsg(player);
					}
				}
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(Npc npc, PlayerInstance player, boolean isSimulated)
	{
		final QuestState qs = getQuestState(player, true);
		String htmltext = null;
		
		switch (qs.getState())
		{
			case State.CREATED:
			{
				if (npc.getId() == SEBION)
				{
					htmltext = "32978-01.htm";
					break;
				}
			}
			case State.STARTED:
			{
				switch (npc.getId())
				{
					case SEBION:
					{
						switch (qs.getCond())
						{
							case 1:
								htmltext = "32978-03.html";
								break;
							case 2:
							{
								if (!isSimulated)
								{
									switch (player.getRace())
									{
										case HUMAN:
										{
											qs.setCond(3, true);
											htmltext = "32978-04.html";
											break;
										}
										case ELF:
										{
											qs.setCond(4, true);
											htmltext = "32978-05.html";
											break;
										}
										case DARK_ELF:
										{
											qs.setCond(5, true);
											htmltext = "32978-06.html";
											break;
										}
										case ORC:
										{
											qs.setCond(6, true);
											htmltext = "32978-07.html";
											break;
										}
										case DWARF:
										{
											qs.setCond(7, true);
											htmltext = "32978-08.html";
											break;
										}
										case KAMAEL:
										{
											qs.setCond(8, true);
											htmltext = "32978-09.html";
											break;
										}
									}
									break;
								}
								break;
							}
							case 3:
							case 4:
							case 5:
							case 6:
							case 7:
							case 8:
								htmltext = "32978-12.html";
								break;
						}
						break;
					}
					case FRANCO:
					{
						if ((qs.getCond() >= 3) && (qs.getCond() <= 8))
						{
							if (player.getRace() == Race.HUMAN)
							{
								switch (player.getClassId())
								{
									case FIGHTER:
									{
										htmltext = "32153-02.html";
										break;
									}
									case MAGE:
									{
										htmltext = "32153-03.html";
										break;
									}
								}
								break;
							}
							htmltext = "32153-01.html";
							break;
						}
						break;
					}
					case RIVIAN:
					{
						if ((qs.getCond() >= 3) && (qs.getCond() <= 8))
						{
							if (player.getRace() == Race.ELF)
							{
								switch (player.getClassId())
								{
									case ELVEN_FIGHTER:
									{
										htmltext = "32147-02.html";
										break;
									}
									case ELVEN_MAGE:
									{
										htmltext = "32147-03.html";
										break;
									}
								}
								break;
							}
							htmltext = "32147-01.html";
							break;
						}
						break;
					}
					case DEVON:
					{
						if ((qs.getCond() >= 3) && (qs.getCond() <= 8))
						{
							if (player.getRace() == Race.DARK_ELF)
							{
								switch (player.getClassId())
								{
									case DARK_FIGHTER:
									{
										htmltext = "32160-02.html";
										break;
									}
									case DARK_MAGE:
									{
										htmltext = "32160-03.html";
										break;
									}
								}
								break;
							}
							htmltext = "32160-01.html";
							break;
						}
						break;
					}
					case TOOK:
					{
						if ((qs.getCond() >= 3) && (qs.getCond() <= 8))
						{
							if (player.getRace() == Race.ORC)
							{
								switch (player.getClassId())
								{
									case ORC_FIGHTER:
									{
										htmltext = "32150-02.html";
										break;
									}
									case ORC_MAGE:
									{
										htmltext = "32150-03.html";
										break;
									}
								}
								break;
							}
							htmltext = "32150-01.html";
							break;
						}
						break;
					}
					case MOKA:
					{
						if ((qs.getCond() >= 3) && (qs.getCond() <= 8))
						{
							if (player.getRace() == Race.DWARF)
							{
								htmltext = "32157-02.html";
								break;
							}
							htmltext = "32157-01.html";
							break;
						}
						break;
					}
					case VALFAR:
					{
						if ((qs.getCond() >= 3) && (qs.getCond() <= 8))
						{
							if (player.getRace() == Race.KAMAEL)
							{
								switch (player.getClassId())
								{
									case MALE_SOLDIER:
									{
										htmltext = "32146-03.html";
										break;
									}
									case FEMALE_SOLDIER:
									{
										htmltext = "32146-02.html";
										break;
									}
								}
								break;
							}
							htmltext = "32146-01.html";
							break;
						}
						break;
					}
				}
				break;
			}
			case State.COMPLETED:
			{
				switch (npc.getId())
				{
					case SEBION:
					case FRANCO:
					case RIVIAN:
					case DEVON:
					case TOOK:
					case MOKA:
					case VALFAR:
					{
						htmltext = getAlreadyCompletedMsg(player);
						break;
					}
				}
				break;
			}
		}
		return htmltext;
	}
	
	@RegisterEvent(EventType.ON_PLAYER_PRESS_TUTORIAL_MARK)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void onPlayerPressTutorialMark(OnPlayerPressTutorialMark event)
	{
		if (event.getQuestId() == getId())
		{
			final PlayerInstance player = event.getActiveChar();
			player.sendPacket(new TutorialShowHtml(getHtm(player.getHtmlPrefix(), "popup.html")));
		}
	}
	
	@RegisterEvent(EventType.ON_PLAYER_LEVEL_CHANGED)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void OnPlayerLevelChanged(OnPlayerLevelChanged event)
	{
		final PlayerInstance player = event.getActiveChar();
		final QuestState qs = getQuestState(player, false);
		final int oldLevel = event.getOldLevel();
		final int newLevel = event.getNewLevel();
		
		if ((qs == null) && (oldLevel < newLevel) && (newLevel == MIN_LEVEL) && (player.getRace() != Race.ERTHEIA) && (player.isInCategory(CategoryType.FIRST_CLASS_GROUP)))
		{
			player.sendPacket(new TutorialShowQuestionMark(getId(), 1));
		}
	}
	
	@RegisterEvent(EventType.ON_PLAYER_LOGIN)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void OnPlayerLogin(OnPlayerLogin event)
	{
		final PlayerInstance player = event.getActiveChar();
		final QuestState qs = getQuestState(player, false);
		
		if ((qs == null) && (player.getRace() != Race.ERTHEIA) && (player.getLevel() >= MIN_LEVEL) && (player.isInCategory(CategoryType.FIRST_CLASS_GROUP)))
		{
			player.sendPacket(new TutorialShowQuestionMark(getId(), 1));
		}
	}
}