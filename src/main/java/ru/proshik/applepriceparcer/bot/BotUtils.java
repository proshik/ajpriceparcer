package ru.proshik.applepriceparcer.bot;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.proshik.applepriceparcer.model.SelectedProduct;

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


    public static InlineKeyboardMarkup buildInlineKeyboard(Map<String, SelectedProduct> items, int onLine) {

        List<InlineKeyboardButton> inlineButtons = new ArrayList<>();
        for (Map.Entry<String, SelectedProduct> item : items.entrySet()) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(item.getKey());
            try {
                String callbackObject = objectMapper.writeValueAsString(item.getValue());
                button.setCallbackData(callbackObject);
                inlineButtons.add(button);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                throw new RuntimeException("Error on write value as string");
            }
        }

        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        int n = 0;
        for (int i = 0; i < inlineButtons.size(); i += onLine) {
            List<InlineKeyboardButton> rowButton = new ArrayList<>();
            for (int y = 0; y < onLine; y++) {
                rowButton.add(inlineButtons.get(i + y));
            }
            keyboard.add(n, rowButton);
            n++;
        }

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(keyboard);

        return markup;
    }

    public static ReplyKeyboardMarkup buildReplyKeyboard(List<List<String>> groups) {
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
