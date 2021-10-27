package com.beardtrust.webapp.loanservice.controllers;

import com.beardtrust.webapp.loanservice.entities.LoanEntity;
import com.beardtrust.webapp.loanservice.entities.LoanTypeEntity;
import com.beardtrust.webapp.loanservice.services.LoanTypeService;
import java.util.ArrayList;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;

@RestController
@RequestMapping("/loantypes")
@Slf4j
public class LoanTypeController {

    LoanTypeService loanTypeService;

    @Autowired
    public LoanTypeController(LoanTypeService loanTypeService) {
        this.loanTypeService = loanTypeService;
    }
    
    @PreAuthorize("permitAll()")
    @GetMapping("/new")
    public ResponseEntity<LoanTypeEntity> getNewUUID() {
        log.trace("Get new endpoint reached...");
        ResponseEntity<LoanTypeEntity> response = new ResponseEntity<>(loanTypeService.getNewLoanType(), HttpStatus.OK);
        log.info("Outbound loan type entity: " + response);
        return response;
    }

    @PostMapping()
    @PreAuthorize("hasAuthority('admin')")
    @Consumes({MediaType.ALL_VALUE, MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @Produces({MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public void createLoanType(@RequestBody LoanTypeEntity loanType){
        log.trace("Start LoanTypeController.createLoanType(" + loanType + ")");
        loanTypeService.save(loanType);
        log.trace("End LoanTypeController.createLoanType(" + loanType + ")");
    }
    
    @PostMapping("/{id}")
    @PreAuthorize("permitAll()")
    @Consumes({MediaType.ALL_VALUE, MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @Produces({MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<LoanEntity> creditCheck(@RequestBody LoanTypeEntity loan, @PathVariable String id){
        log.trace("Start LoanTypeController.creditCheck(" + loan + ", " + id + ")");
        ResponseEntity<LoanEntity> response = new ResponseEntity<>(loanTypeService.creditCheck(loan, id), HttpStatus.OK);
        log.trace("End LoanTypeController.creditCheck(" + loan + ", " + id + ")");
        return response;
    }
    
    @GetMapping
    @PreAuthorize("hasAuthority('admin')")
    @Consumes({MediaType.ALL_VALUE, MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @Produces({MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Page<LoanTypeEntity>> getAllLoanTypesPage(
            @RequestParam(name = "page", defaultValue = "0") int pageNumber,
            @RequestParam(name = "size", defaultValue = "10") int pageSize,
            @RequestParam(name = "sortBy", defaultValue = "id,asc") String[] sortBy,
            @RequestParam(name = "search", defaultValue = "") String search)
    {
        log.trace("Start LoanTypeController.getAllLoanTypesPage(" + pageNumber + ", " + pageSize + ", " + sortBy + ", " + search + ")");
        Pageable page = PageRequest.of(pageNumber, pageSize);
        ResponseEntity<Page<LoanTypeEntity>> response = new ResponseEntity<>(loanTypeService.getAllLoanTypesPage(pageNumber, pageSize, sortBy, search), HttpStatus.OK);
        log.trace("End LoanTypeController.getAllLoanTypesPage(" + pageNumber + ", " + pageSize + ", " + sortBy + ", " + search + ")");
        return response;

    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('admin')")
    @Consumes({MediaType.ALL_VALUE, MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @Produces({MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public List<LoanTypeEntity> getAllLoanTypes(){
        log.trace("Start LoanTypeController.getAllLoanTypes()");
        return loanTypeService.getAll();
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("permitAll()")
    @Consumes({MediaType.ALL_VALUE, MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @Produces({MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<LoanTypeEntity> getSpecificLoanType(@PathVariable String id){
        log.trace("Start LoanTypeController.getSpecificLoanType(" + id + ")");
        ResponseEntity<LoanTypeEntity> response = new ResponseEntity<>(loanTypeService.getSpecificLoanTypeEntity(id), HttpStatus.OK);
        log.trace("End LoanTypeController.getSpecificLoanType(" + id + ")");
        return response;
    }

    @PutMapping()
    @PreAuthorize("hasAuthority('admin')")
    @Consumes({MediaType.ALL_VALUE, MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @Produces({MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public void updateLoanType(@RequestBody LoanTypeEntity loanType){
        log.trace("Start LoanTypeController.updateLoanType(" + loanType + ")");
        loanTypeService.save(loanType);
        log.trace("End LoanTypeController.updateLoanType(" + loanType + ")");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('admin')")
    @Consumes({MediaType.ALL_VALUE, MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @Produces({MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public void deactivateLoanType(@PathVariable String id){
        log.trace("Start LoanTypeController.deactivateLoanType(" + id + ")");
        loanTypeService.deactivate(id);
        log.trace("End LoanTypeController.deactivateLoanType(" + id + ")");
    }
}
