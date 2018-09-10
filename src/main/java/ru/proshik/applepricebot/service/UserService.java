package ru.proshik.applepricebot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.proshik.applepricebot.exception.ProviderNotFoundException;
import ru.proshik.applepricebot.exception.UserNotFoundException;
import ru.proshik.applepricebot.model.SubscriptionReq;
import ru.proshik.applepricebot.model.SubscriptionResp;
import ru.proshik.applepricebot.repository.ProviderRepository;
import ru.proshik.applepricebot.repository.UserRepository;
import ru.proshik.applepricebot.repository.model.Provider;
import ru.proshik.applepricebot.repository.model.Subscription;
import ru.proshik.applepricebot.repository.model.User;

import java.util.List;

import static ru.proshik.applepricebot.utils.Transformer.transform;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final ProviderRepository providerRepository;

    @Autowired
    public UserService(UserRepository userRepository, ProviderRepository providerRepository) {
        this.userRepository = userRepository;
        this.providerRepository = providerRepository;
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

}
