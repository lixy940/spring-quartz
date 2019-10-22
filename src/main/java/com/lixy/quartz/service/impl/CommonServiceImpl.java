package com.lixy.quartz.service.impl;

import com.lixy.quartz.dao.SysDBInfoMapper;
import com.lixy.quartz.entity.SysDBInfo;
import com.lixy.quartz.enums.DBTypeEnum;
import com.lixy.quartz.enums.DriverNameEnum;
import com.lixy.quartz.service.CommonService;
import com.lixy.quartz.utils.GenDBUtils;
import com.lixy.quartz.utils.TimeBeginToEndExecuteCalUtils;
import com.lixy.quartz.vo.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.*;
import java.util.*;

/**
 * @Author: MR LIS
 * @Description:沙盘公共服务接口实现
 * @Date: Create in 14:46 2018/5/25
 * @Modified By:
 */
@Transactional
@Service
public class CommonServiceImpl implements CommonService {

    private Logger logger = LoggerFactory.getLogger(CommonServiceImpl.class);

    /**
     * MYSQL前缀
     */
    private static String MYSQL_PREFIX = "jdbc:mysql://";
    /**
     * MYSQL后缀
     */
    private static String MYSQL_SUFFIX = "?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai";
    /**
     * ORACLE前缀
     */
    private static String ORACLE_PREFIX = "jdbc:oracle:thin:@//";
    /**
     * postgres前缀
     */
    private static String POSTGRES_PREFIX = "jdbc:postgresql://";

    @Autowired
    private SysDBInfoMapper configMapper;

    @Override
    public List<ColumnInfoVO> getAllColumnInfo(Integer dbId, String tableName) {
        SysDBInfo sysDBInfo = configMapper.selectByPrimaryKey(dbId);
        //表头
        List<ColumnInfoVO> columnInfoVOList = GenDBUtils.getAllColumnInfo(sysDBInfo, tableName);
        return columnInfoVOList;
    }

    @Override
    public int executePageTotalCount(Integer dbId, String tableName) {
        SysDBInfo sysDBInfo = configMapper.selectByPrimaryKey(dbId);
        //表总记录数
        int totalCount = GenDBUtils.executePageTotalCount(sysDBInfo, tableName);
        logger.info("dbId={},tableName={},总记录数 = {}", dbId, tableName, totalCount);
        return totalCount;
    }

    @Override
    public List<List<Object>> executePageQueryNotCount(Integer dbId, String tableName, Integer pageNum, Integer pageSize) {
        int start = (pageNum - 1) * pageSize;
        int end = pageSize * pageNum;
        SysDBInfo sysDBInfo = configMapper.selectByPrimaryKey(dbId);
        return GenDBUtils.executePage(sysDBInfo, tableName, pageSize, start, end);
    }

    @Override
    public int executePageTotalCountWithCondition(ConditionCountVo countVo) {
        SysDBInfo sysDBInfo = configMapper.selectByPrimaryKey(countVo.getDbId());
        //表总记录数
        int totalCount = GenDBUtils.executePageTotalCountWithCondition(sysDBInfo, countVo.getTableName(), countVo.getConditionVos());
//        logger.info("dbId={},tableName={},总记录数 = {}", countVo.getDbId(), countVo.getTableName(), totalCount);
        return totalCount;
    }

    @Override
    public List<List<Object>> executePageQueryNotCountWithCondition(ConditionPageVo pageVo, String columnArr) {
        int start = (pageVo.getPageNum() - 1) * pageVo.getPageSize();
        int end = pageVo.getPageSize() * pageVo.getPageNum();
        SysDBInfo sysDBInfo = configMapper.selectByPrimaryKey(pageVo.getDbId());
        return GenDBUtils.executePageWithCondition(sysDBInfo, pageVo.getTableName(), columnArr, pageVo.getConditionVos(), pageVo.getPageSize(), start, end);
    }

    @Override
    public List<List<Object>> executePageQueryColumnRecord(Integer dbId, String tableName, String columnArr, Integer pageNum, Integer pageSize) {
        int start = (pageNum - 1) * pageSize;
        int end = pageSize * pageNum;
        SysDBInfo sysDBInfo = configMapper.selectByPrimaryKey(dbId);
        return GenDBUtils.executePage(sysDBInfo, tableName, columnArr, pageSize, start, end);
    }

