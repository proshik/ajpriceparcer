package ru.proshik.applepriceparcer.service;

import org.apache.log4j.Logger;
import ru.proshik.applepriceparcer.model2.DiffProducts;
import ru.proshik.applepriceparcer.model2.ChangeProductNotification;
import ru.proshik.applepriceparcer.model2.Shop;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class NotificationQueueService {

    private static final Logger LOG = Logger.getLogger(NotificationQueueService.class);

    private BlockingQueue<ChangeProductNotification> blockingQueue = new LinkedBlockingDeque<>();

    public void add(Shop shop, String userId, List<DiffProducts> diffProducts) {
        try {
            blockingQueue.add(new ChangeProductNotification(userId, shop, diffProducts));
        } catch (IllegalStateException e) {
            LOG.error("Error on add element in notification queue for userId=" + userId + ", shop=" + shop.getTitle());
        }
    }

    public ChangeProductNotification take() {
        try {
            return blockingQueue.take();
        } catch (InterruptedException e) {
            LOG.error("Error on read from queue", e);
        } catch (Exception e) {
            LOG.error("Unexpected error on read from queue", e);
        }
        return null;
    }
}
