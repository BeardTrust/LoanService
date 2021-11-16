package com.beardtrust.webapp.loanservice.services;

import com.beardtrust.webapp.loanservice.entities.CurrencyValue;
import com.beardtrust.webapp.loanservice.entities.LoanEntity;
import com.beardtrust.webapp.loanservice.entities.LoanTypeEntity;
import com.beardtrust.webapp.loanservice.repos.LoanRepository;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.apache.commons.lang.NumberUtils.isNumber;

import lombok.extern.slf4j.Slf4j;
import com.beardtrust.webapp.loanservice.repos.LoanTypeRepository;
import com.beardtrust.webapp.loanservice.repos.UserRepository;
import org.apache.commons.validator.GenericValidator;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;

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
//        log.info("userId received for credit check: " + userId);
        l.setUser(ur.findById(userId).get());
        return l;
    }

    @Override
    public Page<LoanEntity> getAllLoansPage(int n, int s, String[] sortBy, String search) {
        log.trace("Start LoanService.getAllLoansPage(" + n + ", " + s + ", " + sortBy + ", " + search + ")");
        List<Sort.Order> orders = parseOrders(sortBy);
        Pageable page = PageRequest.of(n, s, Sort.by(orders));
        System.out.println("Compiled page: " + page);
        System.out.println("Search param: " + search);
        if (search.contains("$")) {
            String[] searchSplit = search.split("\\$");
            if (isNumber(searchSplit[1])) {
                search = searchSplit[1];
            }
        }
        if (!("").equals(search)) {
            if (isNumber(search) && !search.contains(".")) {
                log.trace("Number had no '.', searching without split");
                Integer newSearch = Integer.parseInt(search);
                return repo.findAllByLoanType_AprOrPrincipal_DollarsOrPrincipal_CentsOrBalance_DollarsOrBalance_CentsOrPayment_MinDue_DollarsOrPayment_MinDue_CentsOrPayment_LateFee_DollarsOrPayment_LateFee_Cents(Double.parseDouble(newSearch.toString()), newSearch, newSearch, newSearch, newSearch, newSearch, newSearch, newSearch, newSearch, page);
            }
            if (isNumber(search) && search.contains(".")) {
                String[] nums = search.split("\\.");
                log.trace("Number had a '.', splitting for dollars and cents");
                Integer newSearch = Integer.parseInt(nums[0]);
                List<LoanEntity> l = repo.findByLoanType_AprOrPrincipal_DollarsOrPrincipal_CentsOrBalance_DollarsOrBalance_CentsOrPayment_MinDue_DollarsOrPayment_MinDue_CentsOrPayment_LateFee_DollarsOrPayment_LateFee_Cents(Double.parseDouble(search), newSearch, newSearch, newSearch, newSearch, newSearch, newSearch, newSearch, newSearch, page);
                newSearch = Integer.parseInt(nums[1]);
                List<LoanEntity> p = repo.findByLoanType_AprOrPrincipal_DollarsOrPrincipal_CentsOrBalance_DollarsOrBalance_CentsOrPayment_MinDue_DollarsOrPayment_MinDue_CentsOrPayment_LateFee_DollarsOrPayment_LateFee_Cents(Double.parseDouble(search), newSearch, newSearch, newSearch, newSearch, newSearch, newSearch, newSearch, newSearch, page);
                for (int i = 0; i < p.size(); i++) {
                    l.add((p.get(i)));
                }
                Page<LoanEntity> o = new PageImpl(l, page, l.size());
                return o;
            }
            if (GenericValidator.isDate(search, "yyyy-MM-dd", false)) {
                log.trace("search was a date");
                return repo.findByCreateDateOrPayment_NextDueDate(LocalDate.parse(search), LocalDate.parse(search), page);
            } else {
                return repo.findAllIgnoreCaseByLoanType_TypeNameContainingOrLoanType_DescriptionContainingOrPayment_MinMonthFeeContaining(search, search, search, page);
            }
        }
        log.trace("End LoanService.getAllLoansPage(" + n + ", " + s + ", " + sortBy +  ", " + search + ")");
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
        try {
            repo.save(l);
        } catch (Exception e) {
            log.warn("Error trying to save a new loan: " + e.getMessage());
            ltr.save(l.getLoanType());
            repo.save(l);
        }
        log.trace("End LoanService.LoanEntity(" + l + ")");
        return l;
    }

    @Override
    public String updateLoan(LoanEntity l) {
        log.trace("Start LoanService.updateLoan(" + l + ")");
        ltr.save(l.getLoanType());
        repo.save(l);
        log.trace("updated loan valueTitle: " + repo.findById(l.getId()));
        try {
            LoanEntity l2 = repo.findById(l.getId()).orElse(null);
            repo.save(l2);
            log.trace("End LoanService.updateLoan(" + l + ")");
            return "Update successful: " + l2;
        } catch (Exception e) {
            log.warn("Exception LoanService.updateLoan(" + l + ") \n"
                    + e.getMessage());
            return "Update Failed";
        }
    }

    public String lateFeeCheck(LoanEntity l) {
        log.trace("Checking late fee...");
//        if (l.checkDate()) {
//            log.trace("late fee applied to loan past due...");
////            System.out.println("late fee added: " + l.getLateFee());
////            repo.save(l);
//            log.trace("Returning from late fee check...");
//            return "Late fee applied";
//        } else {
//            log.trace("loan not past due, no late fee applied...");
////            l.checkDate();
//            log.trace("Returning from late fee check...");
//            return "No late fee applied";
//        }
        return null;
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
        if (search.contains("$")) {
            String[] searchSplit = search.split("\\$");
            if (isNumber(searchSplit[1])) {
                search = searchSplit[1];
            }
        }
        if (!("").equals(search)) {
            if (isNumber(search) && !search.contains(".")) {
                log.trace("Number had no '.', searching without split");
                Integer newSearch = Integer.parseInt(search);
                return repo.findAllByLoanType_AprOrPrincipal_DollarsOrPrincipal_CentsOrBalance_DollarsOrBalance_CentsOrPayment_MinDue_DollarsOrPayment_MinDue_CentsOrPayment_LateFee_DollarsOrPayment_LateFee_CentsAndUser_UserId(Double.parseDouble(newSearch.toString()), newSearch, newSearch, newSearch, newSearch, newSearch, newSearch, newSearch, newSearch, userId, page);
            }
            if (isNumber(search) && search.contains(".")) {
                String[] nums = search.split("\\.");
                log.trace("Number had a '.', splitting for dollars and cents");
                Integer newSearch = Integer.parseInt(nums[0]);
                List<LoanEntity> l = repo.findByLoanType_AprOrPrincipal_DollarsOrPrincipal_CentsOrBalance_DollarsOrBalance_CentsOrPayment_MinDue_DollarsOrPayment_MinDue_CentsOrPayment_LateFee_DollarsOrPayment_LateFee_CentsAndUser_UserId(Double.parseDouble(search), newSearch, newSearch, newSearch, newSearch, newSearch, newSearch, newSearch, newSearch, userId, page);
                newSearch = Integer.parseInt(nums[1]);
                List<LoanEntity> p = repo.findByLoanType_AprOrPrincipal_DollarsOrPrincipal_CentsOrBalance_DollarsOrBalance_CentsOrPayment_MinDue_DollarsOrPayment_MinDue_CentsOrPayment_LateFee_DollarsOrPayment_LateFee_CentsAndUser_UserId(Double.parseDouble(search), newSearch, newSearch, newSearch, newSearch, newSearch, newSearch, newSearch, newSearch, userId, page);
                for (int i = 0; i < p.size(); i++) {
                    l.add((p.get(i)));
                }
                Page<LoanEntity> o = new PageImpl(l, page, l.size());
                return o;
            }
            if (GenericValidator.isDate(search, "yyyy-MM-dd", false)) {
                log.trace("search was a date");
                return repo.findByCreateDateOrPayment_NextDueDateAndUser_UserId(LocalDate.parse(search), LocalDate.parse(search), userId, page);
            } else {
                return repo.findAllIgnoreCaseByLoanType_TypeNameContainingOrLoanType_DescriptionContainingOrPayment_MinMonthFeeContainingAndUser_UserId(search, search, search, userId, page);
            }
        }
        log.info("End loanService.getAllMyLoansPage(" + n + ", " + s + ", " + sortBy + ", " + search + ")");
        return repo.findByUser_UserId(userId, page);
    }

    /*

    Receives a CurrencyValue and String representing the value to be paid towards the loan of id.
    Late fees will automatically take out a cut, any resulting amount will go towards
    the minimum due. If the minimum die is cleared, the hasPaid status will be set to true.
    If the amount paid is more than amount owed, the remaining amount will be returned to the
    payment source

     */
    public CurrencyValue makePayment(CurrencyValue c, String id) {
        CurrencyValue returnValue = new CurrencyValue(false, 0, 0);
        try {
            LoanEntity l = repo.findById(id).get();
            returnValue = processPayment(c, l);
            if (l.getPayment().getMinDue().isNegative()) {
                System.out.println("Min Due Negative");
                l.getPayment().getMinDue().setNegative(false);
                l.getPayment().getMinDue().setDollars(0);
                l.getPayment().getMinDue().setCents(0);
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
//            l1.calculateMinDue();
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

    /*
    Receives a CurrencyValue object representing the value to be paid towards the loan.
    Late fees will automatically take out a cut, any resulting amount will go towards
    the minimum due. If the minimum die is cleared, the hasPaid status will be set to true.
    If the amount paid is more than amount owed, the remaining amount will be returned to the
    payment source

     */
    public CurrencyValue processPayment(CurrencyValue payment, LoanEntity l) {
        l.getPayment().getMinDue().setNegative(false);
        CurrencyValue temp;
        System.out.println("Incoming payment: " + payment.toString());
        if (l.getPayment().getLateFee().getDollars() > 0 || l.getPayment().getLateFee().getCents() > 0) {
            System.out.println("latefee to pay on: " + l.getPayment().getLateFee());
            temp = l.getPayment().getLateFee();
            l.getPayment().getLateFee().add(payment);
            payment.add(temp);
            System.out.println("payment after late fee: " + payment);
        }
        payment.setNegative(false);
        if (payment.compareTo(l.getBalance()) == 1) {//paynment is greater
            payment.setNegative(true);
            System.out.println("paying more than balance..." + payment);
            System.out.println("balance: " + l.getBalance());
            l.getPayment().getMinDue().add(payment);
            l.getBalance().add(payment);
            System.out.println("balance added to payment: " + l.getBalance());
            payment.setDollars(l.getBalance().getDollars());
            payment.setCents(l.getBalance().getCents());
            payment.setNegative(false);
            l.getBalance().setDollars(0);
            l.getBalance().setCents(0);
            l.getBalance().setNegative(false);
            l.getPayment().getMinDue().setDollars(0);
            l.getPayment().getMinDue().setCents(0);
            payment.setNegative(false);
            System.out.println("resulting balance: " + l.getBalance());
            System.out.println("remaining payment: " + payment);
            l.getPayment().setHasPaid(true);
            return payment;
        } else {
            payment.setNegative(true);
            System.out.println("balance paid on: " + l.getBalance());
            System.out.println("amount paying: " + payment);
            l.getPayment().getMinDue().add(payment);
            l.getBalance().add(payment);
            System.out.println("minimum payment due: " + l.getPayment().getMinDue());
            System.out.println("balance after payment: " + l.getBalance());
        }
        int temp2 = l.getPayment().getMinDue().getDollars() + l.getPayment().getMinDue().getCents();
        if (temp2 <= 0) {
            l.getPayment().getMinDue().setDollars(0);
            l.getPayment().getMinDue().setCents(0);
            l.getPayment().getMinDue().setNegative(false);
            l.getPayment().setHasPaid(true);
//            l.getPayment().checkDate();
        }
        System.out.println("Loan haspaid status: " + l.getPayment().isHasPaid());
        return payment;
    }

    public LoanEntity creditCheck(String userId, LoanTypeEntity lt) {
        /*
        credit check logic here
         */
        LoanEntity l = getNewLoan(userId);
        l.setLoanType(lt);
        l.setPrincipal(new CurrencyValue(false, 1000, 0));
//        l.initializeBalance();
//        l.calculateMinDue();
        log.info("loan built: " + l.toString());
        return l;
    }
}
