package com.lixy.quartz.dao;

import com.lixy.quartz.entity.HandlerStatusTask;
import com.lixy.quartz.vo.TaskStatusSearchVo;
import com.lixy.quartz.vo.TaskStatusShowVo;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HandlerStatusTaskMapper {

    int deleteByPrimaryKey(Integer handlerId);

    int insert(HandlerStatusTask record);

    int insertSelective(HandlerStatusTask record);

    HandlerStatusTask selectByPrimaryKey(Integer handlerId);

    int updateByPrimaryKey(HandlerStatusTask record);

    List<TaskStatusShowVo> findTaskStatusPage(TaskStatusSearchVo record);
}