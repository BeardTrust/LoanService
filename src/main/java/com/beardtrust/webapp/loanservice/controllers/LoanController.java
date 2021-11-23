package com.beardtrust.webapp.loanservice.controllers;

import com.beardtrust.webapp.loanservice.entities.CurrencyValue;
import com.beardtrust.webapp.loanservice.entities.LoanEntity;
import com.beardtrust.webapp.loanservice.entities.LoanTypeEntity;
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

    /**
     * This method accepts an HTTP GET request on the /loans/health
     * endpoint and returns a String, "Healthy," and a status code of 200
     * if healthy or a String, "Unhealthy," and a status code of 503 if
     * not healthy.
     *
     * @return a ResponseEntity<String> with HttpStatus.OK
     */
    @PreAuthorize("permitAll()")
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        log.trace("Health check endpoint reached...");
        String status = ls.healthCheck();
        ResponseEntity<String> response;

        if (status.equals("Healthy")) {
            log.trace("Controller found service healthy");
            response = new ResponseEntity<>(status, HttpStatus.OK);
        } else {
            log.warn("Controller found service unhealthy");
            response = new ResponseEntity<>(status, HttpStatus.SERVICE_UNAVAILABLE);
        }
        log.trace("Controller returning health status...");
        return  response;
    }

    /**
     * This method accepts an HTTP POST request on the /loans/new
     * endpoint and returns an empty LoanEntity for the front end to
     * build on.
     *
     * @param userId the user to create the loan for
     *
     * @return a ResponseEntity<LoanEntity> for the front end to build on.
     */
    @PreAuthorize("permitAll()")
    @PostMapping("/new")
    public ResponseEntity<LoanEntity> getNewUUID(@RequestBody String userId) {
        log.trace("Get new endpoint reached...");
        String res = UUID.randomUUID().toString();
        ResponseEntity<LoanEntity> response = new ResponseEntity<>(ls.getNewLoan(userId), HttpStatus.OK);
        log.info("Outbound entity: " + response);
        return response;
    }

    /**
     * This method accepts an HTTP POST request on the /loans/check/{userId}
     * endpoint and returns a new LoanEntity after performing a credit check
     * to offer the user
     *
     * @param userId the user to create the loan for
     * @param l The loan type that the user wants
     *
     * @return a ResponseEntity<LoanEntity> to offer the user
     */
    @PostMapping("/check/{userId}")
    @PreAuthorize("hasAuthority('admin') or principal == #userId")
    @Consumes({MediaType.ALL_VALUE, MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @Produces({MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<LoanEntity> creditCheck(@RequestBody LoanTypeEntity l, @PathVariable(name = "userId") String userId) {
        log.info("Start LoanController.creditCheck(" + userId + ")");
        ResponseEntity<LoanEntity> response = new ResponseEntity<>(ls.creditCheck(userId, l), HttpStatus.OK);
        log.trace("End LoanController.registerLoan(" + userId + ")");
        return response;
    }

    /**
     * This method accepts an HTTP POST request on the /loans endpoint
     * and returns a new LoanEntity after saving the new loan.
     *
     * @param userId the userid for authentication
     * @param loan The loan to save
     *
     * @return a ResponseEntity<LoanEntity> to offer the user
     */
    @PostMapping()
    @PreAuthorize("hasAuthority('admin') or principal == #userId")
    @Consumes({MediaType.ALL_VALUE, MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @Produces({MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<LoanEntity> registerLoan(@RequestBody LoanEntity loan, @RequestParam(name = "userId", defaultValue = "") String userId) {
        log.trace("Start LoanController.registerLoan(" + loan.toString() + ")");
        ResponseEntity<LoanEntity> response = new ResponseEntity<>(ls.save(loan), HttpStatus.ACCEPTED);
        log.trace("End LoanController.registerLoan(" + loan.toString() + ")");
		return response;
    }

    /**
     * This method accepts an HTTP POST request on the /loans/{userId}/{id}
     * endpoint to make a payment towards a loan and returns a
     * CurrencyValue of any remainder after a successful payment.
     * Tranasctionservice no longer allows for larger payments than the
     * current balance, so this should no longer return more than 0.
     *
     * @param userId the user to create the loan for
     * @param id The loan to pay on
     * @param c The amount of the payment
     *
     * @return a ResponseEntity<CurrencyValue> The remaining payment
     */
    @PreAuthorize("hasRole('admin') or principal == #userId")
    @PostMapping("/{userId}/{id}")//<-- Loan to be paid on
    @Consumes({MediaType.ALL_VALUE, MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @Produces({MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<CurrencyValue> payOnLoan(@PathVariable String id, @PathVariable String userId, @RequestBody CurrencyValue c) {
        log.trace("Pay on loan endpoint reached...");
        log.debug(("Loan id received: " + id + ". UserId received: " + userId + ". CurrencyValue received: " + c.toString()));
        ResponseEntity<CurrencyValue> response = new ResponseEntity<>(ls.makePayment(c, id), HttpStatus.ACCEPTED);
        return response;

    }

    /**
     * This method accepts an HTTP GET request on the /loans
     * endpoint and returns a Pageable of all loans applicable
     * to the sorting and filtering included in the request
     * This is an admin-level method intended to find ALL
     * loans in the database.
     *
     * @param userId the user to search by
     * @param pageNum The page number for the Pageable Object
     * @param pageSize The page size for the Pageable Object
     * @param search The search string for the Pageable Object
     * @param sortBy The sort order for the Pageable Object
     *
     * @return a ResponseEntity<Page</LoanEntity>> The loans found by the given criteria
     */
    @GetMapping
    @PreAuthorize("hasAuthority('admin')")
    @Consumes({MediaType.ALL_VALUE, MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @Produces({MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Page<LoanEntity>> getAllLoansPage(
            @RequestParam(name = "pageNum", defaultValue = "0") int pageNum,
            @RequestParam(name = "pageSize", defaultValue = "5") int pageSize,
            @RequestParam(name = "sortBy", defaultValue = "id,asc") String[] sortBy,
            @RequestParam(name = "search", defaultValue = "") String search,
            @RequestParam(name = "userId", defaultValue = "") String userId){
        log.info("Start LoanController.getAllLoansPage(" + pageNum + ", " + pageSize + ", " + sortBy + ", " + search + ")");
        ResponseEntity<Page<LoanEntity>> response = new ResponseEntity<>(ls.getAllLoansPage(pageNum, pageSize, sortBy, search), HttpStatus.OK);
        log.trace("End LoanController.getAllLoansPage(" + pageNum + ", " + pageSize + ", " + sortBy + ", " + search + ")");
        return response;
    }

    /**
     * This method accepts an HTTP GET request on the /loans/all
     * endpoint and returns a List of all loans applicable
     * This is an admin-level method intended to find ALL
     * loans in the database.
     *
     * @return a ResponseEntity<List</LoanEntity>> The loans found
     */
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

    /**
     * This method accepts an HTTP GET request on the /loans/me
     * endpoint and returns a Pageable of All loans applicable
     * to the sorting and filtering included in the request
     * This is an user-level method intended to find ALL
     * loans in the database owned by said user.
     *
     * @param userId the user requesting their loans
     * @param pageNum The page number for the Pageable Object
     * @param pageSize The page size for the Pageable Object
     * @param search The search string for the Pageable Object
     * @param sortBy The sort order for the Pageable Object
     *
     * @return a ResponseEntity<Page</LoanEntity>> The loans found by the given criteria
     */
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

    /**
     * This method accepts an HTTP PUT request on the /loans
     * endpoint and for updating a Loan's information with
     * the incoming information. It returns a response String indicating success or failure
     * This is an admin-level method intended for an employee to use for customer assistance
     *
     * @param a The loan entity containing the updated information
     *
     * @return a ResponseEntity</String> Success/Failure message
     */
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

    /**
     * This method accepts an HTTP PUT request on the /loans/late
     * endpoint and for checking loan's late payment status
     * It returns a response String indicating success or failure
     * This is an admin-level method intended for an employee to use for customer assistance
     *
     * @param a The loan entity containing the updated information
     *
     * @return a ResponseEntity</String> Success/Failure message
     */
    @PutMapping("/late")
    @PreAuthorize("hasAuthority('admin')")
    @Consumes({MediaType.ALL_VALUE, MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @Produces({MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<String> checkLateFee(@RequestBody LoanEntity a) {
        log.info("Start LoanController.updateLoan(" + a + ")");
        ResponseEntity<String> response = new ResponseEntity<>(ls.lateFeeCheck(a), HttpStatus.OK);
        log.trace("End LoanController.updateLoan(" + a + ")");
        return response;
    }

    /**
     * This method accepts an HTTP DELETE request on the /loans/{id}
     * endpoint and for removing a loan from the database.
     * This is an admin-level method intended for an employee to use for customer assistance
     *
     * @param id The loan entity to remove
     *
     * @return a ResponseEntity</String> Success/Failure message
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('admin')")
    @Consumes({MediaType.ALL_VALUE, MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @Produces({MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<String> deleteLoan(@PathVariable String id) {
        log.trace("Start LoanController.deleteLoan(" + id + ")");
        ResponseEntity<String> response;
        try {
            response = new ResponseEntity<>(ls.deleteById(id), HttpStatus.OK);
            log.trace("End LoanController.deleteLoan(" + id + ")");
            return response;
        } catch (Exception e) {
            log.debug("Exception LoanController.deleteLoan(" + id + ")" + " \n" +
                    "     " + e);
            return new ResponseEntity<>("Loan not found!!!", HttpStatus.NO_CONTENT);
        }
    }

    /**
     * This method accepts an HTTP GET request on the /loans/calc
     * endpoint and for repairing loans that lack a proper minimum due
     */
    @GetMapping("/calc")
    @PreAuthorize("permitAll()")
    public void calculateLoan() {
        ls.calculateMinDue();
    }
}
