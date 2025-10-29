package com.carpathian.bot.service;

import com.carpathian.bot.model.Place;
import com.carpathian.bot.model.PlaceCategory;
import com.carpathian.bot.model.User;
import com.carpathian.bot.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service managing curated places.  Provides methods to fetch places
 * accessible to a user for a given house and categories, subject to
 * premium entitlements.  Administrative methods for creating and
 * updating places are also available.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class PlaceService {

    private final PlaceRepository placeRepository;
    private final UserService userService;

    public Place save(Place place) {
        return placeRepository.save(place);
    }

    public void delete(Long id) {
        placeRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Place> getAccessiblePlacesForHouse(User user, Long houseId, Set<PlaceCategory> categories) {
        if (categories == null || categories.isEmpty()) {
            // If no categories specified, fetch all categories user has entitlement for.
            return placeRepository.findByHouses_Id(houseId).stream()
                    .filter(place -> userService.hasActiveEntitlement(user, place.getCategory()))
                    .toList();
        }
        return placeRepository.findByHouses_Id(houseId).stream()
                .filter(place -> categories.contains(place.getCategory()))
                .filter(place -> userService.hasActiveEntitlement(user, place.getCategory()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Place> getPlacesForHouse(Long houseId) {
        return placeRepository.findByHouses_Id(houseId);
    }
}