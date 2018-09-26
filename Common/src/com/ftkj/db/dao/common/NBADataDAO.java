package com.ftkj.db.dao.common;

import java.util.List;

import org.joda.time.DateTime;

import com.ftkj.db.conn.dao.NBAConnectionDAO;
import com.ftkj.db.conn.dao.ResultSetRow;
import com.ftkj.db.conn.dao.RowHandler;
import com.ftkj.db.domain.NBAGameGuess;
import com.ftkj.db.domain.NBAPKSchedule;
import com.ftkj.db.domain.NBAPKScoreBoard;
import com.ftkj.db.domain.NBAPKScoreBoardDetail;
import com.ftkj.db.domain.NBAVS;
import com.ftkj.db.domain.bean.PlayerBeanVO;
import com.ftkj.db.domain.bean.PlayerMoneyBeanPO;
import com.ftkj.util.DateTimeUtil;

/**
 * @author tim.huang
 * 2017年3月15日
 */
public class NBADataDAO extends NBAConnectionDAO {
    RowHandler<PlayerBeanVO> PLAYERBEANPO = new RowHandler<PlayerBeanVO>() {

        @Override
        public PlayerBeanVO handleRow(ResultSetRow row) throws Exception {
            PlayerBeanVO po = new PlayerBeanVO();
            po.setAst(row.getInt("ast"));
            po.setAttrCap(row.getInt("attr_cap"));
            po.setBeforePrice(row.getInt("before_price"));
            po.setBlk(row.getInt("blk"));
            po.setCap(row.getInt("cap"));
            po.setDreb(row.getInt("dreb"));
            po.setFgm(row.getInt("fgm"));
            po.setFtm(row.getInt("ftm"));
            po.setGrade(row.getString("grade"));
            po.setGuaCap(row.getInt("gua_cap"));
            po.setInjured(row.getInt("injured"));
            po.setMin(row.getInt("min"));
            po.setName(row.getString("name"));
            po.setOreb(row.getInt("oreb"));
            po.setPf(row.getInt("pf"));
            po.setPlayerId(row.getInt("player_id"));
            po.setPlayerType(row.getInt("player_type"));
            po.setPosition(row.getString("position"));
            po.setPrice(row.getInt("price"));
            po.setPts(row.getInt("pts"));
            po.setShortName(row.getString("short_name"));
            po.setStl(row.getInt("stl"));
            po.setTeamId(row.getInt("team_id"));
            po.setThreePm(row.getInt("three_pm"));
            po.setTo(row.getInt("to"));
            po.setBeforeCap(row.getInt("before_cap"));
            po.setSkill(row.getInt("plus"));
            po.setEnName(row.getString("ename"));
            po.setDraft(row.getString("draft"));
            return po;
        }
    };
    RowHandler<PlayerBeanVO> PLAYERBEANPOAVG = new RowHandler<PlayerBeanVO>() {

        @Override
        public PlayerBeanVO handleRow(ResultSetRow row) throws Exception {
            PlayerBeanVO po = new PlayerBeanVO();
            po.setAst(row.getFloat("ast"));
            po.setBlk(row.getFloat("blk"));
            po.setDreb(row.getFloat("dreb"));
            po.setFgm(row.getFloat("fgm"));
            po.setFtm(row.getFloat("ftm"));
            po.setMin(row.getFloat("min"));
            po.setOreb(row.getFloat("oreb"));
            po.setPf(row.getFloat("pf"));
            po.setPlayerId(row.getInt("player_id"));
            po.setPts(row.getFloat("pts"));
            po.setStl(row.getFloat("stl"));
            po.setThreePm(row.getFloat("three_pm"));
            po.setThreePa(row.getFloat("three_pa"));
            po.setTo(row.getFloat("to"));
            return po;
        }
    };

    private RowHandler<PlayerMoneyBeanPO> PLAYERMONEYBEANPO = new RowHandler<PlayerMoneyBeanPO>() {

        @Override
        public PlayerMoneyBeanPO handleRow(ResultSetRow row) throws Exception {
            PlayerMoneyBeanPO po = new PlayerMoneyBeanPO();
            po.setMoney(row.getInt("price"));
            po.setMoneyTime(new DateTime(row.getTimestamp("time")));
            po.setPlayerId(row.getInt("player_id"));
            return po;
        }
    };

