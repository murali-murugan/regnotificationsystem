package com.srm.prj.publicationextractor.bootstrap;

import com.srm.prj.publicationextractor.schedular.quartz.FinJobScheduler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ExtractorBootStrap implements CommandLineRunner {

    @Autowired
    private FinJobScheduler finJobScheduler;

    @Override
    public void run(String... args) throws Exception {

        log.debug("Inside Bootstrap!");

        finJobScheduler.createJobsAndStart();

        log.debug("Exit Bootstrap!");
    }
}
