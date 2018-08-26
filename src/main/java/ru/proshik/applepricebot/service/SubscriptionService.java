package ru.proshik.applepricebot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.proshik.applepricebot.exception.ProviderNotFoundException;
import ru.proshik.applepricebot.exception.SubscriptionNotFoundException;
import ru.proshik.applepricebot.exception.UserNotFoundException;
import ru.proshik.applepricebot.model.SubscriptionReq;
import ru.proshik.applepricebot.model.SubscriptionResp;
import ru.proshik.applepricebot.repository.ProviderRepository;
import ru.proshik.applepricebot.repository.SubscriptionRepository;
import ru.proshik.applepricebot.repository.UserRepository;
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
    public SubscriptionResp provideSubscription(Long subscriptionId) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new SubscriptionNotFoundException("Subscription not found by id=" + subscriptionId));

        return transform(subscription);
    }

    @Transactional
    public List<SubscriptionResp> addSubscription(Long userId, SubscriptionReq subscriptionIn) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found by id=" + userId));

        boolean subscriptionDoesNotExist = user.getSubscriptions().stream()
                .noneMatch(s -> s.getProductType() == subscriptionIn.getProductType()
                        && s.getProvider().getId().equals(subscriptionIn.getProviderId()));

        if (subscriptionDoesNotExist) {
            Provider provider = providerRepository.findById(subscriptionIn.getProviderId())
                    .orElseThrow(() -> new ProviderNotFoundException("Provider not found by id=" + userId));

            Subscription addedSubscription = Subscription.builder()
                    .provider(provider)
                    .productType(subscriptionIn.getProductType())
                    .build();

            user.addSubscription(addedSubscription);

            userRepository.saveAndFlush(user);
        }

        return transform(user.getSubscriptions());
    }

    @Transactional
    public List<SubscriptionResp> removeSubscription(Long userId, Long subscriptionId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found by id=" + userId));

        user.getSubscriptions().stream()
                .filter(s -> s.getId().equals(subscriptionId))
                .findFirst()
                .ifPresent(user::removeSubscription);

        return transform(user.getSubscriptions());
    }

    private SubscriptionResp transform(Subscription subscription) {
        return SubscriptionResp.builder()
                .id(subscription.getId())
                .productType(subscription.getProductType())
                .providerId(subscription.getProvider().getId())
                .build();
    }

    private List<SubscriptionResp> transform(List<Subscription> subscriptions) {
        return subscriptions.stream()
                .map(this::transform)
                .collect(Collectors.toList());
    }

}
