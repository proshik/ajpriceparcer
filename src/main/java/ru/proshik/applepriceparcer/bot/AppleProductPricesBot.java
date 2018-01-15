package ru.proshik.applepriceparcer.bot;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
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
            switch (operationType) {
                case PRICES:
                    message = buildShopStep(update);
                    break;
                case HISTORY:
                    message.setText("Not implement yet!");

                    break;
                case COMPARE:
                    message.setText("Not implement yet!");
                    break;
                case SUBSCRIPTION:
                    message.setText("Not implement yet!");
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
                        message = priceCallbackOperation(update, callbackInfo);
                        break;
                    case SUBSCRIPTION:
                        // TODO: 15.01.2018
                        break;
                    default:
                        message.setReplyMarkup(buildRootMenuKeyboard());
                        message.setChatId(update.getCallbackQuery().getMessage().getChatId());
                        message.setText("Error on execution PriceOperation. Please start from the beginning!");
                }
            }
        } else {
            message = buildMainMenuMessage(update);
        }
        return message;
    }

    private SendMessage priceCallbackOperation(Update update, CallbackInfo callbackInfo) {
        SendMessage message = new SendMessage();

        Shop shop;
        DataSequence sequenceData = sequenceOperationStorage.get(callbackInfo.getId());
        switch (sequenceData.getStepType()) {
            case SHOP_SELECTED:
                shop = operationService.findShopByTitle(callbackInfo.getValue());
                if (shop == null) {
                    message.setReplyMarkup(buildRootMenuKeyboard());
                    message.setText("Error on execution PriceOperation. Please start from the beginning!");
                    break;
                }

                sequenceData.setStepType(StepType.PRODUCT_TYPE_SELECTED);
                sequenceData.getData().setShop(shop);
                sequenceOperationStorage.put(callbackInfo.getId(), sequenceData);

                List<ProductType> productTypes = operationService.selectUniqueProductTypes(shop);
                if (productTypes.isEmpty()) {
                    message.setReplyMarkup(buildRootMenuKeyboard());
                    message.setText("No available product types for selected shop");
                    break;
                }

                Map<String, String> productTypeValueTyEnumNam = operationService.selectUniqueProductTypes(shop).stream()
                        .collect(Collectors.toMap(Enum::name, ProductType::getValue));

                message.setReplyMarkup(buildInlineKeyboard(productTypeValueTyEnumNam, callbackInfo.getId(), 3));
                message.setText("Shop: *" + shop.getTitle() + "*\n\n" +
                        "Select the type of product which is available for selected shop");
                message.enableMarkdown(true);
                break;
            case PRODUCT_TYPE_SELECTED:
                shop = sequenceData.getData().getShop();
                ProductType productType = ProductType.fromValue(callbackInfo.getValue());

                String history = operationService.read(shop, productType);

                message.enableMarkdown(true);
                message.setReplyMarkup(buildRootMenuKeyboard());
                message.setText("Shop: *" + shop.getTitle() + "*\n" +
                        "Product type: *" + productType.getValue() + "*\n\n" +
                        history);
                break;
            default:
                message.setReplyMarkup(buildRootMenuKeyboard());
                message.setText("Error on execution PriceOperation. Please start from the beginning!");
        }
        message.setChatId(update.getCallbackQuery().getMessage().getChatId());
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
            throw new RuntimeException("Error on read value from callback", e);
        }
    }

    private SendMessage buildShopStep(Update update) {
        Map<String, String> shopMap = operationService.selectAvailableShops().stream()
                .collect(Collectors.toMap(Shop::getTitle, Shop::getTitle));

        String chatId = String.valueOf(update.getMessage().getChatId());

        InlineKeyboardMarkup inlineKeyboardMarkup = buildInlineKeyboard(shopMap, String.valueOf(chatId), 1);
        SendMessage message = new SendMessage()
                .setReplyMarkup(inlineKeyboardMarkup)
                .setChatId(chatId)
                .setText("Select the shop for continue");

        sequenceOperationStorage.put(chatId, new DataSequence(OperationType.PRICES, StepType.SHOP_SELECTED));

        return message;
    }

    private SendMessage buildGreetingsMessage(Update update) {
        return new SendMessage()
                .setChatId(update.getMessage().getChatId())
                .setReplyMarkup(buildRootMenuKeyboard())
                .setText("Hello, this is Bot for show price for apple products in show SPB and Moscow. " +
                        "You may select shops for check prices and change history price and assortment in shops. " +
                        "And subscribe on a change prices.");
    }

    private SendMessage buildCommandInfoMessage(Update update) {
        return new SendMessage()
                .setChatId(update.getMessage().getChatId())
                .setText("Show prices on Apple products by several shops in SPB and Moscow.\n\n" +
                        "/start - for start work with bot\n" +
                        "/help - show a help message\n");
    }

    private SendMessage buildMainMenuMessage(Update update) {
        return new SendMessage()
                .setChatId(update.getMessage().getChatId())
                .setReplyMarkup(buildRootMenuKeyboard())
                .setText("You are in Main menu. For send text messages, please use a keyboard.\n\n " +
                        "Select one action from list below!");
    }

    private ReplyKeyboardMarkup buildRootMenuKeyboard() {
        return buildReplyKeyboard(ROOT_MENU);
    }

}
