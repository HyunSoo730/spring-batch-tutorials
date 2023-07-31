package com.example.springBatchTutorial.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class TaskletJob {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    /**
     * tasklet을 이용해 간단한 배치는 가능
     */

    @Bean
    public Job taskletBatchBuild() {
        return jobBuilderFactory.get("taskletJob")
                .start(taskletJob_step1())
                .build();  //build()를 통해 Job 리턴
    }

    @Bean
    public Step taskletJob_step1() {
        return stepBuilderFactory.get("taskletJob_step1")
                .tasklet((a, b) -> {
                    log.info("==== job ==== [step1]");
                    System.out.println("asdasdasd=======");
                    return RepeatStatus.FINISHED;
                }).build();
    }

}
