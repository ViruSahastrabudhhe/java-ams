package com.dem_ams.repository;

import com.dem_ams.domain.model.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long>, PagingAndSortingRepository<Payment, Long> {
    @Query("SELECT p FROM Payment p WHERE p.id = :id and p.user.id = :user_id and p.deletedAt is null")
    Optional<Payment> findByIdAndUserId(@Param("id") Long id, @Param("user_id") Long userId);

    @Query("SELECT p FROM Payment p WHERE p.user.id = :user_id and p.deletedAt is null")
    Page<Payment> findByUserId(@Param("user_id") Long userId, PageRequest pageRequest);

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN TRUE ELSE FALSE END FROM Payment p WHERE p.id = :id and p.user.id = :user_id and p.deletedAt is null")
    boolean existsByIdAndUserId(@Param("id") Long id, @Param("user_id") Long userId);
}
