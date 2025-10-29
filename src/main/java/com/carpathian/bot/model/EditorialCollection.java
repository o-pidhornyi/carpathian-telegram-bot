package com.carpathian.bot.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents an editorially curated collection of houses.  These
 * collections power features like “Top 5 houses for October” or
 * “Cozy winter cabins”.  Each collection can target a specific
 * season and includes a set of houses selected by curators.
 */
@Entity
@Table(name = "editorial_collections")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EditorialCollection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Human‑readable title for the collection (e.g. "Top 5 for October"). */
    @Column(nullable = false)
    private String title;

    /** Optional description for the collection. */
    @Column(length = 1024)
    private String description;

    /** Season for which this collection is relevant. */
    @Enumerated(EnumType.STRING)
    private SeasonTag season;

    /** Timestamp of when the collection was created. */
    private LocalDateTime createdAt;

    /** Houses included in this collection. */
    @ManyToMany
    @JoinTable(name = "editorial_collection_houses",
            joinColumns = @JoinColumn(name = "collection_id"),
            inverseJoinColumns = @JoinColumn(name = "house_id"))
    private Set<House> houses = new HashSet<>();
}