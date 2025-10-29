package com.carpathian.bot.bot;

import com.carpathian.bot.model.House;
import com.carpathian.bot.model.Place;
import com.carpathian.bot.model.PlaceCategory;
import com.carpathian.bot.model.User;
import com.carpathian.bot.model.Amenity;
import com.carpathian.bot.model.SeasonTag;
import com.carpathian.bot.service.CatalogService;
import com.carpathian.bot.service.EditorialService;
import com.carpathian.bot.service.PlaceService;
import com.carpathian.bot.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Long‑polling Telegram bot implementing core user flows such as
 * browsing houses, viewing editorial picks and managing favourites.
 * Business logic is delegated to services; this class focuses on
 * command parsing, i18n and user session handling.  Future
 * enhancements (inline keyboards, interactive filters, payments) can
 * build on this foundation.
 */
@Component
@Slf4j
public class CarpathianTelegramBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {

    private final CatalogService catalogService;
    private final EditorialService editorialService;
    private final PlaceService placeService;
    private final UserService userService;
    private final MessageSource messageSource;
    private final TelegramClient telegramClient;

    public CarpathianTelegramBot(CatalogService catalogService, EditorialService editorialService, PlaceService placeService, UserService userService, MessageSource messageSource) {
        this.catalogService = catalogService;
        this.editorialService = editorialService;
        this.placeService = placeService;
        this.userService = userService;
        this.messageSource = messageSource;
        this.telegramClient = new OkHttpTelegramClient(getBotToken());
    }

    @Value("${telegram.bots.carpathianBot.username}")
    private String botUsername;

    @Value("${telegram.bots.carpathianBot.token}")
    private String botToken;

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    @Override
    public void consume(Update update) {
        if (update == null || !update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }
        Message message = update.getMessage();
        Long chatId = message.getChatId();
        String text = message.getText().trim();

        // Find or create the user record.  Default language to English.
        User user = userService.findOrCreateUser(chatId,
                message.getFrom() != null ? message.getFrom().getFirstName() : null,
                "en");
        Locale locale = new Locale(user.getPreferredLanguage() != null ? user.getPreferredLanguage() : "en");

        String[] parts = text.split(" ", 2);
        String command = parts[0].toLowerCase();
        String argPart = parts.length > 1 ? parts[1] : "";

        switch (command) {
            case "/start" -> handleStart(chatId, locale);
            case "/browse" -> handleBrowse(chatId, locale);
            case "/picks" -> handlePicks(chatId, locale);
            case "/favorites" -> handleFavorites(chatId, user, locale);
            case "/favorite" -> handleFavouriteToggle(chatId, user, argPart, locale);
            case "/places" -> handlePlaces(chatId, user, argPart, locale);
            default -> sendMessage(chatId, getMessage("error.command", locale));
        }
    }

    private void handleStart(Long chatId, Locale locale) {
        sendMessage(chatId, getMessage("greeting", locale));
    }

    private void handleBrowse(Long chatId, Locale locale) {
        List<House> houses = catalogService.search(null, null, null, null, null);
        if (houses.isEmpty()) {
            sendMessage(chatId, getMessage("houses.none", locale));
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(getMessage("houses.list.header", locale)).append("\n");
        houses.forEach(h -> sb.append("\u2022 ").append(h.getName())
                .append(" (" + h.getRegion() + ", " + h.getCity() + ")\n"));
        sendMessage(chatId, sb.toString());
    }

    private void handlePicks(Long chatId, Locale locale) {
        // Determine current season
        SeasonTag season = determineSeason(LocalDate.now());
        var collections = editorialService.getTopCollectionsForSeason(season, 5);
        if (collections.isEmpty()) {
            sendMessage(chatId, getMessage("picks.none", locale));
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(getMessage("picks.header", locale)).append("\n");
        collections.forEach(c -> {
            sb.append("• ").append(c.getTitle()).append("\n");
            c.getHouses().forEach(h -> sb.append("    - ").append(h.getName()).append("\n"));
        });
        sendMessage(chatId, sb.toString());
    }

    private void handleFavorites(Long chatId, User user, Locale locale) {
        List<House> favourites = userService.getFavourites(user.getId());
        if (favourites.isEmpty()) {
            sendMessage(chatId, "You have no favourites yet.");
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Your favourites:\n");
        favourites.forEach(h -> sb.append("• ").append(h.getName()).append("\n"));
        sendMessage(chatId, sb.toString());
    }

    private void handleFavouriteToggle(Long chatId, User user, String argPart, Locale locale) {
        if (argPart == null || argPart.isBlank()) {
            sendMessage(chatId, "Usage: /favorite <houseId>");
            return;
        }
        try {
            Long houseId = Long.parseLong(argPart.trim());
            var favourites = user.getFavourites();
            boolean existed = favourites.stream().anyMatch(h -> h.getId().equals(houseId));
            if (existed) {
                userService.removeFavourite(user.getId(), houseId);
                sendMessage(chatId, "Removed from favourites.");
            } else {
                userService.addFavourite(user.getId(), houseId);
                sendMessage(chatId, "Added to favourites.");
            }
        } catch (NumberFormatException e) {
            sendMessage(chatId, "Invalid house id.");
        }
    }

    private void handlePlaces(Long chatId, User user, String argPart, Locale locale) {
        String[] args = argPart.split(" ");
        if (args.length < 1 || args[0].isBlank()) {
            sendMessage(chatId, "Usage: /places <houseId> [category1,category2,...]");
            return;
        }
        try {
            Long houseId = Long.parseLong(args[0].trim());
            Set<PlaceCategory> categories = null;
            if (args.length > 1 && !args[1].isBlank()) {
                categories = Arrays.stream(args[1].split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .map(String::toUpperCase)
                        .map(PlaceCategory::valueOf)
                        .collect(Collectors.toSet());
            }
            List<Place> places = placeService.getAccessiblePlacesForHouse(user, houseId, categories);
            if (places.isEmpty()) {
                sendMessage(chatId, getMessage("places.no_access", locale));
                return;
            }
            StringBuilder sb = new StringBuilder();
            sb.append("Places:\n");
            places.forEach(p -> sb.append("• ").append(p.getName()).append(" (" + p.getCategory() + ")\n"));
            sendMessage(chatId, sb.toString());
        } catch (NumberFormatException e) {
            sendMessage(chatId, "Invalid house id.");
        } catch (IllegalArgumentException e) {
            sendMessage(chatId, "Invalid category.");
        }
    }

    private SeasonTag determineSeason(LocalDate date) {
        int month = date.getMonthValue();
        return switch (month) {
            case 12, 1, 2 -> SeasonTag.WINTER;
            case 3, 4, 5 -> SeasonTag.SPRING;
            case 6, 7, 8 -> SeasonTag.SUMMER;
            case 9, 10, 11 -> SeasonTag.AUTUMN;
            default -> SeasonTag.AUTUMN;
        };
    }

    private void sendMessage(Long chatId, String text) {
        SendMessage msg = SendMessage.builder()
                .chatId(String.valueOf(chatId))
                .text(text)
                .build();
        try {
            telegramClient.execute(msg);
        } catch (Exception e) {
            log.error("Failed to send message", e);
        }
    }

    private String getMessage(String key, Locale locale) {
        return messageSource.getMessage(key, null, locale);
    }
}