package ru.proshik.applepriceparcer.command;

import ru.proshik.applepriceparcer.model.Option;
import ru.proshik.applepriceparcer.model.goods.AjAssortment;
import ru.proshik.applepriceparcer.storage.FileStorage;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import static ru.proshik.applepriceparcer.command.utils.CommandUtils.buildAssortmentOut;

public class History extends Command {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
    private static final int DEFAULT_COUNT = 5;

    private FileStorage fileStorage = new FileStorage();

    public History(String title, String description, List<Option> options) {
        super(title, description, options);
    }

    @Override
    public void execute(Option option) {
        if (option != null && option.getTitle().equals("-h")) {
            System.out.println(printInfo());
            return;
        }

        List<AjAssortment> ajAssortments = fileStorage.read();

        if (ajAssortments.isEmpty()) {
            System.out.println("Not found not one assortment. Use command 'read'");
            return;
        }

        List<AjAssortment> limitedAssortment = ajAssortments.stream()
                .sorted((o1, o2) -> o2.getCreatedDate().compareTo(o1.getCreatedDate()))
                .limit(DEFAULT_COUNT)
                .collect(Collectors.toList());

        printHistory(limitedAssortment);
    }

    private void printHistory(List<AjAssortment> limitedAssortment) {
        StringBuilder history = new StringBuilder("History:\n");

        for (AjAssortment ajA : limitedAssortment) {
            history.append(DATE_TIME_FORMATTER.format(ajA.getCreatedDate())).append("\n");
            history.append("-----------------").append("\n");
            history.append(buildAssortmentOut(ajA.getAssortments()));
            history.append("*****************").append("\n\n");
        }

        System.out.println(history);
    }
}
