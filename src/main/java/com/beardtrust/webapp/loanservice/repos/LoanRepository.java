package com.beardtrust.webapp.loanservice.repos;

import com.beardtrust.webapp.loanservice.entities.LoanEntity;
import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanRepository extends JpaRepository<LoanEntity, String> {

    public Page<LoanEntity> findByCreateDate(LocalDate parse, Pageable page);
    
    public Page<LoanEntity> findAllByUser_UserId(String userId, Pageable page);

    public Page<LoanEntity> findByCreateDateOrNextDueDateAndUser_UserId(LocalDate parse, LocalDate parse0, String userId, Pageable page);

    Page<LoanEntity> findAllIgnoreCaseByLoanType_TypeNameOrLoanType_DescriptionOrValueTitleAndUser_UserId(String search, String search1, String search2, String userId, Pageable page);

    Page<LoanEntity> findAllByLoanType_AprOrPrincipal_DollarsOrPrincipal_CentsOrBalance_DollarsOrBalance_CentsAndUser_UserId(Double newSearch, Integer newSearch1, Integer newSearch2, Integer newSearch3, Integer newSearch4, String userId, Pageable page);

    Page<LoanEntity> findAllIgnoreCaseByLoanType_TypeNameOrLoanType_DescriptionOrValueTitle(String search, String search1, String search2, Pageable page);

    Page<LoanEntity> findByCreateDateOrNextDueDateOrPreviousDueDate(LocalDate parse, LocalDate parse1, LocalDate parse2, Pageable page);

    Page<LoanEntity> findAllByLoanType_AprOrPrincipal_DollarsOrPrincipal_CentsOrBalance_DollarsOrBalance_Cents(Double newSearch, Integer newSearch1, Integer newSearch2, Integer newSearch3, Integer newSearch4, Pageable page);

    Page<LoanEntity> findAll(Pageable page);

}
