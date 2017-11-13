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
package org.l2junity.gameserver.model.base;

import java.util.HashSet;
import java.util.Set;

import org.l2junity.gameserver.enums.Race;
import org.l2junity.gameserver.model.interfaces.IIdentifiable;

/**
 * This class defines all classes (ex : human fighter, darkFighter...) that a player can chose.<br>
 * Data:
 * <ul>
 * <li>id : The Identifier of the class</li>
 * <li>isMage : True if the class is a mage class</li>
 * <li>race : The race of this class</li>
 * <li>parent : The parent ClassId or null if this class is the root</li>
 * </ul>
 * @version $Revision: 1.4.4.4 $ $Date: 2005/03/27 15:29:33 $
 */
public enum ClassId implements IIdentifiable
{
	FIGHTER(0, Race.HUMAN, null),
	
	WARRIOR(1, Race.HUMAN, FIGHTER),
	GLADIATOR(2, Race.HUMAN, WARRIOR),
	WARLORD(3, Race.HUMAN, WARRIOR),
	KNIGHT(4, Race.HUMAN, FIGHTER),
	PALADIN(5, Race.HUMAN, KNIGHT),
	DARK_AVENGER(6, Race.HUMAN, KNIGHT),
	ROGUE(7, Race.HUMAN, FIGHTER),
	TREASURE_HUNTER(8, Race.HUMAN, ROGUE),
	HAWKEYE(9, Race.HUMAN, ROGUE),
	
	MAGE(10, Race.HUMAN, null),
	WIZARD(11, Race.HUMAN, MAGE),
	SORCERER(12, Race.HUMAN, WIZARD),
	NECROMANCER(13, Race.HUMAN, WIZARD),
	WARLOCK(14, Race.HUMAN, WIZARD),
	CLERIC(15, Race.HUMAN, MAGE),
	BISHOP(16, Race.HUMAN, CLERIC),
	PROPHET(17, Race.HUMAN, CLERIC),
	
	ELVEN_FIGHTER(18, Race.ELF, null),
	ELVEN_KNIGHT(19, Race.ELF, ELVEN_FIGHTER),
	TEMPLE_KNIGHT(20, Race.ELF, ELVEN_KNIGHT),
	SWORDSINGER(21, Race.ELF, ELVEN_KNIGHT),
	ELVEN_SCOUT(22, Race.ELF, ELVEN_FIGHTER),
	PLAINS_WALKER(23, Race.ELF, ELVEN_SCOUT),
	SILVER_RANGER(24, Race.ELF, ELVEN_SCOUT),
	
	ELVEN_MAGE(25, Race.ELF, null),
	ELVEN_WIZARD(26, Race.ELF, ELVEN_MAGE),
	SPELLSINGER(27, Race.ELF, ELVEN_WIZARD),
	ELEMENTAL_SUMMONER(28, Race.ELF, ELVEN_WIZARD),
	ORACLE(29, Race.ELF, ELVEN_MAGE),
	ELDER(30, Race.ELF, ORACLE),
	
	DARK_FIGHTER(31, Race.DARK_ELF, null),
	PALUS_KNIGHT(32, Race.DARK_ELF, DARK_FIGHTER),
	SHILLIEN_KNIGHT(33, Race.DARK_ELF, PALUS_KNIGHT),
	BLADEDANCER(34, Race.DARK_ELF, PALUS_KNIGHT),
	ASSASSIN(35, Race.DARK_ELF, DARK_FIGHTER),
	ABYSS_WALKER(36, Race.DARK_ELF, ASSASSIN),
	PHANTOM_RANGER(37, Race.DARK_ELF, ASSASSIN),
	
	DARK_MAGE(38, Race.DARK_ELF, null),
	DARK_WIZARD(39, Race.DARK_ELF, DARK_MAGE),
	SPELLHOWLER(40, Race.DARK_ELF, DARK_WIZARD),
	PHANTOM_SUMMONER(41, Race.DARK_ELF, DARK_WIZARD),
	SHILLIEN_ORACLE(42, Race.DARK_ELF, DARK_MAGE),
	SHILLIEN_ELDER(43, Race.DARK_ELF, SHILLIEN_ORACLE),
	
	ORC_FIGHTER(44, Race.ORC, null),
	ORC_RAIDER(45, Race.ORC, ORC_FIGHTER),
	DESTROYER(46, Race.ORC, ORC_RAIDER),
	ORC_MONK(47, Race.ORC, ORC_FIGHTER),
	TYRANT(48, Race.ORC, ORC_MONK),
	
