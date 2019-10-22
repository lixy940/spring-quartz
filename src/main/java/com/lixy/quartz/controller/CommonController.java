package com.lixy.quartz.controller;

import com.lixy.quartz.dao.SysDBInfoMapper;
import com.lixy.quartz.entity.SysDBInfo;
import com.lixy.quartz.enums.DBTypeEnum;
import com.lixy.quartz.service.CommonService;
import com.lixy.quartz.vo.ColumnInfoVO;
import com.lixy.quartz.vo.ConditionCountVo;
import com.lixy.quartz.vo.ConditionPageVo;
import com.lixy.quartz.vo.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: MR LIS
 * @Description:
 * @Date: Create in 16:40 2018/5/25
 * @Modified By:
 */
@Api(tags = {"数据库访问公共接口"})
@RestController
@RequestMapping("/common")
public class CommonController {

    private final static Logger logger = LoggerFactory.getLogger(CommonController.class);

    @Autowired
    private CommonService commonService;

    @Autowired
    private SysDBInfoMapper sysDBInfoMapper;




    /**
     * 获取表预览信息,表头和预览的数据量
     *
     * @return
     */
    @ApiOperation(value = "获取表预览信息", notes = "获取表预览信息", response = ResponseResult.class)
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "dbId", dataType = "Integer", required = true, value = "数据库连接id", defaultValue = ""),
            @ApiImplicitParam(paramType = "query", name = "tableName", dataType = "String", required = true, value = "数据库表名", defaultValue = "")
    })
    @PostMapping(value = "/getColumnListBy")
    public ResponseResult getColumnListBy(Integer dbId, String tableName) {
        ResponseResult result = new ResponseResult();

        result.setData(commonService.getColumnListBy(dbId, tableName));

        return result;
    }

    /**
     * @return
     * @Author: MR LIS
     * @Description: 根据数据库id、表名获取表各列的列名、注释及数据类型
     * @Date: 16:46 2018/5/25
     */
    @ApiOperation(value = "获取表所有列信息", notes = "根据数据库id、表名获取表各列的列名、注释及数据类型", consumes = "application/json", response = ResponseResult.class)
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", name = "dbId", dataType = "Integer", required = true, value = "数据库配置id", defaultValue = ""),
            @ApiImplicitParam(paramType = "path", name = "tableName", dataType = "String", required = true, value = "数据库表名", defaultValue = "")
    })
    @GetMapping("getAllColumnInfo/{dbId}/{tableName}")
    public ResponseResult getAllColumnInfo(@PathVariable("dbId") Integer dbId, @PathVariable("tableName") String tableName) {
        ResponseResult responseResult = new ResponseResult();
        List<ColumnInfoVO> allColumnInfo = commonService.getAllColumnInfo(dbId, tableName);
        responseResult.setData(allColumnInfo);

        return responseResult;
    }


    /**
     * @return
     * @Author: MR LIS
     * @Description: 获取分页列表信息
     * @Date: 16:46 2018/5/25
     */
    @ApiOperation(value = "获取分页列表总记录数", notes = "获取分页列表总记录数", consumes = "application/json", response = ResponseResult.class)
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", name = "dbId", dataType = "Integer", required = true, value = "数据库id", defaultValue = ""),
            @ApiImplicitParam(paramType = "path", name = "tableName", dataType = "String", required = true, value = "数据库表名", defaultValue = "")
    })
    @GetMapping("executePageTotalCount/{dbId}/{tableName}")
    public ResponseResult executePageTotalCount(@PathVariable("dbId") Integer dbId, @PathVariable("tableName") String tableName) {
        ResponseResult responseResult = new ResponseResult();
        int totalCount = commonService.executePageTotalCount(dbId, tableName);
        responseResult.setData(totalCount);

        return responseResult;
    }

    /**
     * @return
     * @Author: MR LIS
     * @Description: 获取分页列表信息, 不进行总记录数查询
     * @Date: 16:46 2018/5/25
     */
    @ApiOperation(value = "获取不含总记录数的分页列表", notes = "获取分页列表信息,不返回总记录数", consumes = "application/json", response = ResponseResult.class)
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", name = "dbId", dataType = "Integer", required = true, value = "数据库id", defaultValue = ""),
            @ApiImplicitParam(paramType = "path", name = "tableName", dataType = "String", required = true, value = "数据库表名", defaultValue = ""),
            @ApiImplicitParam(paramType = "path", name = "pageNum", dataType = "Integer", required = true, value = "当前第几页", defaultValue = ""),
            @ApiImplicitParam(paramType = "path", name = "pageSize", dataType = "Integer", required = true, value = "每页记录数", defaultValue = "")
    })
    @GetMapping("executePageNotCount/{dbId}/{tableName}/{pageNum}/{pageSize}")
    public ResponseResult executePageNotCount(@PathVariable("dbId") Integer dbId, @PathVariable("tableName") String tableName,
                                              @PathVariable("pageNum") Integer pageNum, @PathVariable("pageSize") Integer pageSize) {
        ResponseResult responseResult = new ResponseResult();

        /**
         * 本地数据源
         */
        List<ColumnInfoVO> allColumnInfo = commonService.getAllColumnInfo(dbId, tableName);
        SysDBInfo sysDBInfo = sysDBInfoMapper.selectByPrimaryKey(dbId);
        String columnArr = null;
        if (DBTypeEnum.DB_MYSQL.getDbName().equalsIgnoreCase(sysDBInfo.getDbType()) || DBTypeEnum.DB_TIDB.getDbName().equals(sysDBInfo.getDbType())) {
            List<String> collect = allColumnInfo.stream().map(o -> "`" + o.getColumnEname() + "`").collect(Collectors.toList());
            columnArr = StringUtils.join(collect, ",");
        } else if (DBTypeEnum.DB_POSTGRESQL.getDbName().equalsIgnoreCase(sysDBInfo.getDbType())) {
            List<String> collect = allColumnInfo.stream().map(o -> "\"" + o.getColumnEname() + "\"").collect(Collectors.toList());
            columnArr = StringUtils.join(collect, ",");
        } else {
            List<String> collect = allColumnInfo.stream().map(o -> o.getColumnEname()).collect(Collectors.toList());
            columnArr = StringUtils.join(collect, ",");
        }


        List<List<Object>> dataList = commonService.executePageQueryColumnRecord(dbId, tableName, columnArr, pageNum, pageSize);
        responseResult.setData(dataList);


        return responseResult;
    }

    /**
     * @return
     * @Author: MR LIS
     * @Description: 获取分页列表信息, 不进行总记录数查询+排序
     * @Date: 16:46 2018/5/25
     */
    @ApiOperation(value = "获取不含总记录数的分页列表+排序", notes = "获取分页列表信息,不返回总记录数", consumes = "application/json", response = ResponseResult.class)
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", name = "dbId", dataType = "Integer", required = true, value = "数据库id", defaultValue = ""),
            @ApiImplicitParam(paramType = "path", name = "tableName", dataType = "String", required = true, value = "数据库表名", defaultValue = ""),
            @ApiImplicitParam(paramType = "path", name = "pageNum", dataType = "Integer", required = true, value = "当前第几页", defaultValue = ""),
            @ApiImplicitParam(paramType = "path", name = "pageSize", dataType = "Integer", required = true, value = "每页记录数", defaultValue = ""),
            @ApiImplicitParam(paramType = "query", name = "sortField", dataType = "String", required = false, value = "排序列名", defaultValue = ""),
            @ApiImplicitParam(paramType = "query", name = "descType", dataType = "String", required = false, value = "排序类型：ASC 正序 ,DESC 倒序", defaultValue = ""),
    })
    @GetMapping("executePageSortNotCount/{dbId}/{tableName}/{pageNum}/{pageSize}")
    public ResponseResult executePageSortNotCount(@PathVariable("dbId") Integer dbId, @PathVariable("tableName") String tableName,
                                                  @PathVariable("pageNum") Integer pageNum, @PathVariable("pageSize") Integer pageSize,
                                                  String sortField, String descType) {
        ResponseResult responseResult = new ResponseResult();

        /**
         * 本地数据源
         */
        List<ColumnInfoVO> allColumnInfo = commonService.getAllColumnInfo(dbId, tableName);
        SysDBInfo sysDBInfo = sysDBInfoMapper.selectByPrimaryKey(dbId);
        String columnArr = null;
        if (DBTypeEnum.DB_MYSQL.getDbName().equalsIgnoreCase(sysDBInfo.getDbType()) || DBTypeEnum.DB_TIDB.getDbName().equals(sysDBInfo.getDbType())) {
            List<String> collect = allColumnInfo.stream().map(o -> "`" + o.getColumnEname() + "`").collect(Collectors.toList());
            columnArr = StringUtils.join(collect, ",");
            //如果不为空，加上符号
            if (StringUtils.isNotBlank(sortField)) {
                sortField = "`" + sortField + "`";
            }
        } else if (DBTypeEnum.DB_POSTGRESQL.getDbName().equalsIgnoreCase(sysDBInfo.getDbType())) {
            List<String> collect = allColumnInfo.stream().map(o -> "\"" + o.getColumnEname() + "\"").collect(Collectors.toList());
            columnArr = StringUtils.join(collect, ",");
        } else {
            List<String> collect = allColumnInfo.stream().map(o -> o.getColumnEname()).collect(Collectors.toList());
            columnArr = StringUtils.join(collect, ",");
        }

        List<List<Object>> dataList = commonService.executePageQueryColumnSortRecord(dbId, tableName, columnArr, pageNum, pageSize, sortField, descType);
        responseResult.setData(dataList);


        return responseResult;
    }

    /**
     * @return
     * @Author: MR LIS
     * @Description: 获取分页列表信息
     * @Date: 16:46 2018/5/25
     */
    @ApiOperation(value = "获取带条件分页列表总记录数", notes = "获取带条件分页列表总记录数", response = ResponseResult.class)
    @PostMapping("/executePageTotalCountWithCondition")
    public ResponseResult executePageTotalCountWithCondition(@RequestBody ConditionCountVo countVo) {
        ResponseResult responseResult = new ResponseResult();

        int totalCount = commonService.executePageTotalCountWithCondition(countVo);
        responseResult.setData(totalCount);

        return responseResult;
    }

    /**
     * @return
     * @Author: MR LIS
     * @Description: 获取分页列表信息, 不进行总记录数查询
     * @Date: 16:46 2018/5/25
     */
    @ApiOperation(value = "获取不含总记录数的带条件分页列表", notes = "获取带条件分页列表信息,不返回总记录数", response = ResponseResult.class)
    @PostMapping("/executePageNotCountWithCondition")
    public ResponseResult executePageNotCountWithCondition(@RequestBody ConditionPageVo pageVo) {
        ResponseResult responseResult = new ResponseResult();

        List<ColumnInfoVO> allColumnInfo = commonService.getAllColumnInfo(pageVo.getDbId(), pageVo.getTableName());
        SysDBInfo sysDBInfo = sysDBInfoMapper.selectByPrimaryKey(pageVo.getDbId());
        String columnArr = null;
        if (DBTypeEnum.DB_MYSQL.getDbName().equalsIgnoreCase(sysDBInfo.getDbType()) || DBTypeEnum.DB_TIDB.getDbName().equals(sysDBInfo.getDbType())) {
            List<String> collect = allColumnInfo.stream().map(o -> "`" + o.getColumnEname() + "`").collect(Collectors.toList());
            columnArr = StringUtils.join(collect, ",");
        } else if (DBTypeEnum.DB_POSTGRESQL.getDbName().equalsIgnoreCase(sysDBInfo.getDbType())) {
            List<String> collect = allColumnInfo.stream().map(o -> "\"" + o.getColumnEname() + "\"").collect(Collectors.toList());
            columnArr = StringUtils.join(collect, ",");
        } else {
            List<String> collect = allColumnInfo.stream().map(o -> o.getColumnEname()).collect(Collectors.toList());
            columnArr = StringUtils.join(collect, ",");
        }
        List<List<Object>> dataList = commonService.executePageQueryNotCountWithCondition(pageVo, columnArr);
        responseResult.setData(dataList);


        return responseResult;
    }


}
