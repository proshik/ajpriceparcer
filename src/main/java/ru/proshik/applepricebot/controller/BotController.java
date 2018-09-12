package ru.proshik.applepricebot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.proshik.applepricebot.service.bot.BotMessageHandler;

@RestController
public class BotController {

    @Autowired
    private BotMessageHandler botMessageHandler;

    @PostMapping("/callback/${APPLEPRICESBOT_TELEGRAMTOKEN}")
    @ResponseBody
    public BotApiMethod<?> onUpdateReceived(@RequestBody Update update) {
        return botMessageHandler.onWebhookUpdateReceived(update);
    }

}
