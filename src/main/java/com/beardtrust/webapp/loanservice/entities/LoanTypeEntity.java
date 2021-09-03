package com.beardtrust.webapp.loanservice.entities;

import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="loanTypes")
public class LoanTypeEntity {

    @Id
    @Column(name="LOANTYPEID")
    private String loanTypeId;
    private String typeName;
    private String description;
    private Double apr;
    private Integer numMonths;
    @Column(name = "ACTIVESTATUS")
    private boolean activeStatus;

    public LoanTypeEntity() {
        super();
    }

    public LoanTypeEntity(String id, String typeName, String description, Double apr, Integer numMonths) {
        this.loanTypeId = UUID.randomUUID().toString();
        this.typeName = typeName;
        this.description = description;
        this.apr = apr;
        this.numMonths = numMonths;
    }
    
    public void generateId() {
        this.loanTypeId = UUID.randomUUID().toString();
    }

    public String getLoanTypeId() {
        return loanTypeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getApr() {
        return apr;
    }

    public void setApr(Double apr) {
        this.apr = apr;
    }

    public Integer getNumMonths() {
        return numMonths;
    }

    public void setNumMonths(Integer numMonths) {
        this.numMonths = numMonths;
    }

    public Boolean getActiveStatus() {
        return activeStatus;
    }

    public void setActiveStatus(Boolean activeStatus) {
        this.activeStatus = activeStatus;
    }

    @Override
    public String toString() {
        return "LoanTypeEntity{" +
                "typeName='" + typeName + '\'' +
                ", description='" + description + '\'' +
                ", apr=" + apr +
                ", numMonths=" + numMonths +
                '}';
    }
}
