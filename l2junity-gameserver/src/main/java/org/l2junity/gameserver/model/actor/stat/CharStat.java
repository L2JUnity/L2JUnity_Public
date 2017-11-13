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
package org.l2junity.gameserver.model.actor.stat;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.stream.Stream;

import org.l2junity.commons.util.MathUtil;
import org.l2junity.gameserver.config.PlayerConfig;
import org.l2junity.gameserver.enums.AttributeType;
import org.l2junity.gameserver.enums.Position;
import org.l2junity.gameserver.model.CharEffectList;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.items.instance.ItemInstance;
import org.l2junity.gameserver.model.skills.AbnormalType;
import org.l2junity.gameserver.model.skills.BuffInfo;
import org.l2junity.gameserver.model.skills.EffectScope;
import org.l2junity.gameserver.model.skills.Skill;
import org.l2junity.gameserver.model.skills.SkillConditionScope;
import org.l2junity.gameserver.model.stats.BooleanStat;
import org.l2junity.gameserver.model.stats.DoubleStat;
import org.l2junity.gameserver.model.stats.DoubleStatValue;
import org.l2junity.gameserver.model.stats.Formulas;
import org.l2junity.gameserver.model.stats.MoveType;
import org.l2junity.gameserver.model.stats.StatsHolder;
import org.l2junity.gameserver.model.stats.TraitType;
import org.l2junity.gameserver.model.zone.ZoneId;

public class CharStat
{
	private final Creature _activeChar;
	private long _exp = 0;
	private long _sp = 0;
	private byte _level = 1;
	/** Creature's maximum buff count. */
	private int _maxBuffCount = PlayerConfig.BUFFS_MAX_AMOUNT;
	private double _vampiricSum = 0;
	private double _supportItemBonusRate = 0;
	
	private final Map<DoubleStat, DoubleStatValue> _doubleStats = new EnumMap<>(DoubleStat.class);
	private final Map<DoubleStat, Map<MoveType, Double>> _moveTypeStats = new ConcurrentHashMap<>();
	private final Map<Integer, Double> _reuseStat = new ConcurrentHashMap<>();
	private final Map<Integer, Double> _castingStat = new HashMap<>();
	private final Map<Integer, Double> _mpConsumeStat = new ConcurrentHashMap<>();
	private final Map<Integer, LinkedList<Double>> _skillEvasionStat = new ConcurrentHashMap<>();
	private final Map<DoubleStat, Map<Position, Double>> _positionStats = new ConcurrentHashMap<>();
	private final Deque<StatsHolder> _additionalAdd = new ConcurrentLinkedDeque<>();
	private final Deque<StatsHolder> _additionalMul = new ConcurrentLinkedDeque<>();
	private final Map<DoubleStat, Double> _fixedValue = new ConcurrentHashMap<>();
	private final Set<BooleanStat> _booleanStats = EnumSet.noneOf(BooleanStat.class);
	private final Set<Integer> _blockActionsAllowedSkills = new HashSet<>();
	private final Set<Integer> _blockActionsAllowedItems = new HashSet<>();
	
	private final float[] _attackTraitValues = new float[TraitType.values().length];
	private final float[] _defenceTraitValues = new float[TraitType.values().length];
	private final Set<TraitType> _attackTraits = EnumSet.noneOf(TraitType.class);
	private final Set<TraitType> _defenceTraits = EnumSet.noneOf(TraitType.class);
	private final Set<TraitType> _invulnerableTraits = EnumSet.noneOf(TraitType.class);
	
	/** Values to be recalculated after every stat update */
	private double _attackSpeedMultiplier = 1;
	private double _mAttackSpeedMultiplier = 1;
	
	private final ReentrantReadWriteLock _lock = new ReentrantReadWriteLock();
	
	public CharStat(Creature activeChar)
	{
		_activeChar = activeChar;
	}
	
	/**
	 * @return the Accuracy (base+modifier) of the L2Character in function of the Weapon Expertise Penalty.
	 */
	public int getAccuracy()
	{
		return (int) getValue(DoubleStat.ACCURACY_COMBAT);
	}
	
	/**
	 * @return the Magic Accuracy (base+modifier) of the L2Character
	 */
	public int getMagicAccuracy()
	{
		return (int) getValue(DoubleStat.ACCURACY_MAGIC);
	}
	
