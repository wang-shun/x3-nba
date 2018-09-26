/*
Navicat MySQL Data Transfer

Source Server         : 192.168.10.181
Source Server Version : 50560
Source Host           : 192.168.10.181:3306
Source Database       : nba_lyh

Target Server Type    : MYSQL
Target Server Version : 50560
File Encoding         : 65001

Date: 2018-08-10 14:11:42
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for t_u_player_lower_salary_log
-- ----------------------------
DROP TABLE IF EXISTS `t_u_player_lower_salary_log`;
CREATE TABLE `t_u_player_lower_salary_log` (
  `pls_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '主键Id',
  `team_id` bigint(20) DEFAULT '0' COMMENT '玩家的球队Id',
  `player_id` int(11) DEFAULT '0' COMMENT '被降薪的新球球员Id',
  `before_salary` int(11) DEFAULT '0' COMMENT '新秀降薪前薪资',
  `after_salary` int(11) DEFAULT '0' COMMENT '新秀降薪前薪资',
  `amount` int(11) DEFAULT '0' COMMENT '此次降薪的额度',
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`pls_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='联盟';
