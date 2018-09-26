
alter table t_u_mmatch_t add column `last_lev_rid` int DEFAULT null COMMENT '最后一场比赛的关卡. 用于计算提供的装备经验',
  add column `last_match_time` bigint DEFAULT null COMMENT '最后一场比赛结束时间. 用于计算提供的装备经验';

alter table t_u_mmatch_lev add column `m_c` int DEFAULT null COMMENT '总比赛次数';
