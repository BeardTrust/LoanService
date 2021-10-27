package com.beardtrust.webapp.loanservice.services;

import com.beardtrust.webapp.loanservice.entities.Balance;
import com.beardtrust.webapp.loanservice.entities.CurrencyValue;
import com.beardtrust.webapp.loanservice.entities.LoanEntity;
import com.beardtrust.webapp.loanservice.entities.LoanTypeEntity;
import com.beardtrust.webapp.loanservice.repos.LoanTypeRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import lombok.extern.slf4j.Slf4j;
import com.beardtrust.webapp.loanservice.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.apache.commons.lang.NumberUtils.isNumber;
import org.apache.commons.validator.GenericValidator;
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

    @Override
    public List<LoanTypeEntity> getAll() {
        log.trace("Start LoanTypeService.getAll()");
        log.trace("End LoanTypeService.getAll()");
        return repo.findAll();
    }

    @Override
    @Transactional
    public void deactivate(String id) {
        log.trace("Start LoanTypeService.deactivate(" + id + ")");
        repo.deactivateById(id);
        log.trace("End LoanTypeService.deactivate(" + id + ")");
    }

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

    private Sort.Direction getSortDirection(String direction) {
        log.trace("Start LoanTypeService.getSortDirection(" + direction + ")");
        Sort.Direction returnValue = Sort.Direction.ASC;

        if (direction.equals("desc")) {
            returnValue = Sort.Direction.DESC;
        }

        log.trace("End LoanTypeService.getSortDirection(" + direction + ")");
        return returnValue;
    }

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

    @Override
    public LoanEntity creditCheck(LoanTypeEntity loan, String id) {
        log.trace("Start LoanTypeService.creditCheck(" + loan + ", " + id + ")");
        CurrencyValue c = new CurrencyValue();
        c.setDollars(1000);
        c.setCents(0);
        LoanEntity l = new LoanEntity();
        l.setLoanType(loan);
        l.setUser(ur.findById(id).get());
        CurrencyValue bal = calcBalance(c, loan.getApr());
        l.setPrincipal(c);
        l.setBalance(bal);
        l.calculateMinDue();
        log.trace("End LoanTypeService.creditCheck(" + loan + ", " + id + ")");
        return l;
    }

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
