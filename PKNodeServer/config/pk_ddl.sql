-- MySQL dump 10.16  Distrib 10.1.25-MariaDB, for debian-linux-gnu (x86_64)
--
-- Host: 192.168.10.181    Database: nba_101
-- ------------------------------------------------------
-- Server version	5.5.54-38.6-log


CREATE DATABASE IF NOT EXISTS `nba_pk_node` DEFAULT CHARACTER SET utf8;
USE `nba_pk_node`;


DROP TABLE IF EXISTS `t_u_rmatch_t`;
CREATE TABLE `t_u_rmatch_t` (
  `team_id` bigint NOT NULL COMMENT '球队id',
  `node_name` varchar(500) NOT NULL COMMENT 'node name',

  `s_id` int NOT NULL COMMENT '本赛季. 赛季id',
  `tier` int DEFAULT NULL COMMENT '段位层级',
  `rank` int DEFAULT NULL COMMENT '段位排名',
  `rating` int DEFAULT NULL COMMENT '评分',
  `m_c` int DEFAULT NULL COMMENT '参与比赛次数',
  `w_c` int DEFAULT NULL COMMENT '胜利次数',
  `w_s_max` int DEFAULT NULL COMMENT '最大连胜次数',

  `pre_s_id` int NOT NULL COMMENT '上赛季. 赛季id',
  `pre_tier` int DEFAULT NULL COMMENT '段位层级',
  `pre_rank` int DEFAULT NULL COMMENT '段位排名',
  `pre_rating` int DEFAULT NULL COMMENT '评分',
  `pre_m_c` int DEFAULT NULL COMMENT '参与比赛次数',
  `pre_w_c` int DEFAULT NULL COMMENT '胜利次数',
  `pre_w_s_max` int DEFAULT NULL COMMENT '最大连胜次数',

  `first_award` bigint DEFAULT NULL COMMENT '首次奖励领取状态',
  `t_m_c` int DEFAULT NULL COMMENT '总比赛次数',
  `t_w_c` int DEFAULT NULL COMMENT '总比赛胜利次数',
  `w_s` int DEFAULT NULL COMMENT '当前连胜次数',
  `last_time` bigint DEFAULT NULL COMMENT '最后比赛结束时间',

  PRIMARY KEY (`team_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='球队跨服天梯赛信息';


DROP TABLE IF EXISTS `t_u_rmatch_s_his`;
CREATE TABLE `t_u_rmatch_s_his` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `team_id` bigint NOT NULL COMMENT '球队id',

  `s_id` int NOT NULL COMMENT '赛季id',
  `tier` int DEFAULT NULL COMMENT '段位层级',
  `rank` int DEFAULT NULL COMMENT '段位排名',
  `rating` int DEFAULT NULL COMMENT '评分',
  `m_c` int DEFAULT NULL COMMENT '参与比赛次数',
  `w_c` int DEFAULT NULL COMMENT '胜利次数',
  `w_s_max` int DEFAULT NULL COMMENT '最大连胜次数',

  `t_m_c` int DEFAULT NULL COMMENT '总参与比赛次数',
  `w_s` int DEFAULT NULL COMMENT '当前连胜次数',

  `c_time` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '日志生成时间',
  PRIMARY KEY (`id`),
  KEY `i_t_id` (`team_id`),
  KEY `i_s_id` (`s_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='球队跨服天梯赛历史信息';

