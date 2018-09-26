
DROP TABLE IF EXISTS `t_u_besign`;
CREATE TABLE `t_u_besign` (
  `id` int(11) NOT NULL,
  `team_id` bigint(20) NOT NULL,
  `player_id` int(11) DEFAULT NULL,
  `price` int(11) DEFAULT NULL,
  `tid` int(11) DEFAULT '0',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  PRIMARY KEY (`id`,`team_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

insert into t_u_besign select * from t_u_besign_0;
insert into t_u_besign select * from t_u_besign_1;
insert into t_u_besign select * from t_u_besign_2;
insert into t_u_besign select * from t_u_besign_3;
insert into t_u_besign select * from t_u_besign_4;
insert into t_u_besign select * from t_u_besign_5;
insert into t_u_besign select * from t_u_besign_6;
insert into t_u_besign select * from t_u_besign_7;
insert into t_u_besign select * from t_u_besign_8;
insert into t_u_besign select * from t_u_besign_9;

DROP TABLE if exists `t_u_card`;
CREATE TABLE `t_u_card` (
  `team_id` bigint(20) NOT NULL,
  `player_id` int(11) NOT NULL,
  `type` int(2) NOT NULL,
  `starlv` int(2) DEFAULT NULL,
  `exp` int(11) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`team_id`,`player_id`,`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

insert into t_u_card select * from t_u_card_0;
insert into t_u_card select * from t_u_card_1;
insert into t_u_card select * from t_u_card_2;
insert into t_u_card select * from t_u_card_3;
insert into t_u_card select * from t_u_card_4;
insert into t_u_card select * from t_u_card_5;
insert into t_u_card select * from t_u_card_6;
insert into t_u_card select * from t_u_card_7;
insert into t_u_card select * from t_u_card_8;
insert into t_u_card select * from t_u_card_9;

DROP TABLE if exists `t_u_coach`;
CREATE TABLE `t_u_coach` (
  `team_id` bigint(20) NOT NULL,
  `cid` int(11) NOT NULL,
  `tid` int(11) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`team_id`,`cid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

insert into t_u_coach select * from t_u_coach_0;
insert into t_u_coach select * from t_u_coach_1;
insert into t_u_coach select * from t_u_coach_2;
insert into t_u_coach select * from t_u_coach_3;
insert into t_u_coach select * from t_u_coach_4;
insert into t_u_coach select * from t_u_coach_5;
insert into t_u_coach select * from t_u_coach_6;
insert into t_u_coach select * from t_u_coach_7;
insert into t_u_coach select * from t_u_coach_8;
insert into t_u_coach select * from t_u_coach_9;

DROP TABLE if exists `t_u_email`;
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

insert into t_u_email select * from t_u_email_0;
insert into t_u_email select * from t_u_email_1;
insert into t_u_email select * from t_u_email_2;
insert into t_u_email select * from t_u_email_3;
insert into t_u_email select * from t_u_email_4;
insert into t_u_email select * from t_u_email_5;
insert into t_u_email select * from t_u_email_6;
insert into t_u_email select * from t_u_email_7;
insert into t_u_email select * from t_u_email_8;
insert into t_u_email select * from t_u_email_9;

DROP TABLE if exists `t_u_equi`;
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

insert into t_u_equi select * from t_u_equi_0;
insert into t_u_equi select * from t_u_equi_1;
insert into t_u_equi select * from t_u_equi_2;
insert into t_u_equi select * from t_u_equi_3;
insert into t_u_equi select * from t_u_equi_4;
insert into t_u_equi select * from t_u_equi_5;
insert into t_u_equi select * from t_u_equi_6;
insert into t_u_equi select * from t_u_equi_7;
insert into t_u_equi select * from t_u_equi_8;
insert into t_u_equi select * from t_u_equi_9;

DROP TABLE if exists `t_u_friend`;
CREATE TABLE `t_u_friend` (
  `team_id` bigint(20) NOT NULL,
  `friend_team_id` bigint(20) NOT NULL,
  `type` tinyint(4) DEFAULT '1' COMMENT '1好友 0黑名单',
  `remark` varchar(100) DEFAULT NULL COMMENT '好友备注',
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`team_id`,`friend_team_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

insert into t_u_friend select * from t_u_friend_0;
insert into t_u_friend select * from t_u_friend_1;
insert into t_u_friend select * from t_u_friend_2;
insert into t_u_friend select * from t_u_friend_3;
insert into t_u_friend select * from t_u_friend_4;
insert into t_u_friend select * from t_u_friend_5;
insert into t_u_friend select * from t_u_friend_6;
insert into t_u_friend select * from t_u_friend_7;
insert into t_u_friend select * from t_u_friend_8;
insert into t_u_friend select * from t_u_friend_9;

DROP TABLE if exists `t_u_player`;
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
  PRIMARY KEY (`team_id`,`pid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

insert into t_u_player select * from t_u_player_0;
insert into t_u_player select * from t_u_player_1;
insert into t_u_player select * from t_u_player_2;
insert into t_u_player select * from t_u_player_3;
insert into t_u_player select * from t_u_player_4;
insert into t_u_player select * from t_u_player_5;
insert into t_u_player select * from t_u_player_6;
insert into t_u_player select * from t_u_player_7;
insert into t_u_player select * from t_u_player_8;
insert into t_u_player select * from t_u_player_9;

DROP TABLE if exists `t_u_player_grade`;
CREATE TABLE `t_u_player_grade` (
  `team_id` bigint(20) NOT NULL,
  `player_id` int(11) NOT NULL,
  `grade` int(11) DEFAULT NULL,
  `exp` int(11) DEFAULT NULL,
  `star` int(11) DEFAULT '0',
  `star_grade` int(11) DEFAULT '0',
  PRIMARY KEY (`team_id`,`player_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

insert into t_u_player_grade select * from t_u_player_grade_0;
insert into t_u_player_grade select * from t_u_player_grade_1;
insert into t_u_player_grade select * from t_u_player_grade_2;
insert into t_u_player_grade select * from t_u_player_grade_3;
insert into t_u_player_grade select * from t_u_player_grade_4;
insert into t_u_player_grade select * from t_u_player_grade_5;
insert into t_u_player_grade select * from t_u_player_grade_6;
insert into t_u_player_grade select * from t_u_player_grade_7;
insert into t_u_player_grade select * from t_u_player_grade_8;
insert into t_u_player_grade select * from t_u_player_grade_9;

DROP TABLE if exists `t_u_player_inv`;
CREATE TABLE `t_u_player_inv` (
  `team_id` bigint(20) NOT NULL,
  `player_id` int(11) NOT NULL,
  `total` int(11) DEFAULT NULL,
  `price` float DEFAULT NULL,
  `buy_time` datetime DEFAULT NULL,
  PRIMARY KEY (`team_id`,`player_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

insert into t_u_player_inv select * from t_u_player_inv_0;
insert into t_u_player_inv select * from t_u_player_inv_1;
insert into t_u_player_inv select * from t_u_player_inv_2;
insert into t_u_player_inv select * from t_u_player_inv_3;
insert into t_u_player_inv select * from t_u_player_inv_4;
insert into t_u_player_inv select * from t_u_player_inv_5;
insert into t_u_player_inv select * from t_u_player_inv_6;
insert into t_u_player_inv select * from t_u_player_inv_7;
insert into t_u_player_inv select * from t_u_player_inv_8;
insert into t_u_player_inv select * from t_u_player_inv_9;

DROP TABLE if exists `t_u_player_source`;
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

insert into t_u_player_source select * from t_u_player_source_0;
insert into t_u_player_source select * from t_u_player_source_1;
insert into t_u_player_source select * from t_u_player_source_2;
insert into t_u_player_source select * from t_u_player_source_3;
insert into t_u_player_source select * from t_u_player_source_4;
insert into t_u_player_source select * from t_u_player_source_5;
insert into t_u_player_source select * from t_u_player_source_6;
insert into t_u_player_source select * from t_u_player_source_7;
insert into t_u_player_source select * from t_u_player_source_8;
insert into t_u_player_source select * from t_u_player_source_9;

DROP TABLE if exists `t_u_player_talent`;
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

delete from t_u_player_talent_0 where team_id % 10 <> 0;
delete from t_u_player_talent_1 where team_id % 10 <> 1;
delete from t_u_player_talent_2 where team_id % 10 <> 2;
delete from t_u_player_talent_3 where team_id % 10 <> 3;
delete from t_u_player_talent_4 where team_id % 10 <> 4;
delete from t_u_player_talent_5 where team_id % 10 <> 5;
delete from t_u_player_talent_6 where team_id % 10 <> 6;
delete from t_u_player_talent_7 where team_id % 10 <> 7;
delete from t_u_player_talent_8 where team_id % 10 <> 8;
delete from t_u_player_talent_9 where team_id % 10 <> 9;

insert into t_u_player_talent select * from t_u_player_talent_0;
insert into t_u_player_talent select * from t_u_player_talent_1;
insert into t_u_player_talent select * from t_u_player_talent_2;
insert into t_u_player_talent select * from t_u_player_talent_3;
insert into t_u_player_talent select * from t_u_player_talent_4;
insert into t_u_player_talent select * from t_u_player_talent_5;
insert into t_u_player_talent select * from t_u_player_talent_6;
insert into t_u_player_talent select * from t_u_player_talent_7;
insert into t_u_player_talent select * from t_u_player_talent_8;
insert into t_u_player_talent select * from t_u_player_talent_9;

DROP TABLE if exists `t_u_prop`;
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

insert into t_u_prop select * from t_u_prop_0;
insert into t_u_prop select * from t_u_prop_1;
insert into t_u_prop select * from t_u_prop_2;
insert into t_u_prop select * from t_u_prop_3;
insert into t_u_prop select * from t_u_prop_4;
insert into t_u_prop select * from t_u_prop_5;
insert into t_u_prop select * from t_u_prop_6;
insert into t_u_prop select * from t_u_prop_7;
insert into t_u_prop select * from t_u_prop_8;
insert into t_u_prop select * from t_u_prop_9;

DROP TABLE if exists `t_u_sign`;
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

insert into t_u_sign select * from t_u_sign_0;
insert into t_u_sign select * from t_u_sign_1;
insert into t_u_sign select * from t_u_sign_2;
insert into t_u_sign select * from t_u_sign_3;
insert into t_u_sign select * from t_u_sign_4;
insert into t_u_sign select * from t_u_sign_5;
insert into t_u_sign select * from t_u_sign_6;
insert into t_u_sign select * from t_u_sign_7;
insert into t_u_sign select * from t_u_sign_8;
insert into t_u_sign select * from t_u_sign_9;

DROP TABLE if exists `t_u_skill`;
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

insert into t_u_skill select * from t_u_skill_0;
insert into t_u_skill select * from t_u_skill_1;
insert into t_u_skill select * from t_u_skill_2;
insert into t_u_skill select * from t_u_skill_3;
insert into t_u_skill select * from t_u_skill_4;
insert into t_u_skill select * from t_u_skill_5;
insert into t_u_skill select * from t_u_skill_6;
insert into t_u_skill select * from t_u_skill_7;
insert into t_u_skill select * from t_u_skill_8;
insert into t_u_skill select * from t_u_skill_9;

DROP TABLE if exists `t_u_task`;
CREATE TABLE `t_u_task` (
  `team_id` bigint(20) NOT NULL,
  `tid` int(11) NOT NULL,
  `status` int(11) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`team_id`,`tid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

insert into t_u_task select * from t_u_task_0;
insert into t_u_task select * from t_u_task_1;
insert into t_u_task select * from t_u_task_2;
insert into t_u_task select * from t_u_task_3;
insert into t_u_task select * from t_u_task_4;
insert into t_u_task select * from t_u_task_5;
insert into t_u_task select * from t_u_task_6;
insert into t_u_task select * from t_u_task_7;
insert into t_u_task select * from t_u_task_8;
insert into t_u_task select * from t_u_task_9;

DROP TABLE if exists `t_u_task_condition`;
CREATE TABLE `t_u_task_condition` (
  `team_id` bigint(20) NOT NULL,
  `tid` int(11) NOT NULL,
  `cid` int(11) NOT NULL,
  `val_int` int(11) DEFAULT NULL,
  `val_str` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`team_id`,`tid`,`cid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

insert into t_u_task_condition select * from t_u_task_condition_0;
insert into t_u_task_condition select * from t_u_task_condition_1;
insert into t_u_task_condition select * from t_u_task_condition_2;
insert into t_u_task_condition select * from t_u_task_condition_3;
insert into t_u_task_condition select * from t_u_task_condition_4;
insert into t_u_task_condition select * from t_u_task_condition_5;
insert into t_u_task_condition select * from t_u_task_condition_6;
insert into t_u_task_condition select * from t_u_task_condition_7;
insert into t_u_task_condition select * from t_u_task_condition_8;
insert into t_u_task_condition select * from t_u_task_condition_9;
