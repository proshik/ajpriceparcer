package ru.proshik.applepriceparcer.service;

import ru.proshik.applepriceparcer.command.Change;
import ru.proshik.applepriceparcer.command.Command;
import ru.proshik.applepriceparcer.command.History;
import ru.proshik.applepriceparcer.command.Read;
import ru.proshik.applepriceparcer.model.Option;

import java.util.*;

public class CommandProvider {

    private static final Command READ = new Read("read", "Read aj.ru page and print result",
            Collections.singletonList(new Option("-h", "Show help")));
    private static final Command CHANGE = new Change("change", "Show history by change prices",
            Collections.singletonList(new Option("-h", "Show help")));
    private static final Command HISTORY = new History("history", "Show history by the last N request. Default 5",
            Arrays.asList(new Option("-h", "Show help"), new Option("--only-change", "Show history only changes")));

    private List<Command> commands;

    public CommandProvider() {
        List<Command> commands = new ArrayList<>();

        commands.add(READ);
        commands.add(CHANGE);
        commands.add(HISTORY);

        this.commands = commands;
    }

    public List<Command> commandList() {
        return commands;
    }

}
