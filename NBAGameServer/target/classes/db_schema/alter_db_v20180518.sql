
DROP TABLE IF EXISTS `t_u_r_arena`;
CREATE TABLE `t_u_r_arena` (
  `team_id` bigint NOT NULL,
  `rank` int DEFAULT NULL comment '竞技场排名. 越小越大',
  `max_rank` int DEFAULT NULL comment '历史最大排名. 越小越大',
  `match_time` bigint DEFAULT NULL comment '上次比赛时间',
  `last_rank` int DEFAULT NULL comment '最后一次结算时的排名',
  `t_m_c` bigint DEFAULT NULL comment '总比赛场数',
  `t_w_c` bigint DEFAULT NULL comment '总胜利场数',
  PRIMARY KEY (`team_id`),
  key `i_rank` (`rank`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 comment '竞技场. 个人竞技场';


DROP TABLE IF EXISTS `t_u_league_group`;

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

/*Table structure for table `t_u_league_group_season` */

DROP TABLE IF EXISTS `t_u_league_group_season`;

CREATE TABLE `t_u_league_group_season` (
  `id` int(11) NOT NULL COMMENT '赛季',
  `name` varchar(50) DEFAULT NULL,
  `start_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `end_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `status` int(11) DEFAULT NULL COMMENT '0默认，1开始，2结束',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `t_u_league_group_team` */

DROP TABLE IF EXISTS `t_u_league_group_team`;

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