	ORC_MAGE(49, Race.ORC, null),
	ORC_SHAMAN(50, Race.ORC, ORC_MAGE),
	OVERLORD(51, Race.ORC, ORC_SHAMAN),
	WARCRYER(52, Race.ORC, ORC_SHAMAN),
	
	DWARVEN_FIGHTER(53, Race.DWARF, null),
	SCAVENGER(54, Race.DWARF, DWARVEN_FIGHTER),
	BOUNTY_HUNTER(55, Race.DWARF, SCAVENGER),
	ARTISAN(56, Race.DWARF, DWARVEN_FIGHTER),
	WARSMITH(57, Race.DWARF, ARTISAN),
	
	DUMMY_ENTRY_1(58, null, null),
	DUMMY_ENTRY_2(59, null, null),
	DUMMY_ENTRY_3(60, null, null),
	DUMMY_ENTRY_4(61, null, null),
	DUMMY_ENTRY_5(62, null, null),
	DUMMY_ENTRY_6(63, null, null),
	DUMMY_ENTRY_7(64, null, null),
	DUMMY_ENTRY_8(65, null, null),
	DUMMY_ENTRY_9(66, null, null),
	DUMMY_ENTRY_10(67, null, null),
	DUMMY_ENTRY_11(68, null, null),
	DUMMY_ENTRY_12(69, null, null),
	DUMMY_ENTRY_13(70, null, null),
	DUMMY_ENTRY_14(71, null, null),
	DUMMY_ENTRY_15(72, null, null),
	DUMMY_ENTRY_16(73, null, null),
	DUMMY_ENTRY_17(74, null, null),
	DUMMY_ENTRY_18(75, null, null),
	DUMMY_ENTRY_19(76, null, null),
	DUMMY_ENTRY_20(77, null, null),
	DUMMY_ENTRY_21(78, null, null),
	DUMMY_ENTRY_22(79, null, null),
	DUMMY_ENTRY_23(80, null, null),
	DUMMY_ENTRY_24(81, null, null),
	DUMMY_ENTRY_25(82, null, null),
	DUMMY_ENTRY_26(83, null, null),
	DUMMY_ENTRY_27(84, null, null),
	DUMMY_ENTRY_28(85, null, null),
	DUMMY_ENTRY_29(86, null, null),
	DUMMY_ENTRY_30(87, null, null),
	
	DUELIST(88, Race.HUMAN, GLADIATOR),
	DREADNOUGHT(89, Race.HUMAN, WARLORD),
	PHOENIX_KNIGHT(90, Race.HUMAN, PALADIN),
	HELL_KNIGHT(91, Race.HUMAN, DARK_AVENGER),
	SAGITTARIUS(92, Race.HUMAN, HAWKEYE),
	ADVENTURER(93, Race.HUMAN, TREASURE_HUNTER),
	ARCHMAGE(94, Race.HUMAN, SORCERER),
	SOULTAKER(95, Race.HUMAN, NECROMANCER),
	ARCANA_LORD(96, Race.HUMAN, WARLOCK),
	CARDINAL(97, Race.HUMAN, BISHOP),
	HIEROPHANT(98, Race.HUMAN, PROPHET),
	
	EVA_TEMPLAR(99, Race.ELF, TEMPLE_KNIGHT),
	SWORD_MUSE(100, Race.ELF, SWORDSINGER),
	WIND_RIDER(101, Race.ELF, PLAINS_WALKER),
	MOONLIGHT_SENTINEL(102, Race.ELF, SILVER_RANGER),
	MYSTIC_MUSE(103, Race.ELF, SPELLSINGER),
	ELEMENTAL_MASTER(104, Race.ELF, ELEMENTAL_SUMMONER),
	EVA_SAINT(105, Race.ELF, ELDER),
	
	SHILLIEN_TEMPLAR(106, Race.DARK_ELF, SHILLIEN_KNIGHT),
	SPECTRAL_DANCER(107, Race.DARK_ELF, BLADEDANCER),
	GHOST_HUNTER(108, Race.DARK_ELF, ABYSS_WALKER),
	GHOST_SENTINEL(109, Race.DARK_ELF, PHANTOM_RANGER),
	STORM_SCREAMER(110, Race.DARK_ELF, SPELLHOWLER),
	SPECTRAL_MASTER(111, Race.DARK_ELF, PHANTOM_SUMMONER),
	SHILLIEN_SAINT(112, Race.DARK_ELF, SHILLIEN_ELDER),
	
	TITAN(113, Race.ORC, DESTROYER),
	GRAND_KHAVATARI(114, Race.ORC, TYRANT),
	DOMINATOR(115, Race.ORC, OVERLORD),
	DOOMCRYER(116, Race.ORC, WARCRYER),
	
