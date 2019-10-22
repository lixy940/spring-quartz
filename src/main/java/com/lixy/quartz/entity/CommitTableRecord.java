package com.lixy.quartz.entity;

import java.util.Date;

/**
 * Author：MR LIS，2019/10/21
 * Copyright(C) 2019 All rights reserved.
 */
public class CommitTableRecord {

    private int commitId;

    private int dbId;

    private String tableName;

    private String path;

    private String etlCol;

    private int dataType;

    private Date createTime;


    public int getCommitId() {
        return commitId;
    }

    public void setCommitId(int commitId) {
        this.commitId = commitId;
    }

    public int getDbId() {
        return dbId;
    }

    public void setDbId(int dbId) {
        this.dbId = dbId;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getEtlCol() {
        return etlCol;
    }

    public void setEtlCol(String etlCol) {
        this.etlCol = etlCol;
    }

    public int getDataType() {
        return dataType;
    }

    public void setDataType(int dataType) {
        this.dataType = dataType;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
