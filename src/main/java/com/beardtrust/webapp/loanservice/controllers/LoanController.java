package com.beardtrust.webapp.loanservice.controllers;

import com.beardtrust.webapp.loanservice.entities.LoanEntity;
import com.beardtrust.webapp.loanservice.services.LoanService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/loans")
@Slf4j
public class LoanController {

    private final LoanService ls;

    public LoanController(LoanService ls) {
        this.ls = ls;
    }
	
    @PreAuthorize("permitAll()")
    @GetMapping(path = "/health")
    @Consumes({MediaType.ALL_VALUE, MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
	public ResponseEntity<String> healthCheck() {
            log.info("Health Check Incoming");
        return new ResponseEntity<>("Healthy", HttpStatus.OK);
    }

    @PostMapping()
    @PreAuthorize("hasAuthority('admin')")
    @Consumes({MediaType.ALL_VALUE, MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @Produces({MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<LoanEntity> registerLoan(@RequestBody LoanEntity loan) {
        log.trace("Start LoanController.registerLoan(" + loan.toString() + ")");
        ResponseEntity<LoanEntity> response = new ResponseEntity<>(ls.save(loan), HttpStatus.ACCEPTED);
        log.trace("End LoanController.registerLoan(" + loan.toString() + ")");
		return response;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('admin')")
    @Consumes({MediaType.ALL_VALUE, MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @Produces({MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Page<LoanEntity>> getAllLoansPage(@RequestParam String pageNum, @RequestParam String pageSize, @RequestParam String sortName, @RequestParam String sortDir, @RequestParam String search) {
        log.trace("Start LoanController.getAllLoansPage(" + pageNum + ", " + pageSize + ", " + sortName + ", " + sortDir + ", " + search + ")");
        ResponseEntity<Page<LoanEntity>> response = new ResponseEntity<>(ls.getAllLoansPage(Integer.parseInt(pageNum), Integer.parseInt(pageSize), sortName, sortDir, search), HttpStatus.OK);
        log.trace("End LoanController.getAllLoansPage(" + pageNum + ", " + pageSize + ", " + sortName + ", " + sortDir + ", " + search + ")");
        return response;
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('admin')")
    @Consumes({MediaType.ALL_VALUE, MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @Produces({MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<LoanEntity>> getAllLoans() {
        log.trace("Start LoanController.getAllLoans()");
        ResponseEntity<List<LoanEntity>> response = new ResponseEntity<>(ls.getAllLoans(), HttpStatus.OK);
        log.trace("End LoanController.getAllLoans()");
        return response;
    }


    @GetMapping("/me")
    @PreAuthorize("hasAuthority('admin')")
    @Consumes({MediaType.ALL_VALUE, MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @Produces({MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Page<LoanEntity>> getAllMyLoansPage(
            @RequestParam(name = "page", defaultValue = "0") int pageNum, 
            @RequestParam(name = "size", defaultValue = "10") int pageSize,  
            @RequestParam(name = "sortBy", defaultValue = "loanId,asc") String[] sortBy, 
            @RequestParam(name = "search", defaultValue = "") String search) {
        log.trace("Start LoanController.getAllMyLoansPage(" + pageNum + ", " + pageSize + ", " + sortBy + ", " + search + ")");
        Pageable page = PageRequest.of(pageNum, pageSize);
        ResponseEntity<Page<LoanEntity>> response = new ResponseEntity<>(ls.getAllMyLoansPage(pageNum, pageSize, sortBy, search), HttpStatus.OK);
        log.trace("End LoanController.getAllMyLoansPage(" + pageNum + ", " + pageSize + ", " + sortBy + ", " + search + ")");
        return response;

    }
    
    @PutMapping()
    @PreAuthorize("hasAuthority('admin')")
    @Consumes({MediaType.ALL_VALUE, MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @Produces({MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<String> updateLoan(@RequestBody LoanEntity a) {
        log.trace("Start LoanController.updateLoan(" + a + ")");
        ResponseEntity<String> response = new ResponseEntity<>(ls.updateLoan(a), HttpStatus.OK);
        log.trace("End LoanController.updateLoan(" + a + ")");
        return response;
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('admin')")
    @Consumes({MediaType.ALL_VALUE, MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @Produces({MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public String deleteLoan(@PathVariable String id) {
        log.trace("Start LoanController.deleteLoan(" + id + ")");
        try {
            ls.deleteById(id);
            log.trace("End LoanController.deleteLoan(" + id + ")");
            return "Remove successfull";
        } catch (Exception e) {
            log.debug("Exception LoanController.deleteLoan(" + id + ")" + " \n" +
                    "     " + e);
            return "Error finding Entity: " + e;
        }
    }
}