	public Creature getActiveChar()
	{
		return _activeChar;
	}
	
	/**
	 * @return the Attack Speed multiplier (base+modifier) of the L2Character to get proper animations.
	 */
	public final double getAttackSpeedMultiplier()
	{
		return _attackSpeedMultiplier;
	}
	
	public final double getMAttackSpeedMultiplier()
	{
		return _mAttackSpeedMultiplier;
	}
	
	/**
	 * @return the CON of the L2Character (base+modifier).
	 */
	public final int getCON()
	{
		return (int) getValue(DoubleStat.STAT_CON);
	}
	
	/**
	 * @param init
	 * @return the Critical Damage rate (base+modifier) of the L2Character.
	 */
	public final double getCriticalDmg(double init)
	{
		return getValue(DoubleStat.CRITICAL_DAMAGE, init);
	}
	
	/**
	 * @return the Critical Hit rate (base+modifier) of the L2Character.
	 */
	public int getCriticalHit()
	{
		return (int) getValue(DoubleStat.CRITICAL_RATE);
	}
	
	/**
	 * @return the DEX of the L2Character (base+modifier).
	 */
	public final int getDEX()
	{
		return (int) getValue(DoubleStat.STAT_DEX);
	}
	
	/**
	 * @return the Attack Evasion rate (base+modifier) of the L2Character.
	 */
	public int getEvasionRate()
	{
		return (int) getValue(DoubleStat.EVASION_RATE);
	}
	
	/**
	 * @return the Attack Evasion rate (base+modifier) of the L2Character.
	 */
	public int getMagicEvasionRate()
	{
		return (int) getValue(DoubleStat.MAGIC_EVASION_RATE);
	}
	
	public long getExp()
	{
		return _exp;
	}
	
	public void setExp(long value)
	{
		_exp = value;
	}
	
	/**
	 * @return the INT of the L2Character (base+modifier).
	 */
	public int getINT()
	{
		return (int) getValue(DoubleStat.STAT_INT);
	}
	
	public byte getLevel()
	{
		return _level;
	}
	
	public void setLevel(byte value)
	{
		_level = value;
	}
	
	/**
	 * @param skill
	 * @return the Magical Attack range (base+modifier) of the L2Character.
	 */
	public final int getMagicalAttackRange(Skill skill)
	{
		return skill != null ? (int) getValue(DoubleStat.MAGIC_ATTACK_RANGE, skill.getCastRange()) : _activeChar.getTemplate().getBaseAttackRange();
	}
	
	public int getMaxCp()
	{
		return (int) getValue(DoubleStat.MAX_CP);
	}
	
	public int getMaxRecoverableCp()
	{
		return (int) getValue(DoubleStat.MAX_RECOVERABLE_CP, getMaxCp());
	}
	
	public int getMaxHp()
	{
		return (int) getValue(DoubleStat.MAX_HP);
	}
	
	public int getMaxRecoverableHp()
	{
		return (int) getValue(DoubleStat.MAX_RECOVERABLE_HP, getMaxHp());
	}
	
	public int getMaxMp()
	{
		return (int) getValue(DoubleStat.MAX_MP);
	}
	
	public int getMaxRecoverableMp()
	{
		return (int) getValue(DoubleStat.MAX_RECOVERABLE_MP, getMaxMp());
	}
	
	/**
	 * Return the MAtk (base+modifier) of the L2Character.<br>
	 * <B><U>Example of use</U>: Calculate Magic damage
	 * @return
	 */
	public int getMAtk()
	{
		return (int) getValue(DoubleStat.MAGIC_ATTACK);
	}
	
	/**
	 * @return the MAtk Speed (base+modifier) of the L2Character in function of the Armour Expertise Penalty.
	 */
	public int getMAtkSpd()
	{
		return (int) getValue(DoubleStat.MAGIC_ATTACK_SPEED);
	}
	
	/**
	 * @return the Magic Critical Hit rate (base+modifier) of the L2Character.
	 */
	public final int getMCriticalHit()
	{
		return (int) getValue(DoubleStat.MAGIC_CRITICAL_RATE);
	}
	
	/**
	 * <B><U>Example of use </U>: Calculate Magic damage.
	 * @return the MDef (base+modifier) of the L2Character against a skill in function of abnormal effects in progress.
	 */
	public int getMDef()
	{
		return (int) getValue(DoubleStat.MAGICAL_DEFENCE);
	}
	
