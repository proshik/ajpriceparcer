package ru.proshik.applepricebot.service.bot;

import org.springframework.beans.factory.annotation.Value;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

public class TgWebHookBot extends TelegramWebhookBot {

    @Value("${telegram.token}")
    private String telegramToken;


    @Value("${telegram.username}")
    private String telegramUsername;


    @Override
    public BotApiMethod onWebhookUpdateReceived(Update update) {
        return null;
    }

    @Override
    public String getBotUsername() {
        return telegramUsername;
    }

    @Override
    public String getBotToken() {
        return telegramToken;
    }

    @Override
    public String getBotPath() {
        return null;
    }
}
