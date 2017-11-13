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
package org.l2junity.scripts.ai.individual.Other.ClassMaster;

import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import org.l2junity.commons.util.Rnd;
import org.l2junity.gameserver.data.xml.IGameXmlReader;
import org.l2junity.gameserver.data.xml.impl.CategoryData;
import org.l2junity.gameserver.data.xml.impl.ClassListData;
import org.l2junity.gameserver.data.xml.impl.ExperienceData;
import org.l2junity.gameserver.data.xml.impl.SkillTreesData;
import org.l2junity.gameserver.datatables.ItemTable;
import org.l2junity.gameserver.enums.CategoryType;
import org.l2junity.gameserver.enums.NobleStatus;
import org.l2junity.gameserver.enums.Race;
import org.l2junity.gameserver.model.actor.Npc;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.base.ClassId;
import org.l2junity.gameserver.model.events.EventType;
import org.l2junity.gameserver.model.events.ListenerRegisterType;
import org.l2junity.gameserver.model.events.annotations.RegisterEvent;
import org.l2junity.gameserver.model.events.annotations.RegisterType;
import org.l2junity.gameserver.model.events.impl.character.player.OnPlayerBypass;
import org.l2junity.gameserver.model.events.impl.character.player.OnPlayerLevelChanged;
import org.l2junity.gameserver.model.events.impl.character.player.OnPlayerLogin;
import org.l2junity.gameserver.model.events.impl.character.player.OnPlayerPressTutorialMark;
import org.l2junity.gameserver.model.events.impl.character.player.OnPlayerProfessionChange;
import org.l2junity.gameserver.model.holders.ItemChanceHolder;
import org.l2junity.gameserver.model.spawns.SpawnTemplate;
import org.l2junity.gameserver.model.variables.PlayerVariables;
import org.l2junity.gameserver.network.client.send.PlaySound;
import org.l2junity.gameserver.network.client.send.SocialAction;
import org.l2junity.gameserver.network.client.send.TutorialCloseHtml;
import org.l2junity.gameserver.network.client.send.TutorialShowQuestionMark;
import org.l2junity.gameserver.scripting.annotations.GameScript;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import org.l2junity.scripts.ai.AbstractNpcAI;

/**
 * Class Master AI.
 * @author Nik
 */
public final class ClassMaster extends AbstractNpcAI implements IGameXmlReader
{
	// NPCs
	private static final int[] CLASS_MASTER =
	{
		31756, // Mr. Cat
		31757, // Queen of Hearts
	};
	// Misc
	private boolean _isEnabled;
	private boolean _spawnClassMasters;
	private boolean _showPopupWindow;
	private static final Logger LOGGER = LoggerFactory.getLogger(ClassMaster.class);
	private final List<ClassChangeData> _classChangeData = new LinkedList<>();

	private static final int CHAOS_POMANDER = 37374;
	private static final int CHAOS_POMANDER_DUAL_CLASS = 37375;
	private static final int TAUTI_SUPPORT_BOX = 35728;
	private static final int SUPERION_SUPPORT_BOX = 47355;
	private static final int EXALTED_CLOAK = 37763;
	private static final int EXALTED_TIARA = 45644;
	private static final int DIGNITY_OF_THE_EXALTED_1 = 45922;
	private static final int DIGNITY_OF_THE_EXALTED_2 = 45923;
	private static final int DIGNITY_OF_THE_EXALTED_3 = 45924;
	private static final int BELIEF_OF_THE_EXALTED = 45925;
	private static final int BLESSING_OF_THE_EXALTED = 45926;
	private static final int SUMMON_BATTLE_POTION = 45927;
	private static final int FAVOR_OF_THE_EXALTED_1 = 45928;
	private static final int FAVOR_OF_THE_EXALTED_2 = 45870;
	private static final Map<CategoryType, Integer> AWAKE_POWER = new HashMap<>();
	static
	{
		AWAKE_POWER.put(CategoryType.SIXTH_SIGEL_GROUP, 32264);
		AWAKE_POWER.put(CategoryType.SIXTH_TIR_GROUP, 32265);
		AWAKE_POWER.put(CategoryType.SIXTH_OTHEL_GROUP, 32266);
		AWAKE_POWER.put(CategoryType.SIXTH_YR_GROUP, 32267);
		AWAKE_POWER.put(CategoryType.SIXTH_FEOH_GROUP, 32268);
		AWAKE_POWER.put(CategoryType.SIXTH_WYNN_GROUP, 32269);
		AWAKE_POWER.put(CategoryType.SIXTH_IS_GROUP, 32270);
		AWAKE_POWER.put(CategoryType.SIXTH_EOLH_GROUP, 32271);
	}
	
