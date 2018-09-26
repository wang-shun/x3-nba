--2018年8月8日12:19:32 database:nba
ALTER TABLE `t_u_card`
ADD COLUMN `cost_num`  int(11) NULL DEFAULT 0 COMMENT '吞噬底薪球员的数量' AFTER `exp`;