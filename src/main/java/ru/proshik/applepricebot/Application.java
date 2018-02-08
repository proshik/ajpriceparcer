package ru.proshik.applepricebot;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.quartz.SchedulerException;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.proshik.applepricebot.provider.ProviderFactory;
import ru.proshik.applepricebot.service.*;
import ru.proshik.applepricebot.service.bot.AppleProductPricesBot;
import ru.proshik.applepricebot.service.scheduler.QuartzDefaultScheduler;
import ru.proshik.applepricebot.storage.Database;

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
        // print environment variables
        String telegramUsername = readSystemEnv(TELEGRAM_USERNAME);
        String telegramToken = readSystemEnv(TELEGRAM_TOKEN);
        String dbPath = readSystemEnv(DB_PATH);
        // init data providers
        Database db = new Database(dbPath);
        ProviderFactory providerFactory = new ProviderFactory();
        // init service layer
        ShopService shopService = new ShopService(providerFactory);
        FetchService fetchService = new FetchService(db);
        SubscriberService subscriberService = new SubscriberService(db);
        NotificationQueueService notificationQueueService = new NotificationQueueService();
        CommandService operationService = new CommandService(shopService, fetchService, subscriberService);
        // run quartz scheduler
        try {
            QuartzDefaultScheduler quartzDefaultScheduler =
                    new QuartzDefaultScheduler(providerFactory, fetchService, subscriberService, notificationQueueService);
            quartzDefaultScheduler.init();
        } catch (SchedulerException e) {
            LOG.error("Error on execute quartz scheduler", e);
            System.exit(0);
        }
        // Bot initialization
        ApiContextInitializer.init();
        TelegramBotsApi botsApi = new TelegramBotsApi();
        try {
            AppleProductPricesBot bot = new AppleProductPricesBot(telegramUsername, telegramToken, operationService,
                    notificationQueueService);
            bot.registerScheduler();
            botsApi.registerBot(bot);
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
