package ru.proshik.applepricebot.service.bot;

import org.apache.log4j.Logger;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

final public class TgPollingBot extends TelegramLongPollingBot {

    private static final Logger LOG = Logger.getLogger(TgPollingBot.class);

    private String telegramToken;

    private String telegramUsername;

    private BotMessageHandler botMessageHandler;

    public TgPollingBot(DefaultBotOptions options,
                        BotMessageHandler botMessageHandler,
                        String telegramToken,
                        String telegramUsername) {
        super(options);
        this.botMessageHandler = botMessageHandler;
        this.telegramToken = telegramToken;
        this.telegramUsername = telegramUsername;
    }

    @Override
    public void onUpdateReceived(Update update) {
        BotApiMethod<Message> response = botMessageHandler.onWebHookUpdateReceived(update);
        try {
            execute(response);
        } catch (TelegramApiException e) {
            LOG.error("Panic! Messages not sending!", e);
        }
    }

    @Override
    public String getBotUsername() {
        return telegramUsername;
    }

    @Override
    public String getBotToken() {
        return telegramToken;
    }

}
