package ru.proshik.applepriceparcer.console;

import org.apache.log4j.Logger;
import ru.proshik.applepriceparcer.console.command.Command;
import ru.proshik.applepriceparcer.console.command.model.CommandLineParameters;
import ru.proshik.applepriceparcer.console.command.model.Option;
import ru.proshik.applepriceparcer.console.command.provider.CommandProvider;
import ru.proshik.applepriceparcer.console.exception.NotFoundParameterException;
import ru.proshik.applepriceparcer.console.service.ArgsParser;
import ru.proshik.applepriceparcer.console.service.CommandExtractor;
import ru.proshik.applepriceparcer.console.service.OptionExtractor;

public class ConsoleApp {

    private static final Logger LOG = Logger.getLogger(ConsoleApp.class);

    public static final String APP_TITLE = "app";

    private String[] args;

    private ArgsParser argsParser = new ArgsParser();
    private CommandProvider commandProvider = new CommandProvider();
    private CommandExtractor commandExtractor = new CommandExtractor(commandProvider);
    private OptionExtractor optionExtractor = new OptionExtractor();

    ConsoleApp(String[] args) {
        this.args = args;
    }

    public void Run() {
        LOG.info("Success run application!");

        if (args.length == 0) {
            System.out.println("\nUsage: " + APP_TITLE + " COMMAND\n");

            System.out.println("Commands:\n");
            for (Command c : commandProvider.commandList()) {
                System.out.println(c.printDescription());
            }
            return;
        }

        CommandLineParameters cmdParams = argsParser.parse(args);

        try {
            Command command = commandExtractor.extract(cmdParams.getCommand());

            Option option = optionExtractor.extract(command, cmdParams.getOption());

            command.execute(option);

        } catch (NotFoundParameterException e) {
            LOG.warn("Incorrect parameters", e);
            System.out.println(e.getMessage());
        }

        LOG.info("Close run application!");
    }

}
