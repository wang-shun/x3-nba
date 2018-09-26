-- MySQL dump 10.13  Distrib 5.5.57, for Linux (x86_64)
--
-- Host: logic-3105    Database: nba_3105
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
-- Table structure for table `t_c_config`
--

DROP TABLE IF EXISTS `t_c_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_c_config` (
  `key` varchar(100) NOT NULL COMMENT 'Key值',
  `value` varchar(200) DEFAULT NULL COMMENT '对应数据',
  `description` varchar(200) DEFAULT NULL COMMENT '描述',
  PRIMARY KEY (`key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 CHECKSUM=1 DELAY_KEY_WRITE=1 ROW_FORMAT=DYNAMIC COMMENT='游戏配置表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_c_drop`
--

DROP TABLE IF EXISTS `t_c_drop`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_c_drop` (
  `drop_id` int(11) DEFAULT NULL COMMENT '掉落编号',
  `drop_type` int(11) DEFAULT NULL COMMENT '掉落类型',
  `prop_id` int(11) DEFAULT NULL COMMENT '物品ID',
  `min_num` int(11) DEFAULT NULL COMMENT '最小数量',
  `max_num` int(11) DEFAULT NULL COMMENT '最大数量',
  `probaility` int(11) DEFAULT NULL COMMENT '所占掉落组概率',
  `remark` varchar(200) DEFAULT NULL COMMENT '备注',
  KEY `t_c_drop_` (`drop_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='掉落配置表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_c_player_exchange`
--

DROP TABLE IF EXISTS `t_c_player_exchange`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_c_player_exchange` (
  `player_id` int(11) NOT NULL COMMENT '球员ID',
  `exchange_num` int(11) DEFAULT NULL COMMENT '兑换数量',
  `create_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`player_id`,`create_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_c_player_money`
--

DROP TABLE IF EXISTS `t_c_player_money`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_c_player_money` (
  `player_id` int(11) NOT NULL,
  `price` int(11) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`player_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 CHECKSUM=1 DELAY_KEY_WRITE=1 ROW_FORMAT=DYNAMIC COMMENT='球员底薪';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_c_prop`
--

DROP TABLE IF EXISTS `t_c_prop`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_c_prop` (
  `prop_id` int(11) NOT NULL,
  `prop_type` int(11) DEFAULT NULL,
  `t_id` int(11) DEFAULT NULL,
  `prop_name` varchar(50) DEFAULT NULL,
  `config` varchar(200) DEFAULT NULL,
  `sale` int(11) DEFAULT NULL,
  `description` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`prop_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 CHECKSUM=1 DELAY_KEY_WRITE=1 ROW_FORMAT=DYNAMIC COMMENT='道具配置';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_u_arena`
--

DROP TABLE IF EXISTS `t_u_arena`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_u_arena` (
  `team_id` bigint(20) NOT NULL,
  `cid` int(11) NOT NULL,
  `cur_gold` int(11) DEFAULT NULL,
  `max_gold` int(11) DEFAULT NULL,
  `player_id` int(11) DEFAULT NULL,
  `player_grade` int(11) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`team_id`,`cid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 CHECKSUM=1 DELAY_KEY_WRITE=1 ROW_FORMAT=DYNAMIC COMMENT='球馆建筑信息';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_u_arena_player`
--

DROP TABLE IF EXISTS `t_u_arena_player`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_u_arena_player` (
  `team_id` bigint(20) NOT NULL,
  `pid` int(11) NOT NULL,
  `player_id` int(11) DEFAULT NULL,
  `tid` int(11) DEFAULT NULL,
  `grade` int(11) DEFAULT NULL,
  `position` int(11) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`team_id`,`pid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_u_arena_team`
--

DROP TABLE IF EXISTS `t_u_arena_team`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_u_arena_team` (
  `team_id` bigint(20) NOT NULL,
  `gold` int(11) DEFAULT NULL,
  `power` int(11) DEFAULT NULL,
  `defend` int(11) DEFAULT NULL,
  `level` int(11) DEFAULT NULL,
  `last_update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`team_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 CHECKSUM=1 DELAY_KEY_WRITE=1 ROW_FORMAT=DYNAMIC COMMENT='球馆玩家信息';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_u_battle`
--

DROP TABLE IF EXISTS `t_u_battle`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_u_battle` (
  `battle_id` bigint(20) NOT NULL,
  `battle_type` int(11) DEFAULT NULL,
  `home_team_id` bigint(20) DEFAULT NULL,
  `home_team_name` varchar(100) DEFAULT NULL,
  `home_score` int(11) DEFAULT NULL,
  `away_team_id` bigint(20) DEFAULT NULL,
  `away_team_name` varchar(100) DEFAULT NULL,
  `away_score` int(11) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `vi1` int(11) DEFAULT NULL COMMENT '附加参数int 1',
  `vi2` int(11) DEFAULT NULL COMMENT '附加参数int 2',
  `vi3` int(11) DEFAULT NULL COMMENT '附加参数int 3',
  `vi4` int(11) DEFAULT NULL COMMENT '附加参数int 4',
  `vl1` bigint(20) DEFAULT NULL COMMENT '附加参数long 1',
  `vl2` bigint(20) DEFAULT NULL COMMENT '附加参数long 2',
  `vl3` bigint(20) DEFAULT NULL COMMENT '附加参数long 3',
  `vl4` bigint(20) DEFAULT NULL COMMENT '附加参数long 4',
  `str1` text COMMENT '附加参数str 1',
  `str2` text COMMENT '附加参数str 2',
  PRIMARY KEY (`battle_id`),
  KEY `home_team_id_index` (`home_team_id`),
  KEY `away_team_id_index` (`away_team_id`),
  KEY `i_bt` (`battle_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 CHECKSUM=1 DELAY_KEY_WRITE=1 ROW_FORMAT=DYNAMIC COMMENT='比赛数据';
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
  `bind` tinyint(1) DEFAULT NULL COMMENT '是否绑定',
  PRIMARY KEY (`id`,`team_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_u_buff`
--

DROP TABLE IF EXISTS `t_u_buff`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_u_buff` (
  `id` int(11) NOT NULL,
  `team_id` bigint(20) NOT NULL,
  `params` varchar(200) DEFAULT NULL,
  `end_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `create_time` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`,`team_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_u_card`
--

DROP TABLE IF EXISTS `t_u_card`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_u_card` (
  `team_id` bigint(20) NOT NULL,
  `player_id` int(11) NOT NULL,
  `type` int(2) NOT NULL,
  `starlv` int(2) DEFAULT NULL,
  `exp` int(11) DEFAULT NULL,
  `cost_num` int(11) DEFAULT '0' COMMENT '吞噬底薪球员的数量',
  `create_time` datetime DEFAULT NULL,
  `qua` int(11) DEFAULT '0',
  PRIMARY KEY (`team_id`,`player_id`,`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_u_coach`
--

DROP TABLE IF EXISTS `t_u_coach`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_u_coach` (
  `team_id` bigint(20) NOT NULL,
  `cid` int(11) NOT NULL,
  `tid` int(11) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`team_id`,`cid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_u_custom_guess`
--

DROP TABLE IF EXISTS `t_u_custom_guess`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_u_custom_guess` (
  `team_id` bigint(20) NOT NULL,
  `room_id` int(11) NOT NULL,
  `money_A` int(11) DEFAULT NULL,
  `money_B` int(11) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`team_id`,`room_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_u_custom_room`
--

DROP TABLE IF EXISTS `t_u_custom_room`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_u_custom_room` (
  `room_id` int(11) NOT NULL,
  `pk_type` int(11) DEFAULT NULL,
  `guess_type` int(11) DEFAULT NULL,
  `win_condition` int(11) DEFAULT NULL,
  `power_condition` int(11) DEFAULT NULL,
  `level_condition` int(11) DEFAULT NULL,
  `step_condition` varchar(200) DEFAULT NULL,
  `position_condition` int(11) DEFAULT NULL,
  `room_status` int(11) DEFAULT NULL,
  `room_tip` varchar(300) DEFAULT NULL,
  `room_money` int(11) DEFAULT NULL,
  `room_score` int(11) DEFAULT NULL,
  `home_money` int(11) DEFAULT NULL,
  `away_money` int(11) DEFAULT NULL,
  `home_team` varchar(800) DEFAULT NULL,
  `away_team` varchar(800) DEFAULT NULL,
  `nodes` varchar(500) DEFAULT NULL,
  `start_time` datetime DEFAULT NULL,
  PRIMARY KEY (`room_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 CHECKSUM=1 DELAY_KEY_WRITE=1 ROW_FORMAT=DYNAMIC COMMENT='自定义擂台赛房间信息';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_u_custom_team`
--

DROP TABLE IF EXISTS `t_u_custom_team`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_u_custom_team` (
  `team_id` bigint(20) NOT NULL,
  `money` int(11) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`team_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 CHECKSUM=1 DELAY_KEY_WRITE=1 ROW_FORMAT=DYNAMIC COMMENT='擂台赛玩家信息';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_u_email`
--

DROP TABLE IF EXISTS `t_u_email`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_u_email` (
  `id` int(11) NOT NULL,
  `team_id` bigint(20) NOT NULL,
  `view_id` int(11) DEFAULT NULL,
  `type` int(2) DEFAULT NULL,
  `title` varchar(100) DEFAULT NULL,
  `content` varchar(300) DEFAULT NULL,
  `status` int(2) DEFAULT NULL,
  `award_config` varchar(500) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`,`team_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_u_email_to_team`
--

DROP TABLE IF EXISTS `t_u_email_to_team`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_u_email_to_team` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `seq_id` int(20) NOT NULL COMMENT '邮件补偿ID，指定',
  `title` varchar(200) DEFAULT NULL,
  `content` varchar(200) DEFAULT NULL,
  `award_config` varchar(200) DEFAULT NULL,
  `status` int(2) DEFAULT NULL COMMENT '0未发，1已发',
  `remark` varchar(200) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`,`seq_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_u_equi`
--

DROP TABLE IF EXISTS `t_u_equi`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_u_equi` (
  `id` int(11) NOT NULL COMMENT '序列ID,套装',
  `type` int(11) NOT NULL COMMENT '类型',
  `team_id` bigint(11) NOT NULL COMMENT '球队ID',
  `equi_id` int(11) DEFAULT NULL COMMENT '模板ID',
  `player_id` int(11) DEFAULT NULL COMMENT '装备球员id类型',
  `equi_team` int(11) DEFAULT NULL COMMENT '装备所属球队，0是所有球队可用',
  `strlv` int(11) DEFAULT NULL COMMENT '强化等级',
  `str_bless` float DEFAULT NULL,
  `rand_attr` varchar(200) DEFAULT NULL COMMENT '随机属性',
  `create_time` datetime DEFAULT NULL,
  `end_time` datetime DEFAULT NULL COMMENT '有效时间',
  PRIMARY KEY (`id`,`type`,`team_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_u_friend`
--

DROP TABLE IF EXISTS `t_u_friend`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_u_friend` (
  `team_id` bigint(20) NOT NULL,
  `friend_team_id` bigint(20) NOT NULL,
  `type` tinyint(4) DEFAULT '1' COMMENT '1好友 0黑名单',
  `remark` varchar(100) DEFAULT NULL COMMENT '好友备注',
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`team_id`,`friend_team_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_u_league`
--

DROP TABLE IF EXISTS `t_u_league`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_u_league` (
  `league_id` int(11) NOT NULL DEFAULT '0',
  `league_name` varchar(100) DEFAULT NULL,
  `logo` varchar(50) DEFAULT NULL,
  `league_level` int(11) DEFAULT NULL,
  `honor` int(11) DEFAULT NULL,
  `team_name` varchar(50) DEFAULT NULL,
  `league_tip` varchar(200) CHARACTER SET utf8mb4 DEFAULT NULL,
  `league_notice` varchar(200) CHARACTER SET utf8mb4 DEFAULT NULL,
  `people_count` int(11) DEFAULT NULL,
  `limit_shop` int(11) DEFAULT NULL,
  `limit_honor` int(11) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`league_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 CHECKSUM=1 DELAY_KEY_WRITE=1 ROW_FORMAT=DYNAMIC COMMENT='联盟';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_u_league_group`
--

DROP TABLE IF EXISTS `t_u_league_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_u_league_group` (
  `league_id` int(11) NOT NULL,
  `group_id` int(11) NOT NULL,
  `name` varchar(50) DEFAULT NULL,
  `score` int(11) DEFAULT NULL,
  `win_num` int(11) DEFAULT NULL,
  `loss_num` int(11) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  PRIMARY KEY (`league_id`,`group_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_u_league_group_season`
--

DROP TABLE IF EXISTS `t_u_league_group_season`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_u_league_group_season` (
  `id` int(11) NOT NULL COMMENT '赛季',
  `name` varchar(50) DEFAULT NULL,
  `start_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `end_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `status` int(11) DEFAULT NULL COMMENT '0默认，1开始，2结束',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_u_league_group_team`
--

DROP TABLE IF EXISTS `t_u_league_group_team`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_u_league_group_team` (
  `league_id` int(11) NOT NULL,
  `group_id` int(11) NOT NULL,
  `team_id` bigint(20) NOT NULL,
  `privity` int(11) DEFAULT NULL,
  `position` int(11) DEFAULT NULL,
  `level` int(11) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  PRIMARY KEY (`league_id`,`group_id`,`team_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_u_league_honor`
--

DROP TABLE IF EXISTS `t_u_league_honor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_u_league_honor` (
  `league_id` int(11) NOT NULL,
  `honor_id` int(11) NOT NULL,
  `level` int(11) DEFAULT NULL,
  `end_time` datetime DEFAULT NULL,
  PRIMARY KEY (`league_id`,`honor_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_u_league_honor_pool`
--

DROP TABLE IF EXISTS `t_u_league_honor_pool`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_u_league_honor_pool` (
  `league_id` int(11) NOT NULL,
  `prop_id` int(11) NOT NULL,
  `num` int(11) DEFAULT NULL,
  PRIMARY KEY (`league_id`,`prop_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 CHECKSUM=1 DELAY_KEY_WRITE=1 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_u_league_team`
--

DROP TABLE IF EXISTS `t_u_league_team`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_u_league_team` (
  `league_id` int(11) NOT NULL,
  `team_id` bigint(20) NOT NULL,
  `level` int(11) DEFAULT NULL,
  `score` int(11) DEFAULT NULL,
  `feats` int(11) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `week_score` int(11) NOT NULL DEFAULT '0' COMMENT '成员周贡献',
  PRIMARY KEY (`team_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 CHECKSUM=1 DELAY_KEY_WRITE=1 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_u_league_train`
--

DROP TABLE IF EXISTS `t_u_league_train`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_u_league_train` (
  `bl_id` smallint(6) NOT NULL DEFAULT '0' COMMENT '球馆ID',
  `league_id` int(12) NOT NULL DEFAULT '0' COMMENT '联盟ID',
  `bt_id` smallint(6) NOT NULL DEFAULT '1' COMMENT '配置球队ID',
  PRIMARY KEY (`bl_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='联盟训练馆数据';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_u_logo`
--

DROP TABLE IF EXISTS `t_u_logo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_u_logo` (
  `id` int(11) NOT NULL COMMENT 'id',
  `team_id` bigint(20) DEFAULT NULL COMMENT '球队',
  `player_id` int(11) DEFAULT NULL COMMENT '球员',
  `quality` int(11) DEFAULT NULL COMMENT '品质',
  `create_time` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_u_logo_player`
--

DROP TABLE IF EXISTS `t_u_logo_player`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_u_logo_player` (
  `team_id` bigint(11) NOT NULL,
  `player_id` int(11) NOT NULL,
  `logo_id` int(11) DEFAULT NULL COMMENT '头像ID',
  `lv` int(11) DEFAULT NULL COMMENT '荣誉等级',
  `starLv` int(11) DEFAULT NULL COMMENT '大星脉个数',
  `step` int(11) DEFAULT NULL COMMENT '小星脉个数',
  PRIMARY KEY (`team_id`,`player_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_u_match`
--

DROP TABLE IF EXISTS `t_u_match`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_u_match` (
  `seq_id` int(11) NOT NULL COMMENT '届数',
  `match_id` int(2) NOT NULL COMMENT '多人赛类型',
  `status` int(2) DEFAULT NULL COMMENT '开始报名(1), 报名截止(4), 比赛中(2), 结束(3)',
  `round` int(11) DEFAULT NULL COMMENT '当前轮数',
  `max_round` int(11) DEFAULT NULL COMMENT '最大轮数',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  `match_time` timestamp NULL DEFAULT NULL COMMENT '比赛时间',
  `logic_name` varchar(50) DEFAULT NULL COMMENT '逻辑节点名称',
  PRIMARY KEY (`seq_id`,`match_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_u_match_best`
--

DROP TABLE IF EXISTS `t_u_match_best`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_u_match_best` (
  `match_id` int(11) NOT NULL,
  `team_id` bigint(20) NOT NULL,
  `rank` int(11) DEFAULT NULL,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`match_id`,`team_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_u_match_pk`
--

DROP TABLE IF EXISTS `t_u_match_pk`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_u_match_pk` (
  `battle_id` int(11) NOT NULL,
  `seq_id` int(11) DEFAULT NULL,
  `match_id` int(11) DEFAULT NULL,
  `round` int(11) DEFAULT NULL,
  `home_id` bigint(20) DEFAULT NULL,
  `away_id` bigint(20) DEFAULT NULL,
  `win_team_id` bigint(20) DEFAULT NULL,
  `home_score` int(11) DEFAULT NULL,
  `away_score` int(11) DEFAULT NULL,
  `status` int(2) DEFAULT NULL,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `end_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`battle_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_u_match_sign`
--

DROP TABLE IF EXISTS `t_u_match_sign`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_u_match_sign` (
  `seq_id` int(11) NOT NULL,
  `match_id` int(11) NOT NULL,
  `team_id` bigint(20) NOT NULL,
  `team_cap` int(11) DEFAULT NULL COMMENT '球队攻防',
  `status` int(2) DEFAULT NULL,
  `rank` int(11) DEFAULT NULL COMMENT '排名',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`seq_id`,`match_id`,`team_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_u_mmatch_div`
--

DROP TABLE IF EXISTS `t_u_mmatch_div`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_u_mmatch_div` (
  `id` int(11) NOT NULL,
  `team_id` bigint(20) NOT NULL,
  `r_id` int(11) NOT NULL COMMENT '赛区配置id',
  `star_awards` int(11) DEFAULT NULL COMMENT '奖励领取信息',
  PRIMARY KEY (`team_id`,`id`),
  KEY `i_t_id` (`team_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='球队主线赛程赛区信息';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_u_mmatch_lev`
--

DROP TABLE IF EXISTS `t_u_mmatch_lev`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_u_mmatch_lev` (
  `id` int(11) NOT NULL,
  `team_id` bigint(20) NOT NULL,
  `r_id` int(11) NOT NULL COMMENT '关卡配置id',
  `star` int(11) DEFAULT NULL COMMENT '星级',
  `m_c` int(11) DEFAULT NULL COMMENT '总比赛次数',
  PRIMARY KEY (`team_id`,`id`),
  KEY `i_t_id` (`team_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='球队主线赛程关卡信息';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_u_mmatch_lev_old`
--

DROP TABLE IF EXISTS `t_u_mmatch_lev_old`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_u_mmatch_lev_old` (
  `id` int(11) NOT NULL,
  `team_id` bigint(20) NOT NULL,
  `r_id` int(11) NOT NULL COMMENT '关卡配置id',
  `star_awards` int(11) DEFAULT NULL COMMENT '奖励领取信息',
  PRIMARY KEY (`team_id`,`id`),
  KEY `i_t_id` (`team_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='球队主线赛程关卡信息';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_u_mmatch_node_old`
--

DROP TABLE IF EXISTS `t_u_mmatch_node_old`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_u_mmatch_node_old` (
  `id` int(11) NOT NULL,
  `team_id` bigint(20) NOT NULL,
  `r_id` int(11) NOT NULL COMMENT '节点配置id',
  `star` int(11) DEFAULT NULL COMMENT '星级',
  PRIMARY KEY (`team_id`,`id`),
  KEY `i_t_id` (`team_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='球队主线赛程节点信息';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_u_mmatch_t`
--

DROP TABLE IF EXISTS `t_u_mmatch_t`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_u_mmatch_t` (
  `team_id` bigint(20) NOT NULL,
  `m_num` int(11) DEFAULT NULL COMMENT '当前挑战次数',
  `m_num_time` bigint(20) DEFAULT NULL COMMENT '挑战次数最后更新时间',
  `reg_lev_rid` int(11) DEFAULT NULL COMMENT '常规赛最后一场比赛的关卡',
  `reg_match_time` bigint(20) DEFAULT NULL COMMENT '常规赛最后一场比赛结束时间',
  `cs_time` bigint(20) DEFAULT NULL COMMENT '锦标赛. 当日最后一场比赛开始时间',
  `cs_seed` bigint(20) DEFAULT NULL COMMENT '锦标赛. 随机种子',
  `cs_lev_rid` int(11) DEFAULT NULL COMMENT '锦标赛. 当前关卡',
  `cs_win` int(11) DEFAULT NULL COMMENT '锦标赛. 比赛胜利次数',
  `cs_targets` text COMMENT '锦标赛. 当前对手列表',
  `last_lev_rid` int(11) DEFAULT NULL COMMENT '最后一场比赛的关卡. 用于计算提供的装备经验',
  `last_match_time` bigint(20) DEFAULT NULL COMMENT '最后一场比赛结束时间. 用于计算提供的装备经验',
  PRIMARY KEY (`team_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='球队主线赛程信息';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_u_mmatch_t_old`
--

DROP TABLE IF EXISTS `t_u_mmatch_t_old`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_u_mmatch_t_old` (
  `team_id` bigint(20) NOT NULL,
  `reg_num` int(11) DEFAULT NULL COMMENT '当前常规赛挑战次数',
  `reg_num_time` bigint(20) DEFAULT NULL COMMENT '常规赛挑战次数最后更新时间',
  `e_num` int(11) DEFAULT NULL COMMENT '当前精英赛挑战次数',
  `e_num_time` bigint(20) DEFAULT NULL COMMENT '当前精英赛挑战次数最后更新时间',
  `reg_node_rid` int(11) DEFAULT NULL COMMENT '常规赛最后一场比赛的节点',
  `reg_match_time` bigint(20) DEFAULT NULL COMMENT '常规赛最后一场比赛结束时间',
  `t_time` bigint(20) DEFAULT NULL COMMENT '当日最后一场比赛开始时间',
  `t_seed` bigint(20) DEFAULT NULL COMMENT '随机种子',
  `t_node_rid` int(11) DEFAULT NULL COMMENT '当前节点',
  `t_win` int(11) DEFAULT NULL COMMENT '比赛胜利次数',
  `t_targets` text COMMENT '当前对手列表',
  PRIMARY KEY (`team_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='球队主线赛程信息';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_u_money`
--

DROP TABLE IF EXISTS `t_u_money`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_u_money` (
  `team_id` bigint(20) NOT NULL,
  `money` int(11) DEFAULT NULL,
  `gold` int(11) DEFAULT NULL,
  `exp` int(11) DEFAULT NULL,
  `jsf` int(11) DEFAULT NULL COMMENT '建设费',
  `bd_money` int(11) DEFAULT NULL COMMENT '绑定球券',
  PRIMARY KEY (`team_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='玩家货币数据';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_u_money_old`
--

DROP TABLE IF EXISTS `t_u_money_old`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_u_money_old` (
  `team_id` bigint(20) NOT NULL,
  `money` int(11) DEFAULT NULL,
  `gold` int(11) DEFAULT NULL,
  `exp` int(11) DEFAULT NULL,
  `jsf` int(11) DEFAULT NULL COMMENT '建设费',
  `bd_money` int(11) DEFAULT NULL COMMENT '绑定球券',
  PRIMARY KEY (`team_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='玩家货币数据';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_u_player`
--

DROP TABLE IF EXISTS `t_u_player`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_u_player` (
  `team_id` bigint(20) NOT NULL COMMENT '玩家ID',
  `pid` int(11) NOT NULL COMMENT '球员唯一自增ID',
  `player_id` int(11) NOT NULL COMMENT '球员ID',
  `tid` int(11) DEFAULT '0',
  `price` int(11) NOT NULL COMMENT '球员身价',
  `position` varchar(20) NOT NULL COMMENT '球员选择打的位置',
  `lineup_position` varchar(20) NOT NULL DEFAULT 'NULL' COMMENT '球员场上位置',
  `storage` int(2) DEFAULT NULL COMMENT '0阵容，1仓库',
  `create_time` datetime NOT NULL COMMENT '球员获得时间',
  `bind` tinyint(1) DEFAULT NULL COMMENT '是否绑定',
  PRIMARY KEY (`team_id`,`pid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_u_player_bid`
--

DROP TABLE IF EXISTS `t_u_player_bid`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_u_player_bid` (
  `team_id` bigint(20) NOT NULL,
  `group` int(11) DEFAULT NULL,
  `position` int(11) DEFAULT NULL,
  `grade` varchar(50) DEFAULT NULL,
  `max_grade` varchar(50) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `end_time` datetime DEFAULT NULL,
  PRIMARY KEY (`team_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 CHECKSUM=1 DELAY_KEY_WRITE=1 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_u_player_grade`
--

DROP TABLE IF EXISTS `t_u_player_grade`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_u_player_grade` (
  `team_id` bigint(20) NOT NULL,
  `player_id` int(11) NOT NULL,
  `grade` int(11) DEFAULT NULL,
  `exp` int(11) DEFAULT NULL,
  `star` int(11) DEFAULT '0',
  `star_grade` int(11) DEFAULT '0',
  PRIMARY KEY (`team_id`,`player_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_u_player_inv`
--

DROP TABLE IF EXISTS `t_u_player_inv`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_u_player_inv` (
  `team_id` bigint(20) NOT NULL,
  `player_id` int(11) NOT NULL,
  `total` int(11) DEFAULT NULL,
  `price` float DEFAULT NULL,
  `buy_time` datetime DEFAULT NULL,
  PRIMARY KEY (`team_id`,`player_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_u_player_inv_team`
--

DROP TABLE IF EXISTS `t_u_player_inv_team`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_u_player_inv_team` (
  `team_id` bigint(20) NOT NULL,
  `money` int(11) DEFAULT NULL,
  `max_total` int(11) DEFAULT NULL,
  PRIMARY KEY (`team_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_u_player_lower_salary_log`
--

DROP TABLE IF EXISTS `t_u_player_lower_salary_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_u_player_source`
--

DROP TABLE IF EXISTS `t_u_player_source`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_u_player_source` (
  `team_id` bigint(20) NOT NULL,
  `player_id` int(11) NOT NULL,
  `pts` int(11) DEFAULT NULL,
  `reb` int(11) DEFAULT NULL,
  `ast` int(11) DEFAULT NULL,
  `stl` int(11) DEFAULT NULL,
  `blk` int(11) DEFAULT NULL,
  `to` int(11) DEFAULT NULL,
  `pf` int(11) DEFAULT NULL,
  `fgm` int(11) DEFAULT NULL,
  `fga` int(11) DEFAULT NULL,
  `fta` int(11) DEFAULT NULL,
  `ftm` int(11) DEFAULT NULL,
  `pa3` int(11) DEFAULT NULL,
  `pm3` int(11) DEFAULT NULL,
  `total` int(11) DEFAULT NULL,
  PRIMARY KEY (`team_id`,`player_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_u_player_talent`
--

DROP TABLE IF EXISTS `t_u_player_talent`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_u_player_talent` (
  `team_id` bigint(20) NOT NULL,
  `id` int(11) NOT NULL,
  `player_id` int(11) DEFAULT NULL,
  `df` int(11) DEFAULT NULL,
  `zg` int(11) DEFAULT NULL,
  `lb` int(11) DEFAULT NULL,
  `qd` int(11) DEFAULT NULL,
  `gm` int(11) DEFAULT NULL,
  `tlmz` int(11) DEFAULT NULL,
  `fqmz` int(11) DEFAULT NULL,
  `sfmz` int(11) DEFAULT NULL,
  PRIMARY KEY (`team_id`,`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_u_prop`
--

DROP TABLE IF EXISTS `t_u_prop`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_u_prop` (
  `team_id` bigint(20) NOT NULL,
  `id` int(11) NOT NULL,
  `pid` int(11) DEFAULT NULL,
  `num` int(11) DEFAULT NULL,
  `config` varchar(100) DEFAULT '0',
  `end_time` datetime DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`team_id`,`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_u_r_arena`
--

DROP TABLE IF EXISTS `t_u_r_arena`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_u_r_arena` (
  `team_id` bigint(20) NOT NULL,
  `rank` int(11) DEFAULT NULL COMMENT '竞技场排名. 越小越大',
  `max_rank` int(11) DEFAULT NULL COMMENT '历史最大排名. 越小越大',
  `match_time` bigint(20) DEFAULT NULL COMMENT '上次比赛时间',
  `last_rank` int(11) DEFAULT NULL COMMENT '最后一次结算时的排名',
  `t_m_c` bigint(20) DEFAULT NULL COMMENT '总比赛场数',
  `t_w_c` bigint(20) DEFAULT NULL COMMENT '总胜利场数',
  PRIMARY KEY (`team_id`),
  KEY `i_rank` (`rank`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='竞技场. 个人竞技场';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_u_rmatch_s_his`
--

DROP TABLE IF EXISTS `t_u_rmatch_s_his`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_u_rmatch_s_his` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `team_id` bigint(20) NOT NULL COMMENT '球队id',
  `s_id` int(11) NOT NULL COMMENT '赛季id',
  `tier` int(11) DEFAULT NULL COMMENT '段位层级',
  `rank` int(11) DEFAULT NULL COMMENT '段位排名',
  `rating` int(11) DEFAULT NULL COMMENT '评分',
  `m_c` int(11) DEFAULT NULL COMMENT '参与比赛次数',
  `w_c` int(11) DEFAULT NULL COMMENT '胜利次数',
  `w_s_max` int(11) DEFAULT NULL COMMENT '最大连胜次数',
  `t_m_c` int(11) DEFAULT NULL COMMENT '总参与比赛次数',
  `w_s` int(11) DEFAULT NULL COMMENT '当前连胜次数',
  `c_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '日志生成时间',
  PRIMARY KEY (`id`),
  KEY `i_t_id` (`team_id`),
  KEY `i_s_id` (`s_id`)
) ENGINE=InnoDB AUTO_INCREMENT=106 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='球队跨服天梯赛历史信息';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_u_rmatch_t`
--

DROP TABLE IF EXISTS `t_u_rmatch_t`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_u_rmatch_t` (
  `team_id` bigint(20) NOT NULL COMMENT '球队id',
  `node_name` varchar(500) NOT NULL COMMENT 'node name',
  `s_id` int(11) NOT NULL COMMENT '本赛季. 赛季id',
  `tier` int(11) DEFAULT NULL COMMENT '段位层级',
  `rank` int(11) DEFAULT NULL COMMENT '段位排名',
  `rating` int(11) DEFAULT NULL COMMENT '评分',
  `m_c` int(11) DEFAULT NULL COMMENT '参与比赛次数',
  `w_c` int(11) DEFAULT NULL COMMENT '胜利次数',
  `w_s_max` int(11) DEFAULT NULL COMMENT '最大连胜次数',
  `pre_s_id` int(11) NOT NULL COMMENT '上赛季. 赛季id',
  `pre_tier` int(11) DEFAULT NULL COMMENT '段位层级',
  `pre_rank` int(11) DEFAULT NULL COMMENT '段位排名',
  `pre_rating` int(11) DEFAULT NULL COMMENT '评分',
  `pre_m_c` int(11) DEFAULT NULL COMMENT '参与比赛次数',
  `pre_w_c` int(11) DEFAULT NULL COMMENT '胜利次数',
  `pre_w_s_max` int(11) DEFAULT NULL COMMENT '最大连胜次数',
  `first_award` bigint(20) DEFAULT NULL COMMENT '首次奖励领取状态',
  `t_m_c` int(11) DEFAULT NULL COMMENT '总比赛次数',
  `t_w_c` int(11) DEFAULT NULL COMMENT '总比赛胜利次数',
  `w_s` int(11) DEFAULT NULL COMMENT '当前连胜次数',
  `last_time` bigint(20) DEFAULT NULL COMMENT '最后比赛结束时间',
  PRIMARY KEY (`team_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='球队跨服天梯赛信息';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_u_sign`
--

DROP TABLE IF EXISTS `t_u_sign`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_u_sign` (
  `type` int(2) NOT NULL,
  `period` int(11) NOT NULL COMMENT '开服月数',
  `team_id` bigint(20) NOT NULL,
  `sign_num` int(11) DEFAULT NULL COMMENT '签到次数',
  `patch_num` int(11) DEFAULT NULL COMMENT '补签次数',
  `total_sign` int(11) DEFAULT NULL COMMENT '总次数',
  `total_patch` int(11) DEFAULT NULL COMMENT '总补签',
  `status` varchar(100) DEFAULT NULL,
  `last_sign_time` timestamp NULL DEFAULT NULL COMMENT '最后签到时间',
  PRIMARY KEY (`type`,`team_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_u_skill`
--

DROP TABLE IF EXISTS `t_u_skill`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_u_skill` (
  `team_id` bigint(20) NOT NULL,
  `player_id` int(11) NOT NULL,
  `attack` int(11) DEFAULT NULL,
  `defend` int(11) DEFAULT NULL,
  `step1` varchar(100) COLLATE utf8_general_mysql500_ci DEFAULT NULL,
  `step2` varchar(100) COLLATE utf8_general_mysql500_ci DEFAULT NULL,
  `step3` varchar(100) COLLATE utf8_general_mysql500_ci DEFAULT NULL,
  `step4` varchar(100) COLLATE utf8_general_mysql500_ci DEFAULT NULL,
  `step5` varchar(100) COLLATE utf8_general_mysql500_ci DEFAULT NULL,
  `step6` varchar(100) COLLATE utf8_general_mysql500_ci DEFAULT NULL,
  PRIMARY KEY (`team_id`,`player_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_mysql500_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_u_stage`
--

DROP TABLE IF EXISTS `t_u_stage`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_u_stage` (
  `team_id` bigint(11) NOT NULL,
  `scene` int(11) DEFAULT NULL COMMENT '大关卡，赛季',
  `stage_id` int(11) DEFAULT NULL COMMENT '关卡ID',
  `step` int(11) DEFAULT NULL COMMENT '季后赛比分',
  `score` varchar(2400) DEFAULT NULL COMMENT '比分',
  PRIMARY KEY (`team_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_u_starlet_dual_meet`
--

DROP TABLE IF EXISTS `t_u_starlet_dual_meet`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_u_starlet_dual_meet` (
  `team_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '球队id',
  `player_rid` int(11) NOT NULL DEFAULT '0' COMMENT '策划配置id',
  `pts` int(11) NOT NULL DEFAULT '0' COMMENT '得分',
  `reb` int(11) NOT NULL DEFAULT '0' COMMENT '篮板',
  `ast` int(11) NOT NULL DEFAULT '0' COMMENT '助攻',
  `stl` int(11) NOT NULL DEFAULT '0' COMMENT '抢断',
  `blk` int(11) NOT NULL DEFAULT '0' COMMENT '盖帽',
  `to` int(11) NOT NULL DEFAULT '0' COMMENT '失误',
  `pf` int(11) NOT NULL DEFAULT '0' COMMENT '犯规',
  `total` int(11) NOT NULL DEFAULT '0' COMMENT '总上次次数',
  PRIMARY KEY (`team_id`,`player_rid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='新秀对抗赛数据';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_u_starlet_player`
--

DROP TABLE IF EXISTS `t_u_starlet_player`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_u_starlet_player` (
  `team_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '球队ID',
  `player_rid` int(11) NOT NULL DEFAULT '0' COMMENT '策划配置id',
  `lineup_position` varchar(11) NOT NULL COMMENT '位置',
  `cap` int(11) NOT NULL DEFAULT '0' COMMENT '新秀战力',
  PRIMARY KEY (`team_id`,`lineup_position`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='新秀阵容球员';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_u_starlet_rank`
--

DROP TABLE IF EXISTS `t_u_starlet_rank`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_u_starlet_rank` (
  `team_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '球队ID',
  `rank` int(11) NOT NULL DEFAULT '0' COMMENT '排行',
  PRIMARY KEY (`rank`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='新秀排位赛排行榜';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_u_starlet_team_redix`
--

DROP TABLE IF EXISTS `t_u_starlet_team_redix`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_u_starlet_team_redix` (
  `team_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '球队ID',
  `redix_num` int(11) NOT NULL DEFAULT '0' COMMENT '获胜累计基数',
  `card_type` int(11) NOT NULL DEFAULT '0' COMMENT '对应卡组类型',
  PRIMARY KEY (`team_id`,`card_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='球队新秀对抗赛获得新秀卡组基础数据';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_u_street_ball`
--

DROP TABLE IF EXISTS `t_u_street_ball`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_u_street_ball` (
  `team_id` bigint(20) NOT NULL,
  `type_1` int(11) DEFAULT NULL,
  `type_2` int(11) DEFAULT NULL,
  `type_3` int(11) DEFAULT NULL,
  `type_4` int(11) DEFAULT NULL,
  `type_5` int(11) DEFAULT NULL,
  PRIMARY KEY (`team_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_u_tactics`
--

DROP TABLE IF EXISTS `t_u_tactics`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_u_tactics` (
  `team_id` bigint(20) NOT NULL,
  `tid` int(11) NOT NULL,
  `level` int(11) DEFAULT NULL,
  `buff_time` datetime DEFAULT NULL COMMENT '突破结束时间',
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`team_id`,`tid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 CHECKSUM=1 DELAY_KEY_WRITE=1 ROW_FORMAT=DYNAMIC COMMENT='玩家战术数据';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_u_task`
--

DROP TABLE IF EXISTS `t_u_task`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_u_task` (
  `team_id` bigint(20) NOT NULL,
  `tid` int(11) NOT NULL,
  `status` int(11) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`team_id`,`tid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_u_task_condition`
--

DROP TABLE IF EXISTS `t_u_task_condition`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_u_task_condition` (
  `team_id` bigint(20) NOT NULL,
  `tid` int(11) NOT NULL,
  `cid` int(11) NOT NULL,
  `val_int` int(11) DEFAULT NULL,
  `val_str` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`team_id`,`tid`,`cid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_u_team`
--

DROP TABLE IF EXISTS `t_u_team`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_u_team` (
  `team_id` bigint(20) NOT NULL COMMENT '玩家ID',
  `user_id` bigint(20) DEFAULT NULL COMMENT '玩家uid',
  `shard_id` int(11) DEFAULT NULL COMMENT '信息所在区id',
  `level` int(11) DEFAULT NULL COMMENT '玩家等级',
  `name` varchar(50) COLLATE utf8_estonian_ci DEFAULT NULL COMMENT '玩家昵称',
  `logo` varchar(20) COLLATE utf8_estonian_ci NOT NULL COMMENT '玩家头像',
  `title` varchar(20) COLLATE utf8_estonian_ci NOT NULL COMMENT '玩家称号',
  `price` int(11) DEFAULT NULL COMMENT '玩家工资帽',
  `price_count` int(11) DEFAULT NULL COMMENT '工资帽购买次数',
  `lineup_count` int(11) DEFAULT NULL COMMENT '替补位数量',
  `sec_id` int(11) DEFAULT NULL COMMENT '小秘书',
  `help_step` varchar(100) COLLATE utf8_estonian_ci DEFAULT NULL COMMENT '新手引导',
  `task_step` int(11) DEFAULT NULL COMMENT '任务目标ID',
  `user_status` int(11) DEFAULT NULL COMMENT '用户状态，0正常',
  `chat_status` int(11) DEFAULT NULL COMMENT '世界聊天0是正常 1是禁言   ',
  `last_login_time` datetime DEFAULT NULL COMMENT '玩家上一次登录时间',
  `last_offline_time` datetime DEFAULT NULL COMMENT '玩家上一次下线时间',
  `create_time` datetime DEFAULT NULL COMMENT '玩家创建时间',
  PRIMARY KEY (`team_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_estonian_ci CHECKSUM=1 DELAY_KEY_WRITE=1 ROW_FORMAT=DYNAMIC COMMENT='玩家信息';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_u_team_daily`
--

DROP TABLE IF EXISTS `t_u_team_daily`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_u_team_daily` (
  `team_id` bigint(11) NOT NULL DEFAULT '0' COMMENT '球队ID',
  `trade_chat_lm_state` tinyint(2) NOT NULL DEFAULT '0' COMMENT '每日交易留言状态（0:未留言, 1:已留言）',
  `delete_flag` tinyint(2) NOT NULL DEFAULT '0' COMMENT '是否已删除(0: 未删除，1:已删除）',
  PRIMARY KEY (`team_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='球队每日数据';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_u_team_train`
--

DROP TABLE IF EXISTS `t_u_team_train`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_u_team_train` (
  `team_id` bigint(20) NOT NULL,
  `robbed_count` tinyint(2) NOT NULL DEFAULT '5' COMMENT '剩余抢夺次数',
  `refresh_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '抢夺次数最后刷新时间',
  `robbed_total_count` int(11) NOT NULL DEFAULT '0' COMMENT '训练馆抢夺总次数',
  `robbed_win_count` int(6) NOT NULL COMMENT '训练馆抢夺胜利次数',
  `robbed_fail_count` int(6) NOT NULL DEFAULT '0' COMMENT '训练馆抢夺失败次数',
  `train_count` int(11) NOT NULL DEFAULT '0' COMMENT '训练馆训练次数',
  PRIMARY KEY (`team_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='球队训练数据';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_u_trade`
--

DROP TABLE IF EXISTS `t_u_trade`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_u_trade` (
  `id` bigint(20) NOT NULL,
  `team_id` bigint(20) DEFAULT NULL,
  `pid` int(11) DEFAULT NULL,
  `player_id` int(11) DEFAULT NULL,
  `position` varchar(10) DEFAULT NULL,
  `price` int(11) DEFAULT NULL,
  `market_price` int(11) DEFAULT NULL,
  `money` int(11) DEFAULT NULL,
  `talent` varchar(60) DEFAULT NULL,
  `status` int(11) DEFAULT NULL COMMENT '0是上架状态； 1自行下架；	2交易完成',
  `buy_team` bigint(20) DEFAULT NULL COMMENT '购买者ID',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `end_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `deal_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '交易时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_u_trade_p2p`
--

DROP TABLE IF EXISTS `t_u_trade_p2p`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_u_trade_p2p` (
  `id` bigint(20) NOT NULL,
  `team_id` bigint(20) DEFAULT NULL,
  `player_id` int(11) DEFAULT NULL,
  `position` varchar(10) DEFAULT NULL,
  `price` int(11) DEFAULT NULL COMMENT '最大工资',
  `market_price` int(11) DEFAULT NULL,
  `money` int(11) DEFAULT NULL COMMENT '求购球券',
  `talent` int(11) DEFAULT NULL COMMENT '最低天赋',
  `status` int(11) DEFAULT NULL COMMENT '0是上架状态； 1自行下架；	2交易完成',
  `buy_team` bigint(20) DEFAULT NULL COMMENT '购买者ID',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `end_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `deal_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '交易时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_u_train`
--

DROP TABLE IF EXISTS `t_u_train`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_u_train` (
  `train_id` int(12) NOT NULL DEFAULT '0' COMMENT '训练馆唯一ID',
  `team_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '训练馆球队ID',
  `train_level` smallint(4) NOT NULL DEFAULT '1' COMMENT '训练馆等级',
  `train_exp` int(11) NOT NULL DEFAULT '0' COMMENT 'trainLevel',
  `type` tinyint(2) NOT NULL DEFAULT '0' COMMENT '训练类型 (1:默认4小时,2:8小时) 结算奖励',
  `player_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '球员唯一ID',
  `player_rid` int(11) NOT NULL DEFAULT '0' COMMENT '玩家基础ID(主要用于NPC玩家查询名字)',
  `start_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '训练开始时间',
  `player_cap` int(11) NOT NULL DEFAULT '0' COMMENT '训练球员攻防',
  `reward_state` tinyint(2) NOT NULL DEFAULT '0' COMMENT '奖励领取状态(0:未领取, 1:已领取)',
  `robbed_num` int(6) NOT NULL DEFAULT '0' COMMENT ' 被抢夺技能点',
  `is_league` tinyint(2) NOT NULL DEFAULT '0' COMMENT '训练位 (0:个人位,1:联盟位)',
  `train_hour` tinyint(2) NOT NULL DEFAULT '0' COMMENT '训练总时间(联盟训练馆结算)',
  `clear` tinyint(2) NOT NULL DEFAULT '0' COMMENT '联盟训练馆清理标识(1:清理)',
  `bl_id` smallint(6) NOT NULL DEFAULT '0' COMMENT '联盟训练馆基础ID',
  PRIMARY KEY (`train_id`,`team_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='训练馆数据';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_u_train_player`
--

DROP TABLE IF EXISTS `t_u_train_player`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_u_train_player` (
  `team_id` bigint(11) NOT NULL,
  `player_id` int(11) NOT NULL,
  `item` int(11) DEFAULT '1' COMMENT '训练项',
  `step` int(11) DEFAULT NULL COMMENT '小星脉个数',
  `step_count` int(11) DEFAULT NULL COMMENT '总步数',
  PRIMARY KEY (`team_id`,`player_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_u_vip`
--

DROP TABLE IF EXISTS `t_u_vip`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_u_vip` (
  `team_id` bigint(20) NOT NULL,
  `add_money` int(11) DEFAULT NULL,
  `exp` int(11) DEFAULT NULL,
  `level` int(11) DEFAULT NULL,
  `start_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `end_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `buy_status` varchar(200) DEFAULT NULL,
  `create_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `update_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`team_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2018-09-08 15:36:33
