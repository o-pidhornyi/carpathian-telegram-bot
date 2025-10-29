package com.carpathian.bot.controller;

import com.carpathian.bot.model.House;
import com.carpathian.bot.service.UserService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for user‑specific operations such as managing favourite
 * houses.  Requires the user identifier in the path to ensure
 * operations apply to the correct account.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}/favorites")
    public ResponseEntity<List<House>> getFavourites(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getFavourites(userId));
    }

    @PostMapping("/{userId}/favorites/{houseId}")
    public ResponseEntity<Void> addFavourite(
            @PathVariable Long userId,
            @PathVariable Long houseId) {
        userService.addFavourite(userId, houseId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{userId}/favorites/{houseId}")
    public ResponseEntity<Void> removeFavourite(
            @PathVariable Long userId,
            @PathVariable Long houseId) {
        userService.removeFavourite(userId, houseId);
        return ResponseEntity.noContent().build();
    }
}