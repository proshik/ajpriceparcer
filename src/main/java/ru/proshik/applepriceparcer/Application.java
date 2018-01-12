package ru.proshik.applepriceparcer;

import org.apache.commons.lang3.StringUtils;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.proshik.applepriceparcer.bot.ApplePricePriceBot;
import ru.proshik.applepriceparcer.provider.ScreenerProviderFactory;
import ru.proshik.applepriceparcer.service.CommandService;
import ru.proshik.applepriceparcer.service.ShopService;
import ru.proshik.applepriceparcer.storage.Database;

public class Application {

    private static final String TELEGRAM_USERNAME = "APPLEPRICEPARCER_TELEGRAMUSERNAME";
    private static final String TELEGRAM_TOKEN = "APPLEPRICEPARCER_TELEGRAMTOKEN";
    private static final String DB_PATH = "APPLEPRICEPARCER_DBPATH";

    public static void main(String[] args) {
        Application application = new Application();
        application.Run();
    }

    private void Run() {
        // read environment variables
        String telegramUsername = readSystemEnv(TELEGRAM_USERNAME);
        String telegramToken = readSystemEnv(TELEGRAM_TOKEN);
        String dbPath = readSystemEnv(DB_PATH);

        Database db = new Database(dbPath);
        ScreenerProviderFactory screenerProviderFactory = new ScreenerProviderFactory();

        ShopService shopService = new ShopService(db, screenerProviderFactory);
        CommandService commandService = new CommandService(shopService, screenerProviderFactory);

        // Bot initialization
        ApiContextInitializer.init();
        TelegramBotsApi botsApi = new TelegramBotsApi();
        try {
            botsApi.registerBot(new ApplePricePriceBot(telegramUsername, telegramToken, commandService));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private String readSystemEnv(String telegramUsername) {
        String value = System.getenv(telegramUsername);
        checkEnvironmentVariabe(value);
        return value;
    }

    private void checkEnvironmentVariabe(String telegramUsername) {
        if (StringUtils.isEmpty(telegramUsername)) {
            System.out.println("Variable " + TELEGRAM_USERNAME + " is empty");
            System.exit(0);
        }
    }

}
