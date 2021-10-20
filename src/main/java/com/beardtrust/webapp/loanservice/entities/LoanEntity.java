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
        System.out.println("building loan...");
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

    public void setMinMonthFee(String minMonthFee) {
        this.minMonthFee = minMonthFee;
    }

    public void calculateMinDue() {
        System.out.println("parsing balance: " + this.getBalance().toString());
        Double temp = (double) this.getBalance().getDollars() + (double) (this.getBalance().getCents() / 100);
        temp /= this.loanType.getNumMonths();
        System.out.println("min due temp value set: " + (int) Math.ceil(temp));
        System.out.println("min due temp value parsed: " + CurrencyValue.valueOf(temp));
        this.minDue = CurrencyValue.valueOf(temp);
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

    public void checkDate() {
        LocalDate l = LocalDate.now();
        if (nextDueDate.isAfter(l) && !hasPaid) {
            lateFee.add(20, 0);
            lateFee.setNegative(false);
            nextDueDate.plusDays(30);
            previousDueDate.plusDays(30);
        } else if (nextDueDate.isAfter(l) && hasPaid) {
            nextDueDate.plusDays(30);
            previousDueDate.plusDays(30);
        }
    }

    /*
    Receives a CurrencyValue object representing the value to be paid towards the loan.
    Late fees will automatically take out a cut, any resulting amount will go towards
    the minimum due. If the minimum die is cleared, the hasPaid status will be set to true.
    If the amount paid is more than amount owed, the remaining amount will be returned to the
    payment source

     */
    public CurrencyValue makePayment(CurrencyValue payment) {
        minDue.setNegative(false);
        CurrencyValue temp;
        System.out.println("Incoming payment: " + payment.toString());
        if (lateFee.getDollars() > 0 || lateFee.getCents() > 0) {
            System.out.println("latefee to pay on: " + lateFee);
             temp = lateFee;
//            lateFee.add(payment);
            payment.add(temp);
            System.out.println("payment after late fee: " + payment);
        }
        payment.setNegative(false);
        if (payment.compareTo(this.getBalance()) == 1) {//paynment is greater
            payment.setNegative(true);
            System.out.println("paying more than balance..." + payment);
            System.out.println("balance: " + this.getBalance());
            minDue.add(payment);
            this.getBalance().add(payment);
            System.out.println("balance added to payment: " + this.getBalance());
            payment.setDollars(this.getBalance().getDollars());
            payment.setCents(this.getBalance().getCents());
            payment.setNegative(false);
            this.getBalance().setDollars(0);
            this.getBalance().setCents(0);
            getBalance().setNegative(false);
            minDue.setDollars(0);
            minDue.setCents(0);
            payment.setNegative(false);
            System.out.println("resulting balance: " + this.getBalance());
            System.out.println("remaining payment: " + payment);
            hasPaid = true;
            return payment;
        } else {
            payment.setNegative(true);
            System.out.println("balance paid on: " + this.getBalance());
            System.out.println("amount paying: " + payment);
            minDue.add(payment);
            this.getBalance().add(payment);
            System.out.println("minimum payment due: " + minDue);
            System.out.println("balance after payment: " + this.getBalance());
        }
        int temp2 = minDue.getDollars() + minDue.getCents();
        if (temp2 <= 0) {
            minDue.setDollars(0);
            minDue.setCents(0);
            minDue.setNegative(false);
            hasPaid = true;
            checkDate();
        }
        System.out.println("Loan haspaid status: " + hasPaid);
    return payment;
    }

    public boolean isHasPaid() {
        return hasPaid;
    }

    public void setHasPaid(boolean hasPaid) {
        this.hasPaid = hasPaid;
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
