package com.beardtrust.webapp.loanservice.repos;

import com.beardtrust.webapp.loanservice.entities.LoanTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanTypeRepository extends JpaRepository<LoanTypeEntity, String> {

    @Modifying
    @Query(value = "update loan_types set isAvailable=false where id=?1", nativeQuery = true)
    void deactivateById(String cardId);

    @Override
    @Query(value = "select * from loan_types where isAvailable=true", nativeQuery = true)
    List<LoanTypeEntity> findAll();
}
