package com.carpathian.bot.repository;

import com.carpathian.bot.model.Amenity;
import com.carpathian.bot.model.House;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for {@link House} entities.  Extends {@link JpaSpecificationExecutor}
 * to enable dynamic filtering via JPA Specifications for the search API.
 */
@Repository
public interface HouseRepository extends JpaRepository<House, Long>, JpaSpecificationExecutor<House> {
    // Additional query methods can be defined as needed

    List<House> findByRegionIgnoreCase(String region);
    List<House> findByCityIgnoreCase(String city);
    List<House> findByRegionIgnoreCaseAndCityIgnoreCase(String region, String city);
}