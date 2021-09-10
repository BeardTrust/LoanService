package com.beardtrust.webapp.loanservice.controllers;

import com.beardtrust.webapp.loanservice.entities.LoanEntity;
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
    
    @PostMapping("/{id}")//<-- userId
    @PreAuthorize("permitAll()")
    public ResponseEntity<LoanEntity> creditCheck(@RequestBody LoanTypeEntity loan, @PathVariable String id){
        System.out.println("credit check rcvd: " + loan + ", userId: " + id);
        ResponseEntity<LoanEntity> response = new ResponseEntity<>(loanTypeService.creditCheck(loan, id), HttpStatus.OK);
        return response;
    }
    
    @GetMapping
    public ResponseEntity<Page<LoanTypeEntity>> getAllLoanTypesPage(
            @RequestParam(name = "page", defaultValue = "0") int pageNumber,
            @RequestParam(name = "size", defaultValue = "10") int pageSize,
            @RequestParam(name = "sortBy", defaultValue = "id,asc") String[] sortBy,
            @RequestParam(name = "search", defaultValue = "") String search) 
        {
            System.out.println("inbound parameters: pagenum " + pageNumber + ", pagesize: " + pageSize + ", sortby: " + sortBy.toString() + ", search: " + search);
        Pageable page = PageRequest.of(pageNumber, pageSize);
        ResponseEntity<Page<LoanTypeEntity>> response = new ResponseEntity<>(loanTypeService.getAllLoanTypesPage(pageNumber, pageSize, sortBy, search), HttpStatus.OK);
        return response;

    }

    @GetMapping("/all")
    @PreAuthorize("permitAll()")
    public List<LoanTypeEntity> getAllLoanTypes(){
        return loanTypeService.getAll();
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<LoanTypeEntity> getSpecificLoanType(@PathVariable String id){
        ResponseEntity<LoanTypeEntity> response = new ResponseEntity<>(loanTypeService.getSpecificLoanTypeEntity(id), HttpStatus.OK);
        return response;
    }

    @PutMapping()
    @PreAuthorize("permitAll()")
    public void updateLoanType(@RequestBody LoanTypeEntity loanType){
        loanTypeService.save(loanType);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("permitAll()")
    public void deactivateLoanType(@PathVariable String id){
        loanTypeService.deactivate(id);
    }
}
