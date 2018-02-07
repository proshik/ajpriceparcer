package ru.proshik.applepriceparcer.service;

import ru.proshik.applepriceparcer.model2.DiffProducts;
import ru.proshik.applepriceparcer.model2.Fetch;
import ru.proshik.applepriceparcer.model2.HistoryDiff;
import ru.proshik.applepriceparcer.model2.Product;
import ru.proshik.applepriceparcer.service.scheduler.ScreeningJob;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DiffService {

    public List<HistoryDiff> historyDiff(List<Fetch> fetches) {
        List<HistoryDiff> result = new ArrayList<>();

        List<Fetch> limitedFetches = fetches.stream().sorted((o1, o2) -> o2.getCreatedDate().compareTo(o1.getCreatedDate()))
                .limit(5)
                .collect(Collectors.toList());
        for (int i = 0; i < limitedFetches.size() - 1; i++) {
            List<DiffProducts> diff = findDiff(limitedFetches.get(i + 1), limitedFetches.get(i));
            result.add(new HistoryDiff(limitedFetches.get(i).getCreatedDate(), limitedFetches.get(i + 1).getCreatedDate(), diff));
        }

        return result;
    }

    private List<DiffProducts> findDiff(Fetch oldFetch, Fetch newFetch) {
        List<DiffProducts> diff = new ArrayList<>();

        Map<ScreeningJob.ProductKey, List<Product>> groupNewFetch = newFetch.getProducts().stream()
                .collect(Collectors.groupingBy(o -> new ScreeningJob.ProductKey(o.getTitle(), o.getDescription(), o.getProductType())));
        Map<ScreeningJob.ProductKey, List<Product>> groupLastFetch = oldFetch.getProducts().stream()
                .collect(Collectors.groupingBy(o -> new ScreeningJob.ProductKey(o.getTitle(), o.getDescription(), o.getProductType())));

        for (Map.Entry<ScreeningJob.ProductKey, List<Product>> newEntry : groupNewFetch.entrySet()) {
            List<Product> oldProducts = groupLastFetch.get(newEntry.getKey());
            if (oldProducts != null) {
                List<Product> newProducts = newEntry.getValue();
                newProducts.sort(Comparator.comparing(Product::getPrice));
                oldProducts.sort(Comparator.comparing(Product::getPrice));

                if (newProducts.size() == oldProducts.size()) {
                    for (int i = 0; i < newProducts.size(); i++) {
                        if (!oldProducts.get(i).getPrice().equals(newProducts.get(i).getPrice())
                                || (oldProducts.get(i).getAvailable() != null && newProducts.get(i).getAvailable() != null
                                && (!oldProducts.get(i).getAvailable().equals(newProducts.get(i).getAvailable())))) {
                            diff.add(new DiffProducts(oldProducts.get(i), newProducts.get(i)));
                        }
                    }
                } else {
                    List<Product> forAdded = new ArrayList<>();
                    if (newProducts.size() > oldProducts.size()) {
                        newProducts.retainAll(oldProducts);
                        forAdded.addAll(newProducts);
                    } else {
                        oldProducts.retainAll(newProducts);
                        forAdded.addAll(oldProducts);
                    }
                    for (Product p : forAdded) {
                        diff.add(new DiffProducts(null, p));
                    }
                }
            } else {
                for (Product p : newEntry.getValue()) {
                    diff.add(new DiffProducts(null, p));
                }
            }
        }

        return diff;
    }

}
