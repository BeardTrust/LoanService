package com.beardtrust.webapp.loanservice.entities;

import com.beardtrust.webapp.loanservice.repos.LoanTypeRepository;
import java.time.LocalDate;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="loans")
public class LoanEntity {

    @Id
    @Column(unique = true)
    private final String loanId;
    private String userId;
    @ManyToOne
    private LoanTypeEntity loanType;
    @Embedded
    private CurrencyValue currencyValue;
    private LocalDate createDate;
    private Integer principal;
    private LocalDate nextDueDate;
    private LocalDate previousDueDate;
    private String valueTitle;

    public String getValueString() {
        setValueString(valueTitle);
        return valueTitle;
    }

    public void setValueString(String valueTitle) {
        this.valueTitle = currencyValue.toString();
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

    public CurrencyValue getCurrencyValue() {
        return currencyValue;
    }

    public void setCurrencyValue(CurrencyValue currencyValue) {
        this.currencyValue = currencyValue;
    }

    public LocalDate getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDate createDate) {
        this.createDate = createDate;
    }

    public Integer getPrincipal() {
        return principal;
    }

    public void setPrincipal(Integer principal) {
        this.principal = principal;
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

}