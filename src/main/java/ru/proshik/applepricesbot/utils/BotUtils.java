package ru.proshik.applepricesbot.utils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BotUtils {

    public static Optional<String> extractArgument(String text) {
        return Stream.of(text.split(" "))
                .skip(1)
                .findFirst();
    }

    public static List<String> extractArguments(String text) {
        return Stream.of(text.split(" "))
                .skip(1)
                .collect(Collectors.toList());
    }

}
