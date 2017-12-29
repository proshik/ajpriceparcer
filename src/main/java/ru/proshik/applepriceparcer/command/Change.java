package ru.proshik.applepriceparcer.command;

import org.apache.commons.lang3.tuple.ImmutablePair;
import ru.proshik.applepriceparcer.command.utils.CommandUtils;
import ru.proshik.applepriceparcer.model.Option;
import ru.proshik.applepriceparcer.model.goods.AjAssortment;
import ru.proshik.applepriceparcer.model.goods.Assortment;
import ru.proshik.applepriceparcer.model.goods.Item;
import ru.proshik.applepriceparcer.service.aj.AjReader;
import ru.proshik.applepriceparcer.storage.FileStorage;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Change extends Command {

    private AjReader ajReader = new AjReader();
    private FileStorage fileStorage = new FileStorage();

    public Change(String title, String description, List<Option> options) {
        super(title, description, options);
    }

    @Override
    public void execute(Option option) {
        if (option != null && option.getTitle().equals("-h")) {
            System.out.println(printInfo());
            return;
        }

        List<Assortment> newAjAssortments = ajReader.printAjPrices();

        List<AjAssortment> ajAssortments = fileStorage.read();

        if (!ajAssortments.isEmpty()) {
            Map<String, List<ImmutablePair<Item, BigDecimal>>> assortmentChanges =
                    getAssortmentChanges(newAjAssortments, CommandUtils.getLastAssortment(ajAssortments).getAssortments());
            if (!assortmentChanges.isEmpty()) {
                fileStorage.save(new AjAssortment(LocalDateTime.now(), newAjAssortments));

                System.out.println(buildChangesString(assortmentChanges));
            }
        } else {
            fileStorage.save(new AjAssortment(LocalDateTime.now(), newAjAssortments));
        }

        System.out.println("Change NOT was found");
    }

    private Map<String, List<ImmutablePair<Item, BigDecimal>>> getAssortmentChanges(List<Assortment> newAjAssortments,
                                                                                    List<Assortment> lastAssortments) {
        Map<ImmutablePair<String, String>, Item> savedAssortments = lastAssortments.stream()
                .flatMap(assortment -> assortment.getItems().stream().map(item -> new ImmutablePair<>(assortment, item)))
                .collect(Collectors.toMap(o -> new ImmutablePair<>(o.left.getTitle(), o.right.getTitle()), ImmutablePair::getRight));

        Map<String, List<ImmutablePair<Item, BigDecimal>>> foundChanges = new HashMap<>();

        for (Assortment a : newAjAssortments) {
            for (Item i : a.getItems()) {
                Item item = savedAssortments.get(new ImmutablePair<>(a.getTitle(), i.getTitle()));
                if (item == null) {
                    foundChanges.computeIfAbsent(a.getTitle(), k -> new ArrayList<>())
                            .add(new ImmutablePair<>(i, i.getPrice()));
                    continue;
                }

                if (!item.getPrice().equals(i.getPrice())) {
                    foundChanges.computeIfAbsent(a.getTitle(), k -> new ArrayList<>())
                            .add(new ImmutablePair<>(i, savedAssortments.get(new ImmutablePair<>(a.getTitle(), i.getTitle())).getPrice()));
                }
            }
        }

        return foundChanges;
    }

    private String buildChangesString(Map<String, List<ImmutablePair<Item, BigDecimal>>> assortmentChanges) {
        StringBuilder change = new StringBuilder("Change WAS FOUND!\n");

        for (Map.Entry<String, List<ImmutablePair<Item, BigDecimal>>> entry : assortmentChanges.entrySet()) {
            change.append(entry.getKey()).append("\n");
            for (ImmutablePair<Item, BigDecimal> i : entry.getValue()) {
                change.append(i.getLeft().getTitle()).append(": ")
                        .append(" old - ").append(i.getRight()).append("; new - ").append(i.getLeft().getPrice()).append("\n");
            }
            change.append("\n");
        }

        return change.toString();
    }

}