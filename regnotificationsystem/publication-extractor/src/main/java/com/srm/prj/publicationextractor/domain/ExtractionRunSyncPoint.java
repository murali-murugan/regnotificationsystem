package com.srm.prj.publicationextractor.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "extr_run_syncpoint")
public class ExtractionRunSyncPoint {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name="job_id")
    private String jobId;

    @Column(name="run_status")
    private String runStatus;

}
