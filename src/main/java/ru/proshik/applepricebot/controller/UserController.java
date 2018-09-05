package ru.proshik.applepricebot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import ru.proshik.applepricebot.model.SubscriptionReq;
import ru.proshik.applepricebot.model.SubscriptionResp;
import ru.proshik.applepricebot.model.UserResp;
import ru.proshik.applepricebot.repository.UserRepository;
import ru.proshik.applepricebot.repository.model.ProductType;
import ru.proshik.applepricebot.repository.model.User;
import ru.proshik.applepricebot.service.UserService;
import ru.proshik.applepricebot.service.utils.Transformer;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "api/v1/user")
public class UserController {

    private final UserRepository userRepository;

    private final UserService userService;

    @Autowired
    public UserController(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @GetMapping
    public List<UserResp> list(@PageableDefault Pageable pageable) {
        // TODO: 26/08/2018 change returned object on Page<Custom object with subscription ids>
        List<User> users = userRepository.findUsers(pageable);

        return users.stream()
                .map(Transformer::transform)
                .collect(Collectors.toList());
    }

    @PostMapping(value = "{userId}/subscription")
    public List<SubscriptionResp> addSubscription(@PathVariable(value = "userId") Long userId,
                                                  @RequestBody @Valid SubscriptionReq subscriptionIn) {

        return userService.addSubscription(userId, subscriptionIn);
    }

    @DeleteMapping(value = "{userId}/subscription/{subscriptionId}")
    public List<SubscriptionResp> removeSubscription(@PathVariable(value = "userId") Long userId,
                                                     @PathVariable(value = "subscriptionId") Long subscriptionId) {
        return userService.removeSubscription(userId, subscriptionId);
    }

}
