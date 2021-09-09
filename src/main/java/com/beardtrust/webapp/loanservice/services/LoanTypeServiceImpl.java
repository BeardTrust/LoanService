package com.beardtrust.webapp.loanservice.services;

import com.beardtrust.webapp.loanservice.entities.Balance;
import com.beardtrust.webapp.loanservice.entities.CurrencyValue;
import com.beardtrust.webapp.loanservice.entities.LoanEntity;
import com.beardtrust.webapp.loanservice.entities.LoanTypeEntity;
import com.beardtrust.webapp.loanservice.repos.LoanTypeRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import static org.apache.commons.lang.NumberUtils.isNumber;
import org.apache.commons.validator.GenericValidator;
import static org.apache.commons.validator.GenericValidator.isDouble;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Service
public class LoanTypeServiceImpl implements LoanTypeService {

    @Autowired
    LoanTypeRepository repo;

    @Override
    @Transactional
    public void save(LoanTypeEntity loanType) {
        loanType.generateId();
        repo.save(loanType);
    }

    @Override
    public List<LoanTypeEntity> getAll() {
        return repo.findAll();
    }

    @Override
    public void deactivate(LoanTypeEntity loanType) {
//        repo.deactivateById(loanType.getId());
    }

    @Override
    public Page<LoanTypeEntity> getAllLoanTypesPage(int pageNum, int pageSize, String[] sortBy, String search) {
        List<Sort.Order> orders = parseOrders(sortBy);
        System.out.println("Combined orders: " + orders);
        Pageable page = PageRequest.of(pageNum, pageSize, Sort.by(orders));
        System.out.println("Compiled page: " + page);
        System.out.println("Search param: " + search);
        if (!("").equals(search)) {
            if (isNumber(search)) {
                if (isDouble(search)) {
                    Double newSearch = Double.parseDouble(search);
                    return repo.findAllByApr(newSearch, page);
                } else {
                    System.out.println("search was a number");
                    Integer newSearch = Integer.parseInt(search);
                    return repo.findAllByNumMonths(newSearch, newSearch, page);
                }
            } else {
                System.out.println("generic search parameter");
                return repo.findAllByActiveStatusIsTrueAndIdOrTypeNameIgnoreCaseOrDescriptionContainsIgnoreCase(search, search, search, page);
            }
        }
        System.out.println("find all");
        System.out.println("all loantypes in repo: " + repo.findAll());
        return repo.findAllByActiveStatusIsTrue(page);
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

        return orders;
    }

    private Sort.Direction getSortDirection(String direction) {
        Sort.Direction returnValue = Sort.Direction.ASC;

        if (direction.equals("desc")) {
            returnValue = Sort.Direction.DESC;
        }

        return returnValue;
    }

    @Override
    public LoanTypeEntity getSpecificLoanTypeEntity(String id) {
        System.out.println("get specific loantype request. inbound id: " + id);
        LoanTypeEntity lte = new LoanTypeEntity();
        lte.setApr(100.0);
        lte.setActiveStatus(false);
        lte.setDescription("ERROR - LOAN TYPE NOT FOUND. CONTACT ADMINISTRATOR.");
        lte.setNumMonths(0);
        lte.setTypeName("ERROR");
        try {
            Optional<LoanTypeEntity> ol = repo.findById(id);
            System.out.println("loan type entity found: " + ol.get());
            lte.setId(id);
            lte.setApr(ol.get().getApr());
            lte.setActiveStatus(true);
            lte.setDescription(ol.get().getDescription());
            lte.setNumMonths(ol.get().getNumMonths());
            lte.setTypeName(ol.get().getTypeName());
            return lte;
        } catch (Exception e) {
            System.out.println("exception caught: " + e.getMessage());
        }
        System.out.println("returning: " + lte);
        return lte;
    }

    @Override
    public LoanEntity creditCheck(LoanTypeEntity loan, String id) {
        System.out.println("attempting credit check");
        CurrencyValue c = new CurrencyValue();
        c.setDollars(1000);
        c.setCents(0);
        LoanEntity l = new LoanEntity();
        l.setCurrencyValue(c);
        l.setLoanType(loan);
        Integer prince = principalCalc(c, loan.getApr());
        l.setPrincipal(prince);
        l.setCurrencyValue(c);
        l.setUserId(id);
        return l;
    }
    
    public Integer principalCalc(CurrencyValue c, Double apr) {
        CurrencyValue c2 = new CurrencyValue();
        Integer p = 0;
        double v = c.getDollars() + c.getCents();
        double a = v * (1 + apr/100);
        int ce = (int) (a - Math.floor(a));
        int dol = (int) (a - (a - Math.floor(a)));
        System.out.println("making principal with: $" + dol + " and $0." + ce);
        c2.add(dol, ce);
        return c2.getDollars() + c2.getCents();
    }

}
