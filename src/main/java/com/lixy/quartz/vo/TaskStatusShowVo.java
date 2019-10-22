package com.lixy.quartz.vo;

/**
 * Author：MR LIS，2019/10/21
 * Copyright(C) 2019 All rights reserved.
 */
public class TaskStatusShowVo {

    /**
     * 表名
     */
    private String tableName;
    /**
     * 任务处理状态
     */
    private int status;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
