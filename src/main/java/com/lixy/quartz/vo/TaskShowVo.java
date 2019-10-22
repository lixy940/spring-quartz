package com.lixy.quartz.vo;

import java.util.Date;

/**
 * Author：MR LIS，2019/10/21
 * Copyright(C) 2019 All rights reserved.
 */
public class TaskShowVo {
    /**
     * 表名
     */
    private String tableName;

    /**
     * 数据库连接名
     */
    private String dbName;

    /**
     * 任务状态
     */
    private int taskStatus;

    /**
     * 任务类型
     */
    private int taskType;


    /**
     * 最新处理时间
     */
    private Date lastHandleTime;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public int getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(int taskStatus) {
        this.taskStatus = taskStatus;
    }

    public int getTaskType() {
        return taskType;
    }

    public void setTaskType(int taskType) {
        this.taskType = taskType;
    }

    public Date getLastHandleTime() {
        return lastHandleTime;
    }

    public void setLastHandleTime(Date lastHandleTime) {
        this.lastHandleTime = lastHandleTime;
    }
}
