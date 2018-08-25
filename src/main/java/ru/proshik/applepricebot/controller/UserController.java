package ru.proshik.applepricebot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.proshik.applepricebot.model.SubscriptionRestOut;
import ru.proshik.applepricebot.model.UserRestOut;
import ru.proshik.applepricebot.repository.UserRepository;
import ru.proshik.applepricebot.repository.model.Subscription;
import ru.proshik.applepricebot.repository.model.User;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "api/v1/user")
public class UserController {

    private final UserRepository userRepository;

    @Autowired
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<UserRestOut> list(@PageableDefault Pageable pageable) {
        List<User> users = userRepository.findUsers(pageable);

        return users.stream()
                .map(this::transform)
                .collect(Collectors.toList());
    }

    private UserRestOut transform(User user) {
        List<SubscriptionRestOut> subscriptions = user.getSubscriptions().stream()
                .map(this::transform)
                .collect(Collectors.toList());

        return UserRestOut.builder()
                .id(user.getId())
                .createdDate(user.getCreatedDate())
                .chatId(user.getChatId())
                .subscriptions(subscriptions)
                .build();
    }

    private SubscriptionRestOut transform(Subscription subscription) {
        return SubscriptionRestOut.builder()
                .id(subscription.getId())
                .providerId(subscription.getProvider().getId())
                .productType(subscription.getProductType())
                .build();
    }

}