    private RowHandler<NBAPKSchedule> NBAPKSCHEDULE = new RowHandler<NBAPKSchedule>() {

        @Override
        public NBAPKSchedule handleRow(ResultSetRow row) throws Exception {
            NBAPKSchedule po = new NBAPKSchedule();
            po.setAwayTeamId(row.getInt("away_team_id"));
            po.setAwayTeamScore(row.getInt("away_team_score"));
            po.setGameId(row.getInt("game_id"));
            po.setGameTime(new DateTime(row.getTimestamp("game_time")));
            po.setGameType(row.getInt("game_type"));
            po.setHomeTeamId(row.getInt("home_team_id"));
            po.setHomeTeamScore(row.getInt("home_team_score"));
            po.setSeasonId(row.getInt("season_id"));
            po.setStatus(row.getInt("status"));
            return po;
        }
    };

    private RowHandler<NBAPKScoreBoard> NABPKSCOREBOADRD = new RowHandler<NBAPKScoreBoard>() {

        @Override
        public NBAPKScoreBoard handleRow(ResultSetRow row) throws Exception {
            NBAPKScoreBoard po = new NBAPKScoreBoard();
            po.setGameId(row.getInt("game_id"));
            po.setOt1(row.getInt("ot1"));
            po.setOt2(row.getInt("ot2"));
            po.setOt3(row.getInt("ot3"));
            po.setOt4(row.getInt("ot4"));
            po.setOt5(row.getInt("ot5"));
            po.setOt6(row.getInt("ot6"));
            po.setOt7(row.getInt("ot7"));
            po.setQ1(row.getInt("quarter1"));
            po.setQ2(row.getInt("quarter2"));
            po.setQ3(row.getInt("quarter3"));
            po.setQ4(row.getInt("quarter4"));
            po.setTeamId(row.getInt("team_id"));
            po.setTotal(row.getInt("total"));
            return po;
        }
    };

    private RowHandler<NBAPKScoreBoardDetail> NBAPKSCOREBOARDDETAIL = new RowHandler<NBAPKScoreBoardDetail>() {

        @Override
        public NBAPKScoreBoardDetail handleRow(ResultSetRow row) throws Exception {
            NBAPKScoreBoardDetail po = new NBAPKScoreBoardDetail();
            po.setAst(row.getInt("ast"));
            po.setBlk(row.getInt("blk"));
            po.setDreb(row.getInt("dreb"));
            po.setEffectPoint(row.getInt("effect_point"));
            po.setFga(row.getInt("fga"));
            po.setFgm(row.getInt("fgm"));
            po.setFta(row.getInt("fta"));
            po.setFtm(row.getInt("ftm"));
            po.setGameId(row.getInt("game_id"));
            po.setId(row.getInt("id"));
            po.setIsStarter(row.getInt("is_starter"));
            po.setMin(row.getInt("min"));
            po.setOreb(row.getInt("oreb"));
            po.setPf(row.getInt("pf"));
            po.setPlayerId(row.getInt("player_id"));
            po.setPts(row.getInt("pts"));
            po.setReb(row.getInt("reb"));
            po.setStl(row.getInt("stl"));
            po.setTeamId(row.getInt("team_id"));
            po.setThreePa(row.getInt("three_pa"));
            po.setThreePm(row.getInt("three_pm"));
            po.setTo(row.getInt("to"));
            return po;
        }
    };
    private RowHandler<NBAPKScoreBoardDetail> NBAPKSCOREBOARDDETAILLIMIT = new RowHandler<NBAPKScoreBoardDetail>() {

        @Override
        public NBAPKScoreBoardDetail handleRow(ResultSetRow row) throws Exception {
            NBAPKScoreBoardDetail po = new NBAPKScoreBoardDetail();
            po.setAst(row.getInt("ast"));
            po.setBlk(row.getInt("blk"));
            po.setDreb(row.getInt("dreb"));
            po.setEffectPoint(row.getInt("effect_point"));
            po.setFga(row.getInt("fga"));
            po.setFgm(row.getInt("fgm"));
            po.setFta(row.getInt("fta"));
            po.setFtm(row.getInt("ftm"));
            po.setGameId(row.getInt("game_id"));
            po.setId(row.getInt("id"));
            po.setIsStarter(row.getInt("is_starter"));
            po.setMin(row.getInt("min"));
            po.setOreb(row.getInt("oreb"));
            po.setPf(row.getInt("pf"));
            po.setPlayerId(row.getInt("player_id"));
            po.setPts(row.getInt("pts"));
            po.setReb(row.getInt("reb"));
            po.setStl(row.getInt("stl"));
            po.setTeamId(row.getInt("team_id"));
            po.setThreePa(row.getInt("three_pa"));
            po.setThreePm(row.getInt("three_pm"));
            po.setTo(row.getInt("to"));
            po.setTime(new DateTime(row.getTimestamp("game_time")));
            return po;
        }
    };

