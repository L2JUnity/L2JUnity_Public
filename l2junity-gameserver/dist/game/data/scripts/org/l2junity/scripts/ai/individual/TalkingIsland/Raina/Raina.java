/*
 * Copyright (C) 2004-2015 L2J DataPack
 * 
 * This file is part of L2J DataPack.
 * 
 * L2J DataPack is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J DataPack is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2junity.scripts.ai.individual.TalkingIsland.Raina;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.l2junity.gameserver.config.PlayerConfig;
import org.l2junity.gameserver.data.xml.impl.CategoryData;
import org.l2junity.gameserver.data.xml.impl.ClassListData;
import org.l2junity.gameserver.data.xml.impl.SkillTreesData;
import org.l2junity.gameserver.enums.CategoryType;
import org.l2junity.gameserver.enums.Race;
import org.l2junity.gameserver.enums.SubclassInfoType;
import org.l2junity.gameserver.model.actor.Npc;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.base.ClassId;
import org.l2junity.gameserver.model.base.SubClass;
import org.l2junity.gameserver.model.events.EventType;
import org.l2junity.gameserver.model.events.ListenerRegisterType;
import org.l2junity.gameserver.model.events.annotations.Id;
import org.l2junity.gameserver.model.events.annotations.RegisterEvent;
import org.l2junity.gameserver.model.events.annotations.RegisterType;
import org.l2junity.gameserver.model.events.impl.character.npc.OnNpcMenuSelect;
import org.l2junity.gameserver.network.client.send.ExSubjobInfo;
import org.l2junity.gameserver.network.client.send.NpcHtmlMessage;
import org.l2junity.gameserver.network.client.send.string.SystemMessageId;
import org.l2junity.gameserver.scripting.annotations.GameScript;
import org.l2junity.scripts.ai.AbstractNpcAI;
import org.l2junity.scripts.quests.Q10385_RedThreadOfFate.Q10385_RedThreadOfFate;
import org.l2junity.scripts.quests.Q10472_WindsOfFateEncroachingShadows.Q10472_WindsOfFateEncroachingShadows;

/**
 * Raina AI.
 * @author St3eT
 */
public final class Raina extends AbstractNpcAI
{
	// NPC
	private static final int RAINA = 33491;
	// Items
	private static final int SUBCLASS_CERTIFICATE = 30433;
	private static final int CHAOS_POMANDER = 37375;
	// Misc
	private static final Set<ClassId> mainSubclassSet;
	private static final Set<ClassId> neverSubclassed = EnumSet.of(ClassId.OVERLORD, ClassId.WARSMITH);
	private static final Set<ClassId> subclasseSet1 = EnumSet.of(ClassId.DARK_AVENGER, ClassId.PALADIN, ClassId.TEMPLE_KNIGHT, ClassId.SHILLIEN_KNIGHT);
	private static final Set<ClassId> subclasseSet2 = EnumSet.of(ClassId.TREASURE_HUNTER, ClassId.ABYSS_WALKER, ClassId.PLAINS_WALKER);
	private static final Set<ClassId> subclasseSet3 = EnumSet.of(ClassId.HAWKEYE, ClassId.SILVER_RANGER, ClassId.PHANTOM_RANGER);
	private static final Set<ClassId> subclasseSet4 = EnumSet.of(ClassId.WARLOCK, ClassId.ELEMENTAL_SUMMONER, ClassId.PHANTOM_SUMMONER);
	private static final Set<ClassId> subclasseSet5 = EnumSet.of(ClassId.SORCERER, ClassId.SPELLSINGER, ClassId.SPELLHOWLER);
	private static final EnumMap<ClassId, Set<ClassId>> subclassSetMap = new EnumMap<>(ClassId.class);
	
