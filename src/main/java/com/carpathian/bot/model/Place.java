package com.carpathian.bot.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a curated point of interest (premium place) that can be
 * unlocked via premium entitlements.  Places are categorised (e.g.
 * hiking trail, museum) and may be linked to multiple houses.  Season
 * tags indicate when the place is particularly attractive (e.g.
 * ski resorts in winter).
 */
@Entity
@Table(name = "places")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Place {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 1024)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlaceCategory category;

    private Double latitude;

    private Double longitude;

    /**
     * Season tags associated with this place.  A place may be suitable
     * for multiple seasons (e.g. hiking trail in summer and autumn).
     */
    @ElementCollection(targetClass = SeasonTag.class)
    @CollectionTable(name = "place_seasons", joinColumns = @JoinColumn(name = "place_id"))
    @Column(name = "season", nullable = false)
    @Enumerated(EnumType.STRING)
    private Set<SeasonTag> seasons = new HashSet<>();

    /** Houses for which this place is recommended. */
    @ManyToMany
    @JoinTable(name = "place_houses",
            joinColumns = @JoinColumn(name = "place_id"),
            inverseJoinColumns = @JoinColumn(name = "house_id"))
    private Set<House> houses = new HashSet<>();
}