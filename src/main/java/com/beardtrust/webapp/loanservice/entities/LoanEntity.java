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
        @AttributeOverride(name = "cents", column = @Column(name = "principal_cents")),
        @AttributeOverride(name = "dollars", column = @Column(name = "principal_dollars")),
        @AttributeOverride(name = "is_negative", column = @Column(name = "principal_negative", insertable=false, nullable=false, updatable=false))
    })
    private CurrencyValue principal;
    @Embedded
    private LocalDate createDate;
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "cents", column = @Column(name = "balance_cents")),
        @AttributeOverride(name = "dollars", column = @Column(name = "balance_dollars")),
        @AttributeOverride(name = "is_negative", column = @Column(name = "balance_negative", insertable=false, nullable=false, updatable=false))
    })
    private CurrencyValue balance;
    private LocalDate nextDueDate;
    private LocalDate previousDueDate;
    private String valueTitle;

    public String getValueString() {
        setValueString(valueTitle);
        return valueTitle;
    }

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

//    public void calculateBalance() {
//        String temp = principal.toString();
//        System.out.println("Creating balance...");
//        System.out.println("Principal: " + principal.toString());
//        Double b = Double.parseDouble(temp);
//        b = b * loanType.getApr();
//        System.out.println("APR: " + loanType.getApr());
//        temp = String.valueOf(b);
//        System.out.println("Calculated balance: " + temp);
//        String[] nums = temp.split(".");
//        balance.setDollars(Integer.valueOf(nums[0]));
//        balance.setCents(Integer.valueOf(nums[1]));
//    }
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

}
