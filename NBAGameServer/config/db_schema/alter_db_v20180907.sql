/*
Navicat MySQL Data Transfer

Source Server         : 192.168.10.181
Source Server Version : 50560
Source Host           : 192.168.10.181:3306
Source Database       : nba_lyh

Target Server Type    : MYSQL
Target Server Version : 50560
File Encoding         : 65001

Date: 2018-09-10 09:40:40
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for t_u_customer_service
-- ----------------------------
DROP TABLE IF EXISTS `t_u_customer_service`;
CREATE TABLE `t_u_customer_service` (
  `cs_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '主键Id',
  `area_name` varchar(30) NOT NULL COMMENT '区服名称',
  `team_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '玩家的球队Id',
  `vip_level` int(11) DEFAULT '0' COMMENT 'VIP等级',
  `player_name` varchar(50) DEFAULT NULL COMMENT '玩家名称',
  `telphone` varchar(20) DEFAULT NULL COMMENT '手机号码',
  `qq` varchar(30) DEFAULT NULL COMMENT '玩家QQ号',
  `problem` varchar(500) DEFAULT NULL COMMENT '问题描述',
  `response` varchar(500) DEFAULT NULL COMMENT '客服回复',
  `resp_status` varchar(1) DEFAULT '0' COMMENT '提问回复状态:''0''未回复,''1''已回复未读,''2''已回复已读',
  `occur_time` varchar(20) DEFAULT NULL COMMENT '问题发生的时间',
  `delete_flag` tinyint(2) DEFAULT '0' COMMENT '是否删除:0未删除,1已删除',
  `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT '1979-01-01 01:00:00' ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`cs_id`),
  KEY `idx_team_id` (`team_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='联盟';
