-- 9月25号
ALTER TABLE `t_u_league`
ADD COLUMN `league_exp`  int(11) NULL DEFAULT 0 COMMENT '联盟当前经验' AFTER `league_level`,
ADD COLUMN `league_total_exp`  int NULL DEFAULT 0 COMMENT '联盟累计获得总经验值' AFTER `league_exp`,
ADD COLUMN `score`  bigint NULL DEFAULT 0 COMMENT '联盟累计总的贡献' AFTER `honor`;

-- 创建存储过程用于修改联盟成就等级调整成和联盟等级相同
DROP PROCEDURE IF EXISTS proc_update_t_u_league_honor_level;
CREATE PROCEDURE proc_update_t_u_league_honor_level()
BEGIN

	-- 游标返回变量
	DECLARE _league_id INT(8);
	DECLARE _league_level INT(8);
	DECLARE _now_date_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
	DECLARE _week INT(8);

	-- 游标状态标志
	DECLARE flag BOOLEAN DEFAULT TRUE;
	DECLARE cur CURSOR FOR SELECT league_id,league_level FROM t_u_league;
	-- 游标状态赋值
	DECLARE CONTINUE HANDLER FOR NOT FOUND SET flag = FALSE;
	OPEN cur;
	FETCH cur INTO _league_id, _league_level;
	WHILE flag DO
			UPDATE t_u_league_honor SET t_u_league_honor.`level`=_league_level WHERE t_u_league_honor.league_id=_league_id;
			FETCH cur INTO _league_id, _league_level;
			COMMIT;
	END WHILE;
	CLOSE cur;

	-- 修改所有成就激活结束时间为本周六0点结束或者当前为周6周天则到下周6的0点为结束时间
	SET _week = DAYOFWEEK(_now_date_time);
	IF _week=7 THEN -- 周六
		SET _now_date_time = DATE_ADD(CURRENT_TIMESTAMP,INTERVAL 7 DAY);
	ELSEIF _week=1 THEN -- 周日
		SET _now_date_time = DATE_ADD(CURRENT_TIMESTAMP,INTERVAL 6 DAY);
	END IF;
	SET @ymd = date_format(_now_date_time, '%Y-%m-%d');
	UPDATE t_u_league_honor SET t_u_league_honor.end_time=STR_TO_DATE(@ymd,'%Y-%m-%d %H');
END;


-- 统计联盟总的贡献值,更新到联盟表的score字段
DROP PROCEDURE IF EXISTS proc_update_t_u_league_score;
CREATE PROCEDURE proc_update_t_u_league_score()
BEGIN

	-- 游标返回变量
	DECLARE _league_id INT(8);
	DECLARE _score INT(8);
	

	-- 游标状态标志
	DECLARE flag BOOLEAN DEFAULT TRUE;
	DECLARE cur CURSOR FOR SELECT league_id,SUM(score) FROM t_u_league_team WHERE league_id > 0 GROUP BY t_u_league_team.league_id;
	-- 游标状态赋值
	DECLARE CONTINUE HANDLER FOR NOT FOUND SET flag = FALSE;
	OPEN cur;
	FETCH cur INTO _league_id, _score;
	WHILE flag DO
		UPDATE t_u_league SET t_u_league.score=_score WHERE t_u_league.league_id=_league_id;
		FETCH cur INTO _league_id, _score;
		COMMIT;
	END WHILE;
	CLOSE cur;
END;


-- 训练馆领取奖励后球员状态没有重置（概率）
-- 修改玩家球员训练状态没有重置的问题2018年9月23日17:07:26
DROP PROCEDURE IF EXISTS proc_update_t_u_player_storage;
CREATE PROCEDURE proc_update_t_u_player_storage()
BEGIN

	-- 游标返回变量
	DECLARE _team_id bigint(20);
	DECLARE _pid INT(11);
	DECLARE _count INT(11) DEFAULT 0;
	
	-- 游标状态标志
	DECLARE flag BOOLEAN DEFAULT TRUE;
	DECLARE cur CURSOR FOR SELECT team_id, pid FROM t_u_player WHERE t_u_player.`storage`=4;
	-- 游标状态赋值
	DECLARE CONTINUE HANDLER FOR NOT FOUND SET flag = FALSE;
	OPEN cur;
	FETCH cur INTO _team_id, _pid;
	WHILE flag DO
		
		-- 查询球员是否还在训练
		SELECT count(*) INTO _count FROM t_u_train WHERE team_id=_team_id AND player_id=_pid;
	
		-- 如果_count=0则需要重置改玩家球员的状态为1(在仓库状态)
		IF _count=0 THEN
			UPDATE t_u_player SET `storage`=1 WHERE team_id=_team_id AND pid=_pid;
		END IF;

		FETCH cur INTO _team_id, _pid;
		COMMIT;
	END WHILE;
	CLOSE cur;

	-- 重置球员唯一Id为0的训练数据
	update t_u_train set player_rid=0,reward_state=0,train_hour=0,bl_id=0 where player_id=0 AND reward_state=1;
END;

-- 修改交易过期的球员状态为在仓库状态[交易-有几率出现球员过期了，但是球员的交易状态没重置。（概率）]
DROP PROCEDURE IF EXISTS proc_update_trade_player_storage;
CREATE PROCEDURE proc_update_trade_player_storage()
BEGIN
	-- 修改交易过期的球员状态为在仓库状态[交易-有几率出现球员过期了，但是球员的交易状态没重置。（概率）]
	-- 游标返回变量
	DECLARE _team_id bigint(20);
	DECLARE _pid INT(11);
	DECLARE _count INT(11) DEFAULT 0;
	
	-- 游标状态标志
	DECLARE flag BOOLEAN DEFAULT TRUE;
	DECLARE cur CURSOR FOR SELECT team_id,pid FROM t_u_trade WHERE t_u_trade.`status`=0 AND NOW()>t_u_trade.end_time;
	-- 游标状态赋值
	DECLARE CONTINUE HANDLER FOR NOT FOUND SET flag = FALSE;
	OPEN cur;
	FETCH cur INTO _team_id, _pid;
	WHILE flag DO
		
		-- 查询球员是否还在训练
		SELECT count(*) INTO _count FROM t_u_player WHERE team_id=_team_id AND pid=_pid AND `storage`=2;
	
		-- 如果_count=1则需要重置改玩家球员的状态为1(在仓库状态)
		IF _count=1 THEN
			UPDATE t_u_player SET `storage`=1 WHERE team_id=_team_id AND pid=_pid;
		END IF;

		FETCH cur INTO _team_id, _pid;
		COMMIT;
	END WHILE;
	CLOSE cur;
END;

-- 执行存储过程更新数据
call proc_update_t_u_league_honor_level();
call proc_update_t_u_league_score();
call proc_update_t_u_player_storage();
call proc_update_trade_player_storage();