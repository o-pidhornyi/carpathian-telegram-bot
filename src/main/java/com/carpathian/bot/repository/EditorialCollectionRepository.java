package com.carpathian.bot.repository;

import com.carpathian.bot.model.EditorialCollection;
import com.carpathian.bot.model.SeasonTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for {@link EditorialCollection} entities.  Provides
 * convenience methods for retrieving collections by season or title.
 */
@Repository
public interface EditorialCollectionRepository extends JpaRepository<EditorialCollection, Long> {
    List<EditorialCollection> findBySeason(SeasonTag season);
    EditorialCollection findByTitleIgnoreCase(String title);
    List<EditorialCollection> findTop5BySeasonOrderByCreatedAtDesc(SeasonTag season);
}