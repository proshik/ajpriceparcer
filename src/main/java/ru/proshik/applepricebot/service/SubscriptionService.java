package ru.proshik.applepricebot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.proshik.applepricebot.exception.SubscriptionNotFoundException;
import ru.proshik.applepricebot.model.SubscriptionResp;
import ru.proshik.applepricebot.repository.ProviderRepository;
import ru.proshik.applepricebot.repository.SubscriptionRepository;
import ru.proshik.applepricebot.repository.UserRepository;
import ru.proshik.applepricebot.repository.model.Subscription;

import static ru.proshik.applepricebot.service.utils.Transformer.transform;

@Service
public class SubscriptionService {

    private final UserRepository userRepository;

    private final ProviderRepository providerRepository;

    private final SubscriptionRepository subscriptionRepository;

    @Autowired
    public SubscriptionService(UserRepository userRepository,
                               ProviderRepository providerRepository,
                               SubscriptionRepository subscriptionRepository) {
        this.userRepository = userRepository;
        this.providerRepository = providerRepository;
        this.subscriptionRepository = subscriptionRepository;
    }

    @Transactional
    public SubscriptionResp provideSubscription(Long subscriptionId) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new SubscriptionNotFoundException("Subscription not found by id=" + subscriptionId));

        return transform(subscription);
    }

}
