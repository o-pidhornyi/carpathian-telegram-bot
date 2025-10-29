package com.carpathian.bot.service;

import com.carpathian.bot.model.House;
import com.carpathian.bot.repository.HouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service for basic CRUD operations on {@link House}.  Delegates to
 * {@link HouseRepository} and may be extended with caching or
 * validation logic.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class HouseService {

    private final HouseRepository houseRepository;

    @Transactional(readOnly = true)
    public List<House> findAll() {
        return houseRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<House> findById(Long id) {
        return houseRepository.findById(id);
    }

    public House save(House house) {
        return houseRepository.save(house);
    }

    public void deleteById(Long id) {
        houseRepository.deleteById(id);
    }
}