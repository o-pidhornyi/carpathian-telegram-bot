package com.carpathian.bot.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Represents an entitlement granted to a user after a premium purchase.
 * Entitlements allow access to certain categories of places until the
 * expiration date.  They are derived from purchases but stored
 * explicitly for quick lookup.
 */
@Entity
@Table(name = "entitlements")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Entitlement {

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
    private LocalDateTime grantedAt;

    @Column(nullable = false)
    private LocalDateTime expiresAt;
}