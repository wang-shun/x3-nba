package com.ftkj.dao.data;

import java.util.ArrayList;
import java.util.List;
import com.ftkj.conn.DataConnectionDAO;
import com.ftkj.conn.ResultSetRow;
import com.ftkj.conn.ResultSetRowHandler;
import com.ftkj.domain.data.NBAPlayerDetail;
import com.ftkj.domain.data.NBAPlayerScore;
import com.ftkj.domain.data.NBATeamDetail;
import com.ftkj.domain.data.PlayerAvgRate;
import com.ftkj.domain.data.PlayerAbi;
import com.ftkj.domain.data.PlayerPrice;
import com.ftkj.domain.data.PlayerPricePolicy;


public class NBADataDAO extends DataConnectionDAO{	
	private static final ResultSetRowHandler<Integer> FBID_HANDLER=	new ResultSetRowHandler<Integer>() {
		@Override
		public Integer handleRow(ResultSetRow row) throws Exception {
			return row.getInt("grade");
		}
	};
	//
	private static final ResultSetRowHandler<NBATeamDetail>TEAM_ROW_HANDLER=	new ResultSetRowHandler<NBATeamDetail>() {
		@Override
		public NBATeamDetail handleRow(ResultSetRow row) throws Exception {
			NBATeamDetail t=new NBATeamDetail();
			t.setTeamId(row.getInt("team_id"));
			t.setEspnName(row.getString("espn_name"));
			t.setTeamName(row.getString("team_name"));
			t.setTeamEname(row.getString("team_ename"));
			t.setArea(row.getString("area"));
			t.setShortName(row.getString("short_name"));
			t.setAreaShort(row.getString("area_short"));
			t.setWin(row.getInt("win"));
			t.setLoss(row.getInt("loss"));
			t.setWinLoss(row.getInt("winloss"));
			t.setRank(row.getInt("rank"));
			return t;
		}
	};

