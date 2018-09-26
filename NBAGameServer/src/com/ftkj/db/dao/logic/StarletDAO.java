package com.ftkj.db.dao.logic;

import java.util.List;

import com.ftkj.db.conn.dao.GameConnectionDAO;
import com.ftkj.db.conn.dao.ResultSetRow;
import com.ftkj.db.conn.dao.RowHandler;
import com.ftkj.manager.starlet.StarletDualMeet;
import com.ftkj.manager.starlet.StarletPlayer;
import com.ftkj.manager.starlet.StarletRank;
import com.ftkj.manager.starlet.StarletTeamRedix;

/**
 * @author tim.huang
 * 2017年9月27日
 *
 */
public class StarletDAO extends GameConnectionDAO {
	
	
	private RowHandler<StarletDualMeet> STARLETDUALMEET = new RowHandler<StarletDualMeet>() {
		
		@Override
		public StarletDualMeet handleRow(ResultSetRow row) throws Exception {
		    StarletDualMeet starletDualMeet = new StarletDualMeet();
		    starletDualMeet.setTeamId(row.getLong("team_id"));
		    starletDualMeet.setPlayerRid(row.getInt("player_rid"));
		    starletDualMeet.setAst(row.getInt("ast"));
		    starletDualMeet.setBlk(row.getInt("blk"));
		    starletDualMeet.setPf(row.getInt("pf"));
		    starletDualMeet.setPts(row.getInt("pts"));
		    starletDualMeet.setReb(row.getInt("reb"));
		    starletDualMeet.setStl(row.getInt("stl"));
		    starletDualMeet.setTo(row.getInt("to"));
		    starletDualMeet.setTotal(row.getInt("total"));
            return starletDualMeet;	
		}
	};
	
	
	private RowHandler<StarletPlayer> STARLETPLAYER = new RowHandler<StarletPlayer>() {
        
        @Override
        public StarletPlayer handleRow(ResultSetRow row) throws Exception {
            StarletPlayer starletPlayer = new StarletPlayer();
            starletPlayer.setTeamId(row.getLong("team_id"));
            starletPlayer.setPlayerRid(row.getInt("player_rid"));
            starletPlayer.setCap(row.getInt("cap"));           
            starletPlayer.setLineupPosition(row.getString("lineup_position"));
            return starletPlayer;         
        }
    };
    
	
    private RowHandler<StarletRank> STARLETRANK = new RowHandler<StarletRank>() {
        
        @Override
        public StarletRank handleRow(ResultSetRow row) throws Exception {
            StarletRank starletRank = new StarletRank();
            starletRank.setRank(row.getInt("rank"));
            starletRank.setTeamId(row.getLong("team_id"));
            return starletRank;         
        }
    };
    
    private RowHandler<StarletTeamRedix> STARLETTEAMREDIX = new RowHandler<StarletTeamRedix>() {
        
        @Override
        public StarletTeamRedix handleRow(ResultSetRow row) throws Exception {
            StarletTeamRedix starletTeamRedix = new StarletTeamRedix();
            starletTeamRedix.setTeamId(row.getLong("team_id"));      
            starletTeamRedix.setRedixNum(row.getInt("redix_num"));
            starletTeamRedix.setCardType(row.getInt("card_type"));
            return starletTeamRedix;
        }
    };
	
	public List<StarletTeamRedix> getStarletTeamRedixList(long teamId) {
		String sql = "select * from t_u_starlet_team_redix where team_id = ?";
		return queryForList(sql, STARLETTEAMREDIX, teamId);
	}
    
	public List<StarletRank> getStarletRankList() {
        String sql = "select * from t_u_starlet_rank";
        return queryForList(sql, STARLETRANK);
    }
	
	public List<StarletPlayer> getStarletPlayerList(long teamId) {
        String sql = "select * from t_u_starlet_player where team_id = ?";
        return queryForList(sql, STARLETPLAYER, teamId);
    }
	
	public List<StarletDualMeet> getStarletDualMeetList(long teamId) {
        String sql = "select * from t_u_starlet_dual_meet where team_id = ?";
        return queryForList(sql, STARLETDUALMEET, teamId);
    }

   public void deleteStarletPlayer(long tid, Integer pid) {
       String sql = "delete from t_u_starlet_player where team_id =? and player_rid = ?";
       execute(sql, tid, pid);
   }
   
   public void deleteStarletDualMeet(long tid, Integer prid) {
       String sql = "delete from t_u_starlet_dual_meet where team_id =? and player_rid = ?";
       execute(sql, tid, prid);
   }
}