	static
	{
		final Set<ClassId> subclasses = CategoryData.getInstance().getCategoryByType(CategoryType.THIRD_CLASS_GROUP).stream().map(ClassId::getClassId).collect(Collectors.toSet());
		subclasses.removeAll(neverSubclassed);
		mainSubclassSet = subclasses;
		subclassSetMap.put(ClassId.DARK_AVENGER, subclasseSet1);
		subclassSetMap.put(ClassId.PALADIN, subclasseSet1);
		subclassSetMap.put(ClassId.TEMPLE_KNIGHT, subclasseSet1);
		subclassSetMap.put(ClassId.SHILLIEN_KNIGHT, subclasseSet1);
		subclassSetMap.put(ClassId.TREASURE_HUNTER, subclasseSet2);
		subclassSetMap.put(ClassId.ABYSS_WALKER, subclasseSet2);
		subclassSetMap.put(ClassId.PLAINS_WALKER, subclasseSet2);
		subclassSetMap.put(ClassId.HAWKEYE, subclasseSet3);
		subclassSetMap.put(ClassId.SILVER_RANGER, subclasseSet3);
		subclassSetMap.put(ClassId.PHANTOM_RANGER, subclasseSet3);
		subclassSetMap.put(ClassId.WARLOCK, subclasseSet4);
		subclassSetMap.put(ClassId.ELEMENTAL_SUMMONER, subclasseSet4);
		subclassSetMap.put(ClassId.PHANTOM_SUMMONER, subclasseSet4);
		subclassSetMap.put(ClassId.SORCERER, subclasseSet5);
		subclassSetMap.put(ClassId.SPELLSINGER, subclasseSet5);
		subclassSetMap.put(ClassId.SPELLHOWLER, subclasseSet5);
	}
	
	private static final Map<CategoryType, Integer> classCloak = new HashMap<>();
	{
		classCloak.put(CategoryType.SIXTH_SIGEL_GROUP, 30310); // Abelius Cloak
		classCloak.put(CategoryType.SIXTH_TIR_GROUP, 30311); // Sapyros Cloak Grade
		classCloak.put(CategoryType.SIXTH_OTHEL_GROUP, 30312); // Ashagen Cloak Grade
		classCloak.put(CategoryType.SIXTH_YR_GROUP, 30313); // Cranigg Cloak Grade
		classCloak.put(CategoryType.SIXTH_FEOH_GROUP, 30314); // Soltkreig Cloak Grade
		classCloak.put(CategoryType.SIXTH_WYNN_GROUP, 30315); // Naviarope Cloak Grade
		classCloak.put(CategoryType.SIXTH_IS_GROUP, 30316); // Leister Cloak Grade
		classCloak.put(CategoryType.SIXTH_EOLH_GROUP, 30317); // Laksis Cloak Grade
	}
	
	private static final Map<CategoryType, Integer> powerItem = new HashMap<>();
	{
		powerItem.put(CategoryType.SIXTH_SIGEL_GROUP, 32264); // Abelius Power
		powerItem.put(CategoryType.SIXTH_TIR_GROUP, 32265); // Sapyros Power
		powerItem.put(CategoryType.SIXTH_OTHEL_GROUP, 32266); // Ashagen Power
		powerItem.put(CategoryType.SIXTH_YR_GROUP, 32267); // Cranigg Power
		powerItem.put(CategoryType.SIXTH_FEOH_GROUP, 32268); // Soltkreig Power
		powerItem.put(CategoryType.SIXTH_WYNN_GROUP, 32269); // Naviarope Power
		powerItem.put(CategoryType.SIXTH_IS_GROUP, 32270); // Leister Power
		powerItem.put(CategoryType.SIXTH_EOLH_GROUP, 32271); // Laksis Power
	}
	
	private static final List<ClassId> dualClassList = new ArrayList<>();
	
	{
		dualClassList.addAll(Arrays.asList(ClassId.SIGEL_PHOENIX_KNIGHT, ClassId.SIGEL_HELL_KNIGHT, ClassId.SIGEL_EVA_TEMPLAR, ClassId.SIGEL_SHILLIEN_TEMPLAR));
		dualClassList.addAll(Arrays.asList(ClassId.TYRR_DUELIST, ClassId.TYRR_DREADNOUGHT, ClassId.TYRR_TITAN, ClassId.TYRR_GRAND_KHAVATARI, ClassId.TYRR_DOOMBRINGER));
		dualClassList.addAll(Arrays.asList(ClassId.OTHELL_ADVENTURER, ClassId.OTHELL_WIND_RIDER, ClassId.OTHELL_GHOST_HUNTER, ClassId.OTHELL_FORTUNE_SEEKER));
		dualClassList.addAll(Arrays.asList(ClassId.YUL_SAGITTARIUS, ClassId.YUL_MOONLIGHT_SENTINEL, ClassId.YUL_GHOST_SENTINEL, ClassId.YUL_TRICKSTER));
		dualClassList.addAll(Arrays.asList(ClassId.FEOH_ARCHMAGE, ClassId.FEOH_SOULTAKER, ClassId.FEOH_MYSTIC_MUSE, ClassId.FEOH_STORM_SCREAMER, ClassId.FEOH_SOUL_HOUND));
		dualClassList.addAll(Arrays.asList(ClassId.ISS_HIEROPHANT, ClassId.ISS_SWORD_MUSE, ClassId.ISS_SPECTRAL_DANCER, ClassId.ISS_DOOMCRYER));
		dualClassList.addAll(Arrays.asList(ClassId.WYNN_ARCANA_LORD, ClassId.WYNN_ELEMENTAL_MASTER, ClassId.WYNN_SPECTRAL_MASTER));
		dualClassList.addAll(Arrays.asList(ClassId.AEORE_CARDINAL, ClassId.AEORE_EVA_SAINT, ClassId.AEORE_SHILLIEN_SAINT));
	}
	
