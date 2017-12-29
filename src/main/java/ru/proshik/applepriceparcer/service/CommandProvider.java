package ru.proshik.applepriceparcer.service;

import ru.proshik.applepriceparcer.command.Change;
import ru.proshik.applepriceparcer.command.Command;
import ru.proshik.applepriceparcer.command.Read;
import ru.proshik.applepriceparcer.model.Option;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class CommandProvider {

    private static final Command READ = new Read("read", "Read aj.ru page and print result",
            Arrays.asList(new Option("-h", "Show help")));
    private static final Command CHANGE = new Change("change", "Show history by change prices",
            Arrays.asList(new Option("-h", "Show help")));

    private List<Command> commands;

    public CommandProvider() {
        List<Command> commands = new ArrayList<>();

        commands.add(READ);
        commands.add(CHANGE);

        this.commands = commands;
    }

    public List<Command> commandList() {
        return commands;
    }

}
