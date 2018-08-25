package ru.proshik.applepricebot.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.proshik.applepricebot.repository.model.ProductType;

import java.math.BigDecimal;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductOut {

    private String title;

    private String description;

    private Boolean available;

    private BigDecimal price;

    private ProductType productType;

}
