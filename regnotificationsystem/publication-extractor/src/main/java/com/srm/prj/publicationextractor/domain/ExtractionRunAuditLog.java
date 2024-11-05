package com.srm.prj.publicationextractor.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "extr_run_audit_log")
public class ExtractionRunAuditLog {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name="site_id")
    private String siteId;

    @Column(name="run_completion_date")
    private LocalDateTime runCompletionDate;

    @Column(name="run_start_date")
    private LocalDateTime runStartDate;

    @Column(name="run_completion_status")
    private String runCompletionStatus;

}
