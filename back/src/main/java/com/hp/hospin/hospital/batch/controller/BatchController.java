//package com.hp.hospin.hospital.batch.controller;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.batch.core.Job;
//import org.springframework.batch.core.JobExecution;
//import org.springframework.batch.core.JobParameter;
//import org.springframework.batch.core.JobParameters;
//import org.springframework.batch.core.launch.JobLauncher;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/batch")
//@RequiredArgsConstructor
//public class BatchController {
//
//    private final JobLauncher jobLauncher;
//    private final Job loadHospitalJob;
//    private final Job loadHospitalGradeJob;
//    private final Job loadHospitalDetailJob;
//
//    @PostMapping("/hospital")
//    public String runHospitalJob() throws Exception {
//        JobExecution execution = jobLauncher.run(loadHospitalJob, createJobParameters("loadHospitalJob"));
//        return "Hospital Job status: " + execution.getStatus();
//    }
//
//    @PostMapping("/hospital-grade")
//    public String runHospitalGradeJob() throws Exception {
//        JobExecution execution = jobLauncher.run(loadHospitalGradeJob, createJobParameters("loadHospitalGradeJob"));
//        return "Hospital Grade Job status: " + execution.getStatus();
//    }
//
//    @PostMapping("/hospital-detail")
//    public String runHospitalDetailJob() throws Exception {
//        JobExecution execution = jobLauncher.run(loadHospitalDetailJob, createJobParameters("loadHospitalDetailJob"));
//        return "Hospital Detail Job status: " + execution.getStatus();
//    }
//
//    private JobParameters createJobParameters(String jobName) {
//        Map<String, JobParameter<?>> params = new HashMap<>();
//        params.put("jobName", new JobParameter<>(jobName, String.class));
//        params.put("timestamp", new JobParameter<>(new Date().getTime(), Long.class)); // 중복 실행 방지용
//        return new JobParameters(params);
//    }
//}