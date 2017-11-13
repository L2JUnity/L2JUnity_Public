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
package org.l2junity.gameserver.model.skills;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.l2junity.commons.util.Rnd;
import org.l2junity.gameserver.config.AdminConfig;
import org.l2junity.gameserver.config.GeneralConfig;
import org.l2junity.gameserver.config.PlayerConfig;
import org.l2junity.gameserver.data.xml.impl.EnchantSkillGroupsData;
import org.l2junity.gameserver.data.xml.impl.SkillData;
import org.l2junity.gameserver.data.xml.impl.SkillTreesData;
import org.l2junity.gameserver.enums.AttributeType;
import org.l2junity.gameserver.enums.BasicProperty;
import org.l2junity.gameserver.enums.MountType;
import org.l2junity.gameserver.enums.NextActionType;
import org.l2junity.gameserver.enums.ShotType;
import org.l2junity.gameserver.handler.IAffectObjectHandler;
import org.l2junity.gameserver.handler.IAffectScopeHandler;
import org.l2junity.gameserver.handler.ITargetTypeHandler;
import org.l2junity.gameserver.handler.TargetHandler;
import org.l2junity.gameserver.instancemanager.HandysBlockCheckerManager;
import org.l2junity.gameserver.model.ArenaParticipantsHolder;
import org.l2junity.gameserver.model.L2Clan;
import org.l2junity.gameserver.model.PcCondOverride;
import org.l2junity.gameserver.model.StatsSet;
import org.l2junity.gameserver.model.WorldObject;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.actor.instance.L2BlockInstance;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.cubic.CubicInstance;
import org.l2junity.gameserver.model.debugger.DebugType;
import org.l2junity.gameserver.model.effects.AbstractEffect;
import org.l2junity.gameserver.model.effects.L2EffectType;
import org.l2junity.gameserver.model.events.ListenersContainer;
import org.l2junity.gameserver.model.holders.AlterSkillHolder;
import org.l2junity.gameserver.model.holders.AttachSkillHolder;
import org.l2junity.gameserver.model.interfaces.IIdentifiable;
import org.l2junity.gameserver.model.items.instance.ItemInstance;
import org.l2junity.gameserver.model.items.type.ActionType;
import org.l2junity.gameserver.model.stats.BasicPropertyResist;
import org.l2junity.gameserver.model.stats.BooleanStat;
import org.l2junity.gameserver.model.stats.Formulas;
import org.l2junity.gameserver.model.stats.TraitType;
import org.l2junity.gameserver.network.client.send.ActionFailed;
import org.l2junity.gameserver.network.client.send.SystemMessage;
import org.l2junity.gameserver.network.client.send.string.SystemMessageId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Skill extends ListenersContainer implements IIdentifiable
{
	private static final Logger LOGGER = LoggerFactory.getLogger(Skill.class);
	
	/** Skill ID. */
	private final int _id;
	/** Skill level. */
	private final int _level;
	/** Skill sub level. */
	private final int _subLevel;
	/** Custom skill ID displayed by the client. */
	private final int _displayId;
	/** Custom skill level displayed by the client. */
	private final int _displayLevel;
	/** Skill client's name. */
	private final String _name;
	/** Operative type: passive, active, toggle. */
	private final SkillOperateType _operateType;
	private final int _magic;
	private final TraitType _traitType;
	private final boolean _staticReuse;
	/** MP consumption. */
	private final int _mpConsume;
	/** Initial MP consumption. */
	private final int _mpInitialConsume;
	/** MP consumption per channeling. */
	private final int _mpPerChanneling;
	/** HP consumption. */
	private final int _hpConsume;
	/** Amount of items consumed by this skill from caster. */
	private final int _itemConsumeCount;
	/** Id of item consumed by this skill from caster. */
	private final int _itemConsumeId;
	/** Fame points consumed by this skill from caster */
	private final int _pvpPointConsume;
	/** Clan points consumed by this skill from caster's clan */
	private final int _pledgeNvConsume;
	/** Cast range: how far can be the target. */
	private final int _castRange;
	/** Effect range: how far the skill affect the target. */
	private final int _effectRange;
	/** Abnormal instant, used for herbs mostly. */
	private final boolean _isAbnormalInstant;
	/** Abnormal level, global effect level. */
	private final int _abnormalLvl;
	/** Abnormal type: global effect "group". */
	private final AbnormalType _abnormalType;
	/** Abnormal type: local effect "group". */
	private final AbnormalType _subordinationAbnormalType;
	/** Abnormal time: global effect duration time. */
	private final int _abnormalTime;
	/** Abnormal visual effect: the visual effect displayed ingame. */
	private Set<AbnormalVisualEffect> _abnormalVisualEffects;
	/** If {@code true} this skill's effect should stay after death. */
	private final boolean _stayAfterDeath;
	/** If {@code true} this skill's effect recovery HP/MP or CP from herb. */
	private final boolean _isRecoveryHerb;
	
	private final int _refId;
	// all times in milliseconds
	private final int _hitTime;
	private final double _hitCancelTime;
	private final int _coolTime;
	private final long _reuseHashCode;
	private final int _reuseDelay;
	private final int _reuseDelayGroup;
	
	private final int _magicLevel;
	private final int _lvlBonusRate;
	private final int _activateRate;
	private final int _minChance;
	private final int _maxChance;
	
	// Effecting area of the skill, in radius.
	// The radius center varies according to the _targetType:
	// "caster" if targetType = AURA/PARTY/CLAN or "target" if targetType = AREA
	private final ITargetTypeHandler _targetTypeHandler;
	private final IAffectScopeHandler _affectScopeHandler;
	private final IAffectObjectHandler _affectObjectHandler;
	private final int _affectRange;
	private final int[] _fanRange = new int[4]; // unk;startDegree;fanAffectRange;fanAffectAngle
	private final int[] _affectLimit = new int[3]; // TODO: Third value is unknown... find it out!
	private final int[] _affectHeight = new int[2];
	
	private final NextActionType _nextAction;
	
	private final boolean _removedOnAnyActionExceptMove;
	private final boolean _removedOnDamage;
	
	private final boolean _blockedInOlympiad;
	
	private final AttributeType _attributeType;
	private final int _attributeValue;
	
	private final BasicProperty _basicProperty;
	
	private final int _minPledgeClass;
	private final int _soulMaxConsume;
	private final int _chargeConsume;
	
	private final boolean _isTriggeredSkill; // If true the skill will take activation buff slot instead of a normal buff slot
	private final int _effectPoint;
	private Set<MountType> _rideState;
	
	private final Map<SkillConditionScope, List<ISkillCondition>> _conditionLists = new EnumMap<>(SkillConditionScope.class);
	private final Map<EffectScope, List<AbstractEffect>> _effectLists = new EnumMap<>(EffectScope.class);
	
	private final int _isDebuff;
	
	private final boolean _isSuicideAttack;
	private final boolean _canBeDispelled;
	
	private final boolean _excludedFromCheck;
	private final boolean _withoutAction;
	
	private final String _icon;
	
	private volatile Byte[] _effectTypes;
	
	// Channeling data
	private final int _channelingSkillId;
	private final long _channelingStart;
	private final long _channelingTickInterval;
	
	// Mentoring
	private final boolean _isMentoring;
	
	// Stance skill IDs
	private final int _doubleCastSkill;
	
	private final boolean _canDoubleCast;
	private final boolean _isSharedWithSummon;
	private final boolean _isNecessaryToggle;
	private final boolean _deleteAbnormalOnLeave;
	private final boolean _irreplacableBuff; // Stays after death, on subclass change, cant be canceled.
	private final boolean _blockActionUseSkill; // Blocks the use skill client action and is not showed on skill list.
	
	private final int _toggleGroupId;
	private final int _attachToggleGroupId;
	private final List<AlterSkillHolder> _alterSkills;
	private final List<AttachSkillHolder> _attachSkills;
	private final Set<AbnormalType> _abnormalResists;
	
	private final double _magicCriticalRate;
	
	private final boolean _displayInList;
	
	public Skill(StatsSet set)
	{
		_id = set.getInt(".id");
		_level = set.getInt(".level");
		_subLevel = set.getInt(".subLevel", 0);
		_refId = set.getInt(".referenceId", 0);
		_displayId = set.getInt(".displayId", _id);
		_displayLevel = set.getInt(".displayLevel", _level);
		_name = set.getString(".name", "");
		_operateType = set.getEnum("operateType", SkillOperateType.class);
		_magic = set.getInt("isMagic", 0);
		_traitType = set.getEnum("trait", TraitType.class, TraitType.NONE);
		_staticReuse = set.getBoolean("staticReuse", false);
		_mpConsume = set.getInt("mpConsume", 0);
		_mpInitialConsume = set.getInt("mpInitialConsume", 0);
		_mpPerChanneling = set.getInt("mpPerChanneling", _mpConsume);
		_hpConsume = set.getInt("hpConsume", 0);
		_itemConsumeCount = set.getInt("itemConsumeCount", 0);
		_itemConsumeId = set.getInt("itemConsumeId", 0);
		_pvpPointConsume = set.getInt("pvpPointConsume", 0);
		_pledgeNvConsume = set.getInt("pledgeNvConsume", 0);
		
		_castRange = set.getInt("castRange", -1);
		_effectRange = set.getInt("effectRange", -1);
		_abnormalLvl = set.getInt("abnormalLvl", 0);
		_abnormalType = set.getEnum("abnormalType", AbnormalType.class, AbnormalType.NONE);
		_subordinationAbnormalType = set.getEnum("subordinationAbnormalType", AbnormalType.class, AbnormalType.NONE);
		
		int abnormalTime = set.getInt("abnormalTime", 0);
		if (PlayerConfig.ENABLE_MODIFY_SKILL_DURATION && PlayerConfig.SKILL_DURATION_LIST.containsKey(getId()))
		{
			if ((getLevel() < 100) || (getLevel() > 140))
			{
				abnormalTime = PlayerConfig.SKILL_DURATION_LIST.get(getId());
			}
			else if ((getLevel() >= 100) && (getLevel() < 140))
			{
				abnormalTime += PlayerConfig.SKILL_DURATION_LIST.get(getId());
			}
		}
		
		_abnormalTime = abnormalTime;
		_isAbnormalInstant = set.getBoolean("abnormalInstant", false);
		parseAbnormalVisualEffect(set.getString("abnormalVisualEffect", null));
		
		_stayAfterDeath = set.getBoolean("stayAfterDeath", false);
		
		_hitTime = set.getInt("hitTime", 0);
		_hitCancelTime = set.getDouble("hitCancelTime", 0);
		_coolTime = set.getInt("coolTime", 0);
		_isDebuff = set.getInt("isDebuff", 0);
		_isRecoveryHerb = set.getBoolean("isRecoveryHerb", false);
		
		if (PlayerConfig.ENABLE_MODIFY_SKILL_REUSE && PlayerConfig.SKILL_REUSE_LIST.containsKey(_id))
		{
			if (GeneralConfig.DEBUG)
			{
				LOGGER.info("*** Skill {} ({}) changed reuse from {} to {} seconds.", _name, _level, set.getInt("reuseDelay", 0), PlayerConfig.SKILL_REUSE_LIST.get(_id));
			}
			_reuseDelay = PlayerConfig.SKILL_REUSE_LIST.get(_id);
		}
		else
		{
			_reuseDelay = set.getInt("reuseDelay", 0);
		}
		
		_reuseDelayGroup = set.getInt("reuseDelayGroup", -1);
		_reuseHashCode = SkillData.getSkillHashCode(_reuseDelayGroup > 0 ? _reuseDelayGroup : _id, _level, _subLevel);
		
		final String targetType = set.getString("targetType", "NONE");
		_targetTypeHandler = TargetHandler.getInstance().getTargetTypeHandler(targetType);
		if (_targetTypeHandler == null)
		{
			throw new RuntimeException("Target Type not found for Skill Id[" + _id + "] Level[" + _level + "] SubLevel[" + _subLevel + "] TargetType[" + targetType + "].");
		}
		
		final String affectScope = set.getString("affectScope", "SINGLE");
		_affectScopeHandler = TargetHandler.getInstance().getAffectScopeHandler(affectScope);
		if (_affectScopeHandler == null)
		{
			throw new RuntimeException("Affect Scope not found for Skill Id[" + _id + "] Level[" + _level + "] SubLevel[" + _subLevel + "] AffectScope[" + affectScope + "].");
		}
		
		final String affectObject = set.getString("affectObject", "ALL");
		_affectObjectHandler = TargetHandler.getInstance().getAffectObjectHandler(affectObject);
		if (_affectObjectHandler == null)
		{
			throw new RuntimeException("Affect Object not found for Skill Id[" + _id + "] Level[" + _level + "] SubLevel[" + _subLevel + "] AffectObject[" + affectObject + "].");
		}
		
		_affectRange = set.getInt("affectRange", 0);
		
		final String rideState = set.getString("rideState", null);
		if (rideState != null)
		{
			String[] state = rideState.split(";");
			if (state.length > 0)
			{
				_rideState = new HashSet<>(state.length);
				for (String s : state)
				{
					try
					{
						_rideState.add(MountType.valueOf(s));
					}
					catch (Exception e)
					{
						LOGGER.warn("Bad data in rideState for skill {}!", this, e);
					}
				}
			}
		}
		final String fanRange = set.getString("fanRange", null);
		if (fanRange != null)
		{
			try
			{
				String[] valuesSplit = fanRange.split(";");
				_fanRange[0] = Integer.parseInt(valuesSplit[0]);
				_fanRange[1] = Integer.parseInt(valuesSplit[1]);
				_fanRange[2] = Integer.parseInt(valuesSplit[2]);
				_fanRange[3] = Integer.parseInt(valuesSplit[3]);
			}
			catch (Exception e)
			{
				throw new IllegalArgumentException("SkillId: " + _id + " invalid fanRange value: " + fanRange + ", \"unk;startDegree;fanAffectRange;fanAffectAngle\" required");
			}
		}
		
		final String affectLimit = set.getString("affectLimit", null);
		if (affectLimit != null)
		{
			try
			{
				String[] valuesSplit = affectLimit.split("-");
				_affectLimit[0] = Integer.parseInt(valuesSplit[0]);
				_affectLimit[1] = Integer.parseInt(valuesSplit[1]);
				if (valuesSplit.length > 2)
				{
					_affectLimit[2] = Integer.parseInt(valuesSplit[2]);
				}
			}
			catch (Exception e)
			{
				throw new IllegalArgumentException("SkillId: " + _id + " invalid affectLimit value: " + affectLimit + ", \"minAffected-additionalRandom\" required");
			}
		}
		
		final String affectHeight = set.getString("affectHeight", null);
		if (affectHeight != null)
		{
			try
			{
				String[] valuesSplit = affectHeight.split(";");
				_affectHeight[0] = Integer.parseInt(valuesSplit[0]);
				_affectHeight[1] = Integer.parseInt(valuesSplit[1]);
			}
			catch (Exception e)
			{
				throw new IllegalArgumentException("SkillId: " + _id + " invalid affectHeight value: " + affectHeight + ", \"minHeight-maxHeight\" required");
			}
			
			if (_affectHeight[0] > _affectHeight[1])
			{
				throw new IllegalArgumentException("SkillId: " + _id + " invalid affectHeight value: " + affectHeight + ", \"minHeight-maxHeight\" required, minHeight is higher than maxHeight!");
			}
		}
		
		_magicLevel = set.getInt("magicLvl", 0);
		_lvlBonusRate = set.getInt("lvlBonusRate", 0);
		_activateRate = set.getInt("activateRate", -1);
		_minChance = set.getInt("minChance", PlayerConfig.MIN_ABNORMAL_STATE_SUCCESS_RATE);
		_maxChance = set.getInt("maxChance", PlayerConfig.MAX_ABNORMAL_STATE_SUCCESS_RATE);
		
		_nextAction = set.getEnum("nextAction", NextActionType.class, NextActionType.NONE);
		
		_removedOnAnyActionExceptMove = set.getBoolean("removedOnAnyActionExceptMove", false);
		_removedOnDamage = set.getBoolean("removedOnDamage", false);
		
		_blockedInOlympiad = set.getBoolean("blockedInOlympiad", false);
		
		_attributeType = set.getEnum("attributeType", AttributeType.class, AttributeType.NONE);
		_attributeValue = set.getInt("attributeValue", 0);
		
		_basicProperty = set.getEnum("basicProperty", BasicProperty.class, BasicProperty.NONE);
		
		_isSuicideAttack = set.getBoolean("isSuicideAttack", false);
		
		_minPledgeClass = set.getInt("minPledgeClass", 0);
		
		_soulMaxConsume = set.getInt("soulMaxConsumeCount", 0);
		_chargeConsume = set.getInt("chargeConsume", 0);
		
		_isTriggeredSkill = set.getBoolean("isTriggeredSkill", false);
		_effectPoint = set.getInt("effectPoint", 0);
		
		_canBeDispelled = set.getBoolean("canBeDispelled", true);
		
		_excludedFromCheck = set.getBoolean("excludedFromCheck", false);
		_withoutAction = set.getBoolean("withoutAction", false);
		
		_icon = set.getString("icon", "icon.skill0000");
		
		_channelingSkillId = set.getInt("channelingSkillId", 0);
		_channelingTickInterval = (long) set.getFloat("channelingTickInterval", 2000f) * 1000;
		_channelingStart = (long) (set.getFloat("channelingStart", 0f) * 1000);
		
		_isMentoring = set.getBoolean("isMentoring", false);
		
		_doubleCastSkill = set.getInt("doubleCastSkill", 0);
		
		_canDoubleCast = set.getBoolean("canDoubleCast", false);
		_isSharedWithSummon = set.getBoolean("isSharedWithSummon", true);
		
		_isNecessaryToggle = set.getBoolean("isNecessaryToggle", false);
		_deleteAbnormalOnLeave = set.getBoolean("deleteAbnormalOnLeave", false);
		_irreplacableBuff = set.getBoolean("irreplacableBuff", false);
		_blockActionUseSkill = set.getBoolean("blockActionUseSkill", false);
		
		_toggleGroupId = set.getInt("toggleGroupId", -1);
		_attachToggleGroupId = set.getInt("attachToggleGroupId", -1);
		_alterSkills = set.getList("alterSkill", StatsSet.class, Collections.emptyList()).stream().map(AlterSkillHolder::fromStatsSet).collect(Collectors.toList());
		_attachSkills = set.getList("attachSkillList", StatsSet.class, Collections.emptyList()).stream().map(AttachSkillHolder::fromStatsSet).collect(Collectors.toList());
		
		final String abnormalResist = set.getString("abnormalResists", null);
		if (abnormalResist != null)
		{
			String[] abnormalResistStrings = abnormalResist.split(";");
			if (abnormalResistStrings.length > 0)
			{
				_abnormalResists = new HashSet<>(abnormalResistStrings.length);
				for (String s : abnormalResistStrings)
				{
					try
					{
						_abnormalResists.add(AbnormalType.valueOf(s));
					}
					catch (Exception e)
					{
						LOGGER.warn("Skill ID[{}] Expected AbnormalType for abnormalResists but found {}", _id, s, e);
					}
				}
			}
			else
			{
				_abnormalResists = Collections.emptySet();
			}
		}
		else
		{
			_abnormalResists = Collections.emptySet();
		}
		
		_magicCriticalRate = set.getDouble("magicCriticalRate", 0);
		_displayInList = set.getBoolean("displayInList", true);
	}
	
	public TraitType getTraitType()
	{
		return _traitType;
	}
	
	public AttributeType getAttributeType()
	{
		return _attributeType;
	}
	
	public int getAttributeValue()
	{
		return _attributeValue;
	}
	
	public boolean isSuicideAttack()
	{
		return _isSuicideAttack;
	}
	
	public boolean allowOnTransform()
	{
		return isPassive();
	}
	
	/**
	 * Verify if this skill is abnormal instant.<br>
	 * Herb buff skills yield {@code true} for this check.
	 * @return {@code true} if the skill is abnormal instant, {@code false} otherwise
	 */
	public boolean isAbnormalInstant()
	{
		return _isAbnormalInstant;
	}
	
	/**
	 * Gets the skill abnormal type.
	 * @return the abnormal type
	 */
	public AbnormalType getAbnormalType()
	{
		return _abnormalType;
	}
	
	/**
	 * Gets the skill subordination abnormal type.
	 * @return the abnormal type
	 */
	public AbnormalType getSubordinationAbnormalType()
	{
		return _subordinationAbnormalType;
	}
	
	/**
	 * Gets the skill abnormal level.
	 * @return the skill abnormal level
	 */
	public int getAbnormalLvl()
	{
		return _abnormalLvl;
	}
	
	/**
	 * Gets the skill abnormal time.<br>
	 * Is the base to calculate the duration of the continuous effects of this skill.
	 * @return the abnormal time
	 */
	public int getAbnormalTime()
	{
		return _abnormalTime;
	}
	
	/**
	 * Gets the skill abnormal visual effect.
	 * @return the abnormal visual effect
	 */
	public Set<AbnormalVisualEffect> getAbnormalVisualEffects()
	{
		return (_abnormalVisualEffects != null) ? _abnormalVisualEffects : Collections.emptySet();
	}
	
	/**
	 * Verify if the skill has abnormal visual effects.
	 * @return {@code true} if the skill has abnormal visual effects, {@code false} otherwise
	 */
	public boolean hasAbnormalVisualEffects()
	{
		return (_abnormalVisualEffects != null) && !_abnormalVisualEffects.isEmpty();
	}
	
	/**
	 * Gets the skill magic level.
	 * @return the skill magic level
	 */
	public int getMagicLevel()
	{
		return _magicLevel;
	}
	
	public int getLvlBonusRate()
	{
		return _lvlBonusRate;
	}
	
	public int getActivateRate()
	{
		return _activateRate;
	}
	
	/**
	 * Return custom minimum skill/effect chance.
	 * @return
	 */
	public int getMinChance()
	{
		return _minChance;
	}
	
	/**
	 * Return custom maximum skill/effect chance.
	 * @return
	 */
	public int getMaxChance()
	{
		return _maxChance;
	}
	
	/**
	 * Return true if skill effects should be removed on any action except movement
	 * @return
	 */
	public boolean isRemovedOnAnyActionExceptMove()
	{
		return _removedOnAnyActionExceptMove;
	}
	
	/**
	 * @return {@code true} if skill effects should be removed on damage
	 */
	public boolean isRemovedOnDamage()
	{
		return _removedOnDamage;
	}
	
	/**
	 * @return {@code true} if skill can not be used in olympiad.
	 */
	public boolean isBlockedInOlympiad()
	{
		return _blockedInOlympiad;
	}
	
	/**
	 * Return the additional effect Id.
	 * @return
	 */
	public int getChannelingSkillId()
	{
		return _channelingSkillId;
	}
	
	/**
	 * Return character action after cast
	 * @return
	 */
	public NextActionType getNextAction()
	{
		return _nextAction;
	}
	
	/**
	 * @return Returns the castRange.
	 */
	public int getCastRange()
	{
		return _castRange;
	}
	
	/**
	 * @return Returns the effectRange.
	 */
	public int getEffectRange()
	{
		return _effectRange;
	}
	
	/**
	 * @return Returns the hpConsume.
	 */
	public int getHpConsume()
	{
		return _hpConsume;
	}
	
	/**
	 * Gets the skill ID.
	 * @return the skill ID
	 */
	@Override
	public int getId()
	{
		return _id;
	}
	
	/**
	 * Verify if this skill is a debuff.
	 * @return {@code true} if this skill is a debuff, {@code false} otherwise
	 */
	public boolean isDebuff()
	{
		return _isDebuff > 0;
	}
	
	public int getDebuffValue()
	{
		return _isDebuff;
	}
	
	/**
	 * Verify if this skill is coming from Recovery Herb.
	 * @return {@code true} if this skill is a recover herb, {@code false} otherwise
	 */
	public boolean isRecoveryHerb()
	{
		return _isRecoveryHerb;
	}
	
	public int getDisplayId()
	{
		return _displayId;
	}
	
	public int getDisplayLevel()
	{
		return _displayLevel;
	}
	
	/**
	 * Return skill basic property type.
	 * @return
	 */
	public BasicProperty getBasicProperty()
	{
		return _basicProperty;
	}
	
	/**
	 * @return Returns the how much items will be consumed.
	 */
	public int getItemConsumeCount()
	{
		return _itemConsumeCount;
	}
	
	/**
	 * @return Returns the ID of item for consume.
	 */
	public int getItemConsumeId()
	{
		return _itemConsumeId;
	}
	
	/**
	 * @return Fame points consumed by this skill from caster
	 */
	public int getPvPPointConsume()
	{
		return _pvpPointConsume;
	}
	
	/**
	 * @return Clan points consumed by this skill from caster's clan
	 */
	public int getPledgeNvConsume()
	{
		return _pledgeNvConsume;
	}
	
	/**
	 * @return Returns the level.
	 */
	public int getLevel()
	{
		return _level;
	}
	
	/**
	 * @return Returns the sub level.
	 */
	public int getSubLevel()
	{
		return _subLevel;
	}
	
	/**
	 * @return isMagic integer value from the XML.
	 */
	public int getMagicType()
	{
		return _magic;
	}
	
	/**
	 * @return Returns true to set physical skills.
	 */
	public boolean isPhysical()
	{
		return _magic == 0;
	}
	
	/**
	 * @return Returns true to set magic skills.
	 */
	public boolean isMagic()
	{
		return _magic == 1;
	}
	
	/**
	 * @return Returns true to set static skills.
	 */
	public boolean isStatic()
	{
		return _magic == 2;
	}
	
	/**
	 * @return Returns true to set dance skills.
	 */
	public boolean isDance()
	{
		return _magic == 3;
	}
	
	/**
	 * @return Returns true to set static reuse.
	 */
	public boolean isStaticReuse()
	{
		return _staticReuse;
	}
	
	/**
	 * @return Returns the mpConsume.
	 */
	public int getMpConsume()
	{
		return _mpConsume;
	}
	
	/**
	 * @return Returns the mpInitialConsume.
	 */
	public int getMpInitialConsume()
	{
		return _mpInitialConsume;
	}
	
	/**
	 * @return Mana consumption per channeling tick.
	 */
	public int getMpPerChanneling()
	{
		return _mpPerChanneling;
	}
	
	/**
	 * @return the skill name
	 */
	public String getName()
	{
		return _name;
	}
	
	/**
	 * @return the reuse delay
	 */
	public int getReuseDelay()
	{
		return _reuseDelay;
	}
	
	/**
	 * @return the skill ID from which the reuse delay should be taken.
	 */
	public int getReuseDelayGroup()
	{
		return _reuseDelayGroup;
	}
	
	public long getReuseHashCode()
	{
		return _reuseHashCode;
	}
	
	public int getHitTime()
	{
		return _hitTime;
	}
	
	public double getHitCancelTime()
	{
		return _hitCancelTime;
	}
	
	/**
	 * @return the cool time
	 */
	public int getCoolTime()
	{
		return _coolTime;
	}
	
	/**
	 * @return the target type of the skill : SELF, TARGET, SUMMON, GROUND...
	 */
	public ITargetTypeHandler getTargetTypeHandler()
	{
		return _targetTypeHandler;
	}
	
	/**
	 * @return the affect scope of the skill : SINGLE, FAN, SQUARE, PARTY, PLEDGE...
	 */
	public IAffectScopeHandler getAffectScopeHandler()
	{
		return _affectScopeHandler;
	}
	
	/**
	 * @return the affect object of the skill : All, Clan, Friend, NotFriend, Invisible...
	 */
	public IAffectObjectHandler getAffectObjectHandler()
	{
		return _affectObjectHandler;
	}
	
	/**
	 * @return the AOE range of the skill.
	 */
	public int getAffectRange()
	{
		return _affectRange;
	}
	
	/**
	 * @return the AOE fan range of the skill.
	 */
	public int[] getFanRange()
	{
		return _fanRange;
	}
	
	/**
	 * @return the maximum amount of targets the skill can affect or 0 if unlimited.
	 */
	public int getAffectLimit()
	{
		if ((_affectLimit[0] > 0) || (_affectLimit[1] > 0))
		{
			return (_affectLimit[0] + Rnd.get(_affectLimit[1]));
		}
		
		return 0;
	}
	
	public int getAffectHeightMin()
	{
		return _affectHeight[0];
	}
	
	public int getAffectHeightMax()
	{
		return _affectHeight[1];
	}
	
	public boolean isActive()
	{
		return _operateType.isActive();
	}
	
	public boolean isPassive()
	{
		return _operateType.isPassive();
	}
	
	public boolean isToggle()
	{
		return _operateType.isToggle();
	}
	
	public boolean isAura()
	{
		return _operateType.isAura();
	}
	
	public boolean isHidingMesseges()
	{
		return _operateType.isHidingMessages();
	}
	
	public boolean isNotBroadcastable()
	{
		return _operateType.isNotBroadcastable();
	}
	
	public boolean isContinuous()
	{
		return _operateType.isContinuous() || isSelfContinuous();
	}
	
	public boolean isFlyType()
	{
		return _operateType.isFlyType();
	}
	
	public boolean isSelfContinuous()
	{
		return _operateType.isSelfContinuous();
	}
	
	public boolean isChanneling()
	{
		return _operateType.isChanneling();
	}
	
	public boolean isTriggeredSkill()
	{
		return _isTriggeredSkill;
	}
	
	public boolean isSynergySkill()
	{
		return _operateType.isSynergy();
	}
	
	public SkillOperateType getOperateType()
	{
		return _operateType;
	}
	
	/**
	 * Verify if the skill is a transformation skill.
	 * @return {@code true} if the skill is a transformation, {@code false} otherwise
	 */
	public boolean isTransformation()
	{
		return _abnormalType == AbnormalType.TRANSFORM;
	}
	
	public int getEffectPoint()
	{
		return _effectPoint;
	}
	
	public boolean useSoulShot()
	{
		return _magic == 0;
	}
	
	public boolean useSpiritShot()
	{
		return _magic == 1;
	}
	
	public int getMinPledgeClass()
	{
		return _minPledgeClass;
	}
	
	public boolean isHeroSkill()
	{
		return SkillTreesData.getInstance().isHeroSkill(_id, _level);
	}
	
	public boolean isGMSkill()
	{
		return SkillTreesData.getInstance().isGMSkill(_id, _level);
	}
	
	public boolean is7Signs()
	{
		return (_id > 4360) && (_id < 4367);
	}
	
	/**
	 * Verify if this is a healing potion skill.
	 * @return {@code true} if this is a healing potion skill, {@code false} otherwise
	 */
	public boolean isHealingPotionSkill()
	{
		return getAbnormalType() == AbnormalType.HP_RECOVER;
	}
	
	public int getMaxSoulConsumeCount()
	{
		return _soulMaxConsume;
	}
	
	public int getChargeConsume()
	{
		return _chargeConsume;
	}
	
	public boolean isStayAfterDeath()
	{
		return _stayAfterDeath || isIrreplacableBuff() || isNecessaryToggle();
	}
	
	public boolean isBad()
	{
		return _effectPoint < 0;
	}
	
	public boolean checkCondition(Creature activeChar, WorldObject object)
	{
		return checkCondition(activeChar, object, null);
	}
	
	public boolean checkCondition(Creature activeChar, WorldObject object, ItemInstance item)
	{
		if (activeChar.canOverrideCond(PcCondOverride.SKILL_CONDITIONS) && !AdminConfig.GM_SKILL_RESTRICTION)
		{
			return true;
		}
		
		if (activeChar.isPlayer() && !canBeUseWhileRiding((PlayerInstance) activeChar))
		{
			final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS);
			sm.addSkillName(_id);
			activeChar.sendPacket(sm);
			return false;
		}
		
		if (isBad() && object.isNpc() && !object.canBeAttacked() && !activeChar.getActingPlayer().getAccessLevel().allowPeaceAttack())
		{
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return false;
		}
		
		// Check general conditions.
		if (!checkConditions(SkillConditionScope.GENERAL, activeChar, object) || !checkConditions(SkillConditionScope.TARGET, activeChar, object))
		{
			final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS);
			sm.addSkillName(_id);
			activeChar.sendPacket(sm);
			return false;
		}
		
		// Consume the required items. Should happen after use message is displayed and SetupGauge
		if ((getItemConsumeId() > 0) && (getItemConsumeCount() > 0))
		{
			// Check if item is consumed from its own action type.
			if ((item != null) && ((item.getItem().getDefaultAction() == ActionType.CAPSULE) || (item.getItem().getDefaultAction() == ActionType.SKILL_REDUCE) || (item.getItem().getDefaultAction() == ActionType.SKILL_REDUCE_ON_SKILL_SUCCESS)))
			{
				// Consume item only if its action type is to be consumed on cast, otherwise check if its available to be consumed after cast is successful.
				if ((item.getCount() < getItemConsumeCount()) || (((item.getItem().getDefaultAction() == ActionType.CAPSULE) || (item.getItem().getDefaultAction() == ActionType.SKILL_REDUCE)) && !activeChar.destroyItem(item.toString(), item.getObjectId(), getItemConsumeCount(), object, true)))
				{
					return false;
				}
			}
		}
		
		// Consume the required fame
		if ((getPvPPointConsume() > 0) && activeChar.isPlayer())
		{
			final PlayerInstance player = activeChar.getActingPlayer();
			if (player.getFame() < getPvPPointConsume())
			{
				player.sendPacket(SystemMessageId.YOU_DON_T_HAVE_ENOUGH_FAME_TO_DO_THAT);
				return false;
			}
		}
		
		// Consume clan reputation points
		if ((getPledgeNvConsume() > 0) && (activeChar.getClan() != null))
		{
			final L2Clan clan = activeChar.getClan();
			if (clan.getReputationScore() < getPledgeNvConsume())
			{
				activeChar.sendPacket(SystemMessageId.THE_CLAN_REPUTATION_IS_TOO_LOW);
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Checks if a player can use this skill while riding.
	 * @param player the player
	 * @return {@code true} if the player can use this skill, {@code false} otherwise
	 */
	public boolean canBeUseWhileRiding(final PlayerInstance player)
	{
		return (_rideState == null) || _rideState.contains(player.getMountType());
	}
	
	/**
	 * @param activeChar the character that requests getting the skill target.
	 * @param forceUse if character pressed ctrl (force pick target)
	 * @param dontMove if character pressed shift (dont move and pick target only if in range)
	 * @param sendMessage send SystemMessageId packet if target is incorrect.
	 * @return {@code WorldObject} this skill can be used on, or {@code null} if there is no such.
	 */
	public WorldObject getTarget(Creature activeChar, boolean forceUse, boolean dontMove, boolean sendMessage)
	{
		return getTarget(activeChar, activeChar.getTarget(), forceUse, dontMove, sendMessage);
	}
	
	/**
	 * @param activeChar the character that requests getting the skill target.
	 * @param seletedTarget the target that has been selected by this character to be checked.
	 * @param forceUse if character pressed ctrl (force pick target)
	 * @param dontMove if character pressed shift (dont move and pick target only if in range)
	 * @param sendMessage send SystemMessageId packet if target is incorrect.
	 * @return the selected {@code WorldObject} this skill can be used on, or {@code null} if there is no such.
	 */
	public WorldObject getTarget(Creature activeChar, WorldObject seletedTarget, boolean forceUse, boolean dontMove, boolean sendMessage)
	{
		try
		{
			return _targetTypeHandler.getTarget(activeChar, seletedTarget, this, forceUse, dontMove, sendMessage);
		}
		catch (Exception e)
		{
			LOGGER.warn("Exception in Skill.getTarget(): {}", e.getMessage(), e);
		}
		activeChar.sendMessage("Target type of skill " + this + " is not currently handled.");
		return null;
	}
	
	/**
	 * @param activeChar the character that needs to gather targets.
	 * @param target the initial target activeChar is focusing upon.
	 * @return list containing objects gathered in a specific geometric way that are valid to be affected by this skill.
	 */
	public List<WorldObject> getTargetsAffected(Creature activeChar, WorldObject target)
	{
		if (target == null)
		{
			return null;
		}
		activeChar.sendDebugMessage("-> " + this + "\n      TT: " + getTargetTypeHandler() + "\n      AS: " + getAffectScopeHandler() + "\n      AO: " + getAffectObjectHandler(), DebugType.SKILLS);
		try
		{
			final List<WorldObject> result = new LinkedList<>();
			_affectScopeHandler.forEachAffected(activeChar, target, this, o -> result.add(o));
			if (activeChar.isDebug())
			{
				_affectScopeHandler.drawEffected(activeChar, target, this);
			}
			return result;
		}
		catch (Exception e)
		{
			LOGGER.warn("Exception in Skill.getTargetsAffected(): {}", e.getMessage(), e);
		}
		activeChar.sendMessage("Target affect scope of skill " + this + " is not currently handled.");
		return null;
	}
	
	/**
	 * @param activeChar the character that needs to gather targets.
	 * @param target the initial target activeChar is focusing upon.
	 * @param action for each affected target.
	 */
	public void forEachTargetAffected(Creature activeChar, WorldObject target, Consumer<? super WorldObject> action)
	{
		if (target == null)
		{
			return;
		}
		
		try
		{
			_affectScopeHandler.forEachAffected(activeChar, target, this, action);
			if (activeChar.isDebug())
			{
				_affectScopeHandler.drawEffected(activeChar, target, this);
			}
		}
		catch (Exception e)
		{
			LOGGER.warn("Exception in Skill.forEachTargetAffected(): {}", e.getMessage(), e);
		}
	}
	
	/**
	 * Adds an effect to the effect list for the given effect scope.
	 * @param effectScope the effect scope
	 * @param effect the effect
	 */
	public void addEffect(EffectScope effectScope, AbstractEffect effect)
	{
		_effectLists.computeIfAbsent(effectScope, k -> new ArrayList<>()).add(effect);
	}
	
	/**
	 * Gets the skill effects.
	 * @param effectScope the effect scope
	 * @return the list of effects for the give scope
	 */
	public List<AbstractEffect> getEffects(EffectScope effectScope)
	{
		return _effectLists.getOrDefault(effectScope, Collections.emptyList());
	}
	
	/**
	 * Verify if this skill has effects for the given scope.
	 * @param effectScope the effect scope
	 * @return {@code true} if this skill has effects for the given scope, {@code false} otherwise
	 */
	public boolean hasEffects(EffectScope effectScope)
	{
		return !getEffects(effectScope).isEmpty();
	}
	
	/**
	 * Applies the instant effects of this skill effect scope to the target from caster for the given effect scope.
	 * @param effectScope the effect scope
	 * @param caster the caster
	 * @param target the target
	 * @param skill the skill
	 * @param item the item
	 */
	public void applyInstantEffects(EffectScope effectScope, Creature caster, WorldObject target, Skill skill, ItemInstance item)
	{
		for (AbstractEffect effect : getEffects(effectScope))
		{
			if (effect.calcSuccess(caster, target, skill))
			{
				effect.instant(caster, target, skill, item);
			}
		}
	}
	
	/**
	 * Applies the continuous effects of this skill to the buff info for the given effect scope.
	 * @param effectScope the effect scope
	 * @param info the buff info
	 */
	public void applyContinuousEffects(EffectScope effectScope, BuffInfo info)
	{
		for (AbstractEffect effect : getEffects(effectScope))
		{
			if (effect.checkPumpCondition(info.getEffector(), info.getEffected(), info.getSkill()))
			{
				info.addEffect(effect);
			}
		}
	}
	
	/**
	 * Method overload for {@link Skill#applyEffects(Creature, Creature, boolean, boolean, int, ItemInstance)}.<br>
	 * Simplify the calls.
	 * @param effector the caster of the skill
	 * @param effected the target of the effect
	 */
	public void applyEffects(Creature effector, Creature effected)
	{
		applyEffects(effector, effected, false, true, 0, null);
	}
	
	/**
	 * Method overload for {@link Skill#applyEffects(Creature, Creature, boolean, boolean, int, ItemInstance)}.<br>
	 * Simplify the calls.
	 * @param effector the caster of the skill
	 * @param effected the target of the effect
	 * @param item
	 */
	public void applyEffects(Creature effector, Creature effected, ItemInstance item)
	{
		applyEffects(effector, effected, false, true, 0, item);
	}
	
	/**
	 * Method overload for {@link Skill#applyEffects(Creature, Creature, boolean, boolean, int, ItemInstance)}.<br>
	 * Simplify the calls, allowing abnormal time time customization.
	 * @param effector the caster of the skill
	 * @param effected the target of the effect
	 * @param instant if {@code true} instant effects will be applied to the effected
	 * @param abnormalTime custom abnormal time, if equal or lesser than zero will be ignored
	 */
	public void applyEffects(Creature effector, Creature effected, boolean instant, int abnormalTime)
	{
		applyEffects(effector, effected, false, instant, abnormalTime, null);
	}
	
	/**
	 * Applies the effects from this skill to the target.
	 * @param effector the caster of the skill
	 * @param effected the target of the effect
	 * @param self if {@code true} self-effects will be casted on the caster
	 * @param instant if {@code true} instant effects will be applied to the effected
	 * @param abnormalTime custom abnormal time, if equal or lesser than zero will be ignored
	 * @param item
	 */
	public void applyEffects(Creature effector, Creature effected, boolean self, boolean instant, int abnormalTime, ItemInstance item)
	{
		// null targets cannot receive any effects.
		if (effected == null)
		{
			return;
		}
		
		if (effected.isIgnoringSkillEffects(getId(), getLevel()))
		{
			effected.sendDebugMessage("Skill " + toString() + " has been ignored (ignoring skill effects)", DebugType.SKILLS);
			return;
		}
		
		boolean addContinuousEffects = _operateType.isToggle() || (_operateType.isContinuous() && Formulas.calcEffectSuccess(effector, effected, this));
		if (!self)
		{
			if (addContinuousEffects)
			{
				final BuffInfo info = new BuffInfo(effector, effected, this, !instant, item, null);
				if (abnormalTime > 0)
				{
					info.setAbnormalTime(abnormalTime);
				}
				
				applyContinuousEffects(EffectScope.GENERAL, info);
				applyContinuousEffects((effector.isPlayable() && effected.isPlayable()) ? EffectScope.PVP : EffectScope.PVE, info);
				
				// Aura skills reset the abnormal time.
				final BuffInfo existingInfo = _operateType.isAura() ? effected.getEffectList().getBuffInfoBySkillId(getId()) : null;
				if (existingInfo != null)
				{
					existingInfo.resetAbnormalTime(info.getAbnormalTime());
				}
				else
				{
					effected.getEffectList().add(info);
				}
				
				// Check for mesmerizing debuffs and increase resist level.
				if (isDebuff() && (getBasicProperty() != BasicProperty.NONE) && effected.hasBasicPropertyResist())
				{
					final BasicPropertyResist resist = effected.getBasicPropertyResist(getBasicProperty());
					resist.increaseResistLevel();
					effected.sendDebugMessage(toString() + " has increased your " + getBasicProperty() + " debuff resistance to " + resist.getResistLevel() + " level for " + resist.getRemainTime().toMillis() + " milliseconds.", DebugType.SKILLS);
				}
			}
			
			if (instant)
			{
				applyInstantEffects(EffectScope.GENERAL, effector, effected, this, item);
				applyInstantEffects((effector.isPlayable() && effected.isPlayable()) ? EffectScope.PVP : EffectScope.PVE, effector, effected, this, item);
			}
			
			// Support for buff sharing feature including healing herbs.
			if (isSharedWithSummon() && effected.isPlayer() && effected.hasServitors() && !isTransformation())
			{
				if ((addContinuousEffects && isContinuous() && !isDebuff()) || isRecoveryHerb())
				{
					effected.getServitors().values().forEach(s -> applyEffects(effector, s, isRecoveryHerb(), 0));
				}
			}
		}
		else
		{
			addContinuousEffects = _operateType.isToggle() || (_operateType.isSelfContinuous() && Formulas.calcEffectSuccess(effector, effector, this));
			
			if (addContinuousEffects)
			{
				final BuffInfo info = new BuffInfo(effector, effector, this, !instant, item, null);
				if (abnormalTime > 0)
				{
					info.setAbnormalTime(abnormalTime);
				}
				
				applyContinuousEffects(EffectScope.SELF, info);
				// Aura skills reset the abnormal time.
				final BuffInfo existingInfo = _operateType.isAura() ? effector.getEffectList().getBuffInfoBySkillId(getId()) : null;
				if (existingInfo != null)
				{
					existingInfo.resetAbnormalTime(info.getAbnormalTime());
				}
				else
				{
					info.getEffector().getEffectList().add(info);
				}
			}
			
			if (instant)
			{
				applyInstantEffects(EffectScope.SELF, effector, effector, this, item);
			}
			
			// Support for buff sharing feature.
			// Avoiding Servitor Share since it's implementation already "shares" the effect.
			if (addContinuousEffects && isSharedWithSummon() && effector.isPlayer() && isContinuous() && !isDebuff() && effector.hasServitors())
			{
				effector.getServitors().values().forEach(s -> applyEffects(effector, s, false, 0));
			}
		}
	}
	
	/**
	 * Applies the channeling effects from this skill to the target.
	 * @param effector the caster of the skill
	 * @param effected the target of the effect
	 */
	public void applyChannelingEffects(Creature effector, Creature effected)
	{
		// null targets cannot receive any effects.
		if (effected == null)
		{
			return;
		}
		
		if (effected.isIgnoringSkillEffects(getId(), getLevel()))
		{
			effected.sendDebugMessage("Skill " + toString() + " has been ignored (ignoring skill effects)", DebugType.SKILLS);
			return;
		}
		
		applyInstantEffects(EffectScope.CHANNELING, effector, effected, this, null);
	}
	
	/**
	 * Activates a skill for the given creature and targets.
	 * @param caster the caster
	 * @param targets the targets
	 */
	public void activateSkill(Creature caster, WorldObject... targets)
	{
		activateSkill(caster, null, targets);
	}
	
	/**
	 * Activates a skill for the given creature and targets.
	 * @param caster the caster
	 * @param item
	 * @param targets the targets
	 */
	public void activateSkill(Creature caster, ItemInstance item, WorldObject... targets)
	{
		activateSkill(caster, null, item, targets);
	}
	
	/**
	 * Activates a skill for the given cubic and targets.
	 * @param cubic the cubic
	 * @param targets the targets
	 */
	public void activateSkill(CubicInstance cubic, WorldObject... targets)
	{
		activateSkill(cubic.getOwner(), cubic, null, targets);
	}
	
	/**
	 * Activates the skill to the targets.
	 * @param caster the caster
	 * @param cubic the cubic that cast the skill, can be {@code null}
	 * @param item
	 * @param targets the targets
	 */
	public final void activateSkill(Creature caster, CubicInstance cubic, ItemInstance item, WorldObject... targets)
	{
		// TODO: replace with AI
		switch (getId())
		{
			case 5852:
			case 5853:
			{
				final L2BlockInstance block = targets[0] instanceof L2BlockInstance ? (L2BlockInstance) targets[0] : null;
				final PlayerInstance player = caster.isPlayer() ? (PlayerInstance) caster : null;
				if ((block == null) || (player == null))
				{
					return;
				}
				
				final int arena = player.getBlockCheckerArena();
				if (arena != -1)
				{
					final ArenaParticipantsHolder holder = HandysBlockCheckerManager.getInstance().getHolder(arena);
					if (holder == null)
					{
						return;
					}
					
					final int team = holder.getPlayerTeam(player);
					final int color = block.getColorEffect();
					if ((team == 0) && (color == 0x00))
					{
						block.changeColor(player, holder, team);
					}
					else if ((team == 1) && (color == 0x53))
					{
						block.changeColor(player, holder, team);
					}
				}
				break;
			}
			default:
			{
				for (WorldObject targetObject : targets)
				{
					if (!targetObject.isCreature())
					{
						continue;
					}
					
					Creature target = (Creature) targetObject;
					if (Formulas.calcBuffDebuffReflection(target, this))
					{
						// if skill is reflected instant effects should be casted on target
						// and continuous effects on caster
						applyEffects(target, caster, false, 0);
						
						applyInstantEffects(EffectScope.GENERAL, caster, target, this, item);
						applyInstantEffects((caster.isPlayable() && target.isPlayable()) ? EffectScope.PVP : EffectScope.PVE, caster, target, this, item);
					}
					else
					{
						applyEffects(caster, target, item);
					}
				}
				break;
			}
		}
		
		// Self Effect
		if (hasEffects(EffectScope.SELF))
		{
			applyEffects(caster, caster, true, true, 0, item);
		}
		
		if (cubic == null)
		{
			if (useSpiritShot())
			{
				caster.unchargeShot(caster.isChargedShot(ShotType.BLESSED_SPIRITSHOTS) ? ShotType.BLESSED_SPIRITSHOTS : ShotType.SPIRITSHOTS);
			}
			else if (useSoulShot())
			{
				caster.unchargeShot(ShotType.SOULSHOTS);
			}
		}
		
		if (isSuicideAttack())
		{
			caster.doDie(caster);
		}
	}
	
	/**
	 * Adds a condition to the condition list for the given condition scope.
	 * @param skillConditionScope the condition scope
	 * @param skillCondition the condition
	 */
	public void addCondition(SkillConditionScope skillConditionScope, ISkillCondition skillCondition)
	{
		_conditionLists.computeIfAbsent(skillConditionScope, k -> new ArrayList<>()).add(skillCondition);
	}
	
	/**
	 * Checks the conditions of this skills for the given condition scope.
	 * @param skillConditionScope the condition scope
	 * @param caster the caster
	 * @param target the target
	 * @return {@code false} if at least one condition returns false, {@code true} otherwise
	 */
	public boolean checkConditions(SkillConditionScope skillConditionScope, Creature caster, WorldObject target)
	{
		return _conditionLists.getOrDefault(skillConditionScope, Collections.emptyList()).stream().allMatch(c -> c.canUse(caster, this, target));
	}
	
	@Override
	public String toString()
	{
		return "Skill " + _name + "(" + _id + "," + _level + "," + _subLevel + ")";
	}
	
	/**
	 * used for tracking item id in case that item consume cannot be used
	 * @return reference item id
	 */
	public int getReferenceItemId()
	{
		return _refId;
	}
	
	public boolean canBeDispelled()
	{
		return _canBeDispelled;
	}
	
	/**
	 * Verify if the skill can be stolen.
	 * @return {@code true} if skill can be stolen, {@code false} otherwise
	 */
	public boolean canBeStolen()
	{
		return !isPassive() && !isToggle() && !isDebuff() && !isIrreplacableBuff() && !isHeroSkill() && !isGMSkill() && !(isStatic() && (getId() != CommonSkill.CARAVANS_SECRET_MEDICINE.getId())) && canBeDispelled();
	}
	
	public boolean isClanSkill()
	{
		return SkillTreesData.getInstance().isClanSkill(_id, _level);
	}
	
	public boolean isExcludedFromCheck()
	{
		return _excludedFromCheck;
	}
	
	public boolean isWithoutAction()
	{
		return _withoutAction;
	}
	
	/**
	 * Parses all the abnormal visual effects.
	 * @param abnormalVisualEffects the abnormal visual effects list
	 */
	private void parseAbnormalVisualEffect(String abnormalVisualEffects)
	{
		if (abnormalVisualEffects != null)
		{
			final String[] data = abnormalVisualEffects.split(";");
			final Set<AbnormalVisualEffect> aves = new HashSet<>(1);
			for (String aveString : data)
			{
				final AbnormalVisualEffect ave = AbnormalVisualEffect.findByName(aveString);
				if (ave != null)
				{
					aves.add(ave);
				}
				else
				{
					LOGGER.warn("Invalid AbnormalVisualEffect({}) found for Skill({})", aveString, this);
				}
			}
			
			if (!aves.isEmpty())
			{
				_abnormalVisualEffects = aves;
			}
		}
	}
	
	/**
	 * @param effectType Effect type to check if its present on this skill effects.
	 * @param effectTypes Effect types to check if are present on this skill effects.
	 * @return {@code true} if at least one of specified {@link L2EffectType} types is present on this skill effects, {@code false} otherwise.
	 */
	public boolean hasEffectType(L2EffectType effectType, L2EffectType... effectTypes)
	{
		if (_effectTypes == null)
		{
			synchronized (this)
			{
				if (_effectTypes == null)
				{
					Set<Byte> effectTypesSet = new HashSet<>();
					for (List<AbstractEffect> effectList : _effectLists.values())
					{
						if (effectList != null)
						{
							for (AbstractEffect effect : effectList)
							{
								effectTypesSet.add((byte) effect.getEffectType().ordinal());
							}
						}
					}
					
					Byte[] effectTypesArray = effectTypesSet.toArray(new Byte[effectTypesSet.size()]);
					Arrays.sort(effectTypesArray);
					_effectTypes = effectTypesArray;
				}
			}
		}
		
		if (Arrays.binarySearch(_effectTypes, (byte) effectType.ordinal()) >= 0)
		{
			return true;
		}
		
		for (L2EffectType type : effectTypes)
		{
			if (Arrays.binarySearch(_effectTypes, (byte) type.ordinal()) >= 0)
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * @param effectScope Effect Scope to look inside for the specific effect type.
	 * @param effectType Effect type to check if its present on this skill effects.
	 * @param effectTypes Effect types to check if are present on this skill effects.
	 * @return {@code true} if at least one of specified {@link L2EffectType} types is present on this skill effects, {@code false} otherwise.
	 */
	public boolean hasEffectType(EffectScope effectScope, L2EffectType effectType, L2EffectType... effectTypes)
	{
		if (hasEffects(effectScope))
		{
			return false;
		}
		
		for (AbstractEffect effect : _effectLists.get(effectScope))
		{
			if (effectType == effect.getEffectType())
			{
				return true;
			}
			
			for (L2EffectType type : effectTypes)
			{
				if (type == effect.getEffectType())
				{
					return true;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * @return icon of the current skill.
	 */
	public String getIcon()
	{
		return _icon;
	}
	
	public long getChannelingTickInterval()
	{
		return _channelingTickInterval;
	}
	
	public long getChannelingTickInitialDelay()
	{
		return _channelingStart;
	}
	
	public Set<MountType> getRideState()
	{
		return _rideState;
	}
	
	public boolean isMentoring()
	{
		return _isMentoring;
	}
	
	/**
	 * @param activeChar
	 * @return alternative skill that has been attached due to the effect of toggle skills on the player (e.g Fire Stance, Water Stance).
	 */
	public Skill getAttachedSkill(Creature activeChar)
	{
		// If character is double casting, return double cast skill.
		if ((getDoubleCastSkill() > 0) && activeChar.getStat().has(BooleanStat.DOUBLE_CAST))
		{
			return SkillData.getInstance().getSkill(getDoubleCastSkill(), getLevel(), getSubLevel());
		}
		
		// Default toggle group ID, assume nothing attached.
		if ((getAttachToggleGroupId() <= 0) || (getAttachSkills() == null))
		{
			return null;
		}
		
		//@formatter:off
		final int toggleSkillId = activeChar.getEffectList().getEffects().stream()
			.filter(info -> info.getSkill().getToggleGroupId() == getAttachToggleGroupId())
			.mapToInt(info -> info.getSkill().getId())
			.findAny().orElse(0);
		//@formatter:on
		
		// No active toggles with this toggle group ID found.
		if (toggleSkillId == 0)
		{
			return null;
		}
		
		final AttachSkillHolder attachedSkill = getAttachSkills().stream().filter(ash -> ash.getRequiredSkillId() == toggleSkillId).findAny().orElse(null);
		
		// No attached skills for this toggle found.
		if (attachedSkill == null)
		{
			return null;
		}
		
		return SkillData.getInstance().getSkill(attachedSkill.getSkillId(), getLevel(), getSubLevel());
	}
	
	public boolean canDoubleCast()
	{
		return _canDoubleCast;
	}
	
	public int getDoubleCastSkill()
	{
		return _doubleCastSkill;
	}
	
	public boolean isSharedWithSummon()
	{
		return _isSharedWithSummon;
	}
	
	public boolean isNecessaryToggle()
	{
		return _isNecessaryToggle;
	}
	
	public boolean isDeleteAbnormalOnLeave()
	{
		return _deleteAbnormalOnLeave;
	}
	
	/**
	 * @return {@code true} if the buff cannot be replaced, canceled, removed on death, etc.<br>
	 *         It can be only overriden by higher stack, but buff still remains ticking and activates once the higher stack buff has passed away.
	 */
	public boolean isIrreplacableBuff()
	{
		return _irreplacableBuff;
	}
	
	public boolean isDisplayInList()
	{
		return _displayInList;
	}
	
	/**
	 * @return if skill could not be requested for use by players.
	 */
	public boolean isBlockActionUseSkill()
	{
		return _blockActionUseSkill;
	}
	
	public int getToggleGroupId()
	{
		return _toggleGroupId;
	}
	
	public int getAttachToggleGroupId()
	{
		return _attachToggleGroupId;
	}
	
	public List<AlterSkillHolder> getAlterSkills()
	{
		return _alterSkills;
	}
	
	public List<AttachSkillHolder> getAttachSkills()
	{
		return _attachSkills;
	}
	
	public Set<AbnormalType> getAbnormalResists()
	{
		return _abnormalResists;
	}
	
	public double getMagicCriticalRate()
	{
		return _magicCriticalRate;
	}
	
	public SkillBuffType getBuffType()
	{
		return isTriggeredSkill() ? SkillBuffType.TRIGGER : isToggle() ? SkillBuffType.TOGGLE : isDance() ? SkillBuffType.DANCE : isDebuff() ? SkillBuffType.DEBUFF : !isHealingPotionSkill() ? SkillBuffType.BUFF : SkillBuffType.NONE;
	}
	
	public boolean isEnchantable()
	{
		return EnchantSkillGroupsData.getInstance().isEnchantable(this);
	}
}
