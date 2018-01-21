package ru.proshik.applepriceparcer.bot;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.proshik.applepriceparcer.model.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BotUtils {

    public static final ObjectMapper objectMapper = new ObjectMapper();

    public static Optional<String> extractArgument(String text) {
        return Stream.of(text.split(" "))
                .skip(1)
                .findFirst();
    }

    public static List<String> extractArguments(String text) {
        return Stream.of(text.split(" "))
                .skip(1)
                .collect(Collectors.toList());
    }


    static InlineKeyboardMarkup buildInlineKeyboard(Map<String, String> items, String callbackId, int onLine) {

        List<InlineKeyboardButton> inlineButtons = new ArrayList<>();
        for (Map.Entry<String, String> item : items.entrySet()) {

            String callbackData;
            try {
                callbackData = objectMapper.writeValueAsString(new CallbackInfo(callbackId, item.getKey()));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Error on write value as string", e);
            }

            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(item.getValue());
            button.setCallbackData(callbackData);
            inlineButtons.add(button);
        }

        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        // In elements less then resolution of table buttons then change count elements on line
        if (items.size() < onLine) {
            onLine = items.size();
        }

        int i = 1;
        List<InlineKeyboardButton> rowButton = new ArrayList<>();
        for (int n = 0; n < inlineButtons.size(); n++) {
            if (i == onLine) {
                rowButton.add(inlineButtons.get(n));
                keyboard.add(rowButton);

                rowButton = new ArrayList<>();
                i = 1;
            } else {
                rowButton.add(inlineButtons.get(n));
                i++;
            }
            if (n == inlineButtons.size() - 1) {
                keyboard.add(rowButton);
            }
        }

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(keyboard);

        return markup;
    }

    static ReplyKeyboardMarkup buildReplyKeyboard(List<List<String>> groups) {
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
        markup.setResizeKeyboard(true);
        markup.setOneTimeKeyboard(true);

        return markup;
    }
}