	// @formatter:off
	private static final int[] REAWAKEN_PRICE =
	{
		100_000_000, 90_000_000, 80_000_000, 70_000_000, 60_000_000, 50_000_000, 40_000_000, 30_000_000, 20_000_000, 10_000_000
	};
	// @formatter:on
	
	private Raina()
	{
		addStartNpc(RAINA);
		addFirstTalkId(RAINA);
		addTalkId(RAINA);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		String htmltext = null;
		switch (event)
		{
			case "33491-01.html":
			case "33491-02.html":
			case "33491-03.html":
			case "33491-04.html":
			case "33491-05.html":
			case "reawakenCancel.html":
			{
				htmltext = event;
				break;
			}
			case "addSubclass":
			{
				if (player.isTransformed())
				{
					htmltext = "noTransform.html";
				}
				else if (player.hasSummon())
				{
					htmltext = "noSummon.html";
				}
				else if (player.getRace() == Race.ERTHEIA)
				{
					htmltext = "noErtheia.html";
				}
				else if (!PlayerConfig.ALT_GAME_SUBCLASS_WITHOUT_QUESTS && !player.hasQuestCompleted(Q10385_RedThreadOfFate.class.getSimpleName()))
				{
					htmltext = "noQuest.html";
				}
				else if (!hasAllSubclassLeveled(player) || (player.getTotalSubClasses() >= PlayerConfig.MAX_SUBCLASS))
				{
					htmltext = "addFailed.html";
				}
				else if (!player.isInventoryUnder80(true) || (player.getWeightPenalty() >= 2))
				{
					htmltext = "inventoryLimit.html";
				}
				else
				{
					final Set<ClassId> availSubs = getAvailableSubClasses(player);
					final StringBuilder sb = new StringBuilder();
					final NpcHtmlMessage html = getNpcHtmlMessage(player, npc, "subclassList.html");
					
					if ((availSubs == null) || availSubs.isEmpty())
					{
						break;
					}
					
					for (ClassId subClass : availSubs)
					{
						if (subClass != null)
						{
							final int classId = subClass.getId();
							final int npcStringId = 11170000 + classId;
							sb.append("<fstring p1=\"0\" p2=\"" + classId + "\">" + npcStringId + "</fstring>");
						}
					}
					html.replace("%subclassList%", sb.toString());
					player.sendPacket(html);
				}
				break;
			}
			case "removeSubclass":
			{
				if (player.isTransformed())
				{
					htmltext = "noTransform.html";
				}
				else if (player.hasSummon())
				{
					htmltext = "noSummon.html";
				}
				else if (player.getRace() == Race.ERTHEIA)
				{
					htmltext = "noErtheia.html";
				}
				else if (!player.isInventoryUnder80(true) || (player.getWeightPenalty() >= 2))
				{
					htmltext = "inventoryLimit.html";
				}
				else if (player.getSubClasses().isEmpty())
				{
					htmltext = "noSubChange.html";
				}
				else
				{
					final StringBuilder sb = new StringBuilder();
					final NpcHtmlMessage html = getNpcHtmlMessage(player, npc, "subclassRemoveList.html");
					
					for (SubClass subClass : player.getSubClasses().values())
					{
						if (subClass != null)
						{
							final int classId = subClass.getClassId();
							final int npcStringId = 11170000 + classId;
							sb.append("<fstring p1=\"2\" p2=\"" + subClass.getClassIndex() + "\">" + npcStringId + "</fstring>");
						}
					}
					html.replace("%removeList%", sb.toString());
					player.sendPacket(html);
				}
				break;
			}
			case "changeSubclass": // TODO: Finish me
			{
				if (player.isTransformed())
				{
					htmltext = "noTransform.html";
				}
				else if (player.hasSummon())
				{
					htmltext = "noSummon.html";
				}
				else if (player.getRace() == Race.ERTHEIA)
				{
					htmltext = "noErtheia.html";
				}
				else if (!player.isInventoryUnder80(true) || (player.getWeightPenalty() >= 2))
				{
					htmltext = "inventoryLimit.html";
				}
				else if (player.getSubClasses().isEmpty())
				{
					htmltext = "noSubChange.html";
				}
				else if (!hasQuestItems(player, SUBCLASS_CERTIFICATE))
				{
					htmltext = "noCertificate.html";
				}
				else
				{
					player.sendMessage("Not done yet.");
				}
				break;
			}
			case "ertheiaDualClass":
			{
				// TODO: Maybe html is different when you have 85lvl but you haven't completed quest
				if (player.hasDualClass() || (!PlayerConfig.ALT_GAME_SUBCLASS_WITHOUT_QUESTS && !player.hasQuestCompleted(Q10472_WindsOfFateEncroachingShadows.class.getSimpleName())))
				{
					htmltext = "addDualClassErtheiaFailed.html";
				}
				else
				{
					htmltext = "addDualClassErtheia.html";
				}
				break;
			}
			case "addDualClass_SIXTH_SIGEL_GROUP":
			case "addDualClass_SIXTH_TIR_GROUP":
			case "addDualClass_SIXTH_OTHEL_GROUP":
			case "addDualClass_SIXTH_YR_GROUP":
			case "addDualClass_SIXTH_FEOH_GROUP":
			case "addDualClass_SIXTH_IS_GROUP":
			case "addDualClass_SIXTH_WYNN_GROUP":
			case "addDualClass_SIXTH_EOLH_GROUP":
			{
				final CategoryType cType = CategoryType.valueOf(event.replace("addDualClass_", ""));
				
				if (cType == null)
				{
					_logger.warn("Cannot parse CategoryType, event: " + event);
				}
				
				final StringBuilder sb = new StringBuilder();
				final NpcHtmlMessage html = getNpcHtmlMessage(player, npc, "addDualClassErtheiaList.html");
				
				for (ClassId dualClasses : getDualClasses(player, cType))
				{
					if (dualClasses != null)
					{
						sb.append("<button value=\"" + ClassListData.getInstance().getClass(dualClasses.getId()).getClassName() + "\" action=\"bypass -h menu_select?ask=6&reply=" + dualClasses.getId() + "\" width=\"200\" height=\"31\" back=\"L2UI_CT1.HtmlWnd_DF_Awake_Down\" fore=\"L2UI_CT1.HtmlWnd_DF_Awake\"><br>");
					}
				}
				html.replace("%dualclassList%", sb.toString());
				player.sendPacket(html);
				break;
			}
			case "reawakenDualclass":
			{
				if (player.isTransformed())
				{
					htmltext = "reawakenNoTransform.html";
				}
				else if (player.hasSummon())
				{
					htmltext = "reawakenNoSummon.html";
				}
				else if (!player.hasDualClass() || (player.getLevel() < 85) || !player.isDualClassActive() || !player.isAwakenedClass())
				{
					htmltext = "reawakenNoDual.html";
				}
				else if (!player.isInventoryUnder80(true) || (player.getWeightPenalty() >= 2))
				{
					htmltext = "reawakenInventoryLimit.html";
				}
				else
				{
					final NpcHtmlMessage html = getNpcHtmlMessage(player, npc, "reawaken.html");
					final int index = player.getLevel() > 94 ? REAWAKEN_PRICE.length - 1 : player.getLevel() - 85;
					html.replace("%price%", REAWAKEN_PRICE[index]);
					player.sendPacket(html);
				}
				break;
			}
			case "reawakenDualclassConfirm":
			{
				final int index = player.getLevel() > 94 ? REAWAKEN_PRICE.length - 1 : player.getLevel() - 85;
				
				if (!player.hasDualClass() || !player.isDualClassActive() || !player.isAwakenedClass())
				{
					htmltext = "reawakenNoDual.html";
				}
				else if (!player.isInventoryUnder80(true) || (player.getWeightPenalty() >= 2))
				{
					htmltext = "reawakenInventoryLimit.html";
				}
				else if ((player.getAdena() < REAWAKEN_PRICE[index]) || !hasQuestItems(player, getCloakId(player)))
				{
					final NpcHtmlMessage html = getNpcHtmlMessage(player, npc, "reawakenNoFee.html");
					html.replace("%price%", REAWAKEN_PRICE[index]);
					player.sendPacket(html);
				}
				else if (player.isTransformed())
				{
					htmltext = "reawakenNoTransform.html";
				}
				else if (player.hasSummon())
				{
					htmltext = "reawakenNoSummon.html";
				}
				else
				{
					final NpcHtmlMessage html = getNpcHtmlMessage(player, npc, "reawakenList.html");
					player.sendPacket(html);
				}
				break;
			}
			case "reawaken_SIXTH_SIGEL_GROUP":
			case "reawaken_SIXTH_TIR_GROUP":
			case "reawaken_SIXTH_OTHEL_GROUP":
			case "reawaken_SIXTH_YR_GROUP":
			case "reawaken_SIXTH_FEOH_GROUP":
			case "reawaken_SIXTH_IS_GROUP":
			case "reawaken_SIXTH_WYNN_GROUP":
			case "reawaken_SIXTH_EOLH_GROUP":
			{
				final CategoryType cType = CategoryType.valueOf(event.replace("reawaken_", ""));
				
				if (cType == null)
				{
					_logger.warn("Cannot parse CategoryType, event: " + event);
				}
				
				final StringBuilder sb = new StringBuilder();
				final NpcHtmlMessage html = getNpcHtmlMessage(player, npc, "reawakenClassList.html");
				
				for (ClassId dualClasses : getDualClasses(player, cType))
				{
					if (dualClasses != null)
					{
						sb.append("<button value=\"" + ClassListData.getInstance().getClass(dualClasses.getId()).getClassName() + "\" action=\"bypass -h menu_select?ask=5&reply=" + dualClasses.getId() + "\" width=\"200\" height=\"31\" back=\"L2UI_CT1.HtmlWnd_DF_Awake_Down\" fore=\"L2UI_CT1.HtmlWnd_DF_Awake\"><br>");
					}
				}
				html.replace("%dualclassList%", sb.toString());
				player.sendPacket(html);
				break;
			}
		}
		return htmltext;
	}
	
