
DROP TABLE IF EXISTS `t_u_team_daily`;
CREATE TABLE `t_u_team_daily` (
  `team_id` bigint(11) NOT NULL DEFAULT '0' COMMENT '球队ID',
  `trade_chat_lm_state` tinyint(2) NOT NULL DEFAULT '0' COMMENT '每日交易留言状态（0:未留言, 1:已留言）',
  `delete_flag` tinyint(2) NOT NULL DEFAULT '0' COMMENT '是否已删除(0: 未删除，1:已删除）',
  PRIMARY KEY (`team_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='球队每日数据';

