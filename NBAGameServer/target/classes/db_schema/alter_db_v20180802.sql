ALTER TABLE t_u_starlet_player DROP PRIMARY KEY;
DELETE t_u_starlet_player
FROM
	t_u_starlet_player,
	(
		SELECT
			team_id,
			player_rid,
			lineup_position,
			MAX(cap) AS cap
		FROM
			t_u_starlet_player
		GROUP BY
			team_id,
			lineup_position
		HAVING
			count(*) > 1
	) t2
WHERE
	t_u_starlet_player.team_id = t2.team_id
AND t_u_starlet_player.lineup_position = t2.lineup_position
AND t_u_starlet_player.cap <= t2.cap;

ALTER TABLE t_u_starlet_player ADD PRIMARY KEY (team_id,lineup_position);
