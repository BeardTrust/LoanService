package LoanService.src.main.java.com.beardtrust.webapp.loanservice.controllers;

import com.beardtrust.webapp.loanservice.entities.LoanTypeEntity;
import com.beardtrust.webapp.loanservice.services.LoanTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/loantypes/")
public class LoanTypeController {

    LoanTypeService loanTypeService;

    @Autowired
    public LoanTypeController(LoanTypeService loanTypeService) {
        this.loanTypeService = loanTypeService;
    }

    @PostMapping()
    @PreAuthorize("permitAll()")
    public void createLoanType(@RequestBody LoanTypeEntity loanType){
        loanTypeService.save(loanType);
    }

    @GetMapping()
    @PreAuthorize("permitAll()")
    public List<LoanTypeEntity> getAllLoanTypes(){
        return loanTypeService.getAll();
    }
}
