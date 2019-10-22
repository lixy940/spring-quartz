package com.lixy.quartz.controller;

import com.lixy.quartz.dao.SysDBInfoMapper;
import com.lixy.quartz.entity.CommitTableRecord;
import com.lixy.quartz.entity.SysDBInfo;
import com.lixy.quartz.service.DataExtractService;
import com.lixy.quartz.vo.*;
import com.lixy.quartz.vo.page.SpringPageVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Author：MR LIS，2019/10/21
 * Copyright(C) 2019 All rights reserved.
 */
@Api(tags = {"数据库连接配置"})
@RestController
@RequestMapping("/dataextract")
public class DataExtractController {

    @Autowired
    private DataExtractService dataExtractService;


    @Autowired
    private SysDBInfoMapper sysDBInfoMapper;

    /**
     * 保存数据库连接
     *
     * @return
     * @Author: MR LIS
     * @Date: 16:24 2018/7/30
     */
    @ApiOperation(value = "保存数据库连接", notes = "保存数据库连接")
    @PostMapping(value = "/saveDbInfo")
    public long saveDbInfo(SysDBInfo sysDBInfo) {
        sysDBInfo.setStatus(1);
        return sysDBInfoMapper.insert(sysDBInfo);
    }


    /**
     * 获取所有的数据库连接
     *
     * @return
     * @Author: MR LIS
     * @Date: 16:24 2018/7/30
     */
    @ApiOperation(value = "获取所有的数据库连接", notes = "获取所有的数据库连接")
    @PostMapping(value = "/getSysDbInfoList")
    public ResponseResult getSysDbInfoList() {
        ResponseResult result = new ResponseResult();
        List<SysDBInfo> sysDBInfos = sysDBInfoMapper.selectAll();
        result.setData(sysDBInfos);
        return result;
    }


    @ApiOperation(value = "根据数据库连接id获取所有表", notes = "获取所有的数据库连接")
    @PostMapping(value = "/getTableListByDbId")
    public ResponseResult getTableListByDbId(@RequestBody SourceTableSearchVo record) {
        ResponseResult result = new ResponseResult();
        SourceTablePageVo data = dataExtractService.getTableListByDbId(record);
        result.setData(data);
        return result;
    }


    @ApiOperation(value = "保存提交表记录", notes = "保存提交表记录")
    @PostMapping("/saveCommitTableRecord")
    public ResponseResult saveCommitTableRecord(@RequestBody CommitTableRecord record) {
        ResponseResult result = new ResponseResult();
        dataExtractService.saveCommitTableRecord(record);
        return result;
    }


    /**
     * 已提交表分页列表
     *
     * @param record
     * @return
     */
    @ApiOperation(value = "已提交表分页列表", notes = "已提交表分页列表")
    @PostMapping("findCommitTablePage")
    public ResponseResult findCommitTablePage(@RequestBody CommitTableSearchVo record) {
        ResponseResult result = new ResponseResult();
        SpringPageVo<CommitTableRecord> pageVo = dataExtractService.findCommitTablePage(record);
        result.setData(pageVo);
        return result;
    }


    /**
     * 批量添加已提交任务列表
     *
     * @return
     */
    @ApiOperation(value = "添加任务", notes = "添加任务")
    @PostMapping("batchAddHandlerTask")
    public ResponseResult batchAddHandlerTask(HandlerTaskVo[] taskVos) {
        ResponseResult result = new ResponseResult();
        dataExtractService.batchAddHandlerTask(taskVos);
        return result;
    }


    @ApiOperation(value = "启动任务", notes = "启动任务")
    @PostMapping("startTasks")
    public ResponseResult startTasks(Integer[] taskIds) {
        ResponseResult result = new ResponseResult();
        dataExtractService.startTasks(taskIds);
        return result;
    }



    @ApiOperation(value = "任务列表", notes = "任务列表")
    @PostMapping("findTaskPage")
    public ResponseResult findTaskPage(@RequestBody TaskSearchVo record) {
        ResponseResult result = new ResponseResult();
        SpringPageVo<TaskShowVo> pageVo = dataExtractService.findTaskPage(record);
        result.setData(pageVo);
        return result;
    }


    @ApiOperation(value = "任务状态列表", notes = "任务状态列表")
    @PostMapping("findTaskStatusPage")
    public ResponseResult findTaskStatusPage(@RequestBody TaskStatusSearchVo record) {
        ResponseResult result = new ResponseResult();
        SpringPageVo<TaskStatusShowVo> pageVo = dataExtractService.findTaskStatusPage(record);
        result.setData(pageVo);
        return result;
    }

}
