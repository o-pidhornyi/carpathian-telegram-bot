package com.carpathian.bot.controller;

import com.carpathian.bot.model.SeasonTag;
import com.carpathian.bot.service.CatalogService;
import com.carpathian.bot.service.EditorialService;
import com.carpathian.bot.service.MediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Admin controller for managing houses, media uploads and editorial collections.
 * Secured via Spring Security (configured in SecurityConfig) and exposed at the
 * "/admin" context path. All methods return Thymeleaf templates under
 * src/main/resources/templates/admin/.
 */
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final CatalogService catalogService;
    private final MediaService mediaService;
    private final EditorialService editorialService;

    /**
     * Login page is handled by Spring Security; simply returns the login view.
     */
    @GetMapping("/login")
    public String login() {
        return "admin/login";
    }

    /**
     * Dashboard listing all houses. In future this could be paginated or filtered.
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("houses", catalogService.search(null, null, null, null, null));
        return "admin/dashboard";
    }

    /**
     * Show the media upload form.
     */
    @GetMapping("/upload")
    public String uploadForm() {
        return "admin/upload";
    }

    /**
     * Handle uploading media to Telegram. Stores the returned fileId to display on the page.
     */
    @PostMapping("/upload")
    public String handleUpload(@RequestParam("file") MultipartFile file, Model model) {
        if (file == null || file.isEmpty()) {
            model.addAttribute("error", "Please select a file to upload.");
            return "admin/upload";
        }
        try {
            String fileId = mediaService.uploadMedia(file.getInputStream(), file.getOriginalFilename());
            model.addAttribute("fileId", fileId);
            model.addAttribute("success", true);
        } catch (IOException e) {
            model.addAttribute("error", "Failed to upload file: " + e.getMessage());
        }
        return "admin/upload";
    }

    /**
     * Display editorial collections for a season. Allows drag-and-drop ordering in the future.
     */
    @GetMapping("/collections")
    public String collections(@RequestParam(value = "season", required = false) SeasonTag season,
                              Model model) {
        SeasonTag current = season != null ? season : SeasonTag.AUTUMN;
        var collections = editorialService.getTopCollectionsForSeason(current, 10);
        model.addAttribute("season", current);
        model.addAttribute("collections", collections);
        return "admin/collections";
    }
}
