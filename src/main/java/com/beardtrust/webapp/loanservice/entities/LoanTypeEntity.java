package com.beardtrust.webapp.loanservice.entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="loan_types")
public class LoanTypeEntity {

    @Id
    private String id;
    private String typeName;
    private String description;
    private Double apr;
    private Integer numMonths;
    private Boolean isAvailable;

    public LoanTypeEntity() {
        super();
    }

    public LoanTypeEntity(String id, String typeName, String description, Double apr, Integer numMonths) {
        this.id = id;
        this.typeName = typeName;
        this.description = description;
        this.apr = apr;
        this.numMonths = numMonths;
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

    public Boolean getAvailable() {
        return isAvailable;
    }

    public void setAvailable(Boolean available) {
        isAvailable = available;
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
