package ru.proshik.applepricebot.service.bot;

import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import javax.annotation.PostConstruct;

public class TgWebHookBot extends TelegramWebhookBot {

    @PostConstruct
    public void init(String telegramWebHookUrl) throws TelegramApiRequestException {
        setWebhook(telegramWebHookUrl, "");
    }

    @Override
    public BotApiMethod onWebhookUpdateReceived(Update update) {
        return new SendMessage().setChatId(update.getMessage().getChatId()).setText("Hello!");
    }

    @Override
    public String getBotUsername() {
        return null;
    }

    @Override
    public String getBotToken() {
        return null;
    }

    @Override
    public String getBotPath() {
        return null;
    }


}
