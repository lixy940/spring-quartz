package com.lixy.quartz.dao;

import com.lixy.quartz.entity.HandlerTask;
import com.lixy.quartz.vo.TaskSearchVo;
import com.lixy.quartz.vo.TaskShowVo;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Author：MR LIS，2019/10/21
 * Copyright(C) 2019 All rights reserved.
 */
@Repository
public interface HandlerTaskMapper {

    int deleteByPrimaryKey(Integer taskId);

    int insert(HandlerTask record);

    int insertSelective(HandlerTask record);

    HandlerTask selectByPrimaryKey(Integer taskId);

    int updateByPrimaryKeySelective(HandlerTask record);


    List<TaskShowVo> findTaskPage(TaskSearchVo searchVo);
}