	FORTUNE_SEEKER(117, Race.DWARF, BOUNTY_HUNTER),
	MAESTRO(118, Race.DWARF, WARSMITH),
	
	WORLD_TRAP(119, null, null),
	PC_TRAP(120, null, null),
	DOPPELGANGER(121, null, null),
	SIEGE_ATTACKER(122, null, null),
	
	MALE_SOLDIER(123, Race.KAMAEL, null),
	FEMALE_SOLDIER(124, Race.KAMAEL, null),
	TROOPER(125, Race.KAMAEL, MALE_SOLDIER),
	WARDER(126, Race.KAMAEL, FEMALE_SOLDIER),
	BERSERKER(127, Race.KAMAEL, TROOPER),
	MALE_SOULBREAKER(128, Race.KAMAEL, TROOPER),
	FEMALE_SOULBREAKER(129, Race.KAMAEL, WARDER),
	ARBALESTER(130, Race.KAMAEL, WARDER),
	DOOMBRINGER(131, Race.KAMAEL, BERSERKER),
	MALE_SOUL_HOUND(132, Race.KAMAEL, MALE_SOULBREAKER),
	FEMALE_SOUL_HOUND(133, Race.KAMAEL, FEMALE_SOULBREAKER),
	TRICKSTER(134, Race.KAMAEL, ARBALESTER),
	INSPECTOR(135, Race.KAMAEL, WARDER),
	JUDICATOR(136, Race.KAMAEL, INSPECTOR),
	
	DUMMY_ENTRY_35(137, null, null),
	DUMMY_ENTRY_36(138, null, null),
	
	SIGEL_KNIGHT(139, null, null),
	TYRR_WARRIOR(140, null, null),
	OTHELL_ROGUE(141, null, null),
	YUL_ARCHER(142, null, null),
	FEOH_WIZARD(143, null, null),
	ISS_ENCHANTER(144, null, null),
	WYNN_SUMMONER(145, null, null),
	AEORE_HEALER(146, null, null),
	
	DUMMY_ENTRY_37(147, null, null),
	
	SIGEL_PHOENIX_KNIGHT(148, Race.HUMAN, PHOENIX_KNIGHT),
	SIGEL_HELL_KNIGHT(149, Race.HUMAN, HELL_KNIGHT),
	SIGEL_EVA_TEMPLAR(150, Race.ELF, EVA_TEMPLAR),
	SIGEL_SHILLIEN_TEMPLAR(151, Race.DARK_ELF, SHILLIEN_TEMPLAR),
	TYRR_DUELIST(152, Race.HUMAN, DUELIST),
	TYRR_DREADNOUGHT(153, Race.HUMAN, DREADNOUGHT),
	TYRR_TITAN(154, Race.ORC, TITAN),
	TYRR_GRAND_KHAVATARI(155, Race.ORC, GRAND_KHAVATARI),
	TYRR_MAESTRO(156, Race.DWARF, MAESTRO),
	TYRR_DOOMBRINGER(157, Race.KAMAEL, DOOMBRINGER),
	OTHELL_ADVENTURER(158, Race.HUMAN, ADVENTURER),
	OTHELL_WIND_RIDER(159, Race.ELF, WIND_RIDER),
	OTHELL_GHOST_HUNTER(160, Race.DARK_ELF, GHOST_HUNTER),
	OTHELL_FORTUNE_SEEKER(161, Race.DWARF, FORTUNE_SEEKER),
	YUL_SAGITTARIUS(162, Race.HUMAN, SAGITTARIUS),
	YUL_MOONLIGHT_SENTINEL(163, Race.ELF, MOONLIGHT_SENTINEL),
	YUL_GHOST_SENTINEL(164, Race.DARK_ELF, GHOST_SENTINEL),
	YUL_TRICKSTER(165, Race.KAMAEL, TRICKSTER),
	FEOH_ARCHMAGE(166, Race.HUMAN, ARCHMAGE),
	FEOH_SOULTAKER(167, Race.HUMAN, SOULTAKER),
	FEOH_MYSTIC_MUSE(168, Race.ELF, MYSTIC_MUSE),
	FEOH_STORM_SCREAMER(169, Race.DARK_ELF, STORM_SCREAMER),
	FEOH_SOUL_HOUND(170, Race.KAMAEL, MALE_SOUL_HOUND, FEMALE_SOUL_HOUND),
	ISS_HIEROPHANT(171, Race.HUMAN, HIEROPHANT),
	ISS_SWORD_MUSE(172, Race.ELF, SWORD_MUSE),
	ISS_SPECTRAL_DANCER(173, Race.DARK_ELF, SPECTRAL_DANCER),
	ISS_DOMINATOR(174, Race.ORC, DOMINATOR),
	ISS_DOOMCRYER(175, Race.ORC, DOOMCRYER),
	WYNN_ARCANA_LORD(176, Race.HUMAN, ARCANA_LORD),
	WYNN_ELEMENTAL_MASTER(177, Race.ELF, ELEMENTAL_MASTER),
	WYNN_SPECTRAL_MASTER(178, Race.DARK_ELF, SPECTRAL_MASTER),
	AEORE_CARDINAL(179, Race.HUMAN, CARDINAL),
	AEORE_EVA_SAINT(180, Race.ELF, EVA_SAINT),
	AEORE_SHILLIEN_SAINT(181, Race.DARK_ELF, SHILLIEN_SAINT),
	
