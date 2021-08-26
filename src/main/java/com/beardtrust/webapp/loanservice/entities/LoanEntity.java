package com.beardtrust.webapp.loanservice.entities;

import java.time.LocalDate;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class LoanEntity {

    @Id
    private String loanId;
    private String userId;
    private LoanTypeEntity loanType;
    private Integer balance;
    private LocalDate createDate;
    private Integer principal;
    private Integer payDay;

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
        loanId = UUID.randomUUID().toString();
    }

    public LoanTypeEntity getLoanType() {
        return loanType;
    }

    public void setLoanType(LoanTypeEntity loanType) {
        this.loanType = loanType;
    }

    public Integer getBalance() {
        return balance;
    }

    public void setBalance(Integer balance) {
        this.balance = balance;
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

    public Integer getPayDay() {
        return payDay;
    }

    public void setPayDay(Integer payDay) {
        this.payDay = payDay;
    }
}
