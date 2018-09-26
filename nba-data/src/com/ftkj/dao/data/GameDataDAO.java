package com.ftkj.dao.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;
import com.ftkj.conn.DataConnectionDAO;
import com.ftkj.conn.ResultSetRow;
import com.ftkj.conn.ResultSetRowHandler;
import com.ftkj.domain.data.GameDataJobRunLog;
import com.ftkj.domain.data.GameVS;
import com.ftkj.domain.data.MatchData;
import com.ftkj.domain.data.NBAGameDetail;
import com.ftkj.domain.data.NBAPlayerScore;
import com.ftkj.domain.data.NBATeamDetail;
import com.ftkj.domain.data.NBATeamScore;
import com.ftkj.domain.data.PlayerAvgRate;
import com.ftkj.domain.data.PlayerPrice;
import com.ftkj.domain.data.PlayerStats;
import com.ftkj.domain.data.TeamScore;
import com.ftkj.jredis.Key;


public class GameDataDAO extends DataConnectionDAO {

	public GameDataDAO() {}


	private static final ResultSetRowHandler<GameDataJobRunLog> ROW_HANDLER	= new ResultSetRowHandler<GameDataJobRunLog>() {
		@Override
		public GameDataJobRunLog handleRow(ResultSetRow row) throws Exception {
			GameDataJobRunLog log=new GameDataJobRunLog();
			log.setId(row.getInt("id"));				
			log.setRunTime(row.getTimestamp("run_time"));
			log.setGameTime(row.getTimestamp("game_time"));
			//System.out.println("0=="+UtilDateTime.toDateString(log.getGameTime(), UtilDateTime.SIMPLEFORMATSTRING));
			return log;
		}
	};

	private static final ResultSetRowHandler<NBAGameDetail> NBAGameInfo_HANDLER =	new ResultSetRowHandler<NBAGameDetail>() {
		@Override
		public NBAGameDetail handleRow(ResultSetRow row) throws Exception {
			NBAGameDetail log=new NBAGameDetail();
			log.setGameBoxId(row.getInt("game_id")+"");
			log.setHomeTeamId(row.getInt("home_team_id"));
			log.setAwayTeamId(row.getInt("away_team_id"));
			log.setHomeTeamScore(row.getInt("home_team_score"));
			log.setAwayTeamScore(row.getInt("away_team_score"));
			log.setStatus("Final");
			log.setDateTime(row.getTimestamp("game_time"));
			return log;
		}
	};

	private static final ResultSetRowHandler<GameVS> VS_HANDLER	= new ResultSetRowHandler<GameVS>() {
		@Override
		public GameVS handleRow(ResultSetRow row) throws Exception {
			GameVS log=new GameVS();
			log.setGameId(row.getInt("game_id"));
			log.setHome(row.getString("home"));
			log.setHomeName(row.getString("home_name"));
			log.setAway(row.getString("away"));
			log.setAwayName(row.getString("away_name"));
			log.setTime(row.getTimestamp("date_time"));
			return log;
		}
	};

	private static final ResultSetRowHandler<Integer>SCHEDULE_HANDLER=	new ResultSetRowHandler<Integer>() {
		@Override
		public Integer handleRow(ResultSetRow row) throws Exception {
			return row.getInt("game_id");
		}
	};

