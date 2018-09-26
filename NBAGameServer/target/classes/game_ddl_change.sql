
ALTER TABLE `t_u_battle`
   ADD COLUMN `vi1` int(11) DEFAULT NULL COMMENT '附加参数int 1',
  ADD COLUMN `vi2` int(11) DEFAULT NULL COMMENT '附加参数int 2',
  ADD COLUMN `vi3` int(11) DEFAULT NULL COMMENT '附加参数int 3',
  ADD COLUMN `vi4` int(11) DEFAULT NULL COMMENT '附加参数int 4',
  ADD COLUMN `vl1` bigint DEFAULT NULL COMMENT '附加参数long 1',
  ADD COLUMN `vl2` bigint DEFAULT NULL COMMENT '附加参数long 2',
  ADD COLUMN `vl3` bigint DEFAULT NULL COMMENT '附加参数long 3',
  ADD COLUMN `vl4` bigint DEFAULT NULL COMMENT '附加参数long 4',
  ADD COLUMN `str1` text COMMENT '附加参数str 1',
  ADD COLUMN `str2` text COMMENT '附加参数str 2';
 
 ALTER TABLE `t_u_battle` ADD INDEX `i_bt` (`battle_type`);