    private RowHandler<NBAVS> NBAVS2 = new RowHandler<NBAVS>() {

        @Override
        public NBAVS handleRow(ResultSetRow row) throws Exception {
            NBAVS po = new NBAVS();
            po.setAway(row.getString("away"));
            po.setGameId(row.getInt("game_id"));
            po.setDateTime(new DateTime(row.getTimestamp("date_time")));
            po.setHome(row.getString("home"));
            return po;
        }
    };
    
    private RowHandler<NBAGameGuess> NBAGAMEGUESS = new RowHandler<NBAGameGuess>() {

        @Override
        public NBAGameGuess handleRow(ResultSetRow row) throws Exception {
        	NBAGameGuess po = new NBAGameGuess();
        	po.setId(row.getInt("id"));
            po.setHomeName(row.getString("home_team_name"));
            po.setRoadName(row.getString("road_team_name"));
            po.setGameResult(row.getInt("game_result"));
            po.setCancel(row.getInt("cancel"));
            po.setRewardConfig(row.getString("reward"));
            po.setSendReward(row.getInt("send_reward"));
            po.setStartDateTime(new DateTime(row.getTimestamp("start_date")));
            po.setEndDateTime(new DateTime(row.getTimestamp("end_date")));
            return po;
        }
    };
    
    RowHandler<PlayerBeanVO> PLAYERINFOBEANPO = new RowHandler<PlayerBeanVO>() {

        @Override
        public PlayerBeanVO handleRow(ResultSetRow row) throws Exception {
            PlayerBeanVO po = new PlayerBeanVO();
            po.setTeamId(row.getInt("team_id"));
            po.setPlayerId(row.getInt("player_id"));
            po.setPrice(row.getInt("price"));
            return po;
        }
    };
    
    public List<PlayerBeanVO> getPlayerInfoList(){
    	String sql = "select team_id, player_id, price from player_info where team_id > 0";
    	return queryForList(sql, PLAYERINFOBEANPO);
    }

    public List<NBAVS> getNBAVSDetailList() {
        String sql = "select * from data_game_vs where date_time >=now()";
        return queryForList(sql, NBAVS2);
    }
    
    public List<NBAGameGuess> getNBAGameGuessDetailList() {
        String sql = "select * from t_u_active_game_guess "
        		+ "where cancel=0 "
        		+ "and (send_reward is NULL OR send_reward <> 1) "
        		+ "and NOW()<=end_date AND (now()>=start_date OR to_days(now())=to_days(start_date))";
        return queryForList(sql, NBAGAMEGUESS);
    }
    
    public NBAGameGuess getNbaGameGuessById(int id){
    	String sql = "select * from t_u_active_game_guess where id=? and cancel=0 "
    			+ "and (send_reward is NULL OR send_reward <> 1) "
    			+ "and NOW()<=end_date AND (now()>=start_date OR to_days(now())=to_days(start_date))";
        return queryForObject(sql, NBAGAMEGUESS, id);
    }
    
    public NBAGameGuess selectNbaGameGuessById(int id){
    	String sql = "select * from t_u_active_game_guess where id=?";
        return queryForObject(sql, NBAGAMEGUESS, id);
    }

    public List<NBAPKSchedule> getAllNBAPKSchedule() {
        String sql = "select * from x_data_game_schedule ";
        return queryForList(sql, NBAPKSCHEDULE);
    }

    public List<NBAPKScoreBoard> getAllNBAPKScoreBoard() {
        String sql = "select * from x_data_score_board ";
        return queryForList(sql, NABPKSCOREBOADRD);//TODO 需要解决错误使用 jdbc 连接池的问题
    }

    public List<NBAPKScoreBoardDetail> getAllNBAPKScoreBoardDetail() {
        String sql = "select * from x_data_score_board_detail ";
        return queryForList(sql, NBAPKSCOREBOARDDETAIL);
    }

