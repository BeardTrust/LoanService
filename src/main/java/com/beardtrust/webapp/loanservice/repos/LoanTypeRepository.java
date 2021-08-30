package com.beardtrust.webapp.loanservice.repos;

import com.beardtrust.webapp.loanservice.entities.LoanTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

@Repository
public interface LoanTypeRepository extends JpaRepository<LoanTypeEntity, String> {

    @Modifying
    @Query(value = "update loan_types set activeStatus=false", nativeQuery = true)
    void deactivateById(String id);

    @Override
    @Query(value = "select * from loan_types where activeStatus=true", nativeQuery = true)
    Page<LoanTypeEntity> findAll(Pageable page);

    public Page<LoanTypeEntity> findAllBy(Pageable page);

    public Page<LoanTypeEntity> findAllByApr(Double newSearch, Pageable page);

    public Page<LoanTypeEntity> findAllByNumMonths(Integer newSearch, Integer newSearch0, Pageable page);

    public Page<LoanTypeEntity> findAllByAllIgnoreCaseIdOrTypeNameOrDescriptionAndActiveStatusIsTrue(
            String search, String search1, String search2, Pageable page);
    
    @Query("SELECT lt FROM LoanTypeEntity lt WHERE lt.activeStatus=1 AND lt.id LIKE %:search% OR lt.typeName LIKE %:search% OR lt.description LIKE %:search%")
    public Page<LoanTypeEntity> genericSearch(@Param("search") String search, Pageable page);
}