	/**
	 * @return the MEN of the L2Character (base+modifier).
	 */
	public final int getMEN()
	{
		return (int) getValue(DoubleStat.STAT_MEN);
	}
	
	public final int getLUC()
	{
		return (int) getValue(DoubleStat.STAT_LUC);
	}
	
	public final int getCHA()
	{
		return (int) getValue(DoubleStat.STAT_CHA);
	}
	
	public double getMovementSpeedMultiplier()
	{
		double baseSpeed;
		if (_activeChar.isInsideZone(ZoneId.WATER))
		{
			baseSpeed = _activeChar.getTemplate().getBaseValue(_activeChar.isRunning() ? DoubleStat.SWIM_RUN_SPEED : DoubleStat.SWIM_WALK_SPEED, 0);
		}
		else
		{
			baseSpeed = _activeChar.getTemplate().getBaseValue(_activeChar.isRunning() ? DoubleStat.RUN_SPEED : DoubleStat.WALK_SPEED, 0);
		}
		return getMoveSpeed() * (1. / baseSpeed);
	}
	
	/**
	 * @return the RunSpeed (base+modifier) of the L2Character in function of the Armour Expertise Penalty.
	 */
	public double getRunSpeed()
	{
		return getValue(_activeChar.isInsideZone(ZoneId.WATER) ? DoubleStat.SWIM_RUN_SPEED : DoubleStat.RUN_SPEED);
	}
	
	/**
	 * @return the WalkSpeed (base+modifier) of the L2Character.
	 */
	public double getWalkSpeed()
	{
		return getValue(_activeChar.isInsideZone(ZoneId.WATER) ? DoubleStat.SWIM_WALK_SPEED : DoubleStat.WALK_SPEED);
	}
	
	/**
	 * @return the SwimRunSpeed (base+modifier) of the L2Character.
	 */
	public double getSwimRunSpeed()
	{
		return getValue(DoubleStat.SWIM_RUN_SPEED);
	}
	
	/**
	 * @return the SwimWalkSpeed (base+modifier) of the L2Character.
	 */
	public double getSwimWalkSpeed()
	{
		return getValue(DoubleStat.SWIM_WALK_SPEED);
	}
	
	/**
	 * @return the RunSpeed (base+modifier) or WalkSpeed (base+modifier) of the L2Character in function of the movement type.
	 */
	public double getMoveSpeed()
	{
		if (_activeChar.isInsideZone(ZoneId.WATER))
		{
			return _activeChar.isRunning() ? getSwimRunSpeed() : getSwimWalkSpeed();
		}
		return _activeChar.isRunning() ? getRunSpeed() : getWalkSpeed();
	}
	
	/**
	 * @return the PAtk (base+modifier) of the L2Character.
	 */
	public int getPAtk()
	{
		return (int) getValue(DoubleStat.PHYSICAL_ATTACK);
	}
	
	/**
	 * @return the PAtk Speed (base+modifier) of the L2Character in function of the Armour Expertise Penalty.
	 */
	public int getPAtkSpd()
	{
		return (int) getValue(DoubleStat.PHYSICAL_ATTACK_SPEED);
	}
	
	/**
	 * @return the PDef (base+modifier) of the L2Character.
	 */
	public int getPDef()
	{
		return (int) getValue(DoubleStat.PHYSICAL_DEFENCE);
	}
	
	/**
	 * @return the Physical Attack range (base+modifier) of the L2Character.
	 */
	public final int getPhysicalAttackRange()
	{
		return (int) getValue(DoubleStat.PHYSICAL_ATTACK_RANGE);
	}
	
	public int getPhysicalAttackRadius()
	{
		return 40;
	}
	
	public int getPhysicalAttackAngle()
	{
		return 120;
	}
	
	/**
	 * @return the weapon reuse modifier.
	 */
	public final double getWeaponReuseModifier()
	{
		return getValue(DoubleStat.ATK_REUSE, 1);
	}
	
	/**
	 * @return the ShieldDef rate (base+modifier) of the L2Character.
	 */
	public final int getShldDef()
	{
		return (int) getValue(DoubleStat.SHIELD_DEFENCE);
	}
	
	public long getSp()
	{
		return _sp;
	}
	
