package com.beardtrust.webapp.loanservice.controllers;

import com.beardtrust.webapp.loanservice.entities.LoanEntity;
import com.beardtrust.webapp.loanservice.entities.LoanTypeEntity;
import com.beardtrust.webapp.loanservice.services.LoanTypeService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    /**
     * This method accepts an HTTP GET request on the /loantypess/new
     * endpoint and returns an empty LoanTypeEntity for the front end to
     * build on.
     *
     * @return a ResponseEntity<LoanTypeEntity> for the front end to build on.
     */
    @PreAuthorize("permitAll()")
    @GetMapping("/new")
    public ResponseEntity<LoanTypeEntity> getNewUUID() {
        log.trace("Get new endpoint reached...");
        ResponseEntity<LoanTypeEntity> response = new ResponseEntity<>(loanTypeService.getNewLoanType(), HttpStatus.OK);
        log.info("Outbound loan type entity: " + response);
        return response;
    }

    /**
     * This method accepts an HTTP POST request on the /loantypess
     * endpoint for adding a new loanType to the database
     *
     * @param loanType The entity to save to the database
     */
    @PostMapping()
    @PreAuthorize("hasAuthority('admin')")
    @Consumes({MediaType.ALL_VALUE, MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @Produces({MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public void createLoanType(@RequestBody LoanTypeEntity loanType) {
        log.info("Start LoanTypeController.createLoanType(" + loanType + ")");
        loanTypeService.save(loanType);
        log.trace("End LoanTypeController.createLoanType(" + loanType + ")");
    }

    /**
     * This method accepts an HTTP POST request on the /loantypes/{id}
     * endpoint and returns a new LoanEntity after performing a credit check
     * to offer the user
     *
     * @param id the user to create the loan for
     * @param loan The loan type that the user wants
     *
     * @return a ResponseEntity<LoanEntity> to offer the user
     */
    @PostMapping("/{id}")
    @PreAuthorize("permitAll()")
    @Consumes({MediaType.ALL_VALUE, MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @Produces({MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<LoanEntity> creditCheck(@RequestBody LoanTypeEntity loan, @PathVariable String id) {
        log.trace("Start LoanTypeController.creditCheck(" + loan + ", " + id + ")");
        ResponseEntity<LoanEntity> response = new ResponseEntity<>(loanTypeService.creditCheck(loan, id), HttpStatus.OK);
        log.trace("End LoanTypeController.creditCheck(" + loan + ", " + id + ")");
        return response;
    }

    /**
     * This method accepts an HTTP GET request on the /loantypes
     * endpoint and returns a Pageable of all loan types applicable
     * to the sorting and filtering included in the request
     * This is an user-level method intended to find ALL
     * loan types in the database.
     *
     * @param userId the user to search by
     * @param pageNumber The page number for the Pageable Object
     * @param pageSize The page size for the Pageable Object
     * @param search The search string for the Pageable Object
     * @param sortBy The sort order for the Pageable Object
     *
     * @return a ResponseEntity<Page</LoanTypeEntity>> The loan types found by the given criteria
     */
    @GetMapping
    @PreAuthorize("permitAll()")
    @Consumes({MediaType.ALL_VALUE, MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @Produces({MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Page<LoanTypeEntity>> getAllLoanTypesPage(
            @RequestParam(name = "page", defaultValue = "0") int pageNumber,
            @RequestParam(name = "size", defaultValue = "10") int pageSize,
            @RequestParam(name = "sortBy", defaultValue = "id,asc") String[] sortBy,
            @RequestParam(name = "search", defaultValue = "") String search,
            @RequestParam(name = "userId", defaultValue = "") String userId) {
        log.trace("Start LoanTypeController.getAllLoanTypesPage(" + pageNumber + ", " + pageSize + ", " + sortBy + ", " + search + ")");
        Pageable page = PageRequest.of(pageNumber, pageSize);
        ResponseEntity<Page<LoanTypeEntity>> response = new ResponseEntity<>(loanTypeService.getAllLoanTypesPage(pageNumber, pageSize, sortBy, search), HttpStatus.OK);
        log.trace("End LoanTypeController.getAllLoanTypesPage(" + pageNumber + ", " + pageSize + ", " + sortBy + ", " + search + ")");
        return response;

    }

    /**
     * This method accepts an HTTP GET request on the /loantypes/all
     * endpoint and returns a List of all loan types
     * This is an admin-level method intended to find ALL
     * loan types in the database.
     *
     * @return a ResponseEntity<List</LoanTypeEntity>> The loan types found
     */
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('admin')")
    @Consumes({MediaType.ALL_VALUE, MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @Produces({MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public List<LoanTypeEntity> getAllLoanTypes() {
        log.trace("Start LoanTypeController.getAllLoanTypes()");
        return loanTypeService.getAll();
    }

    /**
     * This method accepts an HTTP GET request on the /loantypes/{id}
     * endpoint and returns an individual loan type based on the given id
     *
     * @param id the loan type requested
     *
     * @return a ResponseEntity</LoanEntity> The loan type found
     */
    @GetMapping("/{id}")
    @PreAuthorize("permitAll()")
    @Consumes({MediaType.ALL_VALUE, MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @Produces({MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<LoanTypeEntity> getSpecificLoanType(@PathVariable String id) {
        log.trace("Start LoanTypeController.getSpecificLoanType(" + id + ")");
        ResponseEntity<LoanTypeEntity> response = new ResponseEntity<>(loanTypeService.getSpecificLoanTypeEntity(id), HttpStatus.OK);
        log.trace("End LoanTypeController.getSpecificLoanType(" + id + ")");
        return response;
    }

    /**
     * This method accepts an HTTP PUT request on the /loantypes
     * endpoint for updating an individual loan type
     *
     * @param loanType the loan type to update
     */
    @PutMapping()
    @PreAuthorize("hasAuthority('admin')")
    @Consumes({MediaType.ALL_VALUE, MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @Produces({MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public void updateLoanType(@RequestBody LoanTypeEntity loanType) {
        log.trace("Start LoanTypeController.updateLoanType(" + loanType + ")");
        loanTypeService.save(loanType);
        log.trace("End LoanTypeController.updateLoanType(" + loanType + ")");
    }

    /**
     * This method accepts an HTTP DELETE request on the /loantypes/{id}
     * endpoint and deactivates the individual loan type given
     * this is an admin-level method intended for database management purposes
     *
     * @param id the loan type requested
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('admin')")
    @Consumes({MediaType.ALL_VALUE, MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @Produces({MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public void deactivateLoanType(@PathVariable String id) {
        log.trace("Start LoanTypeController.deactivateLoanType(" + id + ")");
        loanTypeService.deactivate(id);
        log.trace("End LoanTypeController.deactivateLoanType(" + id + ")");
    }
}
