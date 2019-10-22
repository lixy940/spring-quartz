package com.lixy.quartz.vo;

import java.io.Serializable;
import java.util.Optional;

/**
 * Author：MR LIS，2019/10/21
 * Copyright(C) 2019 All rights reserved.
 */
public class PageVo implements Serializable {


    private Integer pageSize;

    private Integer pageNo;

    public Integer getPageSize() {
        return Optional.ofNullable(this.pageSize).orElse(10);
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getPageNo() {
        return Optional.ofNullable(this.pageNo).orElse(1);
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }
}
