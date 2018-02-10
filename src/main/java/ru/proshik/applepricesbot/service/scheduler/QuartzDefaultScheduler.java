package ru.proshik.applepricesbot.service.scheduler;

import org.apache.log4j.Logger;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import ru.proshik.applepricesbot.provider.ProviderFactory;
import ru.proshik.applepricesbot.service.FetchService;
import ru.proshik.applepricesbot.service.NotificationQueueService;
import ru.proshik.applepricesbot.service.SubscriberService;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static org.quartz.SimpleScheduleBuilder.repeatHourlyForever;
import static org.quartz.SimpleScheduleBuilder.repeatMinutelyForever;

public class QuartzDefaultScheduler {

    private static final Logger LOG = Logger.getLogger(QuartzDefaultScheduler.class);

    private static final int HOUR_INTERVAL = 1;

    private FetchService fetchService;
    private ProviderFactory providerFactory;
    private final SubscriberService subscriberService;
    private final NotificationQueueService notificationQueueService;

    public QuartzDefaultScheduler(ProviderFactory providerFactory,
                                  FetchService fetchService,
                                  SubscriberService subscriberService,
                                  NotificationQueueService notificationQueueService) {
        this.providerFactory = providerFactory;
        this.fetchService = fetchService;
        this.subscriberService = subscriberService;
        this.notificationQueueService = notificationQueueService;
    }

    public void init() throws SchedulerException {
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

        JobDetail job = JobBuilder.newJob(ScreeningJob.class)
                .withIdentity("ScreeningJob", "default")
                .build();

        LocalDateTime now = LocalDateTime.now().plusSeconds(5);

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("ScreeningTrigger", "default")
                .startAt(Date.from(now.atZone(ZoneId.systemDefault()).toInstant()))
                .withSchedule(repeatHourlyForever(HOUR_INTERVAL))
                .build();

        // Tell quartz to schedule the job using our trigger
        scheduler.getContext().put(ScreeningJob.PROVIDER_LABEL, providerFactory);
        scheduler.getContext().put(ScreeningJob.FETCH_SERVICE_LABEL, fetchService);
        scheduler.getContext().put(ScreeningJob.SUBSCRIBER_SERVICE_LABEL, subscriberService);
        scheduler.getContext().put(ScreeningJob.NOTIFICATION_SERVICE_LABEL, notificationQueueService);

        scheduler.scheduleJob(job, trigger);
        scheduler.start();

        LOG.info("Scheduler with hour repeat interval=" + HOUR_INTERVAL + "h was started!");
    }

}
