package com.beardtrust.webapp.loanservice.services;

import com.beardtrust.webapp.loanservice.entities.LoanTypeEntity;

import java.util.List;

public interface LoanTypeService {

    public void save(LoanTypeEntity loanType);

    public List<LoanTypeEntity> getAll();
}
