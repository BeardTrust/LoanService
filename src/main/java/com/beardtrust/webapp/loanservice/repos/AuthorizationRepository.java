package com.beardtrust.webapp.loanservice.repos;

import com.beardtrust.webapp.loanservice.entities.LoanEntity;
import org.springframework.data.repository.Repository;

import java.util.Optional;

/**
 * The interface Authorization repository.
 *
 * @author Matthew Crowell <Matthew.Crowell@Smoothstack.com>
 */
@org.springframework.stereotype.Repository
public interface AuthorizationRepository extends Repository<LoanEntity, String> {
	/**
	 * Find by user id optional.
	 *
	 * @param id the id
	 * @return the optional
	 */
	Optional<LoanEntity> findByUserId(String id);
}
