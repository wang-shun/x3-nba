package com.ftkj.db.dao.logic;

import org.joda.time.DateTime;

import com.ftkj.db.conn.dao.GameConnectionDAO;
import com.ftkj.db.conn.dao.RowHandler;
import com.ftkj.manager.activity.HappySevenDay;

/**
 * 开服7天乐
 * @author zehong.he
 *
 */
public class HappySevenDayDao extends GameConnectionDAO {

    private RowHandler<HappySevenDay> HappySevenDay_MAPPER = (rs) -> {
        HappySevenDay happySevenDay = new HappySevenDay();
        happySevenDay.setTeamId(rs.getLong("team_id"));
        happySevenDay.setTask(rs.getString("task"));
        happySevenDay.setBox(rs.getString("box"));
        happySevenDay.setCreateTime(new DateTime(rs.getTimestamp("create_time")));
        happySevenDay.setUpdateTime(new DateTime(rs.getTimestamp("update_time")));
        return happySevenDay;
    };
    
    public HappySevenDay getHappySevenDayByTeam(long teamId) {
      String sql = "select * from t_u_happy_sevenday where team_id=?";
      return queryForObject(sql, HappySevenDay_MAPPER, teamId);
    }
    
    public void addHappySevenDay(HappySevenDay happySevenDay) {
      String sql = "INSERT INTO `t_u_happy_sevenday`(team_id,task,box,create_time,update_time) VALUES (?,?,?,now(),now())";
      execute(sql,happySevenDay.getTeamId(),happySevenDay.getTask(),happySevenDay.getBox());
    }
}
