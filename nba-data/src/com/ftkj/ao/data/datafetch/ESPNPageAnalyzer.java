package com.ftkj.ao.data.datafetch;
import com.ftkj.domain.data.GameVS;
import com.ftkj.domain.data.MatchData;
import com.ftkj.domain.data.NBAGameDetail;
import com.ftkj.domain.data.NBAPlayerDetail;
import com.ftkj.domain.data.NBATeamDetail;
import com.ftkj.domain.data.PlayerStats;
import com.ftkj.domain.data.TeamScore;
import com.ftkj.script.JSONUtil;
import com.ftkj.script.espn.A;
import com.ftkj.script.espn.Competitions;
import com.ftkj.script.espn.Events;
import com.ftkj.script.espn.Team;

import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

/**
 * 抓取数据主要解析类 </BR>
 * 数据来源：http://www.espn.com
 */
public class ESPNPageAnalyzer {
    private static final Logger logger = LoggerFactory.getLogger(ESPNPageAnalyzer.class);
	private static final String FIREFOX_UA="Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.6; zh-CN; rv:1.9.2.11) Gecko/20111012 Firefox/3.6.11";


	/**
	 * 受伤球员
	 * @return
	 * @throws PageAnalyzerException
	 */
	public List<Integer> getPlayerInjuries()throws PageAnalyzerException{
		List<Integer> playerIdList=new ArrayList<Integer>();
		String content=getURLPageContent("http://www.espn.com/nba/injuries");
		HtmlCleaner htmlCleaner=new HtmlCleaner();
		try {
			TagNode root=htmlCleaner.clean(content);
			TagNode nodes[]=root.getElementsByAttValue("class","tablehead",true,false);
			//if(nodes.length==0) return result;
			TagNode playerRootNode=nodes[0];
			TagNode[] playerNodes = playerRootNode.getAllElements(true);
			for(TagNode node:playerNodes){
				if(node.getName().equals("a")){
					String tagValue=node.getAttributeByName("href");
					if(tagValue!=null&&tagValue.indexOf("http://www.espn.com/nba/player/_/id")!=-1){
						tagValue = tagValue.replace("http://www.espn.com/nba/player/_/id/", "");
						String playerId=tagValue.split("[/]")[0];
						if(!playerId.equals("")){
							int id = Integer.parseInt(playerId);
							if(!playerIdList.contains(id))playerIdList.add(id);
						}
						//System.out.println(playerId+" "+node.getText());
					}
				}
			}
		} catch (Exception e){
			e.printStackTrace();
		}
		return playerIdList;
	}

	/**
	 * 查询球队球员
	 * @param teamName
	 * @param teamId
	 * @return
	 */
	public List<NBAPlayerDetail> getPlayerListByTeam(String teamName,int teamId){
		List<NBAPlayerDetail> playerList=new ArrayList<NBAPlayerDetail>();
		try {
			//String content=getURLPageContent("http://espn.go.com/nba/teams/roster?team="+teamName);
			String content=getURLPageContent("http://www.espn.com/nba/team/roster/_/name/"+teamName);
			HtmlCleaner htmlCleaner=new HtmlCleaner();
			TagNode root=htmlCleaner.clean(content);
			TagNode nodes[]=root.getElementsByAttValue("class","tablehead",true,false);
			//if(nodes.length==0) return result;
			TagNode playerRootNode=nodes[0];
			TagNode[] playerNodes = playerRootNode.getAllElements(true);
			for(TagNode node:playerNodes){

				if(node.getName().equals("a")){
					String tagValue=node.getAttributeByName("href");
					if(tagValue!=null&&tagValue.indexOf("http://www.espn.com/nba/player/_/id")!=-1){
						tagValue = tagValue.replace("http://www.espn.com/nba/player/_/id/", "");
						//System.out.println("----"+tagValue);
						String playerId=tagValue.split("[/]")[0];
						String pos = node.getParent().getParent().getAllElements(true)[3].getText().toString();
						//System.out.println("----"+node.getParent().getParent().getAllElements(true)[0].getText().toString());
						int player_num = 0;
						String player_num_str = node.getParent().getParent().getAllElements(true)[0].getText().toString();
						if(!player_num_str.equals("--"))
							player_num = Integer.parseInt(player_num_str);
						if(pos.equals("G"))pos="PG/SG";
						else if(pos.equals("F"))pos="SF/PF";
						playerList.add(new NBAPlayerDetail(Integer.parseInt(playerId),teamId));
//						System.out.println("update player_info set posx = '"+pos+"' where player_id = "+playerId+";");
						//System.out.println("update player_info set number="+player_num+" where player_id = "+playerId+";");
						//System.out.println("update player_info set team_id = "+teamId+" where player_id = "+playerId+";");
						//System.out.println("update player_info set team_id = "+teamId+",number="+player_num+" where player_id = "+playerId+";");
						//System.out.println(playerId+",");
					}
				}
			}
		} catch (Exception e)	 {
			e.printStackTrace();
		}
		return playerList;
	}

