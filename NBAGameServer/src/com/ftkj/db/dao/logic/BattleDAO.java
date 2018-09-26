package com.ftkj.db.dao.logic;

import com.ftkj.db.conn.dao.GameConnectionDAO;
import com.ftkj.db.conn.dao.RowHandler;
import com.ftkj.db.domain.BattleInfoPO;
import com.ftkj.enums.battle.EBattleType;
import org.joda.time.DateTime;

import java.util.List;

/**
 * 比赛记录
 */
public class BattleDAO extends GameConnectionDAO {

    private RowHandler<BattleInfoPO> Battle_Mapper = rs -> {
        BattleInfoPO e = new BattleInfoPO();
        e.setBattleId(rs.getLong("battle_id"));
        e.setBattleType(rs.getInt("battle_type"));

        e.setHomeTeamId(rs.getLong("home_team_id"));
        e.setHomeTeamName(rs.getString("home_team_name"));
        e.setHomeScore(rs.getInt("home_score"));

        e.setAwayTeamId(rs.getLong("away_team_id"));
        e.setAwayTeamName(rs.getString("away_team_name"));
        e.setAwayScore(rs.getInt("away_score"));
        e.setCreateTime(new DateTime(rs.getTimestamp("create_time")));

        e.setVi1(rs.getInt("vi1"));
        e.setVi2(rs.getInt("vi2"));
        e.setVi3(rs.getInt("vi3"));
        e.setVi4(rs.getInt("vi4"));

        e.setVl1(rs.getLong("vl1"));
        e.setVl2(rs.getLong("vl2"));
        e.setVl3(rs.getLong("vl3"));
        e.setVl4(rs.getLong("vl4"));

        e.setStr1(rs.getString("str1"));
        e.setStr2(rs.getString("str2"));
        return e;
    };

    /** 获取球队在主客场的比赛历史记录 */
    public List<BattleInfoPO> findBattleHis(long teamId, EBattleType bt, int offset, int limit) {
        String sql = "select * from t_u_battle where battle_type = ? and (home_team_id = ? or away_team_id = ?)" +
                " order by battle_id desc limit ?, ?";
        return queryForList(sql, Battle_Mapper, bt.getId(), teamId, teamId, offset, limit);
    }

    /** 获取球队在客场的比赛历史记录 */
    public List<BattleInfoPO> findAwayBattleHis(long teamId, EBattleType bt, int offset, int limit) {
        String sql = "select * from t_u_battle where battle_type = ? and away_team_id = ?" +
                " order by battle_id desc limit ?, ?";
        return queryForList(sql, Battle_Mapper, bt.getId(), teamId, offset, limit);
    }

    /** 获取球队在主场的比赛历史记录 */
    public List<BattleInfoPO> findHomeBattleHis(long teamId, EBattleType bt, int offset, int limit) {
        String sql = "select * from t_u_battle where battle_type = ? and home_team_id = ?" +
                " order by battle_id desc limit ?, ?";
        return queryForList(sql, Battle_Mapper, bt.getId(), teamId, offset, limit);
    }

}
