package ru.proshik.applepricebot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.proshik.applepricebot.exception.SubscriptionNotFoundException;
import ru.proshik.applepricebot.model.SubscriptionResp;
import ru.proshik.applepricebot.repository.SubscriptionRepository;
import ru.proshik.applepricebot.repository.model.Subscription;

import static ru.proshik.applepricebot.utils.Transformer.transform;

@Service
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;

    @Autowired
    public SubscriptionService(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    @Transactional
    public SubscriptionResp provideSubscription(Long subscriptionId) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new SubscriptionNotFoundException("Subscription not found by id=" + subscriptionId));

        return transform(subscription);
    }

}
