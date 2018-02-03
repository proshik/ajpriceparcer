package ru.proshik.applepriceparcer.provider2;

import ru.proshik.applepriceparcer.model2.Assortment;
import ru.proshik.applepriceparcer.model2.Product;
import ru.proshik.applepriceparcer.model2.ProductType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ProviderUtils {

    public static Map<String, String> extractParameters(String value) {
        Map<String, String> result = new HashMap<>();

        List<String> values = Arrays.asList(value.split(" "));
        for (String v : values) {
            if (v.toUpperCase().contains("GB") ) {
                result.put("GB", v);
            }
        }
        return result;
    }

    public static void printAssortments(List<Assortment> assortments) {
        for (Assortment a : assortments) {
            System.out.println("AssortmentType: " + a.getAssortmentType() + ", title: " + a.getTitle() + ", description: " + a.getDescription());

            Map<ProductType, List<Product>> group = a.getProducts().stream()
                    .collect(Collectors.groupingBy(Product::getProductType));

            for (Map.Entry<ProductType, List<Product>> entry : group.entrySet()) {
                for (Product p : entry.getValue()) {
                    System.out.println("productType: " + p.getProductType() + ", presence: " + p.getAvailable()
                            + ", title:" + p.getTitle() + ", description:" + p.getDescription() + ", price:"
                            + p.getPrice() + ", params:" + p.getParameters());
                }
                System.out.println();
            }
            System.out.println("\n");
        }
    }
}
