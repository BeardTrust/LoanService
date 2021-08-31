package com.beardtrust.webapp.loanservice.services;

import com.beardtrust.webapp.loanservice.entities.LoanTypeEntity;

import java.util.List;
import org.springframework.data.domain.Page;

public interface LoanTypeService {

    public void save(LoanTypeEntity loanType);
    
    public Page<LoanTypeEntity> getAllLoanTypesPage(int pn, int ps, String[] sb, String s);

    public List<LoanTypeEntity> getAll();

    public void deactivate(LoanTypeEntity loanType);
}