	private static final ResultSetRowHandler<PlayerPrice>MONEY_ROW_HANDLER=	new ResultSetRowHandler<PlayerPrice>() {
		@Override
		public PlayerPrice handleRow(ResultSetRow row) throws Exception {
			PlayerPrice t=new PlayerPrice();
			t.setPlayerId(row.getInt("player_id"));
			t.setPrice(row.getInt("price"));
			t.setTime(row.getTimestamp("time"));
			t.setBizNum(row.getInt("biznum"));
			return t;
		}
	};
	private static final ResultSetRowHandler<PlayerAvgRate>PLAYER_AVG_ROW_HANDLER=	new ResultSetRowHandler<PlayerAvgRate>() {
		@Override
		public PlayerAvgRate handleRow(ResultSetRow row) throws Exception {
			PlayerAvgRate avg=new PlayerAvgRate();
			avg.setPlayerId(row.getInt("player_id"));
			avg.setSeasonId(row.getInt("season_id"));
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
	private static final ResultSetRowHandler<PlayerAvgRate>PLAYER_AVG_ROW_HANDLER2=	new ResultSetRowHandler<PlayerAvgRate>() {
		@Override
		public PlayerAvgRate handleRow(ResultSetRow row) throws Exception {
			PlayerAvgRate avg=new PlayerAvgRate();
			avg.setPlayerId(row.getInt("player_id"));
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
			avg.setPlayCount(row.getInt("total"));
			return avg;
		}
	};
	
	private static final ResultSetRowHandler<PlayerAvgRate>PLAYER_AVG_ROW_HANDLER3=	new ResultSetRowHandler<PlayerAvgRate>() {
		@Override
		public PlayerAvgRate handleRow(ResultSetRow row) throws Exception {
			PlayerAvgRate avg=new PlayerAvgRate();
			avg.setPlayerId(row.getInt("player_id"));
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

	private static final ResultSetRowHandler<PlayerAbi>PLAYER_CAP_ROW_HANDLER=	new ResultSetRowHandler<PlayerAbi>() {
		@Override
		public PlayerAbi handleRow(ResultSetRow row) throws Exception {
			PlayerAbi cap=new PlayerAbi();
			cap.setPlayerId(row.getInt("player_id"));
			cap.setFgm(row.getInt("fgm"));				
			cap.setFtm(row.getInt("ftm"));
			cap.setPts(row.getInt("pts"));
			cap.setTo(row.getInt("to"));
			cap.setMin(row.getInt("min"));
			cap.setPf(row.getInt("pf"));
			cap.setThreePm(row.getInt("three_pm"));				
			cap.setOreb(row.getInt("oreb"));
			cap.setDreb(row.getInt("dreb"));
			cap.setAst(row.getInt("ast"));
			cap.setBlk(row.getInt("blk"));
			cap.setStl(row.getInt("stl"));
			cap.setAttrAbi(row.getInt("attr_cap"));
			cap.setGuaAbi(row.getInt("gua_cap"));
			cap.setAbi(row.getInt("cap"));
			return cap;
		}
	};

	private static final ResultSetRowHandler<NBAPlayerScore> PLAYER_SCORE_DAY_HANDLER = new ResultSetRowHandler<NBAPlayerScore>() {
		@Override
		public NBAPlayerScore handleRow(ResultSetRow row) throws Exception {
			NBAPlayerScore ps=new NBAPlayerScore();
			ps.setGameTime(row.getTimestamp("game_time"));
			ps.setFgM(row.getInt("fgm"));
			ps.setFgA(row.getInt("fga"));			
			ps.setFtM(row.getInt("ftm"));	
			ps.setFtA(row.getInt("fta"));			
			ps.setThreePM(row.getInt("three_pm"));
			ps.setThreePA(row.getInt("three_pa"));			
			ps.setReb(row.getInt("reb"));			
			ps.setAst(row.getInt("ast"));
			ps.setStl(row.getInt("stl"));			
			ps.setBlk(row.getInt("blk"));
			ps.setTo(row.getInt("to"));
			ps.setPf(row.getInt("pf"));
			ps.setPts(row.getInt("pts"));					
			ps.setMin(row.getInt("min"));
			ps.setEffectPoint(row.getInt("plus"));
			return ps;
		}
	};

	private static final ResultSetRowHandler<NBAPlayerDetail>PLAYER_INFO_ROW_HANDLER=	new ResultSetRowHandler<NBAPlayerDetail>() {
		@Override
		public NBAPlayerDetail handleRow(ResultSetRow row) throws Exception {
			NBAPlayerDetail info=new NBAPlayerDetail();
			info.setPlayerId(row.getInt("player_id"));
			info.setEspnId(row.getInt("espn_id"));
			info.setTeamId(row.getInt("team_id"));
			info.setName(row.getString("name"));
			info.setEname(row.getString("ename"));
			info.setShortName(row.getString("short_name"));
			info.setShortNameTw(row.getString("short_name_tw"));
			info.setShortNameEn(row.getString("short_name_en"));
			info.setNumber(row.getInt("number"));
			info.setPosition(row.getString("position"));
			info.setHeight(row.getString("height"));
			info.setWeight(row.getString("weight"));
			info.setSchool(row.getString("school"));
			info.setBirthday(row.getString("birthday"));
			info.setNation(row.getString("nation"));
			info.setDraft(row.getString("draft"));
			info.setSalary(row.getString("salary"));
			info.setContract(row.getString("contract"));				
			info.setGrade(row.getString("grade"));
			info.setPrice(row.getInt("price"));
			info.setBeforePrice(row.getInt("before_price"));
			info.setPlayerType(row.getInt("player_type"));
			info.setInjured(row.getInt("injured"));
			info.setCap(row.getInt("cap"));
			info.setBeforeCap(row.getInt("before_cap"));
			info.setAttr(row.getInt("attr"));
			info.setBeforeAttr(row.getInt("before_attr"));			
			info.setGrades(row.getString("grades"));
			info.setContractEn(row.getString("contract_en"));
			info.setContractTr(row.getString("contract_tr"));
			info.setContractTw(row.getString("contract_tw"));
			info.setPlus(NBAPlayerDetail.getPlusStat(row.getInt("plus")));
			return info;
		}
	};
	

	private static final ResultSetRowHandler<PlayerPricePolicy> PLAYER_RULE_ROW_HANDLER=new ResultSetRowHandler<PlayerPricePolicy>() {
		@Override
		public PlayerPricePolicy handleRow(ResultSetRow row) throws Exception {
			PlayerPricePolicy rule=new PlayerPricePolicy();
			rule.setId(row.getInt("id"));
			rule.setGrade(row.getString("grade"));
			rule.setMinMarketPrice(row.getInt("money_min"));
			rule.setMaxMarketPrice(row.getInt("money_max"));
			rule.setNum(row.getInt("nums"));
			rule.setTop(row.getInt("tops"));
			return rule;
		}
	};	
	
	
	
	public List<PlayerAbi>getPlayerCaps(){
		String sql="select * from data_player_cap";
		return queryForList(sql, PLAYER_CAP_ROW_HANDLER);
	}

	public String getMaxNbaGameData(){
		return queryForString("select DATE_FORMAT(max(time),'%Y-%m-%d') from player_money");
	}

	public int getFBGrade(String id){
		List<Integer> list =  queryForList("select grade from facebook_id_grade where id = ?", FBID_HANDLER,id);
		if(list==null || list.size()==0) 
			return 0;
		else
			return list.get(0);
	}
	public List<NBAPlayerDetail>getPlayers(){
		executeUpdate("update player_info set injured = 0 where player_id in(select player_id from injured where stat = 1 and timex=DATE_FORMAT(now(),'%Y-%m-%d'))");
		return queryForList("select * from player_info", PLAYER_INFO_ROW_HANDLER);
	}

	public void changeTeamId(int playerId,int teamId){
		executeUpdate("update player_info set team_id = ? where player_id = ?",teamId,playerId);
	}
	public List<PlayerAvgRate>getPlayerAvgs(int seasonId){
		//String sql="select * from data_player_avg where season_id=? and play_count>=0";
		String sql="select a.* from data_player_avg a,player_info b where a.player_id = b.player_id and a.season_id=? and b.team_id>=0";
		return queryForList(sql, PLAYER_AVG_ROW_HANDLER,seasonId);
	}
	
//	public PlayerAvgRate getPlayerAvg(int playerId,int seasonId,String gameIDs){
//		String sql = "select player_id,count(1) as total,avg(fgm) as fgm,avg(fga) as fga,avg(ftm)as ftm,avg(fta) as fta,avg(three_pm) as three_pm"
//				+ ",avg(three_pa) as three_pa,avg(oreb) as oreb,avg(dreb) as dreb,avg(reb) as reb,avg(ast) as ast,avg(stl) as stl"
//				+ ",avg(blk) as blk,avg(`to`) as `to`,avg(pf) as pf,avg(pts) as pts"
//				+ ",avg(effect_point) as effect_point,avg(`min`) as min  "
//				+ "from (select d.* from data_score_board_detail as d,data_game_schedule as s where d.player_id = ? and s.season_id = ? "
//				+ "and d.game_id not in (?) and d.game_id = s.game_id order by d.game_id desc limit 10) as t ;";
//		return queryForObject(sql, PLAYER_AVG_ROW_HANDLER2, playerId,seasonId,gameIDs);
//	}
	
	public PlayerAvgRate getPlayerAvgOther(int playerId,int seasonId,String gameIDs,int start,int num){
		String sql = "select player_id,count(1) as total,avg(fgm) as fgm,avg(fga) as fga,avg(ftm)as ftm,avg(fta) as fta,avg(three_pm) as three_pm"
				+ ",avg(three_pa) as three_pa,avg(oreb) as oreb,avg(dreb) as dreb,avg(reb) as reb,avg(ast) as ast,avg(stl) as stl"
				+ ",avg(blk) as blk,avg(`to`) as `to`,avg(pf) as pf,avg(pts) as pts"
				+ ",avg(effect_point) as effect_point,avg(`min`) as min  " 
				+ "from (select d.* from data_score_board_detail as d,data_game_schedule as s where d.player_id = ? and s.season_id = ? "
				+ "and d.game_id not in (?) and d.game_id = s.game_id and d.min>0  order by d.game_id desc limit ?,?) as t  ;";
		return queryForObject(sql, PLAYER_AVG_ROW_HANDLER2, playerId,seasonId,gameIDs,start,num);
	}
	
	/**
	 * 指定比赛时间的最近几场平均数据
	 * @param playerId
	 * @param seasonId
	 * @param gameIDs
	 * @param start
	 * @param num
	 * @param datetime
	 * @return
	 */
	public PlayerAvgRate getPlayerAvgOther(int playerId,int seasonId,String gameIDs,int start,int num, String datetime){
		String sql = "select player_id,count(1) as total,avg(fgm) as fgm,avg(fga) as fga,avg(ftm)as ftm,avg(fta) as fta,avg(three_pm) as three_pm"
				+ ",avg(three_pa) as three_pa,avg(oreb) as oreb,avg(dreb) as dreb,avg(reb) as reb,avg(ast) as ast,avg(stl) as stl"
				+ ",avg(blk) as blk,avg(`to`) as `to`,avg(pf) as pf,avg(pts) as pts"
				+ ",avg(effect_point) as effect_point,avg(`min`) as min  " 
				+ "from (select d.* from data_score_board_detail as d,data_game_schedule as s where date(game_time)=? and d.player_id = ? and s.season_id = ? "
				+ "and d.game_id not in (?) and d.game_id = s.game_id and d.min>0  order by d.game_id desc limit ?,?) as t  ;";
		return queryForObject(sql, PLAYER_AVG_ROW_HANDLER2, datetime,playerId,seasonId,gameIDs,start,num);
	}
	
	
	public PlayerAvgRate getCurPlayerAvg(int playerId){
		String sql = "select * from x_data_score_board_detail where player_id = ?";
		return queryForObject(sql, PLAYER_AVG_ROW_HANDLER3, playerId);
	}
	
	public String getCurSchedule(){
		String sql = "select group_concat(game_id) from data_game_schedule where  game_time >= DATE_FORMAT(now(),'%Y%m%d')";
		return queryForString(sql);
	}
	
	public List<PlayerAbi> getPlayerOrderByCap(){
		String update_new=	"update player_info a inner join (select a.player_id from data_player_avg a,player_info b where a.player_id = b.player_id and a.season_id = 2018 and b.grades='N' and a.play_count>=5)b on a.player_id = b.player_id set a.grade = 'D+'";
		String sql="select a.* from data_player_cap a,player_info b where a.player_id=b.player_id and b.team_id>0 and b.grade<>'N'and b.grade<>'X' order by cap desc,attr_cap desc,player_id asc";//and b.grade!='N'
		
		executeUpdate(update_new);
		return queryForList(sql,PLAYER_CAP_ROW_HANDLER);
	}
	public List<NBATeamDetail>getTeams(){
		String sql="select * from team_info where team_id>0 order by area_short,rank asc";
		return queryForList(sql,TEAM_ROW_HANDLER);
	}

	public List<PlayerPricePolicy> getPlayerRule(){
		String sql="select * from player_rule order by id asc";
		return queryForList(sql,PLAYER_RULE_ROW_HANDLER);
	}
	public List<PlayerPrice> getPlayerMoneyList(int playerId){
		String sql="select * from player_money where player_id = ? order by time desc limit 30";
		return queryForList(sql,MONEY_ROW_HANDLER,playerId);
	}

	String sql_score = "select date(b.game_time) game_time,fgm,fga,ftm,fta,three_pm,three_pa,reb,ast,stl,blk,`to`,pf,pts,`min`,a.effect_point plus from data_score_board_detail a,data_game_schedule b where a.game_id = b.game_id and a.player_id = ?  order by game_time desc limit 5";
	//休赛期用
	String sql_score_x = "select date(b.game_time) game_time,fgm,fga,ftm,fta,three_pm,three_pa,reb,ast,stl,blk,`to`,pf,pts,`min`,a.effect_point plus from data_score_board_detail a,data_game_schedule b,data_rand c where a.game_id = b.game_id and a.player_id = ? and c.vs_time=date(b.game_time) and date(c.game_time)<=date(now()) order by c.game_time desc limit 5";
	public List<NBAPlayerScore> getPlayerScoreDay(int playerId){
		return queryForList(sql_score,PLAYER_SCORE_DAY_HANDLER,playerId);
	}

	public void addPlayerBaseInfo(NBAPlayerDetail info){
		String sql="insert into player_info(" +
		"player_id,espn_id,team_id,name,ename,short_name,short_name_tw,short_name_en,number,position,height,weight," +
		"school,birthday,nation,draft,salary,contract,grade,grades,price,before_price,cap,before_cap,attr,before_attr)values(" +
		"?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,40,40,20,20)";
		execute(sql,
				info.getEspnId(),
				info.getPlayerId(),
				info.getTeamId(),
				info.getName(),
				info.getEname(),
				info.getShortName(),
				info.getShortName(),
				info.getShortName(),
				info.getNumber(),
				info.getPosition(),
				info.getHeight(),
				info.getWeight(),
				info.getSchool(),
				info.getBirthday(),
				info.getNation(),
				info.getDraft(),
				info.getSalary(),
				info.getContract(),
				info.getGrade(),
				info.getGrade(),
				info.getPrice(),
				info.getBeforePrice());
	}

	public void updatePlayerPrice(int playerid,String grade,int price,int before_price,int cap,int before_cap,int attr){
		String sql = "";
		if(attr!=0){
			sql="update player_info set grade=?,price=?,before_price=?,cap=?,before_cap=?,before_attr=attr,attr=? where player_id=?";
		}else{
			sql="update player_info set grade=?,price=?,before_price=?,cap=?,before_cap=?,before_attr=attr,attr=attr+? where player_id=?";
		}
		System.out.println("=="+sql+" / "+ playerid);
		executeUpdate(sql,grade,price,before_price,cap,before_cap,attr,playerid);
	}
	
	public void addPlayerAvg(int seasonId){
		String sql="insert into data_player_avg(season_id,player_id)select "+seasonId+",player_id from player_info where player_id not in (select player_id from data_player_avg where season_id="+seasonId+")";
		execute(sql);
	}
	
	public void deletePlayerAvg(int seasonId){
		executeUpdate("delete from data_player_avg where season_id="+seasonId);
	}

	public void addPlayerMoneyHaveData(){
		executeUpdate("delete from player_money where datediff(time,now())=0");
		executeUpdate("insert into player_money(player_id,time,price,biznum)select a.player_id,now(),a.price,b.cap from player_info a,data_player_cap b where a.player_id = b.player_id and a.grade<>'X' and a.grade<>'M' and a.team_id>=0");
	}
	
	/**
	 * 更新之前身价，player_money set before_price
	 */
	public void delete_price_50(){
		List<NBAPlayerDetail> list = queryForList("select * from player_info where before_price = 50 and price>50 and team_id>0",PLAYER_INFO_ROW_HANDLER);
		for(NBAPlayerDetail T:list){
			delete_price_50(T.getPlayerId());
		}
	}
	
	/**
	 * 更新之前身价
	 * @param playerId
	 */
	private void delete_price_50(int playerId){
		int all_size = queryForInteger("select count(1) from player_money where player_id = ?",playerId);
		int all_xxxx = queryForInteger("select count(1) from player_money where player_id = ? and price = 50",playerId);
		if((all_xxxx+1)==all_size){
			executeUpdate("delete from player_money where player_id = ? and price=50",playerId);
			executeUpdate("update player_info set before_price=price where player_id = ?",playerId);
		}
	}
	
	//更新状态
	public void changePlus(int seasonId){
		String sql=" update player_info a inner join "+
		"(" +
		"  select a.player_id,a.effect_point plus from  data_score_board_detail a,(" +
		"   select b.player_id,max(a.game_id) game_id from data_game_schedule a,data_score_board_detail b where a.game_id = b.game_id and a.season_id = ? and datediff(a.game_time,now())=0  group by b.player_id" +
		"  ) as b where a.player_id = b.player_id and a.game_id = b.game_id and a.effect_point is not NULL order by a.player_id" +
		")b "+
		"on a.player_id = b.player_id set a.plus = b.plus ";
		executeUpdate(sql,seasonId);
	}
	
		String sql_last_day = "update player_info a inner join "+
		"("+
		" select a.player_id,a.effect_point plus from  data_score_board_detail a,("+
		"  select b.player_id,max(a.game_id) game_id from data_game_schedule a,data_score_board_detail b where a.game_id = b.game_id and a.season_id = ? group by b.player_id"+
		") as b where a.player_id = b.player_id and a.game_id = b.game_id order by a.player_id"+
		")b on a.player_id = b.player_id set a.plus = b.plus ";
	
		String sql_today =    "update player_info a inner join "+
		"("+
		" select a.player_id,a.effect_point plus from  data_score_board_detail a,("+
		"  select b.player_id,max(a.game_id) game_id from data_game_schedule a,data_score_board_detail b where a.game_id = b.game_id and a.season_id = ? and datediff(a.game_time,?)=0 group by b.player_id"+
		") as b where a.player_id = b.player_id and a.game_id = b.game_id order by a.player_id"+
		")b on a.player_id = b.player_id set a.plus = b.plus ";
	
	public void changePlus(int seasonId,int flag){
		if(flag==1){
			executeUpdate(sql_last_day,seasonId);
		}
		if(flag==2){
			String time = getRandSchedulerTime();
			executeUpdate(sql_today,seasonId,time);
		}
	}

	public String getRandSchedulerTime(){
		//return queryForString("select DATE_FORMAT(game_time,'%Y-%m-%d') from data_game_schedule where season_id = 2010 order by rand() limit 0,1");
		return queryForString("select vs_time from data_rand where datediff(game_time,now()) = 0");
	}
	
	/**
	 * 取指定赛季，比赛场次数不少于次数，的有比赛的日期，用来做随机身价，随机指定天数的比赛.
	 * @param seasionId
	 * @param minVs
	 * @return
	 */
	public List<String> getSchedulerSeason(int seasionId, String startTime, String endTime, int minVs) {
		String sql = "select date(game_time) as gametime from data_game_schedule where date(game_time)>=? and date(game_time)<=? group by date(game_time) having count(1)>? order by rand(?)";
		return queryForList(sql, new ResultSetRowHandler<String>() {
			@Override
			public String handleRow(ResultSetRow row) throws Exception {
				return row.getString("gametime");
			}
		}, startTime, endTime, minVs, seasionId);
	}

	public void addPlayerMoneyNotData(){		
		executeUpdate("update player_info set before_price=price,before_cap=cap,before_attr=attr");	
		addPlayerMoneyHaveData();		
	}

	public List<PlayerAvgRate> getRecentAvg(int playerId,int seasonId,int number){
		String sql="select " +
		"a.player_id," +
		"b.season_id,"+
		"1 play_count,"+
		"a.is_starter starter_count," +
		"a.fgm," +
		"a.fga," +
		"a.ftm," +
		"a.fta," +
		"a.three_pm," +
		"a.three_pa," +
		"a.oreb," +
		"a.dreb," +
		"a.ast," +
		"a.stl," +
		"a.blk,"+
		"a.`to`,"+
		"a.pf,"+
		"a.pts,"+
		"a.`min`"+
		"from data_score_board_detail a,data_game_schedule b "+
		"where a.game_id=b.game_id "+
		"and b.season_id=? "+
		"and player_id=? "+		
		"order by b.game_time desc "+
		"limit 0,?";
		return queryForList(sql, PLAYER_AVG_ROW_HANDLER,seasonId,playerId,number);
	}

	public List<PlayerAvgRate> getRecentAvg_time(int playerId,int seasonId,String gameDate,int number){
		String sql="select " +
		"a.player_id," +
		"b.season_id,"+
		"1 play_count,"+
		"a.is_starter starter_count," +
		"a.fgm," +
		"a.fga," +
		"a.ftm," +
		"a.fta," +
		"a.three_pm," +
		"a.three_pa," +
		"a.oreb," +
		"a.dreb," +
		"a.ast," +
		"a.stl," +
		"a.blk,"+
		"a.`to`,"+
		"a.pf,"+
		"a.pts,"+
		"a.`min`"+
		"from data_score_board_detail a,data_game_schedule b "+
		"where a.game_id=b.game_id "+
		"and datediff(b.game_time,?)=0 "+
		"and b.season_id=? "+
		"and player_id=? "+		
		"order by b.game_time desc "+
		"limit 0,?";
		return queryForList(sql, PLAYER_AVG_ROW_HANDLER,gameDate,seasonId,playerId,number);
	}

	/**
	 * 计算指定赛季,打过比赛的球员数据平均值，表：data_player_avg
	 * @param seasonId
	 */
	public void calculatePlayerAvg(int seasonId){
		String sql="update data_player_avg a inner join ("+
		"select "+
		"player_id,"+
		"count(*) play_count,"+
		"sum(is_starter) starter_count,"+
		"sum(fgm)/count(*) fgm,"+
		"sum(fga)/count(*) fga,"+
		"sum(ftm)/count(*) ftm,"+
		"sum(fta)/count(*) fta,"+
		"sum(dreb)/count(*) dreb,"+
		"sum(ast)/count(*)  ast,"+
		"sum(three_pm)/count(*) three_pm,"+
		"sum(blk)/count(*)  blk,"+
		"sum(three_pa)/count(*) three_pa,"+
		"sum(oreb)/count(*) oreb,"+
		"sum(stl)/count(*)  stl,"+
		"sum(`to`)/count(*)  tos,"+
		"sum(pf)/count(*)  pf,"+
		"sum(pts)/count(*)  pts,"+
		"sum(`min`)/count(*)  mins "+
		"from data_score_board_detail "+
		"where game_id in (select game_id from data_game_schedule where season_id=?) " +
		"group by player_id )b on a.player_id=b.player_id and a.season_id=? "+
		"set "+					
		"a.play_count=b.play_count,"+
		"a.starter_count=b.starter_count,"+
		"a.fgm=b.fgm,"+
		"a.fga=b.fga,"+
		"a.ftm=b.ftm,"+
		"a.fta=b.fta,"+
		"a.three_pm=b.three_pm,"+
		"a.three_pa=b.three_pa,"+
		"a.oreb=b.oreb,"+
		"a.dreb=b.dreb,"+
		"a.ast=b.ast,"+
		"a.stl=b.stl,"+
		"a.blk=b.blk,"+
		"a.`to`=b.tos,"+
		"a.pf=b.pf,"+
		"a.pts=b.pts,"+
		"a.`min`=b.mins;";
		executeUpdate(sql,seasonId,seasonId);
		//不显示新秀
		//executeUpdate("update data_player_avg set play_count=-1 where season_id = 2011 and play_count>0 and player_id in(select player_id from player_info where grade='N')");
	}

	public void initCap(){
		execute("insert into data_player_cap(player_id,attr_cap,gua_cap,cap) select player_id,25,25,50 from player_info where player_id not in(select player_id from data_player_cap)");
	}

	public void updateCap(PlayerAbi cap){
		StringBuffer sql=new StringBuffer("update data_player_cap set ");
		List<Object> val = new ArrayList<Object>();
		sql.append("fgm=?");
		val.add(cap.getFgm());
		sql.append(",ftm=?");
		val.add(cap.getFtm());
		sql.append(",pts=?");
		val.add(cap.getPts());
		sql.append(",three_pm=?");
		val.add(cap.getThreePm());
		sql.append(",oreb=?");
		val.add(cap.getOreb());
		sql.append(",dreb=?");
		val.add(cap.getDreb());
		sql.append(",ast=?");
		val.add(cap.getAst());
		sql.append(",stl=?");
		val.add(cap.getStl());
		sql.append(",blk=?");
		val.add(cap.getBlk());
		sql.append(",`to`=?");
		val.add(cap.getTo());
		sql.append(",`min`=?");
		val.add(cap.getMin());
		sql.append(",pf=?");
		val.add(cap.getPf());
		sql.append(",attr_cap=?");
		val.add(cap.getAttrAbi());
		sql.append(",gua_cap=?");
		val.add(cap.getGuaAbi());	
		sql.append(",cap=?");
		val.add(cap.getAbi());
		sql.append(" where player_id=?");
		val.add(cap.getPlayerId());
		System.out.println("==sql:"+cap.toString());
		executeUpdate(sql.toString(),val.toArray());
		
		//executeUpdate("update player_info set before_attr=attr,attr=? where player_id=?",cap.getAttrAbi(),cap.getPlayerId());
	}	


	public void updatePlayer(NBAPlayerDetail player) {
		String sql = "update player_info set name=?,ename=?,team_id=?,grade=?,position=? where player_id=?";
		executeUpdate(sql, player.getName(), player.getEname(),player.getTeamId(), player.getGrade(), player.getPosition(), player.getPlayerId());
	}	

}
