package ru.proshik.applepriceparcer;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.quartz.SchedulerException;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.proshik.applepriceparcer.bot.AppleProductPricesBot2;
import ru.proshik.applepriceparcer.provider2.ProviderFactory;
import ru.proshik.applepriceparcer.service.FetchService;
import ru.proshik.applepriceparcer.service.OperationService2;
import ru.proshik.applepriceparcer.service.UserService2;
import ru.proshik.applepriceparcer.service.scheduler.QuartzDefaultScheduler2;
import ru.proshik.applepriceparcer.storage.Database2;

public class Application2 {

    private static final Logger LOG = Logger.getLogger(Application2.class);

    private static final String TELEGRAM_USERNAME = "APPLEPRICEPARCER_TELEGRAMUSERNAME";
    private static final String TELEGRAM_TOKEN = "APPLEPRICEPARCER_TELEGRAMTOKEN";
    private static final String DB_PATH = "APPLEPRICEPARCER_DBPATH";

    public static void main(String[] args) {
        Application2 application = new Application2();
        application.Run();
    }

    private void Run() {
        // priceAssortment environment variables
        String telegramUsername = readSystemEnv(TELEGRAM_USERNAME);
        String telegramToken = readSystemEnv(TELEGRAM_TOKEN);
        String dbPath = readSystemEnv(DB_PATH);

        Database2 db = new Database2(dbPath);
        ProviderFactory providerFactory = new ProviderFactory();

        FetchService fetchService = new FetchService(db);
        UserService2 userService = new UserService2(db);
        OperationService2 operationService = new OperationService2(providerFactory, fetchService, userService);

        QuartzDefaultScheduler2 quartzDefaultScheduler = new QuartzDefaultScheduler2(providerFactory, fetchService);
        try {
            quartzDefaultScheduler.init();
        } catch (SchedulerException e) {
            LOG.error("Error on execute quartz scheduler", e);
            System.exit(0);
        }

        // Bot initialization
        ApiContextInitializer.init();
        TelegramBotsApi botsApi = new TelegramBotsApi();
        try {
            botsApi.registerBot(new AppleProductPricesBot2(telegramUsername, telegramToken, operationService));
        } catch (TelegramApiException e) {
            LOG.error("Error on registration bot in Telegram api", e);
            System.exit(0);
        }

        LOG.info("Application was started!");
    }

    private String readSystemEnv(String telegramUsername) {
        String value = System.getenv(telegramUsername);
        checkEnvironmentVariable(value);
        return value;
    }

    private void checkEnvironmentVariable(String telegramUsername) {
        if (StringUtils.isEmpty(telegramUsername)) {
            LOG.error("Variable " + TELEGRAM_USERNAME + " is empty");
            System.exit(0);
        }
    }

}
