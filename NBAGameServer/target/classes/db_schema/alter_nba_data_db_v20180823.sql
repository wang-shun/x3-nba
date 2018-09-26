/*
Navicat MySQL Data Transfer

Source Server         : 192.168.10.181
Source Server Version : 50560
Source Host           : 192.168.10.181:3306
Source Database       : nba_data

Target Server Type    : MYSQL
Target Server Version : 50560
File Encoding         : 65001

Date: 2018-08-23 17:59:47
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for t_u_active_game_guess
-- ----------------------------
DROP TABLE IF EXISTS `t_u_active_game_guess`;
CREATE TABLE `t_u_active_game_guess` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `home_team_name` varchar(30) NOT NULL COMMENT '主队名字',
  `road_team_name` varchar(30) NOT NULL COMMENT '客队名字',
  `game_result` int(11) DEFAULT NULL COMMENT '比赛结果:0平局,1主队赢,2客队赢',
  `send_reward` int(11) DEFAULT NULL COMMENT '是否发奖:0没有,1奖励已发',
  `start_date` datetime NOT NULL COMMENT '比赛开始时间',
  `end_date` datetime NOT NULL COMMENT '比赛的结束时间',
  `reward` varchar(50) NOT NULL COMMENT '奖励配置数据',
  `cancel` int(11) NOT NULL DEFAULT '0' COMMENT '是否取消:0正常,1取消',
  `create_time` timestamp NOT NULL DEFAULT '1979-01-01 01:00:00',
  `update_time` timestamp NOT NULL DEFAULT '1979-01-01 01:00:00' ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8;
