

DROP TABLE IF EXISTS `t_u_trade_p2p`;

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

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
