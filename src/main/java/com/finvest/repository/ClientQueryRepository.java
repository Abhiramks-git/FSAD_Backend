package com.finvest.repository;

import com.finvest.entity.ClientQuery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientQueryRepository extends JpaRepository<ClientQuery, Long> {
    List<ClientQuery> findByInvestorIdOrderByCreatedAtDesc(Long investorId);
    List<ClientQuery> findAllByOrderByCreatedAtDesc();
    long countByStatus(String status);
}
