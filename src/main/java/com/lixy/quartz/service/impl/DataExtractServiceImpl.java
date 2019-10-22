package com.lixy.quartz.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lixy.quartz.dao.HandlerTaskMapper;
import com.lixy.quartz.dao.SysDBInfoMapper;
import com.lixy.quartz.entity.HandlerTask;
import com.lixy.quartz.quartz.JobConstant;
import com.lixy.quartz.quartz.QuartzUtils;
import com.lixy.quartz.quartz.job.MissionJobImpl;
import com.lixy.quartz.service.DataExtractService;
import com.lixy.quartz.vo.HandlerTaskVo;
import com.lixy.quartz.vo.TaskDateTimeVo;
import com.lixy.quartz.vo.TaskSearchVo;
import com.lixy.quartz.vo.TaskShowVo;
import com.lixy.quartz.vo.page.ListToSpringPageVo;
import com.lixy.quartz.vo.page.SpringPageVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Author：MR LIS，2019/10/21
 * Copyright(C) 2019 All rights reserved.
 */
@Service
public class DataExtractServiceImpl implements DataExtractService {

    @Autowired
    private SysDBInfoMapper sysDBInfoMapper;

    @Autowired
    private HandlerTaskMapper handlerTaskMapper;

    @Autowired
    private QuartzUtils quartzUtils;



    @Override
    public void batchAddHandlerTask(HandlerTaskVo[] taskVos) {

        for (HandlerTaskVo taskVo : taskVos) {
            HandlerTask handlerTask = new HandlerTask();
            handlerTask.setCommitId(taskVo.getCommitId());
            handlerTask.setTaskType(taskVo.getTaskType());
            handlerTask.setCronExp(convertTaskDateTimeVoToCron(taskVo.getTaskDateTimeVo(), taskVo.getTaskType()));
            handlerTaskMapper.insertSelective(handlerTask);
        }
    }

    @Override
    public SpringPageVo<TaskShowVo> findTaskPage(TaskSearchVo record) {
        PageHelper.startPage(record.getPageNo(), record.getPageSize());
        List<TaskShowVo> taskPage = handlerTaskMapper.findTaskPage(record);
        PageInfo<TaskShowVo> pageInfos = new PageInfo<>(taskPage);
        return ListToSpringPageVo.listToPage(record.getPageNo(), record.getPageSize(), pageInfos.getTotal(), pageInfos.getList());
    }


    @Override
    public void startTasks(Integer[] taskIds) {
        for (Integer taskId : taskIds) {
            HandlerTask handlerTask = handlerTaskMapper.selectByPrimaryKey(taskId);
            /**
             * taskType: 1 永久一次的 2 周期执行 3 指定时间执行
             */
            if (handlerTask.getTaskType() == TaskTypeEnum.ONCE.getCode()) {
                CompletableFuture.runAsync(() -> {
                    executeTask(taskId);
                });
            } else {
                Map<String, Object> params = new HashMap<>();
                params.put(JobConstant.TASK_ID_KEY, taskId);
                quartzUtils.addJob(JobConstant.DATA_EXTRACT_TASK_JOB_PREFIX + handlerTask.getTaskId(), handlerTask.getCronExp(), MissionJobImpl.class, params);
            }
        }
    }

    @Override
    public void executeTask(Integer taskId) {
        HandlerTask handlerTask = handlerTaskMapper.selectByPrimaryKey(taskId);
        //todo 调用linux脚本

    }


    /**
     * 转换
     *
     * @param
     * @return
     * @author silent
     * @date 2019/2/20
     */
    private String convertTaskDateTimeVoToCron(TaskDateTimeVo vo, int type) {
        if (null == vo) {
            return null;
        }
        String pattern = "{0} {1} {2} * * ? *";
        String hourString = cronNumToString(vo.getHour(), type);
        String minuteString = cronNumToString(vo.getMinute(), type);
        String secondString = cronNumToString(vo.getSecond(), type);
        return MessageFormat.format(pattern, secondString, minuteString, hourString);
    }


    /**
     * 转换
     *
     * @param
     * @return
     * @author silent
     * @date 2019/2/20
     */
    private TaskDateTimeVo convertCronToTaskDateTimeVo(String cron, int type) {
        if (StringUtils.isBlank(cron)) {
            return null;
        }

        String[] strings = cron.split(" ");
        String secondString = strings[0];
        String minuteString = strings[1];
        String hourString = strings[2];
        Integer hour = cronStringToNum(hourString, type);
        Integer minute = cronStringToNum(minuteString, type);
        Integer second = cronStringToNum(secondString, type);

        TaskDateTimeVo vo = new TaskDateTimeVo();
        vo.setHour(hour);
        vo.setMinute(minute);
        vo.setSecond(second);

        return vo;
    }

    /**
     * 转换
     *
     * @param
     * @return
     * @author silent
     * @date 2019/2/20
     */
    private String cronNumToString(Integer num, int type) {
        String ret = null;
        if (TaskTypeEnum.PERIOD.getCode() == type) {
            if (null == num) {
                ret = "*";
            } else {
                ret = "0/" + num;
            }
        } else if (TaskTypeEnum.REGULAR.getCode() == type) {
            if (null == num) {
                ret = "*";
            } else {
                ret = "" + num;
            }
        }
        return ret;
    }

    /**
     * 转换
     *
     * @param
     * @return
     * @author silent
     * @date 2019/2/20
     */
    private Integer cronStringToNum(String s, int type) {
        Integer ret = null;
        if (TaskTypeEnum.PERIOD.getCode() == type) {
            if ("*".equals(s)) {
                ret = null;
            } else {
                ret = Integer.valueOf(s.replace("0/", ""));
            }
        } else if (TaskTypeEnum.REGULAR.getCode() == type) {
            if ("*".equals(s)) {
                ret = null;
            } else {
                ret = Integer.valueOf(s);
            }
        }
        return ret;
    }


    private enum TaskTypeEnum {
        ONCE(1, "永久一次"),
        PERIOD(2, "周期执行"),
        REGULAR(3, "指定时间执行"),
        ;
        private int code;
        private String name;

        TaskTypeEnum(int code, String name) {
            this.code = code;
            this.name = name;
        }

        public int getCode() {
            return code;
        }

        public String getName() {
            return name;
        }

    }
}
