package com.ftkj.db.dao.logic;

import com.ftkj.db.conn.dao.GameConnectionDAO;
import com.ftkj.db.conn.dao.RowHandler;
import com.ftkj.manager.match.MainMatch;
import com.ftkj.manager.match.MainMatchDivision;
import com.ftkj.manager.match.MainMatchLevel;

import java.util.List;

/**
 * 主线赛程.
 */
public class MainMatchDao extends GameConnectionDAO {

    private RowHandler<MainMatchDivision> DIV_MAPPER = (rs) -> {
        MainMatchDivision idv = new MainMatchDivision();
        idv.setId(rs.getInt("id"));
        idv.setTeamId(rs.getLong("team_id"));
        idv.setResourceId(rs.getInt("r_id"));
        idv.setStarAwards(rs.getInt("star_awards"));
        return idv;
    };

    private RowHandler<MainMatchLevel> LEV_MAPPER = (rs) -> {
        MainMatchLevel lev = new MainMatchLevel();
        lev.setId(rs.getInt("id"));
        lev.setTeamId(rs.getLong("team_id"));
        lev.setResourceId(rs.getInt("r_id"));
        lev.setStar(rs.getInt("star"));
        lev.setMatchCount(rs.getInt("m_c"));
        return lev;
    };

    private RowHandler<MainMatch> TMM_MAPPER = rs -> {
        MainMatch mm = new MainMatch();
        mm.setTeamId(rs.getLong("team_id"));
        mm.setMatchNum(rs.getInt("m_num"));
        mm.setMatchNumLastUpTime(rs.getLong("m_num_time"));

        mm.setLastLevelRid(rs.getInt("last_lev_rid"));
        mm.setLastMatchEndTime(rs.getLong("last_match_time"));

        mm.setChampionshipLastMatchStartTime(rs.getLong("cs_time"));
        mm.setChampionshipRndSeed(rs.getLong("cs_seed"));
        mm.setChampionshipLevelRid(rs.getInt("cs_lev_rid"));
        mm.setChampionshipWinNum(rs.getInt("cs_win"));
        mm.setChampionshipTargets(rs.getString("cs_targets"));
        return mm;
    };

    public List<MainMatchDivision> findDivs(long teamId) {
        return queryForList("select * from t_u_mmatch_div where team_id = ?", DIV_MAPPER, teamId);
    }

    public List<MainMatchLevel> findLevs(long teamId) {
        return queryForList("select * from t_u_mmatch_lev where team_id = ?", LEV_MAPPER, teamId);
    }

    public MainMatch findMMatch(long teamId) {
        return queryForObject("select * from t_u_mmatch_t where team_id = ?", TMM_MAPPER, teamId);

    }
}
