package ru.proshik.applepriceparcer;

import org.apache.commons.lang3.StringUtils;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.proshik.applepriceparcer.bot.ApplePricePraceBot;

public class Application {

    private static final String TELEGRAM_USERNAME = "APPLEPRICEPARCER_TELEGRAMUSERNAME";
    private static final String TELEGRAM_TOKEN = "APPLEPRICEPARCER_TELEGRAMTOKEN";

    public static void main(String[] args) {
        Application application = new Application();
        application.Run();
    }

    private void Run() {
        // read environment variables
        String telegramUsername = System.getenv(TELEGRAM_USERNAME);
        checkEnvironmentVariabe(telegramUsername);

        String telegramToken = System.getenv(TELEGRAM_TOKEN);
        checkEnvironmentVariabe(telegramToken);

        // Bot initialization
        ApiContextInitializer.init();
        TelegramBotsApi botsApi = new TelegramBotsApi();
        try {
            botsApi.registerBot(new ApplePricePraceBot(telegramUsername, telegramToken));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void checkEnvironmentVariabe(String telegramUsername) {
        if (StringUtils.isEmpty(telegramUsername)) {
            System.out.println("Variable " + TELEGRAM_USERNAME + " is empty");
            System.exit(0);
        }
    }

}
