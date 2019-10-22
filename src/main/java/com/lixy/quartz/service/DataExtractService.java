package com.lixy.quartz.service;

import com.lixy.quartz.entity.CommitTableRecord;
import com.lixy.quartz.vo.*;
import com.lixy.quartz.vo.page.SpringPageVo;

/**
 * Author：MR LIS，2019/10/21
 * Copyright(C) 2019 All rights reserved.
 */
public interface DataExtractService {

    /**
     * 已提交表分页列表
     * @param record
     * @return
     */
    SpringPageVo<CommitTableRecord> findCommitTablePage(CommitTableSearchVo record);

    /**
     * 根据数据库连接id获取所有表
     * @param searchVo
     * @return
     */
    SourceTablePageVo getTableListByDbId(SourceTableSearchVo searchVo);

    /**
     * 保存提交列表
     * @param record
     */
    void saveCommitTableRecord(CommitTableRecord record);

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
     * 任务状态列表
     * @param record
     * @return
     */
    SpringPageVo<TaskStatusShowVo> findTaskStatusPage(TaskStatusSearchVo record);


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
