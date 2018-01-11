package ru.proshik.applepriceparcer.console.command;

import org.apache.commons.lang3.tuple.ImmutablePair;
import ru.proshik.applepriceparcer.console.command.model.Option;
import ru.proshik.applepriceparcer.console.storage.FileStorage;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//import static ru.proshik.applepriceparcer.console.command.Change.buildAssortmentChanges;
//import static ru.proshik.applepriceparcer.console.command.Change.buildChangesString;
//import static ru.proshik.applepriceparcer.console.command.utils.CommandUtils.buildAssortmentOut;

public class History extends Command {

//    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
//    private static final int DEFAULT_COUNT = 5;
//
//    private FileStorage fileStorage = new FileStorage();

    public History(String title, String description, List<Option> options) {
        super(title, description, options);
    }

    @Override
    public void execute(Option option) {
//        if (option != null && option.getTitle().equals("-h")) {
//            System.out.println(printInfo());
//            return;
//        }
//
//        List<AjAssortment> ajAssortments = fileStorage.read();
//        if (ajAssortments.isEmpty()) {
//            System.out.println("Not found not one assortment. Use command 'read'");
//            return;
//        }
//
//        List<AjAssortment> lAssort = ajAssortments.stream()
//                .sorted((o1, o2) -> o2.getCreatedDate().compareTo(o1.getCreatedDate()))
//                .limit(DEFAULT_COUNT)
//                .collect(Collectors.toList());
//
//        if (option != null && option.getTitle().equals("--only-change")) {
//            StringBuilder history = new StringBuilder("History changes:\n");
//
//            for (int i = 0; i < lAssort.size(); i++) {
//                if (i < lAssort.size() - 1) {
//                    Map<String, List<ImmutablePair<Item, BigDecimal>>> changes =
//                            buildAssortmentChanges(lAssort.get(i).getAssortments(), lAssort.get(i + 1).getAssortments());
//                    history.append(buildChangesString(lAssort.get(i).getCreatedDate(), lAssort.get(i + 1).getCreatedDate(), changes)).append("\n");
//                }
//            }
//
//            System.out.println(history);
//        } else {
//            printSimpleHistory(lAssort);
//        }
    }

//    private void printSimpleHistory(List<AjAssortment> limitedAssortment) {
//        StringBuilder history = new StringBuilder("History:\n");
//
//        for (AjAssortment ajA : limitedAssortment) {
//            history.append("Date: ").append(DATE_TIME_FORMATTER.format(ajA.getCreatedDate())).append("\n");
//            history.append("-----------------").append("\n");
//            history.append(buildAssortmentOut(ajA.getAssortments()));
//            history.append("*****************").append("\n\n");
//        }
//
//        System.out.println(history);
//    }
}
