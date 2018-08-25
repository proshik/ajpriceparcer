package ru.proshik.applepricebot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.proshik.applepricebot.model.SubscriptionRestOut;
import ru.proshik.applepricebot.repository.model.ProductType;
import ru.proshik.applepricebot.service.SubscriptionService;

import java.util.List;

@RestController
@RequestMapping(value = "api/v1/subscription")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @Autowired
    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @GetMapping(value = "{subscriptionId}")
    public SubscriptionRestOut providerSubscription(@PathVariable(value = "subscriptionId") Long subscriptionId) {
        return subscriptionService.provideSubscription(subscriptionId);
    }

    @PostMapping
    public List<SubscriptionRestOut> addSubscription(@RequestParam(value = "userId") Long userId,
                                                     @RequestParam(value = "providerId") Long providerId,
                                                     @RequestParam(value = "productType") ProductType productType) {

        return subscriptionService.addSubscription(userId, providerId, productType);
    }

    @DeleteMapping(value = "{subscriptionId}")
    public List<SubscriptionRestOut> removeSubscription(@PathVariable(value = "subscriptionId") Long subscriptionId,
                                                        @RequestParam(value = "userId") Long userId) {
        return subscriptionService.removeSubscription(userId, subscriptionId);
    }
}
