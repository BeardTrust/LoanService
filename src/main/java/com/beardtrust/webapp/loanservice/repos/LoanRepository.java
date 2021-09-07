package com.beardtrust.webapp.loanservice.repos;

import com.beardtrust.webapp.loanservice.entities.Balance;
import com.beardtrust.webapp.loanservice.entities.LoanEntity;
import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanRepository extends JpaRepository<LoanEntity, String> {

//    public Page<LoanEntity> getAllLoans(Integer n, Integer s, String sortName, String sortDir, String search, Pageable page);

    public Page<LoanEntity> findByCreateDate(LocalDate parse, Pageable page);

    public LoanEntity findAllByLoanId(String loanId);

    public LoanEntity findByLoanId(String loanId);

    public Page<LoanEntity> findAllByLoanType_TypeNameIgnoreCase(String search, Pageable page);

//    public Page<LoanEntity> findAllByPrincipalOrPayDayOrBalance_ValueIsLike(Integer newSearch, Balance newSearch0, Pageable page);


}