	public void setSp(long value)
	{
		_sp = value;
	}
	
	/**
	 * @return the STR of the L2Character (base+modifier).
	 */
	public final int getSTR()
	{
		return (int) getValue(DoubleStat.STAT_STR);
	}
	
	/**
	 * @return the WIT of the L2Character (base+modifier).
	 */
	public final int getWIT()
	{
		return (int) getValue(DoubleStat.STAT_WIT);
	}
	
	/**
	 * @param skill
	 * @return the mpConsume.
	 */
	public final int getMpConsume(Skill skill)
	{
		if (skill == null)
		{
			return 1;
		}
		double mpConsume = skill.getMpConsume();
		double nextDanceMpCost = Math.ceil(skill.getMpConsume() / 2.);
		if (skill.isDance())
		{
			if (PlayerConfig.DANCE_CONSUME_ADDITIONAL_MP && (_activeChar != null) && (_activeChar.getDanceCount() > 0))
			{
				mpConsume += _activeChar.getDanceCount() * nextDanceMpCost;
			}
		}
		
		return (int) (mpConsume * getMpConsumeTypeValue(skill.getMagicType()));
	}
	
	/**
	 * @param skill
	 * @return the mpInitialConsume.
	 */
	public final int getMpInitialConsume(Skill skill)
	{
		return skill == null ? 1 : skill.getMpInitialConsume();
	}
	
	public AttributeType getAttackElement()
	{
		ItemInstance weaponInstance = _activeChar.getActiveWeaponInstance();
		// 1st order - weapon element
		if ((weaponInstance != null) && (weaponInstance.getAttackAttributeType() != AttributeType.NONE))
		{
			return weaponInstance.getAttackAttributeType();
		}
		
		//@formatter:off
		// Find the greatest attack element attribute greater than 0.
		return Arrays.stream(AttributeType.ATTRIBUTE_TYPES)
			.filter(a -> getAttackElementValue(a) > 0)
			.sorted(Comparator.<AttributeType>comparingInt(this::getAttackElementValue).reversed())
			.findFirst()
			.orElse(AttributeType.NONE);
		//@formatter:on
	}
	
	public int getAttackElementValue(AttributeType attackAttribute)
	{
		switch (attackAttribute)
		{
			case FIRE:
				return (int) getValue(DoubleStat.FIRE_POWER);
			case WATER:
				return (int) getValue(DoubleStat.WATER_POWER);
			case WIND:
				return (int) getValue(DoubleStat.WIND_POWER);
			case EARTH:
				return (int) getValue(DoubleStat.EARTH_POWER);
			case HOLY:
				return (int) getValue(DoubleStat.HOLY_POWER);
			case DARK:
				return (int) getValue(DoubleStat.DARK_POWER);
			default:
				return 0;
		}
	}
	
	public int getDefenseElementValue(AttributeType defenseAttribute)
	{
		switch (defenseAttribute)
		{
			case FIRE:
				return (int) getValue(DoubleStat.FIRE_RES);
			case WATER:
				return (int) getValue(DoubleStat.WATER_RES);
			case WIND:
				return (int) getValue(DoubleStat.WIND_RES);
			case EARTH:
				return (int) getValue(DoubleStat.EARTH_RES);
			case HOLY:
				return (int) getValue(DoubleStat.HOLY_RES);
			case DARK:
				return (int) getValue(DoubleStat.DARK_RES);
			default:
				return (int) getValue(DoubleStat.BASE_ATTRIBUTE_RES);
		}
	}
	
	public void mergeAttackTrait(TraitType traitType, float value)
	{
		_attackTraitValues[traitType.ordinal()] *= value;
		_attackTraits.add(traitType);
	}
	
	public float getAttackTrait(TraitType traitType)
	{
		_lock.readLock().lock();
		try
		{
			return _attackTraitValues[traitType.ordinal()];
		}
		finally
		{
			_lock.readLock().unlock();
		}
	}
	
	public boolean hasAttackTrait(TraitType traitType)
	{
		_lock.readLock().lock();
		try
		{
			return _attackTraits.contains(traitType);
		}
		finally
		{
			_lock.readLock().unlock();
		}
	}
	
	public void mergeDefenceTrait(TraitType traitType, float value)
	{
		_defenceTraitValues[traitType.ordinal()] *= value;
		_defenceTraits.add(traitType);
	}
	
