package com.carpathian.bot.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Represents a premium purchase made by a user via Telegram Payments.
 * Purchases grant access (entitlements) to categories of places for a
 * limited time period.  The price is stored in minor currency units
 * (e.g. cents) and the currency is recorded separately.
 */
@Entity
@Table(name = "premium_purchases")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PremiumPurchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlaceCategory category;

    @Column(nullable = false)
    private LocalDateTime purchaseTime;

    @Column(nullable = false)
    private LocalDateTime expiryTime;

    /** Price expressed in minor currency units (e.g. cents). */
    @Column(nullable = false)
    private Long price;

    /** ISO 4217 currency code (e.g. "UAH", "EUR"). */
    @Column(nullable = false)
    private String currency;
}