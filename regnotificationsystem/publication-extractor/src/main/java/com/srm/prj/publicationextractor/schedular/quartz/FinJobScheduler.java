package com.srm.prj.publicationextractor.schedular.quartz;

import com.srm.prj.publicationextractor.schedular.job.ExtractionAndEmailJob;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static org.quartz.JobBuilder.newJob;

@Slf4j
@Component
public class FinJobScheduler {

    @Autowired
    private Scheduler scheduler;

    @Value("${fin.extraction.cron.expr}")
    private String cronExpression;

    @Value("${fin.extraction.sched.enable.flag}")
    private boolean enableScheduler;

    public void createJobsAndStart() throws SchedulerException {

        //- if scheduler not enabled then dont start the schedular
        if (!enableScheduler) { return; }

        log.debug("Begin starting quartz jobs");

        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("jobID", "Extract-And-Notify-Job-1");

        JobDetail job = newJob(ExtractionAndEmailJob.class)
                .withIdentity("Extract-And-Notify-1")
                .usingJobData(jobDataMap)
                .build();

        Trigger trigger1 = TriggerBuilder
                .newTrigger()
                .withIdentity("dummyTriggerName1", "group1")
                .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                .build();

        scheduler.scheduleJob(job, trigger1);

        scheduler.start();

        log.debug("Quartz jobs started");

    }

}
