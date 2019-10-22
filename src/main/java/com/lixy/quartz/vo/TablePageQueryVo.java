package com.lixy.quartz.vo;

/**
 * @author LIS
 * @date 2018/12/2116:37
 */
public class TablePageQueryVo {

    /**
     * 数据连接id
     */
    private Integer dbId;
    /**
     * 表名
     */
    private String tableName;
    /**
     * 第几页
     */
    private Integer pageNo;
    /**
     * 每页记录是
     */
    private Integer pageSize;

    public Integer getDbId() {
        return dbId;
    }

    public void setDbId(Integer dbId) {
        this.dbId = dbId;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
