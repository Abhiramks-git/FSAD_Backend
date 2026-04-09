package com.finvest.repository;

import com.finvest.entity.Investment;
import com.finvest.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvestmentRepository extends JpaRepository<Investment, Long> {

    List<Investment> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Investment> findByUserId(Long userId);

    List<Investment> findByUserIdAndFundId(Long userId, String fundId);

    List<Investment> findByUserIdAndModeAndSipActive(Long userId, String mode, Boolean sipActive);

    long countByUserIdAndModeAndSipActive(Long userId, String mode, Boolean sipActive);

    // Correct enum-based JPQL — avoids string comparison bug with enum columns
    @Query("SELECT i FROM Investment i WHERE i.user.role = :role")
    List<Investment> findByUserRole(@Param("role") User.Role role);
}
