package com.carpathian.bot.repository;

import com.carpathian.bot.model.Place;
import com.carpathian.bot.model.PlaceCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for {@link Place} entities.  Provides methods to find
 * places by category or by associated house.
 */
@Repository
public interface PlaceRepository extends JpaRepository<Place, Long> {
    List<Place> findByCategory(PlaceCategory category);
    List<Place> findByHouses_Id(Long houseId);
}