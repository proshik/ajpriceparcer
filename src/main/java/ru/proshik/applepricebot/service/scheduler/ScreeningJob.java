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

//        LocalDateTime previousDay = LocalDateTime.now().minusDays(1);
//
//        Map<ShopType, Map<ProductType, List<PriceProductDifferent>>> different = diffService.buildPriceDifferent(products, previousDay);
//        if (!different.isEmpty()) {
//            notificationService.buildEventNotification(different);
//        }

    }

}
