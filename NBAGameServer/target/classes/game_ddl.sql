-- MySQL dump 10.16  Distrib 10.1.25-MariaDB, for debian-linux-gnu (x86_64)
--
-- Host: 192.168.10.181    Database: nba_101
-- ------------------------------------------------------
-- Server version	5.5.54-38.6-log


CREATE DATABASE IF NOT EXISTS `nba_101` DEFAULT CHARACTER SET utf8;
USE `nba_101`;

-- Table structure for table `t_c_config`
DROP TABLE IF EXISTS `t_c_config`;
CREATE TABLE `t_c_config` (
  `key` varchar(100) NOT NULL COMMENT 'Key值',
  `value` varchar(200) DEFAULT NULL COMMENT '对应数据',
  `description` varchar(200) DEFAULT NULL COMMENT '描述',
  PRIMARY KEY (`key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 CHECKSUM=1 DELAY_KEY_WRITE=1 ROW_FORMAT=DYNAMIC COMMENT='游戏配置表';

-- Table structure for table `t_c_drop`
DROP TABLE IF EXISTS `t_c_drop`;
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

-- Table structure for table `t_c_player_money`
DROP TABLE IF EXISTS `t_c_player_money`;
CREATE TABLE `t_c_player_money` (
  `player_id` int(11) NOT NULL,
  `price` int(11) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`player_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 CHECKSUM=1 DELAY_KEY_WRITE=1 ROW_FORMAT=DYNAMIC COMMENT='球员底薪';

-- Table structure for table `t_c_prop`
DROP TABLE IF EXISTS `t_c_prop`;
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

-- Table structure for table `t_u_active_data`
DROP TABLE IF EXISTS `t_u_active_data`;
CREATE TABLE `t_u_active_data` (
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

-- Table structure for table `t_u_arena`
DROP TABLE IF EXISTS `t_u_arena`;
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

-- Table structure for table `t_u_arena_player`
DROP TABLE IF EXISTS `t_u_arena_player`;
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

-- Table structure for table `t_u_arena_team`
DROP TABLE IF EXISTS `t_u_arena_team`;
CREATE TABLE `t_u_arena_team` (
  `team_id` bigint(20) NOT NULL,
  `gold` int(11) DEFAULT NULL,
  `power` int(11) DEFAULT NULL,
  `defend` int(11) DEFAULT NULL,
  `level` int(11) DEFAULT NULL,
  `last_update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`team_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 CHECKSUM=1 DELAY_KEY_WRITE=1 ROW_FORMAT=DYNAMIC COMMENT='球馆玩家信息';

-- Table structure for table `t_u_battle`
DROP TABLE IF EXISTS `t_u_battle`;
CREATE TABLE `t_u_battle` (
  `battle_id` bigint(20) NOT NULL  COMMENT '比赛id',
  `battle_type` int(11) DEFAULT NULL COMMENT '比赛类型',
  `home_team_id` bigint(20) DEFAULT NULL COMMENT '主场球队id',
  `home_team_name` varchar(100) DEFAULT NULL COMMENT '主场球队名称',
  `home_score` int(11) DEFAULT NULL COMMENT '主场得分',
  `away_team_id` bigint(20) DEFAULT NULL COMMENT '客场球队id',
  `away_team_name` varchar(100) DEFAULT NULL COMMENT '客场球队名称',
  `away_score` int(11) DEFAULT NULL COMMENT '客场得分',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间(比赛结束时间)',
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
  key `i_bt` (`battle_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 CHECKSUM=1 DELAY_KEY_WRITE=1 ROW_FORMAT=DYNAMIC COMMENT='比赛数据';

-- Table structure for table `t_u_besign`
DROP TABLE IF EXISTS `t_u_besign`;
CREATE TABLE `t_u_besign` (
  `id` int(11) NOT NULL,
  `team_id` bigint(20) NOT NULL,
  `player_id` int(11) DEFAULT NULL,
  `price` int(11) DEFAULT NULL,
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  PRIMARY KEY (`id`,`team_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Table structure for table `t_u_buff`
DROP TABLE IF EXISTS `t_u_buff`;
CREATE TABLE `t_u_buff` (
  `id` int(11) NOT NULL,
  `team_id` bigint(20) NOT NULL,
  `params` varchar(200) DEFAULT NULL,
  `end_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `create_time` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`,`team_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Table structure for table `t_u_card`
DROP TABLE IF EXISTS `t_u_card`;
CREATE TABLE `t_u_card` (
  `id` int(11) NOT NULL,
  `team_id` bigint(20) DEFAULT NULL,
  `player_id` int(11) DEFAULT NULL,
  `grade` int(2) DEFAULT NULL,
  `starlv` int(2) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Table structure for table `t_u_card_col`
DROP TABLE IF EXISTS `t_u_card_col`;
CREATE TABLE `t_u_card_col` (
  `team_id` bigint(20) NOT NULL,
  `player_id` int(11) NOT NULL,
  `grade` int(2) DEFAULT NULL,
  `starlv` int(2) DEFAULT NULL,
  `exp` int(11) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`team_id`,`player_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Table structure for table `t_u_coach`
DROP TABLE IF EXISTS `t_u_coach`;
CREATE TABLE `t_u_coach` (
  `team_id` bigint(20) NOT NULL,
  `cid` int(11) NOT NULL,
  `tid` int(11) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`team_id`,`cid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Table structure for table `t_u_custom_guess`
DROP TABLE IF EXISTS `t_u_custom_guess`;
CREATE TABLE `t_u_custom_guess` (
  `team_id` bigint(20) NOT NULL,
  `room_id` int(11) NOT NULL,
  `money_A` int(11) DEFAULT NULL,
  `money_B` int(11) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`team_id`,`room_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Table structure for table `t_u_custom_team`
DROP TABLE IF EXISTS `t_u_custom_team`;
CREATE TABLE `t_u_custom_team` (
  `team_id` bigint(20) NOT NULL,
  `money` int(11) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`team_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 CHECKSUM=1 DELAY_KEY_WRITE=1 ROW_FORMAT=DYNAMIC COMMENT='擂台赛玩家信息';

-- Table structure for table `t_u_email`
DROP TABLE IF EXISTS `t_u_email`;
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

-- Table structure for table `t_u_email_to_team`
DROP TABLE IF EXISTS `t_u_email_to_team`;
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

-- Table structure for table `t_u_equi`
DROP TABLE IF EXISTS `t_u_equi`;
CREATE TABLE `t_u_equi` (
  `id` int(11) NOT NULL COMMENT '序列ID,套装',
  `type` int(11) NOT NULL COMMENT '类型',
  `team_id` bigint(11) NOT NULL COMMENT '球队ID',
  `equi_id` int(11) DEFAULT NULL COMMENT '模板ID',
  `player_id` int(11) DEFAULT NULL COMMENT '装备球员id类型',
  `equi_team` int(11) DEFAULT NULL COMMENT '装备所属球队，0是所有球队可用',
  `lv` int(11) DEFAULT NULL COMMENT '等级',
  `qua_exp` int(11) DEFAULT NULL COMMENT '进阶经验',
  `exp` int(11) DEFAULT NULL COMMENT '总经验',
  `strlv` int(11) DEFAULT NULL COMMENT '强化等级',
  `str_bless` int(11) DEFAULT '0' COMMENT '强化祝福值',
  `qua_bless` int(11) DEFAULT '0' COMMENT '进阶祝福值',
  `create_time` datetime DEFAULT NULL,
  `end_time` datetime DEFAULT NULL COMMENT '有效时间',
  PRIMARY KEY (`id`,`type`,`team_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Table structure for table `t_u_friend`
DROP TABLE IF EXISTS `t_u_friend`;
CREATE TABLE `t_u_friend` (
  `team_id` bigint(20) NOT NULL,
  `friend_team_id` bigint(20) NOT NULL,
  `type` tinyint(4) DEFAULT '1' COMMENT '1好友 0黑名单',
  `remark` varchar(100) DEFAULT NULL COMMENT '好友备注',
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`team_id`,`friend_team_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Table structure for table `t_u_league`
DROP TABLE IF EXISTS `t_u_league`;
CREATE TABLE `t_u_league` (
  `league_id` int(11) NOT NULL DEFAULT '0',
  `league_name` varchar(100) DEFAULT NULL,
  `logo` varchar(50) DEFAULT NULL,
  `league_level` int(11) DEFAULT NULL,
  `honor` int(11) DEFAULT NULL,
  `team_name` varchar(50) DEFAULT NULL,
  `league_tip` varchar(200) DEFAULT NULL,
  `league_notice` varchar(500) DEFAULT NULL,
  `people_count` int(11) DEFAULT NULL,
  `limit_shop` int(11) DEFAULT NULL,
  `limit_honor` int(11) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`league_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 CHECKSUM=1 DELAY_KEY_WRITE=1 ROW_FORMAT=DYNAMIC COMMENT='联盟';

-- Table structure for table `t_u_league_honor`
DROP TABLE IF EXISTS `t_u_league_honor`;
CREATE TABLE `t_u_league_honor` (
  `league_id` int(11) NOT NULL,
  `honor_id` int(11) NOT NULL,
  `level` int(11) DEFAULT NULL,
  `end_time` datetime DEFAULT NULL,
  PRIMARY KEY (`league_id`,`honor_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Table structure for table `t_u_league_honor_pool`
DROP TABLE IF EXISTS `t_u_league_honor_pool`;
CREATE TABLE `t_u_league_honor_pool` (
  `league_id` int(11) NOT NULL,
  `prop_id` int(11) NOT NULL,
  `num` int(11) DEFAULT NULL,
  PRIMARY KEY (`league_id`,`prop_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 CHECKSUM=1 DELAY_KEY_WRITE=1 ROW_FORMAT=DYNAMIC;

-- Table structure for table `t_u_league_team`
DROP TABLE IF EXISTS `t_u_league_team`;
CREATE TABLE `t_u_league_team` (
  `league_id` int(11) NOT NULL,
  `team_id` bigint(20) NOT NULL,
  `level` int(11) DEFAULT NULL,
  `score` int(11) DEFAULT NULL,
  `feats` int(11) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`team_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 CHECKSUM=1 DELAY_KEY_WRITE=1 ROW_FORMAT=DYNAMIC;

-- Table structure for table `t_u_logo`
DROP TABLE IF EXISTS `t_u_logo`;
CREATE TABLE `t_u_logo` (
  `id` int(11) NOT NULL COMMENT 'id',
  `team_id` bigint(20) DEFAULT NULL COMMENT '球队',
  `player_id` int(11) DEFAULT NULL COMMENT '球员',
  `quality` int(11) DEFAULT NULL COMMENT '品质',
  `create_time` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Table structure for table `t_u_logo_player`
DROP TABLE IF EXISTS `t_u_logo_player`;
CREATE TABLE `t_u_logo_player` (
  `team_id` bigint(11) NOT NULL,
  `player_id` int(11) NOT NULL,
  `logo_id` int(11) DEFAULT NULL COMMENT '头像ID',
  `lv` int(11) DEFAULT NULL COMMENT '荣誉等级',
  `starLv` int(11) DEFAULT NULL COMMENT '大星脉个数',
  `step` int(11) DEFAULT NULL COMMENT '小星脉个数',
  PRIMARY KEY (`team_id`,`player_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Table structure for table `t_u_match`
DROP TABLE IF EXISTS `t_u_match`;
CREATE TABLE `t_u_match` (
  `seq_id` int(11) NOT NULL COMMENT '届数',
  `match_id` int(2) NOT NULL COMMENT '多人赛类型',
  `status` int(2) DEFAULT NULL COMMENT '开始报名(1), 报名截止(4), 比赛中(2), 结束(3)',
  `round` int(11) DEFAULT NULL COMMENT '当前轮数',
  `max_round` int(11) DEFAULT NULL COMMENT '最大轮数',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  `match_time` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`seq_id`,`match_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Table structure for table `t_u_match_best`
DROP TABLE IF EXISTS `t_u_match_best`;
CREATE TABLE `t_u_match_best` (
  `match_id` int(11) NOT NULL,
  `team_id` bigint(20) NOT NULL,
  `rank` int(11) DEFAULT NULL,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`match_id`,`team_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Table structure for table `t_u_match_pk`
DROP TABLE IF EXISTS `t_u_match_pk`;
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

-- Table structure for table `t_u_match_sign`
DROP TABLE IF EXISTS `t_u_match_sign`;
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

-- Table structure for table `t_u_money`
DROP TABLE IF EXISTS `t_u_money`;
CREATE TABLE `t_u_money` (
  `team_id` bigint(20) NOT NULL,
  `money` int(11) DEFAULT NULL,
  `gold` int(11) DEFAULT NULL,
  `exp` int(11) DEFAULT NULL,
  `jsf` int(11) DEFAULT NULL COMMENT '建设费',
  `bd_money` int(11) DEFAULT NULL COMMENT '绑定球券',
  PRIMARY KEY (`team_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 CHECKSUM=1 DELAY_KEY_WRITE=1 ROW_FORMAT=DYNAMIC COMMENT='玩家货币数据';

-- Table structure for table `t_u_player_`
DROP TABLE IF EXISTS `t_u_player_`;
CREATE TABLE `t_u_player_` (
  `team_id` bigint(20) NOT NULL COMMENT '玩家ID',
  `pid` int(11) NOT NULL COMMENT '球员唯一自增ID',
  `player_id` int(11) NOT NULL COMMENT '球员ID',
  `price` int(11) NOT NULL COMMENT '球员身价',
  `position` varchar(20) NOT NULL COMMENT '球员选择打的位置',
  `lineup_position` varchar(20) NOT NULL DEFAULT 'NULL' COMMENT '球员场上位置',
  `storage` int(2) DEFAULT NULL COMMENT '0阵容，1仓库',
  `create_time` datetime NOT NULL COMMENT '球员获得时间',
  PRIMARY KEY (`team_id`,`pid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 CHECKSUM=1 DELAY_KEY_WRITE=1 ROW_FORMAT=DYNAMIC COMMENT='玩家球员数据';

-- Table structure for table `t_u_player_grade`
DROP TABLE IF EXISTS `t_u_player_grade`;
CREATE TABLE `t_u_player_grade` (
  `team_id` bigint(20) NOT NULL,
  `player_id` int(11) NOT NULL,
  `grade` int(11) DEFAULT NULL,
  `exp` int(11) DEFAULT NULL,
  `star` int(11) DEFAULT '0',
  `star_grade` int(11) DEFAULT '0',
  PRIMARY KEY (`team_id`,`player_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Table structure for table `t_u_prop_`
DROP TABLE IF EXISTS `t_u_prop_`;
CREATE TABLE `t_u_prop_` (
  `team_id` bigint(20) NOT NULL,
  `id` int(11) NOT NULL,
  `pid` int(11) DEFAULT NULL,
  `num` int(11) DEFAULT NULL,
  `config` varchar(100) DEFAULT '0',
  `end_time` datetime DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`team_id`,`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 CHECKSUM=1 DELAY_KEY_WRITE=1 ROW_FORMAT=DYNAMIC COMMENT='玩家道具数据';

-- Table structure for table `t_u_sign`
DROP TABLE IF EXISTS `t_u_sign`;
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

-- Table structure for table `t_u_skill`
DROP TABLE IF EXISTS `t_u_skill`;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_mysql500_ci CHECKSUM=1 DELAY_KEY_WRITE=1 ROW_FORMAT=DYNAMIC COMMENT='技能数据';

-- Table structure for table `t_u_stage`
DROP TABLE IF EXISTS `t_u_stage`;
CREATE TABLE `t_u_stage` (
  `team_id` bigint(11) NOT NULL,
  `scene` int(11) DEFAULT NULL COMMENT '大关卡，赛季',
  `stage_id` int(11) DEFAULT NULL COMMENT '关卡ID',
  `step` int(11) DEFAULT NULL COMMENT '季后赛比分',
  `score` varchar(2400) DEFAULT NULL COMMENT '比分',
  PRIMARY KEY (`team_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Table structure for table `t_u_street_ball`
DROP TABLE IF EXISTS `t_u_street_ball`;
CREATE TABLE `t_u_street_ball` (
  `team_id` bigint(20) NOT NULL,
  `type_1` int(11) DEFAULT NULL,
  `type_2` int(11) DEFAULT NULL,
  `type_3` int(11) DEFAULT NULL,
  `type_4` int(11) DEFAULT NULL,
  `type_5` int(11) DEFAULT NULL,
  PRIMARY KEY (`team_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Table structure for table `t_u_tactics`
DROP TABLE IF EXISTS `t_u_tactics`;
CREATE TABLE `t_u_tactics` (
  `team_id` bigint(20) NOT NULL,
  `tid` int(11) NOT NULL,
  `level` int(11) DEFAULT NULL,
  `buff_time` datetime DEFAULT NULL COMMENT '突破结束时间',
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`team_id`,`tid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 CHECKSUM=1 DELAY_KEY_WRITE=1 ROW_FORMAT=DYNAMIC COMMENT='玩家战术数据';

-- Table structure for table `t_u_task`
DROP TABLE IF EXISTS `t_u_task`;
CREATE TABLE `t_u_task` (
  `team_id` bigint(20) NOT NULL,
  `tid` int(11) NOT NULL,
  `status` int(11) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`team_id`,`tid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Table structure for table `t_u_task_condition`
DROP TABLE IF EXISTS `t_u_task_condition`;
CREATE TABLE `t_u_task_condition` (
  `team_id` bigint(20) NOT NULL,
  `tid` int(11) NOT NULL,
  `cid` int(11) NOT NULL,
  `val_int` int(11) DEFAULT NULL,
  `val_str` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`team_id`,`tid`,`cid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 CHECKSUM=1 DELAY_KEY_WRITE=1 ROW_FORMAT=DYNAMIC COMMENT='任务条件';

-- Table structure for table `t_u_team`
DROP TABLE IF EXISTS `t_u_team`;
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
  `last_login_time` datetime DEFAULT NULL COMMENT '玩家上一次登录时间',
  `create_time` datetime DEFAULT NULL COMMENT '玩家创建时间',
  PRIMARY KEY (`team_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_estonian_ci CHECKSUM=1 DELAY_KEY_WRITE=1 ROW_FORMAT=DYNAMIC COMMENT='玩家信息';

-- Table structure for table `t_u_trade`
DROP TABLE IF EXISTS `t_u_trade`;
CREATE TABLE `t_u_trade` (
  `id` int(11) NOT NULL,
  `team_id` bigint(20) DEFAULT NULL,
  `pid` int(11) DEFAULT NULL,
  `player_id` int(11) DEFAULT NULL,
  `position` varchar(10) DEFAULT NULL,
  `price` int(11) DEFAULT NULL,
  `market_price` int(11) DEFAULT NULL,
  `money` int(11) DEFAULT NULL,
  `status` int(11) DEFAULT NULL COMMENT '0是上架状态； 1自行下架；	2交易完成',
  `buy_team` bigint(20) DEFAULT NULL COMMENT '购买者ID',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `end_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `deal_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '交易时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Table structure for table `t_u_train`
DROP TABLE IF EXISTS `t_u_train`;
CREATE TABLE `t_u_train` (
  `id` int(11) NOT NULL,
  `team_id` bigint(20) DEFAULT NULL,
  `name` varchar(50) DEFAULT NULL,
  `power` int(11) DEFAULT NULL,
  `league_id` int(11) DEFAULT NULL,
  `league_name` varchar(50) DEFAULT NULL,
  `logo` varchar(20) DEFAULT NULL,
  `level` int(11) DEFAULT NULL,
  `dec_count` int(11) DEFAULT NULL,
  `start_time` datetime DEFAULT NULL,
  `end_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 CHECKSUM=1 DELAY_KEY_WRITE=1 ROW_FORMAT=DYNAMIC COMMENT='训练馆位置';

-- Table structure for table `t_u_train_player`
DROP TABLE IF EXISTS `t_u_train_player`;
CREATE TABLE `t_u_train_player` (
  `team_id` bigint(11) NOT NULL,
  `player_id` int(11) NOT NULL,
  `item` int(11) DEFAULT '1' COMMENT '训练项',
  `step` int(11) DEFAULT NULL COMMENT '小星脉个数',
  `step_count` int(11) DEFAULT NULL COMMENT '总步数',
  PRIMARY KEY (`team_id`,`player_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Table structure for table `t_u_vip`
DROP TABLE IF EXISTS `t_u_vip`;
CREATE TABLE `t_u_vip` (
  `team_id` bigint(20) NOT NULL,
  `add_money` int(11) DEFAULT NULL,
  `level` int(11) DEFAULT NULL,
  `start_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `end_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `buy_status` varchar(200) DEFAULT NULL,
  `create_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `update_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`team_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `t_u_mmatch_t`;
CREATE TABLE `t_u_mmatch_t` (
  `team_id` bigint NOT NULL,
  `m_num` int DEFAULT null COMMENT '当前挑战次数',
  `m_num_time` bigint DEFAULT null COMMENT '挑战次数最后更新时间',

  `last_lev_rid` int DEFAULT null COMMENT '最后一场比赛的关卡. 用于计算提供的装备经验',
  `last_match_time` bigint DEFAULT null COMMENT '最后一场比赛结束时间. 用于计算提供的装备经验',

  `cs_time` bigint DEFAULT null COMMENT '锦标赛. 当日最后一场比赛开始时间',
  `cs_seed` bigint DEFAULT null COMMENT '锦标赛. 随机种子',
  `cs_lev_rid` int DEFAULT null COMMENT '锦标赛. 当前关卡',
  `cs_win` int DEFAULT null COMMENT '锦标赛. 比赛胜利次数',
  `cs_targets` text DEFAULT null COMMENT '锦标赛. 当前对手列表',

  PRIMARY KEY (`team_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='球队主线赛程信息';

DROP TABLE IF EXISTS `t_u_mmatch_div`;
CREATE TABLE `t_u_mmatch_div` (
  `id` int NOT NULL,
  `team_id` bigint NOT NULL,
  `r_id` int not null COMMENT '赛区配置id',
  `star_awards` int DEFAULT null COMMENT '奖励领取信息',

  PRIMARY KEY (`team_id`, `id`),
  key `i_t_id` (`team_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='球队主线赛程赛区信息';

DROP TABLE IF EXISTS `t_u_mmatch_lev`;
CREATE TABLE `t_u_mmatch_lev` (
  `id` int NOT NULL,
  `team_id` bigint NOT NULL,
  `r_id` int not null COMMENT '关卡配置id',
  `star` int DEFAULT null COMMENT '星级',
  `m_c` int DEFAULT null COMMENT '总比赛次数',

  PRIMARY KEY (`team_id`, `id`),
  key `i_t_id` (`team_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='球队主线赛程关卡信息';

