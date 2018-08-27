package ru.proshik.applepricebot.service.utils;

import ru.proshik.applepricebot.model.SubscriptionResp;
import ru.proshik.applepricebot.model.UserResp;
import ru.proshik.applepricebot.repository.model.Subscription;
import ru.proshik.applepricebot.repository.model.User;

import java.util.List;
import java.util.stream.Collectors;

public class Transformer {

    public static UserResp transform(User user) {
        List<Long> subscriptionIds = user.getSubscriptions().stream()
                .map(Subscription::getId)
                .collect(Collectors.toList());

        return UserResp.builder()
                .id(user.getId())
                .createdDate(user.getCreatedDate())
                .chatId(user.getChatId())
                .subscriptionIds(subscriptionIds)
                .build();
    }

    public static SubscriptionResp transform(Subscription subscription) {
        return SubscriptionResp.builder()
                .id(subscription.getId())
                .productType(subscription.getProductType())
                .providerId(subscription.getProvider().getId())
                .build();
    }

    public static List<SubscriptionResp> transform(List<Subscription> subscriptions) {
        return subscriptions.stream()
                .map(Transformer::transform)
                .collect(Collectors.toList());
    }

}
