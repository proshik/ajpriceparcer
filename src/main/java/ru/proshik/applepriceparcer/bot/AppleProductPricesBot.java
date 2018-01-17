package ru.proshik.applepriceparcer.bot;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.proshik.applepriceparcer.exception.DatabaseException;
import ru.proshik.applepriceparcer.exception.ServiceLayerException;
import ru.proshik.applepriceparcer.model.*;
import ru.proshik.applepriceparcer.model.sequence.DataSequence;
import ru.proshik.applepriceparcer.service.OperationService;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;
import static ru.proshik.applepriceparcer.bot.BotUtils.buildInlineKeyboard;
import static ru.proshik.applepriceparcer.bot.BotUtils.buildReplyKeyboard;
import static ru.proshik.applepriceparcer.bot.PrintUtils.buildHistory;
import static ru.proshik.applepriceparcer.bot.PrintUtils.printAssortment;
import static ru.proshik.applepriceparcer.model.StepType.SHOP_SELECTED;

public class AppleProductPricesBot extends TelegramLongPollingBot {

    private static final Logger LOG = Logger.getLogger(AppleProductPricesBot.class);

    // Commands
    private static final String COMMAND_START = "/start";
    private static final String COMMAND_HELP = "/help";

    // Root menu elements
    private final List<List<String>> ROOT_MENU = Arrays.asList(
            singletonList(OperationType.PRICES.getValue()),
            singletonList(OperationType.HISTORY.getValue()),
//            singletonList(OperationType.COMPARE.getValue()),
            singletonList(OperationType.SUBSCRIPTION.getValue()),
            singletonList(OperationType.MAIN_MENU.getValue()));

    // Telegram bot settings
    private final String botUsername;
    private final String botToken;

    // Injected services
    private OperationService operationService;
    // In-memory map for user steps
    private Map<String, DataSequence> sequenceOperationStorage = new HashMap<>();

    public AppleProductPricesBot(String botUsername,
                                 String botToken,
                                 OperationService operationService) {
        this.botUsername = botUsername;
        this.botToken = botToken;
        this.operationService = operationService;
    }

    @Override
    public void onUpdateReceived(Update update) {

        SendMessage message;
        try {
            if (update.hasMessage()) {
                message = processMessageOperation(update);
            } else if (update.hasCallbackQuery()) {
                message = processCallbackOperation(update);
            } else {
                LOG.error("Unexpected situation. Unrecognized update operation. Update obj: " + update.toString());
                return;
            }
        } catch (Exception e) {
            LOG.error("Error on build message", e);
            message = new SendMessage()
                    .setChatId(extractChatId(update))
                    .setText("Error on execute operation! Connect with support!");
        }

        try {
            execute(message);
        } catch (TelegramApiException e) {
            LOG.error("Panic! Messages not sending!", e);
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
            message = processKeyboardMessageOperation(update);
        }
        return message;
    }

    private SendMessage processCommandMessageOperation(Update update) {

        SendMessage message;

        switch (update.getMessage().getText().split(" ")[0]) {
            case COMMAND_START:
                message = buildGreetingsMessage(update);
                break;
            case COMMAND_HELP:
                message = buildCommandInfoMessage(update);
                break;
            default:
                message = buildCommandInfoMessage(update);
        }

        return message;
    }

    private SendMessage processKeyboardMessageOperation(Update update) {

        SendMessage message = new SendMessage();

        OperationType operationType = OperationType.fromValue(update.getMessage().getText());
        if (operationType != null) {
            // clean sequence if before was started other operation
            sequenceOperationStorage.remove(String.valueOf(update.getMessage().getChatId()));
            // select need operationType
            switch (operationType) {
                case PRICES:
                    message = buildPricesFirstStep(update, OperationType.PRICES);
                    break;
                case HISTORY:
                    message = buildPricesFirstStep(update, OperationType.HISTORY);
                    break;
                case COMPARE:
                    message.setText("Not implement yet!")
                            .setChatId(update.getMessage().getChatId())
                            .setReplyMarkup(buildRootMenuKeyboard());
                    break;
                case SUBSCRIPTION:
                    message = buildFirstStepSubscription(update);
                    break;
                case MAIN_MENU:
                default:
                    message = buildMainMenuMessage(update);
            }
        } else {
            message = buildMainMenuMessage(update);
        }
        return message;
    }

