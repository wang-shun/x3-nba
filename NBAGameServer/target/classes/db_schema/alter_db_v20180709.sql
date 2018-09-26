CREATE TABLE `t_u_starlet_rank` (
  `team_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '球队ID',
  `rank` int(11) NOT NULL DEFAULT '0' COMMENT '排行',
  PRIMARY KEY (`rank`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='新秀排位赛排行榜';

CREATE TABLE `t_u_starlet_player` (
  `team_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '球队ID',
  `player_rid` int(11) NOT NULL DEFAULT '0' COMMENT '策划配置id',
  `lineup_position` varchar(11) NOT NULL COMMENT '位置',
  `cap` int(11) NOT NULL DEFAULT '0' COMMENT '新秀战力',
  PRIMARY KEY (`team_id`,`player_rid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='新秀阵容球员';

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

CREATE TABLE `t_u_starlet_team_redix` (
  `team_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '球队ID',
  `redix_num` int(11) NOT NULL DEFAULT '0' COMMENT '获胜累计基数',
  `card_type` int(11) NOT NULL DEFAULT '0' COMMENT '对应卡组类型',
  PRIMARY KEY (`team_id`,`card_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='球队新秀对抗赛获得新秀卡组基础数据';

