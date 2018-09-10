package ru.proshik.applepricebot.utils;

import ru.proshik.applepricebot.model.SubscriptionResp;
import ru.proshik.applepricebot.model.UserResp;
import ru.proshik.applepricebot.repository.model.Subscription;
import ru.proshik.applepricebot.repository.model.User;

import java.util.List;
import java.util.stream.Collectors;

public class Transformer {

    public static UserResp transform(User user) {
        // TODO: 05.09.2018 remove if everething is ok
//        List<Long> subscriptionIds = user.getSubscriptions().stream()
//                .map(Subscription::getId)
//                .collect(Collectors.toList());

        List<SubscriptionResp> subscriptions = user.getSubscriptions().stream()
                .map(Transformer::transform)
                .collect(Collectors.toList());

        return UserResp.builder()
                .id(user.getId())
                .createdDate(user.getCreatedDate())
                .chatId(user.getChatId())
                .subscriptions(subscriptions)
                .build();
    }

    public static SubscriptionResp transform(Subscription subscription) {
        return SubscriptionResp.builder()
                .id(subscription.getId())
                .productType(subscription.getProductType())
                .providerId(subscription.getProvider().getId())
                .providerType(subscription.getProvider().getType())
                .build();
    }

    public static List<SubscriptionResp> transform(List<Subscription> subscriptions) {
        return subscriptions.stream()
                .map(Transformer::transform)
                .collect(Collectors.toList());
    }

}
