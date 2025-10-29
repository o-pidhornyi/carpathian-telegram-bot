package com.carpathian.bot.service;

import com.carpathian.bot.model.Entitlement;
import com.carpathian.bot.model.House;
import com.carpathian.bot.model.PlaceCategory;
import com.carpathian.bot.model.User;
import com.carpathian.bot.model.UserRole;
import com.carpathian.bot.repository.EntitlementRepository;
import com.carpathian.bot.repository.HouseRepository;
import com.carpathian.bot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Service layer encapsulating operations on {@link User}.  Provides
 * methods for creating or retrieving users, managing favourites and
 * checking premium entitlements.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final HouseRepository houseRepository;
    private final EntitlementRepository entitlementRepository;

    public Optional<User> findByTelegramId(Long telegramId) {
        return userRepository.findByTelegramId(telegramId);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public User findOrCreateUser(Long telegramId, String name, String preferredLanguage) {
        return userRepository.findByTelegramId(telegramId)
                .orElseGet(() -> {
                    User user = User.builder()
                            .telegramId(telegramId)
                            .name(name)
                            .preferredLanguage(preferredLanguage)
                            .build();
                    user.getRoles().add(UserRole.USER);
                    return userRepository.save(user);
                });
    }

    public List<House> getFavourites(Long userId) {
        return userRepository.findById(userId)
                .map(user -> List.copyOf(user.getFavourites()))
                .orElse(List.of());
    }

    public void addFavourite(Long userId, Long houseId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        House house = houseRepository.findById(houseId).orElseThrow(() -> new IllegalArgumentException("House not found"));
        user.getFavourites().add(house);
        userRepository.save(user);
    }

    public void removeFavourite(Long userId, Long houseId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        House house = houseRepository.findById(houseId).orElseThrow(() -> new IllegalArgumentException("House not found"));
        user.getFavourites().remove(house);
        userRepository.save(user);
    }

    public boolean hasActiveEntitlement(User user, PlaceCategory category) {
        LocalDateTime now = LocalDateTime.now();
        return !entitlementRepository.findByUserAndCategoryAndExpiresAtAfter(user, category, now).isEmpty();
    }

    public Entitlement grantEntitlement(User user, PlaceCategory category, LocalDateTime expiry) {
        Entitlement entitlement = Entitlement.builder()
                .user(user)
                .category(category)
                .grantedAt(LocalDateTime.now())
                .expiresAt(expiry)
                .build();
        return entitlementRepository.save(entitlement);
    }
}