package com.ftkj.db.dao.logic;

import java.util.List;

import com.ftkj.db.conn.dao.GameConnectionDAO;
import com.ftkj.db.conn.dao.ResultSetRow;
import com.ftkj.db.conn.dao.RowHandler;
import com.ftkj.manager.train.LeagueTrain;
import com.ftkj.manager.train.TeamTrain;
import com.ftkj.manager.train.Train;

public class TrainDAO extends GameConnectionDAO {
    
    private RowHandler<TeamTrain> TEAMTRAIN = new RowHandler<TeamTrain>() {
        
        @Override
        public TeamTrain handleRow(ResultSetRow row) throws Exception {
            TeamTrain teamTrain = new TeamTrain();
            teamTrain.setTeamId(row.getLong("team_id"));
            teamTrain.setRefreshTime(row.getLong("refresh_time"));
            teamTrain.setRobbedCount(row.getInt("robbed_count"));
            teamTrain.setRobbedTotalCount(row.getInt("robbed_total_count"));
            teamTrain.setRobbedWinCount(row.getInt("robbed_win_count"));
            teamTrain.setRobbedFailCount(row.getInt("robbed_fail_count"));
            teamTrain.setTrainCount(row.getInt("train_count"));           
            return teamTrain;
        }
    };    
	
	private RowHandler<Train> TRAIN = new RowHandler<Train>() {
	  
		@Override
		public Train handleRow(ResultSetRow row) throws Exception {
		    Train train = new Train();
		    train.setTrainId(row.getInt("train_id"));
		    train.setTeamId(row.getLong("team_id"));
		    train.setTrainLevel(row.getInt("train_level"));
		    train.setPlayerCap(row.getInt("player_cap"));
		    train.setPlayerId(row.getInt("player_id"));
		    train.setPlayerRid(row.getInt("player_rid"));
		    train.setRewardState(row.getInt("reward_state"));
		    train.setRobbedNum(row.getInt("robbed_num"));
		    train.setStartTime(row.getLong("start_time"));
		    train.setTrainExp(row.getInt("train_exp"));
		    train.setType(row.getInt("type"));		    
		    train.setIsLeague(row.getInt("is_league"));
		    train.setTrainHour(row.getInt("train_hour"));
		    train.setBlId(row.getInt("bl_id"));
		    train.setClear(row.getInt("clear"));
			return train;
		}
	};
	
	
    private RowHandler<LeagueTrain> LEAGUETRAIN = new RowHandler<LeagueTrain>() {
        
        @Override
        public LeagueTrain handleRow(ResultSetRow row) throws Exception {
            LeagueTrain leagueTrain = new LeagueTrain();
            leagueTrain.setBlId(row.getInt("bl_id"));
            leagueTrain.setLeagueId(row.getInt("league_id"));
            leagueTrain.setBtId(row.getInt("bt_id"));
         
            return leagueTrain;
        }
    };  
    
	public List<TeamTrain> getALlTeamTrain() {
		String sql = "select * from t_u_team_train";
		return queryForList(sql, TEAMTRAIN);
	}
	
	public List<Train> getALlTrain() {
        String sql = "select * from t_u_train";
        return queryForList(sql, TRAIN);
    }
	
	public List<Train> getTrainByTeamId(long teamId) {
        String sql = "select * from t_u_train where team_id = ?";
        return queryForList(sql, TRAIN, teamId);
    }
	
	public TeamTrain getTeamTrainByTeamId(long teamId) {
        String sql = "select * from t_u_team_train where team_id = ?";
        return queryForObject(sql, TEAMTRAIN, teamId);
    }
	
	public List<LeagueTrain> getLeagueTrainList() {
        String sql = "select * from t_u_league_train";
        return queryForList(sql, LEAGUETRAIN);
    }

    public void clearLeagueTrain() {
        String sql = "update t_u_league_train set league_id = 0, bt_id = 0";
        execute(sql);
    }
}
