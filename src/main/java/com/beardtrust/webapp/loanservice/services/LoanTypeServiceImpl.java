package com.beardtrust.webapp.loanservice.services;

import com.beardtrust.webapp.loanservice.entities.LoanTypeEntity;
import com.beardtrust.webapp.loanservice.repos.LoanTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LoanTypeServiceImpl implements LoanTypeService{

    @Autowired
    LoanTypeRepository loanTypeRepo;

    @Override
    @Transactional
    public void save(LoanTypeEntity loanType){
        loanTypeRepo.save(loanType);
    }

    @Override
    public List<LoanTypeEntity> getAll() {
        return loanTypeRepo.findAll();
    }

    @Override
    public void deactivate(LoanTypeEntity loanType){
        loanTypeRepo.deactivateById(loanType.getId());
    }

}
