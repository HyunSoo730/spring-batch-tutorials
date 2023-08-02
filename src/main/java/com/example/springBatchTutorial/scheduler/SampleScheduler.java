package com.example.springBatchTutorial.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component  //Bean으로 등록하기 위해 Component
@RequiredArgsConstructor
public class SampleScheduler {

    private final Job helloWorldJob;
    private final JobLauncher jobLauncher;

    @Scheduled(cron = "0 */1 * * * *")   //1분에 한번씩 helloWorldJob을 실행시키는 스케줄이라고 말하는거임
    public void helloWorldJobRun() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {

        JobParameters jobParameters = new JobParameters(
                //넘길 파라미터에 대해서 Map 형식으로 key value를 넘겨주면 된다.
                Collections.singletonMap("requestTime", new JobParameter(System.currentTimeMillis()))
        );

        jobLauncher.run(helloWorldJob, jobParameters);
    }
}