	public ClassMaster()
	{
		load();
		addStartNpc(CLASS_MASTER);
		addTalkId(CLASS_MASTER);
		addFirstTalkId(CLASS_MASTER);
	}
	
	public void load()
	{
		_classChangeData.clear();
		try
		{
			parseDatapackFile("config/ClassMaster.xml");
		}
		catch (Exception e)
		{
			LOGGER.error("Failed loading class master.", e);
		}
		
		LOGGER.info("Loaded {} class change options.", _classChangeData.size());
	}
	
	@Override
	public boolean isValidating()
	{
		return false;
	}
	
	@Override
	public void parseDocument(Document doc, Path path)
	{
		NamedNodeMap attrs;
		for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling())
		{
			if ("list".equals(n.getNodeName()))
			{
				for (Node cm = n.getFirstChild(); cm != null; cm = cm.getNextSibling())
				{
					attrs = cm.getAttributes();
					if ("classMaster".equals(cm.getNodeName()))
					{
						_isEnabled = parseBoolean(attrs, "classChangeEnabled", false);
						if (!_isEnabled)
						{
							return;
						}
						
						_spawnClassMasters = parseBoolean(attrs, "spawnClassMasters", true);
						_showPopupWindow = parseBoolean(attrs, "showPopupWindow", false);
						
						for (Node c = cm.getFirstChild(); c != null; c = c.getNextSibling())
						{
							attrs = c.getAttributes();
							if ("classChangeOption".equals(c.getNodeName()))
							{
								List<CategoryType> appliedCategories = new LinkedList<>();
								List<ItemChanceHolder> requiredItems = new LinkedList<>();
								List<ItemChanceHolder> rewardedItems = new LinkedList<>();
								boolean setNoble = false;
								boolean setHero = false;
								String optionName = parseString(attrs, "name", "");
								for (Node b = c.getFirstChild(); b != null; b = b.getNextSibling())
								{
									attrs = b.getAttributes();
									if ("appliesTo".equals(b.getNodeName()))
									{
										for (Node r = b.getFirstChild(); r != null; r = r.getNextSibling())
										{
											attrs = r.getAttributes();
											if ("category".equals(r.getNodeName()))
											{
												CategoryType category = CategoryType.findByName(r.getTextContent().trim());
												if (category == null)
												{
													LOGGER.error("Incorrect category type: {}", r.getNodeValue());
													continue;
												}
												
												appliedCategories.add(category);
											}
										}
									}
									if ("rewards".equals(b.getNodeName()))
									{
										for (Node r = b.getFirstChild(); r != null; r = r.getNextSibling())
										{
											attrs = r.getAttributes();
											if ("item".equals(r.getNodeName()))
											{
												int itemId = parseInteger(attrs, "id");
												int count = parseInteger(attrs, "count", 1);
												int chance = parseInteger(attrs, "chance", 100);
												
												rewardedItems.add(new ItemChanceHolder(itemId, chance, count));
											}
											else if ("setNoble".equals(r.getNodeName()))
											{
												setNoble = true;
											}
											else if ("setHero".equals(r.getNodeName()))
											{
												setHero = true;
											}
										}
									}
									else if ("conditions".equals(b.getNodeName()))
									{
										for (Node r = b.getFirstChild(); r != null; r = r.getNextSibling())
										{
											attrs = r.getAttributes();
											if ("item".equals(r.getNodeName()))
											{
												int itemId = parseInteger(attrs, "id");
												int count = parseInteger(attrs, "count", 1);
												int chance = parseInteger(attrs, "chance", 100);
												
												requiredItems.add(new ItemChanceHolder(itemId, chance, count));
											}
										}
									}
								}
								
								if (appliedCategories.isEmpty())
								{
									LOGGER.warn("Class change option: {} has no categories to be applied on. Skipping!", optionName);
									continue;
								}
								
								ClassChangeData classChangeData = new ClassChangeData(optionName, appliedCategories);
								classChangeData.setItemsRequired(requiredItems);
								classChangeData.setItemsRewarded(rewardedItems);
								classChangeData.setRewardHero(setHero);
								classChangeData.setRewardNoblesse(setNoble);
								
								_classChangeData.add(classChangeData);
							}
						}
					}
				}
			}
		}
	}
	
	@Override
	public void onSpawnActivate(SpawnTemplate template)
	{
		if (_spawnClassMasters)
		{
			template.spawnAllIncludingNotDefault(null);
		}
	}
	
	@Override
	public void onSpawnDeactivate(SpawnTemplate template)
	{
		template.despawnAll();
	}
	
	@Override
	public String onFirstTalk(Npc npc, PlayerInstance player)
	{
		return _isEnabled ? "test_server_helper001.htm" : null;
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		if (!_isEnabled)
		{
			return null;
		}
		
		String htmltext = null;
		StringTokenizer st = new StringTokenizer(event);
		event = st.nextToken();
		switch (event)
		{
			case "buyitems":
			{
				htmltext = npc.getId() == CLASS_MASTER[0] ? "test_server_helper001a.htm" : "test_server_helper001b.htm";
				break;
			}
			case "setnoble":
			{
				if (player.isNoble())
				{
					showHtmlFile(player, "test_server_helper025b.htm");
				}
				else if (player.isExalted())
				{
					showHtmlFile(player, "test_server_helper025c.htm");
				}
				else if (player.getLevel() < 75)
				{
					showHtmlFile(player, "test_server_helper025a.htm");
				}
				else
				{
					player.setNoble(NobleStatus.NOBLESS);
					player.broadcastUserInfo();
					// TODO: SetOneTimeQuestFlag(talker, 10385, 1);
					showHtmlFile(player, "test_server_helper025.htm");
				}
				break;
			}
			case "exalted":
			{
				if (player.isExalted())
				{
					showHtmlFile(player, "test_server_helper025d.htm");
				}
				else if (!player.isNoble() && player.getLevel() < 99)
				{
					showHtmlFile(player, "test_server_helper025e.htm");
				}
				else
				{
					player.setNoble(NobleStatus.EXALTED_3);
					giveItems(player, EXALTED_CLOAK, 1);
					giveItems(player, EXALTED_TIARA, 1);
					giveItems(player, DIGNITY_OF_THE_EXALTED_1, 1);
					giveItems(player, DIGNITY_OF_THE_EXALTED_2, 1);
					giveItems(player, DIGNITY_OF_THE_EXALTED_3, 1);
					giveItems(player, BELIEF_OF_THE_EXALTED, 1);
					giveItems(player, BLESSING_OF_THE_EXALTED, 1);
					giveItems(player, SUMMON_BATTLE_POTION, 1);
					giveItems(player, FAVOR_OF_THE_EXALTED_1, 1);
					giveItems(player, FAVOR_OF_THE_EXALTED_2, 1);
					player.broadcastUserInfo();
					showHtmlFile(player, "test_server_helper025f.htm");
				}
				break;
			}
			case "give_95_lv":
			{
				if (player.getLevel() < 95)
				{
					player.addExpAndSp(ExperienceData.getInstance().getExpForLevel(95) - player.getExp(), 0);
					player.broadcastUserInfo();
				}
				else
				{
					showHtmlFile(player, "test_server_helper040.htm");
				}
				break;
			}
			case "tauti_support_box":
			{
				if (!player.getVariables().getBoolean(PlayerVariables.TAUTI_SUPPORT_BOX, false))
				{
					player.getVariables().set(PlayerVariables.TAUTI_SUPPORT_BOX, true);
					giveItems(player, TAUTI_SUPPORT_BOX, 1);
				}
				else
				{
					showHtmlFile(player, "test_server_helper041.htm");
				}
				break;
			}
			case "give_clan_reputation":
			{
				if ((player.isClanLeader() || (player.getPledgeClass() == 9 && player.getClan() != null)) && player.getClan().getLevel() >= 10)
				{
					if (!player.getVariables().getBoolean(PlayerVariables.GIVE_CLAN_REP, false))
					{
						player.getClan().addReputationScore(493700, true);
						player.getVariables().set(PlayerVariables.GIVE_CLAN_REP, true);
						player.getClan().broadcastClanStatus();
						showHtmlFile(player, "test_server_helper042b.htm");
					}
					else
					{
						showHtmlFile(player, "test_server_helper042c.htm");
					}
				}
				else
				{
					showHtmlFile(player, "test_server_helper042.htm");
				}
				break;
			}
			case "give_105_lv":
			{
				if (!player.getVariables().getBoolean(PlayerVariables.GIVE_105_LEVEL, false))
				{
					if (player.getLevel() < 105)
					{
						player.addExpAndSp(ExperienceData.getInstance().getExpForLevel(105) - player.getExp(), 0);
						player.broadcastUserInfo();
						player.getVariables().set(PlayerVariables.GIVE_105_LEVEL, true);
					}
					else
					{
						showHtmlFile(player, "test_server_helper060.htm");
					}
				}
				else
				{
					showHtmlFile(player, "test_server_helper061.htm");
				}
				break;
			}
			case "superion_support_box":
			{
				if (!player.getVariables().getBoolean(PlayerVariables.SUPERION_SUPPORT_BOX, false))
				{
					player.getVariables().set(PlayerVariables.SUPERION_SUPPORT_BOX, true);
					giveItems(player, SUPERION_SUPPORT_BOX, 1);
					showHtmlFile(player, "test_server_helper062.htm");
				}
				else
				{
					showHtmlFile(player, "test_server_helper063.htm");
				}
				break;
			}
			case "firstclass":
			{
				if (player.isInCategory(CategoryType.FIRST_CLASS_GROUP))
				{
					if (player.getRace() == Race.ERTHEIA)
					{
						htmltext = "test_server_helper027a.htm";
					}
					else if (player.getLevel() < 20)
					{
						htmltext = "test_server_helper027.htm";
					}
					else
					{
						htmltext = getFirstOccupationChangeHtml(player);
					}
				}
				else if (player.isInCategory(CategoryType.SECOND_CLASS_GROUP))
				{
					htmltext = "test_server_helper028.htm";
				}
				else if (player.isInCategory(CategoryType.THIRD_CLASS_GROUP))
				{
					htmltext = "test_server_helper010.htm";
				}
				else if (player.isInCategory(CategoryType.FOURTH_CLASS_GROUP))
				{
					htmltext = "test_server_helper011.htm";
				}
				else if (player.isInCategory(CategoryType.SIXTH_CLASS_GROUP))
				{
					htmltext = "test_server_helper011a.htm";
				}
				break;
			}
			case "secondclass":
			{
				if (player.isInCategory(CategoryType.SECOND_CLASS_GROUP) || player.isInCategory(CategoryType.FIRST_CLASS_GROUP))
				{
					htmltext = player.getLevel() < 40 ? "test_server_helper023.htm" : getSecondOccupationChangeHtml(player);
				}
				else if (player.isInCategory(CategoryType.THIRD_CLASS_GROUP))
				{
					htmltext = "test_server_helper010.htm";
				}
				else if (player.isInCategory(CategoryType.FOURTH_CLASS_GROUP))
				{
					htmltext = "test_server_helper011.htm";
				}
				else if (player.isInCategory(CategoryType.SIXTH_CLASS_GROUP))
				{
					htmltext = "test_server_helper011a.htm";
				}
				else
				{
					htmltext = "test_server_helper029.htm";
				}
				break;
			}
			case "thirdclass":
			{
				if (player.isInCategory(CategoryType.THIRD_CLASS_GROUP) && (player.getLevel() > 75))
				{
					if (changeToNextClass(player))
					{
						player.sendPacket(new PlaySound("ItemSound.quest_fanfare_2"));
						player.broadcastUserInfo();
						htmltext = "test_server_helper021.htm";
					}
				}
				else if (player.isInCategory(CategoryType.FOURTH_CLASS_GROUP))
				{
					htmltext = "test_server_helper011.htm";
				}
				else if (player.isInCategory(CategoryType.SIXTH_CLASS_GROUP))
				{
					htmltext = "test_server_helper011a.htm";
				}
				else
				{
					htmltext = "test_server_helper024.htm";
				}
				break;
			}
			case "awaken":
			{
				if (player.isInCategory(CategoryType.FOURTH_CLASS_GROUP) && (player.getLevel() > 84))
				{
					if (changeToNextClass(player))
					{
						player.sendPacket(new PlaySound("ItemSound.quest_fanfare_2"));
						player.broadcastUserInfo();
						player.store(false); // Save player cause if server crashes before this char is saved, he will lose class and the money payed for class change.
						htmltext = "test_server_helper021.htm";
					}
				}
				else if (player.isInCategory(CategoryType.SIXTH_CLASS_GROUP))
				{
					htmltext = "test_server_helper011a.htm";
				}
				else
				{
					htmltext = "test_server_helper011b.htm";
				}
				break;
			}
			case "setclass":
			{
				if (!st.hasMoreTokens())
				{
					return null;
				}
				
				int classId = Integer.parseInt(st.nextToken());
				
				boolean canChange = false;
				if ((player.isInCategory(CategoryType.SECOND_CLASS_GROUP) || player.isInCategory(CategoryType.FIRST_CLASS_GROUP)) && (player.getLevel() >= 40)) // In retail you can skip first occupation
				{
					canChange = CategoryData.getInstance().isInCategory(CategoryType.THIRD_CLASS_GROUP, classId) || (player.isInCategory(CategoryType.FIRST_CLASS_GROUP) && CategoryData.getInstance().isInCategory(CategoryType.SECOND_CLASS_GROUP, classId));
				}
				else if (player.isInCategory(CategoryType.FIRST_CLASS_GROUP) && (player.getLevel() >= 20))
				{
					canChange = CategoryData.getInstance().isInCategory(CategoryType.SECOND_CLASS_GROUP, classId);
				}
				else if (player.isInCategory(CategoryType.THIRD_CLASS_GROUP) && (player.getLevel() >= 76))
				{
					canChange = CategoryData.getInstance().isInCategory(CategoryType.FOURTH_CLASS_GROUP, classId);
				}
				else if (player.isInCategory(CategoryType.FOURTH_CLASS_GROUP) && (player.getLevel() >= 85)) // 9
				{
					canChange = CategoryData.getInstance().isInCategory(CategoryType.SIXTH_CLASS_GROUP, classId); // 11
				}
				
				if (canChange)
				{
					int classDataIndex = -1;
					if (st.hasMoreTokens())
					{
						classDataIndex = Integer.parseInt(st.nextToken());
					}
					
					if (checkIfClassChangeHasOptions(player))
					{
						if (classDataIndex == -1)
						{
							htmltext = getHtm(player.getHtmlPrefix(), "cc_options.htm");
							htmltext = htmltext.replace("%name%", ClassListData.getInstance().getClass(classId).getClassName()); // getEscapedClientCode());
							htmltext = htmltext.replace("%options%", getClassChangeOptions(player, classId));
							return htmltext;
						}
						
						final ClassChangeData data = getClassChangeData(classDataIndex);
						if (data == null)
						{
							return null;
						}
						
						//@formatter:off
						boolean paid = data.getItemsRequired().stream()
						.filter(ich -> ich.getChance() > Rnd.get(100)) // Chance to pay the price
						.filter(ih -> player.getInventory().getInventoryItemCount(ih.getId(), -1) >= ih.getCount())
						.allMatch(ih -> player.destroyItemByItemId(getClass().getSimpleName(), ih.getId(), ih.getCount(), npc, true));
						//@formatter:on
						
						if (paid)
						{
							//@formatter:off
							data.getItemsRewarded().stream()
							.filter(ich -> ich.getChance() > Rnd.get(100)) // Chance to receive the reward
							.forEach(ih -> player.addItem(getClass().getSimpleName(), ih.getId(), ih.getCount(), npc, true));
							//@formatter:on
						}
						else
						{
							return null; // No class change if payment failed.
						}
					}
					
					player.setClassId(classId);
					if (player.isSubClassActive())
					{
						player.getSubClasses().get(player.getClassIndex()).setClassId(player.getActiveClass());
					}
					else
					{
						player.setBaseClass(player.getActiveClass());
					}
					
					// Special awakening handling for players.
					if (player.isInOneOfCategory(CategoryType.FIFTH_CLASS_GROUP, CategoryType.SIXTH_CLASS_GROUP, CategoryType.ERTHEIA_FOURTH_CLASS_GROUP))
					{
						player.broadcastPacket(new SocialAction(player.getObjectId(), 20));
						for (Entry<CategoryType, Integer> ent : AWAKE_POWER.entrySet())
						{
							if (player.isInCategory(ent.getKey()))
							{
								giveItems(player, ent.getValue(), 1);
								break;
							}
						}
						giveItems(player, player.isDualClassActive() ? CHAOS_POMANDER_DUAL_CLASS : CHAOS_POMANDER, 2);
						
						SkillTreesData.getInstance().cleanSkillUponAwakening(player);
					}
					
					player.sendPacket(new PlaySound("ItemSound.quest_fanfare_2"));
					player.broadcastUserInfo();
					player.sendSkillList();
					player.store(false); // Save player cause if server crashes before this char is saved, he will lose class and the money payed for class change.
					return "test_server_helper021.htm";
				}
				break;
			}
			case "clanlevel":
			{
				htmltext = player.isClanLeader() ? "test_server_helper022.htm" : "pl014.htm";
				break;
			}
			case "learnskills":
			{
				// Retail class master only lets you learn all third class skills.
				if (player.isInCategory(CategoryType.SIXTH_CLASS_GROUP))
				{
					htmltext = "test_server_helper001_failed.htm";
				}
				else
				{
					player.giveAvailableSkills(true, true);
				}
				break;
			}
			case "clanlevelup":
			{
				if ((player.getClan() == null) || !player.isClanLeader())
				{
					return null;
				}
				
				if (player.getClan().getLevel() >= 10)
				{
					htmltext = "test_server_helper022a.htm";
				}
				else
				{
					player.getClan().setLevel(player.getClan().getLevel() + 1);
					player.getClan().broadcastClanStatus();
				}
				break;
			}
		}
		
		return htmltext;
	}
	
	private String getFirstOccupationChangeHtml(PlayerInstance player)
	{
		switch (player.getClassId())
		{
			case FIGHTER:
				return "test_server_helper026a.htm";
			case MAGE:
				return "test_server_helper026b.htm";
			case ELVEN_FIGHTER:
				return "test_server_helper026c.htm";
			case ELVEN_MAGE:
				return "test_server_helper026d.htm";
			case DARK_FIGHTER:
				return "test_server_helper026e.htm";
			case DARK_MAGE:
				return "test_server_helper026f.htm";
			case ORC_FIGHTER:
				return "test_server_helper026g.htm";
			case ORC_MAGE:
				return "test_server_helper026h.htm";
			case DWARVEN_FIGHTER:
				return "test_server_helper026i.htm";
			case MALE_SOLDIER:
				return "test_server_helper026j.htm";
			case FEMALE_SOLDIER:
				return "test_server_helper026k.htm";
			default:
				return null;
		}
	}
	
	private String getSecondOccupationChangeHtml(PlayerInstance player)
	{
		switch (player.getClassId())
		{
			case FIGHTER:
				return "test_server_helper012.htm";
			case WARRIOR:
				return "test_server_helper012a.htm";
			case KNIGHT:
				return "test_server_helper012b.htm";
			case ROGUE:
				return "test_server_helper012c.htm";
			case MAGE:
				return "test_server_helper013.htm";
			case WIZARD:
				return "test_server_helper013a.htm";
			case CLERIC:
				return "test_server_helper013b.htm";
			case ELVEN_FIGHTER:
				return "test_server_helper014.htm";
			case ELVEN_KNIGHT:
				return "test_server_helper014a.htm";
			case ELVEN_SCOUT:
				return "test_server_helper014b.htm";
			case ELVEN_MAGE:
				return "test_server_helper015.htm";
			case ELVEN_WIZARD:
				return "test_server_helper015a.htm";
			case ORACLE:
				return "test_server_helper015b.htm";
			case DARK_FIGHTER:
				return "test_server_helper016.htm";
			case PALUS_KNIGHT:
				return "test_server_helper016a.htm";
			case ASSASSIN:
				return "test_server_helper016b.htm";
			case DARK_MAGE:
				return "test_server_helper017.htm";
			case DARK_WIZARD:
				return "test_server_helper017a.htm";
			case SHILLIEN_ORACLE:
				return "test_server_helper017b.htm";
			case ORC_FIGHTER:
				return "test_server_helper018.htm";
			case ORC_RAIDER:
				return "test_server_helper018a.htm";
			case ORC_MONK:
				return "test_server_helper018b.htm";
			case ORC_MAGE:
			case ORC_SHAMAN:
				return "test_server_helper019.htm";
			case DWARVEN_FIGHTER:
				return "test_server_helper020.htm";
			case ARTISAN:
				return "test_server_helper020b.htm";
			case SCAVENGER:
				return "test_server_helper020a.htm";
			case TROOPER:
				return "test_server_helper020c.htm";
			case WARDER:
				return "test_server_helper020d.htm";
			case ERTHEIA_FIGHTER:
				return "test_server_helper020e.htm";
			case ERTHEIA_WIZARD:
				return "test_server_helper020f.htm";
			default:
				return null;
		}
	}
	
	private boolean changeToNextClass(PlayerInstance player)
	{
		final ClassId newClass = player.getClassId().getNextClassIds().stream().findFirst().orElse(null);
		if (newClass == null)
		{
			LOGGER.warn("No new classId found for player {}", player);
			return false;
		}
		else if (newClass == player.getClassId())
		{
			LOGGER.warn("New classId found for player {} is exactly the same as the one he currently is!", player);
			return false;
		}
		else if (checkIfClassChangeHasOptions(player))
		{
			String html = getHtm(player.getHtmlPrefix(), "cc_options.htm");
			html = html.replace("%name%", ClassListData.getInstance().getClass(newClass.getId()).getClassName()); // getEscapedClientCode());
			html = html.replace("%options%", getClassChangeOptions(player, newClass.getId()));
			showResult(player, html);
			return false;
		}
		else
		{
			player.setClassId(newClass.getId());
			if (player.isSubClassActive())
			{
				player.getSubClasses().get(player.getClassIndex()).setClassId(player.getActiveClass());
			}
			else
			{
				player.setBaseClass(player.getActiveClass());
			}
			player.sendSkillList();
			return true;
		}
	}
	
	private void showPopupWindow(PlayerInstance player)
	{
		if (!_showPopupWindow)
		{
			return;
		}
		
		//@formatter:off
		if ((player.isInCategory(CategoryType.FIRST_CLASS_GROUP) && (player.getLevel() >= 20)) ||
			((player.isInCategory(CategoryType.SECOND_CLASS_GROUP) || player.isInCategory(CategoryType.FIRST_CLASS_GROUP)) && (player.getLevel() >= 40)) ||
			(player.isInCategory(CategoryType.THIRD_CLASS_GROUP) && (player.getLevel() >= 76)) ||
			(player.isInCategory(CategoryType.FOURTH_CLASS_GROUP) && (player.getLevel() >= 85)))
		//@formatter:on
		{
			player.sendPacket(new TutorialShowQuestionMark(1001, 1));
		}
	}
	
	@RegisterEvent(EventType.ON_PLAYER_PRESS_TUTORIAL_MARK)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void onPlayerPressTutorialMark(OnPlayerPressTutorialMark event)
	{
		final PlayerInstance player = event.getActiveChar();
		
		if (!_showPopupWindow || (event.getQuestId() != 1001))
		{
			return;
		}
		
		String html = null;
		if ((player.isInCategory(CategoryType.SECOND_CLASS_GROUP) || player.isInCategory(CategoryType.FIRST_CLASS_GROUP)) && (player.getLevel() >= 40)) // In retail you can skip first occupation
		{
			html = getHtm(player.getHtmlPrefix(), onAdvEvent("secondclass", null, player));
		}
		else if (player.isInCategory(CategoryType.FIRST_CLASS_GROUP) && (player.getLevel() >= 20))
		{
			html = getHtm(player.getHtmlPrefix(), onAdvEvent("firstclass", null, player));
		}
		else if (player.isInCategory(CategoryType.THIRD_CLASS_GROUP) && (player.getLevel() >= 76))
		{
			html = getHtm(player.getHtmlPrefix(), "qm_thirdclass.htm");
		}
		else if (player.isInCategory(CategoryType.FOURTH_CLASS_GROUP) && (player.getLevel() >= 85)) // 9
		{
			html = getHtm(player.getHtmlPrefix(), "qm_awaken.htm");
		}
		
		if (html != null)
		{
			showResult(event.getActiveChar(), html);
			// player.sendPacket(new TutorialShowHtml(html));
		}
	}
	
	@RegisterEvent(EventType.ON_PLAYER_BYPASS)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void OnPlayerBypass(OnPlayerBypass event)
	{
		if (event.getCommand().startsWith("Quest ClassMaster "))
		{
			String html = onAdvEvent(event.getCommand().substring(18), null, event.getActiveChar());
			event.getActiveChar().sendPacket(TutorialCloseHtml.STATIC_PACKET);
			showResult(event.getActiveChar(), html);
		}
	}
	
	@RegisterEvent(EventType.ON_PLAYER_PROFESSION_CHANGE)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void OnPlayerProfessionChange(OnPlayerProfessionChange event)
	{
		showPopupWindow(event.getActiveChar());
	}
	
	@RegisterEvent(EventType.ON_PLAYER_LEVEL_CHANGED)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void OnPlayerLevelChanged(OnPlayerLevelChanged event)
	{
		showPopupWindow(event.getActiveChar());
	}
	
	@RegisterEvent(EventType.ON_PLAYER_LOGIN)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void OnPlayerLogin(OnPlayerLogin event)
	{
		showPopupWindow(event.getActiveChar());
	}
	
	private String getClassChangeOptions(PlayerInstance player, int selectedClassId)
	{
		final StringBuilder sb = new StringBuilder();
		
		for (int i = 0; i < _classChangeData.size(); i++)
		{
			ClassChangeData option = getClassChangeData(i);
			if ((option == null) || !option.getCategories().stream().anyMatch(ct -> player.isInCategory(ct)))
			{
				continue;
			}
			
			sb.append("<tr><td><img src=L2UI_CT1.ChatBalloon_DF_TopCenter width=276 height=1 /></td></tr>");
			sb.append("<tr><td><table bgcolor=3f3f3f width=100%>");
			sb.append("<tr><td align=center><a action=\"bypass -h Quest ClassMaster setclass " + selectedClassId + " " + i + "\">" + option.getName() + ":</a></td></tr>");
			sb.append("<tr><td><table width=276>");
			sb.append("<tr><td>Requirements:</td></tr>");
			if (option.getItemsRequired().isEmpty())
			{
				sb.append("<tr><td><font color=LEVEL>Free</font></td></tr>");
			}
			else
			{
				option.getItemsRequired().forEach(ih ->
				{
					if (ih.getChance() >= 100)
					{
						sb.append("<tr><td><font color=\"LEVEL\">" + ih.getCount() + "</font></td><td>" + ItemTable.getInstance().getTemplate(ih.getId()).getName() + "</td><td width=30></td></tr>");
					}
					else
					{
						sb.append("<tr><td><font color=\"LEVEL\">" + ih.getCount() + "</font></td><td>" + ItemTable.getInstance().getTemplate(ih.getId()).getName() + "</td><td width=30><font color=LEVEL>" + ih.getChance() + "%</font></td></tr>");
					}
				});
			}
			sb.append("<tr><td>Rewards:</td></tr>");
			if (option.getItemsRewarded().isEmpty())
			{
				if (option.isRewardNoblesse())
				{
					sb.append("<tr><td><font color=\"LEVEL\">Noblesse status.</font></td></tr>");
				}
				
				if (option.isRewardHero())
				{
					sb.append("<tr><td><font color=\"LEVEL\">Hero status.</font></td></tr>");
				}
				
				if (!option.isRewardNoblesse() && !option.isRewardHero())
				{
					sb.append("<tr><td><font color=LEVEL>none</font></td></tr>");
				}
			}
			else
			{
				option.getItemsRewarded().forEach(ih ->
				{
					if (ih.getChance() >= 100)
					{
						sb.append("<tr><td><font color=\"LEVEL\">" + ih.getCount() + "</font></td><td>" + ItemTable.getInstance().getTemplate(ih.getId()).getName() + "</td><td width=30></td></tr>");
					}
					else
					{
						sb.append("<tr><td><font color=\"LEVEL\">" + ih.getCount() + "</font></td><td>" + ItemTable.getInstance().getTemplate(ih.getId()).getName() + "</td><td width=30><font color=LEVEL>" + ih.getChance() + "%</font></td></tr>");
					}
				});
				
				if (option.isRewardNoblesse())
				{
					sb.append("<tr><td><font color=\"LEVEL\">Noblesse status.</font></td></tr>");
				}
				if (option.isRewardHero())
				{
					sb.append("<tr><td><font color=\"LEVEL\">Hero status.</font></td></tr>");
				}
			}
			sb.append("</table></td></tr>");
			sb.append("</table></td></tr>");
			sb.append("<tr><td><img src=L2UI_CT1.ChatBalloon_DF_TopCenter width=276 height=1 /></td></tr>");
		}
		
		return sb.toString();
	}
	
	private static class ClassChangeData
	{
		private final String _name;
		private final List<CategoryType> _appliedCategories;
		private boolean _rewardNoblesse;
		private boolean _rewardHero;
		private List<ItemChanceHolder> _itemsRequired;
		private List<ItemChanceHolder> _itemsRewarded;
		
		public ClassChangeData(String name, List<CategoryType> appliedCategories)
		{
			_name = name;
			_appliedCategories = appliedCategories != null ? appliedCategories : Collections.emptyList();
		}
		
		public String getName()
		{
			return _name;
		}
		
		public List<CategoryType> getCategories()
		{
			return _appliedCategories != null ? _appliedCategories : Collections.emptyList();
		}
		
		public boolean isInCategory(PlayerInstance player)
		{
			if (_appliedCategories != null)
			{
				for (CategoryType category : _appliedCategories)
				{
					if (player.isInCategory(category))
					{
						return true;
					}
				}
			}
			
			return false;
		}
		
		public boolean isRewardNoblesse()
		{
			return _rewardNoblesse;
		}
		
		public void setRewardNoblesse(boolean rewardNoblesse)
		{
			_rewardNoblesse = rewardNoblesse;
		}
		
		public boolean isRewardHero()
		{
			return _rewardHero;
		}
		
		public void setRewardHero(boolean rewardHero)
		{
			_rewardHero = rewardHero;
		}
		
		void setItemsRequired(List<ItemChanceHolder> itemsRequired)
		{
			_itemsRequired = itemsRequired;
		}
		
		public List<ItemChanceHolder> getItemsRequired()
		{
			return _itemsRequired != null ? _itemsRequired : Collections.emptyList();
		}
		
		void setItemsRewarded(List<ItemChanceHolder> itemsRewarded)
		{
			_itemsRewarded = itemsRewarded;
		}
		
		public List<ItemChanceHolder> getItemsRewarded()
		{
			return _itemsRewarded != null ? _itemsRewarded : Collections.emptyList();
		}
	}
	
	private boolean checkIfClassChangeHasOptions(PlayerInstance player)
	{
		boolean showOptions = _classChangeData.stream().filter(ccd -> !ccd.getItemsRequired().isEmpty()).anyMatch(ccd -> ccd.isInCategory(player)); // Check if there are requirements
		if (!showOptions)
		{
			showOptions = _classChangeData.stream().filter(ccd -> !ccd.getItemsRewarded().isEmpty()).filter(ccd -> ccd.isInCategory(player)).count() > 1; // Check if there is more than 1 reward to chose.
		}
		
		return showOptions;
	}
	
	private ClassChangeData getClassChangeData(int index)
	{
		if ((index >= 0) && (index < _classChangeData.size()))
		{
			return _classChangeData.get(index);
		}
		
		return null;
	}
	
	@GameScript
	public static void main()
	{
		new ClassMaster();
	}
}