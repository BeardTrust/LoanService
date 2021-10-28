package com.beardtrust.webapp.loanservice.services;

import java.util.List;

import com.beardtrust.webapp.loanservice.entities.CurrencyValue;
import com.beardtrust.webapp.loanservice.entities.LoanEntity;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public interface LoanService {
	
	public Page<LoanEntity> getAllLoansPage(Integer n, Integer s, String sortName, String sortDir, String search);
        
        public Page<LoanEntity> getAllMyLoansPage(int pn, int ps, String[] sb, String s, String userId);
        
        public List<LoanEntity> getAllLoans();
	
	public LoanEntity getById(String id);
	
	public String deleteById(String id);
	
	public LoanEntity save(LoanEntity l);
        
        public String updateLoan(LoanEntity l);
        
        public LoanEntity getNewLoan(String id);

        public void calculateMinDue();

        public CurrencyValue makePayment(CurrencyValue c, String id);

}
