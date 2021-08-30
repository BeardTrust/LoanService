package com.beardtrust.webapp.loanservice.repos;

import com.beardtrust.webapp.loanservice.entities.LoanTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface LoanTypeRepository extends JpaRepository<LoanTypeEntity, String> {

    @Modifying
    @Query(value = "update loan_types set available=false where id=?1", nativeQuery = true)
    void deactivateById(String id);

    @Override
//    @Query(value = "select * from loan_types where available=true", nativeQuery = true)
    List<LoanTypeEntity> findAll();

    public Page<LoanTypeEntity> findAllBy(Pageable page);

    public Page<LoanTypeEntity> findAllByIdOrTypeNameContainsIgnoreCaseAndIsAvailableOrDescriptionContainsIgnoreCase(
            String searchId, String searchTypeName, boolean searchAvailable, String searchDescription, Pageable page);

    public Page<LoanTypeEntity> findAllByAprOrNumMonths(Integer newSearch, Integer newSearch0, Pageable page);
}
