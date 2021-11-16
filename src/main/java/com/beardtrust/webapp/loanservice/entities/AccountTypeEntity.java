package com.beardtrust.webapp.loanservice.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * This class represents account types.  It possesses a UUID-based id
 * a string for a name, a string for a description, a boolean flag to
 * indicate whether the account type is active or not, a timestamp for
 * when the account type was created, and expiry date to track when the
 * account type was discontinued, and a set of all accounts which are
 * associated with that account type.
 *
 * @author Matthew Crowell <Matthew.Crowell@Smoothstack.com>
 */
@Entity
@Table(name = "account_types")
public class AccountTypeEntity implements Serializable, Comparable<AccountTypeEntity> {
	private static final long serialVersionUID = -412935674305166635L;

	@Id
	private String id;
	private String name;
	private String description;
	private boolean isActive;
	private LocalDateTime createdDate;
	private LocalDateTime expireDate;
	@JsonIgnore
	@OneToMany(mappedBy = "type")
	private Set<AccountEntity> accounts;

	public AccountTypeEntity() {
	}

	public AccountTypeEntity(String name, String description, boolean isActive, LocalDateTime createdDate, LocalDateTime expireDate) {
		this.id = UUID.randomUUID().toString();
		this.name = name;
		this.description = description;
		this.isActive = isActive;
		this.createdDate = createdDate;
		this.expireDate = expireDate;
		this.accounts = new HashSet<AccountEntity>();
	}

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
	 * Gets name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets name.
	 *
	 * @param name the name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets description.
	 *
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets description.
	 *
	 * @param description the description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Is active boolean.
	 *
	 * @return the boolean
	 */
	public boolean isActive() {
		return isActive;
	}

	/**
	 * Sets active.
	 *
	 * @param active the active
	 */
	public void setActive(boolean active) {
		isActive = active;
	}

	/**
	 * Gets created date.
	 *
	 * @return the created date
	 */
	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	/**
	 * Sets created date.
	 *
	 * @param createdDate the created date
	 */
	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

	/**
	 * Gets expired date.
	 *
	 * @return the expired date
	 */
	public LocalDateTime getExpireDate() {
		return expireDate;
	}

	/**
	 * Sets expired date.
	 *
	 * @param expiredDate the expired date
	 */
	public void setExpireDate(LocalDateTime expiredDate) {
		this.expireDate = expiredDate;
	}

	/**
	 * Gets accounts.
	 *
	 * @return the accounts
	 */
	public Set<AccountEntity> getAccounts() {
		return accounts;
	}

	/**
	 * Sets accounts.
	 *
	 * @param accounts the accounts
	 */
	public void setAccounts(Set<AccountEntity> accounts) {
		this.accounts = accounts;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		AccountTypeEntity that = (AccountTypeEntity) o;
		return isActive == that.isActive && Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(description, that.description) && Objects.equals(createdDate, that.createdDate) && Objects.equals(expireDate, that.expireDate) && Objects.equals(accounts, that.accounts);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name, description, isActive, createdDate, expireDate, accounts);
	}

	@Override
	public String toString() {
		return this.name + ": " + this.description;
	}

	@Override
	public int compareTo(AccountTypeEntity o) {
		return this.createdDate.compareTo(o.createdDate);
	}
}
