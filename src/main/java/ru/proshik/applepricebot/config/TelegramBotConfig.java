package ru.proshik.applepricebot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.TelegramBotsApi;
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

    @Value("${telegram.webhook}")
    private boolean isWebHook;

    @Value("${telegram.webhook_url}")
    private String webHookUrl;

    @Bean
    public DefaultAbsSender telegramBot(BotMessageHandler botMessageHandler) throws TelegramApiRequestException {
        ApiContextInitializer.init();

        DefaultBotOptions defaultBotOptions = new DefaultBotOptions();

        if (isWebHook) {
            DefaultAbsSender bot = new DefaultAbsSender(defaultBotOptions) {
                @Override
                public String getBotToken() {
                    return token;
                }
            };
            WebhookUtils.setWebhook(bot, checkAndOrUpdateUrl(webHookUrl) + token, "");

            return bot;
        } else {
            TgPollingBot bot = new TgPollingBot(botMessageHandler, defaultBotOptions, token, username);

            TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
            telegramBotsApi.registerBot(bot);

            return bot;
        }
    }

    private static String checkAndOrUpdateUrl(String url) {
        if (url != null && !url.endsWith("/")) {
            url = url + "/";
        }
        return MessageFormat.format("{0}callback/", url);
    }

}