	public float getDefenceTrait(TraitType traitType)
	{
		_lock.readLock().lock();
		try
		{
			return _defenceTraitValues[traitType.ordinal()];
		}
		finally
		{
			_lock.readLock().unlock();
		}
	}
	
	public boolean hasDefenceTrait(TraitType traitType)
	{
		_lock.readLock().lock();
		try
		{
			return _defenceTraits.contains(traitType);
		}
		finally
		{
			_lock.readLock().unlock();
		}
	}
	
	public void mergeInvulnerableTrait(TraitType traitType)
	{
		_invulnerableTraits.add(traitType);
	}
	
	public boolean isInvulnerableTrait(TraitType traitType)
	{
		_lock.readLock().lock();
		try
		{
			return _invulnerableTraits.contains(traitType);
		}
		finally
		{
			_lock.readLock().unlock();
		}
	}
	
	/**
	 * Gets the maximum buff count.
	 * @return the maximum buff count
	 */
	public int getMaxBuffCount()
	{
		_lock.readLock().lock();
		try
		{
			return _maxBuffCount;
		}
		finally
		{
			_lock.readLock().unlock();
		}
	}
	
	/**
	 * Sets the maximum buff count.
	 * @param buffCount the buff count
	 */
	public void mergeMaxBuffCount(int buffCount)
	{
		_maxBuffCount += buffCount;
	}
	
	/**
	 * Merges the double stat add
	 * @param doubleStat the double stat
	 * @param value the value
	 */
	public void mergeAdd(DoubleStat doubleStat, double value)
	{
		final DoubleStatValue doubleStatValue = _doubleStats.computeIfAbsent(doubleStat, DoubleStatValue::new);
		doubleStatValue.setAdd(doubleStat.add(doubleStatValue.getAdd(), value));
	}
	
	/**
	 * Merges the double stat mul
	 * @param doubleStat the double stat
	 * @param mul the mul
	 */
	public void mergeMul(DoubleStat doubleStat, double mul)
	{
		final DoubleStatValue doubleStatValue = _doubleStats.computeIfAbsent(doubleStat, DoubleStatValue::new);
		doubleStatValue.setMul(doubleStat.mul(doubleStatValue.getMul(), mul));
	}
	
	/**
	 * @param doubleStat
	 * @return the add value
	 */
	public double getAdd(DoubleStat doubleStat)
	{
		_lock.readLock().lock();
		try
		{
			final DoubleStatValue doubleStatValue = _doubleStats.get(doubleStat);
			return doubleStatValue != null ? doubleStatValue.getAdd() : doubleStat.getResetAddValue();
		}
		finally
		{
			_lock.readLock().unlock();
		}
	}
	
	/**
	 * @param doubleStat
	 * @return the mul value
	 */
	public double getMul(DoubleStat doubleStat)
	{
		_lock.readLock().lock();
		try
		{
			final DoubleStatValue doubleStatValue = _doubleStats.get(doubleStat);
			return doubleStatValue != null ? doubleStatValue.getMul() : doubleStat.getResetMulValue();
		}
		finally
		{
			_lock.readLock().unlock();
		}
	}
	
	/**
	 * @param stat
	 * @param baseValue
	 * @return the final value of the stat
	 */
	public double getValue(DoubleStat stat, double baseValue)
	{
		final Double fixedValue = _fixedValue.get(stat);
		return fixedValue != null ? fixedValue : stat.finalize(_activeChar, OptionalDouble.of(baseValue));
	}
	
	/**
	 * @param stat
	 * @return the final value of the stat
	 */
	public double getValue(DoubleStat stat)
	{
		final Double fixedValue = _fixedValue.get(stat);
		return fixedValue != null ? fixedValue : stat.finalize(_activeChar, OptionalDouble.empty());
	}
	
	protected void resetStats()
	{
		_doubleStats.forEach((k, v) -> v.reset(k));
		_booleanStats.clear();
		_blockActionsAllowedSkills.clear();
		_blockActionsAllowedItems.clear();
		_castingStat.clear();
		_vampiricSum = 0;
		_maxBuffCount = PlayerConfig.BUFFS_MAX_AMOUNT;
		Arrays.fill(_attackTraitValues, 1f);
		Arrays.fill(_defenceTraitValues, 1f);
		_attackTraits.clear();
		_defenceTraits.clear();
		_invulnerableTraits.clear();
		_mpConsumeStat.clear();
		_reuseStat.clear();
	}
	
