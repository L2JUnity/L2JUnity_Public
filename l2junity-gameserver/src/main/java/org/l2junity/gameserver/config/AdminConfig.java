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
package org.l2junity.gameserver.config;

import org.l2junity.commons.config.ConfigPropertiesLoader;
import org.l2junity.commons.config.annotation.ConfigClass;
import org.l2junity.commons.config.annotation.ConfigField;

/**
 * @author lord_rex
 */
@ConfigClass(fileName = "Admin")
public final class AdminConfig extends ConfigPropertiesLoader
{
	@ConfigField(name = "GMHeroAura", value = "false", comment =
	{
		"Enable GMs to have the glowing aura of a Hero character on login.",
		"Notes:",
		"	GMs can do '///hero' on themselves and get this aura voluntarily.",
		"	It's advised to keep this off due to graphic lag."
	})
	public static boolean GM_HERO_AURA;
	
	@ConfigField(name = "GMStartupBuilderHide", value = "true", comment =
	{
		"Whether GM logins in builder hide mode by default."
	})
	public static boolean GM_STARTUP_BUILDER_HIDE;
	
	@ConfigField(name = "GMStartupInvulnerable", value = "false", comment =
	{
		"Auto set invulnerable status to a GM on login."
	})
	public static boolean GM_STARTUP_INVULNERABLE;
	
	@ConfigField(name = "GMStartupInvisible", value = "false", comment =
	{
		"Auto set invisible status to a GM on login."
	})
	public static boolean GM_STARTUP_INVISIBLE;
	
	@ConfigField(name = "GMStartupSilence", value = "false", comment =
	{
		"Auto block private messages to a GM on login."
	})
	public static boolean GM_STARTUP_SILENCE;
	
	@ConfigField(name = "GMStartupAutoList", value = "false", comment =
	{
		"Auto list GMs in GM list (/gmlist) on login."
	})
	public static boolean GM_STARTUP_AUTO_LIST;
	
	@ConfigField(name = "GMStartupDietMode", value = "false", comment =
	{
		"Auto set diet mode on to a GM on login (affects your weight penalty)."
	})
	public static boolean GM_STARTUP_DIET_MODE;
	
	@ConfigField(name = "GMItemRestriction", value = "true", comment =
	{
		"Item restrictions apply to GMs as well? (True = restricted usage)"
	})
	public static boolean GM_ITEM_RESTRICTION;
	
	@ConfigField(name = "GMSkillRestriction", value = "true", comment =
	{
		"Skill restrictions apply to GMs as well? (True = restricted usage)"
	})
	public static boolean GM_SKILL_RESTRICTION;
	
	@ConfigField(name = "GMTradeRestrictedItems", value = "false", comment =
	{
		"Allow GMs to drop/trade non-tradable and quest(drop only) items"
	})
	public static boolean GM_TRADE_RESTRICTED_ITEMS;
	
	@ConfigField(name = "GMRestartFighting", value = "true", comment =
	{
		"Allow GMs to restart/exit while is fighting stance"
	})
	public static boolean GM_RESTART_FIGHTING;
	
	@ConfigField(name = "GMShowAnnouncerName", value = "false", comment =
	{
		"Show the GM's name behind an announcement made by him",
		"example: 'Announce: hi (HanWik)'"
	})
	public static boolean GM_ANNOUNCER_NAME;
	
	@ConfigField(name = "GMShowCritAnnouncerName", value = "false", comment =
	{
		"Show the GM's name before an announcement made by him",
		"example: 'Nyaran: hi'"
	})
	public static boolean GM_CRITANNOUNCER_NAME;
	
	@ConfigField(name = "GMGiveSpecialSkills", value = "false", comment =
	{
		"Give special skills for every GM",
		"7029,7041-7064,7088-7096,23238-23249 (Master's Blessing)"
	})
	public static boolean GM_GIVE_SPECIAL_SKILLS;
	
	@ConfigField(name = "GMGiveSpecialAuraSkills", value = "false", comment =
	{
		"Give special aura skills for every GM",
		"7029,23238-23249,23253-23296 (Master's Blessing)"
	})
	public static boolean GM_GIVE_SPECIAL_AURA_SKILLS;
	
	@ConfigField(name = "UseSuperHasteAsGMSpeed", value = "false", comment =
	{
		"In case you are not satisfied with the retail-like implementation of //gmspeed",
		"with this config you can rollback it to the old custom L2J version of the GM Speed."
	})
	public static boolean USE_SUPER_HASTE_AS_GM_SPEED;
}
