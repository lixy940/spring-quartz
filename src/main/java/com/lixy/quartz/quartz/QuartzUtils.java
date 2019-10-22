package com.lixy.quartz.quartz;

import org.apache.commons.lang3.StringUtils;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Quartz工具类
 *
 * @date 2019/2/18
 */
@Component
public class QuartzUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuartzUtils.class);

    @Autowired
    private Scheduler scheduler;


    /**
     * 新增任务
     *
     * @param cron 0/5 * * * * ?
     * @return
     * @date 2019/2/18
     */
    public boolean addJob(String id, String cron, Class<? extends Job> jobClass, Map<String, Object> params) {
        if (StringUtils.isBlank(id) || StringUtils.isBlank(cron) || null == jobClass) {
            return false;
        }

        try {
            JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(getJobKey(id)).build();
            if (null != params && !params.isEmpty()) {
                for (Map.Entry<String, Object> entry : params.entrySet()) {
                    jobDetail.getJobDataMap().put(entry.getKey(), entry.getValue());
                }
            }

            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(getTriggerKey(id))
                    .withSchedule(CronScheduleBuilder.cronSchedule(cron).withMisfireHandlingInstructionDoNothing())
                    .forJob(getJobKey(id))
                    .build();
            scheduler.scheduleJob(jobDetail, trigger);
            return true;
        } catch (Exception e) {
            LOGGER.error("新建任务失败, id: {}", id, e);
            return false;
        }
    }


    /**
     * 更新cron
     *
     * @param
     * @return
     * @date 2019/2/18
     */
    public boolean updateCron(String id, String cron) {
        if (StringUtils.isBlank(id) || StringUtils.isBlank(cron)) {
            return false;
        }

        try {
            CronTriggerImpl trigger = (CronTriggerImpl) scheduler.getTrigger(getTriggerKey(id));
            trigger.setCronExpression(new CronExpression(cron));
            scheduler.rescheduleJob(getTriggerKey(id), trigger);
            return true;
        } catch (Exception e) {
            LOGGER.error("更新任务时间失败, id: {}", id, e);
            return false;
        }
    }


    /**
     * 删除任务
     *
     * @param
     * @return
     * @date 2019/2/18
     */
    public boolean deleteJob(String id) {
        if (StringUtils.isBlank(id)) {
            return false;
        }

        try {
            scheduler.deleteJob(getJobKey(id));
            return true;
        } catch (Exception e) {
            LOGGER.error("删除任务失败, id: {}", id, e);
            return false;
        }
    }


    /**
     * 获取所有jobKey
     *
     * @param
     * @return
     * @date 2019/2/20
     */
    public Set<JobKey> getAllJobs() {
        try {
            return scheduler.getJobKeys(GroupMatcher.jobGroupEquals(JobConstant.GROUP_NAME));
        } catch (Exception e) {
            LOGGER.error("获取所有jobKey失败", e);
            return null;
        }
    }


    /**
     * 检查任务是否存在
     *
     * @param
     * @return
     * @date 2019/2/20
     */
    public boolean checkJobExists(String id) {
        if (StringUtils.isBlank(id)) {
            return false;
        }

        try {
            return scheduler.checkExists(getJobKey(id));
        } catch (Exception e) {
            LOGGER.error("检查任务是否存在失败, id: {}", id, e);
            return false;
        }
    }


    /**
     * 恢复所有错误的触发器
     *
     * @param
     * @return
     * @date 2019/2/19
     */
    public void restoreAllErrorTriggers() {
        try {
            List<String> jobGroupNames = scheduler.getJobGroupNames();
            if (CollectionUtils.isEmpty(jobGroupNames)) {
                return;
            }

            for (String jobGroupName : jobGroupNames) {
                Set<JobKey> jobKeys = scheduler.getJobKeys(GroupMatcher.jobGroupEquals(jobGroupName));
                if (CollectionUtils.isEmpty(jobKeys)) {
                    continue;
                }
                for (JobKey jobKey : jobKeys) {
                    List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
                    if (CollectionUtils.isEmpty(triggers)) {
                        continue;
                    }
                    for (Trigger trigger : triggers) {
                        TriggerKey triggerKey = trigger.getKey();
                        if (Trigger.TriggerState.ERROR.equals(scheduler.getTriggerState(triggerKey))) {
                            scheduler.resumeTrigger(triggerKey);
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("恢复所有错误的触发器失败", e);
        }
    }


    /**
     * job key
     *
     * @param
     * @return
     * @date 2019/2/18
     */
    private JobKey getJobKey(String id) {
        if (StringUtils.isBlank(id)) {
            return null;
        }
        return new JobKey(id, JobConstant.GROUP_NAME);
    }


    /**
     * trigger key
     *
     * @param
     * @return
     * @date 2019/2/18
     */
    private TriggerKey getTriggerKey(String id) {
        if (StringUtils.isBlank(id)) {
            return null;
        }
        return new TriggerKey(id, JobConstant.GROUP_NAME);
    }

}