	@RegisterEvent(EventType.ON_NPC_MENU_SELECT)
	@RegisterType(ListenerRegisterType.NPC)
	@Id(RAINA)
	public final void OnNpcMenuSelect(OnNpcMenuSelect event)
	{
		final PlayerInstance player = event.getTalker();
		final Npc npc = event.getNpc();
		final int ask = event.getAsk();
		
		switch (ask)
		{
			case 0: // Add subclass confirm menu
			{
				final int classId = event.getReply();
				final StringBuilder sb = new StringBuilder();
				final NpcHtmlMessage html = getNpcHtmlMessage(player, npc, "addConfirm.html");
				
				if (!isValidNewSubClass(player, classId))
				{
					return;
				}
				
				final int npcStringId = 11170000 + classId;
				sb.append("<fstring p1=\"1\" p2=\"" + classId + "\">" + npcStringId + "</fstring>");
				html.replace("%confirmButton%", sb.toString());
				player.sendPacket(html);
				break;
			}
			case 1: // Add subclass
			{
				final int classId = event.getReply();
				if (!isValidNewSubClass(player, classId))
				{
					return;
				}
				
				if (!player.addSubClass(classId, player.getTotalSubClasses() + 1, false))
				{
					return;
				}
				
				player.setActiveClass(player.getTotalSubClasses());
				player.sendPacket(new ExSubjobInfo(player, SubclassInfoType.NEW_SLOT_USED));
				player.sendPacket(SystemMessageId.THE_NEW_SUBCLASS_HAS_BEEN_ADDED);
				player.sendPacket(getNpcHtmlMessage(player, npc, "addSuccess.html"));
				break;
			}
			case 2: // Remove (change) subclass list
			{
				final int subclassIndex = event.getReply();
				final Set<ClassId> availSubs = getAvailableSubClasses(player);
				final StringBuilder sb = new StringBuilder();
				final NpcHtmlMessage html = getNpcHtmlMessage(player, npc, "removeSubclassList.html");
				
				if ((availSubs == null) || availSubs.isEmpty())
				{
					return;
				}
				
				for (ClassId subClass : availSubs)
				{
					if (subClass != null)
					{
						final int classId = subClass.getId();
						final int npcStringId = 11170000 + classId;
						sb.append("<fstring p1=\"3\" p2=\"" + classId + "\">" + npcStringId + "</fstring>");
					}
				}
				npc.getVariables().set("SUBCLASS_INDEX_" + player.getObjectId(), subclassIndex);
				html.replace("%subclassList%", sb.toString());
				player.sendPacket(html);
				break;
			}
			case 3: // Remove (change) subclass confirm menu
			{
				final int classId = event.getReply();
				final int classIndex = npc.getVariables().getInt("SUBCLASS_INDEX_" + player.getObjectId(), -1);
				if (classIndex < 0)
				{
					return;
				}
				
				final StringBuilder sb = new StringBuilder();
				final NpcHtmlMessage html = getNpcHtmlMessage(player, npc, "addConfirm2.html");
				final int npcStringId = 11170000 + classId;
				sb.append("<fstring p1=\"4\" p2=\"" + classId + "\">" + npcStringId + "</fstring>");
				html.replace("%confirmButton%", sb.toString());
				player.sendPacket(html);
				break;
			}
			case 4: // Remove (change) subclass
			{
				final int classId = event.getReply();
				final int classIndex = npc.getVariables().getInt("SUBCLASS_INDEX_" + player.getObjectId(), -1);
				if (classIndex < 0)
				{
					return;
				}
				
				player.stopActions();
				
				if (player.modifySubClass(classIndex, classId, false))
				{
					player.abortCast();
					player.getEffectList().stopEffects(b -> !b.getSkill().isIrreplacableBuff(), true, true);
					player.stopCubics();
					player.setActiveClass(classIndex);
					player.sendPacket(new ExSubjobInfo(player, SubclassInfoType.CLASS_CHANGED));
					player.sendPacket(getNpcHtmlMessage(player, npc, "addSuccess.html"));
					player.sendPacket(SystemMessageId.THE_NEW_SUBCLASS_HAS_BEEN_ADDED);
				}
				break;
			}
			case 5: // Reawaken (change dual class)
			{
				final int classId = event.getReply();
				if (player.isTransformed() || player.hasSummon() || (!player.hasDualClass() || !player.isDualClassActive() || !player.isAwakenedClass()))
				{
					break;
				}
				
				// Validating classId
				if (!getDualClasses(player, null).contains(ClassId.getClassId(classId)))
				{
					break;
				}
				
				final int index = player.getLevel() > 94 ? REAWAKEN_PRICE.length - 1 : player.getLevel() - 85;
				if ((player.getAdena() < REAWAKEN_PRICE[index]) || !hasQuestItems(player, getCloakId(player)))
				{
					final NpcHtmlMessage html = getNpcHtmlMessage(player, npc, "reawakenNoFee.html");
					html.replace("%price%", REAWAKEN_PRICE[index]);
					player.sendPacket(html);
					break;
				}
				
				player.reduceAdena((getClass().getSimpleName() + "_Reawaken"), REAWAKEN_PRICE[index], npc, true);
				takeItems(player, getCloakId(player), 1);
				
				final int classIndex = player.getClassIndex();
				if (player.modifySubClass(classIndex, classId, true))
				{
					player.abortCast();
					player.getEffectList().stopEffects(b -> !b.getSkill().isIrreplacableBuff(), true, true);
					player.stopCubics();
					player.setActiveClass(classIndex);
					player.sendPacket(new ExSubjobInfo(player, SubclassInfoType.CLASS_CHANGED));
					player.sendPacket(getNpcHtmlMessage(player, npc, "reawakenSuccess.html"));
					SkillTreesData.getInstance().cleanSkillUponAwakening(player);
					player.sendSkillList();
					giveItems(player, getCloakId(player), 1);
				}
				break;
			}
			case 6: // Add dual class for ertheia
			{
				final int classId = event.getReply();
				if (player.isTransformed() || player.hasSummon() || player.hasDualClass())
				{
					break;
				}
				
				if (!PlayerConfig.ALT_GAME_SUBCLASS_WITHOUT_QUESTS && !player.hasQuestCompleted(Q10472_WindsOfFateEncroachingShadows.class.getSimpleName()))
				{
					break;
				}
				
				// Validating classId
				if (!getDualClasses(player, null).contains(ClassId.getClassId(classId)))
				{
					break;
				}
				
				if (player.addSubClass(classId, player.getTotalSubClasses() + 1, true))
				{
					player.setActiveClass(player.getTotalSubClasses());
					player.sendPacket(new ExSubjobInfo(player, SubclassInfoType.NEW_SLOT_USED));
					player.sendPacket(SystemMessageId.THE_NEW_SUBCLASS_HAS_BEEN_ADDED);
					player.sendPacket(getNpcHtmlMessage(player, npc, "addSuccess.html"));
					SkillTreesData.getInstance().cleanSkillUponAwakening(player);
					player.sendSkillList();
					giveItems(player, getPowerItemId(player), 1);
					giveItems(player, CHAOS_POMANDER, 2);
				}
				break;
			}
		}
	}
	
