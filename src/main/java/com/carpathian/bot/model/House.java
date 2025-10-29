package com.carpathian.bot.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * JPA entity representing a rental house in the Carpathians.  Houses
 * expose basic attributes along with associated media and amenities.
 * They may belong to zero or more editorial collections.  Availability
 * indicates whether the house is currently bookable (bookings are not
 * implemented in this demo) or on hiatus.
 */
@Entity
@Table(name = "houses")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class House {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String region;

    private String city;

    /** Maximum number of guests this house supports. */
    private Integer capacity;

    /** Size of the house in square metres (optional). */
    private Integer size;

    @Column(length = 1024)
    private String description;

    /** Whether the house is currently available for rent. */
    private boolean available = true;

    /** Photos and videos associated with this house. */
    @OneToMany(mappedBy = "house", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Media> media = new ArrayList<>();

    /** List of amenities present in this house. */
    @ElementCollection(targetClass = Amenity.class)
    @CollectionTable(name = "house_amenities", joinColumns = @JoinColumn(name = "house_id"))
    @Column(name = "amenity", nullable = false)
    @Enumerated(EnumType.STRING)
    private Set<Amenity> amenities = new HashSet<>();

    /**
     * Many‑to‑many relationship with editorial collections.  A house can
     * appear in multiple collections, and a collection can contain
     * multiple houses.
     */
    @ManyToMany(mappedBy = "houses")
    private Set<EditorialCollection> editorialCollections = new HashSet<>();
}