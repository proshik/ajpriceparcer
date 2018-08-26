package ru.proshik.applepricebot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import ru.proshik.applepricebot.model.SubscriptionReq;
import ru.proshik.applepricebot.model.SubscriptionResp;
import ru.proshik.applepricebot.model.UserResp;
import ru.proshik.applepricebot.repository.UserRepository;
import ru.proshik.applepricebot.repository.model.Subscription;
import ru.proshik.applepricebot.repository.model.User;
import ru.proshik.applepricebot.service.SubscriptionService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "api/v1/user")
public class UserController {

    private final UserRepository userRepository;

    private final SubscriptionService subscriptionService;

    @Autowired
    public UserController(UserRepository userRepository,
                          SubscriptionService subscriptionService) {
        this.userRepository = userRepository;
        this.subscriptionService = subscriptionService;
    }

    @GetMapping
    public List<UserResp> list(@PageableDefault Pageable pageable) {
        // TODO: 26/08/2018 change returned object on Page<Custom object with subscription ids>
        List<User> users = userRepository.findUsers(pageable);

        return users.stream()
                .map(this::transform)
                .collect(Collectors.toList());
    }

    @PostMapping(value = "{userId}/subscription")
    public List<SubscriptionResp> addSubscription(@PathVariable(value = "userId") Long userId,
                                                  @RequestBody @Valid SubscriptionReq subscriptionIn) {

        return subscriptionService.addSubscription(userId, subscriptionIn);
    }

    @DeleteMapping(value = "{userId}/subscription/{subscriptionId}")
    public List<SubscriptionResp> removeSubscription(@PathVariable(value = "userId") Long userId,
                                                     @PathVariable(value = "subscriptionId") Long subscriptionId) {
        return subscriptionService.removeSubscription(userId, subscriptionId);
    }

    private UserResp transform(User user) {
        List<SubscriptionResp> subscriptions = user.getSubscriptions().stream()
                .map(this::transform)
                .collect(Collectors.toList());

        return UserResp.builder()
                .id(user.getId())
                .createdDate(user.getCreatedDate())
                .chatId(user.getChatId())
                .subscriptionIds(subscriptions.stream().map(SubscriptionResp::getId).collect(Collectors.toList()))
                .build();
    }

    private SubscriptionResp transform(Subscription subscription) {
        return SubscriptionResp.builder()
                .id(subscription.getId())
                .providerId(subscription.getProvider().getId())
                .productType(subscription.getProductType())
                .build();
    }

}