	ERTHEIA_FIGHTER(182, Race.ERTHEIA, null),
	ERTHEIA_WIZARD(183, Race.ERTHEIA, null),
	
	MARAUDER(184, Race.ERTHEIA, ERTHEIA_FIGHTER),
	CLOUD_BREAKER(185, Race.ERTHEIA, ERTHEIA_WIZARD),
	
	RIPPER(186, Race.ERTHEIA, MARAUDER),
	STRATOMANCER(187, Race.ERTHEIA, CLOUD_BREAKER),
	
	EVISCERATOR(188, Race.ERTHEIA, RIPPER),
	SAYHA_SEER(189, Race.ERTHEIA, STRATOMANCER);
	
	/** The Identifier of the Class */
	private final int _id;
	
	/** The Race object of the class */
	private final Race _race;
	
	/** The parent ClassId or null if this class is a root */
	private final ClassId _parent;
	
	private final ClassId[] _parents;
	
	/** List of available Class for next transfer **/
	private final Set<ClassId> _nextClassIds = new HashSet<>(1);
	
	/**
	 * Class constructor.
	 * @param id the class Id.
	 * @param race the race related to the class.
	 * @param parent the parent class Id.
	 * @param moreParents more parents
	 */
	@SafeVarargs
	private ClassId(int id, Race race, ClassId parent, ClassId... moreParents)
	{
		_id = id;
		_race = race;
		_parent = parent;
		_parents = moreParents;
		
		if (_parent != null)
		{
			_parent.addNextClassId(this);
		}
		
		for (ClassId nextParent : moreParents)
		{
			nextParent.addNextClassId(this);
		}
	}
	
	/**
	 * Gets the ID of the class.
	 * @return the ID of the class
	 */
	@Override
	public final int getId()
	{
		return _id;
	}
	
	/**
	 * @return the Race object of the class.
	 */
	public final Race getRace()
	{
		return _race;
	}
	
	/**
	 * @param classId the parent ClassId to check.
	 * @return {code true} if this Class is a child of the selected ClassId.
	 */
	public final boolean childOf(ClassId classId)
	{
		if (_parent == null)
		{
			return false;
		}
		
		if (_parent == classId)
		{
			return true;
		}
		
		for (ClassId parent : _parents)
		{
			if ((parent == classId) || parent.childOf(classId))
			{
				return true;
			}
		}
		
		return _parent.childOf(classId);
		
	}
	
	/**
	 * @param classId the parent ClassId to check.
	 * @return {code true} if this Class is equal to the selected ClassId or a child of the selected ClassId.
	 */
	public final boolean equalsOrChildOf(ClassId classId)
	{
		return (this == classId) || childOf(classId);
	}
	
	/**
	 * @return its parent Class Id
	 */
	public final ClassId getParent()
	{
		return _parent;
	}
	
	/**
	 * @return array of parents of this class id
	 */
	public final ClassId[] getParents()
	{
		return _parents;
	}
	
	/**
	 * @return list of possible class transfer for this class
	 */
	public Set<ClassId> getNextClassIds()
	{
		return _nextClassIds;
	}
	
	/**
	 * @param id
	 * @return the ClassId for the specified id, or else null in case it doesn't exists
	 */
	public static ClassId getClassId(int id)
	{
		try
		{
			return ClassId.values()[id];
		}
		catch (Exception e)
		{
			return null;
		}
	}
	
	public static ClassId getRootClassId(int id)
	{
		try
		{
			ClassId current = ClassId.values()[id];
			while (current.getParent() != null)
			{
				current = current.getParent();
			}
			
			return current;
		}
		catch (Exception e)
		{
			return null;
		}
	}
	
	/**
	 * Adds the classId to the next class id's
	 * @param classId
	 */
	private final void addNextClassId(ClassId classId)
	{
		_nextClassIds.add(classId);
	}
}
