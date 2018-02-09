package ru.proshik.applepricebot.service.scheduler;

import org.apache.log4j.Logger;
import org.quartz.*;
import ru.proshik.applepricebot.dto.DiffProducts;
import ru.proshik.applepricebot.exception.DatabaseException;
import ru.proshik.applepricebot.exception.ProviderParseException;
import ru.proshik.applepricebot.exception.ServiceLayerException;
import ru.proshik.applepricebot.provider.Provider;
import ru.proshik.applepricebot.provider.ProviderFactory;
import ru.proshik.applepricebot.service.DiffService;
import ru.proshik.applepricebot.service.FetchService;
import ru.proshik.applepricebot.service.NotificationQueueService;
import ru.proshik.applepricebot.service.SubscriberService;
import ru.proshik.applepricebot.storage.model.Fetch;
import ru.proshik.applepricebot.storage.model.Shop;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static ru.proshik.applepricebot.utils.FetchUtils.findLastFetch;

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

    private DiffService diffService = new DiffService();

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
                // screening operation
                Fetch fetch = p.getValue().screening();
                // find change in prices and available goods
                List<DiffProducts> diffProducts = tryUpdateFetches(p.getKey(), fetch);
                // if changes was found then send notification to subscribe users
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
        // get fetches for shop from storage
        List<Fetch> existsFetches = fetchService.getFetch(shop);
        //
        if (existsFetches != null && !existsFetches.isEmpty()) {
            Fetch lastFetch = findLastFetch(existsFetches);

            List<DiffProducts> changes = diffService.findDiff(lastFetch, fetch, null);
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

}
