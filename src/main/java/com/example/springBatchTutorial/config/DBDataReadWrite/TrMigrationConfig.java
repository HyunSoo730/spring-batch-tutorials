package com.example.springBatchTutorial.config.DBDataReadWrite;

import com.example.springBatchTutorial.domain.accounts.Accounts;
import com.example.springBatchTutorial.domain.accounts.AccountsRepository;
import com.example.springBatchTutorial.domain.orders.Orders;
import com.example.springBatchTutorial.domain.orders.OrdersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * desc : 주문 테이블 -> 정산 테이블 데이터 이관
 * run : --spring.batch.job.names=trMigrationJob
 */
@Configuration
@RequiredArgsConstructor
public class TrMigrationConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    /**
     * 레포지토리를 통해 데이터를 이관시키기 위해
     */
    private final OrdersRepository ordersRepository;
    private final AccountsRepository accountsRepository;

    @Bean
    public Job trMigrationJob(Step trMigrationStep) {
        return jobBuilderFactory.get("trMigrationJob")
                .incrementer(new RunIdIncrementer())
                .start(trMigrationStep)
                .build();
    }

    @JobScope
    @Bean
    public Step trMigrationStep(ItemReader<Orders> trOrdersReader, ItemProcessor toOrderProcessor, ItemWriter toOrdersWriter) {
        return stepBuilderFactory.get("trMigrationStep")
                .<Orders, Orders>chunk(5)
                .reader(trOrdersReader)
//                .writer(new ItemWriter<Orders>() {
//                    @Override
//                    public void write(List<? extends Orders> items) throws Exception {
//                        items.forEach(System.out::println);
//                    }
//                })
                .processor(toOrderProcessor)
                .writer(toOrdersWriter)
                .build();
    }

    @StepScope
    @Bean
    public ItemWriter<Accounts> toOrdersWriter2() {
        return new ItemWriter<Accounts>() {
            @Override
            public void write(List<? extends Accounts> items) throws Exception {
                items.forEach(item -> accountsRepository.save(item));
            }
        };
    }

    @StepScope
    @Bean
    public RepositoryItemWriter<Accounts> toOrdersWriter() {
        return new RepositoryItemWriterBuilder<Accounts>()
                .repository(accountsRepository)
                .methodName("save")
                .build();
    }

    //Orders로 읽어와서 Accounts로 변경해주는(가공해주는) processor
    @StepScope
    @Bean
    public ItemProcessor<Orders, Accounts> toOrderProcessor() {
        //프로세서 리턴해줘야함
        return new ItemProcessor<Orders, Accounts>() {
            @Override
            public Accounts process(Orders item) throws Exception {
                /**
                 * 지금은 단순히 주문 객체를 Accounts 객체로 변환해주는 것이기 때문에 생성자로 간단하게 처리 가능.
                 */
                return new Accounts(item);
            }
        };
    }

    @StepScope
    @Bean
    public RepositoryItemReader<Orders> trOrdersReader() {
        return new RepositoryItemReaderBuilder<Orders>()
                .name("trOrdersReader")
                .repository(ordersRepository)
                .methodName("findAll")
                .pageSize(5)
                .arguments(Arrays.asList())
                .sorts(Collections.singletonMap("id", Sort.Direction.ASC))
                .build();
    }

}
