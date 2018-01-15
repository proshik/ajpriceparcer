package ru.proshik.applepriceparcer.service.scheduler;

import org.apache.log4j.Logger;
import org.quartz.*;
import ru.proshik.applepriceparcer.exception.DatabaseException;
import ru.proshik.applepriceparcer.exception.ProviderParseException;
import ru.proshik.applepriceparcer.model.Assortment;
import ru.proshik.applepriceparcer.model.Shop;
import ru.proshik.applepriceparcer.provider.Provider;
import ru.proshik.applepriceparcer.service.OperationService;

import java.util.List;

public class ShopJob implements Job {

    private static final Logger LOG = Logger.getLogger(ShopJob.class);

    public static final String OPERATION_SERVICE_LABEL = "OperationService";

    private OperationService operationService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            init(context);
        } catch (SchedulerException e) {
            throw new JobExecutionException(e);
        }

        List<Provider> providers = operationService.selectAllProviders();
        for (Provider p : providers) {
            Shop shop = p.getShop();

            try {
                Assortment assortment = p.screening();
                operationService.tryUpdateAssortment(shop, assortment);
            } catch (ProviderParseException e) {
                LOG.error("Error on screening shop with title=" + shop.getTitle(), e);
            } catch (DatabaseException e) {
                LOG.error("Error on save into database new assortment for shop with title=" + shop.getTitle(), e);
            }

            LOG.info("Success screening shop with title=" + shop.getTitle());
        }
    }

    private void init(JobExecutionContext context) throws SchedulerException {
        SchedulerContext schedulerContext = context.getScheduler().getContext();

        operationService = (OperationService) schedulerContext.get(OPERATION_SERVICE_LABEL);
    }
}
