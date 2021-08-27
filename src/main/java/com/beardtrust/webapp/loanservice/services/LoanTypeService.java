package com.beardtrust.webapp.loanservice.services;

import com.beardtrust.webapp.loanservice.entities.LoanTypeEntity;
import org.springframework.beans.factory.annotation.Autowired;

public interface LoanTypeService {

    public void save(LoanTypeEntity loanType);

    public List<LoanTypeEntity> getAll();
}
