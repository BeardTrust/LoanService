package com.beardtrust.webapp.loanservice.entities;

import java.time.LocalDate;
import java.util.UUID;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
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
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "cents", column = @Column(name = "dueCents")),
            @AttributeOverride(name = "dollars", column = @Column(name = "dueDollars")),
            @AttributeOverride(name = "isNegative", column = @Column(name = "dueIsNegative"))
    })
    private CurrencyValue minDue;
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "cents", column = @Column(name = "feeCents")),
            @AttributeOverride(name = "dollars", column = @Column(name = "feeDollars")),
            @AttributeOverride(name = "isNegative", column = @Column(name = "feeIsNegative"))
    })
    private CurrencyValue lateFee;
    private LocalDate nextDueDate;
    private LocalDate previousDueDate;
    private String minMonthFee;
    private boolean hasPaid;

    public void setValueString(String valueTitle) {
        this.minMonthFee = valueTitle;
    }

    public LoanEntity() {
        this.setCreateDate(LocalDate.now());
        this.setId(UUID.randomUUID().toString());
        this.minMonthFee = "0";
        this.loanType = new LoanTypeEntity();
        this.principal = new CurrencyValue();
        this.lateFee = new CurrencyValue(false, 0, 0);
        this.setBalance(new CurrencyValue());
        this.setHasPaid(false);
        calculateMinDue();
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
        return minMonthFee;
    }

    public void setValueTitle(String valueTitle) {
        this.minMonthFee = valueTitle;
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

    public CurrencyValue getMinDue() {
        return minDue;
    }

    public void setMinDue(CurrencyValue c) {
        this.minDue = c;
    }

    public CurrencyValue getLateFee() {
        return lateFee;
    }

    public void setLateFee(CurrencyValue lateFee) {
        this.lateFee = lateFee;
    }

    public String getMinMonthFee() {
        return minMonthFee;
    }

    public void setMinMonthFee() {
        this.minMonthFee = this.minDue.toString();
    }

    public void calculateMinDue() {
        Double temp = (double) this.getBalance().getDollars() + (double) (this.getBalance().getCents() / 100);
        temp /= this.loanType.getNumMonths();
        this.minDue = CurrencyValue.valueOf(temp);
        setMinMonthFee();
        minMonthFee = minDue.toString();
    }

    public void resetMinDue() {
        System.out.println("resetting minimum due...");
        this.minDue.valueOf(Double.parseDouble(minMonthFee));
        minDue.setNegative(false);
        if (this.minDue.compareTo(this.getBalance()) == 1) {
            System.out.println("minimum due more than balance...");
            minDue.setDollars(getBalance().getDollars());
            minDue.setCents(getBalance().getCents());
        }
        if (hasPaid) {
            System.out.println("Already paid, minimum due is 0...");
            minDue.setDollars(0);
            minDue.setCents(0);
        }
    }

    public boolean checkDate() {
        LocalDate l = LocalDate.now();
        if (nextDueDate.isAfter(l) && !hasPaid) {
            lateFee.add(20, 0);
            lateFee.setNegative(false);
            nextDueDate.plusDays(30);
            previousDueDate.plusDays(30);
            return true;
        } else if (nextDueDate.isAfter(l) && hasPaid) {
            nextDueDate.plusDays(30);
            previousDueDate.plusDays(30);
            return false;
        }
        return false;
    }

    public void initializeBalance() {
        int temp = this.getPrincipal().getDollars() + this.getPrincipal().getCents() / 100;
        Double temp2 = temp + (temp *  (this.getLoanType().getApr() / 100));
        String moneyString = temp2.toString();
        String[] values = moneyString.split("\\.");
        CurrencyValue c = new CurrencyValue();
        c.setDollars(Integer.parseInt(values[0]));
        c.setCents(Integer.parseInt(values[1]));
        this.setBalance(c);
    }

    /*
    Receives a CurrencyValue object representing the value to be paid towards the loan.
    Late fees will automatically take out a cut, any resulting amount will go towards
    the minimum due. If the minimum die is cleared, the hasPaid status will be set to true.
    If the amount paid is more than amount owed, the remaining amount will be returned to the
    payment source

     */

    public boolean isHasPaid() {
        return hasPaid;
    }

    public void setHasPaid(boolean hasPaid) {
        this.hasPaid = hasPaid;
    }

    @Override
    public String toString() {
        return "\nuser: " + this.getUser()
                + "\nitem id: " + this.getId()
                + "\nprincipal dollars: " + this.principal.getDollars()
                + "\nprincipal cents: " + this.principal.getCents()
                + "\nprincipal isNegative: " + this.principal.isNegative()
                + "\nAPR: " + this.loanType.getApr()
                + "\nbalance dollars: " + this.getBalance().getDollars()
                + "\nbalance cents: " + this.getBalance().getCents()
                + "\nbalance isNegative: " + this.getBalance().isNegative()
                + "\nloanType Id: " + this.loanType.getId()
                + "\nloanType typeName: " + this.loanType.getTypeName()
                + "\nloanType description: " + this.loanType.getDescription()
                + "\nloanType months remaining: " + this.loanType.getNumMonths()
                + "\ncreateDate: " + this.getCreateDate()
                + "\nnextDueDate: " + this.getNextDueDate()
                + "\npreviousDueDate: " + this.getPreviousDueDate()
                + "\nminMonthFee: " + this.getValueTitle();
    }

}
