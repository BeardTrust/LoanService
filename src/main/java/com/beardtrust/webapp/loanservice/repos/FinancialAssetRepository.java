package com.beardtrust.webapp.loanservice.repos;

import com.beardtrust.webapp.loanservice.entities.FinancialAsset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FinancialAssetRepository extends JpaRepository<FinancialAsset, String> {
}
