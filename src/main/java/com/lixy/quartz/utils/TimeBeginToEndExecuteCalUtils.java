package com.lixy.quartz.utils;

/**
 * @Author: MR LIS
 * @Description:时间差计算工具
 * @Date: Create in 14:14 2018/6/29
 * @Modified By:
 */
public class TimeBeginToEndExecuteCalUtils {

    private final static ThreadLocal<Long> TIME_THREADLOCAL = new ThreadLocal<Long>(){
        @Override
        protected Long initialValue() {
            return System.currentTimeMillis();
        }
    };

    public static final void begin(){
        TIME_THREADLOCAL.set(System.currentTimeMillis());

    }


    public static final long end(){
        return System.currentTimeMillis()-TIME_THREADLOCAL.get();
    }

}
