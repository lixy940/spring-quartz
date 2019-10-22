package com.lixy.quartz.vo;

/**
 * Author：MR LIS，2019/10/21
 * Copyright(C) 2019 All rights reserved.
 */
public class CommitTableSearchVo extends PageVo{
    private int dbId;

    private String tableName;

    private String etlCol;

    private int dataType;

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
}
