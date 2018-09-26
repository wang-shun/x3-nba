
DELETE c.* FROM	t_u_task AS t LEFT JOIN t_u_task_condition AS c ON c.team_id = t.team_id AND c.tid = t.tid WHERE t.`status` = '3';

DROP TABLE IF EXISTS `t_u_train`;
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
  PRIMARY KEY (`train_id`,`team_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='训练馆数据';

DROP TABLE IF EXISTS `t_u_team_train`;
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
