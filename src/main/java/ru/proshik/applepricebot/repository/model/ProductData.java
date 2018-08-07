package ru.proshik.applepricebot.repository.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductData {

    public static final String P_RAM_MEMORY = "RAM_MEMORY";

    private String title;

    private BigDecimal price;

    private Boolean available;

    private Map<String, String> parameters;

    private String description;

}
