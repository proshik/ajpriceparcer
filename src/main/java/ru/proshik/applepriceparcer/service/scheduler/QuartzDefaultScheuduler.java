package ru.proshik.applepriceparcer.service.scheduler;

import org.apache.log4j.Logger;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static org.quartz.SimpleScheduleBuilder.repeatSecondlyForever;

public class QuartzDefaultScheuduler {

    private static final Logger LOG = Logger.getLogger(QuartzDefaultScheuduler.class);

    private static final int MINUTE_INTERVAL = 5;

    public void init() throws SchedulerException {
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

        JobDetail job = JobBuilder.newJob(ShopJob.class)
                .withIdentity("ShopJob", "default5")
                .build();

        LocalDateTime now = LocalDateTime.now().plusSeconds(5);

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("ShopTrigger", "default")
                .startAt(Date.from(now.atZone(ZoneId.systemDefault()).toInstant()))
                .withSchedule(repeatSecondlyForever(MINUTE_INTERVAL)
                        .repeatForever())
                .build();

        // Tell quartz to schedule the job using our trigger
        scheduler.start();

        Date date = scheduler.scheduleJob(job, trigger);

        LOG.info("Scheduler with repeat interval=" + MINUTE_INTERVAL + "was run in time: " + date);
    }

}
