package ru.proshik.applepricebot.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProviderUtils {

    public static Map<String, String> extractParameters(String value) {
        Map<String, String> result = new HashMap<>();

        List<String> values = Arrays.asList(value.split(" "));
        for (String v : values) {

            if (v.toUpperCase().contains("GB")) {
                result.put("GB", v);
            }
        }
        return result;
    }

}
