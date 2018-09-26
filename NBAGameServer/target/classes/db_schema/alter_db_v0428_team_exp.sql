
DROP TABLE IF EXISTS `t_u_money_old`;
CREATE TABLE `t_u_money_old` (
  `team_id` bigint(20) NOT NULL,
  `money` int(11) DEFAULT NULL,
  `gold` int(11) DEFAULT NULL,
  `exp` int(11) DEFAULT NULL,
  `jsf` int(11) DEFAULT NULL COMMENT '建设费',
  `bd_money` int(11) DEFAULT NULL COMMENT '绑定球券',
  PRIMARY KEY (`team_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='玩家货币数据';

insert into t_u_money_old select * from t_u_money;

-- 更新球队经验
update t_u_money set exp = (9509585 - (6430609 -  exp)) where exp >6087349 and exp <= 6430609;
update t_u_money set exp = (9001561 - (6087349 -  exp)) where exp >5753363 and exp <= 6087349;
update t_u_money set exp = (8507261 - (5753363 -  exp)) where exp >5428876 and exp <= 5753363;
update t_u_money set exp = (8027020 - (5428876 -  exp)) where exp >5114110 and exp <= 5428876;
update t_u_money set exp = (7561166 - (5114110 -  exp)) where exp >4809280 and exp <= 5114110;
update t_u_money set exp = (7110018 - (4809280 -  exp)) where exp >4514592 and exp <= 4809280;
update t_u_money set exp = (6673880 - (4514592 -  exp)) where exp >4230238 and exp <= 4514592;
update t_u_money set exp = (6253036 - (4230238 -  exp)) where exp >3956393 and exp <= 4230238;
update t_u_money set exp = (5847745 - (3956393 -  exp)) where exp >3693212 and exp <= 3956393;
update t_u_money set exp = (5458237 - (3693212 -  exp)) where exp >3440825 and exp <= 3693212;
update t_u_money set exp = (5084704 - (3440825 -  exp)) where exp >3199334 and exp <= 3440825;
update t_u_money set exp = (4727297 - (3199334 -  exp)) where exp >2968809 and exp <= 3199334;
update t_u_money set exp = (4386120 - (2968809 -  exp)) where exp >2749286 and exp <= 2968809;
update t_u_money set exp = (4061226 - (2749286 -  exp)) where exp >2540762 and exp <= 2749286;
update t_u_money set exp = (3752611 - (2540762 -  exp)) where exp >2343195 and exp <= 2540762;
update t_u_money set exp = (3460212 - (2343195 -  exp)) where exp >2156501 and exp <= 2343195;
update t_u_money set exp = (3183905 - (2156501 -  exp)) where exp >1980554 and exp <= 2156501;
update t_u_money set exp = (2923504 - (1980554 -  exp)) where exp >1815186 and exp <= 1980554;
update t_u_money set exp = (2678759 - (1815186 -  exp)) where exp >1660186 and exp <= 1815186;
update t_u_money set exp = (2449359 - (1660186 -  exp)) where exp >1515305 and exp <= 1660186;
update t_u_money set exp = (2234935 - (1515305 -  exp)) where exp >1380255 and exp <= 1515305;
update t_u_money set exp = (2035061 - (1380255 -  exp)) where exp >1254716 and exp <= 1380255;
update t_u_money set exp = (1849263 - (1254716 -  exp)) where exp >1138335 and exp <= 1254716;
update t_u_money set exp = (1677020 - (1138335 -  exp)) where exp >1030736 and exp <= 1138335;
update t_u_money set exp = (1517773 - (1030736 -  exp)) where exp >931520 and exp <= 1030736;
update t_u_money set exp = (1370933 - (931520 -  exp)) where exp >840272 and exp <= 931520;
update t_u_money set exp = (1235885 - (840272 -  exp)) where exp >756565 and exp <= 840272;
update t_u_money set exp = (1111999 - (756565 -  exp)) where exp >679967 and exp <= 756565;
update t_u_money set exp = (998633 - (679967 -  exp)) where exp >610042 and exp <= 679967;
update t_u_money set exp = (895144 - (610042 -  exp)) where exp >546358 and exp <= 610042;
update t_u_money set exp = (800892 - (546358 -  exp)) where exp >488489 and exp <= 546358;
update t_u_money set exp = (715246 - (488489 -  exp)) where exp >436019 and exp <= 488489;
update t_u_money set exp = (637590 - (436019 -  exp)) where exp >388543 and exp <= 436019;
update t_u_money set exp = (567326 - (388543 -  exp)) where exp >345673 and exp <= 388543;
update t_u_money set exp = (503878 - (345673 -  exp)) where exp >307037 and exp <= 345673;
update t_u_money set exp = (446697 - (307037 -  exp)) where exp >272282 and exp <= 307037;
update t_u_money set exp = (395260 - (272282 -  exp)) where exp >241075 and exp <= 272282;
update t_u_money set exp = (349073 - (241075 -  exp)) where exp >213102 and exp <= 241075;
update t_u_money set exp = (307672 - (213102 -  exp)) where exp >188069 and exp <= 213102;
update t_u_money set exp = (270624 - (188069 -  exp)) where exp >165704 and exp <= 188069;
update t_u_money set exp = (237524 - (165704 -  exp)) where exp >145754 and exp <= 165704;
update t_u_money set exp = (207998 - (145754 -  exp)) where exp >127985 and exp <= 145754;
update t_u_money set exp = (181700 - (127985 -  exp)) where exp >112182 and exp <= 127985;
update t_u_money set exp = (158312 - (112182 -  exp)) where exp >98148 and exp <= 112182;
update t_u_money set exp = (137542 - (98148 -  exp)) where exp >85703 and exp <= 98148;
update t_u_money set exp = (119124 - (85703 -  exp)) where exp >74683 and exp <= 85703;
update t_u_money set exp = (102814 - (74683 -  exp)) where exp >64938 and exp <= 74683;
update t_u_money set exp = (88391 - (64938 -  exp)) where exp >56333 and exp <= 64938;
update t_u_money set exp = (75655 - (56333 -  exp)) where exp >48745 and exp <= 56333;
update t_u_money set exp = (64425 - (48745 -  exp)) where exp >42063 and exp <= 48745;
update t_u_money set exp = (54536 - (42063 -  exp)) where exp >36188 and exp <= 42063;
update t_u_money set exp = (45841 - (36188 -  exp)) where exp >31030 and exp <= 36188;
update t_u_money set exp = (38207 - (31030 -  exp)) where exp >26509 and exp <= 31030;
update t_u_money set exp = (31515 - (26509 -  exp)) where exp >22552 and exp <= 26509;
update t_u_money set exp = (25658 - (22552 -  exp)) where exp >19094 and exp <= 22552;
update t_u_money set exp = (20541 - (19094 -  exp)) where exp >16078 and exp <= 19094;
