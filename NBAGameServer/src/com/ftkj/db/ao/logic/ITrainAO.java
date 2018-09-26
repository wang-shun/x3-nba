package com.ftkj.db.ao.logic;

import java.util.List;

import com.ftkj.manager.train.LeagueTrain;
import com.ftkj.manager.train.TeamTrain;
import com.ftkj.manager.train.Train;

public interface ITrainAO {
	/**返回保存的个人训练馆所有数据*/
	public List<Train> getAllTrain();
	/**返回所有球队参与训练的数据*/
	public List<TeamTrain> getAllTeamTrain();
	
	public List<Train> getTrainByTeamId(long teamId);
	
	public TeamTrain getTeamTrainByTeamId(long teamId);
	/**获取,联盟训练馆数据*/
	public List<LeagueTrain> getLeagueTrainList();
	
	public void clearLeagueTrain();
}
