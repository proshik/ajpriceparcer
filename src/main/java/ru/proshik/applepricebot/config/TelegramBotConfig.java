package ru.proshik.applepricebot.config;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.proshik.applepricebot.Application;
import ru.proshik.applepricebot.service.bot.AppleProductPricesBot;

import javax.annotation.PostConstruct;

// TODO: 27.06.2018 fix it
//@Configuration
public class TelegramBotConfig {

    private static final Logger LOG = Logger.getLogger(Application.class);

    static {
        ApiContextInitializer.init();
    }

    @Autowired
    private AppleProductPricesBot appleProductPricesBot;

    @PostConstruct
    public void init() {
        TelegramBotsApi botsApi = new TelegramBotsApi();
        try {
            appleProductPricesBot.registerScheduler();
            botsApi.registerBot(appleProductPricesBot);
        } catch (TelegramApiException e) {
            LOG.error("Error on registration bot in Telegram api", e);
            System.exit(0);
        }
    }
}
