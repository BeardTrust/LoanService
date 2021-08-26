package com.beardtrust.webapp.loanservice.repos;

package com.beardtrust.webapp.loanservice.entities;

import com.beardtrust.webapp.loanservice.entities.LoanEntity;
import java.time.LocalDate;
import java.util.List;
import javax.persistence.Embeddable;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.Objects;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanRepository extends JpaRepository<LoanEntity, String> {

    public List<LoanEntity> getAllLoans();

    public Page<LoanEntity> findByCreateDate(LocalDate parse, Pageable page);

    public LoanEntity findAllById(String Id);

    public LoanEntity findByLoanId(String id);

    public Page<LoanEntity> findAllByLoanType_TypeNameIgnoreCase(String search, Pageable page);

    public Page<LoanEntity> findAllByPrincipalOrPaydayOrBalane_ValueIsLike(Integer newSearch, Integer newSearch0, Pageable page);


}