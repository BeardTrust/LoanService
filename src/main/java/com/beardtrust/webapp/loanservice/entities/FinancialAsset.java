package com.beardtrust.webapp.loanservice.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Set;

/**
 * This class is the abstract base class for all financial assets managed by
 * or interacting directly with assets managed by BeardTrust.  It has a UUID-
 * based id field, a reference to the owning user entity, a boolean representing
 * whether the financial asset is active, a balance consisting of a boolean flag
 * indicating whether the amount is positive or negative, an integer for the
 * number of dollars in the balance, and an integer for the number of cents in
 * the balance, and a timestamp issued when it was created.  Financial assets
 * are compared by their current balances.
 *
 * @author Matthew Crowell <Matthew.Crowell@Smoothstack.com>
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class FinancialAsset implements Comparable<FinancialAsset>, Serializable {
	private static final long serialVersionUID = -1059628569081235804L;

	@Id
	private String id;
	@ManyToOne
	@JoinColumn(name = "user_id")
	private UserEntity user;
	@Embedded
	@AttributeOverrides({
			@AttributeOverride(name = "cents", column = @Column(name = "balanceCents")),
			@AttributeOverride(name = "dollars", column = @Column(name = "balanceDollars")),
			@AttributeOverride(name = "isNegative", column = @Column(name = "balanceIsNegative"))
	})
	private CurrencyValue balance;
	private LocalDate createDate;


	/**
	 * Gets id.
	 *
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets id.
	 *
	 * @param id the id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Gets user.
	 *
	 * @return the user
	 */
	public UserEntity getUser() {
		return user;
	}

	/**
	 * Sets user.
	 *
	 * @param user the user
	 */
	public void setUser(UserEntity user) {
		this.user = user;
	}

	/**
	 * Gets balance.
	 *
	 * @return the balance
	 */
	public CurrencyValue getBalance() {
		return balance;
	}

	/**
	 * Sets balance.
	 *
	 * @param balance the balance
	 */
	public void setBalance(CurrencyValue balance) {
		this.balance = balance;
	}

	/**
	 * Gets create time.
	 *
	 * @return the create time
	 */
	public LocalDate getCreateDate() {
		return createDate;
	}

	/**
	 * Sets create time.
	 *
	 * @param createDate the create time
	 */
	public void setCreateDate(LocalDate createDate) {
		this.createDate = createDate;
	}

	@Override
	public int compareTo(FinancialAsset o) {
		return this.balance.compareTo(o.balance);
	}

	@Override
	public String toString() {
		return "FinancialAsset{" +
				"id='" + id + '\'' +
				", user=" + user +
				", balance=" + balance +
				", createDate=" + createDate +
				'}';
	}
}
