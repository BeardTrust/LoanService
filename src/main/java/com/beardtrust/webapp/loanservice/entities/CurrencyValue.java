package com.beardtrust.webapp.loanservice.entities;

import javax.persistence.Embeddable;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;

/**
 * This class provides an accurate and consistent representation of currency
 * values for entities and models.
 *
 * @author Matthew Crowell <Matthew.Crowell@Smoothstack.com>
 */
@Embeddable
public class CurrencyValue implements Serializable, Comparable<CurrencyValue> {
	private static final long serialVersionUID = -7883135732977736303L;

	@NotNull(message = "Negative value indicator cannot be null")
        @Column(insertable=false, updatable=false)
	private boolean isNegative;
	private int dollars;
	@Min(value = -99, message = "Cents cannot exceed the value of a dollar")
	@Max(value = 99, message = "Cents cannot exceed the value of a dollar")
	private int cents;

	/**
	 * The default constructor for CurrencyValue objects sets the isNegative flag to
	 * false and initializes the dollar and cent values to zero.
	 */
	public CurrencyValue() {
		this(false, 0, 0);
	}

	/**
	 * This parameterized constructor for CurrencyValue objects takes two integers, the
	 * first representing dollars and the second representing cents.  It initializes
	 * the isNegative flag to false.
	 *
	 * @param dollars int the integer representing the number of dollars
	 * @param cents   int the integer representing the number of cents
	 */
	public CurrencyValue(int dollars, int cents) {
		this(dollars < 0 || cents < 0, dollars, cents);
	}

	/**
	 * This is the copy constructor for CurrencyValue objects.  It makes a deep copy of
	 * the CurrencyValue object passed to the constructor as an argument.
	 *
	 * @param amount CurrencyValue the CurrencyValue to copy
	 */
	public CurrencyValue(CurrencyValue amount) {
		this(amount.isNegative, amount.dollars, amount.cents);
	}

	/**
	 * The fully parameterized constructor for CurrencyValue objects accepts three
	 * parameters: a boolean for the isNegative flag and two integers, one representing
	 * the dollar count and the other representing the cents count.  Although it accepts
	 * a boolean for the isNegative flag, it performs other checks and sets the flag of
	 * the resulting CurrencyValue object according to the final results of its evaluations.
	 * All other constructors for the CurrencyValue object call this constructor behind the
	 * scenes.
	 *
	 * @param isNegative    boolean the flag representing whether this value is negative
	 * @param dollarsAmount int the integer representing the number of dollars
	 * @param centsAmount   int the integer representing the number of cents
	 */
	public CurrencyValue(boolean isNegative, int dollarsAmount, int centsAmount) {
		if ((dollarsAmount < 0 || centsAmount < 0)) {
			isNegative = true;
		}

		if (isNegative) {
			dollarsAmount = Math.abs(dollarsAmount) * -1;
			centsAmount = Math.abs(centsAmount) * -1;
		}

		if (centsAmount < -99 || centsAmount > 99) {
			dollarsAmount += centsAmount / 100;
			centsAmount = centsAmount % 100;
		}

		if (centsAmount < 0 && dollarsAmount > 0) {
			dollarsAmount--;
			centsAmount += 100;
		} else if (dollarsAmount < 0 && centsAmount > 0) {
			dollarsAmount++;
			centsAmount -= 100;
		}

		this.setCents(centsAmount);
		this.setDollars(dollarsAmount);
	}

	/**
	 * This static method of the CurrencyValue class attempts to produce a new
	 * CurrencyValue object from a Double.  Although care is taken to produce an
	 * accurate representation of the value represented by the value passed, it
	 * is not always possible to eliminate the uncertainty of using a double
	 * due to the fact that doubles are inherently imprecise approximations of a
	 * specified value. For example, 2.9199999999999998 will evaluate to 2.92, even though
	 * 2.9199999999999997 evaluates to 2.91.  Apply extreme caution when using this method
	 * to generate new CurrencyValue objects.
	 *
	 * @param value Double the double precision floating point representation of a value
	 * @return CurrencyValue the CurrencyValue object representing the value of the value argument
	 */
	public static CurrencyValue valueOf(Double value) {
		int dollarsValue = (int) ((value * 100) / 100);
		int centsValue = (int) Math.ceil((value * 100) % 100);
		boolean isNegative = dollarsValue < 0 || centsValue < 0;
		return new CurrencyValue(isNegative, dollarsValue, centsValue);
	}

