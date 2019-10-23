/*
Navicat MySQL Data Transfer

Source Server         : 本地161开发库
Source Server Version : 50639
Source Host           : 192.168.19.161:3306
Source Database       : dataextract

Target Server Type    : MYSQL
Target Server Version : 50639
File Encoding         : 65001

Date: 2019-10-22 11:19:15
*/

SET FOREIGN_KEY_CHECKS=0;


-- ----------------------------
-- Table structure for handler_task
-- ----------------------------
DROP TABLE IF EXISTS `handler_task`;
CREATE TABLE `handler_task` (
  `task_id` int(11) NOT NULL AUTO_INCREMENT,
  `commit_id` int(11) DEFAULT NULL COMMENT '已提交的记录id',
  `last_handler_time` datetime DEFAULT NULL COMMENT '最新处理时间',
  `cron_exp` varchar(128) DEFAULT NULL COMMENT '定时表达式',
  `task_type` tinyint(2) DEFAULT NULL COMMENT '任务类型 1 永久一次,2 周期执行，3 定时执行',
  `task_status` tinyint(2) DEFAULT '0' COMMENT '务状态，0 未开始，1 进行中，2 已完成',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`task_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务列表';

-- ----------------------------
-- Records of handler_task
-- ----------------------------

-- ----------------------------
-- Table structure for sys_dbinfo
-- ----------------------------
DROP TABLE IF EXISTS `sys_dbinfo`;
CREATE TABLE `sys_dbinfo` (
  `dbinfo_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `db_name` varchar(64) NOT NULL COMMENT '数据库名字',
  `db_type` varchar(64) NOT NULL COMMENT '数据库类型',
  `area_type` int(6) DEFAULT NULL COMMENT '区域类型：离线：1  专题：2  其他数据连接：3  沙盘结果输出：10',
  `db_ip` varchar(64) NOT NULL COMMENT '数据库ip',
  `db_port` varchar(32) NOT NULL COMMENT '数据库端口号',
  `db_server_name` varchar(64) NOT NULL COMMENT '服务名字',
  `db_table_schema` varchar(64) DEFAULT NULL COMMENT 'postgresql,同一个库需要区分不同的模式',
  `db_relkind` varchar(32) DEFAULT NULL COMMENT 'postgresql库区分视图与表 r:表 v:视图',
  `db_user` varchar(64) DEFAULT NULL COMMENT '数据库用户名',
  `db_password` varchar(64) DEFAULT NULL COMMENT '数据库密码',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态 1 可用 0 不可用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_person_id` varchar(36) DEFAULT NULL COMMENT '创建人用户id',
  PRIMARY KEY (`dbinfo_id`),
  UNIQUE KEY `db_unique_index` (`db_name`,`db_type`,`db_server_name`,`db_table_schema`)
) ENGINE=InnoDB AUTO_INCREMENT=36 DEFAULT CHARSET=utf8 COMMENT='数据库';

-- ----------------------------
-- Records of sys_dbinfo
-- ----------------------------
INSERT INTO `sys_dbinfo` VALUES ('1', 'pas', 'mysql', '3', '192.168.xx.xxx', '3306', 'xx', null, null, 'root', '123456', '1', '2018-07-18 16:15:31', '2019-07-31 15:07:30', '1');