	/**
	 * 查询比赛数据
	 * @param gameId
	 * @return
	 * @throws PageAnalyzerException
	 */
	public MatchData getMatchData(String gameId) throws PageAnalyzerException{
		MatchData matchData=new MatchData();
		matchData.setGameBoxId(gameId);
		String urlAddr="http://scores.espn.go.com/nba/boxscore?gameId="+gameId;
		String content=null;
		try {
			content = getURLPageContent(urlAddr);
		} catch (PageAnalyzerException e1) {
			content = reGetURLPageContent(urlAddr, 1000);
		}
		if(content==null){
			throw new PageAnalyzerException("can not fetch url page content from:"+urlAddr);
		}
		HtmlCleaner htmlCleaner=new HtmlCleaner();
		try {
			TagNode root=htmlCleaner.clean(content);
			TagNode[]allElement=root.getAllElements(true);

			String keyword="";

			for(TagNode node:allElement){
				if(node.getName().equals("script")){
					if(String.valueOf(node.getText()).contains("espn.gamepackage.timestamp")) {
						String[] msg = node.getText().toString().split("[;]");
						for(String abc:msg){
							if(abc.contains("espn.gamepackage.timestamp")){
								keyword = abc.replace(" ", "").replace("\n", "").replace("espn.gamepackage.timestamp=", "").replace("\"", "").replace("T", " ").replace("Z", "");
								System.out.println("1~~~~~~~~~~~~~~~~~~~~~~"+keyword);
							}
						}
					}
				}
			}
			//
			Date gameDate = transESTTime(keyword);//getGameDate(root);
			//System.out.println("2~~~~~~~~~~~~~~~~~~~~~~"+UtilDateTime.toDateString(gameDate, UtilDateTime.SIMPLEFORMATSTRING));

			if(gameDate!=null){
				matchData.setGameDate(gameDate);
			}
			matchData.setPlayoff(isSeries(root));
			matchData.setState(getGameState(root));
			TagNode[] nodes=root.getElementsByAttValue("class","miniTable",true,false);
			if(nodes.length==0){
				return null;
			}else{
				setPKScore(root, matchData);
			}
			//
			return matchData;
		} catch (Exception e){
			throw new PageAnalyzerException("error fetch match data from:"+urlAddr,e);
		}
	}

	private PlayerStats getPlayerStats(TagNode node,String []tagNames,boolean isStart){
		TagNode[]scoreArr=node.getChildTags();
		PlayerStats ps=new PlayerStats();
		ps.isStarter = isStart;
		for(int i=0;i<scoreArr.length;i++){
			TagNode n=scoreArr[i];
			String name=tagNames[i].toUpperCase();
			String value=n.getText().toString();
			//System.out.println("===xx**************"+name);

			if(name.equals("STARTERS") || name.equals("BENCH")){
				TagNode[] aArr = n.getChildTags();
				//System.out.println("===**************"+aArr.length);
				if(aArr != null && aArr.length==2) {
					TagNode p_name = aArr[0];
					String player_name = p_name.getText().toString();
					TagNode p_pos = aArr[1];
					String player_pos = p_pos.getText().toString();
					//System.out.println("**************"+player_name+" "+player_pos);
					ps.playerName = player_name+","+player_pos;
				}else{
					ps.playerName=value;
				}
			}
			if(name.equals("FG"))ps.fgMA=value;
			if(name.equals("3PT"))ps.threePMA=value;
			if(name.equals("FT"))ps.ftMA=value;
			if(name.equals("OREB")) ps.oreb=value;
			if(name.equals("DREB")) ps.dreb=value;
			if(name.equals("REB"))  ps.reb=value;
			if(name.equals("AST"))  ps.ast=value;
			if(name.equals("STL"))  ps.stl=value;
			if(name.equals("BLK"))  ps.blk=value;
			if(name.equals("TO"))   ps.to=value;
			if(name.equals("PF"))   ps.pf=value;
			if(name.equals("+/-"))  ps.plus=value;
			if(name.equals("PTS"))  ps.pts=value;
			if(name.equals("MIN"))  ps.min=value;
			//if(name.equals("starters")) ps.isStarter=true;
		}
		if(ps.is0())return null;
		return ps;
	}
	//
	private int transInt(String str){
		try{
			return Integer.valueOf(str);
		}catch(Exception e){
			return 0;
		}
	}

