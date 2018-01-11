package ru.proshik.applepriceparcer.console.service;

import ru.proshik.applepriceparcer.console.command.Command;
import ru.proshik.applepriceparcer.console.exception.NotFoundCommandException;
import ru.proshik.applepriceparcer.console.command.provider.CommandProvider;

import java.util.List;

public class CommandExtractor {

    private CommandProvider commandProvider;

    public CommandExtractor(CommandProvider commandProvider) {
        this.commandProvider = commandProvider;
    }

    public Command extract(String commandTitle) throws NotFoundCommandException {
        if (commandTitle == null) {
            throw new IllegalArgumentException("Command title not must be empty");
        }

        List<Command> commands = commandProvider.commandList();

        for (Command c : commands) {
            if (c.getTitle().equals(commandTitle)) {
                return c;
            }
        }


        throw new NotFoundCommandException("Command with title " + commandTitle + " not found\n");
    }

}


