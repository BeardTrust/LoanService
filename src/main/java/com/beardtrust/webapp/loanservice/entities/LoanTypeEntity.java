package com.beardtrust.webapp.loanservice.entities;

import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="loan_types")
public class LoanTypeEntity {

    @Id
    @Column(name = "id")
    private String id;
    private String typeName;
    @Column(name = "description")
    private String description;
    private Double apr;
    private Integer numMonths;
    private boolean activeStatus;

    public LoanTypeEntity() {
        this.id = UUID.randomUUID().toString();
        this.typeName = "null";
        this.description = "null";
        this.apr = 0.0;
        this.numMonths = 12;
    }
    
    public void generateId() {
        this.id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
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
