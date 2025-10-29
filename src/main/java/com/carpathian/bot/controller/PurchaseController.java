package com.carpathian.bot.controller;

import com.carpathian.bot.model.PlaceCategory;
import com.carpathian.bot.model.PremiumPurchase;
import com.carpathian.bot.model.User;
import com.carpathian.bot.service.PremiumService;
import com.carpathian.bot.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Handles premium purchase requests.  In a real system this endpoint
 * would be invoked after the Telegram Payments process succeeds.  For
 * this demo we simply accept purchase details and record them.
 */
@RestController
@RequestMapping("/api/purchases")
@RequiredArgsConstructor
public class PurchaseController {

    private final PremiumService premiumService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<?> createPurchase(
            @RequestParam Long userId,
            @RequestParam PlaceCategory category,
            @RequestParam long price,
            @RequestParam String currency,
            @RequestParam(defaultValue = "30") int days) {
        User user = userService.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body("User not found");
        }
        PremiumPurchase purchase = premiumService.recordPurchase(user, category, price, currency, days);
        return ResponseEntity.ok(purchase);
    }
}