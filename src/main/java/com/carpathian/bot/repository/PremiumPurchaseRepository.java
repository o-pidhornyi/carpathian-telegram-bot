package com.carpathian.bot.repository;

import com.carpathian.bot.model.PremiumPurchase;
import com.carpathian.bot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for {@link PremiumPurchase} entities.  Allows retrieval
 * of a user's purchase history.
 */
@Repository
public interface PremiumPurchaseRepository extends JpaRepository<PremiumPurchase, Long> {
    List<PremiumPurchase> findByUser(User user);
}