	private static final ResultSetRowHandler<Integer> INTEGER_HANDLER=new ResultSetRowHandler<Integer>() {
		@Override
		public Integer handleRow(ResultSetRow row) throws Exception {
			return row.getInt("num");
		}
	};
	private static final ResultSetRowHandler<PlayerPrice>PRICE_HANDLER=	new ResultSetRowHandler<PlayerPrice>() {
		@Override
		public PlayerPrice handleRow(ResultSetRow row) throws Exception {
			PlayerPrice t=new PlayerPrice();
			t.setPlayerId(row.getInt("player_id"));
			t.setPrice(row.getInt("pricex"));
			return t;
		}
	};
	/*
	//
	private static final ResultSetRowHandler<NBAGameSchedule> HANDLER=	new ResultSetRowHandler<NBAGameSchedule>() {
			@Override
			public NBAGameSchedule handleRow(ResultSetRow rows) throws Exception {
				NBAGameSchedule gameSchedule=new NBAGameSchedule();
				gameSchedule.setGameId(rows.getInt("game_id"));
				gameSchedule.setGameType(rows.getInt("game_type"));
				gameSchedule.setSeasonId(rows.getInt("season_id"));
				gameSchedule.setHomeTeamId(rows.getInt("home_team_id"));
				gameSchedule.setAwayTeamId(rows.getInt("away_team_id"));
				gameSchedule.setHomeScore(rows.getInt("home_team_score"));
				gameSchedule.setAwayScore(rows.getInt("away_team_score"));	
				gameSchedule.setCreateTime(rows.getTimestamp("create_time"));
				gameSchedule.setGameTime(rows.getTimestamp("game_time"));
				return gameSchedule;
			}
	};
	 */
	private static final ResultSetRowHandler<NBAPlayerScore> NPS_HANDLER = new ResultSetRowHandler<NBAPlayerScore>() {
		@Override
		public NBAPlayerScore handleRow(ResultSetRow row) throws Exception {
			NBAPlayerScore ps=new NBAPlayerScore();
			ps.setGameTime(row.getTimestamp("game_time"));
			ps.setAst(row.getInt("ast"));
			ps.setBlk(row.getInt("blk"));
			ps.setDreb(row.getInt("dreb"));
			ps.setFgA(row.getInt("fga"));
			ps.setFgM(row.getInt("fgm"));
			ps.setFtA(row.getInt("fta"));
			ps.setFtM(row.getInt("ftm"));
			ps.setGameId(row.getInt("game_id"));
			ps.setMin(row.getInt("min"));
			ps.setOreb(row.getInt("oreb"));
			ps.setPf(row.getInt("pf"));
			ps.setPlayerId(row.getInt("player_id"));
			ps.setEffectPoint(row.getInt("effect_point"));
			ps.setPts(row.getInt("pts"));
			ps.setReb(row.getInt("reb"));
			ps.setStarter(row.getBoolean("is_starter"));
			ps.setStl(row.getInt("stl"));
			ps.setTeamId(row.getInt("team_id"));
			ps.setThreePA(row.getInt("three_pa"));
			ps.setThreePM(row.getInt("three_pm"));
			ps.setTo(row.getInt("to"));
			return ps;
		}
	};

	private static final ResultSetRowHandler<PlayerStats> PS_HANDLER = new ResultSetRowHandler<PlayerStats>() {
		@Override
		public PlayerStats handleRow(ResultSetRow row) throws Exception {
			PlayerStats ps=new PlayerStats();			
			ps.setAst(row.getString("ast"));
			ps.setBlk(row.getString("blk"));
			ps.setDreb(row.getString("dreb"));
			ps.setFgMA(row.getString("fgm")+"-"+row.getString("fga"));			
			ps.setFtMA(row.getString("ftm")+"-"+row.getString("fta"));				
			ps.setMin(row.getString("min"));
			ps.setOreb(row.getString("oreb"));
			ps.setPf(row.getString("pf"));
			ps.setPlayerId(row.getInt("player_id"));			
			ps.setPts(row.getString("pts"));
			ps.setReb(row.getString("reb"));
			ps.setStarter(row.getBoolean("is_starter"));
			ps.setStl(row.getString("stl"));
			ps.setTeamId(row.getInt("team_id"));
			ps.setThreePMA(row.getString("three_pm")+"-"+row.getString("three_pa"));			
			ps.setTo(row.getString("to"));
			ps.setPlus(row.getString("effect_point"));
			return ps;
		}
	};

	private static final ResultSetRowHandler<NBATeamScore>TS_HANDLER=new ResultSetRowHandler<NBATeamScore>() {
		@Override
		public NBATeamScore handleRow(ResultSetRow row) throws Exception {
			NBATeamScore ts=new NBATeamScore();
			ts.setSeasonId(row.getInt("season_id"));
			ts.setGameTime(row.getTimestamp("game_time"));
			ts.setGameId(row.getInt("game_id"));
			ts.setTeamId(row.getInt("team_id"));
			ts.setScore(row.getInt("total"));
			for(int i=0;i<4;i++){
				ts.getStep()[i]=row.getInt("quarter"+(i+1));
			}
			/*
				for(int i=0;i<8;i++){
					ts.getOt()[i]=row.getInt("ot"+(i+1));
				}*/
			return ts;
		}
	};