	/**
	 * 美国时间
	 * @param date
	 * @return
	 */
	private String getUSATime(Date date){
		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
		Calendar c=Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DAY_OF_YEAR,-1);
		return sdf.format(c.getTime());
	}

	private TeamScore getTeamScore(TagNode node){
		TagNode[]scoreArr=node.getChildTags();
		String teamName = scoreArr[0].getText().toString();
		TeamScore ts=new TeamScore(teamName);
		int score = 0;
		for(int i=1;i<scoreArr.length;i++){
			score = transInt(scoreArr[i].getText().toString().trim());
			ts.scores.add(score);
		}
		return ts;
	}

	public List<String> getPlayerListByPos(String pos) throws PageAnalyzerException{
		List<String> playerList=new ArrayList<String>();
		String content=getURLPageContent("http://espn.go.com/nba/players/_/position/"+pos+"/league/nba");
		HtmlCleaner htmlCleaner=new HtmlCleaner();
		try {
			TagNode root=htmlCleaner.clean(content);
			TagNode nodes[]=root.getElementsByAttValue("class","tablehead",true,false);
			if(nodes.length==0) return playerList;
			TagNode playerRootNode = nodes[0];
			TagNode[] playerNodes = playerRootNode.getAllElements(true);
			for(TagNode playerNode:playerNodes){
				if(playerNode.getName().equals("a")){
					String tagValue=playerNode.getAttributeByName("href");
					if(tagValue!=null&&tagValue.indexOf("playerId")!=-1){
						String playerId=tagValue.substring(tagValue.lastIndexOf('=')+1);
						String name=playerNode.getText().toString();
						playerList.add(playerId+"-"+name);
					}
				}
			}
		} catch (Exception e)	 {
			e.printStackTrace();
		}
		return playerList;
	}



	private List<String> getGameBoxIdByDate(Date date) throws PageAnalyzerException{
		List<String> gameBoxList=new ArrayList<String>();
		String dateStr=getUSATime(date);
		//
		String content=getURLPageContent("http://scores.espn.go.com/nba/scoreboard?date="+dateStr);

		HtmlCleaner htmlCleaner=new HtmlCleaner();
		try {
			TagNode root=htmlCleaner.clean(content);
			TagNode[]allElement=root.getAllElements(true);
			for(TagNode node:allElement){
				if(node.getName().equals("script")){

					if(String.valueOf(node.getText()).contains("window.espn.scoreboardData")) {
						String msg = node.getText().toString();
						if(msg.startsWith("window.espn.scoreboardData")) {
							msg = msg.substring(msg.indexOf("{"), msg.length());
						}
						if(msg.indexOf("window.espn.scoreboardSettings") != -1) {
							msg = msg.substring(0, msg.indexOf("window.espn.scoreboardSettings")-1);
						}
						A a = JSONUtil.fromJson(msg, A.class);
						List<Events> evs = a.getEvents();
						String type_detail = "";
						for(Events t:evs){
							System.out.println("*******"+t.id+"/"+t.competitions.get(0).status.type.detail);
							try{
								type_detail =  t.competitions.get(0).status.type.detail;
							}catch(Exception e){}
							if(!type_detail.equals("Postponed"))gameBoxList.add(t.id);
						}
					}
				}
			}
			//
		}catch (Exception e) {
			throw new PageAnalyzerException("can not fetch data from " +"url:http://scores.espn.go.com/nba/scoreboard",e);
		}
		return gameBoxList;
	}

	private List<String> getGameBoxIdByDate_old(Date date) throws PageAnalyzerException{
		List<String> gameBoxList=new ArrayList<String>();
		String dateStr=getUSATime(date);
		String urlAddr="http://scores.espn.go.com/nba/scoreboard?date="+dateStr;
		//
		String content=null;
		try {
			content = getURLPageContent(urlAddr);
		} catch (PageAnalyzerException e) {
			content = reGetURLPageContent(urlAddr, 1000);
		}
		if(content==null){
			throw new PageAnalyzerException("can not fetch data from url:"+urlAddr);
		}
		StringTokenizer st=new StringTokenizer(content);
		String prefix="id=\"";
		String suffix="-gamebox\"";
		while(st.hasMoreElements()){
			String str=st.nextToken();
			if(str.startsWith(prefix)&&str.endsWith(suffix)){
				String gameboxId=str.substring(prefix.length(),str.length()-suffix.length());
				gameBoxList.add(gameboxId);
			}
		}
		return gameBoxList;
	}

	public String getURLPageContent(String urlAddr) throws PageAnalyzerException{
		return 	getURLPageContent(urlAddr,0);
	}

	public String getURLPageContent(String urlAddr,int count) throws PageAnalyzerException{
		System.out.println("==="+urlAddr);
		try{
			return getURLPageContent(urlAddr,"utf-8");
		}catch (Exception e) {
			count++;
			if(count<=3){
				try {
					Thread.sleep(1000*count);
				} catch (InterruptedException ex) {}
				return getURLPageContent(urlAddr,count);
			}else{
				throw new PageAnalyzerException(e.getMessage(),e);
			}
		}
	}

	public String getURLPageContent(String urlAddr,String charset) throws PageAnalyzerException{
		InputStream is=null;
		try {
			//System.out.println("==="+urlAddr);

			//URLConnection connect = getConnection("http://69.175.21.85/"+urlAddr);
			URLConnection connect = getConnection(urlAddr);
			is = connect.getInputStream();
			BufferedReader br=new BufferedReader(new InputStreamReader(is,charset));
			StringBuilder sb=new StringBuilder();
			String str=null;
			while((str=br.readLine())!=null){
				sb.append(str+"\n");
			}
			connect.getInputStream().close();
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			throw new PageAnalyzerException(e.getMessage(),e);
		}finally{
			if(is!=null){
				try {
					is.close();
				} catch (IOException e) {}
			}
		}
	}

	private String reGetURLPageContent(String urlAddr, long sleepTime) {
		try {
			Thread.sleep(sleepTime);
		} catch (InterruptedException ex) {}
		try {
			return getURLPageContent(urlAddr);
		} catch (PageAnalyzerException ex) {
			logger.error(ex.getMessage(),ex);
		}
		return null;
	}

	private URLConnection getConnection(String urlAddr) throws Exception{
		URL url = new URL(urlAddr);
		URLConnection conn = url.openConnection();
		conn.setRequestProperty ("User-Agent", FIREFOX_UA);
		conn.setConnectTimeout(1000*60*2);
		return conn;
	}

	public List<MatchData> fetchMatchData(Date date,List<Integer> list) throws ParseException, PageAnalyzerException{
		List<MatchData> matchDataList=new ArrayList<MatchData>();
		List<String> gameBoxIdList=getGameBoxIdByDate(date);
		for(String gameboxId:gameBoxIdList){
			if(list.contains(Integer.parseInt(gameboxId)))continue;
			try{
				MatchData data=getMatchData(gameboxId);
				data.setGameDate(date);
				if(data!=null){
					matchDataList.add(data);
				}
			}catch(Exception e){
				throw new PageAnalyzerException("error fetchMatchData from:"+date,e);
			}
		}
		return matchDataList;
	}

	private NBAGameDetail getGameDetail(TagNode node,String gameId,Events A){
		String[] homeData=getTeamNameAndScore(node,gameId,true,A.competitions.get(0));
		String[] awayData=getTeamNameAndScore(node,gameId,false,A.competitions.get(0));

		NBAGameDetail detail=new NBAGameDetail();
		detail.setGameBoxId(gameId);
		detail.setHomeTeamName(homeData[0]);
		if(homeData[1].trim().isEmpty()){
			detail.setHomeTeamScore(-1);
		}else{
			detail.setHomeTeamScore(Integer.valueOf(homeData[1]));
		}
		detail.setAwayTeamName(awayData[0]);
		if(awayData[1].trim().isEmpty()){
			detail.setAwayTeamScore(-1);
		}else{
			detail.setAwayTeamScore(Integer.valueOf(awayData[1]));
		}
		/*
		TagNode[] statusLine1Node= node.getElementsByAttValue("id",gameId+"-statusLine1", true, true);
		if(statusLine1Node[0].getText().toString().indexOf("Final")!=-1){
			detail.setStatus("Final");
		}*/
		detail.setStatus(A.competitions.get(0).status.type.detail);
		return detail;
	}

	private String[] getTeamNameAndScore(TagNode node,String gameId,boolean isHome,Competitions E){
		if(isHome)
			return new String[]{E.competitors.get(0).team.displayName,E.competitors.get(0).score};
		else
			return new String[]{E.competitors.get(1).team.displayName,E.competitors.get(1).score};
		/*

		String home=isHome?"h":"a";
		TagNode[] teamNameNode=node.getElementsByAttValue("id",gameId+"-"+home+"TeamName", true, true);		
		String teamName=teamNameNode[0].getChildTags()[0].getText().toString();
		System.out.println("/////"+teamName);
		TagNode[] scoreNode=node.getElementsByAttValue("id",gameId+"-"+home+"lsh6", true, true);
		String score=scoreNode[0].getText().toString();
		if(score.equals("&nbsp;")){
			score="";
		}
		return new String[]{teamName,score};
		 */
	}

	private List<String> getRealBoxId(String content){
		List<String> list = new ArrayList<String>();
		HtmlCleaner htmlCleaner=new HtmlCleaner();
		try {
			TagNode root=htmlCleaner.clean(content);
			TagNode[]allElement=root.getAllElements(true);
			for(TagNode node:allElement){
				if(node.getName().equals("a")){
					String id=node.getAttributeByName("href");
					if(id!=null && node.getText().toString().indexOf("Score")!=-1 && id.startsWith("/nba/boxscore?gameId=")){
						String gameId=id.replace("/nba/boxscore?gameId=", "");
						System.out.println("---------------"+gameId);
						list.add(gameId);
					}
				}
			}
			//
		}catch (Exception e) {}
		return list;
	}

	private int getPostponed(String content){
		int size = 0;
		HtmlCleaner htmlCleaner=new HtmlCleaner();
		try {
			TagNode root=htmlCleaner.clean(content);
			TagNode[]allElement=root.getAllElements(true);
			for(TagNode node:allElement){
				if(node.getName().equals("p")){
					if(node.getChildTagList().toString().contains("Postponed")){
						size++;
					}
				}
			}
			//
		}catch (Exception e) {}
		return size;
	}

	/*
	public List<NBAGameDetail> _getNowNBAGameDetail() throws PageAnalyzerException{
		List<NBAGameDetail> nbaGameDetailList=new ArrayList<NBAGameDetail>();
		String dateStr=getUSATime(new Date());
		String content=getURLPageContent("http://scores.espn.go.com/nba/scoreboard?date="+dateStr);
		HtmlCleaner htmlCleaner=new HtmlCleaner();
		try {
			TagNode root=htmlCleaner.clean(content);
			TagNode[]allElement=root.getAllElements(true);
			for(TagNode node:allElement){
				if(node.getName().equals("a")){
					String id=node.getAttributeByName("href");
					if(id!=null && node.getText().toString().indexOf("Score")!=-1 && id.startsWith("/nba/boxscore?gameId=")){
						String gameId=id.replace("/nba/boxscore?gameId=", "");
						System.out.println("=="+gameId);
						NBAGameDetail detail=getGameDetail(node,gameId);
						nbaGameDetailList.add(detail);
					}
				}
			}
			//
		}catch (Exception e) {
			throw new PageAnalyzerException("can not fetch data from " +"url:http://scores.espn.go.com/nba/scoreboard",e);
		}
		return nbaGameDetailList;
	}
	 */

	/**
	public List<NBAGameDetail> getNowNBAGameDetail() throws PageAnalyzerException{
		List<NBAGameDetail> nbaGameDetailList=new ArrayList<NBAGameDetail>();
		String dateStr=getUSATime(new Date());
		String content=getURLPageContent("http://scores.espn.go.com/nba/scoreboard?date="+dateStr);

		//int postponed = getPostponed(content);

		//List<String> boxIdList = new ArrayList<String>();
		//if(postponed>0){
		//	boxIdList = getRealBoxId(content);
		//}

		HtmlCleaner htmlCleaner=new HtmlCleaner();
		try {
			TagNode root=htmlCleaner.clean(content);
			TagNode[]allElement=root.getAllElements(true);
			for(TagNode node:allElement){
				if(node.getName().equals("div")){

					//System.out.println("//"+node.getAttributeByName("class"));
					if(node.getAttributeByName("class")!=null && node.getAttributeByName("class").equals("main-content layout-bc")){
						//TagNode[] _allElement=node.getAllElements(true);

						for(TagNode _node:allElement){
							if(_node.getName().equals("div")){
								if(_node.getAttributeByName("data-data")!=null){
									System.out.println("--"+_node.getAttributeByName("data-data"));

									A a = JSONUtil.fromJson(_node.getAttributeByName("data-data"), A.class);

									List<Events> evs = a.getEvents();
									for(Events t:evs){
										System.out.println("//"+t.competitions.get(0).competitors.size());
										//System.out.println("//"+t.competitions.get(0).competitors.get(0).winner);
										//System.out.println("//"+t.competitions.get(0).competitors.get(1).winner);

										NBAGameDetail detail=getGameDetail(node,t.id,t);
										nbaGameDetailList.add(detail);
									}

								}
							}
						}
					}

					//String id=node.getAttributeByName("id");
					//if(id!=null&&id.endsWith("-gamebox")){
					//String gameId=id.substring(0,id.lastIndexOf('-'));
					//	if(postponed>0 && !boxIdList.contains(gameId))continue;
					//	System.out.println("=="+gameId);
					//	NBAGameDetail detail=getGameDetail(node,gameId);
					//	nbaGameDetailList.add(detail);
					//}
				}
			}
			//
		}catch (Exception e) {
			throw new PageAnalyzerException("can not fetch data from " +"url:http://scores.espn.go.com/nba/scoreboard",e);
		}
		return nbaGameDetailList;
	}*/


	/**
	 * 比赛明细，当前美国时间
	 * @return
	 * @throws PageAnalyzerException
	 */
	public List<NBAGameDetail> getNowNBAGameDetail(Date date) throws PageAnalyzerException{
		List<NBAGameDetail> nbaGameDetailList=new ArrayList<NBAGameDetail>();
		String dateStr=getUSATime(date);
		String content=getURLPageContent("http://scores.espn.go.com/nba/scoreboard?date="+dateStr);
		/*
		int postponed = getPostponed(content);

		List<String> boxIdList = new ArrayList<String>();
		if(postponed>0){
			boxIdList = getRealBoxId(content);
		}
		 */
		HtmlCleaner htmlCleaner=new HtmlCleaner();
		try {
			TagNode root=htmlCleaner.clean(content);
			TagNode[]allElement=root.getAllElements(true);
			for(TagNode node:allElement){
				if(node.getName().equals("script")){

					if(String.valueOf(node.getText()).contains("window.espn.scoreboardData")) {
						String msg = node.getText().toString();
						if(msg.startsWith("window.espn.scoreboardData")) {
							msg = msg.substring(msg.indexOf("{"), msg.length());
						}
						if(msg.contains("window.espn.scoreboardSettings")) {
							msg = msg.substring(0, msg.indexOf("window.espn.scoreboardSettings")-1);
						}
						A a = JSONUtil.fromJson(msg, A.class);
						List<Events> evs = a.getEvents();
						for(Events t:evs){
							System.out.println("//"+t.competitions.get(0).competitors.size());

							NBAGameDetail detail=getGameDetail(node,t.id,t);
							System.out.println("//////////////"+detail.getGameBoxId()+":"+detail.getStatus());
							if(detail.getStatus().equals("Postponed"))continue;
							nbaGameDetailList.add(detail);
						}
					}
				}
			}
			//
		}catch (Exception e) {
			throw new PageAnalyzerException("can not fetch data from " +"url:http://scores.espn.go.com/nba/scoreboard",e);
		}
		return nbaGameDetailList;
	}
	/*
	public List<NBAGameDetail> getNowNBAGameDetail_old() throws PageAnalyzerException{
		List<NBAGameDetail> nbaGameDetailList=new ArrayList<NBAGameDetail>();
		String dateStr=getUSATime(new Date());
		String content=getURLPageContent("http://scores.espn.go.com/nba/scoreboard?date="+dateStr);
		int postponed = getPostponed(content);
		List<String> boxIdList = new ArrayList<String>();
		if(postponed>0){
			boxIdList = getRealBoxId(content);
		}

		HtmlCleaner htmlCleaner=new HtmlCleaner();
		try {
			TagNode root=htmlCleaner.clean(content);
			TagNode[]allElement=root.getAllElements(true);
			for(TagNode node:allElement){
				if(node.getName().equals("div")){
					String id=node.getAttributeByName("id");
					if(id!=null&&id.endsWith("-gamebox")){
						String gameId=id.substring(0,id.lastIndexOf('-'));
						if(postponed>0 && !boxIdList.contains(gameId))continue;
						System.out.println("=="+gameId);
						NBAGameDetail detail=getGameDetail(node,gameId);
						nbaGameDetailList.add(detail);
					}
				}
			}
			//
		}catch (Exception e) {
			throw new PageAnalyzerException("can not fetch data from " +"url:http://scores.espn.go.com/nba/scoreboard",e);
		}
		return nbaGameDetailList;
	}
	 */

	public List<GameVS> getGameVSByDate(Date date){
		List<GameVS> gameVSList = new ArrayList<GameVS>();
		String dateStr=getUSATime(date);

		//System.out.println("///"+dateStr);

		HtmlCleaner htmlCleaner=new HtmlCleaner();
		try {
			//List<String> gameIdList = new ArrayList<String>();
			String content=getURLPageContent("http://scores.espn.com/nba/scoreboard?date="+dateStr);
			TagNode root=htmlCleaner.clean(content);

			TagNode[]allElement=root.getAllElements(true);
			for(TagNode node:allElement){
				if(node.getName().equals("script")){
					if(String.valueOf(node.getText()).contains("window.espn.scoreboardData")) {
						String msg = node.getText().toString();
						if(msg.startsWith("window.espn.scoreboardData")) {
							msg = msg.substring(msg.indexOf("{"), msg.length());
						}
						if(msg.indexOf("window.espn.scoreboardSettings") != -1) {
							msg = msg.substring(0, msg.indexOf("window.espn.scoreboardSettings")-1);
						}
						A a = JSONUtil.fromJson(msg, A.class);
						List<Events> evs = a.getEvents();
						Team homeTeam,awayTeam;
						for(Events t:evs){
							homeTeam = t.competitions.get(0).competitors.get(1).team;
							awayTeam = t.competitions.get(0).competitors.get(0).team;
							//System.out.println("//"+t.id+" / "+homeTeam.abbreviation+" /" +homeTeam.displayName+" / "+ awayTeam.abbreviation+" /" +awayTeam.displayName+ "/"+date);
							gameVSList.add(new GameVS(homeTeam.abbreviation.toLowerCase(),homeTeam.displayName,awayTeam.abbreviation.toLowerCase(),awayTeam.displayName,t.id,date));
						}
					}
				}
			}
			/*

			TagNode[] nodes=root.getElementsByAttValue("class","mod-content",true,false);
			Map<String,String> nameMap = new HashMap<String,String>();
			for(TagNode playerTable:nodes){			
				for(TagNode node:playerTable.getAllElements(true)){					
					if(node.getName().equals("a")){
						String aValue=node.getAttributeByName("href");						
						if(aValue.indexOf("/nba/team/_/name/")!=-1){
							String val = aValue.replace("http://espn.go.com/nba/team/_/name/", "").split("[/]")[0];
							if(!gameIdList.contains(val)){
								gameIdList.add(val);
								nameMap.put(val,node.getText().toString());								
							}
						}
						if(aValue.indexOf("/nba/conversation?gameId")!=-1){//
							String val = aValue.replace("/nba/conversation?gameId=", "");
							if(!gameIdList.contains(val)){
								gameIdList.add(val);
							}
						}
					}
				}
			}
			GameVS G = new GameVS();
			for(int i=0;i<gameIdList.size();i+=3){
				G = new GameVS(gameIdList.get(i),
						nameMap.get(gameIdList.get(i)),
						gameIdList.get(i+1),
						nameMap.get(gameIdList.get(i+1)),
						gameIdList.get(i+2),
						date);
				gameVSList.add(G);			
			}
			 */
		} catch (Exception e){
			e.printStackTrace();
		}
		return gameVSList;
	}

	public List<NBATeamDetail> changeTeamRank(){
		List<NBATeamDetail> teamRankList = new ArrayList<NBATeamDetail>();
		try {
			String content=getURLPageContent("http://www.espn.com/nba/standings");
			HtmlCleaner htmlCleaner=new HtmlCleaner();
			TagNode root=htmlCleaner.clean(content);

			TagNode[]allElement=root.getAllElements(true);
			for(TagNode node:allElement){
				if(node.getName().equals("div")){
					if(node.getAttributeByName("class")!=null && node.getAttributeByName("class").equals("responsive-table-wrap")){
						TagNode mNode=node.getChildTags()[0];

						//System.out.println("=="+mNode.getText());
						int x_rank=1;
						for(TagNode _node:mNode.getAllElements(true)){
							if(_node.getName()!=null && _node.getName().equals("tr")){
								int i = 0;
								StringBuffer str = new StringBuffer();
								for(TagNode x:_node.getAllElements(true)){
									//System.out.print(i+"="+x.getText()+",");
									if(i==7 || i==8 || i==9 || i==19){
										str.append(x.getText()).append("|");
									}
									i++;
								}
								//System.out.println("//"+str);
								String[] xxx = str.toString().split("[|]");
								if(str.indexOf("DIV")==-1){
									teamRankList.add(new NBATeamDetail(xxx[0]+"|"+x_rank+"|"+xxx[1]+"|"+xxx[2]+"|"+xxx[3]));
								}else{
									x_rank = 0;
								}
								x_rank++;
							}
						}

						/*
						TagNode[] headNodeArr=mNode.getChildTags()[0].getElementsByName("th", true);

						System.out.println("//"+headNodeArr.length);

						String[] tagNames=new String[headNodeArr.length];
						int i=0;
						for(TagNode head:headNodeArr) {
							tagNames[i++]=head.getText().toString();

						}
						System.out.println("//"+tagNames[0]);
						 */
					}
				}
			}


			/*
			TagNode[] nodes=root.getElementsByAttValue("class","main-content layout-full",true,false);			

			TagNode mNode=nodes[0];			
			TagNode[] headNodeArr=mNode.getChildTags()[0].getChildTags()[1].getElementsByName("th", true);
			String[] tagNames=new String[headNodeArr.length];
			int i=0;
			for(TagNode head:headNodeArr) 
				tagNames[i++]=head.getText().toString();

			List<String> wlStrList = new ArrayList<String>();
			String wlVal = "";
			String cVal;
			TagNode[] scoreNodeArr;
			int rank = 0;
			for(TagNode node:mNode.getAllElements(true)){
				if(node.getName().equals("tr")){
					cVal=node.getAttributeByName("class");
					if(cVal!=null&&cVal.indexOf("standings")!=-1){	

						System.out.println("///");

						rank++;
						scoreNodeArr=node.getChildTags();
						if(rank>=16)rank=rank-15;
						StringBuffer sb = new StringBuffer();
						sb.append(rank).append("|");
						for(int x=0;x<scoreNodeArr.length;x++){
							if(x==2||x==3||x==13){
								TagNode n=scoreNodeArr[x];
								wlVal=n.getText().toString().replace("\n", "").replace(" ", "");
								if(wlVal.indexOf("Won")!=-1) wlVal=wlVal.replace("Won", "");
								if(wlVal.indexOf("Lost")!=-1) wlVal=wlVal.replace("Lost", "-");								
								sb.append(wlVal+"|");
							}
						}
						wlStrList.add(sb.toString());

						System.out.println("//"+sb.toString());
					}
				}
			}
			rank = 0;
			TagNode playerTable=nodes[0];			
			for(TagNode node:playerTable.getAllElements(true)){
				if(node.getName().equals("a")){					
					cVal=node.getAttributeByName("href");
					if(cVal!=null&&cVal.indexOf("http://espn.go.com/nba/team/_/name/")!=-1){
						cVal = cVal.replace("http://espn.go.com/nba/team/_/name/", "");
						String shortName=cVal.split("[/]")[0];
						teamRankList.add(new NBATeamDetail(shortName+"|"+wlStrList.get(rank)));
						rank++;
					}
				}
			}*/
		} catch (Exception e)	 {
			e.printStackTrace();
		}
		return teamRankList;
	}
	//
	private void setPKScore(TagNode root, MatchData matchData) {
		TagNode[] nodes=root.getElementsByAttValue("class","miniTable",true,false);
		TagNode scoreTable=nodes[0];
		TagNode[]trs=scoreTable.getElementsByName("tr", true);
		matchData.setScoreHome(getTeamScore(trs[1]));
		matchData.setScoreAway(getTeamScore(trs[2]));
		//


		nodes=root.getElementsByAttValue("class", "mod-data",true, false);

		//System.out.println("%%%%%%%%%"+nodes.length);
		for(int xx=0;xx<nodes.length;xx++){
			TagNode mNode=nodes[xx];
			boolean isAway=false,isHome=false;
			if(xx == 0) {
				isHome = true;
				isAway = false;
			}else {
				isAway = true;
				isHome = false;
			}

			// 取标头
			//System.out.println("$$$$$$$$$$$$$$$$"+mNode.getElementsByName("thead", true).length);
			if(mNode.getElementsByName("thead", true).length==2){
				for(int jjj=0;jjj<2;jjj++){
					TagNode thead = mNode.getElementsByName("thead", true)[jjj];
					TagNode[] headNodeArr=thead.getElementsByName("th", true);//mNode.getChildTags()[0].getChildTags()[1].getElementsByName("th", true);
					String[] tagNames=new String[headNodeArr.length];
					int i=0;
					for(TagNode head:headNodeArr) {
						tagNames[i++]=head.getText().toString();
						//System.out.println("~~~~~~~~~~ i = " + i + ", txt = " + head.getText().toString());
					}
					// 取球员数据
					TagNode tbody = mNode.getElementsByName("tbody", true)[jjj];
					headNodeArr=tbody.getElementsByName("tr", true);
					for(TagNode node:headNodeArr){
						if(node.getName().equals("tr")){
							String cVal=node.getChildTags()[0].getAttributeByName("class");
							if(cVal!=null&&cVal.indexOf("name")!=-1){
								TagNode[] aArr = node.getChildTags()[0].getElementsByName("a", true);
								if(aArr == null || aArr.length == 0) {
									continue;
								}
								TagNode a = aArr[0];
								//System.out.println("~~~~~~~~~~playerId = "+a.getAttributeByName("href"));
								String playerIdStr = a.getAttributeByName("href").replace("http://www.espn.com/nba/player/_/id/", "");

								//System.out.println("xxx~~~~~~~~~~playerId = "+a.getText());

								//String _playerName = a.getChildren()[0]
								//System.out.println("~~~~~~~~~~playerId = "+playerIdStr);
								//
								//String playerId=playerIdStr.substring(playerIdStr.lastIndexOf('/')+1);
								String playerId=playerIdStr.split("[/]")[0];
								TagNode[]score=node.getChildTags();
								if(score.length==15||score.length==14){

									PlayerStats ps=getPlayerStats(node,tagNames,jjj==0);

									//System.out.println("//--------------------"+ps);

									if(ps==null)continue;
									ps.playerId=Integer.valueOf(playerId);
									if(isHome){
										matchData.getPlayerScoreHome().add(ps);
									}
									if(isAway){
										matchData.getPlayerScoreAway().add(ps);
									}
									System.out.println(isHome+"//--------------------"+ps);
								}
							}

						}
					}
				}
			}
		}
	}
	//
	private String getGameState(TagNode root) {
		try{
			TagNode state[]=root.getElementsByAttValue("class","game-time status-detail",true,false);

			System.out.println("===getGameState=="+state[0].getText().toString());

			return state[0].getText().toString();
		}catch(Exception e){
			TagNode state[]=root.getElementsByAttValue("class","game-time",true,false);

			System.out.println("===getGameState=="+state[0].getText().toString());

			return state[0].getText().toString();
		}
	}

	private boolean isSeries(TagNode root) {
		TagNode seriesNode[]=root.getElementsByAttValue("class","series-status",true,false);
		if(seriesNode!=null&&seriesNode.length!=0){
			String status=seriesNode[0].getChildTags()[0].getText().toString();
			return status.indexOf("Series")==-1;
		}
		return false;
	}

	private Date getGameDate(TagNode root) throws ParseException {
		TagNode timeNode[]=root.getElementsByAttValue("class","game-time-location",true,false);
		if(timeNode==null||timeNode.length==0){
			logger.error("can not found pkTime");
			return null;
		}else{
			String etTime=timeNode[0].getChildTags()[0].getText().toString();
			return transESTTime(etTime);
		}
	}

	private Date transESTTime(String ETTime) throws ParseException{
		/*
		Locale.setDefault(new Locale("en_US"));
		SimpleDateFormat sdf=new SimpleDateFormat("hh:mm aaa z, MMMMM dd, yyyy");
		return sdf.parse(ETTime.replaceAll("ET","EST"));
		 */
		//2016-02-19 01:00
		//Locale.setDefault(new Locale("en_US"));
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd hh:mm");
		return sdf.parse(ETTime);
		//return UtilDateTime.getNextDateAddHour(sdf.parse(ETTime),12);
	}

	private void getPlayerNumber(int playerId)throws Exception{
		String content=getURLPageContent("http://espn.go.com/nba/player/_/id/"+playerId);
		HtmlCleaner htmlCleaner=new HtmlCleaner();
		TagNode root=htmlCleaner.clean(content);
		TagNode[] nodes=root.getElementsByAttValue("class","first",true,false);

		String a = "";
		for(TagNode playerTable:nodes){
			a = playerTable.getText().toString();
			if(a.indexOf("#")!=-1){
				System.out.println("update player_info set number='"+a.split("[ ]")[0].replace("#", "")+"' where player_id="+playerId+";");
			}
		}
	}
	//
	public static void main(String[] args)throws Exception {
		ESPNPageAnalyzer f=new ESPNPageAnalyzer();

		//System.out.println(f.getMatchData("400579400").getScoreAway());

		//String content=f.getURLPageContent("http://espn.go.com/nba/scoreboard");
		//f.getNowNBAGameDetail();


		//f.getRealBoxId(content);

		//f.getNowNBAGameDetail();
		//List<String> list = f.getGameBoxIdByDate(new Date());
		//for(String a:list)
		//	System.out.println("=="+a);
		/*
		List<String> list = f.getPlayerListByPos("sg");
		for(String s:list){
			System.out.println("s ------------ "+s);
		}
		 */
		//f.getGameVS("20120512");

		//System.out.println(f.getMatchData("311229019"));
		//List<Integer> a = f.getInjuries();
		//for(int x:a)
		//	System.out.println("=="+x);
		//System.out.println(f.getMatchData("310531014"));
		/*
		for(int i=0;i<10;i++)
			f.getGameVS(UtilDateTime.getNextDateAddDay(new Date(), i));
		/*
		System.out.println(f.getMatchData("310216008"));
		List<NBAGameInfo> list = f.getCurrentGameSchedule();
		for(NBAGameInfo o:list)
			System.out.println(o);

		 */
		//f.changeRank();
		/* */
		f.getPlayerListByTeam("chi",101);
		f.getPlayerListByTeam("sa" ,102);
		f.getPlayerListByTeam("mem",103);
		f.getPlayerListByTeam("lac",104);
		f.getPlayerListByTeam("lal",105);
		f.getPlayerListByTeam("sac",106);
		f.getPlayerListByTeam("mia",107);
		f.getPlayerListByTeam("orl",108);
		f.getPlayerListByTeam("ny" ,109);
		f.getPlayerListByTeam("mil",110);
		f.getPlayerListByTeam("hou",111);
		f.getPlayerListByTeam("gs" ,112);
		f.getPlayerListByTeam("atl",113);
		f.getPlayerListByTeam("bos",114);
		f.getPlayerListByTeam("wsh",115);
		f.getPlayerListByTeam("cha",116);
		f.getPlayerListByTeam("det",117);
		f.getPlayerListByTeam("ind",118);
		f.getPlayerListByTeam("cle",119);
		f.getPlayerListByTeam("phi",120);
		f.getPlayerListByTeam("dal",121);
		f.getPlayerListByTeam("no" ,122);
		f.getPlayerListByTeam("phx",123);
		f.getPlayerListByTeam("min",124);
		f.getPlayerListByTeam("por",125);
		f.getPlayerListByTeam("okc",126);
		f.getPlayerListByTeam("utah",127);
		f.getPlayerListByTeam("bkn" ,128);
		f.getPlayerListByTeam("tor",129);
		f.getPlayerListByTeam("den",130);
		/* */

		//f.getPlayerNumber(3018);


	}
}
