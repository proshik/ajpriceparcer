package ru.proshik.applepriceparcer.service.scheduler;

import org.apache.log4j.Logger;
import org.quartz.*;
import ru.proshik.applepriceparcer.exception.DatabaseException;
import ru.proshik.applepriceparcer.exception.ProviderParseException;
import ru.proshik.applepriceparcer.exception.ServiceLayerException;
import ru.proshik.applepriceparcer.model2.*;
import ru.proshik.applepriceparcer.provider2.Provider;
import ru.proshik.applepriceparcer.provider2.ProviderFactory;
import ru.proshik.applepriceparcer.service.FetchService;
import ru.proshik.applepriceparcer.service.NotificationQueueService;
import ru.proshik.applepriceparcer.service.SubscriberService;

import java.util.*;
import java.util.stream.Collectors;

import static ru.proshik.applepriceparcer.FetchUtils.findLastFetch;

public class ScreeningJob implements Job {

    private static final Logger LOG = Logger.getLogger(ScreeningJob.class);

    public static final String PROVIDER_LABEL = "ProviderFactory";
    public static final String FETCH_SERVICE_LABEL = "FetchService";
    public static final String SUBSCRIBER_SERVICE_LABEL = "SubscriberService";
    public static final String NOTIFICATION_SERVICE_LABEL = "NotificationQueueService";

    private ProviderFactory providerFactory;
    private FetchService fetchService;
    private SubscriberService subscriberService;
    private NotificationQueueService notificationQueueService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            init(context);
        } catch (SchedulerException e) {
            LOG.error("Error initialize context for ScreeningJob", e);
            throw new JobExecutionException(e);
        }

        Map<Shop, List<String>> shopListMap;
        try {
            shopListMap = subscriberService.subscriptionsSubscribers();
        } catch (DatabaseException e) {
            LOG.error("Error on read subscription subscribers from db", e);
            throw new RuntimeException("Error on read subscribers", e);
        }

        Map<Shop, Provider> providers = providerFactory.providers();
        for (Map.Entry<Shop, Provider> p : providers.entrySet()) {
            try {
                Fetch fetch = p.getValue().screening();

                List<DiffProducts> diffProducts = tryUpdateFetches(p.getKey(), fetch);
                if (!diffProducts.isEmpty()) {
                    List<String> users = shopListMap.get(p.getKey());
                    if (users != null) {
                        for (String userId : users) {
                            notificationQueueService.add(p.getKey(), userId, diffProducts);
                        }
                    }
                }
            } catch (ProviderParseException e) {
                LOG.error("Error on screening shop with title=" + p.getKey().getTitle(), e);
            } catch (Exception e) {
                LOG.error("Unexpected error in scheduler service on execute operation " +
                        "for request assortment for shop with title=" + p.getKey().getTitle(), e);
            }
        }
    }

    private void init(JobExecutionContext context) throws SchedulerException {
        SchedulerContext schedulerContext = context.getScheduler().getContext();

        fetchService = (FetchService) schedulerContext.get(FETCH_SERVICE_LABEL);
        providerFactory = (ProviderFactory) schedulerContext.get(PROVIDER_LABEL);
        subscriberService = (SubscriberService) schedulerContext.get(SUBSCRIBER_SERVICE_LABEL);
        notificationQueueService = (NotificationQueueService) schedulerContext.get(NOTIFICATION_SERVICE_LABEL);
    }

    private List<DiffProducts> tryUpdateFetches(Shop shop, Fetch fetch) throws ServiceLayerException {
        List<Fetch> existsFetches = fetchService.getFetch(shop);

        if (existsFetches != null && !existsFetches.isEmpty()) {
            List<DiffProducts> changes = findChanges(fetch, existsFetches);
            if (!changes.isEmpty()) {
                fetchService.addFetch(shop, fetch);
                LOG.info("Success updated fetch for shop with title=" + shop.getTitle());
                return changes;
            } else {
                LOG.info("Not found changes in fetch for shop with title=" + shop.getTitle());
            }
        } else {
            fetchService.addFetch(shop, fetch);
            LOG.info("Success added at first time fetch for shop with title=" + shop.getTitle());
            return Collections.emptyList();
        }

        return Collections.emptyList();
    }

    private List<DiffProducts> findChanges(Fetch newFetch, List<Fetch> existsFetches) {
        List<DiffProducts> diff = new ArrayList<>();

        Fetch lastFetch = findLastFetch(existsFetches);

        List<Product> newFetchProducts = newFetch.getProducts();
        List<Product> lastFetchProducts = lastFetch.getProducts();

        Map<ProductKey, List<Product>> groupNewFetch = newFetchProducts.stream()
                .collect(Collectors.groupingBy(o -> new ProductKey(o.getTitle(), o.getDescription(), o.getProductType())));
        Map<ProductKey, List<Product>> groupLastFetch = lastFetchProducts.stream()
                .collect(Collectors.groupingBy(o -> new ProductKey(o.getTitle(), o.getDescription(), o.getProductType())));

        for (Map.Entry<ProductKey, List<Product>> newEntry : groupNewFetch.entrySet()) {
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

    public static class ProductKey {

        private String title;
        private String description;
        private ProductType productType;

        public ProductKey(String title, String description, ProductType productType) {
            this.title = title;
            this.description = description;
            this.productType = productType;
        }

        public String getDescription() {
            return description;
        }

        public String getTitle() {
            return title;
        }

        public ProductType getProductType() {
            return productType;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ProductKey that = (ProductKey) o;
            return Objects.equals(title, that.title) &&
                    Objects.equals(description, that.description) &&
                    productType == that.productType;
        }

        @Override
        public int hashCode() {

            return Objects.hash(title, description, productType);
        }
    }

}
