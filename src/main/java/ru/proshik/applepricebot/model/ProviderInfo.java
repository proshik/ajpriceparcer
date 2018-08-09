package ru.proshik.applepricebot.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProviderInfo {

    private String title;

    private String url;

    private boolean enabled;

}
