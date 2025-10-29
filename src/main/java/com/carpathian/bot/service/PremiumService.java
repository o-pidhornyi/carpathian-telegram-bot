package com.carpathian.bot.service;

import com.carpathian.bot.model.PlaceCategory;
import com.carpathian.bot.model.PremiumPurchase;
import com.carpathian.bot.model.User;
import com.carpathian.bot.repository.PremiumPurchaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Service that records premium purchases and manages entitlements.
 * Integrates with {@link UserService} to grant entitlements upon
 * successful purchases.  Payment processing (e.g. interaction with
 * Telegram Payments) is assumed to occur elsewhere; this service
 * persists the result of a purchase.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class PremiumService {

    private final PremiumPurchaseRepository purchaseRepository;
    private final UserService userService;

    /**
     * Records a premium purchase for the given user and category.  A
     * corresponding entitlement is granted until the specified number
     * of days has elapsed.  Prices should be expressed in minor
     * currency units (e.g. cents).  Real payment handling is assumed
     * to be performed by the Telegram Payments API.
     *
     * @param user    the purchasing user
     * @param category the place category purchased
     * @param price   price in minor units
     * @param currency ISO currency code
     * @param days    validity period in days
     * @return the persisted purchase record
     */
    public PremiumPurchase recordPurchase(User user, PlaceCategory category, long price, String currency, int days) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiry = now.plusDays(days);
        // Grant the entitlement
        userService.grantEntitlement(user, category, expiry);
        // Persist the purchase
        PremiumPurchase purchase = PremiumPurchase.builder()
                .user(user)
                .category(category)
                .purchaseTime(now)
                .expiryTime(expiry)
                .price(price)
                .currency(currency)
                .build();
        return purchaseRepository.save(purchase);
    }
}