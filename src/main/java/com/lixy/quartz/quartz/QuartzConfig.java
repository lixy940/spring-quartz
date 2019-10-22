package com.lixy.quartz.quartz;

import org.quartz.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Quartz配置
 *
 * @param
 * @return
 */
@Configuration
@DependsOn(value = {"dataSource", "jobFactory"})
public class QuartzConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuartzConfig.class);

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JobFactory jobFactory;


    @Bean
    public SchedulerFactoryBean schedulerFactoryBean() {
        int threadTotal = Runtime.getRuntime().availableProcessors() * 10;
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        schedulerFactoryBean.setJobFactory(jobFactory);

        schedulerFactoryBean.setDataSource(dataSource);
        Properties properties = new Properties();
        properties.setProperty("org.quartz.jobStore.class", "org.quartz.impl.jdbcjobstore.JobStoreTX");
        properties.setProperty("org.quartz.jobStore.tablePrefix", "QRTZ_");
        properties.setProperty("org.quartz.jobStore.isClustered", "false");
        properties.setProperty("org.quartz.threadPool.threadCount", String.valueOf(threadTotal));
        schedulerFactoryBean.setQuartzProperties(properties);

        return schedulerFactoryBean;
    }


    @Bean
    @Primary
    public Scheduler scheduler() {
        Scheduler scheduler = schedulerFactoryBean().getScheduler();
        try {
            scheduler.start();
            LOGGER.info("scheduler启动完成");
        } catch (Exception e) {
            LOGGER.error("scheduler start error", e);
        }
        return scheduler;
    }
}
