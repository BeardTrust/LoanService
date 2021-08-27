package com.beardtrust.webapp.loanservice.controllers;

import com.beardtrust.webapp.loanservice.entities.LoanTypeEntity;
import com.beardtrust.webapp.loanservice.services.LoanTypeService;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/loantypes")
public class LoanTypeController {

    LoanTypeService loanTypeService;

    @Autowired
    public LoanTypeController(LoanTypeService loanTypeService) {
        this.loanTypeService = loanTypeService;
    }

    @PostMapping()
    @PreAuthorize("permitAll()")
    public void createLoanType(@RequestBody LoanTypeEntity loanType){
        System.out.println("inbound loan type: " + loanType);
        loanTypeService.save(loanType);
    }
    
    @GetMapping
    public ResponseEntity<Page<LoanTypeEntity>> getAllLoanTypesPage(
            @RequestParam(name = "page", defaultValue = "0") int pageNumber,
            @RequestParam(name = "size", defaultValue = "10") int pageSize,
            @RequestParam(name = "sortBy", defaultValue = "loanId,asc") String[] sortBy,
            @RequestParam(name = "search", required = false) String search) 
        {
        Pageable page = PageRequest.of(pageNumber, pageSize);
        ResponseEntity<Page<LoanTypeEntity>> response = new ResponseEntity<>(loanTypeService.getAllLoanTypesPage(pageNumber, pageSize, sortBy, search), HttpStatus.OK);
        return response;

    }

    @GetMapping("/all")
    @PreAuthorize("permitAll()")
    public List<LoanTypeEntity> getAllLoanTypes(){
        return loanTypeService.getAll();
    }

    @PutMapping()
    @PreAuthorize("permitAll()")
    public void updateLoanType(@RequestBody LoanTypeEntity loanType){
        loanTypeService.save(loanType);
    }

    @DeleteMapping()
    @PreAuthorize("permitAll()")
    public void deactivateLoanType(@RequestBody LoanTypeEntity loanType){
        loanTypeService.deactivate(loanType);
    }
}
