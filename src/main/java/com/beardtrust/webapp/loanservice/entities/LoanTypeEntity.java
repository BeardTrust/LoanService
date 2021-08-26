package com.beardtrust.webapp.loanservice.entities;

public class LoanTypeEntity {
    public String name;
    public String billCycle;
    public Integer apr;
    public Integer length;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBillCycle() {
        return billCycle;
    }

    public void setBillCycle(String billCycle) {
        this.billCycle = billCycle;
    }

    public Integer getApr() {
        return apr;
    }

    public void setApr(Integer apr) {
        this.apr = apr;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }
}