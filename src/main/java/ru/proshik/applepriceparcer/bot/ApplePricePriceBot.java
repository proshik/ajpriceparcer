package ru.proshik.applepriceparcer.bot;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.proshik.applepriceparcer.model.ProductType;
import ru.proshik.applepriceparcer.service.CommandService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ApplePricePriceBot extends TelegramLongPollingBot {

    private final String botUsername;
    private final String botToken;

    //    private ShopService shopService;
    private CommandService commandService;

    public ApplePricePriceBot(String botUsername,
                              String botToken,
                              CommandService commandService) {
        this.botUsername = botUsername;
        this.botToken = botToken;
        this.commandService = commandService;
    }

    /*
    By default:
    Replay: Shops, Subscription
    After in shops: list from provider
    After provider show types of products
    Inline: After show title of items and after click concret item you will see
    command history or changes
     */
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {

            SendMessage message = new SendMessage() // Create a SendMessage object with mandatory fields
                    .setChatId(update.getMessage().getChatId());

            if (update.getMessage().isCommand()) {
                switch (update.getMessage().getText().split(" ")[0]) {
                    case "/start":
                        message.setReplyMarkup(replayKeyboard());
                        message.setText("Replay keyboard test");
//                        message.setText(commandInfo());
                        break;
                    case "/shops":
                        String shopsText = commandService.shops();
                        message.setText(shopsText);
                        break;
                    case "/read":
                        String readText = BotUtils.extractArgument(update.getMessage().getText())
                                .map(s -> commandService.read(s))
                                .orElse("Need set a argument for command. It is a title of shop.");
                        message.setText(readText);
                        break;
                    case "/history":
                        String historyText = BotUtils.extractArgument(update.getMessage().getText())
                                .map(s -> commandService.history(s))
                                .orElse("Need set a argument for command. It is a title of shop.");
                        message.setText(historyText);
                        break;
                    case "/diff":
//                        String diffText = BotUtils.extractArgument(update.getMessage().getText())
//                                .map(s -> commandService.history(s))
//                                .orElse("Need set a argument for command. It is a title of shop.");
                        message.setReplyMarkup(keyboard());
                        message.setText("Result diff command text");
                        break;

                    default:
                        message.setText(commandInfo());
                }
            } else {
//                List<String> shopList = commandService.shopsKeyboard();
//                if (shopList.contains(update.getMessage().getText())){
//
//                }
//                for (String shop : shopList) {
//                    if (shop.equals(update.getMessage().getText())) {
//
//                    }
//                }

                switch (update.getMessage().getText()) {
                    case "Shops":
                        List<String> shopList = commandService.shopsKeyboard();
//                        List<List<String>> group = shopList.stream()
//                                .map(s -> {
//                                    List<String> item = new ArrayList<>();
//                                    item.add(s);
//
//                                    return item;
//                                })
//                                .collect(Collectors.toList());
//
//                        ReplyKeyboardMarkup markup = buildReplyKeyboard(group);
                        InlineKeyboardMarkup markup = buildKeyboard(shopList);
                        message.setReplyMarkup(markup);
                        message.setText("Select shop");
                        break;
                    case "Subscription":
                        message.setText("Not implement yet!");
                        break;
                }
//                update.getMessage().getText();
//                message.setText(commandInfo());
            }
            try {
                execute(message); // Call method to send the message
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        } else if (update.hasCallbackQuery()) {
            SendMessage message = new SendMessage() // Create a SendMessage object with mandatory fields
                    .setChatId(String.valueOf(update.getCallbackQuery().getFrom().getId()));

            List<String> productTypes = Arrays.stream(ProductType.values())
                    .map(ProductType::getValue)
//                    .sorted()
                    .collect(Collectors.toList());

            InlineKeyboardMarkup markup = buildKeyboard(productTypes);
            message.setReplyMarkup(markup);

//            message.setReplyMarkup(keyboard());
            message.setText("Select product type");

//            AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
//            answerCallbackQuery.setText("Async answer");
//            answerCallbackQuery.setCallbackQueryId(update.getCallbackQuery().getId());

//            try {
//                executeAsync(answerCallbackQuery, new MyCallback());
//            } catch (TelegramApiException e) {
//                e.printStackTrace();
//            }
//            try {
//                Thread.sleep(2000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            try {
                execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }

//            System.out.println("Update not contains message");
        } else {
            SendMessage message = new SendMessage() // Create a SendMessage object with mandatory fields
                    .setChatId(update.getMessage().getChatId());
            message.setReplyMarkup(replayKeyboard());
            message.setText("Replay keyboard test");

            try {
                execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
            System.out.println("Update not contains message");
//            SendMessage message = new SendMessage() // Create a SendMessage object with mandatory fields
//                    .setChatId(String.valueOf(update.getCallbackQuery().getFrom().getId()));
        }
    }

    private InlineKeyboardMarkup buildKeyboard(List<String> items) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        List<InlineKeyboardButton> inlineButtons = new ArrayList<>();
        for (String item : items) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(item);
            button.setCallbackData(item + "_callback");
            inlineButtons.add(button);
        }

        keyboard.add(inlineButtons);
        markup.setKeyboard(keyboard);
        return markup;
    }

    private ReplyKeyboardMarkup buildReplyKeyboard(List<List<String>> groups) {
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();

        List<KeyboardRow> keyboardRows = new ArrayList<>();
        for (List<String> group : groups) {
            List<KeyboardButton> buttons = new ArrayList<>();
            for (String item : group) {
                KeyboardButton button = new KeyboardButton();
                button.setText(item);
                buttons.add(button);
            }
            KeyboardRow row = new KeyboardRow();
            row.addAll(buttons);
            keyboardRows.add(row);
        }

        markup.setKeyboard(keyboardRows);

        return markup;
    }

