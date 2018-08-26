package ru.proshik.applepricebot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.proshik.applepricebot.model.SubscriptionResp;
import ru.proshik.applepricebot.service.SubscriptionService;

@RestController
@RequestMapping(value = "api/v1/subscription")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @Autowired
    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @GetMapping(value = "{subscriptionId}")
    public SubscriptionResp provideSubscription(@PathVariable(value = "subscriptionId") Long subscriptionId) {
        return subscriptionService.provideSubscription(subscriptionId);
    }

}
