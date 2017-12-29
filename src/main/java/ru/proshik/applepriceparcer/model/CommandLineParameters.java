package ru.proshik.applepriceparcer.model;

public class CommandLineParameters {

    private String command;
    private String option;

    public CommandLineParameters(String command, String option) {
        this.command = command;
        this.option = option;
    }

    public String getCommand() {
        return command;
    }

    public String getOption() {
        return option;
    }
}
