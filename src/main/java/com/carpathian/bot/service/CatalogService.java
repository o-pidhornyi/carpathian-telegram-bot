package com.carpathian.bot.service;

import com.carpathian.bot.model.Amenity;
import com.carpathian.bot.model.House;
import com.carpathian.bot.model.SeasonTag;
import com.carpathian.bot.repository.HouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 * Service responsible for retrieving and filtering houses.  Supports
 * dynamic filtering by region, city, number of guests, size and
 * amenities using JPA Specifications.  Also provides helpers for
 * editorial collections via the EditorialService.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CatalogService {

    private final HouseRepository houseRepository;

    /**
     * Searches for houses matching the provided filters.  Any null
     * parameter is ignored.  Amenity filtering requires all listed
     * amenities to be present on a house.
     *
     * @param region  region to filter by (optional)
     * @param city    city to filter by (optional)
     * @param guests  minimum capacity (optional)
     * @param size    minimum size in square metres (optional)
     * @param amenities set of required amenities (optional)
     * @return list of houses satisfying the criteria
     */
    public List<House> search(String region, String city, Integer guests, Integer size, Set<Amenity> amenities) {
        Specification<House> spec = Specification.where(null);
        if (region != null && !region.isBlank()) {
            spec = spec.and((root, query, cb) -> cb.equal(cb.lower(root.get("region")), region.toLowerCase()));
        }
        if (city != null && !city.isBlank()) {
            spec = spec.and((root, query, cb) -> cb.equal(cb.lower(root.get("city")), city.toLowerCase()));
        }
        if (guests != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("capacity"), guests));
        }
        if (size != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("size"), size));
        }
        if (amenities != null && !amenities.isEmpty()) {
            for (Amenity amenity : amenities) {
                spec = spec.and((root, query, cb) -> cb.isMember(amenity, root.get("amenities")));
            }
        }
        return houseRepository.findAll(spec);
    }
}