	/**
	 * Locks and resets all stats and recalculates all
	 * @param broadcast
	 */
	public final void recalculateStats(boolean broadcast)
	{
		Set<DoubleStat> changedDoubleStats = null;
		_lock.writeLock().lock();
		try
		{
			// Copy old data before wiping it out
			if (broadcast)
			{
				_doubleStats.values().forEach(DoubleStatValue::mark);
			}
			// Wipe all the data
			resetStats();
			
			// Collect all necessary effects
			final CharEffectList effectList = _activeChar.getEffectList();
			final Stream<BuffInfo> options = effectList.getOptions().stream().filter(BuffInfo::isInUse);
			final Stream<BuffInfo> effectsStream = Stream.concat(effectList.getEffects().stream().filter(BuffInfo::isInUse), options != null ? options : Stream.empty());
			
			// Call pump to each effect
			//@formatter:off
			effectsStream.forEach(info -> info.getEffects().stream()
				.filter(effect -> effect.checkPumpCondition(info.getEffector(), info.getEffected(), info.getSkill()))
				.forEach(effect -> effect.pump(info.getEffected(), info.getSkill())));
			//@formatter:on
			
			// Apply all passives
			//@formatter:off
			_activeChar.getAllSkills().stream()
				.filter(Skill::isPassive)
				.filter(skill -> skill.checkConditions(SkillConditionScope.PASSIVE, _activeChar, _activeChar))
				.filter(skill -> skill.hasEffects(EffectScope.GENERAL))
				.forEach(skill -> skill.getEffects(EffectScope.GENERAL).stream()
					.filter(effect -> effect.checkPumpCondition(_activeChar, _activeChar, skill))
					.forEach(effect -> effect.pump(_activeChar, skill)));
			//@formatter:on
			
			if (_activeChar.isSummon() && (_activeChar.getActingPlayer() != null) && _activeChar.getActingPlayer().hasAbnormalType(AbnormalType.ABILITY_CHANGE))
			{
				//@formatter:off
				_activeChar.getActingPlayer().getEffectList().getEffects().stream()
					.filter(BuffInfo::isInUse)
					.filter(info -> info.isAbnormalType(AbnormalType.ABILITY_CHANGE))
					.forEach(info -> info.getSkill().getEffects(EffectScope.GENERAL).stream()
						.filter(effect -> effect.checkPumpCondition(_activeChar, _activeChar, info.getSkill()))
						.forEach(effect -> effect.pump(_activeChar, info.getSkill())));
				//@formatter:on
			}
			
			// Merge with additional stats
			_additionalAdd.stream().filter(holder -> holder.verifyCondition(_activeChar)).forEach(holder -> mergeAdd(holder.getStat(), holder.getValue()));
			_additionalMul.stream().filter(holder -> holder.verifyCondition(_activeChar)).forEach(holder -> mergeMul(holder.getStat(), holder.getValue()));
			
			_attackSpeedMultiplier = Formulas.calcAtkSpdMultiplier(_activeChar);
			_mAttackSpeedMultiplier = Formulas.calcMAtkSpdMultiplier(_activeChar);
			
			if (broadcast)
			{
				// Calculate the difference between old and new stats
				for (Entry<DoubleStat, DoubleStatValue> entry : _doubleStats.entrySet())
				{
					if (entry.getValue().hasChanged())
					{
						if (changedDoubleStats == null)
						{
							changedDoubleStats = new HashSet<>();
						}
						changedDoubleStats.add(entry.getKey());
					}
				}
			}
		}
		finally
		{
			_lock.writeLock().unlock();
		}
		
		if (changedDoubleStats != null)
		{
			_activeChar.broadcastModifiedStats(changedDoubleStats);
		}
		
		// Notify recalculation to child classes
		onRecalculateStats(broadcast);
	}
	
