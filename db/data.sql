-- MySQL dump 10.13  Distrib 5.5.57, for Linux (x86_64)
--
-- Host: master-android    Database: nba_data
-- ------------------------------------------------------
-- Server version	5.5.57-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `data_game_schedule`
--

DROP TABLE IF EXISTS `data_game_schedule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `data_game_schedule` (
  `game_id` int(11) NOT NULL,
  `season_id` int(11) DEFAULT NULL,
  `game_type` int(11) DEFAULT NULL,
  `home_team_id` int(11) DEFAULT NULL,
  `away_team_id` int(11) DEFAULT NULL,
  `home_team_score` int(11) DEFAULT NULL,
  `away_team_score` int(11) DEFAULT NULL,
  `game_time` datetime DEFAULT NULL,
  PRIMARY KEY (`game_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `data_game_vs`
--

DROP TABLE IF EXISTS `data_game_vs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `data_game_vs` (
  `game_id` int(11) NOT NULL,
  `home` varchar(11) DEFAULT NULL,
  `away` varchar(11) DEFAULT NULL,
  `date_time` date DEFAULT NULL,
  `home_name` varchar(50) DEFAULT NULL,
  `away_name` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`game_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `data_job_runlog`
--

DROP TABLE IF EXISTS `data_job_runlog`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `data_job_runlog` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `run_time` datetime DEFAULT NULL,
  `game_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=2510 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `data_player_avg`
--

DROP TABLE IF EXISTS `data_player_avg`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `data_player_avg` (
  `season_id` int(11) NOT NULL,
  `player_id` int(11) NOT NULL,
  `play_count` int(11) NOT NULL DEFAULT '-1',
  `starter_count` int(11) NOT NULL DEFAULT '0',
  `fgm` float NOT NULL DEFAULT '0',
  `fga` float NOT NULL DEFAULT '0',
  `ftm` float NOT NULL DEFAULT '0',
  `fta` float NOT NULL DEFAULT '0',
  `three_pm` float NOT NULL DEFAULT '0',
  `three_pa` float NOT NULL DEFAULT '0',
  `oreb` float NOT NULL DEFAULT '0',
  `dreb` float NOT NULL DEFAULT '0',
  `ast` float NOT NULL DEFAULT '0',
  `stl` float NOT NULL DEFAULT '0',
  `blk` float NOT NULL DEFAULT '0',
  `to` float NOT NULL DEFAULT '0',
  `pf` float NOT NULL DEFAULT '0',
  `pts` float NOT NULL DEFAULT '0',
  `min` float NOT NULL DEFAULT '0',
  PRIMARY KEY (`season_id`,`player_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `data_player_cap`
--

