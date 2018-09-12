package ru.proshik.applepricebot.service.bot;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class BotMessageHandler {

    private static final Logger LOG = Logger.getLogger(BotMessageHandler.class);

    public BotApiMethod<Message> onWebhookUpdateReceived(Update update) {
        // 1. build custom keyboard after any /start command and any messages. Buttons: Information, Statistics, Notifications.
        // Information shows the actual information by prices into shops. Statistic shows a change the prices between dates and dynamics by long period.
        // Notifications need for subscribe on several events like change prices for shops and products and avalable new products.
        SendMessage message;
        try {
            if (update.hasMessage()) {
                switch (update.getMessage().getText().split(" ")[0]) {
                    case "/start":
                        message = new SendMessage()
                                .setChatId(update.getMessage().getChatId())
//                                .setReplyMarkup(buildRootMenuKeyboard())
                                .setText("Hello, this is Bot for follow prices on apple products in several shops.");
                        break;
                    default:
                        message = buildCommandInfoMessage(update);
                }
            } else if (update.hasCallbackQuery()) {
                message = processCallbackOperation(update);
            } else {
                LOG.error("unrecognized update operation. Update obj: " + update.toString());
                throw new RuntimeException("Unexpected situation");
            }
        } catch (Exception e) {
            LOG.error("Error on build message", e);
            message = new SendMessage()
                    .setChatId(extractChatId(update))
                    .setText("Error on execute operation! Connect with support!");
        }

        return message;
//        return new SendMessage().setChatId(update.getMessage().getChatId())
//                .setText("Hello " + update.getMessage().getText() + "!");
    }


    private SendMessage processCallbackOperation(Update update) {

        SendMessage message = new SendMessage();

        String data = update.getCallbackQuery().getData();

        message.setChatId(update.getMessage().getChatId())
                .setText("test");
//        if (StringUtils.isNotEmpty(data)) {
//
//            CallbackInfo callbackInfo = extractCallbackInfo(data);
//            DataSequence sequenceData = sequenceOperationStorage.get(callbackInfo.getId());
//
//            if (sequenceData != null) {
//                switch (sequenceData.getOperationType()) {
//                    case PRICES:
//                        message = callbackSimpleOperation(update, sequenceData, callbackInfo);
//                        break;
//                    case HISTORY:
//                        message = callbackSimpleOperation(update, sequenceData, callbackInfo);
//                        break;
//                    case SUBSCRIPTION:
//                        message = callbackSubscriptionOperation(update, callbackInfo);
//                        break;
//                    default:
//                        message.setReplyMarkup(buildRootMenuKeyboard())
//                                .setChatId(update.getCallbackQuery().getMessage().getChatId())
//                                .setText("Operation ended with error. Please start from the begin!");
//                }
//            }
//        } else {
//            message = buildMainMenuMessage(update);
//        }
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

    private SendMessage buildCommandInfoMessage(Update update) {
        return new SendMessage()
                .setChatId(update.getMessage().getChatId())
                .setText("Please, used follow commands:\n\n" +
                        "/start - for start work with bot\n" +
                        "/help - show a help message\n");
    }

}
