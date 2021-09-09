package com.beardtrust.webapp.loanservice.repos;

import com.beardtrust.webapp.loanservice.entities.LoanTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface LoanTypeRepository extends JpaRepository<LoanTypeEntity, String> {

//    void deactivateById(String Id);

    public Page<LoanTypeEntity> findAllByApr(Double newSearch, Pageable page);

    public Page<LoanTypeEntity> findAllByNumMonths(Integer newSearch, Integer newSearch0, Pageable page);

    public Page<LoanTypeEntity> findAllByActiveStatusIsTrueAndIdOrTypeNameIgnoreCaseOrDescriptionContainsIgnoreCase(
            String search, String search1, String search2, Pageable page);
  
    public Page<LoanTypeEntity> findAllByActiveStatusIsTrue(Pageable page);
}