	/**
	 * Returns list of available subclasses Base class and already used subclasses removed
	 * @param player
	 * @return
	 */
	private Set<ClassId> getAvailableSubClasses(PlayerInstance player)
	{
		final int currentBaseId = player.getBaseClass();
		final ClassId baseCID = ClassId.getClassId(currentBaseId);
		int baseClassId = (CategoryData.getInstance().isInCategory(CategoryType.FOURTH_CLASS_GROUP, baseCID.getId()) || CategoryData.getInstance().isInCategory(CategoryType.FIFTH_CLASS_GROUP, baseCID.getId()) || CategoryData.getInstance().isInCategory(CategoryType.SIXTH_CLASS_GROUP, baseCID.getId())) ? baseCID.getParent().getId() : currentBaseId;
		
		final Set<ClassId> availSubs = getSubclasses(player, baseClassId);
		
		if ((availSubs != null) && !availSubs.isEmpty())
		{
			for (ClassId pclass : availSubs)
			{
				// scan for already used subclasses
				final int availClassId = pclass.getId();
				final ClassId cid = ClassId.getClassId(availClassId);
				
				for (SubClass subList : player.getSubClasses().values())
				{
					final ClassId subId = ClassId.getClassId(subList.getClassId());
					
					if (subId.equalsOrChildOf(cid))
					{
						availSubs.remove(cid);
						break;
					}
				}
			}
		}
		return availSubs;
	}
	
