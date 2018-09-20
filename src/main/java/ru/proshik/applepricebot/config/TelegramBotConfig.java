package ru.proshik.applepricebot.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.util.WebhookUtils;
import ru.proshik.applepricebot.service.bot.BotMessageHandler;
import ru.proshik.applepricebot.service.bot.TgPollingBot;

import java.text.MessageFormat;

@Configuration
public class TelegramBotConfig {

    @Value("${telegram.token}")
    private String token;

    @Value("${telegram.username}")
    private String username;

    @Value("${telegram.webhook.enabled}")
    private boolean webHookEnabled;

    @Value("${telegram.webhook.url}")
    private String webHookUrl;

    @Autowired
    private BotMessageHandler botMessageHandler;

    @Bean
    public AbsSender telegramBot() throws TelegramApiRequestException {
        ApiContextInitializer.init();

        DefaultBotOptions defaultBotOptions = new DefaultBotOptions();

        AbsSender bot;
        if (webHookEnabled) {
            DefaultAbsSender webHookBot = new DefaultAbsSender(defaultBotOptions) {
                @Override
                public String getBotToken() {
                    return token;
                }
            };
            WebhookUtils.setWebhook(webHookBot, checkAndOrUpdateUrl(webHookUrl) + token, "");

            bot = webHookBot;
        } else {
            TgPollingBot pollingBot = new TgPollingBot(defaultBotOptions, botMessageHandler, token, username);

            TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
            telegramBotsApi.registerBot(pollingBot);

            bot = pollingBot;
        }

        return bot;
    }

    private static String checkAndOrUpdateUrl(String url) {
        if (url != null && !url.endsWith("/")) {
            url = url + "/";
        }
        return MessageFormat.format("{0}callback/", url);
    }

}
