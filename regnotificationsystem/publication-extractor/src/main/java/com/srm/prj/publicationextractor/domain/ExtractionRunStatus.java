package com.srm.prj.publicationextractor.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "extr_run_status")
public class ExtractionRunStatus {
    public static final String EXTRACTION_RUN_STATUS_RUNNING = "RUNNING";
    public static final String EXTRACTION_RUN_STATUS_COMPLETED = "COMPLETED";
    public static final String EXTRACTION_RUN_STATUS_ERROR = "COMPLETED_WITH_ERROR";
    public static final String EXTRACTION_RUN_STATUS_READY = "READY";

    @Id
    @Column(name="site_id")
    private String siteId;

    @Column(name="last_publish_date")
    private LocalDate lastPublishDate;

    @Column(name="run_status")
    private String runStatus;

    @Column(name="last_run_date")
    private LocalDateTime lastRunDate;

    @Column(name="update_date")
    private LocalDateTime updateDate;

}
