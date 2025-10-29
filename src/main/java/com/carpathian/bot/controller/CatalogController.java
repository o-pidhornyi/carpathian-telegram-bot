package com.carpathian.bot.controller;

import com.carpathian.bot.model.Amenity;
import com.carpathian.bot.model.House;
import com.carpathian.bot.service.CatalogService;
import com.carpathian.bot.service.HouseService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * REST controller exposing search and retrieval operations for houses.
 * Clients may filter by region, city, number of guests, size and
 * amenities via query parameters.  Amenity names should match the
 * {@link Amenity} enumeration names (case insensitive).
 */
@RestController
@RequestMapping("/api/houses")
@RequiredArgsConstructor
public class CatalogController {

    private final CatalogService catalogService;
    private final HouseService houseService;

    @GetMapping
    public List<House> searchHouses(
            @RequestParam(required = false) String region,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) @Min(1) @Max(20) Integer guests,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String amenities) {
        Set<Amenity> amenitySet = Collections.emptySet();
        if (amenities != null && !amenities.isBlank()) {
            amenitySet = new HashSet<>(
                    List.of(amenities.split(","))
                            .stream()
                            .map(String::trim)
                            .filter(s -> !s.isEmpty())
                            .map(String::toUpperCase)
                            .map(Amenity::valueOf)
                            .collect(Collectors.toSet())
            );
        }
        return catalogService.search(region, city, guests, size, amenitySet);
    }

    @GetMapping("/{id}")
    public ResponseEntity<House> getHouse(@PathVariable Long id) {
        return houseService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}