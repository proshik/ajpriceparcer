package ru.proshik.applepricebot.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.proshik.applepricebot.repository.model.ProductType;

import java.math.BigDecimal;
import java.util.Map;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductResp {

    private Long id;

    private String title;

    private String description;

    private Boolean available;

    private BigDecimal price;

    private ProductType productType;

    private Map<String, String> parameters;

}
