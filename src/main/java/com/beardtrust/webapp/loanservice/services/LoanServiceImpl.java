package com.beardtrust.webapp.loanservice.services;

import com.beardtrust.webapp.loanservice.entities.*;
import com.beardtrust.webapp.loanservice.repos.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.apache.commons.lang.NumberUtils.isNumber;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.validator.GenericValidator;
import org.hibernate.type.LocalDateTimeType;
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
    private final AccountRepository ar;
    private final CardRepository cr;
    private final FinancialAssetRepository far;

    public LoanServiceImpl(LoanRepository repo, LoanTypeRepository ltr, UserRepository ur,
                           AccountRepository ar, CardRepository cr, FinancialAssetRepository far) {
        this.repo = repo;
        this.ltr = ltr;
        this.ur = ur;
        this.ar = ar;
        this.cr = cr;
        this.far = far;
    }
    /**
     A simple health check that ensures all repos can be accessed without issue;

     @return String A message indicating the health status of the service.
     * */
    public String healthCheck() {
        StringBuilder healthStatus = new StringBuilder();
        Pageable page = PageRequest.of(0, 5);

        try {
            Page<LoanEntity> loans = repo.findAll(page);
            Page<LoanTypeEntity> types = ltr.findAll(page);
            Page<UserEntity> users = ur.findAll(page);
            Page<AccountEntity> accounts = ar.findAll(page);
            Page<CardEntity> cards = cr.findAll(page);
            Page<FinancialAsset> assets = far.findAll(page);
            log.info("Health check completed with healthy status.");
            processFees();
            healthStatus.append("Healthy");
        } catch(Exception e) {
            log.debug("Health check failed wih error: " + e.getMessage());
            healthStatus.append("Unhealthy");
        }

        return healthStatus.toString();
    }
    /**
     Returns a new loan for creation between the front end
     @param userId the user that owns the loan
     @return LoanEntity the user's new loan being returned.

     */
    public LoanEntity getNewLoan(String userId) {
        log.trace("LoanService getNewLoan...");
        LoanEntity l = new LoanEntity();
        l.setUser(ur.findById(userId).get());
        log.debug("New loan created: " + l);
        log.trace("Loanservice End getNewLoan");
        return l;
    }

    /**
     Gets all loans as a Pageable object that sorts and filters based on params passed in.

     @param n the page number for the Pageable object
     @param s the page size for the Pageable object
     @param sortBy the array containing sort orders for the Pageable object
     @param search the String used for filtering the Pageable object
     @return Page</LoanEntity> the user's loans being returned.

     */
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
    /**
     *
      Returns all Loans as a list
     @return List</LoanEntity> The list of loans
     */
    public List<LoanEntity> getAllLoans() {
        log.trace("Start LoanService.getAllLoans()");
        return repo.findAll();
    }
    /**
     Gets a loan by its Id
     @param loanId The loan to be retrieved
     @return LoanEntity The loan found by the Id
     */
    @Override
    public LoanEntity getById(String loanId) {
        log.trace("Start LoanService.getById(" + loanId + ")");
        return repo.findById(loanId).get();
    }
    /**
     Deletes a loan by its Id. This is a true removal from the database and should be avoided,
     unless the loan is backed up in the NOSQLDB
     @param loanId The loan to be deleted
     @return String the message indicating success or failure
     */
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
    /**
     Saves a loan to the database
     @param l The loan to be saved
     @return LoanEntity the loan saved
     */
    public LoanEntity save(LoanEntity l) {
        log.info("Start LoanService.LoanEntity(" + l + ")");
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
    /**
     Overwrites an existing loan with new data
     @param l the loan to update
     @return String the message indicating success or failure
     */
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
    /**
     This processes the fees of every loan in BeardTrust. I will check to see if the minimum due date has been reached,
     increment the minimum due amount,
     add a late fee when required,
     and increment the date.
     This is a part of the health check process.
     */
    public void processFees() {
        log.trace("Processing loan fees");
        List<LoanEntity> loans = repo.findAll();
        //set any late fees
        for (LoanEntity loan : loans) {
            if (loan.getPayment().getNextDueDate() == LocalDateTime.now()) {
                //loan pay date has passed, increment minimum due

                //First, get the current minimum due
                CurrencyValue c = loan.getPayment().getMinDue();

                //second, add the normal min due
                c.add(loan.getPayment().calculateMinDue(loan.getPrincipal(), loan.getLoanType().getNumMonths()));

                //third, set the new minimum due
                loan.getPayment().setMinDue(c);

                //fourth, check for a late fee.
                loan.getPayment().checkLate();

                //fifth, subtract a payment month
                loan.getLoanType().setNumMonths(loan.getLoanType().getNumMonths() - 1);

                //finally, increment the due date
                loan.getPayment().incrementDueDate();

                //Done, save our work...
                repo.save(loan);

                }
            }
        }
    /**
     Gets all of a user's loans as a Pageable object that sorts and filters based on params passed in.

     @param n the page number for the Pageable object
     @param s the page size for the Pageable object
     @param sortBy the array containing sort orders for the Pageable object
     @param search the String used for filtering the Pageable object
     @param userId the user to filter by
     @return Page</LoanEntity> the user's loans being returned.

     */
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

    /**

    Receives a CurrencyValue and String representing the value to be paid towards the loan of id.
    Late fees will automatically take out a cut, any resulting amount will go towards
    the minimum due. If the minimum die is cleared, the hasPaid status will be set to true.
    If the amount paid is more than amount owed, the remaining amount will be returned to the
    payment source

     @param c the payment amount
     @param id the id of the loan being paid on
     @return CurrencyValue any leftover money (depracated, transactions no longer allow payments above the loan balance.)

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
            return returnValue;
        } catch (Exception e) {
            log.warn("Payment process failed!!!");
            return null;
        }
    }
    /**
     Checks the late fee of an individual loan

     @param l The loan to check

     @return String the message indicating if a fee was applied
     */
    @Override
    public String lateFeeCheck(LoanEntity l) {
        if (l.getPayment().checkLate()) {
            return "Late fee applied.";
        }
        else {
            return "No late fee applied";
        }
    }

    /**
     This will iterate through every loan and ensure the minimum due is correctly set
     * */
    public void calculateMinDue() {
        List<LoanEntity> l = repo.findAll();
        for (int i = 0; i < l.size(); i++) {
            LoanEntity l1 = l.get(i);
            l1.getPayment().calculateMinDue(l1.getBalance(), l1.getLoanType().getNumMonths());
            repo.save(l1);
        }
    }
/**
 This accepts the sort direction in the form of a string and converts it to a proper Sort.Direction for use with parseOrders()

 @param direction the sort direction, in the form of a String
 @return Sort.Direction the properly parsed direction
 * */
    private Sort.Direction getSortDirection(String direction) {
        if (direction.equals("asc")) {
            return Sort.Direction.ASC;
        } else if (direction.equals("desc")) {
            return Sort.Direction.DESC;
        }

        return Sort.Direction.ASC;
    }
/**
 Sorting orders are sent in an array to be broken down and pieced back together into a Pageable Sort.Order list
 This allows for multiple fields to be sorted by at once It works in tandem with getSortDirection() to ensure proper oders

 @param sortBy the array containing the order(s)
 @return List</Sort.Order> The orders to put into a Pageable
 * */
    private List<Sort.Order> parseOrders(String[] sortBy) {
        List<Sort.Order> orders = new ArrayList<>();

        if (sortBy.length > 2) {
            for (int i = 0; i < sortBy.length; i++) {
                String sort = sortBy[i];
                String dir = sortBy[i + 1];
                orders.add(new Sort.Order(getSortDirection(dir), sort));
                i++;
            }
        } else {
            orders.add(new Sort.Order(getSortDirection(sortBy[1]), sortBy[0]));
        }
        return orders;
    }

    /**
     *
    Receives a CurrencyValue object representing the value to be paid towards the loan.
    Late fees will automatically take out a cut, any resulting amount will go towards
    the minimum due. If the minimum die is cleared, the hasPaid status will be set to true.
    If the amount paid is more than amount owed, the remaining amount will be returned to the
    payment source
     @param payment The amount being paid
     @param l The loan being paid on

     @return CurrencyValue the amount paid (not really used for anything)

     */
    public CurrencyValue processPayment(CurrencyValue payment, LoanEntity l) {
        log.trace("process payment service reached...");
        log.debug("Payment received: " + payment + ". Loan received: " + l.toString());
        l.getPayment().getMinDue().setNegative(false);
        CurrencyValue temp = new CurrencyValue(l.getPayment().getLateFee());
        if (l.getPayment().getLateFee().getDollars() > 0 || l.getPayment().getLateFee().getCents() > 0) {
            log.trace("Loan had a late fee to pay on...");
            log.debug("Late fee found: " + l.getPayment().getLateFee().toString());
            l.getPayment().getLateFee().add(payment);
            System.out.println("temp value: " + temp);
            payment.add(temp);
            if (l.getPayment().getLateFee().isNegative()) {
                l.getPayment().getLateFee().setDollars(0);
                l.getPayment().getLateFee().setCents(0);
                l.getPayment().getLateFee().setNegative(false);
            }
            System.out.println("payment after late fee: " + payment);
        }
        payment.setNegative(false);
        if (payment.compareTo(l.getBalance()) == 1) {//paynment is greater (transactionservice shouldn't allow this)
            log.trace("Payment was greater than the present balance...");
            log.debug("Payment received: " + payment + ". Loan balance: " + l.getBalance().toString());
            payment.setNegative(true);
            l.getPayment().getMinDue().add(payment);
            l.getBalance().add(payment);
            payment.setDollars(l.getBalance().getDollars());
            payment.setCents(l.getBalance().getCents());
            payment.setNegative(false);
            l.getBalance().setDollars(0);
            l.getBalance().setCents(0);
            l.getBalance().setNegative(false);
            l.getPayment().getMinDue().setDollars(0);
            l.getPayment().getMinDue().setCents(0);
            payment.setNegative(false);
            l.getPayment().setHasPaid(true);
            log.trace("Returning payment from payment processor...");
            log.debug("Payment to return: " + payment);
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
        }
        l.getPayment().checkLate();
        log.trace("Returning payment from payment processor...");
        log.debug("Payment to return: " + payment);
        return payment;
    }
    /**
     This method is used to run a credit check and determine your credit score for purposes of determining how much
     of a loan to offer. It currently has no external API calls to make, so it defaults to $1000

     @param id The user the loan will be offered to
     @param loan The type of loan the user is applying for

     @return LoanEntity The loan once the credit score has been checked

     */
    public LoanEntity creditCheck(String id, LoanTypeEntity loan) {
        log.info("Start LoanService.creditCheck(" + loan + ", " + id + ")");
        CurrencyValue c = new CurrencyValue();
        c.setDollars(1000);
        c.setCents(0);
        LoanEntity l = new LoanEntity();
        l.setLoanType(loan);
        l.setUser(ur.findById(id).get());
        CurrencyValue bal = calcBalance(c, loan.getApr());
        l.setPrincipal(c);
        l.setBalance(bal);
        System.out.println("setting min due...");
        PaymentEntity p = new PaymentEntity();
        p.calculateMinDue(l.getBalance(), l.getLoanType().getNumMonths());
        p.setLateFee(new CurrencyValue( false, 0, 0));
        p.setHasPaid(false);
        LocalDateTime n = LocalDateTime.now();
        n.plusDays(30);
        p.setNextDueDate(n);
        n.minusDays(60);
        p.setPreviousDueDate(n);
        l.setPayment(p);
        log.trace("End LoanService.creditCheck(" + loan + ", " + id + ")");
        return l;
    }
    /**
     Calculates the balance the user will have to pay back. multiplies the loan amount by the APR

     @param c The initial principal to base the balance on
     @param apr The interest rate a user has to pay back.

     @return CurrencyValue The resulting balance
     * */
    public CurrencyValue calcBalance(CurrencyValue c, Double apr) {
        log.trace("Start LoanService.principalCalc(" + c + ", " + apr + ")");
        CurrencyValue c2 = new CurrencyValue();
        c.setNegative(false);
        Integer p = 0;
        double v = c.getDollars() + c.getCents();
        double a = v * (1 + apr/100);
        int ce = (int) (a - Math.floor(a));
        int dol = (int) (a - (a - Math.floor(a)));
        log.trace("End LoanService.principalCalc(" + c + ", " + apr + ")");
        c2.add(dol, ce);
        return c2;
    }
}
