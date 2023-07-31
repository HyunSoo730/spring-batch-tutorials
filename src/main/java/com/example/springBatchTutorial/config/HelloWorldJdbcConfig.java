package com.example.springBatchTutorial.config;

import com.example.springBatchTutorial.batch.validatedparam.validator.FileParamValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.*;
import org.springframework.batch.core.job.CompositeJobParametersValidator;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class HelloWorldJdbcConfig {

    private final JobBuilderFactory jobBuilderFactory;  //이걸 통해 JOb을 생성
    private final StepBuilderFactory stepBuilderFactory;  //Step 생성을 위해
    /**
     * 두 객체를 활용하여 Job과 Step을 생성할꺼야
     Job은 JobBuilderFactory로 만들고
     Step은 StepBuilderFactory로 만들자.
     */

    /**
     * 스프링 배치 5.0 버전부터는 StepBuilderFactory, JobBuilderFactory가 Deprecated되었다.
     * 그래서 이제는 JobRepository를 명시적으로 사용하는 방식으로 해야한다!!!
     * 하지만 아직 jdk17 보다는 jdk 8,11버전으로 공부하자. 레퍼런스가 훨씬 많다.
     * 완전히 바뀐게 아니라서 내용은 공부할 수 있어.
     */

    @Bean
    public Job helloWorldJob() {
        Job helloWorldJob = jobBuilderFactory.get("helloWorldJob")  //이름을 정해주고, get에 써놓은 Job 이름으로 배치를 실행하게 될꺼야.
                .incrementer(new RunIdIncrementer())  //Job을 실행할 때 id를 부여하는데, Sequence를 순차적으로 부여할 수 있도록 RunIdIncrementer를 해주자.
                .validator(multipleValidator())
                .start(helloWorldStep())   //Job안에는 Step이 존재해야해.
                .build();
        return helloWorldJob;
    }

    private CompositeJobParametersValidator multipleValidator() {
        CompositeJobParametersValidator validator = new CompositeJobParametersValidator();
        validator.setValidators(Arrays.asList(new FileParamValidator()));
        return validator;
    }

    @JobScope
    @Bean
    public Step helloWorldStep() {

        TaskletStep helloWorldStep = stepBuilderFactory.get("helloWorldStep")  //역시 똑같이 get을 통해 이름 명시
                .tasklet(helloWorldTasklet())     //일단 간단하게 taskLet으로 해보자.
                .build();
        return helloWorldStep;
    }

    @StepScope   //step 하위에서 실행되기 때문에 @StepScope를 등록하자.
    @Bean
    public Tasklet helloWorldTasklet() {
        //Tasklet은 그냥 만들면 돼
        Tasklet tasklet = new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                //내가 원하는 작업 부분
                System.out.println("Hello World Spring Batch");
                log.info("asdasdasd");
                log.info("batch start!!");
                //원하는 작업이 끝난 이후 어떻게 할 것인가 ?에 대한 Status를 명시해야함.
                return RepeatStatus.FINISHED; // FINISHED를 명시함으로써 이 Step을 끝낸다는 뜻.
            }
        };
        return tasklet;
    }

    /**
     * 위 과정을 모두 다 했다면
     * HelloWorld를 보기 위한 하나의 Job이 완성이 된 것이다 !
     * 스프링을 실행해서 출력이 되는지 확인하자 !!
     * Job을 실행시킬 때는 properties에서 설정한 batch.job.names를 파라미터로 넘겨주어야 Job이 실행이 된다.
     * 위에서 Job 이름은 helloWorldJob이기 때문에 파라미터로 넣어주자 !
     *Edit Configurations > Program Parameters에 --spring.batch.job.names=helloWorldJob을 넣어주자.
     * 아니면 --job.name={Job이름} 이런 식으로 넘기면 돼
     * 그러고 실행하면 돼  !
     */

    /**
     * 또한 Job을 실행할 때는
     * apllication.properties에 설정한
     * spring.batch.job.names에 파라미터로 넘겨주어야 job이 실행이 된다.
     * --spring.batch.job.names={이름} 이런 식으로 넘겨줘야해
     * 내가 현재 설정한 job의 이름은 helloWorldJob이기 때문에 해당 값을 넣어서 파라미터 등록하자.
     */
}
