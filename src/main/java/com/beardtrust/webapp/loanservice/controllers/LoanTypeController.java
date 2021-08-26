package LoanService.src.main.java.com.beardtrust.webapp.loanservice.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/loantypes/")
public class LoanTypeController {

    @Autowired
    private final LoanTypeService loanTypeService;

    @PostMapping()
    @PreAuthorize("permitAll()")
    public void createLoanType(@RequestBody LoanTypeEntity loanType){
        loanTypeService.save(loanType);
    }

}
