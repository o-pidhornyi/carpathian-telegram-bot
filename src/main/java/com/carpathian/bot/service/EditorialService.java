package com.carpathian.bot.service;

import com.carpathian.bot.model.EditorialCollection;
import com.carpathian.bot.model.SeasonTag;
import com.carpathian.bot.repository.EditorialCollectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service providing CRUD operations for editorial collections.  It
 * delegates persistence to the {@link EditorialCollectionRepository} and
 * automatically timestamps new collections.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class EditorialService {

    private final EditorialCollectionRepository repository;

    public EditorialCollection createCollection(EditorialCollection collection) {
        collection.setCreatedAt(LocalDateTime.now());
        return repository.save(collection);
    }

    public EditorialCollection updateCollection(EditorialCollection collection) {
        return repository.save(collection);
    }

    public void deleteCollection(Long id) {
        repository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<EditorialCollection> getAllCollections() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public EditorialCollection getById(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public EditorialCollection getByTitle(String title) {
        return repository.findByTitleIgnoreCase(title);
    }

    @Transactional(readOnly = true)
    public List<EditorialCollection> getBySeason(SeasonTag season) {
        return repository.findBySeason(season);
    }

    @Transactional(readOnly = true)
    public List<EditorialCollection> getTopCollectionsForSeason(SeasonTag season, int limit) {
        List<EditorialCollection> collections = repository.findTop5BySeasonOrderByCreatedAtDesc(season);
        if (collections.size() > limit) {
            return collections.subList(0, limit);
        }
        return collections;
    }
}