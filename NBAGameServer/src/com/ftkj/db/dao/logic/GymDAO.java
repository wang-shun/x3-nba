package com.ftkj.db.dao.logic;

import com.ftkj.db.conn.dao.DBManager;
import com.ftkj.db.conn.dao.GameConnectionDAO;
import com.ftkj.db.conn.dao.ResultSetRow;
import com.ftkj.db.conn.dao.RowHandler;
import com.ftkj.db.domain.ArenaPlayerPO;
import com.ftkj.db.domain.TeamArenaConstructionPO;
import com.ftkj.db.domain.TeamArenaPO;
import org.joda.time.DateTime;

import java.util.List;

/**
 * @author tim.huang
 * 2017年7月14日
 */
public class GymDAO extends GameConnectionDAO {

    public GymDAO() {
        DBManager.putGameDelSql("delete from t_u_arena_player where player_id = 0");
    }

    private RowHandler<TeamArenaPO> TEAMARENAPO = new RowHandler<TeamArenaPO>() {

        @Override
        public TeamArenaPO handleRow(ResultSetRow row) throws Exception {
            TeamArenaPO po = new TeamArenaPO();
            po.setDefend(row.getInt("defend"));
            po.setGold(row.getInt("gold"));
            po.setLastUpdateTime(new DateTime(row.getTimestamp("last_update_time")));
            po.setLevel(row.getInt("level"));
            po.setPower(row.getInt("power"));
            po.setTeamId(row.getLong("team_id"));
            return po;
        }
    };

    private RowHandler<TeamArenaConstructionPO> TEAMARENACONSTRUCTIONPO = new RowHandler<TeamArenaConstructionPO>() {

        @Override
        public TeamArenaConstructionPO handleRow(ResultSetRow row) throws Exception {
            TeamArenaConstructionPO po = new TeamArenaConstructionPO();
            po.setcId(row.getInt("cid"));
            po.setCurGold(row.getInt("cur_gold"));
            po.setMaxGold(row.getInt("max_gold"));
            po.setPlayerGrade(row.getInt("player_grade"));
            po.setPlayerId(row.getInt("player_id"));
            po.setTeamId(row.getLong("team_id"));
            po.setUpdateTime(new DateTime(row.getTimestamp("update_time")));
            return po;
        }
    };

    private RowHandler<ArenaPlayerPO> ARENAPLAYERPO = new RowHandler<ArenaPlayerPO>() {

        @Override
        public ArenaPlayerPO handleRow(ResultSetRow row) throws Exception {
            ArenaPlayerPO po = new ArenaPlayerPO();
            po.setCreateTime(new DateTime(row.getTimestamp("create_time")));
            po.setPid(row.getInt("pid"));
            po.setPlayerId(row.getInt("player_id"));
            po.setPosition(row.getInt("position"));
            po.setTeamId(row.getLong("team_id"));
            po.setTid(row.getInt("tid"));
            po.setGrade(row.getInt("grade"));
            return po;
        }
    };

    public TeamArenaPO getTeamArena(long teamId) {
        String sql = "select * from t_u_arena_team where team_id = ?";
        return queryForObject(sql, TEAMARENAPO, teamId);
    }

    public List<TeamArenaConstructionPO> getTeamArenaConstruction(long teamId) {
        String sql = "select * from t_u_arena where team_id = ?";
        return queryForList(sql, TEAMARENACONSTRUCTIONPO, teamId);
    }

    public List<ArenaPlayerPO> getArenaPlayerPO(long teamId) {
        String sql = "select * from t_u_arena_player where team_id = ?";
        return queryForList(sql, ARENAPLAYERPO, teamId);
    }

}