    public List<NBAPKScoreBoardDetail> getNBAScoreBoardDetailLimit(int playerId) {
        String sql = "select a.game_time,b.* from data_game_schedule a,data_score_board_detail b where a.game_id = b.game_id  and a.season_id = 2017 and b.player_id = ? order by a.game_time desc limit 5;";
        return queryForList(sql, NBAPKSCOREBOARDDETAILLIMIT, playerId);
    }
    
    /**
     * 随机赛程的最近5场，赛季要更新，现在2017
     * @param playerId
     * @param dateList
     * @return
     */
    public List<NBAPKScoreBoardDetail> getNBAScoreBoardDetailLimit(int playerId, List<String> dateList) {
    	//
    	StringBuilder sb = new StringBuilder("\"");
    	for(String date : dateList) {
    		sb.append(date).append(",");
    	}
    	if(sb.length() > 1) {
    		sb.deleteCharAt(sb.length()-1);
    	}
    	sb.append("\"");
    	StringBuilder sqlBuilder = new StringBuilder("select a.game_time,b.* from data_game_schedule a,data_score_board_detail b where a.game_id = b.game_id  and a.season_id = 2017 and b.player_id = ? order by find_in_set(date(a.game_time), ");
    	sqlBuilder.append(sb).append(") desc, a.game_time desc limit 5;");
    	//
        //String sql = "select a.game_time,b.* from data_game_schedule a,data_score_board_detail b where a.game_id = b.game_id  and a.season_id = 2017 and b.player_id = ? order by date(a.game_time) in(?) desc, a.game_time desc limit 5;";
        return queryForList(sqlBuilder.toString(), NBAPKSCOREBOARDDETAILLIMIT, playerId);
    }

    public List<PlayerBeanVO> getAllPlayerBean() {
        String sql = "select * from t_v_player_info where team_id >=0";
        return queryForList(sql, PLAYERBEANPO);
    }

    /**
     * 2018新秀显示2018赛季.
     * @return
     */
    public List<PlayerBeanVO> getAllPlayerAvgBean() {
         // String sql = "select * from data_player_avg where season_id = 2017";
    	// 兼容新秀的写法
        String sql = "select * from data_player_avg where (season_id = 2017 and player_id not in(select player_id from player_info where draft like '2018_%')) " + 
        		" or (season_id = 2018 and player_id in (select player_id from player_info where draft like '2018_%')) "; 
        return queryForList(sql, PLAYERBEANPOAVG);
    }

    public PlayerBeanVO getMaxPlayerAvgBean() {
        String sql = "select max(ast) as ast,max(blk) as blk,max(dreb) as dreb,max(fgm) as fgm,max(ftm) as ftm " +
                ",max(`min`) as `min`,max(oreb) as oreb,max(pf) as pf,max(pts) as pts " +
                ",max(stl) as stl, max(three_pm) as three_pm, max(three_pa) as three_pa " +
                ",max(`to`) as `to`,0 as player_id from data_player_avg where season_id =2017";
        return queryForObject(sql, PLAYERBEANPOAVG);
    }

    public List<PlayerMoneyBeanPO> getAllPlayerMoneyBean() {
        String date = DateTimeUtil.getString(DateTime.now().plusDays(-31), DateTimeUtil.y_m_d);
        String sql = "select player_id,time,price from player_money where time>= ?";
        return queryForList(sql, PLAYERMONEYBEANPO, date);
    }

    public List<NBAPKScoreBoardDetail> getNBAPKScoreBoardDetailByPlayer(
            int playerId) {
        String sql = "select * from data_score_board_detail where player_id = ? order by game_id desc limit 20;";
        return queryForList(sql, NBAPKSCOREBOARDDETAIL, playerId);
    }
    
    /**
	 * 取指定赛季，比赛场次数不少于次数，的有比赛的日期，用来做随机身价，随机指定天数的比赛.
	 * @param seasionId
	 * @param minVs
	 * @return
	 */
	public List<String> getRandSecheduleDateList(int seasionId, String startTime, String endTime, int minVs) {
		String sql = "select date(game_time) as gametime from data_game_schedule where date(game_time)>=? and date(game_time)<=? group by date(game_time) having count(1)>? order by rand(?)";
		return queryForList(sql, new RowHandler<String>() {
			@Override
			public String handleRow(ResultSetRow row) throws Exception {
				return row.getString("gametime");
			}
		}, startTime, endTime, minVs, seasionId);
	}
}
