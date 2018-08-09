package ru.proshik.applepricebot.service.scheduler;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import ru.proshik.applepricebot.service.DiffService;
import ru.proshik.applepricebot.service.v2.NotificationService;
import ru.proshik.applepricebot.service.v2.ScreeningService;

public class ScreeningJob implements Job {

    private static final Logger LOG = Logger.getLogger(ScreeningJob.class);

    @Autowired
    private ScreeningService screeningService;

    @Autowired
    private DiffService diffService;

    @Autowired
    private NotificationService notificationService;

    @Override
    public void execute(JobExecutionContext context) {
        try {
            screeningService.provideProducts(true);
        } catch (Exception e) {
            LOG.error("Unexpected error on screening job", e);
        }
//        LocalDateTime previousDay = LocalDateTime.now().minusDays(1);
//
//        Map<ShopType, Map<ProductT ype, List<PriceProductDifferent>>> different = diffService.buildPriceDifferent(assortment, previousDay);
//        if (!different.isEmpty()) {
//            notificationService.buildEventNotification(different);
//        }

    }

}
