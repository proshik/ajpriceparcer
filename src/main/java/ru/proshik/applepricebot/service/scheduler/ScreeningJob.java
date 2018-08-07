package ru.proshik.applepricebot.service.scheduler;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import ru.proshik.applepricebot.model.PriceProductDifferent;
import ru.proshik.applepricebot.repository.model.Product;
import ru.proshik.applepricebot.repository.model.ShopType;
import ru.proshik.applepricebot.service.DiffService;
import ru.proshik.applepricebot.service.v2.NotificationService;
import ru.proshik.applepricebot.service.v2.ProductService;
import ru.proshik.applepricebot.storage.model.ProductType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class ScreeningJob implements Job {

    private static final Logger LOG = Logger.getLogger(ScreeningJob.class);

    @Autowired
    private ProductService productService;

    @Autowired
    private DiffService diffService;

    @Autowired
    private NotificationService notificationService;

    // Provide products.
    // Find different from previous day grouped by Shop and then Goods
    // If different is exists to notify users by their subscription

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        Map<ShopType, List<Product>> products = productService.provideProducts();

        LocalDateTime previousDay = LocalDateTime.now().minusDays(1);

        Map<ShopType, Map<ProductType, List<PriceProductDifferent>>> different = diffService.buildPriceDifferent(products, previousDay);
        if (!different.isEmpty()) {
            notificationService.buildEventNotification(different);
        }

//
//
//        for (Map.Entry<ShopType, List<Product>> entry : products.entrySet()) {
//            try {
//                Fetch fetch = p.getValue().screening();
//                // find change in prices and available goods
//                List<DiffProducts> diffProducts = tryUpdateFetches(p.getKey(), fetch);
//                // if changes was found then send notification to subscribe users
//                if (!diffProducts.isEmpty()) {
//                    List<String> users = shopListMap.get(p.getKey());
//                    if (users != null) {
//                        for (String userId : users) {
//                            notificationQueueService.add(p.getKey(), userId, diffProducts);
//                        }
//                    }
//                }
//            } catch (ProviderParseException e) {
//                LOG.error("Error on screening shop with title=" + p.getKey().getTitle(), e);
//            } catch (Exception e) {
//                LOG.error("Unexpected error in scheduler service on execute operation " +
//                        "for request assortment for shop with title=" + p.getKey().getTitle(), e);
//            }
//        }
    }

//    private void init(JobExecutionContext context) throws SchedulerException {
//        SchedulerContext schedulerContext = context.getScheduler().getContext();
//
//        fetchService = (FetchService) schedulerContext.get(FETCH_SERVICE_LABEL);
//        providerFactory = (ProviderFactory) schedulerContext.get(PROVIDER_LABEL);
//        subscriberService = (SubscriberService) schedulerContext.get(SUBSCRIBER_SERVICE_LABEL);
//        notificationQueueService = (NotificationQueueService) schedulerContext.get(NOTIFICATION_SERVICE_LABEL);
//    }

//    private List<DiffProducts> tryUpdateFetches(Shop shop, Fetch fetch) throws ServiceLayerException {
//        // get fetches for shop from storage
//        List<Fetch> existsFetches = fetchService.getFetch(shop);
//        //
//        if (existsFetches != null && !existsFetches.isEmpty()) {
//            Fetch lastFetch = findLastFetch(existsFetches);
//
//            List<DiffProducts> changes = diffService.findDiff(lastFetch, fetch, null);
//            if (!changes.isEmpty()) {
//                fetchService.addFetch(shop, fetch);
//                LOG.info("Success updated fetch for shop with title=" + shop.getTitle());
//                return changes;
//            } else {
//                LOG.info("Not found changes in fetch for shop with title=" + shop.getTitle());
//            }
//        } else {
//            fetchService.addFetch(shop, fetch);
//            LOG.info("Success added at first time fetch for shop with title=" + shop.getTitle());
//            return Collections.emptyList();
//        }
//
//        return Collections.emptyList();
//    }

}
