package com.beardtrust.webapp.loanservice.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

/**
 * This class represents a Transaction status.
 *
 * @author Matthew Crowell <Matthew.Crowell@Smoothstack.com>
 */
@Entity
@Table(name = "transaction_statuses")
public class TransactionStatus {
	@Id
	private int statusId;
	@Column(unique = true)
	private String statusName;
	private String statusDescription;
	@JsonIgnore
	@OneToMany(mappedBy = "transactionStatus")
	private Set<FinancialTransaction> transaction;

	/**
	 * Instantiates a new Transaction status.
	 */
	public TransactionStatus() {
	}

	/**
	 * Instantiates a new Transaction status.
	 *
	 * @param statusId          int the status id
	 * @param statusName        String the status name
	 * @param statusDescription String the status description
	 */
	public TransactionStatus(int statusId, String statusName, String statusDescription) {
		this.statusId = statusId;
		this.statusName = statusName;
		this.statusDescription = statusDescription;
	}

	/**
	 * Gets status id.
	 *
	 * @return int the status id
	 */
	public int getStatusId() {
		return statusId;
	}

	/**
	 * Sets status id.
	 *
	 * @param statusId int the status id
	 */
	public void setStatusId(int statusId) {
		this.statusId = statusId;
	}

	/**
	 * Gets status name.
	 *
	 * @return String the status name
	 */
	public String getStatusName() {
		return statusName;
	}

	/**
	 * Sets status name.
	 *
	 * @param statusName String the status name
	 */
	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}

	/**
	 * Gets status description.
	 *
	 * @return String the status description
	 */
	public String getStatusDescription() {
		return statusDescription;
	}

	/**
	 * Sets status description.
	 *
	 * @param statusDescription String the status description
	 */
	public void setStatusDescription(String statusDescription) {
		this.statusDescription = statusDescription;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		TransactionStatus that = (TransactionStatus) o;
		return statusId == that.statusId && Objects.equals(statusName, that.statusName) && Objects.equals(statusDescription, that.statusDescription);
	}

	@Override
	public int hashCode() {
		return Objects.hash(statusId, statusName, statusDescription);
	}

	@Override
	public String toString() {
		return this.statusName;
	}
}
