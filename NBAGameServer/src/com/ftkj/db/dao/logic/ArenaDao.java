package com.ftkj.db.dao.logic;

import com.ftkj.db.conn.dao.GameConnectionDAO;
import com.ftkj.db.conn.dao.ResultSetRow;
import com.ftkj.db.conn.dao.RowHandler;
import com.ftkj.manager.arena.Arena;

import java.sql.SQLException;
import java.util.List;

/**
 * 竞技场. 个人排名竞技.
 *
 * @author luch
 */
public class ArenaDao extends GameConnectionDAO {

    private final RowHandler<Arena> Mapper = (rs) -> createArena(rs);

    private Arena createArena(ResultSetRow rs) throws SQLException {
        Arena ar = new Arena();
        ar.setTeamId(rs.getLong("team_id"));
        ar.setRank(rs.getInt("rank"));
        ar.setMaxRank(rs.getInt("max_rank"));
        ar.setPreMatchTime(rs.getLong("match_time"));
        ar.setLastRank(rs.getInt("last_rank"));
        ar.setTotalMatchCount(rs.getInt("t_m_c"));
        ar.setTotalWinCount(rs.getInt("t_w_c"));
        return ar;
    }

    /** 附加 {@link mnba.model.team.Arena.TeamCap} 的排名信息 */
    public List<Arena> findByRankAsc(int maxRank) {
        return queryForList("SELECT * FROM t_u_r_arena where rank > 0 and rank <= ? order by rank asc", Mapper, maxRank);
    }

    public Arena findById(long teamId) {
        return queryForObject("SELECT * FROM t_u_r_arena where team_id = ?", Mapper, teamId);
    }

}
