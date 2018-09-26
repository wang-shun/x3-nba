package com.ftkj.ao.data.job;

import com.ftkj.NBAPush;
import com.ftkj.action.data.MatchDataAction;
import com.ftkj.action.data.NBADataAction;
import com.ftkj.action.data.impl.MatchDataActionImpl;
import com.ftkj.action.data.impl.NBADataActionImpl;
import com.ftkj.ao.data.GameDataAO;
import com.ftkj.ao.data.NBADataAO;
import com.ftkj.dao.data.GameDataDAO;
import com.ftkj.dao.data.NBADataDAO;
import com.ftkj.domain.data.MatchData;
import com.ftkj.domain.data.NBAGameDetail;
import com.ftkj.domain.data.NBAPlayerDetail;
import com.ftkj.domain.data.NBASeason;
import com.ftkj.exception.GameDataException;
import com.ftkj.invoker.ResourceCache;
import com.ftkj.invoker.ResourceType;
import com.ftkj.tool.quartz.QuartzServer;
import com.ftkj.util.UtilDateTime;

import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GameDataJob {
    private static final Logger logger = LoggerFactory.getLogger(GameDataJob.class);
	static MatchDataAction matchDataAction;
	static NBADataAction  nBADataAction;
	//static JredisUtil redis;
	static String ip;
	static int port;
	static boolean isRand = true; // TODO 随机身价开关
	
	public static void main(String[] args){

//		String classPath = GameDataJob.class.getResource( "/" ).getPath();
//		logger.info("***"+classPath);
//		PropertyConfigurator.configure(classPath+"log4j.properties");
		
//		这两个是关联的
//		好了，181的gmserver没有启动，所以连接不上。 把ip改成gmserver的地址就没问题了
//		打包的时候记得把playerpush也打包进去， 导入进项目，直接打包即可。
//		E:\\FTXNBA\\Server\\NBASourceServer
		String path = "F:\\FTXNBA\\Server\\NBASourceServer\\config\\config.js";	
		int flush_date = 0;
		
		if(args.length==1){
			path = args[0];
		}
		if(args.length>=2){
			path = args[0];
			flush_date = Integer.parseInt(args[1]);
			ip =  args[2];
			port =  Integer.parseInt(args[3]);
		}
		if("".equals(ip) || ip == null){
			ip = "192.168.10.70";
			port = 2222;
		}
		//
		ResourceCache res = ResourceCache.get();
		res.runInitScript(path);

		res.addResource(ResourceType.NBADataDAO, new NBADataDAO());
		res.addResource(ResourceType.GameDataDAO, new GameDataDAO());

		res.addResource(ResourceType.GameDataAO, new GameDataAO());
		res.addResource(ResourceType.NBADataAO, new NBADataAO());

		matchDataAction = new MatchDataActionImpl();
		nBADataAction  = new NBADataActionImpl();
		// 定时检查连接的健康，后面可以完善
		NBAPush.get().initNBAPush(ip, port);
		// 更新赛程要放开
		UpdateNBAPK();
		// 测试身价更新
		//UpdateNBAPlayer();
		logger.debug("加载最新比赛数据");
		try {
			QuartzServer.get().start();
		} catch (SchedulerException e) {
			logger.error("定时线程异常{}",e);
		}
		
		logger.debug("数据更新完毕");
		//System.exit(1);
	}
	
	/**
	 * 更新身价  
	 */
	public static void UpdateNBAPlayer(){
		logger.debug("开始更新球员身价");
		//是否随机身价
		if(isRand) {
			//休赛期用,随机身价
			Date randDate = GameDataJob.getRandSchedulerDate();
			GameDataJob.run_rand(randDate);
			// 这里有什么用？不是返回最近5场比赛的？
			//matchDataAction.getGameVSByDate(randDate);
		}else {
			// 赛季身价
			GameDataJob.run();
			matchDataAction.saveGameVS();
		}
		logger.debug("完成球员身价更新");
		//matchDataAction.changeTeamRank();
		GameDataJob.change_teamId();
		try {
			NBAPush.get().reloadNBAData();
		} catch (Exception e) {
			logger.error("身价异常:{}",e);
			e.printStackTrace();
		}
	}
	
	/**
	 * 更新赛程
	 */
	public static void UpdateNBAPK(){
		// 
		Date time = null;
		if(isRand) {
			time = GameDataJob.getRandSchedulerDate();
		}else {
			time = new Date();
		}
		GameDataJob.getMatchDataByDate(time);
		try {
			NBAPush.get().reloadNBAPKData();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
//	private static void push(){
//		try {
//			NBAPush.get().reloadNBAPKData("172.18.145.95", 2222);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
	
//	private void getMatchData(int flush_date){
//		logger.info("进行数据抓取:info");
//		try {
//			List<NBAGameDetail> okList = new ArrayList<NBAGameDetail>();
//			List<NBAGameDetail> list = matchDataAction.getNowNBAGameDetail();
//			List<NBAGameDetail> match = new ArrayList<NBAGameDetail>();
//			if(list==null)return;
//			int all_size = list.size();
//			int ok__size = 0;
//			logger.info("--------size---"+list.size());
//			
//			StringBuffer ids = new StringBuffer();
//			for(NBAGameDetail G:list){
//				logger.info(G.toString());
//				String key = Key.nba_data+G.getGameBoxId();
//				if(redis.exits(key)){
//					MatchData data = (MatchData) redis.getObj(key);
//					G.setDateTime(data.getGameDate());
//					if(data.getState().indexOf("Final")==-1){
//						boolean ok = getMatchData(G.getGameBoxId(),0);
//						if(ok)okList.add(G);
//					}else{
//						okList.add(G);
//						ok__size++;
//						match.add(G);
//					}
//				}else{
//					G.setDateTime(new Date());
//					boolean ok = getMatchData(G.getGameBoxId(),0);
//					if(ok)okList.add(G);
//				}
//			}
//			for(NBAGameDetail T:okList){
//				ids.append(T.getGameBoxId()).append(",");
//				//System.out.println("-----------------"+T.getHomeTeamId()+","+T.getAwayTeamId()+"/"+T.toString());
//			}
//			if(match.size()>0)nBADataAction.changeMatchScore(match);//竞猜数据更新
//			
//			redis.pushxx(Key.nba_info, okList,Key.ZERO);			
//			redis.set(Key.nba_data_id, ids.toString());
//			String _info = "数据抓取条数:"+okList.size()+"//"+all_size+"//"+ok__size+"//"+nBADataAction.getNbaDataRunLog()+"--"+ids.toString();
//			logger.info(_info);
//			if(all_size>0 && all_size==ok__size && flush_date==-1 && nBADataAction.getNbaDataRunLog()==0){//提前更新数据
//				nBADataAction.addNbaDataRunLog();
//				run();
//			}
//		} catch (GameDataException e) {
//			logger.error("数据抓取失败:info", e);
//		}
//	}
	
	private static void getMatchDataByDate(Date date){
		logger.info("进行数据抓取:info");
		try {
			List<NBAGameDetail> list = matchDataAction.getNBAGameDetail(date);
			if(list==null) {
				return;
			}
			logger.info("--------getMatchData2 size---"+list.size());
			matchDataAction.clearTodayData();//清空今日比赛数据表:x_data_game_schedule,x_data_score_board,x_data_score_board_detail
			
			for(NBAGameDetail G:list){
				logger.info(G.toString());
				getMatchData(G.getGameBoxId(),1);				
			}			
		} catch (GameDataException e) {
			logger.error("数据抓取失败:info", e);
		}
	}

	private static boolean getMatchData(String boxId,int flag){
		logger.info("进行数据抓取:"+boxId);
		try {
			//String key = Key.nba_data+boxId;
			MatchData data = matchDataAction.getMatchData(boxId);
			if(flag==1) {
				matchDataAction.addCurrData(data);
			}
			//redis.set(key, data,Key.ZERO);
			return true;
		} catch (GameDataException e) {
			logger.error("数据抓取失败:"+boxId, e);
			return false;
		}
	}	
	
	//更新身价
	private static void run() {
		try {			
			List<NBAPlayerDetail> players = nBADataAction.getPlayers();
			List<Integer> list_injuries = matchDataAction.getPlayerInjuries();
			//伤病球员
			List<NBAPlayerDetail> map = new ArrayList<NBAPlayerDetail>();
			for(NBAPlayerDetail T:players){
				if(list_injuries.contains(T.getPlayerId())) {
					map.add(T);
				}
			}
			List<Date>datesNeedRun=matchDataAction.getDatesNeedToRunJob();
			datesNeedRun.add(new Date());
			List<MatchData>dataList=new ArrayList<MatchData>();
			
			Date start =  UtilDateTime.toDataTime("2017-10-17 01:00:00");
			
			for(Date date:datesNeedRun){
				try {
					if(date.getTime()<start.getTime())continue;
					logger.info("2=="+UtilDateTime.toDateString(date, UtilDateTime.SIMPLEFORMATSTRING));
					dataList.addAll(matchDataAction.updateMatchData(date,map));
					
				} catch (Exception e) {
					logger.error(e.getMessage(),e);
				}
			}
			// 计算数据
			if(!dataList.isEmpty()){
				nBADataAction.calculateData(new Date());
			}else{
				nBADataAction.addPlayerMoneyNotData();
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}
	
	/**
	 * 同步球员所在球队
	 */
	private static void change_teamId(){
		List<NBAPlayerDetail> list = new ArrayList<NBAPlayerDetail>();
		list.addAll(matchDataAction.getPlayerListByTeam("chi",101));//
		list.addAll(matchDataAction.getPlayerListByTeam("sa" ,102));//
		list.addAll(matchDataAction.getPlayerListByTeam("mem",103));//
		list.addAll(matchDataAction.getPlayerListByTeam("lac",104));//
		list.addAll(matchDataAction.getPlayerListByTeam("lal",105));//
		list.addAll(matchDataAction.getPlayerListByTeam("sac",106));//
		list.addAll(matchDataAction.getPlayerListByTeam("mia",107));//
		list.addAll(matchDataAction.getPlayerListByTeam("orl",108));//
		list.addAll(matchDataAction.getPlayerListByTeam("ny" ,109));//
		list.addAll(matchDataAction.getPlayerListByTeam("mil",110));//
		list.addAll(matchDataAction.getPlayerListByTeam("hou" ,111));//
		list.addAll(matchDataAction.getPlayerListByTeam("gs" ,112));//
		list.addAll(matchDataAction.getPlayerListByTeam("atl",113));//
		list.addAll(matchDataAction.getPlayerListByTeam("bos",114));//
		list.addAll(matchDataAction.getPlayerListByTeam("wsh",115));//
		list.addAll(matchDataAction.getPlayerListByTeam("cha",116));//
		list.addAll(matchDataAction.getPlayerListByTeam("det",117));//
		list.addAll(matchDataAction.getPlayerListByTeam("ind",118));//
		list.addAll(matchDataAction.getPlayerListByTeam("cle",119));//
		list.addAll(matchDataAction.getPlayerListByTeam("phi",120));//
		list.addAll(matchDataAction.getPlayerListByTeam("dal",121));//
		list.addAll(matchDataAction.getPlayerListByTeam("no" ,122));//
		list.addAll(matchDataAction.getPlayerListByTeam("phx",123));//
		list.addAll(matchDataAction.getPlayerListByTeam("min",124));//
		list.addAll(matchDataAction.getPlayerListByTeam("por",125));//
		list.addAll(matchDataAction.getPlayerListByTeam("okc",126));//
		list.addAll(matchDataAction.getPlayerListByTeam("utah",127));//
		list.addAll(matchDataAction.getPlayerListByTeam("bkn",128));//
		list.addAll(matchDataAction.getPlayerListByTeam("tor",129));//
		list.addAll(matchDataAction.getPlayerListByTeam("den",130));//
		
		Set<Integer> teams = new HashSet<Integer>();
		for(NBAPlayerDetail T:list){
			teams.add(T.getTeamId());
		}
		for(Integer teamId:teams){
			nBADataAction.changeTeamId0(teamId);
		}
		for(NBAPlayerDetail t:list){
			nBADataAction.changeTeamId(t.getPlayerId(), t.getTeamId());
		}
		nBADataAction.changeInjured();
	}
	
	/**
	 * 随机身价，取历史某天比赛
	 * 没配置赛季的时候，默认返回当前时间，注意注意
	 * @return
	 */
	private static Date getRandSchedulerDate() {
		Date randTime = new Date();
		NBASeason season=GameDataAO.getSeason(randTime);
		if(season==null){
			return randTime;
		}
		// 正式随机到的时间
		randTime = nBADataAction.getRandSchedulerDate(season, "2017-10-18", "2018-04-12", 2);
		logger.error("随机身价赛程日期：" + UtilDateTime.toDateString(randTime, UtilDateTime.YYYY_MM_DD));
		return randTime;
	}
	
	/**
	 * 随机身价，指定历史比赛时间
	 * @param randTime
	 */
	private static void run_rand(Date randTime) {
		nBADataAction.calculateData_rand(randTime);
	}

}
