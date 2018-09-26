package com.ftkj.ao.data;

import com.ftkj.ao.BaseAO;
import com.ftkj.dao.data.NBADataDAO;
import com.ftkj.domain.data.NBAPlayerDetail;
import com.ftkj.domain.data.NBAPlayerScore;
import com.ftkj.domain.data.NBATeamDetail;
import com.ftkj.domain.data.PlayerAbi;
import com.ftkj.domain.data.PlayerAvgRate;
import com.ftkj.domain.data.PlayerPrice;
import com.ftkj.domain.data.PlayerPricePolicy;
import com.ftkj.invoker.Resource;
import com.ftkj.invoker.ResourceType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NBADataAO extends BaseAO {
    private static final Logger logger = LoggerFactory.getLogger(NBADataAO.class);
	@Resource(value = ResourceType.NBADataDAO)
	public NBADataDAO nbaDataDAO;

	public List<NBAPlayerDetail> getPlayers() {
		return nbaDataDAO.getPlayers();
	}

	public void changeTeamId(int playerId, int teamId) {
		nbaDataDAO.changeTeamId(playerId, teamId);
	}

	public List<PlayerPrice> getPlayerMoneyList(int playerId) {
		return nbaDataDAO.getPlayerMoneyList(playerId);
	}

	public List<NBAPlayerScore> getPlayerScoreDay(int playerId) {
		return nbaDataDAO.getPlayerScoreDay(playerId);
	}

	public void addPlayerMoney() {
		nbaDataDAO.addPlayerMoneyHaveData();
	}

	public void changePlus(int seasonId) {
		nbaDataDAO.changePlus(seasonId);
	}

	public void delete_price_50() {
		nbaDataDAO.delete_price_50();
	}

	public void _changePlus(int seasonId) {
		nbaDataDAO.changePlus(seasonId, 1);
		nbaDataDAO.changePlus(seasonId, 2);
	}

	public String getRandSchedulerTime() {
		return nbaDataDAO.getRandSchedulerTime();
	}
	
	public List<String> getSchedulerSeason(int seasionId, String startTime, String endTime, int minVs) {
		
		return nbaDataDAO.getSchedulerSeason(seasionId, startTime, endTime, minVs);
	}

	public void addPlayerMoneyNotData() {
		nbaDataDAO.addPlayerMoneyNotData();
	}

	public String getMaxNbaGameData() {
		return nbaDataDAO.getMaxNbaGameData();
	}

	public int getFBGrade(String id) {
		return nbaDataDAO.getFBGrade(id);
	}

	public List<PlayerAbi> getPlayerCaps() {
		return nbaDataDAO.getPlayerCaps();
	}

	public List<PlayerAvgRate> getPlayerAvgs(int seasonId) {
		return nbaDataDAO.getPlayerAvgs(seasonId);
	}

	public List<NBATeamDetail> getTeams() {
		return nbaDataDAO.getTeams();
	}

	public void addPlayerBaseInfo(NBAPlayerDetail info) {
		nbaDataDAO.addPlayerBaseInfo(info);
	}

	/**
	 * 计算指定赛季,打过比赛的球员数据平均值，表：data_player_avg 
	 * </BR> 先把赛季的球员添加到avg，再联表查询更新
	 * @param seasonId
	 */
	public void executePlayerAvg(int seasonId) {
		// nbaDataDAO.deletePlayerAvg(seasonId);
		nbaDataDAO.addPlayerAvg(seasonId);
		nbaDataDAO.calculatePlayerAvg(seasonId);
	}

	/**
	 * 身价-核心算法
	 * @param seasonId
	 * @param randTime 随机身价时间
	 */
	public void executePlayerAbi(int seasonId, String randTime) {
		// 是否是随机身价
		boolean isRand = randTime==null||randTime.equals("")?false:true;
		nbaDataDAO.initCap();//player_info表中有新球员数据在data_player_cap表不存在该球员数据，则默认插入一条数据：attr_cap=25,gua_cap=25,cap=50

		Map<Integer, Integer> NList = getNMap();//新秀特殊映射值
		Map<Integer, Integer> attrMap = new HashMap<Integer, Integer>();

		// 新秀刚出来时，在休赛期不更新数据
		List<Integer> newNList = isRand ? getNNList() : new ArrayList<Integer>();
		List<NBAPlayerDetail> infos = nbaDataDAO.getPlayers();//查询所有球员数据player_info
		//上上赛季,data_player_avg
		List<PlayerAvgRate> avgs_old = nbaDataDAO.getPlayerAvgs(seasonId - 2);// seasonId-2
		//上赛季,data_player_avg
		List<PlayerAvgRate> avgs_last = nbaDataDAO.getPlayerAvgs(seasonId - 1);// seasonId-1
		// 本赛季,data_player_avg
		List<PlayerAvgRate> avgs_cur = nbaDataDAO.getPlayerAvgs(seasonId);
		//
		Map<Integer, PlayerAvgRate> seasonOld = new HashMap<Integer, PlayerAvgRate>();
		for (PlayerAvgRate avg : avgs_old) {
			seasonOld.put(avg.getPlayerId(), avg);
		}
		Map<Integer, PlayerAvgRate> seasonLast = new HashMap<Integer, PlayerAvgRate>();
		for (PlayerAvgRate avg : avgs_last) {
			// 上季没怎么比赛，取上上赛季
			if (avg.getPlayCount() <= 0
					&& seasonOld.containsKey(avg.getPlayerId())) {
				seasonLast.put(avg.getPlayerId(), seasonOld.get(avg.getPlayerId()));
			} else {
				seasonLast.put(avg.getPlayerId(), avg);
			}
		}
		Map<Integer, PlayerAvgRate> seasonCurrent = new HashMap<Integer, PlayerAvgRate>();
		for (PlayerAvgRate avg : avgs_cur) {
			seasonCurrent.put(avg.getPlayerId(), avg);
		}

		// String rand_time = nbaDataDAO.getRandSchedulerTime();//休赛期

		for (NBAPlayerDetail info : infos) {

			if (info.getGrade().equals("X") || info.getTeamId() == -1
					|| info.getTeamId() == 0) {
				continue;
			}
			/*
			 * if(!info.getGrade().equals("X")){ continue; }
			 */
			int playerId = info.getPlayerId();

			// 新秀刚出来时，在休赛期不更新数据
			if (newNList.contains(playerId)) {
				continue;
			}

			int thisPlayCount = 0, lastPlayCount = 0;

			PlayerAvgRate lastSeason = seasonLast.get(playerId);
			if (NList.containsKey(playerId)) {// N 特别处理
				lastSeason = seasonLast.get(NList.get(playerId));
			}
			if (lastSeason != null) {
				lastPlayCount = lastSeason.getPlayCount();
			}

//			PlayerAvgRate thisSeason = seasonCurrent.get(playerId);
//			if (thisSeason != null) {
//				thisPlayCount = thisSeason.getPlayCount();
//			}
			
			// 指定赛季的最近1场比赛?
			List<PlayerAvgRate> list = null;
			if(!isRand) {
				// 最后一天--正式期用
				list = nbaDataDAO.getRecentAvg(playerId, seasonId, PlayerAbi.RECENT_NUMBER);
			}else {
				//指定一天--休赛期用，随机身价
				 list=nbaDataDAO.getRecentAvg_time(playerId, seasonId, randTime, PlayerAbi.RECENT_NUMBER);
			}
			// list = nbaDataDAO.getRecentAvg(playerId,seasonId,PlayerAbi.RECENT_NUMBER);//不再使用
			// 无PK的，数据不动
			if (list.size() == 0) {
				continue;
			}
			
			// ***************************************************
			//x_data_score_board_detail
			PlayerAvgRate recentAvg = nbaDataDAO.getCurPlayerAvg(playerId);//最近1场比赛数据
			int start = 0;
			if(recentAvg == null){
				// 随机的则取指定天数
				if(isRand) {
					recentAvg = nbaDataDAO.getPlayerAvgOther(playerId, seasonId, "0",start, PlayerAbi.RECENT_NUMBER, randTime);
				}else {
					recentAvg = nbaDataDAO.getPlayerAvgOther(playerId, seasonId, "0",start, PlayerAbi.RECENT_NUMBER);
				}
				start = PlayerAbi.RECENT_NUMBER;
			}
			// 今日之后的赛程，排除
			String gameIDs = nbaDataDAO.getCurSchedule();
			if("null".equals(gameIDs) || "".equals(gameIDs) || gameIDs == null){
				gameIDs = "0";
			}
			PlayerAvgRate otherSeason2 = nbaDataDAO.getPlayerAvgOther(playerId, seasonId, gameIDs,start,10);//最近2-11场比赛数据
			start+=10;
			PlayerAvgRate otherSeason3 = nbaDataDAO.getPlayerAvgOther(playerId, seasonId, gameIDs,start,10);//最近12-21场比赛数据
			start+=10;
			PlayerAvgRate otherSeason4 = nbaDataDAO.getPlayerAvgOther(playerId, seasonId, gameIDs,start,10);//最近22-31场比赛数据
			start+=10;
			PlayerAvgRate otherSeason5 = nbaDataDAO.getPlayerAvgOther(playerId, seasonId, gameIDs,start,10);//最近32-41场比赛数据
			start+=10;
			PlayerAvgRate otherSeason6 = nbaDataDAO.getPlayerAvgOther(playerId, seasonId, gameIDs,start,9999);//本赛季其他场比赛数据
			start+=9999;
			
			if (playerId == 3908809) {
				logger.info("seasonId={},randTime={}", seasonId,randTime);
				logger.info("1={}", recentAvg.toString());
				logger.info("2-11={}", otherSeason2.toString());
				logger.info("12-21={}", otherSeason3.toString());
				logger.info("22-31={}", otherSeason4.toString());
				logger.info("32-41={}", otherSeason5.toString());
				logger.info("剩余={}", otherSeason6.toString());
			}
			
			/*
			 * 根据这个球员的过去和现在的真实比赛数据拆分出七部分按不同的权重算出来一个综合数据去参与运算能力值，按能力值高低再排出身价，七部分分别是：
			 * 最近1场比赛数据
			 * 最近2-11场比赛数据
			 * 最近12-21场比赛数据
			 * 最近22-31场比赛数据
			 * 最近32-41场比赛数据
			 * 本赛季其他场比赛数据
			 * 上个赛季平均数据
			 */
			thisPlayCount = (recentAvg==null?0:recentAvg.getPlayCount())
					+ (otherSeason2==null?0:otherSeason2.getPlayCount())
					+ (otherSeason3==null?0:otherSeason3.getPlayCount())
					+ (otherSeason4==null?0:otherSeason4.getPlayCount())
					+ (otherSeason5==null?0:otherSeason5.getPlayCount())
					+ (otherSeason6==null?0:otherSeason6.getPlayCount())
					// 不算上个赛季?
					;
			
			
			/** 计算 **/
			int fgm = getValue(getFgm(lastSeason), getFgm(otherSeason2),
					getFgm(recentAvg),getFgm(otherSeason3),getFgm(otherSeason4),getFgm(otherSeason5),getFgm(otherSeason6), lastPlayCount, thisPlayCount, playerId);
			int ftm = getValue(getFtm(lastSeason), getFtm(otherSeason2),
					getFtm(recentAvg),getFtm(otherSeason3),getFtm(otherSeason4),getFtm(otherSeason5),getFtm(otherSeason6),  lastPlayCount, thisPlayCount, playerId);
			int pts = getValue(getPts(lastSeason), getPts(otherSeason2),
					getPts(recentAvg),getPts(otherSeason3),getPts(otherSeason4),getPts(otherSeason5),getPts(otherSeason6),  lastPlayCount, thisPlayCount, playerId);
//			int pts1 = getValue(getPts1(lastSeason), getPts1(otherSeason2),
//					getPts1(recentAvg),getPts1(otherSeason3),getPts1(otherSeason4),getPts1(otherSeason5),getPts1(otherSeason6),  beforePlayCount, thisPlayCount, playerId);
//			int pts2 = getValue(getPts2(lastSeason), getPts2(otherSeason2),
//					getPts2(recentAvg),getPts2(otherSeason3),getPts2(otherSeason4),getPts2(otherSeason5),getPts2(otherSeason6),  beforePlayCount, thisPlayCount, playerId);
//			int pts3 = getValue(getPts3(lastSeason), getPts3(otherSeason2),
//					getPts3(recentAvg),getPts3(otherSeason3),getPts3(otherSeason4),getPts3(otherSeason5),getPts3(otherSeason6),  beforePlayCount, thisPlayCount, playerId);
			int threePm = getValue(getThreePm(lastSeason),
					getThreePm(otherSeason2), getThreePm(recentAvg),getThreePm(otherSeason3),getThreePm(otherSeason4),getThreePm(otherSeason5),getThreePm(otherSeason6),  
					lastPlayCount, thisPlayCount, playerId);
			int ast = getValue(getAst(lastSeason), getAst(otherSeason2),
					getAst(recentAvg),getAst(otherSeason3),getAst(otherSeason4),getAst(otherSeason5),getAst(otherSeason6),  lastPlayCount, thisPlayCount, playerId);
			int oreb = getValue(getOreb(lastSeason), getOreb(otherSeason2),
					getOreb(recentAvg),getOreb(otherSeason3),getOreb(otherSeason4),getOreb(otherSeason5),getOreb(otherSeason6),  lastPlayCount, thisPlayCount,
					playerId);
			int dreb = getValue(getDreb(lastSeason), getDreb(otherSeason2),
					getDreb(recentAvg),getDreb(otherSeason3),getDreb(otherSeason4),getDreb(otherSeason5),getDreb(otherSeason6),  lastPlayCount, thisPlayCount,
					playerId);
			int stl = getValue(getStl(lastSeason), getStl(otherSeason2),
					getStl(recentAvg),getStl(otherSeason3),getStl(otherSeason4),getStl(otherSeason5),getStl(otherSeason6),  lastPlayCount, thisPlayCount, playerId);
			int blk = getValue(getBlk(lastSeason), getBlk(otherSeason2),
					getBlk(recentAvg),getBlk(otherSeason3),getBlk(otherSeason4),getBlk(otherSeason5),getBlk(otherSeason6),  lastPlayCount, thisPlayCount, playerId);
			int to = getValue(getTo(lastSeason), getTo(otherSeason2),
					getTo(recentAvg),getTo(otherSeason3),getTo(otherSeason4),getTo(otherSeason5),getTo(otherSeason6),  lastPlayCount, thisPlayCount, playerId);
			int pf = getValue(getPf(lastSeason), getPf(otherSeason2),
					getPf(recentAvg),getPf(otherSeason3),getPf(otherSeason4),getPf(otherSeason5),getPf(otherSeason6),  lastPlayCount, thisPlayCount, playerId);
			int min = getValue(getMin(lastSeason), getMin(otherSeason2),
					getMin(recentAvg),getMin(otherSeason3),getMin(otherSeason4),getMin(otherSeason5),getMin(otherSeason6),  lastPlayCount, thisPlayCount, playerId);

			int attrCap = (int) (pts * 1.5f + ftm * 0.2f + fgm * 0.3f + threePm * 0.25f + oreb * 0.4f + ast * 0.6f - to * 0.7f - pf * 0.2f + min *0f);//进攻和
			int guaCap = (int) (dreb * 0.3f + stl * 0.8f + blk * 0.6f - to * 0f -pf * 0f + min *0.2f);//防守和
//			attrCap = (int) (pts1 * 1f + pts2 *1.2f + pts3 * 1f + ftm * 0.4f + fgm * 0.4f 
//					+ threePm * 0.4f + oreb * 0.8f + ast * 0.8f - to * 0.5f - pf * 0.1f + min *0.4f);
//			int guaCap = (int)(dreb * 2f + stl * 2f + blk * 2f - to * 0.5f -pf * 0.1f + min *0.4f);
//			int attrCap = (int) (pts * 1.2 + fgm * 0.2 + ftm * 0.5 + threePm
//					* 0.8 + ast * 0.8 + (oreb + dreb) * 0.3 + min * 0.5 - to
//					* 0.5 - pf * 0.15) - 45;
//			int guaCap = (int) ((oreb + dreb) * 0.7 + stl * 1.3 + blk + min
//					* 0.5 - to * 0.5 - pf * 0.15) - 25;

			if (attrCap < 20)
				attrCap = 20;
			if (guaCap < 20)
				guaCap = 20;

			PlayerAbi cap = new PlayerAbi();//data_player_cap表
			cap.setPlayerId(playerId);
			cap.setFgm(fgm);
			cap.setFtm(ftm);
			cap.setPts(pts);
			cap.setThreePm(threePm);
			cap.setAst(ast);
			cap.setOreb(oreb);
			cap.setDreb(dreb);
			cap.setStl(stl);
			cap.setBlk(blk);
			cap.setTo(to);
			cap.setPf(pf);
			cap.setMin(min);
			cap.setAttrAbi(attrCap);
			cap.setGuaAbi(guaCap);
			cap.setAbi(attrCap + guaCap);//工资帽
			// 更新攻防 data_player_cap表
			nbaDataDAO.updateCap(cap);
			attrMap.put(playerId, attrCap);
			if (playerId == 3908809) {
				logger.info("最终值：{}",cap.toString());
			}
		}

		calPlayerGrade(attrMap);
	}

	// 任意球员，AVG数据取CAP
	public PlayerAbi getPlayerAbi(PlayerAvgRate avg) {
		int playerId = avg.getPlayerId();
		PlayerAbi cap = new PlayerAbi();
		PlayerAvgRate lastSeason = avg;
		PlayerAvgRate thisSeason = avg;
		PlayerAvgRate recentAvg = avg;
		// logger.info("=="+avg);

//		int thisPlayCount = thisSeason.getPlayCount(), beforePlayCount = lastSeason
//				.getPlayCount();
//		/** 计算 **/
//		int fgm = getValue(getFgm(lastSeason), getFgm(thisSeason),
//				getFgm(recentAvg), beforePlayCount, thisPlayCount, playerId);
//		int ftm = getValue(getFtm(lastSeason), getFtm(thisSeason),
//				getFtm(recentAvg), beforePlayCount, thisPlayCount, playerId);
//		int pts = getValue(getPts(lastSeason), getPts(thisSeason),
//				getPts(recentAvg), beforePlayCount, thisPlayCount, playerId);
//		int threePm = getValue(getThreePm(lastSeason), getThreePm(thisSeason),
//				getThreePm(recentAvg), beforePlayCount, thisPlayCount, playerId);
//		int ast = getValue(getAst(lastSeason), getAst(thisSeason),
//				getAst(recentAvg), beforePlayCount, thisPlayCount, playerId);
//		int oreb = getValue(getOreb(lastSeason), getOreb(thisSeason),
//				getOreb(recentAvg), beforePlayCount, thisPlayCount, playerId);
//		int dreb = getValue(getDreb(lastSeason), getDreb(thisSeason),
//				getDreb(recentAvg), beforePlayCount, thisPlayCount, playerId);
//		int stl = getValue(getStl(lastSeason), getStl(thisSeason),
//				getStl(recentAvg), beforePlayCount, thisPlayCount, playerId);
//		int blk = getValue(getBlk(lastSeason), getBlk(thisSeason),
//				getBlk(recentAvg), beforePlayCount, thisPlayCount, playerId);
//		int to = getValue(getTo(lastSeason), getTo(thisSeason),
//				getTo(recentAvg), beforePlayCount, thisPlayCount, playerId);
//		int pf = getValue(getPf(lastSeason), getPf(thisSeason),
//				getPf(recentAvg), beforePlayCount, thisPlayCount, playerId);
//		int min = getValue(getMin(lastSeason), getMin(thisSeason),
//				getMin(recentAvg), beforePlayCount, thisPlayCount, playerId);
//
//		int attrCap = (int) (pts * 1.2 + fgm * 0.2 + ftm * 0.5 + threePm * 0.5
//				+ ast * 0.8 + (oreb + dreb) * 0.3 + min * 0.5 - to * 0.5 - pf * 0.15) - 45;
//		int guaCap = (int) ((oreb + dreb) * 0.7 + stl * 1.3 + blk + min * 0.5
//				- to * 0.5 - pf * 0.15) - 25;
//
//		if (attrCap < 20)
//			attrCap = 20;
//		if (guaCap < 20)
//			guaCap = 20;
//
//		cap.setPlayerId(avg.getPlayerId());
//		cap.setFgm(fgm);
//		cap.setFtm(ftm);
//		cap.setPts(pts);
//		cap.setThreePm(threePm);
//		cap.setAst(ast);
//		cap.setOreb(oreb);
//		cap.setDreb(dreb);
//		cap.setStl(stl);
//		cap.setBlk(blk);
//		cap.setTo(to);
//		cap.setPf(pf);
//		cap.setMin(min);
//		cap.setAttrAbi(attrCap);
//		cap.setGuaAbi(guaCap);
//		cap.setAbi(attrCap + guaCap);

		return cap;
	}

	/**
	 * 
	 * @param lastSeason 		上赛季平均数据
	 * @param thisSeason 		本赛季2~10场平均数据
	 * @param recent	 		最近1场比赛数据
	 * @param otherSeason		最近12-21场比赛数据
	 * @param otherSeason2		最近22~31场平均数据
	 * @param otherSeason3		最近32~41场平均数据
	 * @param otherSeason4		本赛季其他场平均数据
	 * @param lastPlayCount		上赛季打比赛场数
	 * @param thisPlayCount		根据球员计算出的7部分打的比赛所有场数之和
	 * @param playerId
	 * @return
	 */
	private int getValue(float lastSeason, float thisSeason, float recent,float otherSeason,float otherSeason2
			,float otherSeason3,float otherSeason4,int lastPlayCount, int thisPlayCount, int playerId) {
		// if(playerId==3907387){
		// System.out.println("#1###########################"+beforeSeason+","+thisSeason+","+recent+","+beforePlayCount+","+thisPlayCount);
		// }
		// 填充数据
		if (lastPlayCount == 0 && lastSeason == 0 && thisSeason > 0) {// 上赛季无数据,本赛季有数据
			lastSeason = thisSeason;
		}
		if (lastSeason > 0 && thisSeason == 0) {
			thisSeason = lastSeason;
		}

		// if(playerId==3907387){
		// System.out.println("#2###########################"+beforeSeason+","+thisSeason+","+recent+","+beforePlayCount+","+thisPlayCount);
		// }
//
//		double before = PlayerAbi.OVERALL_WEIGHT[0] - 0.01 * thisPlayCount;// 0.85
//																			// 0.74-0.01
//		double thisse = PlayerAbi.OVERALL_WEIGHT[1] + 0.01 * thisPlayCount;// 0.05
		// if(before<0.2)before=0.2f;
		// if(thisse>0.7)thisse=0.7f;
		double OVERALL_WEIGHT[] = PlayerAbi.getOverAllWeight(thisPlayCount);
		return (int)(recent * OVERALL_WEIGHT[0] + thisSeason * OVERALL_WEIGHT[1] 
				+ otherSeason * OVERALL_WEIGHT[2] + otherSeason2 * OVERALL_WEIGHT[3] + 
				otherSeason3 * OVERALL_WEIGHT[4] + otherSeason4 * OVERALL_WEIGHT[5] + 
				lastSeason * OVERALL_WEIGHT[6]);
//		return (int) (beforeSeason * before + thisSeason * thisse + recent
//				* PlayerAbi.OVERALL_WEIGHT[2] + otherSeason * PlayerAbi.OVERALL_WEIGHT[3]);
	}

	private float getCheckResult(float src) {
		if (src > 200)
			return 200;
		else if (src < 0)
			return 0;
		else
			return src;
	}

	private float getPts(PlayerAvgRate recentAvg) {
		if (recentAvg == null)
			return 0;
		float pts = (float) (recentAvg.getPts() * PlayerAbi.V_PTS[0]);
		return getCheckResult(pts);
	}
	private float getPts1(PlayerAvgRate recentAvg) {
		if (recentAvg == null)
			return 0;
		float pts = (float) (recentAvg.getFtm()* PlayerAbi.V_PTS1[0]);
		return getCheckResult(pts);
	}
	private float getPts2(PlayerAvgRate recentAvg) {
		if (recentAvg == null)
			return 0;
		float pts = (float) ((recentAvg.getFgm() - recentAvg.getThreePa() )* 2 * PlayerAbi.V_PTS2[0]);
		return getCheckResult(pts);
	}
	private float getPts3(PlayerAvgRate recentAvg) {
		if (recentAvg == null)
			return 0;
		float pts = (float) (recentAvg.getThreePm() * 3 * PlayerAbi.V_PTS3[0]);
		return getCheckResult(pts);
	}
//	private float getPts(PlayerAvgRate recentAvg) {
//		if (recentAvg == null)
//			return 0;
//		float pts = (float) ((recentAvg.getPts() - recentAvg.getThreePm() * 3)
//				* PlayerAbi.V_PTS[0] + PlayerAbi.V_PTS[1]);
//		return getCheckResult(pts);
//	}

	private float getThreePm(PlayerAvgRate recentAvg) {
		if (recentAvg == null || recentAvg.getThreePa()==0)
			return 0;
		
		float threePm = (float) (recentAvg.getThreePm()/recentAvg.getThreePa()*PlayerAbi.V_THREE_PM[0]);
		return getCheckResult(threePm);
	}
//	private float getThreePm(PlayerAvgRate recentAvg) {
//		if (recentAvg == null)
//			return 0;
//		float threePm = (float) (recentAvg.getThreePm()
//				* PlayerAbi.V_THREE_PM[0] + PlayerAbi.V_THREE_PM[1]);
//		return getCheckResult(threePm);
//	}

	private float getOreb(PlayerAvgRate recentAvg) {
		if (recentAvg == null)
			return 0;
		float oReb = (float) (recentAvg.getOreb() * PlayerAbi.V_REB[0]);
		return getCheckResult(oReb);
	}
//	private float getOreb(PlayerAvgRate recentAvg) {
//		if (recentAvg == null)
//			return 0;
//		float oReb = (float) (recentAvg.getOreb() * PlayerAbi.V_REB[0] + PlayerAbi.V_REB[1]);
//		return getCheckResult(oReb);
//	}

	private float getDreb(PlayerAvgRate recentAvg) {
		if (recentAvg == null)
			return 0;
		float dReb = (float) (recentAvg.getDreb() * PlayerAbi.V_DREB[0]);
		return getCheckResult(dReb);
	}
//	private float getDreb(PlayerAvgRate recentAvg) {
//		if (recentAvg == null)
//			return 0;
//		float dReb = (float) (recentAvg.getDreb() * PlayerAbi.V_REB[0] + PlayerAbi.V_REB[1]);
//		return getCheckResult(dReb);
//	}

	private float getAst(PlayerAvgRate recentAvg) {
		if (recentAvg == null)
			return 0;
		float ast = (float) (recentAvg.getAst() * PlayerAbi.V_AST[0]);
		return getCheckResult(ast);
	}
//	private float getAst(PlayerAvgRate recentAvg) {
//		if (recentAvg == null)
//			return 0;
//		float ast = (float) (recentAvg.getAst() * PlayerAbi.V_AST[0] + PlayerAbi.V_AST[1]);
//		return getCheckResult(ast);
//	}

	private float getStl(PlayerAvgRate recentAvg) {
		if (recentAvg == null)
			return 0;
		float stl = (float) (recentAvg.getStl() * PlayerAbi.V_STL[0]);
		return getCheckResult(stl);
	}
//	private float getStl(PlayerAvgRate recentAvg) {
//		if (recentAvg == null)
//			return 0;
//		float stl = (float) (recentAvg.getStl() * PlayerAbi.V_STL[0] + PlayerAbi.V_STL[1]);
//		return getCheckResult(stl);
//	}

	private float getBlk(PlayerAvgRate recentAvg) {
		if (recentAvg == null)
			return 0;
		float blk = (float) (recentAvg.getBlk() * PlayerAbi.V_BLK[0]);
		return getCheckResult(blk);
	}
//	private float getBlk(PlayerAvgRate recentAvg) {
//		if (recentAvg == null)
//			return 0;
//		float blk = (float) (recentAvg.getBlk() * PlayerAbi.V_BLK[0] + PlayerAbi.V_BLK[1]);
//		return getCheckResult(blk);
//	}

	private float getTo(PlayerAvgRate recentAvg) {
		if (recentAvg == null)
			return 0;
		float to = (float) (recentAvg.getTo() * PlayerAbi.V_TO[0]);
		return getCheckResult(to);
	}
//	private float getTo(PlayerAvgRate recentAvg) {
//		if (recentAvg == null)
//			return 0;
//		float to = (float) (recentAvg.getTo() * PlayerAbi.V_TO[0] + PlayerAbi.V_TO[1]);
//		return getCheckResult(to);
//	}

	private float getMin(PlayerAvgRate recentAvg) {
		if (recentAvg == null)
			return 0;
		float min = (float) (recentAvg.getMin() * PlayerAbi.V_MIN[0]);
		return getCheckResult(min);
	}
//	private float getMin(PlayerAvgRate recentAvg) {
//		if (recentAvg == null)
//			return 0;
//		float min = (float) (recentAvg.getMin() * PlayerAbi.V_MIN[0] + PlayerAbi.V_MIN[1]);
//		return getCheckResult(min);
//	}

	private float getPf(PlayerAvgRate recentAvg) {
		if (recentAvg == null)
			return 0;
		float pf = (float) (recentAvg.getPf() * PlayerAbi.V_PF[0]);
		return getCheckResult(pf);
	}
//	private float getPf(PlayerAvgRate recentAvg) {
//		if (recentAvg == null)
//			return 0;
//		float pf = (float) (recentAvg.getPf() * PlayerAbi.V_TO[0] + PlayerAbi.V_TO[1]);
//		return getCheckResult(pf);
//	}

	private float getFgm(PlayerAvgRate recentAvg) {
		if (recentAvg == null)
			return 0;
		float avg = 0;
		if (recentAvg.getFga() == 0 || recentAvg.getFga()-recentAvg.getThreePa()==0)
			avg = 0;
		else
			avg = (recentAvg.getFgm()-recentAvg.getThreePm()) / (recentAvg.getFga()-recentAvg.getThreePa());
		float fgm = (float) (avg * PlayerAbi.V_FGM[0]);
		return getCheckResult(fgm);
	}
//	private float getFgm(PlayerAvgRate recentAvg) {
//		if (recentAvg == null)
//			return 0;
//		float avg = 0;
//		if (recentAvg.getFga() == 0)
//			avg = 0;
//		else
//			avg = recentAvg.getFgm() / recentAvg.getFga();
//		float fgm = (float) ((avg - PlayerAbi.V_FGM[0]) * PlayerAbi.V_FGM[1] + 60);
//		return getCheckResult(fgm);
//	}

	private float getFtm(PlayerAvgRate recentAvg) {
		if (recentAvg == null)
			return 0;
		float avg = 0;
		if (recentAvg.getFta() == 0)
			avg = 0;
		else
			avg = recentAvg.getFtm() / recentAvg.getFta();
		float ftm = (float)(avg * PlayerAbi.V_FTM[0]);
		return getCheckResult(ftm);
	}
//	private float getFtm(PlayerAvgRate recentAvg) {
//		if (recentAvg == null)
//			return 0;
//		float avg = 0;
//		if (recentAvg.getFta() == 0)
//			avg = 0;
//		else
//			avg = recentAvg.getFtm() / recentAvg.getFta();
//		float ftm = (float) ((avg - PlayerAbi.V_FTM[0]) * PlayerAbi.V_FTM[1] + 60);
//		return getCheckResult(ftm);
//	}

	/**
	 * 根据球员的attrCap(进攻和)计算出球员的市场价格和等级，并且更新到player_info表
	 * @param attrMap
	 */
	public void calPlayerGrade(Map<Integer, Integer> attrMap) {
		List<PlayerPricePolicy> rules = new ArrayList<PlayerPricePolicy>();
		rules.add(new PlayerPricePolicy("S+", 15));
		rules.add(new PlayerPricePolicy("S", 35));
		rules.add(new PlayerPricePolicy("S-", 60));
		rules.add(new PlayerPricePolicy("A+", 85));
		rules.add(new PlayerPricePolicy("A", 115));
		rules.add(new PlayerPricePolicy("A-", 150));
		rules.add(new PlayerPricePolicy("B+", 185));
		rules.add(new PlayerPricePolicy("B", 225));
		rules.add(new PlayerPricePolicy("B-", 270));
		rules.add(new PlayerPricePolicy("C+", 300));
		rules.add(new PlayerPricePolicy("C", 330));
		rules.add(new PlayerPricePolicy("C-", 360));
		// rules.add(new PlayerPricePolicy("D+",390));
		// rules.add(new PlayerPricePolicy("D",420));
		rules.add(new PlayerPricePolicy("D+", 380));
		rules.add(new PlayerPricePolicy("D", 400));
		rules.add(new PlayerPricePolicy("D-", 10000));

		List<PlayerAbi> playerList = nbaDataDAO.getPlayerOrderByCap();//根据查询所有球员的cap值，按降序排序
		Map<Integer, NBAPlayerDetail> playerMap = getPlayersMap();//查询出表player_info中所有的球员数据
		int index = 0;
		int attr = 0;
		for (PlayerAbi player : playerList) {
			index++;
			PlayerPricePolicy rule = getRule(rules, index);//index表示球员的排名，循环第一个球员就是排名是1，依次下去，返回球员的等级
			String grade = rule.getGrade();
			double marketPrice = 0;
			if (index >= 1 && index <= 15) {
				marketPrice = 2500 - (index - 1) * 30;
			} else if (index >= 16 && index <= 35) {
				marketPrice = 2050 - (index - 16) * 20;
			} else if (index >= 36 && index <= 60) {
				marketPrice = 1650 - (index - 36) * 10;
			} else if (index >= 61 && index <= 150) {
				marketPrice = 1400 - (index - 61) * 5;
			} else {
				marketPrice = 950 - (index - 151) * 3;
			}
			if (marketPrice < 50)
				marketPrice = 50;
			NBAPlayerDetail info = playerMap.get(player.getPlayerId());
			attr = 0;
			if (attrMap.containsKey(player.getPlayerId()))
				attr = attrMap.get(player.getPlayerId());
			nbaDataDAO.updatePlayerPrice(info.getPlayerId(), grade,
					(int) marketPrice, info.getPrice(), player.getAbi(),
					info.getCap(), attr);
		}
	}

	/*
	 * public void calPlayerGrade(){ List<PlayerRule>
	 * rules=nbaDataDAO.getPlayerRule(); List<PlayerAbi>
	 * playerList=nbaDataDAO.getPlayerOrderByCap(); Map<Integer,NBAPlayer>
	 * playerMap=getPlayersMap(); int index=0; int notD=0; for(PlayerAbi
	 * player:playerList){ PlayerRule rule=getRule(rules,index); String
	 * grade=rule.getGrade(); if(grade.equals("D-")){
	 * rule.setNum(playerList.size()-notD); rule.setTop(playerList.size());
	 * }else{ notD++; } double minMarketPrice=rule.getMinMarketPrice(); double
	 * maxMarketPrice=rule.getMaxMarketPrice(); double num=rule.getNum(); double
	 * marketPrice
	 * =minMarketPrice+(maxMarketPrice-minMarketPrice)/num*(rule.getTop
	 * ()-index); index++; NBAPlayer info=playerMap.get(player.getPlayerId());
	 * nbaDataDAO.updatePlayerPrice(info.getPlayerId(),
	 * grade,(int)marketPrice,info.getPrice()); }
	 * 
	 * }
	 */
	private PlayerPricePolicy getRule(List<PlayerPricePolicy> rules, int index) {
		for (PlayerPricePolicy rule : rules) {
			if (index <= rule.getTop()) {
				return rule;
			}
		}
		return null;
	}

	private void getRecentAvg(List<PlayerAvgRate> list, PlayerAvgRate recentAvg) {
		for (PlayerAvgRate avg : list) {
			recentAvg.setStarterCount(recentAvg.getStarterCount()
					+ avg.getStarterCount());
			recentAvg.setFga(recentAvg.getFga() + avg.getFga());
			recentAvg.setFgm(recentAvg.getFgm() + avg.getFgm());
			recentAvg.setFtm(recentAvg.getFtm() + avg.getFtm());
			recentAvg.setFta(recentAvg.getFta() + avg.getFta());
			recentAvg.setPts(recentAvg.getPts() + avg.getPts());
			recentAvg.setThreePm(recentAvg.getThreePm() + avg.getThreePm());
			recentAvg.setThreePa(recentAvg.getThreePa() + avg.getThreePa());
			recentAvg.setOreb(recentAvg.getOreb() + avg.getOreb());
			recentAvg.setDreb(recentAvg.getDreb() + avg.getDreb());
			recentAvg.setAst(recentAvg.getAst() + avg.getAst());
			recentAvg.setStl(recentAvg.getStl() + avg.getStl());
			recentAvg.setBlk(recentAvg.getBlk() + avg.getBlk());
			recentAvg.setTo(recentAvg.getTo() + avg.getTo());
			recentAvg.setPf(recentAvg.getPf() + avg.getPf());
			recentAvg.setMin(recentAvg.getMin() + avg.getMin());
		}
		if (list.size() > 0) {
			float divisor = list.size();
			recentAvg.setFga(recentAvg.getFga() / divisor);
			recentAvg.setFgm(recentAvg.getFgm() / divisor);
			recentAvg.setFtm(recentAvg.getFtm() / divisor);
			recentAvg.setFta(recentAvg.getFta() / divisor);
			recentAvg.setPts(recentAvg.getPts() / divisor);
			recentAvg.setThreePm(recentAvg.getThreePm() / divisor);
			recentAvg.setThreePa(recentAvg.getThreePa() / divisor);
			recentAvg.setOreb(recentAvg.getOreb() / divisor);
			recentAvg.setDreb(recentAvg.getDreb() / divisor);
			recentAvg.setAst(recentAvg.getAst() / divisor);
			recentAvg.setStl(recentAvg.getStl() / divisor);
			recentAvg.setBlk(recentAvg.getBlk() / divisor);
			recentAvg.setTo(recentAvg.getTo() / divisor);
			recentAvg.setPf(recentAvg.getPf() / divisor);
			recentAvg.setMin(recentAvg.getMin() / divisor);
		}
	}

	public void updatePlayer(NBAPlayerDetail player) {
		nbaDataDAO.updatePlayer(player);
	}

	public Map<Integer, NBAPlayerDetail> getPlayersMap() {
		Map<Integer, NBAPlayerDetail> playerMap = new HashMap<Integer, NBAPlayerDetail>();
		List<NBAPlayerDetail> list = nbaDataDAO.getPlayers();//查询所有球员的player_info
		for (NBAPlayerDetail player : list) {
			playerMap.put(player.getPlayerId(), player);
		}
		return playerMap;
	}

	/**
	 * 保护新秀, 特殊映射数值来填充？
	 * @return
	 */
	private Map<Integer, Integer> getNMap() {
		Map<Integer, Integer> NList = new HashMap<Integer, Integer>();
		// 2017保护新秀
		NList.put(4066636, 3438);
		NList.put(4066421, 4299);
		NList.put(4065648, 3194);
		NList.put(4066297, 2386);
		NList.put(4066259, 3973);
		NList.put(4065654, 1017);
		NList.put(4066336, 6459);
		NList.put(4230547, 2528426);
		NList.put(4065697, 6443);
		NList.put(4066650, 4260);
		NList.put(4066262, 2528210);
		NList.put(3913174, 3988);
		NList.put(3908809, 4239);
		NList.put(4066261, 6615);
		NList.put(3138156, 2528353);
		NList.put(3912332, 2991282);
		NList.put(3136491, 3412);
		NList.put(4066425, 2327577);
		NList.put(3908845, 6452);
		NList.put(4065649, 2774);
		NList.put(4230546, 2284101);
		NList.put(4066328, 3444);
		NList.put(3934719, 3190);
		NList.put(3934662, 2011);
		NList.put(3893059, 6590);
		NList.put(3906671, 2531367);
		NList.put(3134907, 3974);
		NList.put(4065673, 3936296);
		NList.put(3078576, 2991139);
		NList.put(3062679, 2982340);
		NList.put(4065651, 3136477);
		NList.put(3059262, 2993875);
		NList.put(3074797, 2999549);
		NList.put(3059315, 3231);
		NList.put(3917378, 4011991);
		NList.put(3155533, 6485);
		NList.put(3056602, 3998);
		NList.put(3064427, 3064517);
		NList.put(3912854, 2382);
		NList.put(3907821, 3457);
		NList.put(3915560, 2995702);
		NList.put(3934723, 3232);
		NList.put(4222252, 3155942);
		NList.put(2991149, 3191);
		NList.put(3155526, 2531364);
		NList.put(3057187, 3893019);
		NList.put(4066424, 2333797);
		NList.put(3064539, 2528794);
		NList.put(4230557, 6426);
		NList.put(4017851, 4250);
		NList.put(3059310, 3064230);
		NList.put(3136485, 3102528);
		NList.put(3134880, 6453);
		NList.put(3062897, 2990969);
		NList.put(3064457, 3192);
		NList.put(3064308, 6569);
		NList.put(3893014, 2566747);
		NList.put(4017857, 3062892);
		NList.put(2982240, 6469);
		NList.put(3893020, 2184);

		/*
		 * //2016保护新秀 NList.put(3907387,6462);//本-西蒙斯 NList.put(3913176,6466);
		 * NList.put(3917376,4239);//杰伦-布朗 NList.put(4011991,2774);
		 * NList.put(2991139,3423); NList.put(2990984,2386);
		 * NList.put(3936299,2768); NList.put(3907487,3064290);
		 * NList.put(3134908,6427); NList.put(4017843,6624);
		 * NList.put(3155942,6474);//多曼塔斯-萨博尼斯 NList.put(2990962,6601);
		 * NList.put(4017846,2991235); NList.put(2999549,2444);
		 * NList.put(4017839,3209); NList.put(4017844,2767);
		 * NList.put(3136696,4299); NList.put(3906522,4259);
		 * NList.put(3907822,3135047); NList.put(2991043,3417);
		 * NList.put(3062667,3064291); NList.put(3934663,3024);
		 * NList.put(4017838,4267); NList.put(3893019,2778);
		 * NList.put(2982330,2745); NList.put(3929325,1708);
		 * NList.put(3149673,3994); NList.put(3936296,2433);
		 * NList.put(3907497,3235); NList.put(3064559,6464);
		 * NList.put(3937101,2772); NList.put(4017837,2333797);
		 * NList.put(3919335,6616); NList.put(3136194,4303);
		 * NList.put(3893060,25); NList.put(2566769,3135046);
		 * NList.put(3147366,3232); NList.put(3137730,2993874);
		 * NList.put(4017875,4000); NList.put(3924880,2754);
		 * NList.put(3936102,2959753); NList.put(3136477,2178);
		 * NList.put(3892894,6620); NList.put(4017845,136);
		 * NList.put(3059273,2968361); NList.put(2991178,2528393);
		 * NList.put(2982268,1006); NList.put(3893023,4250);
		 * NList.put(2566747,3457); NList.put(2990969,91);
		 * NList.put(3136469,3058254); NList.put(2983551,3452);
		 * NList.put(3899009,3462); NList.put(3062892,2528386);
		 * NList.put(2982329,557); NList.put(3147351,3474);
		 * NList.put(4017850,2581084); NList.put(2595435,2595211);
		 * NList.put(2990983,2991039); NList.put(3002137,6641);
		 * 
		 * //15 //NList.put(3136195,6477);//卡尔-安东尼-唐斯 NList.put(3136776,3423);
		 * NList.put(3135048,3436); NList.put(3064517,2579258);//贾雷尔-马丁
		 * NList.put(3135047,4239); //14 //NList.put(3059319,3209);//安德鲁-威金斯
		 * NList.put(3056600,1708); NList.put(3059318,6459);//乔尔-恩比德
		 * NList.put(3032978,6609); NList.put(2528588,3988);
		 * NList.put(3064511,2490589);
		 */

		// 2015保护新秀
		/*
		 * NList.put(3136195,6477);// NList.put(3136776,3423);//
		 * NList.put(3135048,3436);// NList.put(3102531,4264);
		 * NList.put(2995706,6597); NList.put(2991282,3994);
		 * NList.put(3892818,3456); NList.put(3134881,3056600);
		 * NList.put(2579294,3414); NList.put(3135047,4239);//
		 * NList.put(3133628,3412); NList.put(3136196,6592);
		 * NList.put(3136193,6429); NList.put(3064230,4299);
		 * NList.put(3133603,2488958); NList.put(3074752,2596158);
		 * NList.put(3137733,3445); NList.put(2991184,3243);
		 * NList.put(2531047,3277); NList.put(3064447,3064440);
		 * NList.put(2982340,4003); NList.put(3064482,2745);
		 * NList.put(3064291,6619); NList.put(3135046,3032979);
		 * NList.put(3064517,2579258);// NList.put(3892893,6615);
		 * NList.put(2580365,6424); NList.put(2983727,2761);
		 * NList.put(3153165,4270); NList.put(3155535,4400);
		 * NList.put(3893016,6469); NList.put(2991055,2991473);
		 * NList.put(3064520,3078284); NList.put(2531362,2993875);
		 * NList.put(2999409,3444); NList.put(2596110,2774);
		 * NList.put(2993370,6485); NList.put(2578259,6579);
		 * NList.put(3899008,3239); NList.put(2581190,6603);
		 * NList.put(2578239,2999547); NList.put(2982192,2490149);
		 * NList.put(2528386,3231); NList.put(3064511,2490589);
		 * NList.put(2579458,2527968); NList.put(2595516,3102528);
		 * NList.put(3893015,6605); NList.put(3064516,3004);
		 * NList.put(2595175,4250); NList.put(3904015,2991039);
		 * NList.put(2578269,2284101); NList.put(3902067,6587);
		 * NList.put(2608899,2991281); NList.put(3033042,2991042);
		 * NList.put(2608716,2333797); NList.put(2595211,2594818);
		 * NList.put(3902065,2531038); NList.put(2982331,6594);
		 * NList.put(3899663,4261); NList.put(3899662,6598); /*
		 */
		// 2014保护新秀
		/*
		 * NList.put(3059319,3209); NList.put(3056600,1708);
		 * NList.put(3059318,2754); NList.put(3064290,6624);
		 * NList.put(3102528,3993); NList.put(2990992,1981);
		 * NList.put(3064514,2767); NList.put(2991042,1007);
		 * NList.put(3078284,3242); NList.put(2583639,3964);
		 * NList.put(2528588,3988); NList.put(3032978,6609);
		 * NList.put(3064440,2768); NList.put(2982334,4239);
		 * NList.put(2531100,3974); NList.put(3102530,991);
		 * NList.put(3064509,2799); NList.put(3059281,2782);
		 * NList.put(2999547,4242); NList.put(3113297,1705);
		 * NList.put(2991041,3462); NList.put(2993873,6630);
		 * NList.put(2581177,3190); NList.put(2530780,2382);
		 * NList.put(3102529,4298); NList.put(2594816,4243);
		 * NList.put(3037789,4243); NList.put(2488999,3968);
		 * NList.put(2531364,4400); NList.put(2993874,3413);
		 * NList.put(3102534,1726); NList.put(2566741,976);
		 * NList.put(2528794,2992); NList.put(2991227,4165);
		 * NList.put(2614962,3217); NList.put(2581084,6546);
		 * NList.put(2578232,558); NList.put(2580782,4300);
		 * NList.put(2991070,2760); NList.put(2991039,3422);
		 * NList.put(3112335,2774); NList.put(2580589,2448);
		 * NList.put(3033031,2834); NList.put(2530750,6588);
		 * NList.put(2531367,2792); NList.put(2528426,63);
		 * NList.put(2531038,2166); NList.put(2488721,6637);
		 * NList.put(2528354,2184); NList.put(2528479,6464);
		 * NList.put(3102533,3998); NList.put(3102532,3979);
		 * NList.put(2995725,6594); NList.put(3113298,4023);
		 * NList.put(2983526,2866); NList.put(2527968,6604);
		 * NList.put(3037792,3041); NList.put(2531797,6633);
		 * NList.put(2489006,4303); NList.put(2488845,6439);
		 * NList.put(2991280,6427);//纳伦斯-诺尔 /*
		 */
		// 2013保护新秀
		/*
		 * NList.put(2991473,1713); NList.put(2527963,2367);
		 * NList.put(2594922,2423); NList.put(2579258,3200);
		 * NList.put(2596107,3442); NList.put(2991280,6427);
		 * NList.put(2578213,4249); NList.put(2581018,4239);
		 * NList.put(2579260,2782); NList.put(2490149,3231);
		 * NList.put(2596108,4291); NList.put(2991235,4005);
		 * NList.put(2489663,3447); NList.put(2993875,2992);
		 * NList.put(3032977,2795); NList.put(3032980,2016);
		 * NList.put(3032979,6448); NList.put(2596158,6472);
		 * NList.put(2959745,6480); NList.put(2528353,3243);
		 * NList.put(2534781,2018); NList.put(2488653,4289);
		 * NList.put(2488958,3033); NList.put(2528210,3445);
		 * NList.put(2528779,976); NList.put(2530596,2167);
		 * NList.put(3032976,1982); NList.put(3033028,3457);
		 * NList.put(2991281,3190); NList.put(2959736,2393);
		 * NList.put(2531210,6581); NList.put(2995702,4003);
		 * NList.put(2531185,3981); NList.put(2490589,4300);
		 * NList.put(2488660,4253); NList.put(2528447,6617);
		 * NList.put(2583664,2774); NList.put(2491079,63);
		 * NList.put(2333797,6447); NList.put(2991009,6462);
		 * NList.put(2528356,6449); NList.put(2578204,2166);
		 * NList.put(2991143,2761); NList.put(2490089,6464);
		 * NList.put(2995703,6615); NList.put(2488684,6443);
		 * NList.put(2968361,6454); NList.put(2488651,6593);
		 * NList.put(2489021,3041); NList.put(2579321,6586);
		 * NList.put(2327632,2756); NList.put(2528787,4004);
		 * NList.put(2326369,4008); NList.put(2489572,2810);
		 * NList.put(2959753,4400); NList.put(2488706,4305);
		 * NList.put(2488693,3008); NList.put(2528235,6485);
		 * NList.put(3032985,2477); NList.put(3037804,6511);
		 */
		// 2012 保护新秀
		/*
		 * NList.put(6583,1711); NList.put(6601,6450); NList.put(6580,272);
		 * NList.put(6628,2768); NList.put(6618,308); NList.put(6606,2439);
		 * NList.put(6578,25); NList.put(6619,3187); NList.put(6585,4261);
		 * NList.put(6617,2991); NList.put(6605,3462); NList.put(6603,3450);
		 * NList.put(6607,6468); NList.put(6592,4259); NList.put(6591,3191);
		 * NList.put(6629,3418); NList.put(6631,2167); NList.put(6597,617);
		 * NList.put(6614,6546); NList.put(6588,1981); NList.put(6624,515);
		 * NList.put(6608,2171); NList.put(6594,3417); NList.put(6582,2778);
		 * NList.put(6630,246); NList.put(6616,3242); NList.put(6612,3991);
		 * NList.put(6598,4165); NList.put(6626,3002); NList.put(6587,4005);
		 * NList.put(6156,3974); NList.put(6621,3979); NList.put(6593,6447);
		 * NList.put(6581,3967); NList.put(6589,3246); NList.put(6595,3981);
		 * NList.put(6576,6481); NList.put(6611,1016); NList.put(6609,558);
		 * NList.put(6579,4240); NList.put(6625,4240); NList.put(6604,4019);
		 * NList.put(6622,6439); NList.put(6586,4019); NList.put(6590,2780);
		 * NList.put(6610,2757); NList.put(6613,2004); NList.put(4195,3018);
		 * NList.put(6615,4304); NList.put(6627,4304); NList.put(6599,703);
		 * NList.put(6602,1013); NList.put(6577,6473); NList.put(6623,3464);
		 * NList.put(6596,2008); NList.put(6632,2008); NList.put(6600,2794);
		 * NList.put(6633,1721); NList.put(6584,6497); NList.put(6620,4267);
		 * NList.put(6477,862); NList.put(6464,2991); NList.put(6469,3247);
		 */
		/*
		 * 2011 保护新秀 NList.put(6442,1015); NList.put(6480,1015);
		 * NList.put(6447,1015); NList.put(6474,1015);
		 * 
		 * NList.put(4011,1015); NList.put(4165,862); NList.put(6427,3006);
		 * NList.put(6448,862); NList.put(6479,862); NList.put(6434,862);
		 * NList.put(6475,3006); NList.put(6429,3006); NList.put(6461,3006);
		 * NList.put(6462,3006); NList.put(6450,3006); NList.put(6478,3986);
		 * NList.put(6468,3986); NList.put(6470,3986); NList.put(6440,3986);
		 * 
		 * NList.put(6471,2015); NList.put(6433,2015); NList.put(6443,3554);
		 * NList.put(6428,2015); NList.put(6436,49); NList.put(6445,3038);
		 * NList.put(6431,3038); NList.put(6446,3024); NList.put(6430,3190);
		 * NList.put(3593,3974); NList.put(6438,3974);
		 * 
		 * NList.put(6454,1996); NList.put(6441,3974); NList.put(6481,4265);
		 * NList.put(6473,3974); NList.put(6466,3974); NList.put(6476,4264);
		 * NList.put(6452,2422); NList.put(6463,2816); NList.put(6426,1994);
		 * NList.put(6449,3041); NList.put(6444,1979); NList.put(6435,1979);
		 * NList.put(6451,1979); NList.put(6425,2780); NList.put(6467,1996);
		 * NList.put(6424,3447); NList.put(6432,2016); NList.put(6455,348);
		 * NList.put(6453,3277); NList.put(6460,4010); NList.put(6457,3985);
		 * NList.put(6465,3981); NList.put(6458,1767); NList.put(6437,4010);
		 * NList.put(6472,3432); NList.put(6484,3432); NList.put(6497,3986);
		 * NList.put(6501,4010); NList.put(6534,4010); NList.put(4235,4300);
		 * NList.put(6439,4300); NList.put(6490,4300); NList.put(4329,4010);
		 * NList.put(6527,4010); NList.put(2212,4010); NList.put(6485,4010);
		 * NList.put(4280,4010); NList.put(6544,4010); NList.put(6546,4010);
		 * NList.put(6523,4010);
		 */
		return NList;
	}

	/**
	 * 新秀刚出来时，在休赛期不更新数据
	 * @return
	 */
	private List<Integer> getNNList() {
		List<Integer> NList = new ArrayList<Integer>();
		// 2018新秀
		NList.add(4278129);
		NList.add(4277848);
		NList.add(3945274);
		NList.add(4277961);
		NList.add(4277905);
		NList.add(4277919);
		NList.add(4277847);
		NList.add(4277811);
		NList.add(4278075);
		NList.add(3147657);
		NList.add(4278073);
		NList.add(4066383);
		NList.add(3943606);
		NList.add(4278104);
		NList.add(4278508);
		NList.add(4277923);
		NList.add(3934673);
		NList.add(4277890);
		NList.add(4066372);
		NList.add(4065663);
		NList.add(3135045);
		NList.add(3149010);
		NList.add(3922230);
		NList.add(4351851);
		NList.add(3917314);
		NList.add(3914044);
		NList.add(4066211);
		NList.add(3934621);
		NList.add(4348696);
		NList.add(4065836);
		NList.add(4230550);
		NList.add(3133635);
		NList.add(3934672);
		NList.add(3133601);
		NList.add(3913546);
		NList.add(4351852);
		NList.add(4277843);
		NList.add(3912334);
		NList.add(4348697);
		NList.add(4230548);
		NList.add(4278077);
		NList.add(4065670);
		NList.add(4066373);
		NList.add(4348701);
		NList.add(4080610);
		NList.add(4066436);
		NList.add(3133602);
		NList.add(3136779);
		NList.add(3914283);
		NList.add(4067045);
		NList.add(4066404);
		NList.add(3136989);
		NList.add(3059285);
		NList.add(3915195);
		NList.add(4230551);
		NList.add(3908806);
		NList.add(3143565);
		NList.add(3155536);
		NList.add(3064320);
		NList.add(4066490);
		/** 2017新秀
		NList.add(4066636);
		NList.add(4066421);
		NList.add(4065648);
		NList.add(4066297);
		NList.add(4065654);
		NList.add(4066336);
		NList.add(4230547);
		NList.add(4065697);
		NList.add(4066650);
		NList.add(4066262);
		NList.add(3913174);
		NList.add(3908809);
		NList.add(4066261);
		NList.add(3138156);
		NList.add(3912332);
		NList.add(3136491);
		NList.add(4066425);
		NList.add(3908845);
		NList.add(4065649);
		NList.add(4230546);
		NList.add(4066328);
		NList.add(3934719);
		NList.add(3934662);
		NList.add(3893059);
		NList.add(3906671);
		NList.add(3134907);
		NList.add(4065673);
		NList.add(3078576);
		NList.add(3062679);
		NList.add(4065651);
		NList.add(3059262);
		NList.add(3074797);
		NList.add(3059315);
		NList.add(3917378);
		NList.add(3155533);
		NList.add(3056602);
		NList.add(3064427);
		NList.add(3912854);
		NList.add(3907821);
		NList.add(3915560);
		NList.add(3934723);
		NList.add(4222252);
		NList.add(2991149);
		NList.add(3155526);
		NList.add(3057187);
		NList.add(4066424);
		NList.add(3064539);
		NList.add(4230557);
		NList.add(4017851);
		NList.add(3059310);
		NList.add(3136485);
		NList.add(3134880);
		NList.add(3062897);
		NList.add(3064457);
		NList.add(3064308);
		NList.add(3893014);
		NList.add(4017857);
		NList.add(2982240);
		NList.add(3893020);
		NList.add(4066259);

		// 2016新秀
		NList.add(3907387);
		NList.add(3913176);
		NList.add(3917376);
		NList.add(4011991);
		NList.add(2991139);
		NList.add(2990984);
		NList.add(3936299);
		NList.add(3907487);
		NList.add(3134908);
		NList.add(4017843);
		NList.add(3155942);
		NList.add(2990962);
		NList.add(4017846);
		NList.add(2999549);
		NList.add(4017839);
		NList.add(4017844);
		NList.add(3136696);
		NList.add(3906522);
		NList.add(3907822);
		NList.add(2991043);
		NList.add(3062667);
		NList.add(3934663);
		NList.add(4017838);
		NList.add(3893019);
		NList.add(2982330);
		NList.add(3929325);
		NList.add(3149673);
		NList.add(3936296);
		NList.add(3907497);
		NList.add(3064559);
		NList.add(3937101);
		NList.add(4017837);
		NList.add(3919335);
		NList.add(3136194);
		NList.add(3893060);
		NList.add(2566769);
		NList.add(3147366);
		NList.add(3137730);
		NList.add(4017875);
		NList.add(3924880);
		NList.add(3936102);
		NList.add(3136477);
		NList.add(3892894);
		NList.add(4017845);
		NList.add(3059273);
		NList.add(2991178);
		NList.add(2982268);
		NList.add(3893023);
		NList.add(2566747);
		NList.add(2990969);
		NList.add(3136469);
		NList.add(2983551);
		NList.add(3899009);
		NList.add(3062892);
		NList.add(2982329);
		NList.add(3147351);
		NList.add(4017850);
		NList.add(2595435);
		NList.add(2990983);
		NList.add(3002137);*/

		// 2015新秀
		/*
		 * NList.add(3136195); NList.add(3136776); NList.add(3135048);
		 * NList.add(3102531); NList.add(2995706); NList.add(2991282);
		 * NList.add(3892818); NList.add(3134881); NList.add(2579294);
		 * NList.add(3135047); NList.add(3133628); NList.add(3136196);
		 * NList.add(3136193); NList.add(3064230); NList.add(3133603);
		 * NList.add(3074752); NList.add(3137733); NList.add(2991184);
		 * NList.add(2531047); NList.add(3064447); NList.add(2982340);
		 * NList.add(3064482); NList.add(3064291); NList.add(3135046);
		 * NList.add(3064517); NList.add(3892893); NList.add(2580365);
		 * NList.add(2983727); NList.add(3153165); NList.add(3155535);
		 * NList.add(3893016); NList.add(2991055); NList.add(3064520);
		 * NList.add(2531362); NList.add(2999409); NList.add(2596110);
		 * NList.add(2993370); NList.add(2578259); NList.add(3899008);
		 * NList.add(2581190); NList.add(2578239); NList.add(2982192);
		 * NList.add(2528386); NList.add(3064511); NList.add(2579458);
		 * NList.add(2595516); NList.add(3893015); NList.add(3064516);
		 * NList.add(2595175); NList.add(3904015); NList.add(2578269);
		 * NList.add(3902067); NList.add(2608899); NList.add(3033042);
		 * NList.add(2608716); NList.add(2595211); NList.add(3902065);
		 * NList.add(2982331); NList.add(3899663); NList.add(3899662);
		 */

		/*
		 * //2014新秀 NList.add(3059319); NList.add(3056600); NList.add(3059318);
		 * NList.add(3064290); NList.add(3102528); NList.add(2990992);
		 * NList.add(3064514); NList.add(2991042); NList.add(3078284);
		 * NList.add(2583639); NList.add(2528588); NList.add(3032978);
		 * NList.add(3064440); NList.add(2982334); NList.add(2531100);
		 * NList.add(3102530); NList.add(3064509); NList.add(3059281);
		 * NList.add(2999547); NList.add(3113297); NList.add(2991041);
		 * NList.add(2993873); NList.add(2581177); NList.add(2530780);
		 * NList.add(3102529); NList.add(2594816); NList.add(3037789);
		 * NList.add(2488999); NList.add(2531364); NList.add(2993874);
		 * NList.add(3102534); NList.add(2566741); NList.add(2528794);
		 * NList.add(2991227); NList.add(2614962); NList.add(2581084);
		 * NList.add(2578232); NList.add(2580782); NList.add(2991070);
		 * NList.add(2991039); NList.add(3112335); NList.add(2580589);
		 * NList.add(3033031); NList.add(2530750); NList.add(2531367);
		 * NList.add(2528426); NList.add(2531038); NList.add(2488721);
		 * NList.add(2528354); NList.add(2528479); NList.add(3102533);
		 * NList.add(3102532); NList.add(2995725); NList.add(3113298);
		 * NList.add(2983526); NList.add(2527968); NList.add(3037792);
		 * NList.add(2531797); NList.add(2489006); NList.add(2488845);
		 * 
		 * //2013新秀 NList.add(2991473); NList.add(2527963); NList.add(2594922);
		 * NList.add(2579258); NList.add(2596107); NList.add(2991280);
		 * NList.add(2578213); NList.add(2581018); NList.add(2579260);
		 * NList.add(2490149); NList.add(2596108); NList.add(2991235);
		 * NList.add(2489663); NList.add(2993875); NList.add(3032977);
		 * NList.add(3032980); NList.add(3032979); NList.add(2596158);
		 * NList.add(2959745); NList.add(2528353); NList.add(2534781);
		 * NList.add(2488653); NList.add(2488958); NList.add(2528210);
		 * NList.add(2528779); NList.add(2530596); NList.add(3032976);
		 * NList.add(3033028); NList.add(2991281); NList.add(2959736);
		 * NList.add(2531210); NList.add(2995702); NList.add(2531185);
		 * NList.add(2490589); NList.add(2488660); NList.add(2528447);
		 * NList.add(2583664); NList.add(2491079); NList.add(2333797);
		 * NList.add(2991009); NList.add(2528356); NList.add(2578204);
		 * NList.add(2991143); NList.add(2490089); NList.add(2995703);
		 * NList.add(2488684); NList.add(2968361); NList.add(2488651);
		 * NList.add(2489021); NList.add(2579321); NList.add(2327632);
		 * NList.add(2528787); NList.add(2326369); NList.add(2489572);
		 * NList.add(2959753); NList.add(2488706); NList.add(2488693);
		 * NList.add(2528235); NList.add(3032985); NList.add(3037804);
		 * 
		 * //2012新秀 NList.add(6583); NList.add(6601); NList.add(6580);
		 * NList.add(6628); NList.add(6618); NList.add(6606); NList.add(6578);
		 * NList.add(6619); NList.add(6585); NList.add(6617); NList.add(6605);
		 * NList.add(6603); NList.add(6607); NList.add(6592); NList.add(6591);
		 * NList.add(6629); NList.add(6631); NList.add(6597); NList.add(6614);
		 * NList.add(6588); NList.add(6624); NList.add(6608); NList.add(6594);
		 * NList.add(6582); NList.add(6630); NList.add(6616); NList.add(6612);
		 * NList.add(6598); NList.add(6626); NList.add(6587); NList.add(6156);
		 * NList.add(6621); NList.add(6593); NList.add(6581); NList.add(6589);
		 * NList.add(6595); NList.add(6576); NList.add(6611); NList.add(6609);
		 * NList.add(6579); NList.add(6625); NList.add(6604); NList.add(6622);
		 * NList.add(6586); NList.add(6590); NList.add(6610); NList.add(6613);
		 * NList.add(4195); NList.add(6615); NList.add(6627); NList.add(6599);
		 * NList.add(6602); NList.add(6577); NList.add(6623); NList.add(6596);
		 * NList.add(6632); NList.add(6600); NList.add(6633); NList.add(6584);
		 * NList.add(6620);
		 */
		return NList;
	}

	
}
