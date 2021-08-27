package com.beardtrust.webapp.loanservice.services;

import com.beardtrust.webapp.loanservice.dtos.UserDTO;

public interface AuthorizationService {
	
	UserDTO getUserByUserId(String id);
}
