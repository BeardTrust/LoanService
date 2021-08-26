package com.beardtrust.webapp.loanservice.services;

import com.beardtrust.webapp.loanservice.entities.LoanTypeEntity;
import com.beardtrust.webapp.loanservice.repos.LoanTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public class LoanTypeServiceImpl implements LoanTypeService{

    @Autowired
    LoanTypeRepository loanTypeRepo;

    @Override
    @Transactional
    public void save(LoanTypeEntity loanType){
        loanTypeRepo.save(loanType);
    }
}