	//
	//
	private static final ResultSetRowHandler<PlayerAvgRate>PLAYER_AVG_ROW_HANDLER=	new ResultSetRowHandler<PlayerAvgRate>() {
		@Override
		public PlayerAvgRate handleRow(ResultSetRow row) throws Exception {
			PlayerAvgRate avg=new PlayerAvgRate();
			avg.setGameType(row.getInt("game_type"));
			avg.setPlayerId(row.getInt("player_id"));
			avg.setSeasonId(row.getInt("season_id"));
			avg.setTeamId(row.getInt("team_id"));
			avg.setPlayCount(row.getInt("play_count"));
			avg.setStarterCount(row.getInt("starter_count"));
			avg.setFgm(row.getFloat("fgm"));
			avg.setFga(row.getFloat("fga"));
			avg.setFtm(row.getFloat("ftm"));
			avg.setFta(row.getFloat("fta"));
			avg.setThreePm(row.getFloat("three_pm"));
			avg.setThreePa(row.getFloat("three_pa"));
			avg.setOreb(row.getFloat("oreb"));
			avg.setDreb(row.getFloat("dreb"));
			avg.setAst(row.getFloat("ast"));
			avg.setStl(row.getFloat("stl"));
			avg.setBlk(row.getFloat("blk"));
			avg.setTo(row.getFloat("to"));
			avg.setMin(row.getInt("min"));
			avg.setPf(row.getFloat("pf"));
			avg.setPts(row.getFloat("pts"));
			return avg;
		}
	};

	/**
	 * 清空今日比赛数据
	 */
	public void clearTodayData(){
		execute("delete from x_data_game_schedule");
		execute("delete from x_data_score_board");
		execute("delete from x_data_score_board_detail");
	}

	//
	public void addGameData(MatchData gameData){
		String sql="insert into data_game_schedule(" +
		"game_id," +
		"season_id," +
		"game_type," +
		"home_team_id," +
		"away_team_id," +
		"home_team_score," +
		"away_team_score," +
		"game_time)values(" +
		"?,?,?,?,?,?,?,?)";
		execute(sql, 
				gameData.getGameBoxId(),
				gameData.getSeasonId(),
				gameData.getGameType(),
				gameData.getHomeTeamId(),
				gameData.getAwayTeamId(),
				gameData.getHomeScore(),
				gameData.getAwayScore(),
				gameData.getGameDate());	
	}



	public List<Integer> getSchedule(){
		return queryForList("select game_id from data_game_schedule", SCHEDULE_HANDLER);
	}

	public void addTeamScore(TeamScore teamScore){
		String sql="insert into data_score_board (" +
		"team_id," +
		"game_id," +
		"quarter1," +
		"quarter2," +
		"quarter3," +
		"quarter4," +
		"ot1," +
		"ot2," +
		"ot3," +
		"ot4," +
		"ot5," +
		"ot6," +
		"ot7," +
		"total)values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		Object[]p=new Object[14];
		p[0]=teamScore.teamId;
		p[1]=teamScore.gameId;
		for(int i=0;i<teamScore.scores.size()-1;i++){
			p[2+i]=teamScore.scores.get(i);
		}
		p[p.length-1]=teamScore.scores.get(teamScore.scores.size()-1);
		execute(sql, p);
	}

