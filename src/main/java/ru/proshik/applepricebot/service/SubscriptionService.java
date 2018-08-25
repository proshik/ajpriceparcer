package ru.proshik.applepricebot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.proshik.applepricebot.exception.ProviderNotFoundException;
import ru.proshik.applepricebot.exception.SubscriptionConflictException;
import ru.proshik.applepricebot.exception.SubscriptionNotFoundException;
import ru.proshik.applepricebot.exception.UserNotFoundException;
import ru.proshik.applepricebot.model.SubscriptionRestOut;
import ru.proshik.applepricebot.repository.ProviderRepository;
import ru.proshik.applepricebot.repository.SubscriptionRepository;
import ru.proshik.applepricebot.repository.UserRepository;
import ru.proshik.applepricebot.repository.model.ProductType;
import ru.proshik.applepricebot.repository.model.Provider;
import ru.proshik.applepricebot.repository.model.Subscription;
import ru.proshik.applepricebot.repository.model.User;

import java.util.List;
import java.util.stream.Collectors;

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
    public SubscriptionRestOut provideSubscription(Long subscriptionId) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new SubscriptionNotFoundException("Subscription not found by id=" + subscriptionId));

        return transform(subscription);
    }

    @Transactional
    public List<SubscriptionRestOut> addSubscription(Long userId, Long providerId, ProductType productType) {

        Subscription subscription = subscriptionRepository.findByProviderIdAndProductType(providerId, productType);
        if (subscription != null) {
            throw new SubscriptionConflictException("Subscription is already exists with providerId=" + providerId
                    + " and productType=" + productType);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found by id=" + userId));


        Provider provider = providerRepository.findById(providerId)
                .orElseThrow(() -> new ProviderNotFoundException("Provider not found by id=" + userId));

        user.addSubscription(Subscription.builder()
                .provider(provider)
                .productType(productType)
                .build());

        return transform(user.getSubscriptions());
    }

    @Transactional
    public List<SubscriptionRestOut> removeSubscription(Long userId, Long subscriptionId) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new SubscriptionNotFoundException("Subscription not found by id=" + subscriptionId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found by id=" + userId));

        user.removeSubscription(subscription);

        return transform(user.getSubscriptions());
    }

    private SubscriptionRestOut transform(Subscription subscription) {
        return SubscriptionRestOut.builder()
                .id(subscription.getId())
                .productType(subscription.getProductType())
                .providerId(subscription.getProvider().getId())
                .build();
    }

    private List<SubscriptionRestOut> transform(List<Subscription> subscriptions) {
        return subscriptions.stream()
                .map(this::transform)
                .collect(Collectors.toList());
    }

}