	/**
	 * Check new subclass classId for validity. Base class not added into allowed subclasses.
	 * @param player
	 * @param classId
	 * @return
	 */
	private boolean isValidNewSubClass(PlayerInstance player, int classId)
	{
		final ClassId cid = ClassId.getClassId(classId);
		ClassId subClassId;
		for (SubClass subList : player.getSubClasses().values())
		{
			subClassId = ClassId.getClassId(subList.getClassId());
			
			if (subClassId.equalsOrChildOf(cid))
			{
				return false;
			}
		}
		
		// get player base class
		final int currentBaseId = player.getBaseClass();
		final ClassId baseCID = ClassId.getClassId(currentBaseId);
		
		// we need 2nd occupation ID
		final int baseClassId = (CategoryData.getInstance().isInCategory(CategoryType.FOURTH_CLASS_GROUP, baseCID.getId()) || CategoryData.getInstance().isInCategory(CategoryType.FIFTH_CLASS_GROUP, baseCID.getId()) || CategoryData.getInstance().isInCategory(CategoryType.SIXTH_CLASS_GROUP, baseCID.getId())) ? baseCID.getParent().getId() : currentBaseId;
		final Set<ClassId> availSubs = getSubclasses(player, baseClassId);
		
		if ((availSubs == null) || availSubs.isEmpty())
		{
			return false;
		}
		
		boolean found = false;
		for (ClassId pclass : availSubs)
		{
			if (pclass.getId() == classId)
			{
				found = true;
				break;
			}
		}
		return found;
	}
	