	//
	public void addPlayerScore(PlayerStats playerScore){
		try{
			String sql="insert into data_score_board_detail(" +
			"player_id," +
			"game_id," +
			"team_id," +
			"is_starter," +
			"fgm," +
			"fga," +
			"ftm," +
			"fta," +
			"three_pm," +
			"three_pa," +
			"oreb," +
			"dreb," +
			"reb," +
			"ast," +
			"stl," +
			"blk," +
			"`to`," +
			"pf," +
			"pts," +
			"effect_point," +
			"min)values(" +
			"?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			execute(sql,
					playerScore.gamePlayerId,
					playerScore.gameId,
					playerScore.teamId,
					playerScore.isStarter?1:0,
							playerScore.getFGM(),
							playerScore.getFGA(),
							playerScore.getFTM(),
							playerScore.getFTA(),
							playerScore.getThreePM(),
							playerScore.getThreePA(),
							playerScore.oreb==null?0:playerScore.oreb,
									playerScore.dreb==null?0:playerScore.dreb,
											playerScore.reb==null?0:playerScore.reb,
													playerScore.ast,
													playerScore.stl,
													playerScore.blk,
													playerScore.to,
													playerScore.pf,
													playerScore.pts,
													getEffectPoint(playerScore.plus),
													playerScore.min
			);
			executeUpdate("update player_info set injured=0 where player_id=?",playerScore.gamePlayerId);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	private String getEffectPoint(String in){
		if(in==null){
			return null;
		}else if(in.equals("--")){
			return "0";
		}else if(in.equals("N/A")){
			return null;
		}else{
			return in;
		}
	}

	public List<PlayerAvgRate>  getPlayerAvg(int gameType){
		String sql="select "+
		"game_type," +
		"player_id," +
		"season_id," +
		"team_id,"+
		"count(*) play_count,"+
		"sum(is_starter) starter_count,"+
		"sum(fgm)/count(*) fgm,"+
		"sum(fga)/count(*) fga,"+
		"sum(ftm)/count(*) ftm,"+
		"sum(fta)/count(*) fta,"+
		"sum(three_pm)/count(*) three_pm,"+
		"sum(three_pa)/count(*) three_pa,"+
		"sum(oreb)/count(*) oreb,"+
		"sum(dreb)/count(*) dreb,"+
		"sum(ast)/count(*)  ast,"+
		"sum(stl)/count(*)  stl,"+
		"sum(blk)/count(*)  blk,"+
		"sum(`to`)/count(*)  `to`,"+
		"sum(pf)/count(*)  pf,"+
		"sum(pts)/count(*)  pts,"+
		"sum(`min`)/count(*)  min "+
		"from data_score_board_detail a,data_game_schedule b " +
		"where a.game_id=b.game_id ";
		if(gameType!=-1){
			sql+="and b.game_type=? ";
		}
		sql+="group by a.player_id,b.season_id,a.team_id,b.game_type";			
		if(gameType!=-1){
			return queryForList(sql,PLAYER_AVG_ROW_HANDLER,gameType);
		}else{
			return queryForList(sql, PLAYER_AVG_ROW_HANDLER);
		}	
	}

	public List<PlayerAvgRate>  getPlayerTotal(int gameType){
		String sql="select "+
		"game_type,player_id,season_id,team_id,"+
		"count(1) play_count,"+
		"sum(is_starter) starter_count,"+
		"sum(fgm) fgm,"+
		"sum(fga) fga,"+
		"sum(ftm) ftm,"+
		"sum(fta) fta,"+
		"sum(three_pm) three_pm,"+
		"sum(three_pa) three_pa,"+
		"sum(oreb) oreb,"+
		"sum(dreb) dreb,"+
		"sum(ast) ast,"+
		"sum(stl) stl,"+
		"sum(blk) blk,"+
		"sum(`to`) `to`,"+
		"sum(pf) pf,"+
		"sum(pts) pts,"+
		"sum(`min`) min "+
		"from data_score_board_detail a,data_game_schedule b " +
		"where a.game_id=b.game_id ";
		if(gameType!=-1){
			sql+="and b.game_type=? ";
		}
		sql+="group by a.player_id,b.season_id,a.team_id,b.game_type";			
		if(gameType!=-1){
			return queryForList(sql,PLAYER_AVG_ROW_HANDLER,gameType);
		}else{
			return queryForList(sql, PLAYER_AVG_ROW_HANDLER);
		}	
	}

	//
	public List<PlayerAvgRate>  getPlayerSeasonAvg(int gameType){
		String sql="select "+
		"0 player_id," +
		"season_id," +
		"0 team_id,"+
		"0 play_count,"+
		"sum(is_starter)/ count(*) starter_count,"+
		"sum(fgm)/count(*) fgm,"+
		"sum(fga)/count(*) fga,"+
		"sum(ftm)/count(*) ftm,"+
		"sum(fta)/count(*) fta,"+
		"sum(three_pm)/count(*) three_pm,"+
		"sum(three_pa)/count(*) three_pa,"+
		"sum(oreb)/count(*) oreb,"+
		"sum(dreb)/count(*) dreb,"+
		"sum(ast)/count(*)  ast,"+
		"sum(stl)/count(*)  stl,"+
		"sum(blk)/count(*)  blk,"+
		"sum(`to`)/count(*)  `to`,"+
		"sum(pf)/count(*)  pf,"+
		"sum(pts)/count(*)  pts,"+
		"sum(`min`)/count(*)  min "+
		"from data_score_board_detail a,data_game_schedule b " +
		"where a.game_id=b.game_id ";
		if(gameType!=-1){
			sql+="and b.game_type=? ";
			sql+="group by b.season_id";
			return queryForList(sql,PLAYER_AVG_ROW_HANDLER,gameType);
		}else{
			sql+="group by b.season_id";
			return queryForList(sql, PLAYER_AVG_ROW_HANDLER);
		}	
	}
	//
	public List<PlayerAvgRate>  getPlayerSeasonMax(int gameType){
		String sql="select "+
		"0 player_id," +
		"season_id,"+
		"0 team_id,"+
		"1 play_count,"+
		"1 starter_count,"+
		"max(fgm) fgm,"+
		"max(fga) fga,"+
		"max(ftm) ftm,"+
		"max(fta) fta,"+
		"max(three_pm) three_pm,"+
		"max(three_pa) three_pa,"+
		"max(oreb) oreb,"+
		"max(dreb) dreb,"+
		"max(ast) ast,"+
		"max(stl) stl,"+
		"max(blk) blk,"+
		"max(`to`) `to`,"+
		"max(pf) pf,"+
		"max(pts) pts,"+
		"max(min) `min` "+
		"from data_score_board_detail a,data_game_schedule b " +
		"where a.game_id=b.game_id ";
		if(gameType!=-1){
			sql+="and b.game_type=? ";
			sql+="group by b.season_id";
			return queryForList(sql,PLAYER_AVG_ROW_HANDLER,gameType);
		}else{
			sql+="group by b.season_id";
			return queryForList(sql, PLAYER_AVG_ROW_HANDLER);
		}	
	}
	//
	public List<NBATeamScore> queryTeamScores(int gameId){
		return queryForList("select d.game_time,d.season_id,b.* " +
				"from data_score_board b,data_game_schedule d " +
				"where b.game_id=d.game_id and b.game_id=?", TS_HANDLER, gameId);
	}
	//
	public List<NBAPlayerScore> queryPlayerScores(int gameId){
		return queryForList("select d.*,b.game_time,b.season_id " +
				"from data_score_board_detail d,data_game_schedule b " +
				"where b.game_id=d.game_id and b.game_id=?;", NPS_HANDLER,gameId);
	}

	public List<PlayerStats> _queryPlayerScores(int gameId){
		return queryForList("select d.*,b.game_time,b.season_id " +
				"from data_score_board_detail d,data_game_schedule b " +
				"where b.game_id=d.game_id and b.game_id=?;",PS_HANDLER,gameId);
	}

	public List<NBAGameDetail> getSchedule_not_pk(String time){
		return queryForList("select * from data_game_schedule where DATE_FORMAT(game_time,'%Y-%m-%d')=?",NBAGameInfo_HANDLER,time);
	}

	public MatchData getSchedule_not_pk_gameData(NBAGameDetail info){
		int gameId = Integer.parseInt(info.getGameBoxId());
		MatchData data = new MatchData();

		List<NBATeamScore>   team_score = queryTeamScores(gameId);
		List<PlayerStats> 	play_score  = _queryPlayerScores(gameId);

		TeamScore scoreHome = new TeamScore("");
		TeamScore scoreAway = new TeamScore("");
		List<PlayerStats>playerScoreHome = new ArrayList<PlayerStats>();
		List<PlayerStats>playerScoreAway = new ArrayList<PlayerStats>();
		for(NBATeamScore T:team_score){
			if(T.getTeamId()==info.getHomeTeamId()){
				for(int id:T.getStep()){
					scoreHome.scores.add(id);
				}
				/*
				for(int id:T.getOt()){
					scoreHome.scores.add(id);
				}*/
				scoreHome.scores.add(T.getScore());
			}
			if(T.getTeamId()==info.getAwayTeamId()){
				for(int id:T.getStep()){
					scoreAway.scores.add(id);
				}
				/*
				for(int id:T.getOt()){
					scoreAway.scores.add(id);
				}*/
				scoreAway.scores.add(T.getScore());
			}
		}
		for(PlayerStats T:play_score){
			if(T.getTeamId()==info.getHomeTeamId()){
				playerScoreHome.add(T);
			}
			if(T.getTeamId()==info.getAwayTeamId()){
				playerScoreAway.add(T);
			}
		}
		data.setScoreHome(scoreHome);
		data.setScoreAway(scoreAway);
		data.setPlayerScoreHome(playerScoreHome);
		data.setPlayerScoreAway(playerScoreAway);
		data.setGameBoxId(info.getGameBoxId());
		data.setHomeTeamId(info.getHomeTeamId());
		data.setAwayTeamId(info.getAwayTeamId());
		data.setState(MatchData.GAME_FINAL);
		data.setGameDate(info.getDateTime());
		return data;
	}

	//
	public void addRunLog(GameDataJobRunLog log){
		execute("insert into data_job_runlog(run_time,game_time)values(?,?)",log.getRunTime(),log.getGameTime());
	}
	public void updateInjuries(int playerId){
		execute("delete from nba_data.injured where datediff(timex,now()) = 0 and player_id =? and stat=0",playerId);
		execute("insert into nba_data.injured(timex,player_id,stat)select now(),player_id,0 from player_info where player_id = ? and injured = 0",playerId);
		executeUpdate("update player_info set injured = 1 where player_id = ?",playerId);
		//System.out.println("---------------------===="+playerId);
	}

	public void addNbaDataRunLog(){
		execute("insert into nba_data_run_log(run_time)values(now())");
	}
	public int getNbaDataRunLog(){
		return queryForInteger("select count(1) from nba_data_run_log where datediff(run_time,now())=0");
	}

	public void changeMatchScore(NBAGameDetail T){
		int homeScore = T.getHomeTeamScore();
		int awayScore = T.getAwayTeamScore();
		int result = 0;
		if(homeScore>awayScore){//主动方胜，负值
			result = 0-getRes(homeScore-awayScore);
		}else{
			result = 0+getRes(awayScore-homeScore);
		}
		executeUpdate("update nba_data.game_guess_match_main set home_score=?,away_score=?,result=? where game_id=? and result = 0",homeScore,awayScore,result,T.getGameBoxId());
	}

	private int getRes(int s){
		int result = 0;
		if(s>=1 && s<=5)
			result = 2;
		else if(s>=6 && s<=10)
			result = 3;
		else if(s>=11 && s<=15)
			result = 4;
		else
			result = 5;
		return result;
	}

	//竞猜球员
	public void addGuessPlayer(String time){
		/*
		//清理
		executeUpdate("delete from nba_data.game_guess_player_rand");
		//放入今天有PK的
		if(time.equals(""))
			executeUpdate("insert into nba_data.game_guess_player_rand(player_id,pricex)select a.player_id,price-before_price from player_info a,(select a.player_id from data_score_board_detail a,data_game_schedule b where a.game_id = b.game_id and datediff(b.game_time,now())=0 group by a.player_id) as b where a.player_id=b.player_id");
		else
			executeUpdate("insert into nba_data.game_guess_player_rand(player_id,pricex)select a.player_id,price-before_price from player_info a,(select a.player_id from data_score_board_detail a,data_game_schedule b where a.game_id = b.game_id and datediff(b.game_time,?)=0 group by a.player_id) as b where a.player_id=b.player_id",time);
		//取重复
		List<Integer> pricex = queryForList("select pricex num from nba_data.game_guess_player_rand group by pricex having count(1)>1",INTEGER_HANDLER);
		//去重
		for(int p:pricex){
			executeUpdate("delete from nba_data.game_guess_player_rand where pricex=?",p);
		}
		List<PlayerPrice> listUp   = queryForList("select * from nba_data.game_guess_player_rand where pricex>0 order by pricex desc",PRICE_HANDLER);
		List<PlayerPrice> listDown = queryForList("select * from nba_data.game_guess_player_rand where pricex<0 order by pricex asc",PRICE_HANDLER);

		int index_up = 0,index_down = 0;//随机取数据的起点
		if(listUp.size()>5)
			index_up = getRand(listUp.size()-5);
		if(listDown.size()>5)
			index_down = getRand(listDown.size()-5);

		List<Integer> up = new ArrayList<Integer>();
		List<Integer> down = new ArrayList<Integer>();
		int up_max   = listUp.size()<5?listUp.size():5;
		int down_max = listDown.size()<5?listDown.size():5;

		List<String> gm = new ArrayList<String>();
		GuessPlayerMain T = new GuessPlayerMain();	

		if(up_max>=3 && down_max>=3){
			T.setId(Integer.parseInt(Key.getToday(new Date())));
			for(int i=index_up;i<index_up+up_max;i++){
				up.add(listUp.get(i).getPlayerId());
			}
			for(int i=index_down;i<index_down+down_max;i++){
				down.add(listDown.get(i).getPlayerId());
			}
			T.setPlayerUpMax(up.get(0));
			T.setPlayerDownMax(down.get(0));
			Collections.shuffle(up);
			Collections.shuffle(down);
			StringBuffer ups = new StringBuffer("");
			StringBuffer downs = new StringBuffer("");
			for(int a:up){
				ups.append(a).append("|");
				gm.add("insert into nba_data.game_guess_player_team(guess_id,player_id,team_id,shard_id,team_name,in_fk,in_fp,timex,flag)values("+T.getId()+","+a+",0,0,'0',"+10*(getRand(9)+1)+","+10*(getRand(7)+1)+",now(),1)");
			}
			for(int a:down){
				downs.append(a).append("|");
				gm.add("insert into nba_data.game_guess_player_team(guess_id,player_id,team_id,shard_id,team_name,in_fk,in_fp,timex,flag)values("+T.getId()+","+a+",0,0,'0',"+10*(getRand(6)+1)+","+10*(getRand(5)+1)+",now(),-1)");
			}
			if(up.size()>0)T.setPlayerUpIds(ups.toString().substring(0,ups.length()-1));
			if(down.size()>0)T.setPlayerDownIds(downs.toString().substring(0,downs.length()-1));			
		}

		int count = queryForInteger("select count(1) from nba_data.game_guess_player_main where datediff(timex,now()) = 0");
		if(count==0 && T.getId()>0){
			execute("insert into nba_data.game_guess_player_main(guess_id,player_up_ids,player_up_max,player_down_ids,player_down_max,timex,flag)values(?,?,?,?,?,now(),0)",T.getId(),T.getPlayerUpIds(),T.getPlayerUpMax(),T.getPlayerDownIds(),T.getPlayerDownMax());
			for(String a:gm){
				execute(a);
			}
		}
		 */
	}

	//产生随机数
	static Random random = new Random() ;
	public int getRand(int max){		
		int rand = random.nextInt( max ) ;
		return rand;
	}

	/**
	 * 无队设置伤病
	 */
	public void changeInjured(){
		execute("update player_info set injured = 0 where team_id<=0");
	}

	public void changeTeamId0(int teamId){
		execute("update player_info set team_id = 0 where team_id=? and grade<>'X'",teamId);
	}
	//
	public List<GameDataJobRunLog>queryRunLogs(){
		return queryForList("select * from data_job_runlog  order by id desc limit 0,10",ROW_HANDLER);//where game_time>'2010-10-01'
		//return queryForList("select * from data_job_runlog where game_time<'2010-01-01' order by id desc limit 0,50",ROW_HANDLER);
	}
	//
	public GameDataJobRunLog queryRunLogByTime(Date gameTime){
		return queryForObject("select * from data_job_runlog where datediff(game_time,?)=0",ROW_HANDLER,gameTime);
	}

	public void saveVS(GameVS vs){
		String count="select count(1) from data_game_vs where game_id = ?";
		int num = queryForInteger(count,vs.getGameId());
		if(num==0){
			String sql = "insert into data_game_vs(game_id,home,home_name,away,away_name,date_time)values(?,?,?,?,?,?)";
			execute(sql,vs.getGameId(),vs.getHome(),vs.getHomeName(),vs.getAway(),vs.getAwayName(),vs.getTime());
		}
	}

	public void changeRank(NBATeamDetail team){
		executeUpdate("update team_info set win=?,loss=?,winloss=?,rank=? where espn_name=?",team.getWin(),team.getLoss(),team.getWinLoss(),team.getRank(),team.getEspnName());
	}

	public List<GameVS> getGameVS(){
		return queryForList("select * from data_game_vs where datediff(date_time,now())=0",VS_HANDLER);
	}

	public List<GameVS> getGameVS(Date date){
		return queryForList("select * from data_game_vs where datediff(date_time,?)=0",VS_HANDLER,date);
	}

	//
	public void x_addGameData(MatchData gameData,int status){
		String sql="insert into x_data_game_schedule(" +
		"game_id," +
		"season_id," +
		"game_type," +
		"home_team_id," +
		"away_team_id," +
		"home_team_score," +
		"away_team_score," +
		"game_time,status)values(" +
		"?,?,?,?,?,?,?,?,?)";
		execute(sql, 
				gameData.getGameBoxId(),
				gameData.getSeasonId(),
				gameData.getGameType(),
				gameData.getHomeTeamId(),
				gameData.getAwayTeamId(),
				gameData.getHomeScore(),
				gameData.getAwayScore(),
				gameData.getGameDate(),
				status);	
	}
	public void x_addTeamScore(TeamScore teamScore){
		String sql="insert into x_data_score_board (" +
		"team_id," +
		"game_id," +
		"quarter1," +
		"quarter2," +
		"quarter3," +
		"quarter4," +
		"ot1," +
		"ot2," +
		"ot3," +
		"ot4," +
		"ot5," +
		"ot6," +
		"ot7," +
		"total)values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		Object[]p=new Object[14];
		p[0]=teamScore.teamId;
		p[1]=teamScore.gameId;
		for(int i=0;i<teamScore.scores.size()-1;i++){
			p[2+i]=teamScore.scores.get(i);
		}
		p[p.length-1]=teamScore.scores.get(teamScore.scores.size()-1);
		execute(sql, p);
	}
	public void x_addPlayerScore(PlayerStats playerScore){
		try{
			String sql="insert into x_data_score_board_detail(" +
			"player_id," +
			"game_id," +
			"team_id," +
			"is_starter," +
			"fgm," +
			"fga," +
			"ftm," +
			"fta," +
			"three_pm," +
			"three_pa," +
			"oreb," +
			"dreb," +
			"reb," +
			"ast," +
			"stl," +
			"blk," +
			"`to`," +
			"pf," +
			"pts," +
			"effect_point," +
			"min)values(" +
			"?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			execute(sql,
					playerScore.gamePlayerId,
					playerScore.gameId,
					playerScore.teamId,
					playerScore.isStarter?1:0,
							playerScore.getFGM(),
							playerScore.getFGA(),
							playerScore.getFTM(),
							playerScore.getFTA(),
							playerScore.getThreePM(),
							playerScore.getThreePA(),
							playerScore.oreb==null?0:playerScore.oreb,
									playerScore.dreb==null?0:playerScore.dreb,
											playerScore.reb==null?0:playerScore.reb,
													playerScore.ast,
													playerScore.stl,
													playerScore.blk,
													playerScore.to,
													playerScore.pf,
													playerScore.pts,
													getEffectPoint(playerScore.plus),
													playerScore.min
			);
			executeUpdate("update player_info set injured=0 where player_id=?",playerScore.gamePlayerId);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
