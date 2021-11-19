package com.beardtrust.webapp.loanservice.repos;

import java.time.LocalDate;
import java.util.List;

import com.beardtrust.webapp.loanservice.entities.LoanEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanRepository extends JpaRepository<LoanEntity, String> {

    Page<LoanEntity> findAll(Pageable page);

    Page<LoanEntity> findAllIgnoreCaseByLoanType_TypeNameContainingOrLoanType_DescriptionContainingOrPayment_MinMonthFeeContaining(String search, String search1, String search2, Pageable page);

    Page<LoanEntity> findAllIgnoreCaseByLoanType_TypeNameContainingOrLoanType_DescriptionContainingOrPayment_MinMonthFeeContainingAndUser_UserId(String search, String search1, String search2, String userId, Pageable page);

    Page<LoanEntity> findAllByLoanType_AprOrPrincipal_DollarsOrPrincipal_CentsOrBalance_DollarsOrBalance_CentsOrPayment_MinDue_DollarsOrPayment_MinDue_CentsOrPayment_LateFee_DollarsOrPayment_LateFee_CentsAndUser_UserId(double parseDouble, Integer newSearch, Integer newSearch1, Integer newSearch2, Integer newSearch3, Integer newSearch4, Integer newSearch5, Integer newSearch6, Integer newSearch7, String userId, Pageable page);

    List<LoanEntity> findByLoanType_AprOrPrincipal_DollarsOrPrincipal_CentsOrBalance_DollarsOrBalance_CentsOrPayment_MinDue_DollarsOrPayment_MinDue_CentsOrPayment_LateFee_DollarsOrPayment_LateFee_CentsAndUser_UserId(double parseDouble, Integer newSearch, Integer newSearch1, Integer newSearch2, Integer newSearch3, Integer newSearch4, Integer newSearch5, Integer newSearch6, Integer newSearch7, String userId, Pageable page);

    Page<LoanEntity> findAllByLoanType_AprOrPrincipal_DollarsOrPrincipal_CentsOrBalance_DollarsOrBalance_CentsOrPayment_MinDue_DollarsOrPayment_MinDue_CentsOrPayment_LateFee_DollarsOrPayment_LateFee_Cents(double parseDouble, Integer newSearch, Integer newSearch1, Integer newSearch2, Integer newSearch3, Integer newSearch4, Integer newSearch5, Integer newSearch6, Integer newSearch7, Pageable page);

    List<LoanEntity> findByLoanType_AprOrPrincipal_DollarsOrPrincipal_CentsOrBalance_DollarsOrBalance_CentsOrPayment_MinDue_DollarsOrPayment_MinDue_CentsOrPayment_LateFee_DollarsOrPayment_LateFee_Cents(double parseDouble, Integer newSearch, Integer newSearch1, Integer newSearch2, Integer newSearch3, Integer newSearch4, Integer newSearch5, Integer newSearch6, Integer newSearch7, Pageable page);

    Page<LoanEntity> findByCreateDateOrPayment_NextDueDate(LocalDate parse, LocalDate parse1, Pageable page);

    Page<LoanEntity> findByCreateDateOrPayment_NextDueDateAndUser_UserId(LocalDate parse, LocalDate parse1, String userId, Pageable page);

    Page<LoanEntity> findByUser_UserId(String userId, Pageable page);
}
