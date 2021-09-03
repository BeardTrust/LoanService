package com.beardtrust.webapp.loanservice.repos;

package com.beardtrust.webapp.loanservice.entities;

import com.beardtrust.webapp.loanservice.entities.Balance;
import com.beardtrust.webapp.loanservice.entities.CurrencyValue;
import com.beardtrust.webapp.loanservice.entities.LoanEntity;
import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanRepository extends JpaRepository<LoanEntity, String> {

    public Page<LoanEntity> findByCreateDate(LocalDate parse, Pageable page);
    
    public Page<LoanEntity> findAllByUserId(String userId, Pageable page);

    public LoanEntity findByLoanId(String loanId);

    public Page<LoanEntity> findAllByLoanType_AprOrCurrencyValue_DollarsOrCurrencyValue_CentsAndUserId(Double newSearch, Double newSearch0, Double newSearch1, String userId, Pageable page);

    public Page<LoanEntity> findAllByLoanType_NumMonthsOrPrincipalAndUserId(Integer newSearch, Integer newSearch0, String userId, Pageable page);

    public Page<LoanEntity> findByCreateDateOrNextDueDateAndUserId(LocalDate parse, LocalDate parse0, String userId, Pageable page);

    public Page<LoanEntity> findAllIgnoreCaseByLoanType_TypeNameOrLoanType_DescriptionOrValueTitleAndUserId(String search, String search0, String search1, String userId, Pageable page);


}
