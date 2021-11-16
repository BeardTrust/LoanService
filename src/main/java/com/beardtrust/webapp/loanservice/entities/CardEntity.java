package com.beardtrust.webapp.loanservice.entities;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.Objects;


/**
 * This class represents the card entities as found in the database.
 *
 * @author Davis Hill <Davis.Hill@Smoothstack.com>
 * @author Matthew Crowell <Matthew.Crowell@Smoothstack.com>
 */
@Entity
@Table(name = "cards")
public class CardEntity extends FinancialAsset {
	private static final long serialVersionUID = 1790351438616081041L;

	private String nickname;
	private double interestRate;
	@ManyToOne
	private CardTypeEntity cardType;
	private LocalDateTime expireDate;
	private int billCycleLength;
	private String cardNumber;
	@OneToOne
	private PaymentEntity payment;

	public CardEntity() {
		super();
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public double getInterestRate() {
		return interestRate;
	}

	public void setInterestRate(double interestRate) {
		this.interestRate = interestRate;
	}

	public CardTypeEntity getCardType() {
		return cardType;
	}

	public void setCardType(CardTypeEntity cardType) {
		this.cardType = cardType;
	}

	public LocalDateTime getExpireDate() {
		return expireDate;
	}

	public void setExpireDate(LocalDateTime expireDate) {
		this.expireDate = expireDate;
	}

	public int getBillCycleLength() {
		return billCycleLength;
	}

	public void setBillCycleLength(int billCycle) {
		this.billCycleLength = billCycle;
	}

	public String getCardNumber() {
		return cardNumber;
	}

	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

	public PaymentEntity getPayment() {
		return payment;
	}

	public void setPayment(PaymentEntity payment) {
		this.payment = payment;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		CardEntity that = (CardEntity) o;
		return Double.compare(that.getInterestRate(), getInterestRate()) == 0 && getBillCycleLength() == that.getBillCycleLength() && Objects.equals(getNickname(), that.getNickname()) && Objects.equals(getCardType(), that.getCardType()) && Objects.equals(getExpireDate(), that.getExpireDate()) && Objects.equals(getCardNumber(), that.getCardNumber()) && Objects.equals(getPayment(), that.getPayment());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getNickname(), getInterestRate(), getCardType(), getExpireDate(), getBillCycleLength(), getCardNumber(), getPayment());
	}

	@Override
	public String toString() {
		return "CardEntity{" +
				"nickname='" + nickname + '\'' +
				", interestRate=" + interestRate +
				", cardType=" + cardType +
				", expireDate=" + expireDate +
				", billCycleLength=" + billCycleLength +
				", cardNumber='" + cardNumber + '\'' +
				", payment=" + payment +
				'}';
	}
}