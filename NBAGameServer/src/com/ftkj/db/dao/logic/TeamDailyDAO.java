package com.ftkj.db.dao.logic;

import java.util.List;

import com.ftkj.db.conn.dao.DBManager;
import com.ftkj.db.conn.dao.GameConnectionDAO;
import com.ftkj.db.conn.dao.ResultSetRow;
import com.ftkj.db.conn.dao.RowHandler;
import com.ftkj.manager.team.TeamDaily;
import com.ftkj.server.GameSource;

/**
 * 球队每日数据
 * @author qin.jiang
 */
public class TeamDailyDAO extends GameConnectionDAO {

    public TeamDailyDAO() {
        DBManager.putGameDelSql("delete from t_u_team_daily where team_id < 0 or delete_flag = 1");
    }

    private RowHandler<TeamDaily> TEAMDAILY = new RowHandler<TeamDaily>() {
        
        @Override
        public TeamDaily handleRow(ResultSetRow row) throws Exception {
            TeamDaily teamDaily = new TeamDaily();
            teamDaily.setTeamId(row.getLong("team_id"));
            teamDaily.setTradeChatLMState(row.getInt("trade_chat_lm_state"));
            teamDaily.setTradeChatLMState(row.getInt("delete_flag"));
            return teamDaily;
        }
    };
    
    public TeamDaily getTeamDaily(long teamId) {
        String sql = "select * from t_u_team_daily where team_id = ? and team_id > 0";
        return queryForObject(sql, TEAMDAILY, teamId);
    }
    
    /**
     * 每天凌晨清理
     */
    public void clearTeamDaily() {
        String sql = "update t_u_team_daily set trade_chat_lm_state = 0";
        executeUpdate(sql);
    }
    

    public List<TeamDaily> getAllTeamDaily() {
        String sql = "select * from t_u_team_daily";
        return queryForList(sql, TEAMDAILY);
    }
      
    
  //------------------------------------------------------------------------------
    private RowHandler<String> TableNamePO = new RowHandler<String>() {
        @Override
        public String handleRow(ResultSetRow row) throws Exception {
            return row.getString("table_name");
        }
    };
    
    /**
     * 暂时只有清档调用
     * 20个球队以上，不执行操作 
     * @param teamId
     */
    public void clearAllData() {
        int teamNum  = queryForInteger("select count(1) num from t_u_team_daily;");
        if(teamNum > 2) {
            return;
        }
        String tableNameSql = "select table_name from information_schema.tables where table_schema='nba_?'";
        List<String> tablesList = queryForList(tableNameSql, TableNamePO, GameSource.shardId);
        // 查询所有表并删除
        for(String table : tablesList) {
            execute("delete from ?", table);
        }
    }
}
