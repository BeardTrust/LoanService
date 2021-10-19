package com.beardtrust.webapp.loanservice.services;

import com.beardtrust.webapp.loanservice.entities.Balance;
import com.beardtrust.webapp.loanservice.entities.CurrencyValue;
import java.util.List;

import com.beardtrust.webapp.loanservice.entities.LoanEntity;
import com.beardtrust.webapp.loanservice.repos.LoanRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import static org.apache.commons.lang.NumberUtils.isNumber;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.validator.GenericValidator;
import static org.apache.commons.validator.GenericValidator.isDouble;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class LoanServiceImpl implements LoanService {

    private LoanRepository repo;

    public LoanServiceImpl(LoanRepository repo) {
        this.repo = repo;
    }

    @Override
    public Page<LoanEntity> getAllLoansPage(Integer n, Integer s, String sortName, String sortDir, String search) {
        log.trace("Start LoanService.getAllLoansPage(" + n + ", " + s + ", " + sortName + ", " + sortDir + ", " + search + ")");
        List<Sort.Order> orders = new ArrayList();
        orders.add(new Sort.Order(getDirection(sortDir), sortName));
        Pageable page = PageRequest.of(n, s, Sort.by(orders));
        /*if (!("").equals(search)) {
            if (isDouble(search)) {
                System.out.println("search was a double");
                Double newSearch = Double.parseDouble(search);
                return repo.findAllByLoanTypeEntity_AprOrCurrencyValue_DollarsOrCurrencyValue_CentsAndUserId(newSearch, newSearch, newSearch, page);
            } else if (isNumber(search)){
                System.out.println("search was an Integer");
                Integer newSearch = Integer.parseInt(search);
                return repo.findAllByLoanTypeEntity_NumMonthsOrPrincipalAndUserId(newSearch, newSearch, page);
            } if (GenericValidator.isDate(search, "yyyy-MM", false)) {
                System.out.println("search was a date");
                return repo.findByCreateDateOrNextDueDateAndUserId(LocalDate.parse(search), LocalDate.parse(search), page);
            } else {
                return repo.findAllIgnoreCaseByLoanType_TypeNameOrLoanType_DescriptionAndUserId(search, page);
            }
        }*/
        log.trace("End LoanService.getAllLoansPage(" + n + ", " + s + ", " + sortName + ", " + sortDir + ", " + search + ")");
        return repo.findAll(page);
    }

    public List<LoanEntity> getAllLoans() {
        log.trace("Start LoanService.getAllLoans()");
        return repo.findAll();
    }

    public Sort.Direction getDirection(String dir) {
        log.trace("Start LoanService.getDirection(" + dir + ")");
        if ("asc".equals(dir)) {
            return Sort.Direction.ASC;
        } else {
            return Sort.Direction.DESC;
        }
    }

    @Override
    public LoanEntity getById(String loanId) {
        log.trace("Start LoanService.getById(" + loanId + ")");
        return repo.findByLoanId(loanId);
    }

    public String deleteById(String loanId) {
        log.trace("Start LoanService.deleteById(" + loanId + ")");
        try {
            LoanEntity l = repo.findByLoanId(loanId);
            repo.delete(l);
            return "Delete successful";
        } catch (Exception e) {
            log.info("Exception LoanService.deleteById(" + loanId + ") \n"
            + e.getMessage());
            return "Failed to delete: " + e;
        }
    }

    public LoanEntity save(LoanEntity l) {
        log.trace("Start LoanService.LoanEntity(" + l + ")");
        repo.save(l);
        log.trace("End LoanService.LoanEntity(" + l + ")");
        return l;
    }

    @Override
    public String updateLoan(LoanEntity l) {
        log.trace("Start LoanService.updateLoan(" + l + ")");
        try {
            LoanEntity l2 = repo.findByLoanId(l.getLoanId());
            repo.save(l2);
            log.trace("End LoanService.updateLoan(" + l + ")");
            return "Update successful: " + l2;
        } catch (Exception e) {
            log.info("Exception LoanService.updateLoan(" + l + ") \n"
            + e.getMessage());
            return "Update Failed";
        }
    }

    private CurrencyValue parseCurrency(String searchCriteria) {
        log.trace("Start loanService.parseCurrency(" + searchCriteria + ")");
        String[] values = searchCriteria.split(",");
        CurrencyValue searchBalance = null;

        if (values.length == 2) {
            searchBalance = new CurrencyValue(Integer.parseInt(values[0]), Integer.parseInt(values[1]));
        } else {
            if (Integer.parseInt(values[0]) > 99) {
                searchBalance = new CurrencyValue(Integer.parseInt(values[0]), 0);
            } else {
                searchBalance = new CurrencyValue(Integer.parseInt(values[0]), 0);
            }
        }
        log.trace("End loanService.parseCurrency(" + searchCriteria + ")");
        return searchBalance;
    }

    @Override
    public Page<LoanEntity> getAllMyLoansPage(int n, int s, String[] sortBy, String search) {
        log.trace("Start loanService.getAllMyLoansPage(" + n + ", " + s + ", " + sortBy + ", " + search + ")");
        String sortName = sortBy[0];
        String sortDir = sortBy[1];
        String userId = "";
        if (sortBy.length >= 3) {
            userId = sortBy[2];
        }
        List<Sort.Order> orders = new ArrayList();
        orders.add(new Sort.Order(getDirection(sortDir), sortName));
        Pageable page = PageRequest.of(n, s, Sort.by(orders));
        if (!("").equals(search)) {
            if (isDouble(search)) {
                Double newSearch = Double.parseDouble(search);
                return repo.findAllByLoanType_AprOrCurrencyValue_DollarsOrCurrencyValue_CentsAndUserId(newSearch, newSearch, newSearch, userId, page);
            } else if (isNumber(search)){
                Integer newSearch = Integer.parseInt(search);
                return repo.findAllByLoanType_NumMonthsOrPrincipalAndUserId(newSearch, newSearch, userId, page);
            } if (GenericValidator.isDate(search, "yyyy-MM", false)) {
                return repo.findByCreateDateOrNextDueDateAndUserId(LocalDate.parse(search), LocalDate.parse(search), userId, page);
            } else {
                return repo.findAllIgnoreCaseByLoanType_TypeNameOrLoanType_DescriptionOrValueTitleAndUserId(search, search, search, userId, page);
            }
        }
        log.trace("End loanService.getAllMyLoansPage(" + n + ", " + s + ", " + sortBy + ", " + search + ")");
        return repo.findAllByUserId(userId, page);
    }
}
