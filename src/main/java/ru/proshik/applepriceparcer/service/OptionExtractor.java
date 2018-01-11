package ru.proshik.applepriceparcer.service;

import org.apache.log4j.Logger;
import ru.proshik.applepriceparcer.command.Command;
import ru.proshik.applepriceparcer.exception.NotFoundOptionException;
import ru.proshik.applepriceparcer.command.model.Option;

public class OptionExtractor {

    private static final Logger LOG = Logger.getLogger(OptionExtractor.class);

    public Option extract(Command command, String optionTitle) throws NotFoundOptionException {

        if (optionTitle == null) {
            return null;
        }

        for (Option o : command.getOptions()) {
            if (o.getTitle().equals(optionTitle)) {
                return o;
            }
        }

        LOG.debug("Option not found!");
        throw new NotFoundOptionException("Option with title = " + optionTitle + " not found");
    }

}
