package com.beardtrust.webapp.loanservice.services;

import com.beardtrust.webapp.loanservice.entities.LoanTypeEntity;
import com.beardtrust.webapp.loanservice.repos.LoanTypeRepository;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Service
public class LoanTypeServiceImpl implements LoanTypeService{

    @Autowired
    LoanTypeRepository repo;

    @Override
    @Transactional
    public void save(LoanTypeEntity loanType){
        repo.save(loanType);
    }

    @Override
    public List<LoanTypeEntity> getAll() {
        return repo.findAll();
    }

    @Override
    public void deactivate(LoanTypeEntity loanType){
        repo.deactivateById(loanType.getId());
    }

    public Page<LoanTypeEntity> getAllLoanTypesPage(int pageNum, int pageSize, String[] sortBy, String search) {
        List<Sort.Order> orders = parseOrders(sortBy);
        System.out.println("Combined orders: " + orders);
        Pageable page = PageRequest.of(pageNum, pageSize, Sort.by(orders));
        System.out.println("Compiled page: " + page);
        System.out.println("Search param: " + search);
//        if (!("").equals(search)) {
//            if (isNumber(search)) {
//                System.out.println("search was a number");
//                Balance searchBalance = parseBalance(search);
//                return repo.findAllByPrincipalOrPayDayOrBalance_ValueIsLike(Integer.parseInt(search), searchBalance, page);
//            } else if (GenericValidator.isDate(search, "yyyy-MM", false)) {
//                System.out.println("search was a date");
//                return repo.findByCreateDate(LocalDate.parse(search), page);
//            } else {
//                return repo.findAllByLoanType_TypeNameIgnoreCase(search, page);
//            }
//        }
        return repo.findAll(page);
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

}
