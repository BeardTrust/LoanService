package com.beardtrust.webapp.loanservice.controllers;

import com.beardtrust.webapp.loanservice.entities.LoanEntity;
import com.beardtrust.webapp.loanservice.entities.LoanTypeEntity;
import com.beardtrust.webapp.loanservice.services.LoanTypeService;
import java.util.ArrayList;

import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@RequestMapping("/loantypes")
public class LoanTypeController {

    LoanTypeService loanTypeService;

    @Autowired
    public LoanTypeController(LoanTypeService loanTypeService) {
        this.loanTypeService = loanTypeService;
    }

    @PostMapping()
    @PreAuthorize("hasAutority('admin')")
    public void createLoanType(@RequestBody LoanTypeEntity loanType){
        log.trace("Start LoanTypeController.createLoanType(" + loanType + ")");
        //System.out.println("inbound loan type: " + loanType);
        loanTypeService.save(loanType);
        log.trace("End LoanTypeController.createLoanType(" + loanType + ")");
    }
    
    @PostMapping("/{id}")//<-- userId
    @PreAuthorize("permitAll()")
    public ResponseEntity<LoanEntity> creditCheck(@RequestBody LoanTypeEntity loan, @PathVariable String id){
        log.trace("Start LoanTypeController.creditCheck(" + loan + ", " + id + ")");
        //System.out.println("credit check rcvd: " + loan + ", userId: " + id);
        ResponseEntity<LoanEntity> response = new ResponseEntity<>(loanTypeService.creditCheck(loan, id), HttpStatus.OK);
        log.trace("End LoanTypeController.creditCheck(" + loan + ", " + id + ")");
        return response;
    }
    
    @GetMapping
    @PreAuthorize("hasAutority('admin')")
    public ResponseEntity<Page<LoanTypeEntity>> getAllLoanTypesPage(
            @RequestParam(name = "page", defaultValue = "0") int pageNumber,
            @RequestParam(name = "size", defaultValue = "10") int pageSize,
            @RequestParam(name = "sortBy", defaultValue = "id,asc") String[] sortBy,
            @RequestParam(name = "search", defaultValue = "") String search)
    {
        log.trace("Start LoanTypeController.getAllLoanTypesPage(" + pageNumber + ", " + pageSize + ", " + sortBy + ", " + search + ")");
        //System.out.println("inbound parameters: pagenum " + pageNumber + ", pagesize: " + pageSize + ", sortby: " + sortBy.toString() + ", search: " + search);
        Pageable page = PageRequest.of(pageNumber, pageSize);
        ResponseEntity<Page<LoanTypeEntity>> response = new ResponseEntity<>(loanTypeService.getAllLoanTypesPage(pageNumber, pageSize, sortBy, search), HttpStatus.OK);
        log.trace("End LoanTypeController.getAllLoanTypesPage(" + pageNumber + ", " + pageSize + ", " + sortBy + ", " + search + ")");
        return response;

    }

    @GetMapping("/all")
    @PreAuthorize("hasAutority('admin')")
    public List<LoanTypeEntity> getAllLoanTypes(){
        log.trace("Start LoanTypeController.getAllLoanTypes()");
        return loanTypeService.getAll();
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<LoanTypeEntity> getSpecificLoanType(@PathVariable String id){
        log.trace("Start LoanTypeController.getSpecificLoanType(" + id + ")");
        ResponseEntity<LoanTypeEntity> response = new ResponseEntity<>(loanTypeService.getSpecificLoanTypeEntity(id), HttpStatus.OK);
        log.trace("End LoanTypeController.getSpecificLoanType(" + id + ")");
        return response;
    }

    @PutMapping()
    @PreAuthorize("hasAutority('admin')")
    public void updateLoanType(@RequestBody LoanTypeEntity loanType){
        log.trace("Start LoanTypeController.updateLoanType(" + loanType + ")");
        loanTypeService.save(loanType);
        log.trace("End LoanTypeController.updateLoanType(" + loanType + ")");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAutority('admin')")
    public void deactivateLoanType(@PathVariable String id){
        log.trace("Start LoanTypeController.deactivateLoanType(" + id + ")");
        loanTypeService.deactivate(id);
        log.trace("End LoanTypeController.deactivateLoanType(" + id + ")");
    }
}