	private boolean hasAllSubclassLeveled(PlayerInstance player)
	{
		boolean leveled = true;
		
		for (SubClass sub : player.getSubClasses().values())
		{
			if ((sub != null) && (sub.getLevel() < 75))
			{
				leveled = false;
			}
		}
		return leveled;
	}
	
	public final List<ClassId> getAvailableDualclasses(PlayerInstance player)
	{
		final List<ClassId> dualClasses = new ArrayList<>();
		
		for (ClassId ClassId : ClassId.values())
		{
			if ((ClassId.getRace() != Race.ERTHEIA) && CategoryData.getInstance().isInCategory(CategoryType.SIXTH_CLASS_GROUP, ClassId.getId()) && (ClassId.getId() != player.getClassId().getId()))
			{
				dualClasses.add(ClassId);
			}
		}
		return dualClasses;
	}
	
	private List<ClassId> getDualClasses(PlayerInstance player, CategoryType cType)
	{
		final List<ClassId> tempList = new ArrayList<>();
		final int baseClassId = player.getBaseClass();
		final int dualClassId = player.getClassId().getId();
		
		for (ClassId temp : dualClassList)
		{
			if ((temp.getId() != baseClassId) && (temp.getId() != dualClassId) && ((cType == null) || CategoryData.getInstance().isInCategory(cType, temp.getId())))
			{
				tempList.add(temp);
			}
		}
		return tempList;
	}
	
