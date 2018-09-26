package com.ftkj.db.ao.common.impl;

import java.util.List;

import com.ftkj.annotation.IOC;
import com.ftkj.db.ao.common.INBADataAO;
import com.ftkj.db.conn.ao.BaseAO;
import com.ftkj.db.dao.common.NBADataDAO;
import com.ftkj.db.domain.NBAGameGuess;
import com.ftkj.db.domain.NBAPKSchedule;
import com.ftkj.db.domain.NBAPKScoreBoard;
import com.ftkj.db.domain.NBAPKScoreBoardDetail;
import com.ftkj.db.domain.NBAVS;
import com.ftkj.db.domain.bean.PlayerBeanVO;
import com.ftkj.db.domain.bean.PlayerMoneyBeanPO;

/**
 * @author tim.huang
 * 2017年3月15日
 *
 */
public class NBADataAOImpl extends BaseAO implements INBADataAO {

	@IOC
	private NBADataDAO nbaDataDAO;
	
	
	@Override
	public List<PlayerBeanVO> getAllPlayerBean() {
		return nbaDataDAO.getAllPlayerBean();
	}


	@Override
	public List<PlayerMoneyBeanPO> getAllPlayerMoneyBean() {
		return nbaDataDAO.getAllPlayerMoneyBean();
	}


	@Override
	public List<NBAPKSchedule> getAllNBAPKSchedule() {
		return nbaDataDAO.getAllNBAPKSchedule();
	}


	@Override
	public List<NBAPKScoreBoard> getAllNBAPKScoreBoard() {
		return nbaDataDAO.getAllNBAPKScoreBoard();
	}


	@Override
	public List<NBAPKScoreBoardDetail> getAllNBAPKScoreBoardDetail() {
		return nbaDataDAO.getAllNBAPKScoreBoardDetail();
	}
	
	@Override
	public List<NBAPKScoreBoardDetail> getNBAScoreBoardDetailLimit(int playerId){
		return nbaDataDAO.getNBAScoreBoardDetailLimit(playerId);
	}

	@Override
	public List<NBAPKScoreBoardDetail> getNBAPKScoreBoardDetailByPlayer(
			int playerId) {
		return nbaDataDAO.getNBAPKScoreBoardDetailByPlayer(playerId);
	}


	@Override
	public List<NBAVS> getNBAVSDetailList() {
		return nbaDataDAO.getNBAVSDetailList();
	}


	@Override
	public List<PlayerBeanVO> getAllPlayerAvgBean() {
		return nbaDataDAO.getAllPlayerAvgBean();
	}


	@Override
	public PlayerBeanVO getMaxPlayerAvgBean() {
		return nbaDataDAO.getMaxPlayerAvgBean();
	}
	
	@Override
	public List<String> getRandSecheduleDateList(int seasionId, String startTime, String endTime, int minVs) {
		return nbaDataDAO.getRandSecheduleDateList(seasionId, startTime, endTime, minVs);
	}

	@Override
	public List<NBAPKScoreBoardDetail> getNBAScoreBoardDetailLimit(int playerId, List<String> dateList) {
		return nbaDataDAO.getNBAScoreBoardDetailLimit(playerId, dateList);
	}


	@Override
	public List<NBAGameGuess> getNBAGameGuessDetailList() {
		return nbaDataDAO.getNBAGameGuessDetailList();
	}


	@Override
	public NBAGameGuess getNBAGameGuessById(int id) {
		return nbaDataDAO.getNbaGameGuessById(id);
	}


	@Override
	public List<PlayerBeanVO> getPlayerInfoList() {
		return nbaDataDAO.getPlayerInfoList();
	}


	@Override
	public NBAGameGuess selectNBAGameGuessById(int id) {
		return nbaDataDAO.selectNbaGameGuessById(id);
	}
	
	
	
}
