package ru.proshik.applepricebot.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.proshik.applepricebot.repository.model.ProductType;

import javax.validation.constraints.NotNull;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionReq {

    @NotNull
    private Long providerId;

    @NotNull
    private ProductType productType;

}