	/**
	 * This static method of the CurrencyValue class attempts to produce a new
	 * CurrencyValue object from a Float.  Although care is taken to produce an
	 * accurate representation of the value represented by the value passed, it
	 * is not always possible to eliminate the uncertainty of using a floating point
	 * number due to the fact that floating point numbers are inherently imprecise
	 * approximations of the specified value.  For example, 2.91999999 will evaluate
	 * to 2.92, even though 2.91999995 evaluates to 2.91.  Apply extreme caution when
	 * using this method to generate new CurrencyValue objects.
	 *
	 * @param value Float the floating point representation of a value
	 * @return CurrencyValue the CurrencyValue object representing the value of the value argument
	 */
	public static CurrencyValue valueOf(Float value) {
		int dollarsValue = (int) ((value * 100) / 100);
		int centsValue = (int) ((value * 100) % 100);
		boolean isNegative = dollarsValue < 0 || centsValue < 0;
		return new CurrencyValue(isNegative, dollarsValue, centsValue);
	}

	/**
	 * Getter for the isNegative flag.
	 *
	 * @return boolean the boolean value representing whether the value is negative
	 */
	public boolean isNegative() {
		return isNegative;
	}

	/**
	 * Setter for the isNegative flag.
	 *
	 * @param negative boolean the boolean value representing whether the value is negative
	 */
	public void setNegative(boolean negative) {
		isNegative = negative;
	}

	/**
	 * Getter for the dollar value of the object.
	 *
	 * @return int the integer value of the dollar count
	 */
	public int getDollars() {
		return this.dollars;
	}

	/**
	 * Setter for the dollar value of the object.
	 *
	 * @param dollars int the integer representing the dollar value
	 */
	public void setDollars(int dollars) {
		if (dollars < 0) {
			this.setNegative(true);
		}

		this.dollars = Math.abs(dollars);
	}

	/**
	 * Getter for the cent value of the object.
	 *
	 * @return int the integer representing the number of cents
	 */
	public int getCents() {
		return this.cents;
	}

	/**
	 * Setter for the cents value of the object.
	 *
	 * @param cents int the integer representing the number of cents
	 */
	public void setCents(int cents) {
		if (cents < 0) {
			this.setNegative(true);
		}

		this.cents = Math.abs(cents);
	}

	/**
	 * This overload of the add() method takes two integers, the first
	 * representing the number of dollars and the second representing the
	 * number of cents creates a new CurrencyValue object from those
	 * integers and combines the new CurrencyValue with this CurrencyValue
	 * object.
	 *
	 * @param dollarsAmount int the integer value representing the number of dollars
	 * @param centsAmount   int the integer value representing the number of cents
	 */
	public void add(int dollarsAmount, int centsAmount) {
		this.add(new CurrencyValue(dollarsAmount, centsAmount));
	}

	/**
	 * This overload of the add() method takes a single CurrencyValue object and
	 * combines its value with that of the CurrencyValue argument it was passed.
	 * Because of the way CurrencyValues handle positive and negative values, this
	 * method can be used for both addition and subtraction.  All other overloads of
	 * CurrencyValue arithmetic methods call this method behind the scenes.
	 *
	 * @param amount CurrencyValue the CurrencyValue representing the value to combine with the current value
	 */
	public void add(CurrencyValue amount) {
		int currentDollarsValue = this.isNegative ? this.getDollars() * -1 : this.getDollars();
		int currentCentsValue = this.isNegative ? this.getCents() * -1 : this.getCents();

		currentCentsValue += amount.isNegative ? amount.getCents() * -1 : amount.getCents();
		currentDollarsValue += amount.isNegative ? amount.getDollars() * -1 : amount.getDollars();

		if (currentCentsValue < -99 || currentCentsValue > 99) {
			currentDollarsValue += currentCentsValue / 100;
			currentCentsValue = currentCentsValue % 100;
		}

		if (currentCentsValue < 0 && currentDollarsValue > 0) {
			currentDollarsValue--;
			currentCentsValue += 100;
		} else if (currentDollarsValue < 0 && currentCentsValue > 0) {
			currentDollarsValue++;
			currentCentsValue -= 100;
		}

		this.setNegative(currentCentsValue < 0 || currentDollarsValue < 0);

		this.setCents(currentCentsValue);
		this.setDollars(currentDollarsValue);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		CurrencyValue that = (CurrencyValue) o;
		return isNegative == that.isNegative && dollars == that.dollars && cents == that.cents;
	}

	@Override
	public int hashCode() {
		return Objects.hash(isNegative, dollars, cents);
	}

	@Override
	public String toString() {
		return (((this.isNegative) ? "-" : "") + "$" + this.dollars + "." + ((this.cents < 10) ? "0" : "") + this.cents);
	}

	@Override
	public int compareTo(CurrencyValue o) {
		int returnValue = 0;

		if (this.isNegative() == o.isNegative()) {
			if (this.getDollars() != o.getDollars()) {
				returnValue = this.getDollars() > o.getDollars() ? 1 : -1;
			} else {
				returnValue = this.getCents() > o.getCents() ? 1 : -1;
			}

			if (this.isNegative()) {
				returnValue *= -1;
			}
		} else {
			returnValue = this.isNegative() ? -1 : 1;
		}

		return returnValue;
	}
}