package ru.proshik.applepriceparcer.service.scheduler;

import org.apache.log4j.Logger;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import ru.proshik.applepriceparcer.provider2.ProviderFactory;
import ru.proshik.applepriceparcer.service.FetchService;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static org.quartz.SimpleScheduleBuilder.repeatHourlyForever;
import static org.quartz.SimpleScheduleBuilder.repeatMinutelyForever;

public class QuartzDefaultScheduler2 {

    private static final Logger LOG = Logger.getLogger(QuartzDefaultScheduler2.class);

    private static final int HOUR_INTERVAL = 1;

    private ProviderFactory providerFactory;
    private FetchService fetchService;

    public QuartzDefaultScheduler2(ProviderFactory providerFactory, FetchService fetchService) {
        this.providerFactory = providerFactory;
        this.fetchService = fetchService;
    }

    public void init() throws SchedulerException {
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

        JobDetail job = JobBuilder.newJob(ShopJob2.class)
                .withIdentity("ShopJob2", "default")
                .build();

        LocalDateTime now = LocalDateTime.now().plusSeconds(5);

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("ShopTrigger", "default")
                .startAt(Date.from(now.atZone(ZoneId.systemDefault()).toInstant()))
                .withSchedule(repeatMinutelyForever(HOUR_INTERVAL))
                .build();

        // Tell quartz to schedule the job using our trigger
        scheduler.getContext().put(ShopJob2.PROVIDER_LABEL, providerFactory);
        scheduler.getContext().put(ShopJob2.FETCH_SERVICE_LABEL, fetchService);

        scheduler.scheduleJob(job, trigger);
        scheduler.start();

        LOG.info("Scheduler with hour repeat interval=" + HOUR_INTERVAL + "h was started!");
    }

}
