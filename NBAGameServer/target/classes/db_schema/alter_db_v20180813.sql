alter table t_u_train add column `train_hour` tinyint(2) NOT NULL DEFAULT '0' COMMENT '训练总时间(联盟训练馆结算)';
alter table t_u_train add column `clear` tinyint(2) NOT NULL DEFAULT '0' COMMENT '联盟训练馆清理标识(1:清理)';
alter table t_u_train add column `bl_id` smallint(6) NOT NULL DEFAULT '0' COMMENT '联盟训练馆基础ID';

CREATE TABLE `t_u_league_train` (
  `bl_id` smallint(6) NOT NULL DEFAULT '0' COMMENT '球馆ID',
  `league_id` int(12) NOT NULL DEFAULT '0' COMMENT '联盟ID',
  `bt_id` smallint(6) NOT NULL DEFAULT '1' COMMENT '配置球队ID',
  PRIMARY KEY (`bl_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='联盟训练馆数据';


