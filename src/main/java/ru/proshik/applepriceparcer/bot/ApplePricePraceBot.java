package ru.proshik.applepriceparcer.bot;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.proshik.applepriceparcer.provider.ProviderFactory;
import ru.proshik.applepriceparcer.provider.screener.Screener;

import java.util.List;
import java.util.stream.Collectors;

public class ApplePricePraceBot extends TelegramLongPollingBot {

    private final String botUsername;
    private final String botToken;

//    private Screener screener = new AjScreener();

    public ApplePricePraceBot(String botUsername, String botToken) {
        this.botUsername = botUsername;
        this.botToken = botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            if (update.getMessage().isCommand()) {
                SendMessage message = new SendMessage() // Create a SendMessage object with mandatory fields
                        .setChatId(update.getMessage().getChatId());

                switch (update.getMessage().getText()) {
                    case "/read":
//                        String text = AjScreener.buildAssortmentOut(screener.screening());
                        message.setText("This will be read command");
                        break;
                    case "/shops":
                        List<Screener> list = ProviderFactory.list();

                        String shopsText = list.stream()
                                .map(s -> s.supplier().getTitle() + " " + s.supplier().getUrl())
                                .collect(Collectors.joining("", "", "\n"));

                        message.setText(shopsText);
                        break;
                    default:
                        message.setText("Unknown command");
                }
                try {
                    execute(message); // Call method to send the message
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else {
                SendMessage message = new SendMessage() // Create a SendMessage object with mandatory fields
                        .setChatId(update.getMessage().getChatId())
                        .setText(update.getMessage().getText());
                try {
                    execute(message); // Call method to send the message
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        } else {
            System.out.println("Update not contains message");
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
}
