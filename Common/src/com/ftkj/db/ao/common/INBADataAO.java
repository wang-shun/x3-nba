package com.ftkj.db.ao.common;

import java.util.List;

import com.ftkj.db.domain.NBAGameGuess;
import com.ftkj.db.domain.NBAPKSchedule;
import com.ftkj.db.domain.NBAPKScoreBoard;
import com.ftkj.db.domain.NBAPKScoreBoardDetail;
import com.ftkj.db.domain.NBAVS;
import com.ftkj.db.domain.bean.PlayerBeanVO;
import com.ftkj.db.domain.bean.PlayerMoneyBeanPO;

public interface INBADataAO {
	//取所有球员配置 
	public List<PlayerBeanVO> getAllPlayerBean();
	public List<PlayerBeanVO> getAllPlayerAvgBean();
	public PlayerBeanVO getMaxPlayerAvgBean();
	/** 一个月以内的工资数据*/
	public List<PlayerMoneyBeanPO> getAllPlayerMoneyBean();
	
	public List<NBAPKSchedule> getAllNBAPKSchedule();
	public List<NBAPKScoreBoard> getAllNBAPKScoreBoard();
	public List<NBAPKScoreBoardDetail> getAllNBAPKScoreBoardDetail();
	/**
	 * 最近5场比赛
	 * @param playerId
	 * @return
	 */
	public List<NBAPKScoreBoardDetail> getNBAScoreBoardDetailLimit(int playerId);
	public List<NBAVS> getNBAVSDetailList();
	
	/**
     * 获取竞猜活动,没有取消并且还没有发奖励以及
     * 比赛当前时间大于开始开始时间或者当天开启的的数据.
     * @return
     */
	public List<NBAGameGuess> getNBAGameGuessDetailList();
	/**
	 * 根据竞猜活动比赛Id,查询有效的竞猜获得的比赛数据(过期,取消和奖励发放的都过滤掉).
	 * @param id
	 * @return
	 */
	public NBAGameGuess getNBAGameGuessById(int id);
	
	/**
	 * 根据竞猜活动比赛Id获取竞猜的数据.
	 * @param id
	 * @return
	 */
	public NBAGameGuess selectNBAGameGuessById(int id);
	
	public List<NBAPKScoreBoardDetail> getNBAPKScoreBoardDetailByPlayer(int playerId);
	
	/**
	 * 随机身价赛程
	 */
	public List<String> getRandSecheduleDateList(int seasionId, String startTime, String endTime, int minVs);
	/**
	 * 随机身价赛程，球员的最近5场
	 */
	public List<NBAPKScoreBoardDetail> getNBAScoreBoardDetailLimit(int playerId, List<String> dateList);
	
	/**
	 * 查询球员的市价,并且球员teamId大于0.
	 * @return
	 */
	public List<PlayerBeanVO> getPlayerInfoList();
}
