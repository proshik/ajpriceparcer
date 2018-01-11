package ru.proshik.applepriceparcer.command;

import ru.proshik.applepriceparcer.ConsoleApp;
import ru.proshik.applepriceparcer.command.model.Option;

import java.util.List;

public abstract class Command {

    private String title;
    private String description;
    private List<Option> options;

    Command(String title, String description, List<Option> options) {
        this.title = title;
        this.description = description;
        this.options = options;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public List<Option> getOptions() {
        return options;
    }

    public String printDescription() {
        return title + " - " + description;
    }

    String printInfo() {
        boolean optionEnable = false;
        if (options != null && !options.isEmpty()) {
            optionEnable = true;
        }

        StringBuilder info = new StringBuilder("Usage: "
                + ConsoleApp.APP_TITLE + " " + title
                + (optionEnable ? " [OPTIONS]\n" : "\n"));

        info.append("\n");

        if (optionEnable) {
            info.append("Options:\n");
            for (Option o : options) {
                info.append(o.getTitle()).append(" ").append(o.getDescription()).append("\n");
            }
        }
        info.append("\n");

        return info.toString();
    }

    public abstract void execute(Option options);

}
