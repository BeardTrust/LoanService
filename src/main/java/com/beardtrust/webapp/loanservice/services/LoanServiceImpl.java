package com.beardtrust.webapp.loanservice.services;

import com.beardtrust.webapp.loanservice.entities.CurrencyValue;
import com.beardtrust.webapp.loanservice.entities.LoanEntity;
import com.beardtrust.webapp.loanservice.repos.LoanRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import static org.apache.commons.lang.NumberUtils.isNumber;

import lombok.extern.slf4j.Slf4j;
import com.beardtrust.webapp.loanservice.repos.LoanTypeRepository;
import com.beardtrust.webapp.loanservice.repos.UserRepository;
import org.apache.commons.validator.GenericValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort.Order;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.apache.commons.lang.NumberUtils.isNumber;
import static org.apache.commons.validator.GenericValidator.isDouble;

@Service
@Slf4j
public class LoanServiceImpl implements LoanService {

    private final LoanRepository repo;
    private final LoanTypeRepository ltr;
    private final UserRepository ur;

    public LoanServiceImpl(LoanRepository repo, LoanTypeRepository ltr, UserRepository ur) {
        this.repo = repo;
        this.ltr = ltr;
        this.ur = ur;
    }

    public LoanEntity getNewLoan(String userId) {
        LoanEntity l = new LoanEntity();
        l.setUser(ur.findById(userId).get());
        return l;
    }

    @Override
    public Page<LoanEntity> getAllLoansPage(Integer n, Integer s, String sortName, String sortDir, String search) {
        log.trace("Start LoanService.getAllLoansPage(" + n + ", " + s + ", " + sortName + ", " + sortDir + ", " + search + ")");
        List<Sort.Order> orders = new ArrayList();
        orders.add(new Sort.Order(getDirection(sortDir), sortName));
        Pageable page = PageRequest.of(n, s, Sort.by(orders));
        System.out.println("Compiled page: " + page);
        System.out.println("Search param: " + search);
        /*if (!("").equals(search)) {
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
        return repo.findById(loanId).get();
    }

    public String deleteById(String loanId) {
        log.trace("Start LoanService.deleteById(" + loanId + ")");
        try {
            Optional<LoanEntity> l = repo.findById(loanId);
            repo.delete(l.get());
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
            LoanEntity l2 = repo.findById(l.getId()).orElse(null);
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

    public Page<LoanEntity> getAllMyLoansPage(int n, int s, String[] sortBy, String search, String userId) {
    log.trace("Start loanService.getAllMyLoansPage(" + n + ", " + s + ", " + sortBy + ", " + search + ")");
        List<Sort.Order> orders = parseOrders(sortBy);
        Pageable page = PageRequest.of(n, s, Sort.by(orders));
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
        log.trace("End loanService.getAllMyLoansPage(" + n + ", " + s + ", " + sortBy + ", " + search + ")");
        return repo.findByUser_UserId(userId, page);
    }

    public CurrencyValue makePayment(CurrencyValue c, String id) {
        CurrencyValue returnValue = new CurrencyValue(false, 0, 0);
        try {
            LoanEntity l = repo.findById(id).get();
            returnValue = l.makePayment(c);
            if (l.getMinDue().isNegative()) {
                System.out.println("Min Due Negative");
                l.getMinDue().setNegative(false);
                l.getMinDue().setDollars(0);
                l.getMinDue().setCents(0);
            }
            repo.save(l);
            repo.save(l);
            return returnValue;
        } catch (Exception e) {
            log.warn("Psyment process failed!!!");
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

    private Sort.Direction getSortDirection(String direction) {
        if (direction.equals("asc")) {
            return Sort.Direction.ASC;
        } else if (direction.equals("desc")) {
            return Sort.Direction.DESC;
        }

        return Sort.Direction.ASC;
    }

    private List<Sort.Order> parseOrders(String[] sortBy) {
        List<Sort.Order> orders = new ArrayList<>();

        if (sortBy[0].contains(",")) {
            for (String sortOrder : sortBy) {
                String[] _sortBy = sortOrder.split(",");
                orders.add(new Sort.Order(getSortDirection(_sortBy[1]), _sortBy[0]));
            }
        } else {
            orders.add(new Sort.Order(getSortDirection(sortBy[1]), sortBy[0]));
        }
//        System.out.println("loan orders: " + orders);
        return orders;
    }
}
