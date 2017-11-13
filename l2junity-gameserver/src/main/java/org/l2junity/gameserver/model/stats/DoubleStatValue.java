package org.l2junity.gameserver.model.stats;

/**
 * A state object representing the value of a double stat.
 * @author NosKun
 */
public class DoubleStatValue
{
	private double _add;
	private double _mul;
	private double _markedAdd;
	private double _markedMul;

	public DoubleStatValue(DoubleStat doubleStat)
	{
		reset(doubleStat);
	}

	/**
	 * Gets the add.
	 * @return the add
	 */
	public double getAdd()
	{
		return _add;
	}

	/**
	 * Sets the add.
	 * @param add the add
	 */
	public void setAdd(double add)
	{
		_add = add;
	}

	/**
	 * Gets the mul.
	 * @return the mul
	 */
	public double getMul()
	{
		return _mul;
	}

	/**
	 * Sets the mul.
	 * @param mul the mul
	 */
	public void setMul(double mul)
	{
		_mul = mul;
	}

	/**
	 * Resets this double stat value.
	 * @param doubleStat the double stat
	 */
	public void reset(DoubleStat doubleStat)
	{
		_add = doubleStat.getResetAddValue();
		_mul = doubleStat.getResetMulValue();
	}

	/**
	 * Marks this double stat value so changes can be detected.
	 */
	public void mark()
	{
		_markedAdd = _add;
		_markedMul = _mul;
	}

	/**
	 * Checks if this double stat value has changed since it was marked.
	 * @return {@code true} if this double stat value has changed since it was marked, {@code false} otherwise
	 */
	public boolean hasChanged()
	{
		return _add != _markedAdd || _mul != _markedMul;
	}
}
