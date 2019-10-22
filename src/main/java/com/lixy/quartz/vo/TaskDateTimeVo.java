package com.lixy.quartz.vo;

import java.io.Serializable;

/**
 * 绑定任务时间
 */
public class TaskDateTimeVo implements Serializable {

    private Integer hour;

    private Integer minute;

    private Integer second;

    public Integer getHour() {
        return hour;
    }

    public void setHour(Integer hour) {
        this.hour = hour;
    }

    public Integer getMinute() {
        return minute;
    }

    public void setMinute(Integer minute) {
        this.minute = minute;
    }

    public Integer getSecond() {
        return second;
    }

    public void setSecond(Integer second) {
        this.second = second;
    }
}
