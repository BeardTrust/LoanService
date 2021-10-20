package com.beardtrust.webapp.loanservice.services;

import com.beardtrust.webapp.loanservice.entities.CurrencyValue;
import com.beardtrust.webapp.loanservice.entities.LoanEntity;
import com.beardtrust.webapp.loanservice.repos.LoanRepository;
import com.beardtrust.webapp.loanservice.repos.LoanTypeRepository;
import org.apache.commons.validator.GenericValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.apache.commons.lang.NumberUtils.isNumber;
import static org.apache.commons.validator.GenericValidator.isDouble;

@Service
public class LoanServiceImpl implements LoanService {

    private final LoanRepository repo;
    private final LoanTypeRepository ltr;

    public LoanServiceImpl(LoanRepository repo, LoanTypeRepository ltr) {
        this.repo = repo;
        this.ltr = ltr;
    }

    public LoanEntity getNewLoan() {
        return new LoanEntity();
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
        if (!("").equals(search)) {
            if (isNumber(search)) {
                System.out.println("search was a number");
                Integer newSearch = Integer.parseInt(search);
                Double doubleSearch = Double.parseDouble(search);
                return repo.findAllByLoanType_AprOrPrincipal_DollarsOrPrincipal_CentsOrBalance_DollarsOrBalance_Cents(doubleSearch, newSearch, newSearch, newSearch, newSearch, page);
            } if (GenericValidator.isDate(search, "yyyy-MM-dd", false)) {
                System.out.println("search was a date");
                return repo.findByCreateDateOrNextDueDateOrPreviousDueDate(LocalDate.parse(search), LocalDate.parse(search), LocalDate.parse(search), page);
            } else {
                return repo.findAllIgnoreCaseByLoanType_TypeNameOrLoanType_DescriptionOrValueTitle(search, search, search, page);
            }
        }
//        System.out.println("Found in loan repo: " + repo.findAll(page).getContent().get(0).toString());
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
        try {
            Optional<LoanEntity> l = repo.findById(loanId);
            return l.get();
        } catch (Exception e) {
            return null;
        }
    }

    public String deleteById(String loanId) {
        try {
            Optional<LoanEntity> l = repo.findById(loanId);
            repo.delete(l.get());
            return "Delete successful";
        } catch (Exception e) {
            return "Failed to delete: " + e;
        }
    }

    public LoanEntity save(LoanEntity l) {
        try {
            System.out.println("Attempting to save: " + l.toString());
            repo.save(l);
        } catch (Exception e) {
            System.out.println("Unable to save");
        }
        try {
            System.out.println("Attempting to save loan type: " + l.getLoanType().toString());
            ltr.save(l.getLoanType());
            repo.save(l);
        } catch (Exception e) {
            System.out.println("Unable to save loan type");
        }
        System.out.println("Loan Saved...");
        return l;
    }

    @Override
    public String updateLoan(LoanEntity l) {
        try {
            LoanEntity l2 = repo.findById(l.getId()).orElse(null);
            repo.save(l2);
            return "Update successful";
        } catch (Exception e) {
            return "Failed to update: " + e;
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
                System.out.println("search was an Integer");
                Integer newSearch = Integer.parseInt(search);
                return repo.findAllByLoanType_AprOrPrincipal_DollarsOrPrincipal_CentsOrBalance_DollarsOrBalance_CentsAndUser_UserId(Double.parseDouble(newSearch.toString()), newSearch, newSearch, newSearch, newSearch, userId, page);
            } if (GenericValidator.isDate(search, "yyyy-MM-dd", false)) {
                System.out.println("search was a date");
                return repo.findByCreateDateOrNextDueDateAndUser_UserId(LocalDate.parse(search), LocalDate.parse(search), userId, page);
            } else {
                return repo.findAllIgnoreCaseByLoanType_TypeNameOrLoanType_DescriptionOrValueTitleAndUser_UserId(search, search, search, userId, page);
            }
        }
        System.out.println("generic search, found:" + repo.findAllByUser_UserId(userId, page).getContent());
        System.out.println("UserId searched by: " + userId);
        return repo.findAllByUser_UserId(userId, page);
    }

    public CurrencyValue makePayment(CurrencyValue c, String id) {
        CurrencyValue returnValue = new CurrencyValue(false, 0, 0);
        try {
            LoanEntity l = repo.findById(id).get();
            returnValue = l.makePayment(c);
            repo.save(l);
            l.resetMinDue();
            repo.save(l);
            return returnValue;
        } catch (Exception e) {
            System.out.println("Exception caught: " + e);
            return null;
        }
    }

    public void calculateMinDue() {
     List<LoanEntity> l = repo.findAll();
        for (int i = 0; i < l.size(); i++) {
            LoanEntity l1 = l.get(i);
            l1.calculateMinDue();
            repo.save(l1);
        }
    }
}
