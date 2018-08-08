package ru.proshik.applepricebot.service.scheduler;

import org.apache.log4j.Logger;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
public class QuartzDefaultScheduler {

    private static final Logger LOG = Logger.getLogger(QuartzDefaultScheduler.class);

    private final Scheduler scheduler;

    @Autowired
    public QuartzDefaultScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    @PostConstruct
    public void init() throws SchedulerException {
        TriggerKey triggerKey = TriggerKey.triggerKey("ScreeningTrigger", "default");
        if (scheduler.getTrigger(triggerKey) != null) {
            LOG.debug("Trigger with key " + triggerKey + " al");
            return;
        }

        JobDetail job = JobBuilder.newJob(ScreeningJob.class)
                .withIdentity("ScreeningJob", "default")
                .storeDurably(true)
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(triggerKey)
                .startAt(Date.from(LocalDateTime.now().plusSeconds(5).atZone(ZoneId.systemDefault()).toInstant()))
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0 10 1/1 * ? *")
                        .withMisfireHandlingInstructionFireAndProceed())
                .build();

        scheduler.scheduleJob(job, trigger);
        scheduler.start();

        LOG.info("Scheduler with cron repeat interval one day was started!");
    }

}
