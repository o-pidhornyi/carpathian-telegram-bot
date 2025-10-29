package com.carpathian.bot.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

/**
 * Entity representing a user interacting with the bot.  Users are
 * identified by their Telegram {@code chatId}.  Additional state
 * includes assigned roles, favourite houses and premium entitlements.
 */
@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long telegramId;

    private String name;

    /** Preferred locale of the user (e.g. "en", "uk"). */
    private String preferredLanguage;

    /** Roles assigned to this user (e.g. USER, CURATOR, ADMIN). */
    @ElementCollection(targetClass = UserRole.class)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private Set<UserRole> roles = new HashSet<>();

    /** Houses saved by this user. */
    @ManyToMany
    @JoinTable(name = "user_favourites",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "house_id"))
    private Set<House> favourites = new HashSet<>();

    /** Premium purchases made by the user. */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PremiumPurchase> purchases = new HashSet<>();

    /** Entitlements granted to the user. */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Entitlement> entitlements = new HashSet<>();
}