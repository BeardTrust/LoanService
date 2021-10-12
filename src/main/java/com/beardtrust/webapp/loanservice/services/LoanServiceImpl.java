package com.beardtrust.webapp.loanservice.services;

import com.beardtrust.webapp.loanservice.entities.Balance;
import com.beardtrust.webapp.loanservice.entities.CurrencyValue;
import java.util.List;

import com.beardtrust.webapp.loanservice.entities.LoanEntity;
import com.beardtrust.webapp.loanservice.repos.LoanRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import static org.apache.commons.lang.NumberUtils.isNumber;
import org.apache.commons.validator.GenericValidator;
import static org.apache.commons.validator.GenericValidator.isDouble;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class LoanServiceImpl implements LoanService {

    private LoanRepository repo;

    public LoanServiceImpl(LoanRepository repo) {
        this.repo = repo;
    }

    @Override
    public Page<LoanEntity> getAllLoansPage(Integer n, Integer s, String sortName, String sortDir, String search) {
        List<Sort.Order> orders = new ArrayList();
        orders.add(new Sort.Order(getDirection(sortDir), sortName));
        System.out.println("Inbound sort: " + sortName + " " + sortDir);
        System.out.println("Combined orders: " + orders);
        Pageable page = PageRequest.of(n, s, Sort.by(orders));
        System.out.println("Compiled page: " + page);
        System.out.println("Search param: " + search);
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
        return repo.findAll(page);
    }

    public List<LoanEntity> getAllLoans() {
        return repo.findAll();
    }

    public Sort.Direction getDirection(String dir) {
        if ("asc".equals(dir)) {
            return Sort.Direction.ASC;
        } else {
            return Sort.Direction.DESC;
        }
    }

    @Override
    public LoanEntity getById(String loanId) {
        return repo.findByLoanId(loanId);
    }

    public String deleteById(String loanId) {
        try {
            LoanEntity l = repo.findByLoanId(loanId);
            repo.delete(l);
            return "Delete successful";
        } catch (Exception e) {
            return "Failed to delete: " + e;
        }
    }

    public LoanEntity save(LoanEntity l) {
        repo.save(l);
        return l;
    }

    @Override
    public String updateLoan(LoanEntity l) {
        try {
            LoanEntity l2 = repo.findByLoanId(l.getLoanId());
            repo.save(l2);
            return "Update successful: " + l2;
        } catch (Exception e) {
            return "Update Failed";
        }
    }

    private CurrencyValue parseCurrency(String searchCriteria) {
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

        return searchBalance;
    }

    @Override
    public Page<LoanEntity> getAllMyLoansPage(int n, int s, String[] sortBy, String search) {
        String sortName = sortBy[0];
        String sortDir = sortBy[1];
        String userId = "";
        if (sortBy.length >= 3) {
            userId = sortBy[2];
        }
        System.out.println("Attempting to find my loans");
        List<Sort.Order> orders = new ArrayList();
        orders.add(new Sort.Order(getDirection(sortDir), sortName));
        System.out.println("Inbound sort: " + sortName + " " + sortDir);
        System.out.println("Combined orders: " + orders);
        Pageable page = PageRequest.of(n, s, Sort.by(orders));
        System.out.println("Compiled page: " + page);
        System.out.println("Search param: " + search);
        if (!("").equals(search)) {
            if (isDouble(search)) {
                System.out.println("search was a double");
                Double newSearch = Double.parseDouble(search);
                return repo.findAllByLoanType_AprOrPrincipal_DollarsOrPrincipal_CentsOrBalance_DollarsOrBalance_CentsAndUserId(newSearch, newSearch, newSearch, newSearch, newSearch, userId, page);
            } else if (isNumber(search)){
                System.out.println("search was an Integer");
                Integer newSearch = Integer.parseInt(search);
                return repo.findAllByLoanType_NumMonthsAndUserId(newSearch, newSearch, userId, page);
            } if (GenericValidator.isDate(search, "yyyy-MM", false)) {
                System.out.println("search was a date");
                return repo.findByCreateDateOrNextDueDateAndUserId(LocalDate.parse(search), LocalDate.parse(search), userId, page);
            } else {
                return repo.findAllIgnoreCaseByLoanType_TypeNameOrLoanType_DescriptionOrValueTitleAndUserId(search, search, search, userId, page);
            }
        }
        System.out.println("generic search, found:" + repo.findAllByUserId(userId, page).getContent());
        System.out.println("UserId searched by: " + userId);
        return repo.findAllByUserId(userId, page);
    }
}