    @Override
    public List<List<Object>> executePageQueryColumnSortRecord(Integer dbId, String tableName, String columnArr, Integer pageNum, Integer pageSize, String sortField, String descType) {
        int start = (pageNum - 1) * pageSize;
        int end = pageSize * pageNum;
        SysDBInfo sysDBInfo = configMapper.selectByPrimaryKey(dbId);
        if (StringUtils.isBlank(sortField)) {
            return GenDBUtils.executePageSort(sysDBInfo, tableName, columnArr, pageSize, start, end, sortField, descType);
        }
        /**
         * 针对数据结果，数据结果是mysql库
         */
        if (DBTypeEnum.DB_MYSQL.getDbName().equals(sysDBInfo.getDbType()) || DBTypeEnum.DB_TIDB.getDbName().equals(sysDBInfo.getDbType())) {
            String temp = "select * from " + tableName + " where (" + sortField + " REGEXP '[^0-9.-]')=1 ";
            List<List<Object>> lists = GenDBUtils.executePageRecord(sysDBInfo, temp);
            logger.info(" 查询是否全是数字的 sql是: {}", temp);
            if (lists == null || lists.size() < 1) {
                return GenDBUtils.executePageSortByNumber(sysDBInfo, tableName, columnArr, pageSize, start, end, sortField, descType);
            }
        }

        return GenDBUtils.executePageSort(sysDBInfo, tableName, columnArr, pageSize, start, end, sortField, descType);

    }

    @Override
    public synchronized void dropTable(Integer dbId, String tableName) {
        Integer tableNum = getIsTableExistCount(dbId, tableName);
        if (tableNum > 0) {
            SysDBInfo sysDBInfo = configMapper.selectByPrimaryKey(dbId);
            GenDBUtils.dropTable(sysDBInfo, tableName);
        }
    }

    @Override
    public int getIsTableExistCount(Integer dbId, String tableName) {
        SysDBInfo sysDBInfo = configMapper.selectByPrimaryKey(dbId);
        //表个数
        int totalCount = GenDBUtils.getIsTableExistCount(sysDBInfo, tableName);
        logger.info("dbId={},tableName={},总记录数 = {}", dbId, tableName, totalCount);
        return totalCount;
    }


