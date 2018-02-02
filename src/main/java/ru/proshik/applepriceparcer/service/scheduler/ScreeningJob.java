package ru.proshik.applepriceparcer.service.scheduler;

import org.apache.log4j.Logger;
import org.quartz.*;
import ru.proshik.applepriceparcer.exception.ProviderParseException;
import ru.proshik.applepriceparcer.exception.ServiceLayerException;
import ru.proshik.applepriceparcer.model2.Fetch;
import ru.proshik.applepriceparcer.model2.Product;
import ru.proshik.applepriceparcer.model2.Shop;
import ru.proshik.applepriceparcer.provider2.Provider;
import ru.proshik.applepriceparcer.provider2.ProviderFactory;
import ru.proshik.applepriceparcer.service.FetchService;
import ru.proshik.applepriceparcer.service.SubscriberService;

import java.util.List;
import java.util.Map;

import static ru.proshik.applepriceparcer.FetchUtils.findLastFetch;

public class ScreeningJob implements Job {

    private static final Logger LOG = Logger.getLogger(ScreeningJob.class);

    public static final String PROVIDER_LABEL = "ProviderFactory";
    public static final String FETCH_SERVICE_LABEL = "FetchService";
    public static final String SUBSCRIBER_SERVICE_LABEL = "SubscriberService";

    private ProviderFactory providerFactory;
    private FetchService fetchService;
    private SubscriberService subscriberService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            init(context);
        } catch (SchedulerException e) {
            throw new JobExecutionException(e);
        }

        Map<Shop, Provider> providers = providerFactory.providers();
        for (Map.Entry<Shop, Provider> p : providers.entrySet()) {
            try {
                Fetch fetch = p.getValue().screening();

                boolean wasUpdated = tryUpdateFetches(p.getKey(), fetch);
                // TODO: 16.01.2018 run update an users which has subscribed on a updates of the shop

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
    }

    public boolean tryUpdateFetches(Shop shop, Fetch fetch) throws ServiceLayerException {
        List<Fetch> existsFetches = fetchService.getFetch(shop);

        if (existsFetches != null && !existsFetches.isEmpty()) {
            boolean wasChanges = wasChangeInAssortments(fetch, existsFetches);
            if (wasChanges) {
                fetchService.addFetch(shop, fetch);
                LOG.info("Success updated assortment for shop with title=" + shop.getTitle());
                return true;
            } else {
                LOG.info("Not found changes in assortment for shop with title=" + shop.getTitle());
            }
        } else {
            fetchService.addFetch(shop, fetch);
            LOG.info("Success added at first time assortment for shop with title=" + shop.getTitle());
        }

        return false;
    }

    private boolean wasChangeInAssortments(Fetch newFetch, List<Fetch> existsFetches) {
        Fetch lastFetch = findLastFetch(existsFetches);

        List<Product> newProducts = newFetch.getProducts();
        List<Product> lastFetchProducts = lastFetch.getProducts();

        for (Product p : lastFetchProducts) {
            boolean notFound = newProducts.contains(p);
            if (!notFound) {
                return true;
            }
        }
        return false;
    }

}
