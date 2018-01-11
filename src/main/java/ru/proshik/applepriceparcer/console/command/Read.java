package ru.proshik.applepriceparcer.console.command;

import ru.proshik.applepriceparcer.console.command.utils.CommandUtils;
import ru.proshik.applepriceparcer.console.command.model.Option;
import ru.proshik.applepriceparcer.provider.screener.aj.AjScreener;
import ru.proshik.applepriceparcer.console.storage.FileStorage;

import java.time.LocalDateTime;
import java.util.List;

//import static ru.proshik.applepriceparcer.console.command.utils.CommandUtils.buildAssortmentOut;

public class Read extends Command {

//    private AjScreener ajScreener = new AjScreener();
//
//    private FileStorage fileStorage = new FileStorage();

    public Read(String title, String description, List<Option> options) {
        super(title, description, options);
    }

    @Override
    public void execute(Option option) {
//        if (option != null && option.getTitle().equals("-h")) {
//            System.out.println(printInfo());
//            return;
//        }
//
//        List<Assortment> newAjAssortments = ajScreener.printAjPrices();
//
//        List<AjAssortment> ajAssortments = fileStorage.read();
//
//        if (!ajAssortments.isEmpty()) {
//            boolean wasChangeInPrices = CommandUtils.wasChangeInPrices(newAjAssortments, ajAssortments);
//            if (wasChangeInPrices) {
//                System.out.println("Prices WAS CHANGED. You may use command change for see changes and update!\n");
//            }
//        } else {
//            fileStorage.save(new AjAssortment(LocalDateTime.now(), newAjAssortments));
//        }
//
//        System.out.println(buildAssortmentOut(newAjAssortments));
    }

}
