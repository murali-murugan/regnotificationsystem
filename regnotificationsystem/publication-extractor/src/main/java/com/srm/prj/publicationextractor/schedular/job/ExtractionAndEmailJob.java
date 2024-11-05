package com.srm.prj.publicationextractor.schedular.job;


import com.srm.prj.publicationextractor.services.AllNewsSiteRunner;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ExtractionAndEmailJob implements Job {

    private final AllNewsSiteRunner allNewsSiteRunner;

    public ExtractionAndEmailJob(AllNewsSiteRunner allNewsSiteRunner) {
        this.allNewsSiteRunner = allNewsSiteRunner;
    }


    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
               allNewsSiteRunner.extractAllPublications();
    }
}
