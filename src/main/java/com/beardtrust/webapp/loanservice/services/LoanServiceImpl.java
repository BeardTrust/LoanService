package com.beardtrust.webapp.loanservice.services;

import java.util.List;

import com.beardtrust.webapp.loanservice.entities.LoanEntity;
import com.beardtrust.webapp.loanservice.repos.LoanRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import static org.apache.commons.lang.NumberUtils.isNumber;
import org.apache.commons.validator.GenericValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class LoanServiceImpl implements LoanService {

    private LoanRepository repo;

    public LoanServiceImpl(LoanRepository repo) {
        this.repo = repo;
    }

    @Override
    public Page<LoanEntity> getAllLoans(Integer n, Integer s, String sortName, String sortDir, String search) {
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
                Integer newSearch = Integer.parseInt(search) * 100;
                return repo.findAllByPrincipalOrPaydayOrBalane_ValueIsLike(newSearch, newSearch, page);
            } else if (GenericValidator.isDate(search, "yyyy-MM", false)) {
                System.out.println("search was a date");
                return repo.findByCreateDate(LocalDate.parse(search), page);
            } else {
                return repo.findAllByLoanType_TypeNameIgnoreCase(search, page);
            }
        }
        return repo.findAll(page);
    }

    public Sort.Direction getDirection(String dir) {
        if ("asc".equals(dir)) {
            return Sort.Direction.ASC;
        } else {
            return Sort.Direction.DESC;
        }
    }

    @Override
    public LoanEntity getById(String Id) {
        // TODO Auto-generated method stub
        return repo.findAllById(Id);
    }

    public String deleteById(String id) {
        try {
            LoanEntity l = repo.findByLoanId(id);
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
    
    

}
