package com.carpathian.bot.controller;

import com.carpathian.bot.model.Place;
import com.carpathian.bot.model.PlaceCategory;
import com.carpathian.bot.model.User;
import com.carpathian.bot.service.PlaceService;
import com.carpathian.bot.service.UserService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Exposes endpoints for accessing curated places.  Access to premium
 * places is controlled by user entitlements.  Clients must supply a
 * user identifier so the service can verify entitlements.
 */
@RestController
@RequestMapping("/api/places")
@RequiredArgsConstructor
public class PlaceController {

    private final PlaceService placeService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<?> getPlaces(
            @RequestParam Long houseId,
            @RequestParam Long userId,
            @RequestParam(required = false) String categories) {
        User user = userService.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }
        Set<PlaceCategory> categorySet = new HashSet<>();
        if (categories != null && !categories.isBlank()) {
            categorySet = List.of(categories.split(","))
                    .stream()
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(String::toUpperCase)
                    .map(PlaceCategory::valueOf)
                    .collect(Collectors.toSet());
        }
        List<Place> places = placeService.getAccessiblePlacesForHouse(user, houseId, categorySet);
        if (places.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No entitlements or no places found for the given categories");
        }
        return ResponseEntity.ok(places);
    }
}