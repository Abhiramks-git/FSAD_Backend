package com.finvest.repository;

import com.finvest.entity.Fund;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FundRepository extends JpaRepository<Fund, String> {
    List<Fund> findByCategory(String category);
    List<Fund> findByRisk(String risk);
    List<Fund> findByNameContainingIgnoreCaseOrAmcContainingIgnoreCase(String name, String amc);
}
