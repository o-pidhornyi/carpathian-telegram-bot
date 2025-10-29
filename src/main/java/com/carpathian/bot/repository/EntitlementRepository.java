package com.carpathian.bot.repository;

import com.carpathian.bot.model.Entitlement;
import com.carpathian.bot.model.PlaceCategory;
import com.carpathian.bot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for {@link Entitlement} entities.  Provides lookup
 * operations based on user, category and expiry.
 */
@Repository
public interface EntitlementRepository extends JpaRepository<Entitlement, Long> {
    List<Entitlement> findByUser(User user);
    List<Entitlement> findByUserAndCategoryAndExpiresAtAfter(User user, PlaceCategory category, LocalDateTime now);
}