//    class MyCallback implements SentCallback {
//
//        @Override
//        public void onResult(BotApiMethod method, Serializable response) {
//            System.out.println("OnResult method");
//        }
//
//        @Override
//        public void onError(BotApiMethod method, TelegramApiRequestException apiException) {
//            System.out.println("OnError method");
//        }
//
//        @Override
//        public void onException(BotApiMethod method, Exception exception) {
//            System.out.println("On exception method");
//        }
//    }

    private String commandInfo() {
        StringBuilder startText = new StringBuilder("Bot for show info about prices on Apple products from several online shop in SpB\n");
        startText.append("/shops").append(" - ").append("list of shops for parsing").append("\n");
        startText.append("/read <shop_title>").append(" - ")
                .append("show current prices on assortment in this shop. Argument <shop_title> it is a title of show from result /shops command.")
                .append("\n");
        return startText.toString();
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
//

    private InlineKeyboardMarkup keyboard() {
        final InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        InlineKeyboardButton shopsButton = new InlineKeyboardButton();
        shopsButton.setText("Shops");
        shopsButton.setCallbackData("shopsButtonCallback");

        InlineKeyboardButton subscriptionButton = new InlineKeyboardButton();
        subscriptionButton.setText("Subscription");
        subscriptionButton.setCallbackData("subscriptionButtonCallback");

        keyboard.add(Arrays.asList(shopsButton, subscriptionButton));
        markup.setKeyboard(keyboard);
        return markup;
    }

    private ReplyKeyboardMarkup replayKeyboard() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();

        KeyboardButton shopButton = new KeyboardButton();
        shopButton.setText("Shops");

        KeyboardButton subscrptionButton = new KeyboardButton();
        subscrptionButton.setText("Subscription");

        KeyboardRow shopRow = new KeyboardRow();
        shopRow.add(shopButton);

        KeyboardRow subscriptionRow = new KeyboardRow();
        subscriptionRow.add(subscrptionButton);

        List<KeyboardRow> keyboardRows = new ArrayList<>();
        keyboardRows.add(shopRow);
        keyboardRows.add(subscriptionRow);

        replyKeyboardMarkup.setKeyboard(keyboardRows);

        return replyKeyboardMarkup;
    }


//    public InlineKeyboardButton buttonMain() {
//        InlineKeyboardButton button = new InlineKeyboardButtonBuilder()
//                .setText("Начать!" + winking_face)
//                .setCallbackData(new ActionBuilder(marshaller)
//                        .setName(OPEN_MAIN)
//                        .asString())
//                .build();
//        return button;
//    }
//
//    public InlineKeyboardMarkup keyboardAnswer(Update update, ClsQuest quest) {
//        final InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
//        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
//        for (ClsAnswer clsAnswer : quest.getClsAnswerCollection()) {
//            keyboard.add(Arrays.asList(buttonAnswer(clsAnswer)));
//        }
//        markup.setKeyboard(keyboard);
//        return markup;
//    }
//
//    public InlineKeyboardButton buttonAnswer(ClsAnswer clsAnswer) {
//        InlineKeyboardButton button = new InlineKeyboardButtonBuilder()
//                .setText(clsAnswer.getAnswerText())
//                .setCallbackData(new ActionBuilder(marshaller)
//                        .setName(GET_ANSWER)
//                        .setValue(clsAnswer.getId().toString())
//                        .asString())
//                .build();
//        return button;
//    }
}
