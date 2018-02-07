package ru.proshik.applepriceparcer.service;

import org.apache.log4j.Logger;
import ru.proshik.applepriceparcer.bot.PrintUtils2;
import ru.proshik.applepriceparcer.exception.DatabaseException;
import ru.proshik.applepriceparcer.model2.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.proshik.applepriceparcer.FetchUtils.findLastFetch;

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
        if (arguments.isEmpty()) {
            String availableShops = availableShops();
            List<ProductType> productTypes = Arrays.asList(ProductType.values());

            return "Use next syntax of command: /read <shop> <product type>\n\n"
                    + "Available shops:\n"
                    + availableShops
                    + "\n\n"
                    + "All product types:\n"
                    + productTypes.stream()
                    .map(productType -> "*" + productType.getValue().replace(" ", "") + "*")
                    .collect(Collectors.joining(", "));
        }

        if (arguments.size() != 2) {
            return "Needed 2 arguments for the entered command, write /read for more information.";
        }

        Shop shop = shopService.findShop(arguments.get(0));
        if (shop == null) {
            return "Shop with this title not was found!\n" + "Available shops:\n" + availableShops();
        }

        List<Fetch> fetches;
        ProductType productType;
        try {
            productType = shopService.findProductType(arguments.get(1));
            if (productType == null) {
                return "For shop *" + shop.getTitle() + "* available follow product types:\n" + availableProductTypes(shop);
            }

            fetches = fetchService.getFetch(shop);
        } catch (DatabaseException e) {
            LOG.error("Error on execution read operation", e);
            return "Error on execution operation.";
        }

        if (fetches == null || fetches.isEmpty()) {
            return "Shop with this title has not any records!";
        }

        Fetch lastFetch = findLastFetch(fetches);

        List<Product> selectedProducts = lastFetch.getProducts().stream()
                .filter(p -> p.getProductType() == productType)
                .collect(Collectors.toList());

        return PrintUtils2.fetchInfo(shop.getTitle(), productType, lastFetch.getCreatedDate(), selectedProducts);
    }

    public String history(String shopTitle) {
        if (shopTitle == null) {
            return "With command must be argument shop.\nAvailable shops:\n" + availableShops();
        }

        Shop shop = shopService.findShop(shopTitle);
        if (shop == null) {
            return "Shop with this title not was found!\n" + "Available shops:\n" + availableShops();
        }

        List<Fetch> fetches;
        try {
            fetches = fetchService.getFetch(shop);
        } catch (DatabaseException e) {
            LOG.error("Error on execution read operation", e);
            return "Error on execution operation.";
        }

        if (fetches.size() < 2) {
            return "Changes does not have for shop: *" + shop.getTitle() + "*";
        }

        List<HistoryDiff> historyDiffs = diffService.historyDiff(fetches);

        return PrintUtils2.historyInfo(shop, historyDiffs);
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

        return PrintUtils2.subscriptionInfo(userSubscriptions, availableShops);
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
}
