package ru.proshik.applepricebot.service;

import org.apache.log4j.Logger;
import ru.proshik.applepricebot.dto.HistoryDiff;
import ru.proshik.applepricebot.exception.DatabaseException;
import ru.proshik.applepricebot.storage.model.Fetch;
import ru.proshik.applepricebot.storage.model.Product;
import ru.proshik.applepricebot.storage.model.ProductType;
import ru.proshik.applepricebot.storage.model.Shop;
import ru.proshik.applepricebot.utils.PrintUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.proshik.applepricebot.utils.FetchUtils.findLastFetch;

public class CommandService {

    private static final Logger LOG = Logger.getLogger(CommandService.class);

    private ShopService shopService;
    private FetchService fetchService;
    private SubscriberService subscriberService;
    private DiffService diffService = new DiffService();

    public CommandService(ShopService shopService,
                          FetchService fetchService,
                          SubscriberService subscriberService) {
        this.shopService = shopService;
        this.fetchService = fetchService;
        this.subscriberService = subscriberService;
    }

    public String read(List<String> arguments) {
        ShopProductTypeAgrCommand params = parseArguments(arguments);
        if (params.message != null) {
            return params.message;
        }

        List<Fetch> fetches;
        try {
            fetches = fetchService.getFetch(params.shop);
        } catch (DatabaseException e) {
            LOG.error("Error on execution read operation", e);
            return "Error on execution operation";
        }

        if (fetches == null || fetches.isEmpty()) {
            return "Shop with this title has not any records!";
        }

        Fetch lastFetch = findLastFetch(fetches);

        List<Product> selectedProducts = lastFetch.getProducts().stream()
                .filter(p -> p.getProductType() == params.productType)
                .collect(Collectors.toList());

        return PrintUtils.fetchInfo(params.shop.getTitle(), params.productType, lastFetch.getCreatedDate(),
                selectedProducts);
    }

    public String history(List<String> arguments) {
        ShopProductTypeAgrCommand params = parseArguments(arguments);
        if (params.message != null) {
            return params.message;
        }

        List<Fetch> fetches;
        try {
            fetches = fetchService.getFetch(params.shop);
        } catch (DatabaseException e) {
            LOG.error("Error on execution read operation", e);
            return "Error on execution operation.";
        }

        if (fetches.size() < 2) {
            return "Changes does not have for shop: *" + params.shop.getTitle() + "*";
        }

        List<HistoryDiff> historyDiffs = diffService.historyDiff(fetches, params.productType);

        return PrintUtils.historyInfo(params.shop, historyDiffs);
    }

    private ShopProductTypeAgrCommand parseArguments(List<String> arguments) {
        if (arguments.isEmpty()) {
            String availableShops = availableShops();
            List<ProductType> productTypes = Arrays.asList(ProductType.values());

            return new ShopProductTypeAgrCommand("Use next syntax of arguments command /command :shop :product-type\n\n"
                    + "Available shops:\n"
                    + availableShops
                    + "\n\n"
                    + "All product types:\n"
                    + productTypes.stream()
                    .map(productType -> "*" + productType.getValue().replace(" ", "") + "*")
                    .collect(Collectors.joining(", ")));
        }

        if (arguments.size() != 2) {
            return new ShopProductTypeAgrCommand("Needed 2 arguments for the entered command, " +
                    "write command without arguments for take more information.");
        }

        Shop shop = shopService.findShop(arguments.get(0));
        if (shop == null) {
            return new ShopProductTypeAgrCommand("Shop with this title not was found!\n"
                    + "Available shops:\n" + availableShops());
        }

        ProductType productType;
        try {
            productType = shopService.findProductType(arguments.get(1));
            if (productType == null) {
                return new ShopProductTypeAgrCommand("For shop *" + shop.getTitle()
                        + "* available follow product types:\n" + availableProductTypes(shop));
            }
        } catch (DatabaseException e) {
            LOG.error("Error on execution read operation", e);
            return new ShopProductTypeAgrCommand("Error on execution operation.");
        }

        return new ShopProductTypeAgrCommand(shop, productType);
    }

    public String subscriptions(String userId) {
        Set<Shop> availableShops = shopService.getShops();

        Set<Shop> userSubscriptions;
        try {
            userSubscriptions = subscriberService.userSubscriptions(userId);
        } catch (DatabaseException e) {
            LOG.error("Error on execute subscription operation", e);
            return "Error on execution operation.";
        }

        availableShops.removeAll(new ArrayList<>(userSubscriptions));

        return PrintUtils.subscriptionInfo(userSubscriptions, availableShops);
    }

    public String subscribe(String userId, String shopTitle) {
        Shop shop = shopService.findShop(shopTitle);
        if (shop == null) {
            return "Shop with this title not was found!\n" + "Available shops:\n" + availableShops();
        }

        try {
            Set<Shop> shops = subscriberService.userSubscriptions(userId);
            if (shops.contains(shop)) {
                return "You've already subscribed on notification from *" + shop.getTitle() + "*";
            }
            subscriberService.addSubscription(userId, shop);
        } catch (DatabaseException e) {
            LOG.error("Error on execute subscribe operation", e);
            return "Error on execution operation.";
        }

        return "You have been subscribed on update from *" + shop.getTitle() + "*";
    }

    public String unsubscribe(String userId, String shopTitle) {
        Shop shop = shopService.findShop(shopTitle);
        if (shop == null) {
            return "Shop with this title not was found!\n" + "Available shops:\n" + availableShops();
        }

        try {
            Set<Shop> shops = subscriberService.userSubscriptions(userId);
            if (!shops.contains(shop)) {
                return "You've not yet subscribed on notification from *" + shop.getTitle() + "*";
            }

            boolean removeSubscription = subscriberService.removeSubscription(userId, shop);
            if (!removeSubscription) {
                return "Error! You haven`t unsubscribed on notification from *" + shop.getTitle() + "*";
            }
        } catch (DatabaseException e) {
            LOG.error("Error on execute unsubscribe operation", e);
            return "Error on execution operation.";
        }
        return "You have been unsubscribed on updates from *" + shop.getTitle() + "*";
    }

    private String availableShops() {
        Set<Shop> shops = shopService.getShops();

        return shops.stream()
                .map(shop -> "*" + shop.getTitle() + "* - " + shop.getUrl().replace("http://", ""))
                .collect(Collectors.joining("\n"));
    }

    private String availableProductTypes(Shop shop) throws DatabaseException {
        List<Fetch> fetches = fetchService.getFetch(shop);

        Fetch lastFetch = findLastFetch(fetches);

        return lastFetch.getProducts().stream()
                .map(Product::getProductType)
                .collect(Collectors.toSet()).stream()
                .map(p -> "*" + p.getValue().replace(" ", "") + "*")
                .collect(Collectors.joining(", "));
    }

    private class ShopProductTypeAgrCommand {
        String message;
        Shop shop;
        ProductType productType;

        ShopProductTypeAgrCommand(String message) {
            this.message = message;
        }

        ShopProductTypeAgrCommand(Shop shop, ProductType productType) {
            this.shop = shop;
            this.productType = productType;
        }

    }
}
