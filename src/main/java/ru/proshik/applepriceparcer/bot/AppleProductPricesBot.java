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
import ru.proshik.applepriceparcer.service.CommandService;
import ru.proshik.applepriceparcer.storage.Database;

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

    private static final Logger LOG = Logger.getLogger(Database.class);

    private static final String COMMAND_START = "/start";

    private final List<List<String>> ROOT_MENU = Arrays.asList(
            singletonList(OperationType.PRICES.getValue()),
            singletonList(OperationType.HISTORY.getValue()),
//                singletonList(OperationType.COMPARE.getValue()),
            singletonList(OperationType.SUBSCRIPTION.getValue()),
            singletonList(OperationType.MAIN_MENU.getValue()));

    private final String botUsername;
    private final String botToken;

    private CommandService commandService;

    private Map<String, DataSequence> callbackSequence = new HashMap<>();

    public AppleProductPricesBot(String botUsername,
                                 String botToken,
                                 CommandService commandService) {
        this.botUsername = botUsername;
        this.botToken = botToken;
        this.commandService = commandService;
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
                message = shopMainMenu(update);
            }
        } catch (Exception e) {
            LOG.error(e);
            message = new SendMessage()
                    .setText("Error on execute PriceOperation! Connect with support!");
        }

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
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
        System.out.println("Bot was closing");
    }

    private SendMessage processMessageOperation(Update update) {
        SendMessage message;

        if (update.getMessage().isCommand()) {
            message = processCommandMessageOperation(update);
        } else {
            message = processKeyboardMessageOperation(update);
        }
        message.setChatId(update.getMessage().getChatId());
        return message;
    }

    private SendMessage processCallbackOperation(Update update) {
        String chatId = String.valueOf(update.getCallbackQuery().getFrom().getId());

        SendMessage message = new SendMessage()
                .setChatId(String.valueOf(chatId));

        // TODO: 15.01.2018 проверить для всех кейсов и если что - заменить
        update.getCallbackQuery().getMessage().getChatId();

        String data = update.getCallbackQuery().getData();
        if (StringUtils.isNotEmpty(data)) {
            CallbackInfo callbackInfo = extractCallbackInfo(data);

            DataSequence sequenceData = callbackSequence.get(callbackInfo.getId());

            if (sequenceData != null) {
                switch (sequenceData.getOperationType()) {
                    case PRICES:
                        switch (sequenceData.getStepType()) {
                            case SHOP_SELECTED:
                                Shop shop = commandService.findShopByTitle(callbackInfo.getValue());

                                sequenceData.setStepType(StepType.PRODUCT_TYPE_SELECTED);
                                sequenceData.getData().setShop(shop);
                                callbackSequence.put(callbackInfo.getId(), sequenceData);

                                Map<String, String> productTypes = commandService.productTypes(callbackInfo.getValue()).stream()
                                        .collect(Collectors.toMap(Enum::name, ProductType::getValue));

                                message.setReplyMarkup(buildInlineKeyboard(productTypes, callbackInfo.getId(), 3));
                                message.setText("Shop: *" + shop.getTitle() + "*\n\n" +
                                        "Select the type of product which is available for selected shop");
                                message.enableMarkdown(true);
                                break;
                            case PRODUCT_TYPE_SELECTED:
                                Shop shop1 = sequenceData.getData().getShop();
                                ProductType productType = ProductType.fromValue(callbackInfo.getValue());

                                String history = commandService.history(shop1, productType);

                                message.enableMarkdown(true);
                                message.setReplyMarkup(buildRootMenuKeyboard());
                                message.setText("Shop: *" + shop1.getTitle() + "*\n" +
                                        "Product type: *" + productType.getValue() + "*\n\n" +
                                        history);
                                break;
                            default:
                                message.setReplyMarkup(buildRootMenuKeyboard());
                                message.setText("Error on execution PriceOperation. Please start from the beginning!");
                        }
                        break;
                    default:
                        message.setReplyMarkup(buildRootMenuKeyboard());
                        message.setText("Error on execution PriceOperation. Please start from the beginning!");
                }

            }
        } else {
            message = shopMainMenu(update);
        }

        return message;
    }

    private CallbackInfo extractCallbackInfo(String data) {
        try {
            return BotUtils.objectMapper.readValue(data, CallbackInfo.class);
        } catch (IOException e) {
            LOG.error(e);
            throw new RuntimeException("Error on read value from callback", e);
        }
    }

    private SendMessage processCommandMessageOperation(Update update) {
        SendMessage message;

        switch (update.getMessage().getText().split(" ")[0]) {
            case COMMAND_START:
                message = showGreetings(update);
                break;
            default:
                message = shopMainMenu(update);
        }

        return message;
    }

    private SendMessage processKeyboardMessageOperation(Update update) {
        SendMessage message = new SendMessage();

        OperationType operationType = OperationType.fromValue(update.getMessage().getText());
        switch (operationType) {
            case PRICES:
                message = buildShopStep(String.valueOf(update.getMessage().getChatId()));
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
                message = shopMainMenu(update);
        }
        return message;
    }

    private SendMessage buildShopStep(String chatId) {
        InlineKeyboardMarkup inlineKeyboardMarkup = buildShopKeyboard(chatId);

        return new SendMessage()
                .setReplyMarkup(inlineKeyboardMarkup)
                .setText("Select the shop for continue");
    }

    private InlineKeyboardMarkup buildShopKeyboard(String chatId) {
        List<Shop> shopList = commandService.shopList();

        callbackSequence.put(chatId, new DataSequence(OperationType.PRICES, StepType.SHOP_SELECTED));

        Map<String, String> shopMap = shopList.stream()
                .collect(Collectors.toMap(Shop::getTitle, Shop::getTitle));
        return buildInlineKeyboard(shopMap, chatId, 1);
    }

    private SendMessage showGreetings(Update update) {
        return new SendMessage()
                .setChatId(update.getMessage().getChatId())
                .setReplyMarkup(buildRootMenuKeyboard())
                .setText("Hello, this is Bot for show price for apple products in show SPB and Moscow. " +
                        "You may select shops for check prices and change history price and assortment in shops. " +
                        "And subscribe on a change prices.");
    }

    private SendMessage shopMainMenu(Update update) {
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
