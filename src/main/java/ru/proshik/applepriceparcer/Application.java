package ru.proshik.applepriceparcer;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.quartz.SchedulerException;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.proshik.applepriceparcer.bot.AppleProductPricesBot;
import ru.proshik.applepriceparcer.provider.ProviderFactory;
import ru.proshik.applepriceparcer.service.AssortmentService;
import ru.proshik.applepriceparcer.service.OperationService;
import ru.proshik.applepriceparcer.service.scheduler.QuartzDefaultScheduler;
import ru.proshik.applepriceparcer.storage.Database;

public class Application {

    private static final Logger LOG = Logger.getLogger(Application.class);

    private static final String TELEGRAM_USERNAME = "APPLEPRICEPARCER_TELEGRAMUSERNAME";
    private static final String TELEGRAM_TOKEN = "APPLEPRICEPARCER_TELEGRAMTOKEN";
    private static final String DB_PATH = "APPLEPRICEPARCER_DBPATH";

    public static void main(String[] args) {
        Application application = new Application();
        application.Run();
    }

    private void Run() {
        // priceAssortment environment variables
        String telegramUsername = readSystemEnv(TELEGRAM_USERNAME);
        String telegramToken = readSystemEnv(TELEGRAM_TOKEN);
        String dbPath = readSystemEnv(DB_PATH);

        Database db = new Database(dbPath);
        ProviderFactory providerFactory = new ProviderFactory();

        AssortmentService assortmentService = new AssortmentService(db);
        OperationService operationService = new OperationService(providerFactory, assortmentService);

        QuartzDefaultScheduler quartzDefaultScheduler = new QuartzDefaultScheduler(operationService);
        try {
            quartzDefaultScheduler.init();
        } catch (SchedulerException e) {
            LOG.error("Eror on execute quartz scheduler", e);
            System.exit(0);
        }

        // Bot initialization
        ApiContextInitializer.init();
        TelegramBotsApi botsApi = new TelegramBotsApi();
        try {
            botsApi.registerBot(new AppleProductPricesBot(telegramUsername, telegramToken, operationService));
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
