package com.beardtrust.webapp.loanservice.controllers;

import com.beardtrust.webapp.loanservice.entities.CurrencyValue;
import com.beardtrust.webapp.loanservice.entities.LoanEntity;
import com.beardtrust.webapp.loanservice.services.LoanService;
import java.util.List;
import java.util.UUID;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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

@RestController
@RequestMapping("/loans")
@Slf4j
public class LoanController {

    private final LoanService ls;

    @Autowired
    public LoanController(@Qualifier("loanServiceImpl") LoanService ls) {
        this.ls = ls;
    }
    
    @PreAuthorize("permitAll()")
    @PostMapping("/new")
    public ResponseEntity<LoanEntity> getNewUUID(@RequestBody String userId) {
        log.trace("Get new endpoint reached...");
        String res = UUID.randomUUID().toString();
        ResponseEntity<LoanEntity> response = new ResponseEntity<>(ls.getNewLoan(userId), HttpStatus.OK);
        log.info("Outbound entity: " + response);
        return response;
    }

    @PreAuthorize("permitAll()")
    @PostMapping()
    @Consumes({MediaType.ALL_VALUE, MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @Produces({MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<LoanEntity> registerLoan(@RequestBody LoanEntity loan) {
        log.trace("Register new loan endpoint reached...");
        log.debug("Loan received for registration: " + loan);
        ResponseEntity<LoanEntity> response = new ResponseEntity<>(ls.save(loan), HttpStatus.ACCEPTED);
        log.trace("Returning from new loan registration");
		return response;
    }

    @PreAuthorize("hasRole('admin') or principal == #userId")
    @PostMapping("/{userId}/{id}")//<-- Loan to be paid on
    public ResponseEntity<CurrencyValue> payOnLoan(@PathVariable String id, @PathVariable String userId, @RequestBody CurrencyValue c) {
        log.trace("Pay on loan endpoint reached...");
        log.debug(("Loan id received: " + id + ". UserId received: " + userId + ". CurrencyValue received: " + c.toString()));
        ResponseEntity<CurrencyValue> response = new ResponseEntity<>(ls.makePayment(c, id), HttpStatus.ACCEPTED);
        return response;

    }

    @GetMapping
    @PreAuthorize("hasAuthority('admin')")
//    @PreAuthorize("permitAll()")
    public ResponseEntity<Page<LoanEntity>> getAllLoansPage(@RequestParam String pageNum, @RequestParam String pageSize, @RequestParam String sortName, @RequestParam String sortDir, @RequestParam String search) {//<-- Admin calls full list
        log.trace("Get all loans endpoint reached...");
        log.debug("Get all received: pageNum: " + pageNum + ". pageSize: " + pageSize + ". sortName: " + sortName + ". sortDir: " + sortDir + ". search: " + search);
        ResponseEntity<Page<LoanEntity>> response = new ResponseEntity<>(ls.getAllLoansPage(Integer.parseInt(pageNum), Integer.parseInt(pageSize), sortName, sortDir, search), HttpStatus.OK);
        log.debug("Get all returning: " + response);
        log.trace(("returning from get all loans..."));
        return response;
    }
    
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<List<LoanEntity>> getAllLoans() {//<-- Admin calls full list
        ResponseEntity<List<LoanEntity>> response = new ResponseEntity<>(ls.getAllLoans(), HttpStatus.OK);
        return response;
    }
    
//    @PreAuthorize("permitAll()")
    @PreAuthorize("hasRole('admin') or principal == #userId")
    @GetMapping("/me")
    public ResponseEntity<Page<LoanEntity>> getAllMyLoansPage(// <-- User calls personal list
            @RequestParam(name = "page", defaultValue = "0") int pageNum,
            @RequestParam(name = "size", defaultValue = "10") int pageSize,
            @RequestParam(name = "sortBy", defaultValue = "id,asc") String[] sortBy,
            @RequestParam(name = "search", defaultValue = "") String search,
            @RequestParam(name = "userId", defaultValue = "") String userId) {
        log.trace("Get my loans endpoint reached...");
        Pageable page = PageRequest.of(pageNum, pageSize);
        log.debug(("Page item received: " + page));
        log.trace(("Returning from get my loans controller"));
        ResponseEntity<Page<LoanEntity>> response = new ResponseEntity<>(ls.getAllMyLoansPage(pageNum, pageSize, sortBy, search, userId), HttpStatus.OK);
        return response;

    }
    
    @PutMapping()
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<String> updateLoan(@RequestBody LoanEntity a) {//<-- The entity with new/updated info
        ResponseEntity<String> response = new ResponseEntity<>(ls.updateLoan(a), HttpStatus.OK);
        return response;
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('admin')")
    public String deleteLoan(@PathVariable String id) {
        try {
            ls.deleteById(id);
            return "Remove successfull";
        } catch (Exception e) {
            return "Error finding Entity: " + e;
        }
    }

    @GetMapping("/calc")
    @PreAuthorize("permitAll()")
    public void calculateLoan() {
        ls.calculateMinDue();
    }
}
