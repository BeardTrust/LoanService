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
public class LoanEntity extends FinancialAsset {

    @ManyToOne
    private LoanTypeEntity loanType;
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "cents", column = @Column(name = "principalCents")),
        @AttributeOverride(name = "dollars", column = @Column(name = "principalDollars")),
        @AttributeOverride(name = "isNegative", column = @Column(name = "principalIsNegative"))
    })
    private CurrencyValue principal;
    private LocalDate nextDueDate;
    private LocalDate previousDueDate;
    private String valueTitle;

    public void setValueString(String valueTitle) {
        this.valueTitle = getBalance().toString();
    }

    public LoanEntity() {
        System.out.println("building loan...");
        this.valueTitle = "0";
        this.loanType = new LoanTypeEntity();
        this.principal = new CurrencyValue();
        this.setBalance(new CurrencyValue());
        this.nextDueDate = LocalDate.now().plusDays(30);
        this.previousDueDate = LocalDate.now().minusDays(30);
    }

    public LoanTypeEntity getLoanType() {
        return loanType;
    }

    public void setLoanType(LoanTypeEntity loanType) {
        this.loanType = loanType;
    }

    public CurrencyValue getPrincipal() {
        return principal;
    }

    public void setPrincipal(CurrencyValue principal) {
        this.principal = principal;
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
        return "\nuser: " + this.getUser() +
                "\nprincipal dollars: " + this.principal.getDollars()
                + "\nprincipal cents: " + this.principal.getCents()
                + "\nprincipal isNegative: " + this.principal.isNegative()
                + "\nAPR: " + this.loanType.getApr()
                + "\nbalance dollars: " + this.getBalance().getDollars()
                + "\nbalance cents: " + this.getBalance().getCents()
                + "\nbalance isNegative: " + this.getBalance().isNegative()
                + "\nloanType Id: " + this.loanType.getId()
                + "\nloanType typeName: " + this.loanType.getTypeName()
                + "\nloanType description: " + this.loanType.getDescription()
                + "\ncreateDate: " + this.getCreateDate()
                + "\nnextDueDate: " + this.getNextDueDate()
                + "\npreviousDueDate: " + this.getPreviousDueDate()
                + "\nvalueTitle: " + this.getValueTitle();
    }

}