	protected void onRecalculateStats(boolean broadcast)
	{
		// Check if current HP/MP/CP is lower than max, and regeneration is not running, start it.
		if ((_activeChar.getCurrentCp() < getMaxCp()) || (_activeChar.getCurrentHp() < getMaxHp()) || (_activeChar.getCurrentMp() < getMaxMp()))
		{
			_activeChar.getStatus().startHpMpRegeneration();
		}
		else
		{
			// Check if Max HP/MP/CP is lower than current due to new stats.
			if (_activeChar.getCurrentCp() > getMaxCp())
			{
				_activeChar.setCurrentCp(getMaxCp());
			}
			if (_activeChar.getCurrentHp() > getMaxHp())
			{
				_activeChar.setCurrentHp(getMaxHp());
			}
			if (_activeChar.getCurrentMp() > getMaxMp())
			{
				_activeChar.setCurrentMp(getMaxMp());
			}
		}
	}
	
	public double getPositionTypeValue(DoubleStat stat, Position position)
	{
		return _positionStats.getOrDefault(stat, Collections.emptyMap()).getOrDefault(position, 1d);
	}
	
	public void mergePositionTypeValue(DoubleStat stat, Position position, double value, BiFunction<? super Double, ? super Double, ? extends Double> func)
	{
		_positionStats.computeIfAbsent(stat, key -> new ConcurrentHashMap<>()).merge(position, value, func);
	}
	
	public double getMoveTypeValue(DoubleStat stat, MoveType type)
	{
		return _moveTypeStats.getOrDefault(stat, Collections.emptyMap()).getOrDefault(type, 0d);
	}
	
	public void mergeMoveTypeValue(DoubleStat stat, MoveType type, double value)
	{
		_moveTypeStats.computeIfAbsent(stat, key -> new ConcurrentHashMap<>()).merge(type, value, MathUtil::add);
	}
	
	public double getReuseTypeValue(int magicType)
	{
		_lock.readLock().lock();
		try
		{
			return _reuseStat.getOrDefault(magicType, 1d);
		}
		finally
		{
			_lock.readLock().unlock();
		}
	}
	
	public void mergeReuseTypeValue(int magicType, double value, BiFunction<? super Double, ? super Double, ? extends Double> func)
	{
		_reuseStat.merge(magicType, value, func);
	}
	
	public double getCastChanceValue(int magicType)
	{
		_lock.readLock().lock();
		try
		{
			return _castingStat.getOrDefault(magicType, 0d);
		}
		finally
		{
			_lock.readLock().unlock();
		}
	}
	
	public void mergeCastChanceValue(int magicType, double value, BiFunction<? super Double, ? super Double, ? extends Double> func)
	{
		_castingStat.merge(magicType, value, func);
	}
	
	public double getMpConsumeTypeValue(int magicType)
	{
		_lock.readLock().lock();
		try
		{
			return _mpConsumeStat.getOrDefault(magicType, 1d);
		}
		finally
		{
			_lock.readLock().unlock();
		}
	}
	
	public void mergeMpConsumeTypeValue(int magicType, double value, BiFunction<? super Double, ? super Double, ? extends Double> func)
	{
		_mpConsumeStat.merge(magicType, value, func);
	}
	
	public double getSkillEvasionTypeValue(int magicType)
	{
		final LinkedList<Double> skillEvasions = _skillEvasionStat.get(magicType);
		if ((skillEvasions != null) && !skillEvasions.isEmpty())
		{
			return skillEvasions.peekLast();
		}
		return 0d;
	}
	
	public void addSkillEvasionTypeValue(int magicType, double value)
	{
		_skillEvasionStat.computeIfAbsent(magicType, k -> new LinkedList<>()).add(value);
	}
	
	public void removeSkillEvasionTypeValue(int magicType, double value)
	{
		_skillEvasionStat.computeIfPresent(magicType, (k, v) ->
		{
			v.remove(value);
			return !v.isEmpty() ? v : null;
		});
	}
	
	public void setSupportItemBonusRate(double rate)
	{
		_supportItemBonusRate = rate;
	}
	
	public double getSupportItemBonus()
	{
		return _supportItemBonusRate;
	}
	
	public void addToVampiricSum(double sum)
	{
		_vampiricSum += sum;
	}
	
	public double getVampiricSum()
	{
		_lock.readLock().lock();
		try
		{
			return _vampiricSum;
		}
		finally
		{
			_lock.readLock().unlock();
		}
	}
	
	public void set(BooleanStat stat)
	{
		_booleanStats.add(stat);
	}
	
	public boolean has(BooleanStat stat)
	{
		_lock.readLock().lock();
		try
		{
			return _booleanStats.contains(stat);
		}
		finally
		{
			_lock.readLock().unlock();
		}
	}
	