	public final Set<ClassId> getSubclasses(PlayerInstance player, int classId)
	{
		Set<ClassId> subclasses = null;
		final ClassId pClass = ClassId.getClassId(classId);
		
		if (CategoryData.getInstance().isInCategory(CategoryType.THIRD_CLASS_GROUP, classId) || (CategoryData.getInstance().isInCategory(CategoryType.FOURTH_CLASS_GROUP, classId)))
		{
			subclasses = EnumSet.copyOf(mainSubclassSet);
			subclasses.remove(pClass);
			
			// Ertheia classes cannot be subclassed and only Kamael can take Kamael classes as subclasses.
			for (ClassId cid : ClassId.values())
			{
				if ((cid.getRace() == Race.ERTHEIA) || ((cid.getRace() == Race.KAMAEL) && (player.getRace() != Race.KAMAEL)))
				{
					subclasses.remove(cid);
				}
			}
			
			if (player.getRace() == Race.KAMAEL)
			{
				if (player.getAppearance().getSex())
				{
					subclasses.remove(ClassId.FEMALE_SOULBREAKER);
				}
				else
				{
					subclasses.remove(ClassId.MALE_SOULBREAKER);
				}
				
				if (!player.getSubClasses().containsKey(2) || (player.getSubClasses().get(2).getLevel() < 75))
				{
					subclasses.remove(ClassId.INSPECTOR);
				}
			}
			
			Set<ClassId> unavailableClasses = subclassSetMap.get(pClass);
			
			if (unavailableClasses != null)
			{
				subclasses.removeAll(unavailableClasses);
			}
		}
		
		if (subclasses != null)
		{
			final ClassId currClassId = ClassId.getClassId(player.getClassId().getId());
			for (ClassId tempClass : subclasses)
			{
				final ClassId tempClassId = ClassId.getClassId(tempClass.getId());
				
				if (currClassId.equalsOrChildOf(tempClassId))
				{
					subclasses.remove(tempClass);
				}
			}
		}
		return subclasses;
	}
	
	private NpcHtmlMessage getNpcHtmlMessage(PlayerInstance player, Npc npc, String fileName)
	{
		final NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
		final String text = getHtm(player.getHtmlPrefix(), fileName);
		if (text == null)
		{
			_logger.info("Cannot find HTML file for " + Raina.class.getSimpleName() + " AI: " + fileName);
			return null;
		}
		html.setHtml(text);
		return html;
	}
	
	private int getCloakId(PlayerInstance player)
	{
		return classCloak.entrySet().stream().filter(e -> player.isInCategory(e.getKey())).mapToInt(Entry::getValue).findFirst().orElse(0);
	}
	
	private int getPowerItemId(PlayerInstance player)
	{
		return powerItem.entrySet().stream().filter(e -> player.isInCategory(e.getKey())).mapToInt(Entry::getValue).findFirst().orElse(0);
	}
	
	@GameScript
	public static void main()
	{
		new Raina();
	}
}