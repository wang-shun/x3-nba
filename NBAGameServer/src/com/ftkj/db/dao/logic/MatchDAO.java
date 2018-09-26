package com.ftkj.db.dao.logic;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;

import com.ftkj.db.conn.dao.GameConnectionDAO;
import com.ftkj.db.conn.dao.ResultSetRow;
import com.ftkj.db.conn.dao.RowHandler;
import com.ftkj.db.domain.match.MatchBestPO;
import com.ftkj.db.domain.match.MatchPKPO;
import com.ftkj.db.domain.match.MatchPO;
import com.ftkj.db.domain.match.MatchSignPO;

public class MatchDAO extends GameConnectionDAO {

	
	private RowHandler<MatchPO> MATCHPO = new RowHandler<MatchPO>() {
		
		@Override
		public MatchPO handleRow(ResultSetRow row) throws Exception {
			MatchPO po = new MatchPO();
			po.setSeqId(row.getInt("seq_id")); 
			po.setMatchId(row.getInt("match_id")); 
			po.setStatus(row.getInt("status")); 
			po.setRound(row.getInt("round")); 
			po.setMaxRound(row.getInt("max_round")); 
			po.setCreateTime(new DateTime(row.getTimestamp("create_time"))); 
			po.setMatchTime(new DateTime(row.getTimestamp("match_time"))); 
			po.setLogicName((row.getString("logic_name"))); 
			return po;
		}
	};
	
	private RowHandler<MatchSignPO> MATCHSIGNPO = new RowHandler<MatchSignPO>() {
		
		@Override
		public MatchSignPO handleRow(ResultSetRow row) throws Exception {
			MatchSignPO po = new MatchSignPO();
			po.setSeqId(row.getInt("seq_id")); 
			po.setMatchId(row.getInt("match_id")); 
			po.setTeamId(row.getLong("team_id")); 
			po.setTeamCap(row.getInt("team_cap")); 
			po.setStatus(row.getInt("status")); 
			po.setRank(row.getInt("rank")); 
			po.setCreateTime(new DateTime(row.getTimestamp("create_time")));
			
			return po;
		}
	};
	
	private RowHandler<MatchPKPO> MATCHPKPO = new RowHandler<MatchPKPO>() {

		@Override
		public MatchPKPO handleRow(ResultSetRow row) throws Exception {
			MatchPKPO po = new MatchPKPO();
			po.setBattleId(row.getLong("battle_id")); 
			po.setSeqId(row.getInt("seq_id")); 
			po.setMatchId(row.getInt("match_id")); 
			po.setRound(row.getInt("round")); 
			po.setHomeId(row.getLong("home_id")); 
			po.setAwayId(row.getLong("away_id")); 
			po.setWinTeamId(row.getLong("win_team_id")); 
			po.setHomeScore(row.getInt("home_score")); 
			po.setAwayScore(row.getInt("away_score")); 
			po.setStatus(row.getInt("status")); 
			po.setCreateTime(new DateTime(row.getTimestamp("create_time"))); 
			po.setEndTime(new DateTime(row.getTimestamp("end_time"))); 
			
			return po;
		}
	};
	
	private RowHandler<MatchBestPO> MATCHBESTPO = new RowHandler<MatchBestPO>() {

		@Override
		public MatchBestPO handleRow(ResultSetRow row) throws Exception {
			MatchBestPO po = new MatchBestPO();
			po.setMatchId(row.getInt("match_id")); 
			po.setTeamId(row.getLong("team_id")); 
			po.setRank(row.getInt("rank")); 
			po.setUpdateTime(new DateTime(row.getTimestamp("update_time"))); 
			
			return po;
		}
	};
	
	/**
	 * 30天以内创建的比赛
	 * @return
	 */
	public List<MatchPO> getMatchPOList() {
		String sql = "select * from t_u_match where status <> 3";
		return queryForList(sql, MATCHPO);
	}
	
	/**
	 * 查询最大序列
	 * @return
	 */
	public Map<String, String> getMatchSeq() {
		String sql = "select match_id,max(seq_id) from t_u_match group by match_id;";
		return queryForMap(sql);
	}
	
	/**
	 * 查询指定比赛
	 * @param seqId
	 * @param matchId
	 * @return
	 */
	public MatchPO getMatchPO(int seqId, int matchId) {
		String sql = "select * from t_u_match where seq_id=? and match_id=?";
		return queryForObject(sql, MATCHPO, seqId, matchId);
	}
	
	public List<MatchSignPO> getMatchSignPOList(int seqId, int matchId) {
		String sql = "select * from t_u_match_sign where seq_id=? and match_id=?";
		return queryForList(sql, MATCHSIGNPO, seqId, matchId);
	}
	
	public List<MatchSignPO> getMatchSignPOListRank(int seqId, int matchId, int size) {
		String sql = "select * from t_u_match_sign where seq_id=? and match_id=? and rank>0 order by rank limit ?";
		return queryForList(sql, MATCHSIGNPO, seqId, matchId, size);
	}
	
	public List<MatchPKPO> getMatchPKPOList(int seqId, int matchId) {
		String sql = "select * from t_u_match_pk where seq_id=? and match_id=?";
		return queryForList(sql, MATCHPKPO, seqId, matchId);
	}
	
	public List<MatchBestPO> getMatchBestPOList(int matchId, Collection<Long> teamList) {
		StringBuilder sb = new StringBuilder();
		for(long teamId : teamList) {
			sb.append(teamId).append(",");
		}
		if(sb.length() > 1) {
			sb.deleteCharAt(sb.length()-1);
		}
		String sql = "select * from t_u_match_best where match_id=? and team_id in (?)";
		return queryForList(sql, MATCHBESTPO, matchId, sb.toString());
	}

	public List<MatchBestPO> getTeamMatchBestList(long teamId) {
		String sql = "select * from t_u_match_best where team_id=?";
		return queryForList(sql, MATCHBESTPO, teamId);
	}


}
