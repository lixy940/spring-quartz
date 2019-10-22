package com.lixy.quartz.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lixy.quartz.dao.CommitTableRecordMapper;
import com.lixy.quartz.dao.HandlerStatusTaskMapper;
import com.lixy.quartz.dao.HandlerTaskMapper;
import com.lixy.quartz.dao.SysDBInfoMapper;
import com.lixy.quartz.entity.CommitTableRecord;
import com.lixy.quartz.entity.HandlerStatusTask;
import com.lixy.quartz.entity.HandlerTask;
import com.lixy.quartz.entity.SysDBInfo;
import com.lixy.quartz.quartz.JobConstant;
import com.lixy.quartz.quartz.QuartzUtils;
import com.lixy.quartz.quartz.job.MissionJobImpl;
import com.lixy.quartz.service.DataExtractService;
import com.lixy.quartz.utils.GenDBUtils;
import com.lixy.quartz.vo.*;
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
import java.util.stream.Collectors;

/**
 * Author：MR LIS，2019/10/21
 * Copyright(C) 2019 All rights reserved.
 */
@Service
public class DataExtractServiceImpl implements DataExtractService {

    @Autowired
    private CommitTableRecordMapper commitTableRecordMapper;

    @Autowired
    private SysDBInfoMapper sysDBInfoMapper;

    @Autowired
    private HandlerTaskMapper handlerTaskMapper;

    @Autowired
    private HandlerStatusTaskMapper handlerStatusTaskMapper;

    @Autowired
    private QuartzUtils quartzUtils;

    @Override
    public SpringPageVo<CommitTableRecord> findCommitTablePage(CommitTableSearchVo record) {
        PageHelper.startPage(record.getPageNo(), record.getPageSize());
        List<CommitTableRecord> records = commitTableRecordMapper.findPage();
        PageInfo<CommitTableRecord> pageInfos = new PageInfo<>(records);
        return ListToSpringPageVo.listToPage(record.getPageNo(), record.getPageSize(), pageInfos.getTotal(), pageInfos.getList());
    }

    @Override
    public SourceTablePageVo getTableListByDbId(SourceTableSearchVo pageQueryVo) {

        SourceTablePageVo tablePageVo = new SourceTablePageVo();
        SysDBInfo config = sysDBInfoMapper.selectByPrimaryKey(pageQueryVo.getDbId());
        List<SourceDataInfoShowVO> dbTableInfos = GenDBUtils.getDbTableInfos(config);
        List<SourceDataInfoVO> dataInfoVOS;
        //如果表名不为空
        if (StringUtils.isNotBlank(pageQueryVo.getTableName())) {
            dataInfoVOS = dbTableInfos.stream().filter(d -> d.getSourceDataInfoVO().getTableEname().indexOf(pageQueryVo.getTableName()) != -1).map(v -> {
                int count = commitTableRecordMapper.findCoutByDbIdAndTableName(pageQueryVo.getDbId(), pageQueryVo.getTableName());
                SourceDataInfoVO sourceDataInfoVO = v.getSourceDataInfoVO();
                sourceDataInfoVO.setIsCommit(count > 0 ? 1 : 0);
                return sourceDataInfoVO;
            }).collect(Collectors.toList());
        } else {
            dataInfoVOS = dbTableInfos.stream().map(v -> {
                int count = commitTableRecordMapper.findCoutByDbIdAndTableName(pageQueryVo.getDbId(), pageQueryVo.getTableName());
                SourceDataInfoVO sourceDataInfoVO = v.getSourceDataInfoVO();
                sourceDataInfoVO.setIsCommit(count > 0 ? 1 : 0);
                return sourceDataInfoVO;
            }).collect(Collectors.toList());
        }
        int total = dataInfoVOS.size();
        int start = (pageQueryVo.getPageNo() - 1) * pageQueryVo.getPageSize();
        int end = pageQueryVo.getPageNo() * pageQueryVo.getPageSize() >= total ? total : pageQueryVo.getPageNo() * pageQueryVo.getPageSize();
        List<SourceDataInfoVO> newDataList = dataInfoVOS.subList(start, end);
        tablePageVo.setTotalCount(total);
        tablePageVo.setDataInfoVOS(newDataList);
        return tablePageVo;
    }

    @Override
    public void saveCommitTableRecord(CommitTableRecord record) {
        //todo 调用linux,返回脚本

        //返回脚本
        record.setPath("");
        commitTableRecordMapper.insert(record);
    }

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
    public SpringPageVo<TaskStatusShowVo> findTaskStatusPage(TaskStatusSearchVo record) {
        PageHelper.startPage(record.getPageNo(), record.getPageSize());
        List<TaskStatusShowVo> taskPage = handlerStatusTaskMapper.findTaskStatusPage(record);
        PageInfo<TaskStatusShowVo> pageInfos = new PageInfo<>(taskPage);
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

        //插入导入状态
        HandlerStatusTask statusTask = new HandlerStatusTask();
        statusTask.setTaskId(taskId);
        handlerStatusTaskMapper.insert(statusTask);
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