    /**
     * 批量插入数据到数据看
     *
     * @param sql         插入sql
     * @param columnNum   总共插入的列数量
     * @param list        数据集
     * @param batchOptNum 每次批量提交的数据
     * @throws
     */
    @Override
    public void batchInsert(DbConnInfo dbInfo, String sql, Integer columnNum, List<Map<Integer, Object>> list, Integer batchOptNum) {
        logger.info("{}正在执行插入，请稍后............", Thread.currentThread().getName());
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            Class.forName(dbInfo.getDbDriver());
            conn = DriverManager.getConnection(dbInfo.getUrl(), dbInfo.getUserName(), dbInfo.getPassWord());

            pstm = conn.prepareStatement(sql);
            TimeBeginToEndExecuteCalUtils.begin();
            Integer allSize = list.size();
            for (int i = 0; i < allSize; i++) {
                Map<Integer, Object> map = list.get(i);
                for (int j = 1; j <= columnNum; j++) {
                    pstm.setObject(j, map.get(j));
                }
                pstm.addBatch();
                //每BATCH_OPT_NUM执行一次批量操作
                if (i > 0 && i % batchOptNum == 0) {
                    pstm.executeBatch();
                }

            }
            pstm.executeBatch();
            logger.info("{}========OK,用时：{}毫秒", Thread.currentThread().getName(), TimeBeginToEndExecuteCalUtils.end());
        } catch (Exception e) {
            logger.error("数据插入异常：{}", e.getMessage(), e);
            throw new RuntimeException("数据插入异常:" + e.getMessage());
        } finally {
            closeConn(conn, pstm);
        }
    }

    @Override
    public void batchInsert(DbConnInfo dbInfo, String insertSql, List<List<Object>> datas, int columnSize, Integer batchNum) {
        logger.info("{}正在执行插入，请稍后............", Thread.currentThread().getName());
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            Class.forName(dbInfo.getDbDriver());
            conn = DriverManager.getConnection(dbInfo.getUrl(), dbInfo.getUserName(), dbInfo.getPassWord());

            pstm = conn.prepareStatement(insertSql);
            TimeBeginToEndExecuteCalUtils.begin();
            Integer allSize = datas.size();
            for (int i = 0; i < allSize; i++) {
                List<Object> data = datas.get(i);
                for (int j = 1; j <= columnSize; j++) {
                    pstm.setObject(j, data.get(j - 1));
                }
                pstm.addBatch();
                //每BATCH_OPT_NUM执行一次批量操作
                if (i > 0 && i % batchNum == 0) {
                    pstm.executeBatch();
                }

            }
            pstm.executeBatch();
            logger.info("{}========OK,用时：{}毫秒", Thread.currentThread().getName(), TimeBeginToEndExecuteCalUtils.end());
        } catch (Exception e) {
            logger.error("数据插入异常：{}", e.getMessage(), e);
            throw new RuntimeException("数据插入异常:" + e.getMessage());
        } finally {
            closeConn(conn, pstm);
        }
    }


    /**
     * 执行更新
     */
    @Override
    public void executeJdbc(DbConnInfo dbInfo, String sql) {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            Class.forName(dbInfo.getDbDriver());
            conn = DriverManager.getConnection(dbInfo.getUrl(), dbInfo.getUserName(), dbInfo.getPassWord());
            pstm = conn.prepareStatement(sql);
            pstm.executeUpdate();
            logger.info("{}========OK,用时：{}毫秒", Thread.currentThread().getName(), TimeBeginToEndExecuteCalUtils.end());
        } catch (Exception e) {
            throw new RuntimeException("更新操作异常:" + e.getMessage());
        } finally {
            closeConn(conn, pstm);
        }
    }


    /**
     * 获取所有数据
     *
     * @param dbInfo
     * @param tableName
     * @return
     */
    @Override
    public List<Map<String, Object>> getAllDatas(DbConnInfo dbInfo, String tableName, List<String> columnListList, Integer limitCount) {
        String columnListStr = StringUtils.join(columnListList, ",");
        String sql = "select " + columnListStr + " from " + tableName;
        if (limitCount != null) {
            sql += " limit " + limitCount;
        }
        List<Map<String, Object>> listMap = new ArrayList<>();
        int colLen = columnListList.size();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            Class.forName(dbInfo.getDbDriver());
            conn = DriverManager.getConnection(dbInfo.getUrl(), dbInfo.getUserName(), dbInfo.getPassWord());
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                Map<String, Object> map = new LinkedHashMap<>();
                for (int i = 0; i < colLen; i++) {
                    String colunmName = columnListList.get(i);
                    map.put(colunmName, rs.getObject(colunmName));
                }
                listMap.add(map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConn(conn, stmt, rs);
        }

        return listMap;
    }

    /**
     * 获取所有列序号对应的value结果集合
     *
     * @param dbInfo
     * @param tableName
     * @return
     */
    @Override
    public List<Map<Integer, Object>> getColumnIndexValueMaps(DbConnInfo dbInfo, String tableName, List<String> columnListList, Integer limitCount) {
        String columnListStr = StringUtils.join(columnListList, ",");
        String sql = "select " + columnListStr + " from " + tableName;
        if (limitCount != null) {
            sql += " limit " + limitCount;
        }
        List<Map<Integer, Object>> listMap = new ArrayList<>();
        int colLen = columnListList.size();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            Class.forName(dbInfo.getDbDriver());
            conn = DriverManager.getConnection(dbInfo.getUrl(), dbInfo.getUserName(), dbInfo.getPassWord());
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                Map<Integer, Object> map = new HashMap<>();
                for (int i = 1; i <= colLen; i++) {
                    map.put(i, rs.getObject(i));
                }
                listMap.add(map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConn(conn, stmt, rs);
        }

        return listMap;
    }

    @Override
    public String assembleInsertSql(List<String> columnList, String tableName) {
        StringBuilder sql = new StringBuilder("INSERT INTO ");
        sql.append(tableName).append(" (");
        StringBuilder valueS = new StringBuilder(" VALUES (");
        int dLen = columnList.size();
        for (int i = 0; i < dLen; i++) {
            sql.append(columnList.get(i));
            valueS.append("?");
            if (i == dLen - 1) {
                sql.append(")");
                valueS.append(")");
            } else {
                sql.append(",");
                valueS.append(",");
            }
        }
        sql.append(valueS);

        return sql.toString();
    }


    /**
     * 关闭连接
     *
     * @param conn
     * @param pstm
     */
    @Override
    public void closeConn(Connection conn, PreparedStatement pstm) {
        if (pstm != null) {
            try {
                pstm.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 关闭连接
     *
     * @param stmt
     * @param rs
     */
    @Override
    public void closeConn(Connection conn, PreparedStatement stmt, ResultSet rs) {
        try {
            if (stmt != null) {
                stmt.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * @return
     * @Author: MR LIS
     * @Description: 执行批量插入操作, 可以分批对其处理
     * @Date: 15:43 2018/4/2
     */
    @Override
    public void createDbDDL(DbConnInfo dbConnInfo, String sql) {

        logger.info("正在创建数据库表，请稍后............");
        Connection conn = null;
        PreparedStatement pst = null;
        try {
            Class.forName(dbConnInfo.getDbDriver());
            conn = DriverManager.getConnection(dbConnInfo.getUrl(), dbConnInfo.getUserName(), dbConnInfo.getPassWord());
            pst = conn.prepareStatement(sql);
            pst.execute();
            logger.info("数据库表创建完成............");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            if (pst != null) {
                try {
                    pst.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
        }
    }


    /**
     * 根据数据库连接属性，拼接数据库连接信息
     *
     * @param dbId 数据库连接id
     * @return
     * @Author: MR LIS
     * @Date: 16:58 2018/7/18
     */
    @Override
    public DbConnInfo setDbConnInfo(Integer dbId) {
        SysDBInfo sysDBInfo = configMapper.selectByPrimaryKey(dbId);
        return setDbConnInfo(sysDBInfo);
    }

    @Override
    public DbConnInfo setDbConnInfo(SysDBInfo sysDBInfo) {
        DbConnInfo dbInfo = new DbConnInfo(sysDBInfo.getDbUser(), sysDBInfo.getDbPassword());

        if (DBTypeEnum.DB_MYSQL.getDbName().equals(sysDBInfo.getDbType()) || DBTypeEnum.DB_TIDB.getDbName().equals(sysDBInfo.getDbType())) {
            dbInfo.setDbDriver(DriverNameEnum.DRIVER_MYSQL.getDriverName());
            dbInfo.setUrl(MYSQL_PREFIX + sysDBInfo.getDbIp() + ":" + sysDBInfo.getDbPort() + "/" + sysDBInfo.getDbServerName() + MYSQL_SUFFIX);
        } else if (DBTypeEnum.DB_ORACLE.getDbName().equals(sysDBInfo.getDbType())) {
            dbInfo.setDbDriver(DriverNameEnum.DRIVER_ORACLE.getDriverName());
            dbInfo.setUrl(ORACLE_PREFIX + sysDBInfo.getDbIp() + ":" + sysDBInfo.getDbPort() + "/" + sysDBInfo.getDbServerName());
        } else if (DBTypeEnum.DB_POSTGRESQL.getDbName().equals(sysDBInfo.getDbType())) {
            dbInfo.setDbDriver(DriverNameEnum.DRIVER_POSTGRES.getDriverName());
            dbInfo.setUrl(POSTGRES_PREFIX + sysDBInfo.getDbIp() + ":" + sysDBInfo.getDbPort() + "/" + sysDBInfo.getDbServerName());
        }

        return dbInfo;
    }


    @Override
    public TableViewVo getColumnListBy(Integer dbId, String tableName) {

        TableViewVo viewVo = new TableViewVo();
        SysDBInfo sysDBInfo = configMapper.selectByPrimaryKey(dbId);
        //表头
        List<ColumnInfoVO> columnInfoVOList = GenDBUtils.getAllColumnInfo(sysDBInfo, tableName);
        List<List<Object>> dataList = GenDBUtils.executePage(sysDBInfo, tableName, 10, 0, 10);
        viewVo.setColumnInfoVOList(columnInfoVOList);
        viewVo.setDataList(dataList);
        return viewVo;
    }


}
