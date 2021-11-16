package com.beardtrust.webapp.loanservice.entities;


import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payments")
public class PaymentEntity implements Serializable {
	private static final long serialVersionUID = 4105327858219269106L;

	@Id
	private String id;
	@SuppressWarnings({"JpaModelReferenceInspection", "JpaDataSourceORMInspection"})
	@Embedded
	@AttributeOverrides({
			@AttributeOverride(name = "cents", column = @Column(name = "dueCents")),
			@AttributeOverride(name = "dollars", column = @Column(name = "dueDollars")),
			@AttributeOverride(name = "isNegative", column = @Column(name = "dueIsNegative"))
	})
	private CurrencyValue minDue;
	@SuppressWarnings({"JpaModelReferenceInspection", "JpaDataSourceORMInspection"})
	@Embedded
	@AttributeOverrides({
			@AttributeOverride(name = "cents", column = @Column(name = "lateCents")),
			@AttributeOverride(name = "dollars", column = @Column(name = "lateDollars")),
			@AttributeOverride(name = "isNegative", column = @Column(name = "lateIsNegative"))
	})
	private CurrencyValue lateFee;
	private LocalDateTime  nextDueDate;
	private LocalDateTime previousDueDate;
	public boolean hasPaid;
	public String minMonthFee;
	@JsonBackReference
	@OneToOne
	private FinancialAsset associatedAsset;

	public PaymentEntity() {
		this.setId(UUID.randomUUID().toString());
	}

	public CurrencyValue getLateFee() {
		return lateFee;
	}

	public CurrencyValue getMinDue() {
		return minDue;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setMinDue(CurrencyValue minDue) {
		this.minDue = minDue;
	}

	public void setLateFee(CurrencyValue lateFee) {
		this.lateFee = lateFee;
	}

	public LocalDateTime getNextDueDate() {
		return nextDueDate;
	}

	public void setNextDueDate(LocalDateTime nextDueDate) {
		this.nextDueDate = nextDueDate;
	}

	public LocalDateTime getPreviousDueDate() {
		return previousDueDate;
	}

	public void setPreviousDueDate(LocalDateTime previousDueDate) {
		this.previousDueDate = previousDueDate;
	}

	public boolean isHasPaid() {
		return hasPaid;
	}

	public void setHasPaid(boolean hasPaid) {
		this.hasPaid = hasPaid;
	}

	public String getMinMonthFee() {
		return minMonthFee;
	}

	public void setMinMonthFee(String minMonthFee) {
		this.minMonthFee = minMonthFee;
	}

	public void incrementDueDate() {
		this.previousDueDate = this.nextDueDate;
		this.nextDueDate = this.nextDueDate.plusDays(30);
	}

	public boolean checkLate() {
		boolean returnValue = false;

		LocalDateTime l = LocalDateTime.now();

		if (nextDueDate.isAfter(l) && !hasPaid) {
			lateFee.add(20, 0);
			lateFee.add(minDue.getDollars(), minDue.getCents());
			lateFee.setNegative(false);
			incrementDueDate();
			returnValue = true;
		} else if (nextDueDate.isAfter(l) && hasPaid) {
			incrementDueDate();
		}
		return returnValue;
	}

	@Override
	public String toString() {
		return "payment id: " + this.getId()
				+ "\ncurrent minimum due: " + this.minDue.toString()
				+"\nnormal minimum due: " + this.getMinMonthFee()
				+ "\nlate fee: " + this.lateFee.toString()
				+ "\nnext due date: " + this.getNextDueDate()
				+ "\nprevious due date: " + this.getPreviousDueDate()
				+ "\npaid status: " + this.isHasPaid();
	}
}