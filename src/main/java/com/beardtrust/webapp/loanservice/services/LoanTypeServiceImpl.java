package com.beardtrust.webapp.loanservice.services;

import com.beardtrust.webapp.loanservice.entities.CurrencyValue;
import com.beardtrust.webapp.loanservice.entities.LoanEntity;
import com.beardtrust.webapp.loanservice.entities.LoanTypeEntity;
import com.beardtrust.webapp.loanservice.entities.PaymentEntity;
import com.beardtrust.webapp.loanservice.repos.LoanTypeRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import lombok.extern.slf4j.Slf4j;
import com.beardtrust.webapp.loanservice.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.apache.commons.lang.NumberUtils.isNumber;
import static org.apache.commons.validator.GenericValidator.isDouble;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Service
@Slf4j
public class LoanTypeServiceImpl implements LoanTypeService {

    @Autowired
    LoanTypeRepository repo;
    @Autowired
    UserRepository ur;
    
    public LoanTypeEntity getNewLoanType() {
        return new LoanTypeEntity();
    }

    /**
     Saves a loan type to the database

     @param loanType the loan type to save
     */
    @Override
    @Transactional
    public void save(LoanTypeEntity loanType) {
        log.trace("Start LoanTypeService.save(" + loanType + ")");
        loanType.setActiveStatus(true);
        if(loanType.getId() == null){
            loanType.setId(UUID.randomUUID().toString());
            log.trace("Create New Loan Type with id:" + loanType.getId());
        }
        repo.save(loanType);
        log.trace("End LoanTypeService.save(" + loanType + ")");
    }

    /**
     Retrieves a list of ALL loan types

     @return List</LoanTypeEntity> the list of all loans
     */
    @Override
    public List<LoanTypeEntity> getAll() {
        log.trace("Start LoanTypeService.getAll()");
        log.trace("End LoanTypeService.getAll()");
        return repo.findAll();
    }

    /**
     Deactivates the specified loan type

     @param id the loan type to deactivate
     */
    @Override
    @Transactional
    public void deactivate(String id) {
        log.trace("Start LoanTypeService.deactivate(" + id + ")");
        repo.deactivateById(id);
        log.trace("End LoanTypeService.deactivate(" + id + ")");
    }

    /**
     Gets all loan typess as a Pageable object that sorts and filters based on params passed in.

     @param pageNum the page number for the Pageable object
     @param pageSize the page size for the Pageable object
     @param sortBy the array containing sort orders for the Pageable object
     @param search the String used for filtering the Pageable object

     @return Page</LoanTypeEntity> the  loan types being returned.
     */
    @Override
    @Transactional
    public Page<LoanTypeEntity> getAllLoanTypesPage(int pageNum, int pageSize, String[] sortBy, String search) {
        log.trace("Start LoanTypeService.getAllLoanTypesPage(" + pageNum + ", " + pageSize + ", " + sortBy + ", " + search + ")");
        List<Sort.Order> orders = parseOrders(sortBy);
        Pageable page = PageRequest.of(pageNum, pageSize, Sort.by(orders));
        if (!("").equals(search)) {
            if (isNumber(search)) {
                if (isDouble(search)) {
                    Double newSearch = Double.parseDouble(search);
                    return repo.findAllByApr(newSearch, page);
                } else {
                    Integer newSearch = Integer.parseInt(search);
                    return repo.findAllByNumMonths(newSearch, newSearch, page);
                }
            } else {
                return repo.findAllByActiveStatusIsTrueAndIdOrTypeNameIgnoreCaseOrDescriptionContainsIgnoreCase(search, search, search, page);
            }
        }
        log.trace("End LoanTypeService.getAllLoanTypesPage(" + pageNum + ", " + pageSize + ", " + sortBy + ", " + search + ")");
        return repo.findAllByActiveStatusIsTrue(page);
    }

    /**
     Sorting orders are sent in an array to be broken down and pieced back together into a Pageable Sort.Order list
     This allows for multiple fields to be sorted by at once It works in tandem with getSortDirection() to ensure proper oders

     @param sortBy the array containing the order(s)

     @return List</Sort.Order> The orders to put into a Pageable
      * */
    private List<Sort.Order> parseOrders(String[] sortBy) {
        log.trace("Start LoanTypeService.parseOrders(" + sortBy + ")");
        List<Sort.Order> orders = new ArrayList<>();
        for (int i = 0; i < sortBy.length; i++) {
            System.out.println("sort order received: " + sortBy[i]);
        }

        if (sortBy[0].contains(",")) {
            for (String sortOrder : sortBy) {
                String[] _sortBy = sortOrder.split(",");
                orders.add(new Sort.Order(getSortDirection(_sortBy[1]), _sortBy[0]));
            }
        } else {
            orders.add(new Sort.Order(getSortDirection(sortBy[1]), sortBy[0]));
        }
        log.trace("End LoanTypeService.parseOrders(" + sortBy + ")");
        return orders;
    }

