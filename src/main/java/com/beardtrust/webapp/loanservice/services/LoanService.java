package com.beardtrust.webapp.loanservice.services;

import java.util.List;

import com.beardtrust.webapp.loanservice.entities.LoanEntity;
import org.springframework.data.domain.Page;

public interface LoanService {
	
	public Page<LoanEntity> getAllLoansPage(Integer n, Integer s, String sortName, String sortDir, String search);
        
        public List<LoanEntity> getAllLoans();
	
	public LoanEntity getById(String id);
	
	public String deleteById(String id);
	
	public LoanEntity save(LoanEntity l);
        
        public String updateLoan(LoanEntity l);

}
