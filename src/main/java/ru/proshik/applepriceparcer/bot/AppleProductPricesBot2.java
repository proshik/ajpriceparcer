package ru.proshik.applepriceparcer.bot;

import org.apache.log4j.Logger;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.proshik.applepriceparcer.model2.ChangeProductNotification;
import ru.proshik.applepriceparcer.service.CommandService;
import ru.proshik.applepriceparcer.service.NotificationQueueService;

import java.util.List;

import static ru.proshik.applepriceparcer.bot.BotUtils.extractArgument;
import static ru.proshik.applepriceparcer.bot.PrintUtils2.notificationInfo;

public class AppleProductPricesBot2 extends TelegramLongPollingBot {

    private static final Logger LOG = Logger.getLogger(AppleProductPricesBot2.class);

    // Commands
    private static final String COMMAND_START = "/start";
    private static final String COMMAND_HELP = "/help";
    private static final String COMMAND_READ = "/read";
    private static final String COMMAND_HISTORY = "/history";
    private static final String COMMAND_SUBSCRIPTION = "/subscription";
    private static final String COMMAND_SUBSCRIBE = "/subscribe";
    private static final String COMMAND_UNSUBSCRIBE = "/unsubscribe";

    // Telegram bot settings
    private final String botUsername;
    private final String botToken;

    // Injected services
    private CommandService commandService;
    private NotificationQueueService notificationQueueService;

    public AppleProductPricesBot2(String botUsername,
                                  String botToken,
                                  CommandService commandService,
                                  NotificationQueueService notificationQueueService) {
        this.botUsername = botUsername;
        this.botToken = botToken;
        this.commandService = commandService;
        this.notificationQueueService = notificationQueueService;
    }

    public void registerScheduler() {
        new Thread(() -> {
            while (true) {
                ChangeProductNotification element = notificationQueueService.take();
                if (element == null) {
                    // error situation
                    LOG.warn("Notification does not send to user");
                    continue;
                }
                // create message
                SendMessage message = new SendMessage()
                        .setChatId(element.getUserId())
                        .enableMarkdown(true)
                        .setText(notificationInfo(element));
                try {
                    execute(message);
                } catch (TelegramApiException e) {
                    LOG.error("Panic! Messages not sending from notification thread!", e);
                }
            }
        }).start();
    }

    @Override
    public void onUpdateReceived(Update update) {
        SendMessage message = null;
        try {
            if (update.hasMessage()) {
                message = processMessageOperation(update);
            }
        } catch (Exception e) {
            LOG.error("Error on build message", e);
            message = new SendMessage()
                    .setChatId(extractChatId(update))
                    .setText("Error on execute operation! Connect with support!");
        }

        if (message != null) {
            try {
                execute(message);
            } catch (TelegramApiException e) {
                LOG.error("Panic! Messages not sending!", e);
            }
        }
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onClosing() {
        LOG.info("Bot was closing");
    }

    private SendMessage processMessageOperation(Update update) {
        SendMessage message;

        if (update.getMessage().isCommand()) {
            message = processCommandMessageOperation(update);
        } else {
            message = new SendMessage()
                    .setChatId(update.getMessage().getChatId())
                    .setText(commandInfo());
        }
        return message;
    }

    private SendMessage processCommandMessageOperation(Update update) {
        String message;

        switch (update.getMessage().getText().split(" ")[0]) {
            case COMMAND_START:
                message = startCommand();
                break;
            case COMMAND_READ:
                message = readCommand(update);
                break;
            case COMMAND_HISTORY:
                message = historyCommand(update);
                break;
            case COMMAND_SUBSCRIPTION:
                message = subscriptionCommand(update);
                break;
            case COMMAND_SUBSCRIBE:
                message = subscribeCommand(update);
                break;
            case COMMAND_UNSUBSCRIBE:
                message = unsubscribeCommand(update);
                break;
            case COMMAND_HELP:
                message = helpCommand();
                break;
            default:
                message = commandInfo();
        }

        return new SendMessage()
                .setChatId(update.getMessage().getChatId())
                .enableMarkdown(true)
                .setText(message);
    }

    private String startCommand() {
        return "Hello, this is Bot for follow prices on apple products in several shops.\n\n" + commandInfo();
    }

    private String readCommand(Update update) {
        List<String> arguments = BotUtils.extractArguments(update.getMessage().getText());

        return commandService.read(arguments);
    }

    private String historyCommand(Update update) {
        String argument = BotUtils.extractArgument(update.getMessage().getText())
                .orElse(null);
        return commandService.history(argument);
    }

    private String subscriptionCommand(Update update) {
        return commandService.subscriptions(String.valueOf(update.getMessage().getChatId()));
    }

    private String subscribeCommand(Update update) {
        return extractArgument(update.getMessage().getText())
                .map(argument -> commandService.subscribe(String.valueOf(update.getMessage().getChatId()), argument))
                .orElse("Needed argument for the entered command, write /subscription for give information about shops.");
    }

    private String unsubscribeCommand(Update update) {
        return extractArgument(update.getMessage().getText())
                .map(argument -> commandService.unsubscribe(String.valueOf(update.getMessage().getChatId()), argument))
                .orElse("Needed argument for the entered command, write /subscription for give information about shops.");
    }

    private String helpCommand() {
        return commandInfo();
    }

    private Long extractChatId(Update update) {
        Long chatId;

        if (update.hasMessage()) {
            chatId = update.getMessage().getChatId();
        } else if (update.hasCallbackQuery()) {
            if (update.getCallbackQuery().getMessage() != null) {
                chatId = update.getCallbackQuery().getMessage().getChatId();
            } else {
                chatId = Long.valueOf(update.getCallbackQuery().getFrom().getId());
            }
        } else if (update.hasInlineQuery()) {
            chatId = Long.valueOf(update.getInlineQuery().getFrom().getId());
        } else if (update.hasChosenInlineQuery()) {
            chatId = Long.valueOf(update.getChosenInlineQuery().getFrom().getId());
        } else {
            LOG.error("Error on exception. Unexpected situation. Unrecognized update operation. Update obj:" + update);
            chatId = null;
        }
        return chatId;
    }

    private String commandInfo() {
        return "Please, used follow commands:\n\n" +
                "/start - start work with bot\n" +
                "/read - prices on products\n" +
                "/history - history of price changes\n" +
                "/subscription - subscriptions of user\n" +
                "/subscribe - subscribe for get update on change assortment and prices\n" +
                "/unsubscribe - unsubscribe from notifications\n" +
                "/help - show a help message\n";
    }

}
