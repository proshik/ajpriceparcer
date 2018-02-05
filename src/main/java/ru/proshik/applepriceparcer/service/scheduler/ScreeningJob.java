package ru.proshik.applepriceparcer.service.scheduler;

import org.apache.log4j.Logger;
import org.quartz.*;
import ru.proshik.applepriceparcer.exception.DatabaseException;
import ru.proshik.applepriceparcer.exception.ProviderParseException;
import ru.proshik.applepriceparcer.exception.ServiceLayerException;
import ru.proshik.applepriceparcer.model2.DiffProducts;
import ru.proshik.applepriceparcer.model2.Fetch;
import ru.proshik.applepriceparcer.model2.Product;
import ru.proshik.applepriceparcer.model2.Shop;
import ru.proshik.applepriceparcer.provider2.Provider;
import ru.proshik.applepriceparcer.provider2.ProviderFactory;
import ru.proshik.applepriceparcer.service.FetchService;
import ru.proshik.applepriceparcer.service.NotificationQueueService;
import ru.proshik.applepriceparcer.service.SubscriberService;

import java.util.*;
import java.util.function.Function;
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
//                if (wasUpdated){
                List<String> users = shopListMap.get(p.getKey());
                if (users != null) {
                    for (String userId : users) {
                        notificationQueueService.add(p.getKey(), userId, diffProducts);
                    }
                }
//                }
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
                LOG.info("Success updated assortment for shop with title=" + shop.getTitle());
                return changes;
            } else {
                LOG.info("Not found changes in assortment for shop with title=" + shop.getTitle());
            }
        } else {
            fetchService.addFetch(shop, fetch);
            LOG.info("Success added at first time assortment for shop with title=" + shop.getTitle());
            return Collections.emptyList();
        }

        return Collections.emptyList();
    }

    private List<DiffProducts> findChanges(Fetch newFetch, List<Fetch> existsFetches) {
        List<DiffProducts> diff = new ArrayList<>();

        Fetch lastFetch = findLastFetch(existsFetches);

        List<Product> newProducts = newFetch.getProducts();
        List<Product> lastFetchProducts = lastFetch.getProducts();

        Map<Product.ProductKey, Product> groupByProductKey = newProducts.stream()
                .collect(Collectors.toMap(p -> new Product.ProductKey(p.getTitle(), p.getDescription(), p.getPrice(),
                        p.getAssortmentType(), p.getProductType()), Function.identity()));

        for (Product oldProduct : lastFetchProducts) {
            Product.ProductKey key = new Product.ProductKey(oldProduct.getTitle(), oldProduct.getDescription(),
                    oldProduct.getPrice(), oldProduct.getAssortmentType(), oldProduct.getProductType());

            Product newProduct = groupByProductKey.get(key);
            if (newProduct != null) {
                if (!oldProduct.getPrice().equals(newProduct.getPrice())) {
                    diff.add(new DiffProducts(oldProduct, newProduct));
                }
            } else {
                LOG.warn("Not found element in NewProductMap for key = " + key);
                diff.add(new DiffProducts(oldProduct, null));
            }
        }
        return diff;
    }

}