	/**
	 * Calculates the time required for this skill to be used again.
	 * @param skill the skill from which reuse time will be calculated.
	 * @return the time in milliseconds this skill is being under reuse.
	 */
	public int getReuseTime(Skill skill)
	{
		return skill.isStaticReuse() ? skill.getReuseDelay() : (int) (skill.getReuseDelay() * getReuseTypeValue(skill.getMagicType()));
	}
	
	/**
	 * Adds static value to the 'add' map of the stat everytime recalculation happens
	 * @param stat
	 * @param value
	 * @param condition
	 * @return
	 */
	public boolean addAdditionalStat(DoubleStat stat, double value, BiPredicate<Creature, StatsHolder> condition)
	{
		return _additionalAdd.add(new StatsHolder(stat, value, condition));
	}
	
	/**
	 * Adds static value to the 'add' map of the stat everytime recalculation happens
	 * @param stat
	 * @param value
	 * @return
	 */
	public boolean addAdditionalStat(DoubleStat stat, double value)
	{
		return _additionalAdd.add(new StatsHolder(stat, value));
	}
	
	/**
	 * @param stat
	 * @param value
	 * @return {@code true} if 'add' was removed, {@code false} in case there wasn't such stat and value
	 */
	public boolean removeAddAdditionalStat(DoubleStat stat, double value)
	{
		final Iterator<StatsHolder> it = _additionalAdd.iterator();
		while (it.hasNext())
		{
			final StatsHolder holder = it.next();
			if ((holder.getStat() == stat) && (holder.getValue() == value))
			{
				it.remove();
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Adds static multiplier to the 'mul' map of the stat everytime recalculation happens
	 * @param stat
	 * @param value
	 * @param condition
	 * @return
	 */
	public boolean mulAdditionalStat(DoubleStat stat, double value, BiPredicate<Creature, StatsHolder> condition)
	{
		return _additionalMul.add(new StatsHolder(stat, value, condition));
	}
	
	/**
	 * Adds static multiplier to the 'mul' map of the stat everytime recalculation happens
	 * @param stat
	 * @param value
	 * @return {@code true}
	 */
	public boolean mulAdditionalStat(DoubleStat stat, double value)
	{
		return _additionalMul.add(new StatsHolder(stat, value));
	}
	
	/**
	 * @param stat
	 * @param value
	 * @return {@code true} if 'mul' was removed, {@code false} in case there wasn't such stat and value
	 */
	public boolean removeMulAdditionalStat(DoubleStat stat, double value)
	{
		final Iterator<StatsHolder> it = _additionalMul.iterator();
		while (it.hasNext())
		{
			final StatsHolder holder = it.next();
			if ((holder.getStat() == stat) && (holder.getValue() == value))
			{
				it.remove();
				return true;
			}
		}
		return false;
	}
	
	/**
	 * @param stat
	 * @param value
	 * @return true if the there wasn't previously set fixed value, {@code false} otherwise
	 */
	public boolean addFixedValue(DoubleStat stat, Double value)
	{
		return _fixedValue.put(stat, value) == null;
	}
	
	/**
	 * @param stat
	 * @return {@code true} if fixed value is removed, {@code false} otherwise
	 */
	public boolean removeFixedValue(DoubleStat stat)
	{
		return _fixedValue.remove(stat) != null;
	}
	
	public void addBlockActionsAllowedSkill(int skillId)
	{
		_blockActionsAllowedSkills.add(skillId);
	}
	
	public boolean isBlockedActionsAllowedSkill(Skill skill)
	{
		_lock.readLock().lock();
		try
		{
			return (_blockActionsAllowedSkills != null) && _blockActionsAllowedSkills.contains(skill.getId());
		}
		finally
		{
			_lock.readLock().unlock();
		}
	}
	
	public void addBlockActionsAllowedItem(int itemId)
	{
		_blockActionsAllowedItems.add(itemId);
	}
	
	public boolean isBlockedActionsAllowedItem(ItemInstance item)
	{
		_lock.readLock().lock();
		try
		{
			return (_blockActionsAllowedItems != null) && _blockActionsAllowedItems.contains(item.getId());
		}
		finally
		{
			_lock.readLock().unlock();
		}
	}
}
