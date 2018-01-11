package ru.proshik.applepriceparcer.console.service;

import ru.proshik.applepriceparcer.console.command.model.CommandLineParameters;

public class ArgsParser {

    public CommandLineParameters parse(String[] args) {
        if (args.length == 0) {
            throw new IllegalArgumentException("Empty args array");
        }

        String commandTitle = args[0];

        if (args.length > 1) {
            return new CommandLineParameters(commandTitle, args[1]);
        } else {
            return new CommandLineParameters(commandTitle, null);
        }
    }

}
