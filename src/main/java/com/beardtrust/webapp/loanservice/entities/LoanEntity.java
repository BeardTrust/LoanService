package com.beardtrust.webapp.loanservice.entities;

import com.beardtrust.webapp.loanservice.repos.LoanTypeRepository;
import java.time.LocalDate;
import java.util.UUID;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "loans")
public class LoanEntity {

    @Id
    @Column(unique = true)
    private final String loanId;
    private String userId;
    @ManyToOne
    private LoanTypeEntity loanType;
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "cents", column = @Column(name = "principalCents")),
        @AttributeOverride(name = "dollars", column = @Column(name = "principalDollars")),
        @AttributeOverride(name = "isNegative", column = @Column(name = "principalIsNegative"))
    })
    private CurrencyValue principal;
    private LocalDate createDate;
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "cents", column = @Column(name = "balanceCents")),
        @AttributeOverride(name = "dollars", column = @Column(name = "balanceDollars")),
        @AttributeOverride(name = "isNegative", column = @Column(name = "balanceIsNegative"))
    })
    private CurrencyValue balance;
    private LocalDate nextDueDate;
    private LocalDate previousDueDate;
    private String valueTitle;

    public void setValueString(String valueTitle) {
        this.valueTitle = balance.toString();
    }

    public String getLoanId() {
        return loanId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public LoanEntity() {
        System.out.println("building loan...");
        loanId = UUID.randomUUID().toString();
        this.valueTitle = "0";
        this.createDate = LocalDate.now();
        this.nextDueDate = createDate.plusDays(30);
        this.previousDueDate = createDate.minusDays(30);
    }

    public LoanTypeEntity getLoanType() {
        return loanType;
    }

    public void setLoanType(LoanTypeEntity loanType) {
        this.loanType = loanType;
    }

    public LocalDate getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDate createDate) {
        this.createDate = createDate;
    }

    public CurrencyValue getPrincipal() {
        return principal;
    }

    public void setPrincipal(CurrencyValue principal) {
        this.principal = principal;
    }

    public CurrencyValue getBalance() {
        return balance;
    }

    public void setBalance(CurrencyValue currencyValue) {
        this.balance = currencyValue;
    }

    public String getValueTitle() {
        return valueTitle;
    }

    public void setValueTitle(String valueTitle) {
        this.valueTitle = valueTitle;
    }

    public LocalDate getNextDueDate() {
        return nextDueDate;
    }

    public void setNextDueDate(LocalDate nextDueDate) {
        this.nextDueDate = nextDueDate;
    }

    public void incrementDueDate() {
        this.previousDueDate = this.nextDueDate;
        this.nextDueDate = this.nextDueDate.plusDays(30);
    }

    public LocalDate getPreviousDueDate() {
        return previousDueDate;
    }

    public void setPreviousDueDate(LocalDate previousDueDate) {
        this.previousDueDate = previousDueDate;
    }

    @Override
    public String toString() {
        return "\nloan Id: " + this.loanId
                + "\nuser Id: " + this.userId
                + "\nprincipal dollars: " + this.principal.getDollars()
                + "\nprincipal cents: " + this.principal.getCents()
                + "\nprincipal isNegative: " + this.principal.isNegative()
                + "\nAPR: " + this.loanType.getApr()
                + "\nbalance dollars: " + this.balance.getDollars()
                + "\nbalance cents: " + this.balance.getCents()
                + "\nbalance isNegative: " + this.balance.isNegative()
                + "\nloanType Id: " + this.loanType.getId()
                + "\nloanType typeName: " + this.loanType.getTypeName()
                + "\nloanType description: " + this.loanType.getDescription()
                + "\ncreateDate: " + this.getCreateDate()
                + "\nnextDueDate: " + this.getNextDueDate()
                + "\npreviousDueDate: " + this.getPreviousDueDate()
                + "\nvalueTitle: " + this.getValueTitle();
    }

}