DROP TABLE IF EXISTS `data_player_cap`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `data_player_cap` (
  `player_id` int(4) NOT NULL,
  `fgm` int(11) NOT NULL DEFAULT '0' COMMENT '投篮',
  `ftm` int(11) NOT NULL DEFAULT '0' COMMENT '罚球',
  `pts` int(11) NOT NULL DEFAULT '0' COMMENT '得分',
  `three_pm` int(11) NOT NULL DEFAULT '0' COMMENT '三分',
  `oreb` int(11) NOT NULL DEFAULT '0' COMMENT '前蓝板',
  `dreb` int(11) NOT NULL DEFAULT '0' COMMENT '后篮板',
  `ast` int(11) NOT NULL DEFAULT '0' COMMENT '助功',
  `stl` int(11) NOT NULL DEFAULT '0' COMMENT '抢断',
  `blk` int(11) NOT NULL DEFAULT '0' COMMENT '盖帽',
  `to` int(11) NOT NULL DEFAULT '0' COMMENT '失误',
  `min` int(11) NOT NULL DEFAULT '0' COMMENT '时间',
  `pf` int(11) NOT NULL DEFAULT '0' COMMENT '犯规',
  `attr_cap` int(11) NOT NULL DEFAULT '0' COMMENT '进攻和',
  `gua_cap` int(11) NOT NULL DEFAULT '0' COMMENT '防守和',
  `cap` int(11) NOT NULL DEFAULT '0' COMMENT '总能力',
  PRIMARY KEY (`player_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `data_rand`
--

DROP TABLE IF EXISTS `data_rand`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `data_rand` (
  `game_time` datetime NOT NULL,
  `vs_time` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`game_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `data_score_board`
--

DROP TABLE IF EXISTS `data_score_board`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `data_score_board` (
  `game_id` int(11) DEFAULT NULL,
  `team_id` int(11) DEFAULT NULL,
  `quarter1` int(11) DEFAULT NULL,
  `quarter2` int(11) DEFAULT NULL,
  `quarter3` int(11) DEFAULT NULL,
  `quarter4` int(11) DEFAULT NULL,
  `ot1` int(11) DEFAULT NULL,
  `ot2` int(11) DEFAULT NULL,
  `ot3` int(11) DEFAULT NULL,
  `ot4` int(11) DEFAULT NULL,
  `ot5` int(11) DEFAULT NULL,
  `ot6` int(11) DEFAULT NULL,
  `ot7` int(11) DEFAULT NULL,
  `total` int(11) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `data_score_board_detail`
--

DROP TABLE IF EXISTS `data_score_board_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `data_score_board_detail` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `player_id` int(11) DEFAULT NULL,
  `game_id` int(11) DEFAULT NULL,
  `team_id` int(11) DEFAULT NULL,
  `is_starter` int(11) DEFAULT NULL,
  `fgm` int(11) DEFAULT NULL,
  `fga` int(11) DEFAULT NULL,
  `ftm` int(11) DEFAULT NULL,
  `fta` int(11) DEFAULT NULL,
  `three_pm` int(11) DEFAULT NULL,
  `three_pa` int(11) DEFAULT NULL,
  `oreb` int(11) DEFAULT NULL,
  `dreb` int(11) DEFAULT NULL,
  `reb` int(11) DEFAULT NULL,
  `ast` int(11) DEFAULT NULL,
  `stl` int(11) DEFAULT NULL,
  `blk` int(11) DEFAULT NULL,
  `to` int(11) DEFAULT NULL,
  `pf` int(11) DEFAULT NULL,
  `pts` int(11) DEFAULT NULL,
  `effect_point` int(11) DEFAULT NULL,
  `min` int(11) DEFAULT NULL,
  UNIQUE KEY `id` (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=242698 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `injured`
--

DROP TABLE IF EXISTS `injured`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `injured` (
  `timex` date DEFAULT NULL,
  `player_id` int(11) DEFAULT NULL,
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `stat` int(4) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `player_id` (`player_id`),
  KEY `timex` (`timex`)
) ENGINE=MyISAM AUTO_INCREMENT=2909 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `nba_data_run_log`
--

DROP TABLE IF EXISTS `nba_data_run_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `nba_data_run_log` (
  `int` int(11) NOT NULL AUTO_INCREMENT,
  `run_time` date DEFAULT NULL,
  PRIMARY KEY (`int`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `player_info`
--

DROP TABLE IF EXISTS `player_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `player_info` (
  `player_id` int(4) NOT NULL COMMENT '球员ID',
  `espn_id` int(11) DEFAULT NULL,
  `team_id` int(4) DEFAULT NULL COMMENT '球队ID',
  `name` varchar(50) DEFAULT NULL COMMENT '中文名',
  `ename` varchar(50) DEFAULT NULL COMMENT '英文名',
  `short_name` varchar(50) DEFAULT NULL COMMENT '简称',
  `short_name_tw` varchar(50) DEFAULT NULL COMMENT '繁体',
  `short_name_en` varchar(50) DEFAULT NULL,
  `number` int(4) DEFAULT NULL COMMENT '编号',
  `position` varchar(50) DEFAULT NULL COMMENT '可打位置',
  `height` varchar(50) DEFAULT NULL COMMENT '身高',
  `weight` varchar(50) DEFAULT NULL COMMENT '体重',
  `school` varchar(50) DEFAULT NULL COMMENT '学校',
  `birthday` varchar(50) DEFAULT NULL COMMENT '生日',
  `nation` varchar(50) DEFAULT NULL COMMENT '国籍',
  `draft` varchar(50) DEFAULT '0' COMMENT '选秀',
  `salary` varchar(50) DEFAULT NULL COMMENT '赛季工资',
  `contract` varchar(200) DEFAULT NULL COMMENT '合同情况',
  `grade` varchar(50) DEFAULT NULL COMMENT '等级',
  `price` int(4) DEFAULT NULL COMMENT '工资帽',
  `before_price` int(4) DEFAULT NULL COMMENT '前一天工资',
  `player_type` tinyint(4) DEFAULT '0' COMMENT '0：普通 1：周最佳 2：月最佳',
  `injured` tinyint(4) NOT NULL DEFAULT '0',
  `grades` varchar(4) DEFAULT NULL,
  `cap` int(4) DEFAULT NULL COMMENT '能力值',
  `before_cap` int(4) DEFAULT NULL COMMENT '前一天能力值',
  `attr` int(4) NOT NULL DEFAULT '0' COMMENT '最新的进攻值',
  `before_attr` int(4) NOT NULL DEFAULT '0' COMMENT '前一天的进攻值',
  `contract_en` varchar(500) DEFAULT '' COMMENT '英文简介',
  `contract_tr` varchar(500) DEFAULT '',
  `contract_tw` varchar(200) DEFAULT '',
  `hupu_id` varchar(100) NOT NULL DEFAULT '',
  `plus` int(10) NOT NULL DEFAULT '0',
  PRIMARY KEY (`player_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `player_money`
--

DROP TABLE IF EXISTS `player_money`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `player_money` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `player_id` int(4) DEFAULT NULL,
  `time` date DEFAULT NULL,
  `price` int(11) DEFAULT NULL,
  `biznum` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `index_player_Id` (`player_id`)
) ENGINE=MyISAM AUTO_INCREMENT=2274162 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `player_rule`
--

DROP TABLE IF EXISTS `player_rule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `player_rule` (
  `id` int(4) NOT NULL,
  `grade` varchar(10) DEFAULT NULL COMMENT '等级',
  `money_min` int(4) DEFAULT NULL COMMENT '工资上限',
  `money_max` int(4) DEFAULT NULL COMMENT '工资下限',
  `nums` int(4) DEFAULT NULL COMMENT '该等级有多少人',
  `tops` int(4) DEFAULT NULL COMMENT '从小到大累计人数',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `system_active`
--

DROP TABLE IF EXISTS `system_active`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `system_active` (
  `shard_id` int(11) NOT NULL,
  `atv_id` int(11) NOT NULL,
  `name` varchar(50) DEFAULT NULL,
  `status` int(11) DEFAULT '0' COMMENT '0正常状态',
  `start_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `end_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `jsonConfig` varchar(1000) DEFAULT NULL COMMENT '自定义配置，默认读excel，有值读这里',
  PRIMARY KEY (`shard_id`,`atv_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_u_active_data_101`
--

DROP TABLE IF EXISTS `t_u_active_data_101`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_u_active_data_101` (
  `atv_id` int(11) NOT NULL,
  `shard_id` int(11) NOT NULL,
  `team_id` bigint(20) NOT NULL,
  `team_name` varchar(30) DEFAULT NULL,
  `type` int(5) DEFAULT NULL,
  `last_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `create_day` varchar(10) NOT NULL COMMENT '支持每天1条记录',
  `create_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `update_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `i_data1` int(11) DEFAULT NULL,
  `i_data2` int(11) DEFAULT NULL,
  `i_data3` int(11) DEFAULT NULL,
  `i_data4` int(11) DEFAULT NULL,
  `i_data5` int(11) DEFAULT NULL,
  `s_data1` varchar(500) DEFAULT NULL,
  `s_data2` varchar(500) DEFAULT NULL,
  `s_data3` varchar(500) DEFAULT NULL,
  `s_data4` varchar(500) DEFAULT NULL,
  `s_data5` varchar(500) DEFAULT NULL,
  `prop_num` varchar(500) DEFAULT NULL COMMENT '活动道具类型数据',
  `finish_status` varchar(500) DEFAULT NULL COMMENT '可领奖励',
  `award_status` varchar(500) DEFAULT NULL COMMENT '已领奖励',
  PRIMARY KEY (`atv_id`,`shard_id`,`team_id`,`create_day`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_u_active_data_102`
--

DROP TABLE IF EXISTS `t_u_active_data_102`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_u_active_data_102` (
  `atv_id` int(11) NOT NULL,
  `shard_id` int(11) NOT NULL,
  `team_id` bigint(20) NOT NULL,
  `team_name` varchar(30) DEFAULT NULL,
  `type` int(5) DEFAULT NULL,
  `last_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `create_day` varchar(10) NOT NULL COMMENT '支持每天1条记录',
  `create_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `update_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `i_data1` int(11) DEFAULT NULL,
  `i_data2` int(11) DEFAULT NULL,
  `i_data3` int(11) DEFAULT NULL,
  `i_data4` int(11) DEFAULT NULL,
  `i_data5` int(11) DEFAULT NULL,
  `s_data1` varchar(500) DEFAULT NULL,
  `s_data2` varchar(500) DEFAULT NULL,
  `s_data3` varchar(500) DEFAULT NULL,
  `s_data4` varchar(500) DEFAULT NULL,
  `s_data5` varchar(500) DEFAULT NULL,
  `prop_num` varchar(500) DEFAULT NULL COMMENT '活动道具类型数据',
  `finish_status` varchar(500) DEFAULT NULL COMMENT '可领奖励',
  `award_status` varchar(500) DEFAULT NULL COMMENT '已领奖励',
  PRIMARY KEY (`atv_id`,`shard_id`,`team_id`,`create_day`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_u_active_data_3100`
--

DROP TABLE IF EXISTS `t_u_active_data_3100`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_u_active_data_3100` (
  `atv_id` int(11) NOT NULL,
  `shard_id` int(11) NOT NULL,
  `team_id` bigint(20) NOT NULL,
  `team_name` varchar(30) DEFAULT NULL,
  `type` int(5) DEFAULT NULL,
  `last_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `create_day` varchar(10) NOT NULL COMMENT '支持每天1条记录',
  `create_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `update_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `i_data1` int(11) DEFAULT NULL,
  `i_data2` int(11) DEFAULT NULL,
  `i_data3` int(11) DEFAULT NULL,
  `i_data4` int(11) DEFAULT NULL,
  `i_data5` int(11) DEFAULT NULL,
  `s_data1` varchar(500) DEFAULT NULL,
  `s_data2` varchar(500) DEFAULT NULL,
  `s_data3` varchar(500) DEFAULT NULL,
  `s_data4` varchar(500) DEFAULT NULL,
  `s_data5` varchar(500) DEFAULT NULL,
  `prop_num` varchar(500) DEFAULT NULL COMMENT '活动数据类型道具',
  `finish_status` varchar(500) DEFAULT NULL COMMENT '可领奖励',
  `award_status` varchar(500) DEFAULT NULL COMMENT '已领奖励',
  PRIMARY KEY (`atv_id`,`shard_id`,`team_id`,`create_day`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_u_active_data_3101`
--

DROP TABLE IF EXISTS `t_u_active_data_3101`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_u_active_data_3101` (
  `atv_id` int(11) NOT NULL,
  `shard_id` int(11) NOT NULL,
  `team_id` bigint(20) NOT NULL,
  `team_name` varchar(30) DEFAULT NULL,
  `type` int(5) DEFAULT NULL,
  `last_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `create_day` varchar(10) NOT NULL COMMENT '支持每天1条记录',
  `create_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `update_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `i_data1` int(11) DEFAULT NULL,
  `i_data2` int(11) DEFAULT NULL,
  `i_data3` int(11) DEFAULT NULL,
  `i_data4` int(11) DEFAULT NULL,
  `i_data5` int(11) DEFAULT NULL,
  `s_data1` varchar(500) DEFAULT NULL,
  `s_data2` varchar(500) DEFAULT NULL,
  `s_data3` varchar(500) DEFAULT NULL,
  `s_data4` varchar(500) DEFAULT NULL,
  `s_data5` varchar(500) DEFAULT NULL,
  `prop_num` varchar(500) DEFAULT NULL COMMENT '活动数据类型道具',
  `finish_status` varchar(500) DEFAULT NULL COMMENT '可领奖励',
  `award_status` varchar(500) DEFAULT NULL COMMENT '已领奖励',
  PRIMARY KEY (`atv_id`,`shard_id`,`team_id`,`create_day`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_u_active_data_3102`
--

DROP TABLE IF EXISTS `t_u_active_data_3102`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_u_active_data_3102` (
  `atv_id` int(11) NOT NULL,
  `shard_id` int(11) NOT NULL,
  `team_id` bigint(20) NOT NULL,
  `team_name` varchar(30) DEFAULT NULL,
  `type` int(5) DEFAULT NULL,
  `last_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `create_day` varchar(10) NOT NULL COMMENT '支持每天1条记录',
  `create_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `update_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `i_data1` int(11) DEFAULT NULL,
  `i_data2` int(11) DEFAULT NULL,
  `i_data3` int(11) DEFAULT NULL,
  `i_data4` int(11) DEFAULT NULL,
  `i_data5` int(11) DEFAULT NULL,
  `s_data1` varchar(500) DEFAULT NULL,
  `s_data2` varchar(500) DEFAULT NULL,
  `s_data3` varchar(500) DEFAULT NULL,
  `s_data4` varchar(500) DEFAULT NULL,
  `s_data5` varchar(500) DEFAULT NULL,
  `prop_num` varchar(500) DEFAULT NULL COMMENT '活动数据类型道具',
  `finish_status` varchar(500) DEFAULT NULL COMMENT '可领奖励',
  `award_status` varchar(500) DEFAULT NULL COMMENT '已领奖励',
  PRIMARY KEY (`atv_id`,`shard_id`,`team_id`,`create_day`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_u_active_data_3103`
--

DROP TABLE IF EXISTS `t_u_active_data_3103`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_u_active_data_3103` (
  `atv_id` int(11) NOT NULL,
  `shard_id` int(11) NOT NULL,
  `team_id` bigint(20) NOT NULL,
  `team_name` varchar(30) DEFAULT NULL,
  `type` int(5) DEFAULT NULL,
  `last_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `create_day` varchar(10) NOT NULL COMMENT '支持每天1条记录',
  `create_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `update_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `i_data1` int(11) DEFAULT NULL,
  `i_data2` int(11) DEFAULT NULL,
  `i_data3` int(11) DEFAULT NULL,
  `i_data4` int(11) DEFAULT NULL,
  `i_data5` int(11) DEFAULT NULL,
  `s_data1` varchar(500) DEFAULT NULL,
  `s_data2` varchar(500) DEFAULT NULL,
  `s_data3` varchar(500) DEFAULT NULL,
  `s_data4` varchar(500) DEFAULT NULL,
  `s_data5` varchar(500) DEFAULT NULL,
  `prop_num` varchar(500) DEFAULT NULL COMMENT '活动数据类型道具',
  `finish_status` varchar(500) DEFAULT NULL COMMENT '可领奖励',
  `award_status` varchar(500) DEFAULT NULL COMMENT '已领奖励',
  PRIMARY KEY (`atv_id`,`shard_id`,`team_id`,`create_day`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_u_active_data_3104`
--

DROP TABLE IF EXISTS `t_u_active_data_3104`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_u_active_data_3104` (
  `atv_id` int(11) NOT NULL,
  `shard_id` int(11) NOT NULL,
  `team_id` bigint(20) NOT NULL,
  `team_name` varchar(30) DEFAULT NULL,
  `type` int(5) DEFAULT NULL,
  `last_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `create_day` varchar(10) NOT NULL COMMENT '支持每天1条记录',
  `create_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `update_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `i_data1` int(11) DEFAULT NULL,
  `i_data2` int(11) DEFAULT NULL,
  `i_data3` int(11) DEFAULT NULL,
  `i_data4` int(11) DEFAULT NULL,
  `i_data5` int(11) DEFAULT NULL,
  `s_data1` varchar(500) DEFAULT NULL,
  `s_data2` varchar(500) DEFAULT NULL,
  `s_data3` varchar(500) DEFAULT NULL,
  `s_data4` varchar(500) DEFAULT NULL,
  `s_data5` varchar(500) DEFAULT NULL,
  `prop_num` varchar(500) DEFAULT NULL COMMENT '活动数据类型道具',
  `finish_status` varchar(500) DEFAULT NULL COMMENT '可领奖励',
  `award_status` varchar(500) DEFAULT NULL COMMENT '已领奖励',
  PRIMARY KEY (`atv_id`,`shard_id`,`team_id`,`create_day`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_u_active_data_3105`
--

DROP TABLE IF EXISTS `t_u_active_data_3105`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_u_active_data_3105` (
  `atv_id` int(11) NOT NULL,
  `shard_id` int(11) NOT NULL,
  `team_id` bigint(20) NOT NULL,
  `team_name` varchar(30) DEFAULT NULL,
  `type` int(5) DEFAULT NULL,
  `last_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `create_day` varchar(10) NOT NULL COMMENT '支持每天1条记录',
  `create_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `update_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `i_data1` int(11) DEFAULT NULL,
  `i_data2` int(11) DEFAULT NULL,
  `i_data3` int(11) DEFAULT NULL,
  `i_data4` int(11) DEFAULT NULL,
  `i_data5` int(11) DEFAULT NULL,
  `s_data1` varchar(500) DEFAULT NULL,
  `s_data2` varchar(500) DEFAULT NULL,
  `s_data3` varchar(500) DEFAULT NULL,
  `s_data4` varchar(500) DEFAULT NULL,
  `s_data5` varchar(500) DEFAULT NULL,
  `prop_num` varchar(500) DEFAULT NULL COMMENT '活动数据类型道具',
  `finish_status` varchar(500) DEFAULT NULL COMMENT '可领奖励',
  `award_status` varchar(500) DEFAULT NULL COMMENT '已领奖励',
  PRIMARY KEY (`atv_id`,`shard_id`,`team_id`,`create_day`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_u_active_data_3106`
--

DROP TABLE IF EXISTS `t_u_active_data_3106`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_u_active_data_3106` (
  `atv_id` int(11) NOT NULL,
  `shard_id` int(11) NOT NULL,
  `team_id` bigint(20) NOT NULL,
  `team_name` varchar(30) DEFAULT NULL,
  `type` int(5) DEFAULT NULL,
  `last_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `create_day` varchar(10) NOT NULL COMMENT '支持每天1条记录',
  `create_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `update_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `i_data1` int(11) DEFAULT NULL,
  `i_data2` int(11) DEFAULT NULL,
  `i_data3` int(11) DEFAULT NULL,
  `i_data4` int(11) DEFAULT NULL,
  `i_data5` int(11) DEFAULT NULL,
  `s_data1` varchar(500) DEFAULT NULL,
  `s_data2` varchar(500) DEFAULT NULL,
  `s_data3` varchar(500) DEFAULT NULL,
  `s_data4` varchar(500) DEFAULT NULL,
  `s_data5` varchar(500) DEFAULT NULL,
  `prop_num` varchar(500) DEFAULT NULL COMMENT '活动数据类型道具',
  `finish_status` varchar(500) DEFAULT NULL COMMENT '可领奖励',
  `award_status` varchar(500) DEFAULT NULL COMMENT '已领奖励',
  PRIMARY KEY (`atv_id`,`shard_id`,`team_id`,`create_day`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_u_active_data_3107`
--

DROP TABLE IF EXISTS `t_u_active_data_3107`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_u_active_data_3107` (
  `atv_id` int(11) NOT NULL,
  `shard_id` int(11) NOT NULL,
  `team_id` bigint(20) NOT NULL,
  `team_name` varchar(30) DEFAULT NULL,
  `type` int(5) DEFAULT NULL,
  `last_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `create_day` varchar(10) NOT NULL COMMENT '支持每天1条记录',
  `create_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `update_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `i_data1` int(11) DEFAULT NULL,
  `i_data2` int(11) DEFAULT NULL,
  `i_data3` int(11) DEFAULT NULL,
  `i_data4` int(11) DEFAULT NULL,
  `i_data5` int(11) DEFAULT NULL,
  `s_data1` varchar(500) DEFAULT NULL,
  `s_data2` varchar(500) DEFAULT NULL,
  `s_data3` varchar(500) DEFAULT NULL,
  `s_data4` varchar(500) DEFAULT NULL,
  `s_data5` varchar(500) DEFAULT NULL,
  `prop_num` varchar(500) DEFAULT NULL COMMENT '活动数据类型道具',
  `finish_status` varchar(500) DEFAULT NULL COMMENT '可领奖励',
  `award_status` varchar(500) DEFAULT NULL COMMENT '已领奖励',
  PRIMARY KEY (`atv_id`,`shard_id`,`team_id`,`create_day`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_u_active_data_3108`
--

DROP TABLE IF EXISTS `t_u_active_data_3108`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_u_active_data_3108` (
  `atv_id` int(11) NOT NULL,
  `shard_id` int(11) NOT NULL,
  `team_id` bigint(20) NOT NULL,
  `team_name` varchar(30) DEFAULT NULL,
  `type` int(5) DEFAULT NULL,
  `last_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `create_day` varchar(10) NOT NULL COMMENT '支持每天1条记录',
  `create_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `update_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `i_data1` int(11) DEFAULT NULL,
  `i_data2` int(11) DEFAULT NULL,
  `i_data3` int(11) DEFAULT NULL,
  `i_data4` int(11) DEFAULT NULL,
  `i_data5` int(11) DEFAULT NULL,
  `s_data1` varchar(500) DEFAULT NULL,
  `s_data2` varchar(500) DEFAULT NULL,
  `s_data3` varchar(500) DEFAULT NULL,
  `s_data4` varchar(500) DEFAULT NULL,
  `s_data5` varchar(500) DEFAULT NULL,
  `prop_num` varchar(500) DEFAULT NULL COMMENT '活动数据类型道具',
  `finish_status` varchar(500) DEFAULT NULL COMMENT '可领奖励',
  `award_status` varchar(500) DEFAULT NULL COMMENT '已领奖励',
  PRIMARY KEY (`atv_id`,`shard_id`,`team_id`,`create_day`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_u_active_data_3109`
--

DROP TABLE IF EXISTS `t_u_active_data_3109`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_u_active_data_3109` (
  `atv_id` int(11) NOT NULL,
  `shard_id` int(11) NOT NULL,
  `team_id` bigint(20) NOT NULL,
  `team_name` varchar(30) DEFAULT NULL,
  `type` int(5) DEFAULT NULL,
  `last_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `create_day` varchar(10) NOT NULL COMMENT '支持每天1条记录',
  `create_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `update_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `i_data1` int(11) DEFAULT NULL,
  `i_data2` int(11) DEFAULT NULL,
  `i_data3` int(11) DEFAULT NULL,
  `i_data4` int(11) DEFAULT NULL,
  `i_data5` int(11) DEFAULT NULL,
  `s_data1` varchar(500) DEFAULT NULL,
  `s_data2` varchar(500) DEFAULT NULL,
  `s_data3` varchar(500) DEFAULT NULL,
  `s_data4` varchar(500) DEFAULT NULL,
  `s_data5` varchar(500) DEFAULT NULL,
  `prop_num` varchar(500) DEFAULT NULL COMMENT '活动数据类型道具',
  `finish_status` varchar(500) DEFAULT NULL COMMENT '可领奖励',
  `award_status` varchar(500) DEFAULT NULL COMMENT '已领奖励',
  PRIMARY KEY (`atv_id`,`shard_id`,`team_id`,`create_day`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_u_active_data_3110`
--

DROP TABLE IF EXISTS `t_u_active_data_3110`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_u_active_data_3110` (
  `atv_id` int(11) NOT NULL,
  `shard_id` int(11) NOT NULL,
  `team_id` bigint(20) NOT NULL,
  `team_name` varchar(30) DEFAULT NULL,
  `type` int(5) DEFAULT NULL,
  `last_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `create_day` varchar(10) NOT NULL COMMENT '支持每天1条记录',
  `create_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `update_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `i_data1` int(11) DEFAULT NULL,
  `i_data2` int(11) DEFAULT NULL,
  `i_data3` int(11) DEFAULT NULL,
  `i_data4` int(11) DEFAULT NULL,
  `i_data5` int(11) DEFAULT NULL,
  `s_data1` varchar(500) DEFAULT NULL,
  `s_data2` varchar(500) DEFAULT NULL,
  `s_data3` varchar(500) DEFAULT NULL,
  `s_data4` varchar(500) DEFAULT NULL,
  `s_data5` varchar(500) DEFAULT NULL,
  `prop_num` varchar(500) DEFAULT NULL COMMENT '活动数据类型道具',
  `finish_status` varchar(500) DEFAULT NULL COMMENT '可领奖励',
  `award_status` varchar(500) DEFAULT NULL COMMENT '已领奖励',
  PRIMARY KEY (`atv_id`,`shard_id`,`team_id`,`create_day`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_u_active_data_3801`
--

DROP TABLE IF EXISTS `t_u_active_data_3801`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_u_active_data_3801` (
  `atv_id` int(11) NOT NULL,
  `shard_id` int(11) NOT NULL,
  `team_id` bigint(20) NOT NULL,
  `team_name` varchar(30) DEFAULT NULL,
  `type` int(5) DEFAULT NULL,
  `last_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `create_day` varchar(10) NOT NULL COMMENT '支持每天1条记录',
  `create_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `update_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `i_data1` int(11) DEFAULT NULL,
  `i_data2` int(11) DEFAULT NULL,
  `i_data3` int(11) DEFAULT NULL,
  `i_data4` int(11) DEFAULT NULL,
  `i_data5` int(11) DEFAULT NULL,
  `s_data1` varchar(500) DEFAULT NULL,
  `s_data2` varchar(500) DEFAULT NULL,
  `s_data3` varchar(500) DEFAULT NULL,
  `s_data4` varchar(500) DEFAULT NULL,
  `s_data5` varchar(500) DEFAULT NULL,
  `prop_num` varchar(500) DEFAULT NULL COMMENT '活动数据类型道具',
  `finish_status` varchar(500) DEFAULT NULL COMMENT '可领奖励',
  `award_status` varchar(500) DEFAULT NULL COMMENT '已领奖励',
  PRIMARY KEY (`atv_id`,`shard_id`,`team_id`,`create_day`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_u_active_data_copynew`
--

DROP TABLE IF EXISTS `t_u_active_data_copynew`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_u_active_data_copynew` (
  `atv_id` int(11) NOT NULL,
  `shard_id` int(11) NOT NULL,
  `team_id` bigint(20) NOT NULL,
  `team_name` varchar(30) DEFAULT NULL,
  `type` int(5) DEFAULT NULL,
  `last_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `create_day` varchar(10) NOT NULL COMMENT '支持每天1条记录',
  `create_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `update_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `i_data1` int(11) DEFAULT NULL,
  `i_data2` int(11) DEFAULT NULL,
  `i_data3` int(11) DEFAULT NULL,
  `i_data4` int(11) DEFAULT NULL,
  `i_data5` int(11) DEFAULT NULL,
  `s_data1` varchar(500) DEFAULT NULL,
  `s_data2` varchar(500) DEFAULT NULL,
  `s_data3` varchar(500) DEFAULT NULL,
  `s_data4` varchar(500) DEFAULT NULL,
  `s_data5` varchar(500) DEFAULT NULL,
  `prop_num` varchar(500) DEFAULT NULL COMMENT '活动数据类型道具',
  `finish_status` varchar(500) DEFAULT NULL COMMENT '可领奖励',
  `award_status` varchar(500) DEFAULT NULL COMMENT '已领奖励',
  PRIMARY KEY (`atv_id`,`shard_id`,`team_id`,`create_day`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_u_active_data_share`
--

DROP TABLE IF EXISTS `t_u_active_data_share`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_u_active_data_share` (
  `atv_id` int(11) NOT NULL,
  `shard_id` int(11) NOT NULL,
  `team_id` bigint(20) NOT NULL,
  `team_name` varchar(30) DEFAULT NULL,
  `type` int(5) DEFAULT NULL,
  `last_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `update_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `i_data1` int(11) DEFAULT NULL,
  `i_data2` int(11) DEFAULT NULL,
  `i_data3` int(11) DEFAULT NULL,
  `s_data1` varbinary(200) DEFAULT NULL,
  `s_data2` varbinary(200) DEFAULT NULL,
  `s_data3` varbinary(200) DEFAULT NULL,
  `s_data4` varbinary(200) DEFAULT NULL,
  `s_data5` varbinary(200) DEFAULT NULL,
  PRIMARY KEY (`atv_id`,`team_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_u_active_game_guess`
--

DROP TABLE IF EXISTS `t_u_active_game_guess`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
  `create_time` timestamp NOT NULL DEFAULT '1978-12-31 17:00:00',
  `update_time` timestamp NOT NULL DEFAULT '1978-12-31 17:00:00' ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=62 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_u_besign`
--

DROP TABLE IF EXISTS `t_u_besign`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_u_besign` (
  `id` int(11) NOT NULL,
  `team_id` bigint(20) NOT NULL,
  `player_id` int(11) DEFAULT NULL,
  `price` int(11) DEFAULT NULL,
  `tid` int(11) DEFAULT '0',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  PRIMARY KEY (`id`,`team_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Temporary table structure for view `t_v_player_info`
--

DROP TABLE IF EXISTS `t_v_player_info`;
/*!50001 DROP VIEW IF EXISTS `t_v_player_info`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `t_v_player_info` (
  `player_id` tinyint NOT NULL,
  `team_id` tinyint NOT NULL,
  `name` tinyint NOT NULL,
  `short_name` tinyint NOT NULL,
  `position` tinyint NOT NULL,
  `grade` tinyint NOT NULL,
  `price` tinyint NOT NULL,
  `before_price` tinyint NOT NULL,
  `player_type` tinyint NOT NULL,
  `draft` tinyint NOT NULL,
  `injured` tinyint NOT NULL,
  `fgm` tinyint NOT NULL,
  `ftm` tinyint NOT NULL,
  `pts` tinyint NOT NULL,
  `three_pm` tinyint NOT NULL,
  `oreb` tinyint NOT NULL,
  `dreb` tinyint NOT NULL,
  `ast` tinyint NOT NULL,
  `stl` tinyint NOT NULL,
  `blk` tinyint NOT NULL,
  `to` tinyint NOT NULL,
  `min` tinyint NOT NULL,
  `pf` tinyint NOT NULL,
  `attr_cap` tinyint NOT NULL,
  `gua_cap` tinyint NOT NULL,
  `cap` tinyint NOT NULL,
  `before_cap` tinyint NOT NULL,
  `plus` tinyint NOT NULL,
  `ename` tinyint NOT NULL
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `team_info`
--

DROP TABLE IF EXISTS `team_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `team_info` (
  `team_id` int(4) NOT NULL COMMENT '球队编号',
  `espn_name` varchar(10) DEFAULT NULL COMMENT 'Espn简称',
  `team_name` varchar(50) DEFAULT NULL COMMENT '球队名',
  `team_ename` varchar(50) DEFAULT NULL COMMENT '球队英文名',
  `area` varchar(50) DEFAULT NULL COMMENT '地区',
  `short_name` varchar(50) DEFAULT NULL COMMENT '简称',
  `short_name_tw` varchar(50) DEFAULT NULL,
  `area_short` varchar(50) DEFAULT NULL,
  `win` int(4) DEFAULT '0' COMMENT '胜场',
  `loss` int(4) DEFAULT '0' COMMENT '负场',
  `winloss` int(4) DEFAULT '0' COMMENT '连胜连负',
  `rank` int(4) DEFAULT '1' COMMENT '排名',
  PRIMARY KEY (`team_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Temporary table structure for view `ttttt`
--

DROP TABLE IF EXISTS `ttttt`;
/*!50001 DROP VIEW IF EXISTS `ttttt`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `ttttt` (
  `season_id` tinyint NOT NULL,
  `id` tinyint NOT NULL,
  `player_id` tinyint NOT NULL,
  `game_id` tinyint NOT NULL,
  `team_id` tinyint NOT NULL,
  `is_starter` tinyint NOT NULL,
  `fgm` tinyint NOT NULL,
  `fga` tinyint NOT NULL,
  `ftm` tinyint NOT NULL,
  `fta` tinyint NOT NULL,
  `three_pm` tinyint NOT NULL,
  `three_pa` tinyint NOT NULL,
  `oreb` tinyint NOT NULL,
  `dreb` tinyint NOT NULL,
  `reb` tinyint NOT NULL,
  `ast` tinyint NOT NULL,
  `stl` tinyint NOT NULL,
  `blk` tinyint NOT NULL,
  `to` tinyint NOT NULL,
  `pf` tinyint NOT NULL,
  `pts` tinyint NOT NULL,
  `effect_point` tinyint NOT NULL,
  `min` tinyint NOT NULL
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `x_data_game_schedule`
--

DROP TABLE IF EXISTS `x_data_game_schedule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `x_data_game_schedule` (
  `game_id` int(11) NOT NULL,
  `season_id` int(11) DEFAULT NULL,
  `status` int(11) DEFAULT '0',
  `game_type` int(11) DEFAULT NULL,
  `home_team_id` int(11) DEFAULT NULL,
  `away_team_id` int(11) DEFAULT NULL,
  `home_team_score` int(11) DEFAULT NULL,
  `away_team_score` int(11) DEFAULT NULL,
  `game_time` datetime DEFAULT NULL,
  PRIMARY KEY (`game_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `x_data_score_board`
--

DROP TABLE IF EXISTS `x_data_score_board`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `x_data_score_board` (
  `game_id` int(11) DEFAULT NULL,
  `team_id` int(11) DEFAULT NULL,
  `quarter1` int(11) DEFAULT NULL,
  `quarter2` int(11) DEFAULT NULL,
  `quarter3` int(11) DEFAULT NULL,
  `quarter4` int(11) DEFAULT NULL,
  `ot1` int(11) DEFAULT NULL,
  `ot2` int(11) DEFAULT NULL,
  `ot3` int(11) DEFAULT NULL,
  `ot4` int(11) DEFAULT NULL,
  `ot5` int(11) DEFAULT NULL,
  `ot6` int(11) DEFAULT NULL,
  `ot7` int(11) DEFAULT NULL,
  `total` int(11) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `x_data_score_board_detail`
--

DROP TABLE IF EXISTS `x_data_score_board_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `x_data_score_board_detail` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `player_id` int(11) DEFAULT NULL,
  `game_id` int(11) DEFAULT NULL,
  `team_id` int(11) DEFAULT NULL,
  `is_starter` int(11) DEFAULT NULL,
  `fgm` int(11) DEFAULT NULL,
  `fga` int(11) DEFAULT NULL,
  `ftm` int(11) DEFAULT NULL,
  `fta` int(11) DEFAULT NULL,
  `three_pm` int(11) DEFAULT NULL,
  `three_pa` int(11) DEFAULT NULL,
  `oreb` int(11) DEFAULT NULL,
  `dreb` int(11) DEFAULT NULL,
  `reb` int(11) DEFAULT NULL,
  `ast` int(11) DEFAULT NULL,
  `stl` int(11) DEFAULT NULL,
  `blk` int(11) DEFAULT NULL,
  `to` int(11) DEFAULT NULL,
  `pf` int(11) DEFAULT NULL,
  `pts` int(11) DEFAULT NULL,
  `effect_point` int(11) DEFAULT NULL,
  `min` int(11) DEFAULT NULL,
  UNIQUE KEY `id` (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=1357747 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Final view structure for view `t_v_player_info`
--

/*!50001 DROP TABLE IF EXISTS `t_v_player_info`*/;
/*!50001 DROP VIEW IF EXISTS `t_v_player_info`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`%` SQL SECURITY DEFINER */
/*!50001 VIEW `t_v_player_info` AS (select `player_info`.`player_id` AS `player_id`,`player_info`.`team_id` AS `team_id`,`player_info`.`name` AS `name`,`player_info`.`short_name` AS `short_name`,`player_info`.`position` AS `position`,`player_info`.`grade` AS `grade`,`player_info`.`price` AS `price`,`player_info`.`before_price` AS `before_price`,`player_info`.`player_type` AS `player_type`,`player_info`.`draft` AS `draft`,`player_info`.`injured` AS `injured`,`data_player_cap`.`fgm` AS `fgm`,`data_player_cap`.`ftm` AS `ftm`,`data_player_cap`.`pts` AS `pts`,`data_player_cap`.`three_pm` AS `three_pm`,`data_player_cap`.`oreb` AS `oreb`,`data_player_cap`.`dreb` AS `dreb`,`data_player_cap`.`ast` AS `ast`,`data_player_cap`.`stl` AS `stl`,`data_player_cap`.`blk` AS `blk`,`data_player_cap`.`to` AS `to`,`data_player_cap`.`min` AS `min`,`data_player_cap`.`pf` AS `pf`,`data_player_cap`.`attr_cap` AS `attr_cap`,`data_player_cap`.`gua_cap` AS `gua_cap`,`data_player_cap`.`cap` AS `cap`,`player_info`.`before_cap` AS `before_cap`,`player_info`.`plus` AS `plus`,`player_info`.`ename` AS `ename` from (`player_info` join `data_player_cap`) where (`player_info`.`player_id` = `data_player_cap`.`player_id`)) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `ttttt`
--

/*!50001 DROP TABLE IF EXISTS `ttttt`*/;
/*!50001 DROP VIEW IF EXISTS `ttttt`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`%` SQL SECURITY DEFINER */
/*!50001 VIEW `ttttt` AS (select `s`.`season_id` AS `season_id`,`d`.`id` AS `id`,`d`.`player_id` AS `player_id`,`d`.`game_id` AS `game_id`,`d`.`team_id` AS `team_id`,`d`.`is_starter` AS `is_starter`,`d`.`fgm` AS `fgm`,`d`.`fga` AS `fga`,`d`.`ftm` AS `ftm`,`d`.`fta` AS `fta`,`d`.`three_pm` AS `three_pm`,`d`.`three_pa` AS `three_pa`,`d`.`oreb` AS `oreb`,`d`.`dreb` AS `dreb`,`d`.`reb` AS `reb`,`d`.`ast` AS `ast`,`d`.`stl` AS `stl`,`d`.`blk` AS `blk`,`d`.`to` AS `to`,`d`.`pf` AS `pf`,`d`.`pts` AS `pts`,`d`.`effect_point` AS `effect_point`,`d`.`min` AS `min` from (`data_score_board_detail` `d` join `data_game_schedule` `s`) where ((`s`.`season_id` >= 2017) and (`s`.`game_id` = `d`.`game_id`))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2018-09-08 15:41:04
