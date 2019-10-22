package com.lixy.quartz.vo;

import java.util.List;

/**
 * @author LIS
 * @date 2018/12/2416:58
 */
public class SourceTablePageVo {

    /**
     * 表信息集合对象
     */
    List<SourceDataInfoVO> dataInfoVOS;

    /**
     * 表总个数，用于分页
     */
    private int totalCount;

    public List<SourceDataInfoVO> getDataInfoVOS() {
        return dataInfoVOS;
    }

    public void setDataInfoVOS(List<SourceDataInfoVO> dataInfoVOS) {
        this.dataInfoVOS = dataInfoVOS;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}