    private SendMessage processCallbackOperation(Update update) {

        SendMessage message = new SendMessage();

        String data = update.getCallbackQuery().getData();
        if (StringUtils.isNotEmpty(data)) {

            CallbackInfo callbackInfo = extractCallbackInfo(data);
            DataSequence sequenceData = sequenceOperationStorage.get(callbackInfo.getId());

            if (sequenceData != null) {
                switch (sequenceData.getOperationType()) {
                    case PRICES:
                        message = callbackSimpleOperation(update, sequenceData, callbackInfo);
                        break;
                    case HISTORY:
                        message = callbackSimpleOperation(update, sequenceData, callbackInfo);
                        break;
                    case SUBSCRIPTION:
                        message = callbackSubscriptionOperation(update, callbackInfo);
                        break;
                    default:
                        message.setReplyMarkup(buildRootMenuKeyboard())
                                .setChatId(update.getCallbackQuery().getMessage().getChatId())
                                .setText("Operation ended with error. Please start from the begin!");
                }
            }
        } else {
            message = buildMainMenuMessage(update);
        }
        return message;
    }

    private SendMessage callbackSubscriptionOperation(Update update,
                                                      CallbackInfo callbackInfo) {
        SendMessage message = new SendMessage();
        String chatId = String.valueOf(update.getCallbackQuery().getMessage().getChatId());

        Shop shop = operationService.findShopByTitle(callbackInfo.getValue());
        if (shop == null) {
            return message.setReplyMarkup(buildRootMenuKeyboard())
                    .setText("Unexpected situation. Please start from the begin!");
        }

        try {
            operationService.updateUserSubscriptions(chatId, shop);

            message.setChatId(update.getCallbackQuery().getMessage().getChatId())
//                        .setReplyMarkup(buildRootMenuKeyboard())
                    .enableMarkdown(true)
                    .setText("You add subscription on update from shops: *" + shop.getTitle() + " - " + shop.getUrl() + "*");
        } catch (DatabaseException e) {
            LOG.error("Error on execute database operation", e);
            message.setReplyMarkup(buildRootMenuKeyboard())
                    .setChatId(update.getCallbackQuery().getMessage().getChatId())
                    .setText("Operation ended with error. Please start from the begin!");
        }

        sequenceOperationStorage.remove(chatId);

        return message;
    }

    private SendMessage callbackSimpleOperation(Update update, DataSequence sequenceData, CallbackInfo callbackInfo) {

        SendMessage message = new SendMessage();
        String chatId = String.valueOf(update.getCallbackQuery().getMessage().getChatId());

        switch (sequenceData.getStepType()) {
            case SHOP_SELECTED:
                message = buildShopSelectedStep(callbackInfo, sequenceData);
                break;
            case PRODUCT_TYPE_SELECTED:
                Shop shop = sequenceData.getData().getShop();
                ProductType productType = ProductType.fromValue(callbackInfo.getValue());

                message.enableMarkdown(true);
                message.setReplyMarkup(buildRootMenuKeyboard());
                switch (sequenceData.getOperationType()) {
                    case PRICES:
                        try {
                            String prices = printAssortment(operationService.priceAssortment(shop, productType), productType);
                            message.setText("*Price* operation\n" +
                                    "Shop: *" + shop.getTitle() + "*\n" +
                                    "Product type: *" + productType.getValue() + "*\n\n" +
                                    prices);
                        } catch (ServiceLayerException e) {
                            message.setText("Operation ended with error. Please start from the begin!");
                        }
                        break;
                    case HISTORY:
                        try {
                            String history = buildHistory(operationService.historyAssortments(shop, productType), productType);
                            message.setText("*History* operation\n" +
                                    "Shop: *" + shop.getTitle() + "*\n" +
                                    "Product type: *" + productType.getValue() + "*\n\n" +
                                    history);
                        } catch (ServiceLayerException e) {
                            message.setText("Operation ended with error. Please start from the begin!");
                        }
                        break;
                    default:
//                        message.setReplyMarkup(buildRootMenuKeyboard());
                        message.setText("Unexpected situation. Please start from the begin!");
                }

                sequenceOperationStorage.remove(chatId);
                break;
            default:
                message.setReplyMarkup(buildRootMenuKeyboard());
                message.setText("Unexpected situation. Please start from the begin!");
        }
        message.setChatId(chatId);
        return message;
    }

