package com.lixy.quartz.quartz.job;


import com.lixy.quartz.quartz.JobConstant;
import com.lixy.quartz.service.DataExtractService;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 定时导入任务
 **/
@Component
@DisallowConcurrentExecution
@PersistJobDataAfterExecution
public class MissionJobImpl implements Job {

    private static final Logger LOGGER = LoggerFactory.getLogger(MissionJobImpl.class);

    @Autowired
    private DataExtractService dataExtractService;

    @Override
    public void execute(JobExecutionContext context) {
        try {
            JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
            Integer taskId = (Integer) jobDataMap.get(JobConstant.TASK_ID_KEY);
            dataExtractService.executeTask(taskId);
        } catch (Exception e) {
            LOGGER.error("定时任务导入执行失败", e);
        }
    }
}
