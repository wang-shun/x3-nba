package com.ftkj.db.dao.pk;

import com.ftkj.db.conn.dao.GameConnectionDAO;
import com.ftkj.db.conn.dao.ResultSetRow;
import com.ftkj.db.conn.dao.RowHandler;
import com.ftkj.manager.match.RankedMatch;
import com.ftkj.manager.match.RankedMatch.Season;
import com.ftkj.manager.match.RankedMatch.SeasonHistory;

import java.sql.SQLException;
import java.util.List;

public class XRankedMatchDAO extends GameConnectionDAO {

    private RowHandler<RankedMatch> RM_MAPPER = rs -> {
        RankedMatch e = new RankedMatch();
        e.setTeamId(rs.getLong("team_id"));
        e.setNodeName(rs.getString("node_name"));

        Season currSeason = getSeason(rs, "");
        Season preSeason = getSeason(rs, "pre_");
        if (currSeason.getId() > 0) {
            e.setCurrSeason(currSeason);
        }
        if (preSeason.getId() > 0) {
            e.setLastSeason(preSeason);
        }

        e.setFirstAward(rs.getLong("first_award"));
        e.setTotalMatchCount(rs.getInt("t_m_c"));
        e.setTotalWinCount(rs.getInt("t_w_c"));
        e.setWinningStreak(rs.getInt("w_s"));
        e.setLastMatchTime(rs.getLong("last_time"));
        return e;
    };

    private RowHandler<SeasonHistory> HIS_MAPPER = rs -> {
        SeasonHistory e = new SeasonHistory();
        e.setSeqid(rs.getLong("id"));
        e.setTeamId(rs.getLong("team_id"));

        e.setSeason(getSeason(rs, ""));

        e.setTotalMatchCount(rs.getInt("t_m_c"));
        e.setWinningStreak(rs.getInt("w_s"));
        return e;
    };

    private Season getSeason(ResultSetRow rs, String prefix) throws SQLException {
        Season s = new Season();
        s.setId(rs.getInt(prefix + "s_id"));
        s.setTierId(rs.getInt(prefix + "tier"));
        s.setRank(rs.getInt(prefix + "rank"));
        s.setRating(rs.getInt(prefix + "rating"));
        s.setMatchCount(rs.getInt(prefix + "m_c"));
        s.setWinCount(rs.getInt(prefix + "w_c"));
        s.setWinningStreakMax(rs.getInt(prefix + "w_s_max"));
        return s;
    }

    public RankedMatch findByTid(long teamId) {
        return queryForObject("select * from t_u_rmatch_t where team_id = ?", RM_MAPPER, teamId);
    }

    public long findHisMaxId() {
        return queryForLong("select max(id) from t_u_rmatch_s_his");
    }

    public List<SeasonHistory> findSeasonHis(long teamId, int offset, int pageSize) {
        return queryForList("select * from t_u_rmatch_s_his where team_id = ? order by id desc limit ?, ?",
                HIS_MAPPER, teamId, offset, pageSize);
    }
}