    private SendMessage buildShopSelectedStep(CallbackInfo callbackInfo, DataSequence sequenceData) {

        SendMessage message = new SendMessage();

        Shop shop = operationService.findShopByTitle(callbackInfo.getValue());
        if (shop == null) {
            message.setReplyMarkup(buildRootMenuKeyboard());
            message.setText("Unexpected situation. Please start from the begin!");
            return message;
        }

        sequenceData.setStepType(StepType.PRODUCT_TYPE_SELECTED);
        sequenceData.getData().setShop(shop);
        sequenceOperationStorage.put(callbackInfo.getId(), sequenceData);

        List<ProductType> productTypes = operationService.selectUniqueProductTypes(shop);
        if (productTypes.isEmpty()) {
            message.setReplyMarkup(buildRootMenuKeyboard());
            message.setText("No available product types for selected shop");
            return message;
        }

        Map<String, String> productTypeValueTyEnumNam = operationService.selectUniqueProductTypes(shop).stream()
                .collect(Collectors.toMap(Enum::name, ProductType::getValue));

        message.setReplyMarkup(buildInlineKeyboard(productTypeValueTyEnumNam, callbackInfo.getId(), 3));
        message.setText("Shop: *" + shop.getTitle() + "*\n\n" +
                "Select product type: ");
        message.enableMarkdown(true);
        return message;
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

    private CallbackInfo extractCallbackInfo(String data) {
        try {
            return BotUtils.objectMapper.readValue(data, CallbackInfo.class);
        } catch (IOException e) {
            LOG.error("Error on extract callback info with Object mapper", e);
            throw new RuntimeException("Error on priceAssortment value from callback", e);
        }
    }

    private SendMessage buildPricesFirstStep(Update update, OperationType operationType) {
        Map<String, String> shopMap = operationService.selectAvailableShops().stream()
                .collect(Collectors.toMap(Shop::getTitle, Shop::getTitle));

        String chatId = String.valueOf(update.getMessage().getChatId());

        InlineKeyboardMarkup inlineKeyboardMarkup = buildInlineKeyboard(shopMap, String.valueOf(chatId), 1);
        SendMessage message = new SendMessage()
                .setReplyMarkup(inlineKeyboardMarkup)
                .setChatId(chatId)
                .setText("Select the shop for continue");

        sequenceOperationStorage.put(chatId, new DataSequence(operationType, SHOP_SELECTED));

        return message;
    }

    private SendMessage buildFirstStepSubscription(Update update) {
        SendMessage message = new SendMessage();

        String chatId = String.valueOf(update.getMessage().getChatId());
        try {
            Pair<List<Shop>, List<Shop>> subscriptions =
                    operationService.userSubscriptions(chatId);

            StringBuilder builder = new StringBuilder();

            if (!subscriptions.getLeft().isEmpty()) {
                String userShops = subscriptions.getLeft().stream()
                        .map(shop -> "*" + shop.getTitle() + " - " + shop.getUrl() + "*")
                        .collect(Collectors.joining(", "));
                builder.append("Your subscriptions: ").append(userShops);
            } else {
                builder.append("*You not have any one subscriptions.*\n\n");
            }
            if (!subscriptions.getRight().isEmpty()) {
                Map<String, String> shopMap = operationService.selectAvailableShops().stream()
                        .collect(Collectors.toMap(Shop::getTitle, Shop::getTitle));

                InlineKeyboardMarkup keyboard = buildInlineKeyboard(shopMap, String.valueOf(chatId), 3);
                message.setReplyMarkup(keyboard);

                builder.append("For subscribe on new shops used follow key: ");
            }
            message.setChatId(chatId)
                    .enableMarkdown(true)
//                    .setReplyMarkup(buildRootMenuKeyboard())
                    .setText(builder.toString());
            sequenceOperationStorage.put(chatId, new DataSequence(OperationType.SUBSCRIPTION, SHOP_SELECTED));
        } catch (DatabaseException e) {
            LOG.error("Error on execute database operation", e);
            message.setChatId(chatId)
                    .setReplyMarkup(buildRootMenuKeyboard())
                    .setText("Unexpected situation. Please start from the begin!");
        }

        return message;
    }

    private SendMessage buildGreetingsMessage(Update update) {
        return new SendMessage()
                .setChatId(update.getMessage().getChatId())
                .setReplyMarkup(buildRootMenuKeyboard())
                .setText("Hello, this is Bot for follow prices on apple products in several shops.");
    }

    private SendMessage buildCommandInfoMessage(Update update) {
        return new SendMessage()
                .setChatId(update.getMessage().getChatId())
                .setText("Please, used follow commands:\n\n" +
                        "/start - for start work with bot\n" +
                        "/help - show a help message\n");
    }

    private SendMessage buildMainMenuMessage(Update update) {
        return new SendMessage()
                .setChatId(update.getMessage().getChatId())
                .setReplyMarkup(buildRootMenuKeyboard())
                .setText("You are in Main menu. For send text messages, please use a keyboard.\n");
    }

    private ReplyKeyboardMarkup buildRootMenuKeyboard() {
        return buildReplyKeyboard(ROOT_MENU);
    }

}
