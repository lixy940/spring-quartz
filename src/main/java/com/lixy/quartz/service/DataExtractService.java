package com.lixy.quartz.service;

import com.lixy.quartz.vo.HandlerTaskVo;
import com.lixy.quartz.vo.TaskSearchVo;
import com.lixy.quartz.vo.TaskShowVo;
import com.lixy.quartz.vo.page.SpringPageVo;

/**
 * Author：MR LIS，2019/10/21
 * Copyright(C) 2019 All rights reserved.
 */
public interface DataExtractService {




    /**
     * 添加任务
     */
    void batchAddHandlerTask(HandlerTaskVo[] taskVos);

    /**
     * 任务列表
     * @param record
     * @return
     */
    SpringPageVo<TaskShowVo> findTaskPage(TaskSearchVo record);


    /**
     * 启动任务
     * @param taskIds
     */
    void startTasks(Integer[] taskIds);
    /**
     * 开启任务
     * @param taskId
     */
    void executeTask(Integer taskId);
}
