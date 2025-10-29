package com.carpathian.bot.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.io.InputStream;

/**
 * Service for uploading media (documents/images) to Telegram. The uploaded file
 * is sent to a dedicated admin chat so that Telegram returns a persistent
 * file_id which can then be stored and reused when sending to end users.
 */
@Service
public class MediaService {

    private final OkHttpTelegramClient client;
    private final String adminChatId;

    public MediaService(
            @Value("${telegram.bots.carpathianBot.token}") String botToken,
            @Value("${telegram.admin.chat-id}") String adminChatId) {
        this.client = new OkHttpTelegramClient(botToken);
        this.adminChatId = adminChatId;
    }

    /**
     * Upload a file to Telegram and return the resulting file_id. The file
     * will be sent to the configured admin chat. If the upload fails, a
     * RuntimeException is thrown.
     *
     * @param fileStream the input stream of the file
     * @param filename the name of the file
     * @return the file_id returned by Telegram
     */
    public String uploadMedia(InputStream fileStream, String filename) {
        try {
            SendDocument request = SendDocument.builder()
                    .chatId(adminChatId)
                    .document(new InputFile(fileStream, filename))
                    .build();
            Message message = client.execute(request);
            if (message.getDocument() != null) {
                return message.getDocument().getFileId();
            }
            throw new RuntimeException("Telegram did not return a document");
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload media to Telegram", e);
        }
    }
}