    /**
     This accepts the sort direction in the form of a string and converts it to a proper Sort.Direction for use with parseOrders()

     @param direction the sort direction, in the form of a String

     @return Sort.Direction the properly parsed direction
      * */
    private Sort.Direction getSortDirection(String direction) {
        log.trace("Start LoanTypeService.getSortDirection(" + direction + ")");
        Sort.Direction returnValue = Sort.Direction.ASC;

        if (direction.equals("desc")) {
            returnValue = Sort.Direction.DESC;
        }

        log.trace("End LoanTypeService.getSortDirection(" + direction + ")");
        return returnValue;
    }

    /**
     Retrieves a specific loan type based on the id provided

     @param id the loan type to return

     @return LoanTypeEntity the loan type found
     */
    @Override
    public LoanTypeEntity getSpecificLoanTypeEntity(String id) {
        log.trace("Start LoanTypeService.getSpecificLoanTypeEntity(" + id + ")");
        LoanTypeEntity lte = new LoanTypeEntity();
        lte.setApr(100.0);
        lte.setActiveStatus(false);
        lte.setDescription("ERROR - LOAN TYPE NOT FOUND. CONTACT ADMINISTRATOR.");
        lte.setNumMonths(0);
        lte.setTypeName("ERROR");
        try {
            Optional<LoanTypeEntity> ol = repo.findById(id);
            lte.setId(id);
            lte.setApr(ol.get().getApr());
            lte.setActiveStatus(true);
            lte.setDescription(ol.get().getDescription());
            lte.setNumMonths(ol.get().getNumMonths());
            lte.setTypeName(ol.get().getTypeName());
            return lte;
        } catch (Exception e) {
            log.info("Exception LoanTypeService.getSpecificLoanTypeEntity(" + id + ") \n"
            + e.getMessage());
        }
        log.trace("End LoanTypeService.getSpecificLoanTypeEntity(" + id + ")");
        return lte;
    }

    /**
     This method is used to run a credit check and determine your credit score for purposes of determining how much
     of a loan to offer. It currently has no external API calls to make, so it defaults to $1000

     @param id The user the loan will be offered to
     @param loan The type of loan type the user is applying for

     @return LoanEntity The loan once the credit score has been checked

     */
    @Override
    public LoanEntity creditCheck(LoanTypeEntity loan, String id) {
        log.info("Start LoanTypeService.creditCheck(" + loan + ", " + id + ")");
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
        try {
            l.getPayment().calculateMinDue(l.getBalance(), l.getLoanType().getNumMonths());
        } catch (NullPointerException e) {
            PaymentEntity p = new PaymentEntity();
            p.setHasPaid(false);
            p.setLateFee(new CurrencyValue(false, 0, 0));
            p.setNextDueDate(LocalDateTime.now().plusDays(30));
            p.setPreviousDueDate(LocalDateTime.now().minusDays(30));
            l.setPayment(p);
            l.getPayment().calculateMinDue(l.getBalance(), l.getLoanType().getNumMonths());
        }
        log.trace("End LoanTypeService.creditCheck(" + loan + ", " + id + ")");
        return l;
    }

    /**
     Calculates the balance the user will have to pay back. multiplies the loan amount by the APR

     @param c The initial principal to base the balance on
     @param apr The interest rate a user has to pay back.

     @return CurrencyValue The resulting balance
      **/
    public CurrencyValue calcBalance(CurrencyValue c, Double apr) {
        log.trace("Start LoanTypeService.principalCalc(" + c + ", " + apr + ")");
        CurrencyValue c2 = new CurrencyValue();
        c.setNegative(false);
        Integer p = 0;
        double v = c.getDollars() + c.getCents();
        double a = v * (1 + apr/100);
        int ce = (int) (a - Math.floor(a));
        int dol = (int) (a - (a - Math.floor(a)));
        log.trace("End LoanTypeService.principalCalc(" + c + ", " + apr + ")");
        c2.add(dol, ce);
        return c2;
    }

}
