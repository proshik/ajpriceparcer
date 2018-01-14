package ru.proshik.applepriceparcer.bot;

import org.apache.commons.lang3.StringUtils;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.proshik.applepriceparcer.model.ProductType;
import ru.proshik.applepriceparcer.model.SelectedProduct;
import ru.proshik.applepriceparcer.model.Shop;
import ru.proshik.applepriceparcer.service.CommandService;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;
import static ru.proshik.applepriceparcer.bot.BotUtils.buildInlineKeyboard;
import static ru.proshik.applepriceparcer.bot.BotUtils.buildReplyKeyboard;

public class ApplePricePriceBot extends TelegramLongPollingBot {

    private static final String RM_SHOPS = "Shops";
    private static final String RM_SUBSCRIPTION = "Subscription";
    private static final String RM_MAIN_MENU = "Main menu";

    private static final List<List<String>> ROOT_MENU = Arrays.asList(
            singletonList(RM_SHOPS),
            singletonList(RM_SUBSCRIPTION),
            singletonList(RM_MAIN_MENU));

    private final String botUsername;
    private final String botToken;

    private CommandService commandService;

    public ApplePricePriceBot(String botUsername,
                              String botToken,
                              CommandService commandService) {
        this.botUsername = botUsername;
        this.botToken = botToken;
        this.commandService = commandService;
    }

    @Override
    public void onUpdateReceived(Update update) {
        SendMessage message;
        if (update.hasMessage() && update.getMessage().hasText()) {
            message = processMessageOperation(update);
        } else if (update.hasCallbackQuery()) {
            message = processCallbackOperation(update);
        } else {
            message = cleanUpUserSpace(update);
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
        SendMessage message = new SendMessage() // Create a SendMessage object with mandatory fields
                .setChatId(String.valueOf(update.getCallbackQuery().getFrom().getId()));

        String callbackData = update.getCallbackQuery().getData();
        if (StringUtils.isNotEmpty(callbackData)) {
            SelectedProduct selectedProduct;
            try {
                selectedProduct = BotUtils.objectMapper.readValue(callbackData, SelectedProduct.class);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Error on read value from callback");
            }

            if (selectedProduct != null) {
//                if (selectedProduct.emptyShop()) {
//                    InlineKeyboardMarkup inlineKeyboardMarkup = buildShopKeyboard();
//
//                    message.setReplyMarkup(inlineKeyboardMarkup);
//                    message.setText("Select shop");
//                } else
                if (selectedProduct.emptyProductType()) {
                    Map<String, SelectedProduct> map = new HashMap<>();

                    List<ProductType> productTypes = Arrays.stream(ProductType.values())
                            .collect(Collectors.toList());
                    for (ProductType pt : productTypes) {
                        map.put(pt.getValue(), new SelectedProduct(selectedProduct.getShopTitle(), pt.name()));
                    }
                    message.setReplyMarkup(buildInlineKeyboard(map, 2));

                    message.setText("Select product type");
                } else {
                    String history = commandService.history(selectedProduct.getShopTitle());

                    message.setReplyMarkup(buildReplyKeyboard(ROOT_MENU));
                    message.setText(history);
                }
            }
        } else {
            message.setReplyMarkup(buildReplyKeyboard(ROOT_MENU));
            message.setText("Select operation");
        }

        return message;
    }

    private SendMessage processCommandMessageOperation(Update update) {
        SendMessage message;

        switch (update.getMessage().getText().split(" ")[0]) {
            case "/start":
                message = processGreetingOperation(update);
                break;
//            case "/shopsDescription":
//                String shopsText = commandService.shopsDescription();
//                message.setText(shopsText);
//                break;
//            case "/read":
//                String readText = BotUtils.extractArgument(update.getMessage().getText())
//                        .map(s -> commandService.read(s))
//                        .orElse("Need set a argument for command. It is a title of shop.");
//                message.setText(readText);
//                break;
//            case "/history":
//                String historyText = BotUtils.extractArgument(update.getMessage().getText())
//                        .map(s -> commandService.history(s))
//                        .orElse("Need set a argument for command. It is a title of shop.");
//                message.setText(historyText);
//                break;
//            case "/diff":
//                message.setText("Not implement yet!");
//                break;

            default:
                message = cleanUpUserSpace(update);
//                message.setText(commandInfo());
        }

        return message;
    }


    private SendMessage processGreetingOperation(Update update) {
        return new SendMessage()
                .setChatId(update.getMessage().getChatId())
                .setReplyMarkup(buildReplyKeyboard(ROOT_MENU))
                .setText("Hello, this is Bot for show price for apple products in show SPB and Moscow. " +
                        "You may select shops for check prices and change history price and assortment in shops. " +
                        "And subscribe on a change prices.");
    }

    private SendMessage processKeyboardMessageOperation(Update update) {
        SendMessage message = new SendMessage();

        switch (update.getMessage().getText()) {
            case RM_SHOPS:
                InlineKeyboardMarkup inlineKeyboardMarkup = buildShopKeyboard();

                message.setReplyMarkup(inlineKeyboardMarkup);
                message.setText("Select shop");
                break;
            case RM_SUBSCRIPTION:
                message.setText("Not implement yet!");
                break;
            case RM_MAIN_MENU:
                message = cleanUpUserSpace(update);
                break;
            default:
                message = cleanUpUserSpace(update);
        }

        return message;
    }

    private InlineKeyboardMarkup buildShopKeyboard() {
        List<Shop> shopList = commandService.shopList();

        Map<String, SelectedProduct> map = new HashMap<>();
        for (Shop shop : shopList) {
            map.put(shop.getTitle(), new SelectedProduct(shop.getTitle()));
        }

        return buildInlineKeyboard(map, 1);
    }

    private SendMessage cleanUpUserSpace(Update update){
        return new SendMessage()
                .setChatId(update.getMessage().getChatId())
                .setReplyMarkup(buildReplyKeyboard(ROOT_MENU))
                .setText("For send text messages, please use a keyboard.\n\n" +
                        "This is Bot for show price for apple products in show SPB and Moscow. " +
                        "You may select shops for check prices and change history price and assortment in shops. " +
                        "And subscribe on a change prices.");
    }

    private String commandInfo() {
        StringBuilder startText = new StringBuilder("Bot for show info about prices on Apple products from several online shop in SpB\n");
        startText.append("/shopsDescription").append(" - ").append("list of shopsDescription for parsing").append("\n");
        startText.append("/read <shop_title>").append(" - ")
                .append("show current prices on assortment in this shop. Argument <shop_title> it is a title of show from result /shopsDescription command.")
                .append("\n");
        return startText.toString();
    }

}
