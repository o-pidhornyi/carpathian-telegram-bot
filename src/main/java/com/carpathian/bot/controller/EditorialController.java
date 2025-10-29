package com.carpathian.bot.controller;

import com.carpathian.bot.model.EditorialCollection;
import com.carpathian.bot.model.SeasonTag;
import com.carpathian.bot.service.EditorialService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Exposes endpoints to retrieve editorial collections.  Clients can
 * filter collections by season, title or request a limited number of
 * top collections.  Full CRUD operations are available via the
 * administrative service layer, but only read methods are exposed
 * publicly here.
 */
@RestController
@RequestMapping("/api/editorials")
@RequiredArgsConstructor
public class EditorialController {

    private final EditorialService editorialService;

    @GetMapping
    public List<EditorialCollection> getCollections(
            @RequestParam(required = false) SeasonTag season,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) @Min(1) @Max(10) Integer limit) {
        if (title != null && !title.isBlank()) {
            EditorialCollection ec = editorialService.getByTitle(title);
            return ec != null ? List.of(ec) : List.of();
        }
        if (season != null) {
            if (limit != null) {
                return editorialService.getTopCollectionsForSeason(season, limit);
            }
            return editorialService.getBySeason(season);
        }
        return editorialService.getAllCollections();
    }

    @GetMapping("/{id}")
    public ResponseEntity<EditorialCollection> getCollection(@PathVariable Long id) {
        EditorialCollection ec = editorialService.getById(id);
        return ec != null ? ResponseEntity.ok(ec) : ResponseEntity.notFound().build();
    }
}