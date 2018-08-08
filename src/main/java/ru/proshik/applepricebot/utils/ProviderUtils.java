package ru.proshik.applepricebot.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProviderUtils {

    private static final Logger LOG = Logger.getLogger(ProviderUtils.class);

    private static ObjectMapper objectMapper = new ObjectMapper();

    public static String paramsToString(Map<String, String> params) {
        try {
            return objectMapper.writeValueAsString(params);
        } catch (JsonProcessingException e) {
            LOG.error("Convert params to map" + params);
            return null;
        }
    }

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

    public static Map<String, String> extractGBSolid(String text) {
        Map<String, String> result = new HashMap<>();
        Pattern p = Pattern.compile("(\\d+)(Gb|GB|gb)");
        Matcher matcher = p.matcher(text);
        if (matcher.find()) {
            String value = matcher.group(1);
            result.put("GB", String.format("%sGB", value));
        }
        return result;
    }

    public static ArrayList<String> groupExtractor(String text, String regexp) {
        ArrayList<String> result = new ArrayList<String>();
        Pattern p = Pattern.compile(regexp);
        Matcher matcher = p.matcher(text);
        if (matcher.find()) {
            for (int i = matcher.groupCount(); i > 0; i--) {
                result.add(matcher.group(i).trim());
            }
        }
        return result;
    }

    private static Map<String, String> extractGBSpaces(String value, String regexp) {
        Map<String, String> result = new HashMap<>();
        List<String> values = Arrays.asList(value.split(regexp));
        for (String v : values) {

            if (v.toUpperCase().contains("GB")) {
                result.put("GB", v);
            }
        }
        return result;
    }


}
