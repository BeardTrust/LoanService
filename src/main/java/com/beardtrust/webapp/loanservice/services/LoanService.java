package com.beardtrust.webapp.loanservice.services;

import java.util.List;

import com.beardtrust.webapp.loanservice.entities.LoanEntity;

public interface LoanService {
	
	public List<LoanEntity> getAll();
	
	public LoanEntity getById();
	
	public void deleteById();
	
	public void save();

}
