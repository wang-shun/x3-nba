package com.ftkj.db.dao.logic;

import org.joda.time.DateTime;

import com.ftkj.db.conn.dao.GameConnectionDAO;
import com.ftkj.db.conn.dao.RowHandler;
import com.ftkj.manager.match.HonorMatch;

/**
 * 球星荣耀
 * @author zehong.he
 *
 */
public class HonorMatchDao extends GameConnectionDAO {

    private RowHandler<HonorMatch> DIV_MAPPER = (rs) -> {
        HonorMatch idv = new HonorMatch();
        idv.setTeamId(rs.getLong("team_id"));
        idv.setDivInfo(rs.getString("div_info"));
        idv.setMaxDiv(rs.getInt("max_div"));
        idv.setCreateTime(new DateTime(rs.getTimestamp("create_time")));
        idv.setUpdateTime(new DateTime(rs.getTimestamp("update_time")));
        return idv;
    };
    
    public HonorMatch getHonorMatchByTeam(long teamId) {
      String sql = "select * from t_u_honor_match where team_id=?";
      return queryForObject(sql, DIV_MAPPER, teamId);
    }
    
    public void addHonorMatch(HonorMatch honorMatch) {
      String sql = "INSERT INTO `t_u_honor_match`(team_id,create_time,update_time,max_div,div_info) VALUES (?,now(),now(),?,?)";
      execute(sql,honorMatch.getTeamId(),honorMatch.getMaxDiv(),honorMatch.getDivInfo());
    }
}
