package ru.proshik.applepricebot.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.proshik.applepricebot.repository.model.FetchType;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AssortmentResp {

    private String provider;

    private FetchType fetchType;

    private LocalDateTime fetchDate;

    private List<ProductResp> products;

}

