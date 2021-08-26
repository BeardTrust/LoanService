package com.beardtrust.webapp.loanservice.entities;

import javax.persistence.Embeddable;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.Objects;

@Embeddable
public class Balance {
	private int dollars;
	@Min(value = -99, message = "Cents cannot exceed the value of a dollar")
	@Max(value = 99, message = "Cents cannot exceed the value of a dollar")
	private int cents;

	/**
	 * Instantiates a new Balance.
	 */
	public Balance() {
		this(0, 0);
	}

	public Balance(int cents){
		this(0, cents);
	}

	/**
	 * Instantiates a new Balance.
	 *
	 * @param dollars int the dollars
	 * @param cents   int the cents
	 */
	public Balance(int dollars, int cents) {
		this.dollars = dollars;
		this.cents = cents;
	}

	/**
	 * Instantiates a new Balance.
	 *
	 * @param other Balance the balance with the desired value
	 */
	public Balance(Balance other){
		this(other.getDollars(), other.getCents());
	}

	/**
	 * Gets dollars.
	 *
	 * @return int the dollars
	 */
	public int getDollars() {
		return dollars;
	}

	/**
	 * Sets dollars.
	 *
	 * @param dollars int the dollars
	 */
	public void setDollars(int dollars) {
		this.dollars = dollars;
	}

	/**
	 * Gets cents.
	 *
	 * @return int the cents
	 */
	public int getCents() {
		return cents;
	}

	/**
	 * Sets cents.
	 *
	 * @param cents int the cents
	 */
	public void setCents(int cents) {
		this.cents = cents;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Balance balance = (Balance) o;
		return dollars == balance.dollars && cents == balance.cents;
	}

	@Override
	public int hashCode() {
		return Objects.hash(dollars, cents);
	}

	@Override
	public String toString() {
		return dollars + "." + cents;
	}

	/**
	 * This method adds the specified number of dollars and cents to the balance.
	 *
	 * @param dollars int the dollars
	 * @param cents   int the cents
	 * @return String the string representation of the balance
	 */
	public String add(int dollars, int cents) {
		StringBuilder returnValue = null;

		if (dollars < 0 || cents < 0) {
			returnValue = new StringBuilder();
			returnValue.append("Invalid operand values for addition");
		}

		if (returnValue == null) {
			this.dollars += dollars;
			this.cents += cents;
			if (this.cents > 100) {
				int tempCents = this.cents % 100;
				this.dollars += (this.cents - tempCents) / 100;
				this.cents = tempCents;
			}
			returnValue = new StringBuilder(this.dollars + "." + (this.cents < 10 ? "0" + this.cents : this.cents));
		}

		return returnValue.toString();
	}

	/**
	 * This method adds the specified number of cents to the balance
	 *
	 * @param cents Integer the cents to add
	 * @return String the string
	 */
	public String add(Integer cents){
		return this.add(0, cents);
	}

	/**
	 * This method adds the specified balance to this balance.
	 *
	 * @param other Balance the other balance
	 * @return String the string
	 */
	public String add(Balance other){
		return this.add(other.getDollars(), other.getCents());
	}

	/**
	 * This method subtracts the specified dollars and cents from the
	 * balance.
	 *
	 * @param dollars int the dollars
	 * @param cents   int the cents
	 * @return the string
	 */
	public String subtract(int dollars, int cents) {
		if (dollars < 0) {
			dollars *= -1;
		}

		if (cents < 0) {
			cents *= -1;
		}

		this.dollars -= dollars;
		this.cents -= cents;
		if (this.cents < 0) {
			int tempCents = this.cents % 100;
			int tempDollars = ((this.cents - tempCents) / 100) * -1;
			this.dollars -= tempDollars;
			this.cents = tempCents;
		}

		return this.dollars + "." + (this.cents < 10 ? "0" + this.cents : this.cents);
	}

	/**
	 * This method subtracts the specified number of cents from the balance
	 *
	 * @param cents Integer the cents to subtract
	 * @return String the string
	 */
	public String subtract(Integer cents){
		return this.subtract(0, cents);
	}

	/**
	 * This method subtracts the specified balance to this balance.
	 *
	 * @param other Balance the other balance
	 * @return String the string
	 */
	public String subtract(Balance other){
		return this.subtract(other.getDollars(), other.getCents());
	}
}