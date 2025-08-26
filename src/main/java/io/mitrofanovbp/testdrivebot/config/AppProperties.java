package io.mitrofanovbp.testdrivebot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Application configuration properties bound from application.yml / environment.
 * No secrets are hardcoded; values are expected via environment variables.
 */
@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    /**
     * Telegram bot token (from BotFather).
     */
    private String telegramBotToken;

    /**
     * Telegram bot username (public handle).
     */
    private String telegramBotUsername;

    /**
     * Admin token value required in X-Admin-Token for /api/admin/** endpoints.
     */
    private String adminToken;

    public String getTelegramBotToken() {
        return telegramBotToken;
    }

    public void setTelegramBotToken(String telegramBotToken) {
        this.telegramBotToken = telegramBotToken;
    }

    public String getTelegramBotUsername() {
        return telegramBotUsername;
    }

    public void setTelegramBotUsername(String telegramBotUsername) {
        this.telegramBotUsername = telegramBotUsername;
    }

    public String getAdminToken() {
        return adminToken;
    }

    public void setAdminToken(String adminToken) {
        this.adminToken = adminToken;
    }
}
